package main

import (
	"bytes"
	"encoding/base64"
	"encoding/json"
	"fmt"
	"image/color"
	"image/png"
	"log"
	"net/http"
	"os"
	"strconv"
	"time"
)

var imageCloudSickEndpoint string = fmt.Sprintf("http://%s:%s/sick", os.Getenv("IMAGE_CLOUD_IP"), os.Getenv("IMAGE_CLOUD_PORT"))
var imageCloudTrainEndpoint string = fmt.Sprintf("http://%s:%s/train", os.Getenv("IMAGE_CLOUD_IP"), os.Getenv("IMAGE_CLOUD_PORT"))
var edgeDeviceRoboterEndpoint string = fmt.Sprintf("http://%s:%s/pick", os.Getenv("IMAGE_EDGE_DEVICE_IP"), os.Getenv("IMAGE_EDGE_DEVICE_PORT"))


var BenchMarkEndpoint string = fmt.Sprintf("http://%s:%s/%s", os.Getenv("benchmarkEndPointHost"), os.Getenv("benchmarkEndpointPort"),os.Getenv("benchmarkEndpointURL"))

type BenchmarkData struct {
	WorkerID string `json:"workerID"`
	Timestamp int64 `json:"timestamp"`
	ActType int `json:"type"`
	TimeDelta int64 `json:"timeDelta"`
}

// Zeit zwischenspeichern 
var startTime = time.Now().UnixNano()
var benchValues []BenchmarkData
 

var imageAcceptanceRate = os.Getenv("IMAGE_ACCEPTANCE_RATE")

type Model struct {
	Hweights []byte `json:hweights`
	Oweights []byte `json:oweights`
}

type Request struct {
	Img  string `json:"img"`
	UUID string `json:"uuid"`
}

type Pick struct {
	ready bool   `json:"ready"`
	UUID  string `json:"uuid"`
}

func isBlack(p color.RGBA) bool {

	if p.R != 0x0 {
		return false
	}
	if p.G != 0x0 {
		return false
	}
	if p.B != 0x0 {
		return false
	}
	if p.A != 0xff {
		return false
	}

	return true

}

func sendRobotPick(uuid string, answer bool) {
	timestamp := strconv.FormatInt(time.Now().UnixNano(), 10)

	data, err := json.Marshal(Pick{
		ready: answer,
		UUID:  uuid,
	})

	if err != nil {
		return
	}

	log.Printf("send,pick,%s,%s", uuid, timestamp)

	req, err := http.NewRequest("POST", edgeDeviceRoboterEndpoint, bytes.NewReader(data))

	if err != nil {
		return
	}

	go func() {
		_, err = (&http.Client{}).Do(req)

		log.Println(req)
		if err != nil {
			log.Print(err)

		}
	}()
}

// source: https://github.com/OpenFogStack/smart-factory-fog-example
func processImage(d Request) {
    startTime = time.Now().UnixNano() 
	decoded, err := base64.StdEncoding.DecodeString(d.Img)

	if err != nil {
		return
	}

	reader := bytes.NewReader(decoded)

	img, err := png.Decode(reader)

	if err != nil {
		return
	}

	blacks := 0.0

	for x := img.Bounds().Min.X; x < img.Bounds().Max.X; x++ {
		for y := img.Bounds().Min.Y; y < img.Bounds().Max.Y; y++ {
			//log.Printf("(%v,%v): %#v", x, y, img.At(x, y))

			if isBlack(color.RGBAModel.Convert(img.At(x, y)).(color.RGBA)) {
				blacks = blacks + 1.0
			}
		}
	}

	totalpixels := float64((img.Bounds().Max.X - img.Bounds().Min.X) * (img.Bounds().Max.Y - img.Bounds().Min.Y))

	//log.Print(blacks)
	//log.Print(totalpixels)
	//log.Print(blacks / totalpixels)

	imageAcceptanceRateFloat, _ := strconv.ParseFloat(imageAcceptanceRate, 32)
	answer := false
	var endTime = time.Now().UnixNano() 
	var timeDelta = endTime - startTime
    addBenchValue("Image-Edge", startTime, 3 ,timeDelta)


    
	if blacks/totalpixels > imageAcceptanceRateFloat {
		// there is a disease, send instruction to prod_cntrl
		answer = true
		fmt.Println("Can be picked")

		// send data
		data, err := json.Marshal(d)

		if err != nil {
			return
		}

		req, err := http.NewRequest("POST", imageCloudSickEndpoint, bytes.NewReader(data))

		if err != nil {
			return
		}

		log.Printf("send,%s,%s", d.UUID, strconv.FormatInt(time.Now().UnixNano(), 10))

		_, err = (&http.Client{}).Do(req)

		if err != nil {
			log.Print(err)
		}

	}
    
	go sendRobotPick(d.UUID, answer)
	answer = false
}

func sendCloud(d Request) {
	data, err := json.Marshal(d)

	if err != nil {
		return
	}

	req, err := http.NewRequest("POST", imageCloudTrainEndpoint, bytes.NewReader(data))

	if err != nil {
		return
	}

	log.Printf("send,%s,%s", d.UUID, strconv.FormatInt(time.Now().UnixNano(), 10))

	_, err = (&http.Client{}).Do(req)

	if err != nil {
		log.Print(err)
	}
}

func ImageHandler(w http.ResponseWriter, r *http.Request) {
	timestamp := strconv.FormatInt(time.Now().UnixNano(), 10)

	var data Request
	err := json.NewDecoder(r.Body).Decode(&data)

	if err != nil {
		log.Print(err)
		return
	}

	log.Printf("recv,image,%s,%s", data.UUID, timestamp)

	go processImage(data)
	go sendCloud(data)
}

func saveModel(model Model) {
	log.Println("Start saving models")
	h, err := os.Create("data/hweights.model")
	defer h.Close()
	if err != nil {
		log.Println(err)
	}

	_, err = h.Write(model.Hweights)
	if err != nil {
		log.Println(err)
	}

	o, err := os.Create("data/oweights.model")
	defer o.Close()
	if err != nil {
		log.Println(err)
	}
	_, err = o.Write(model.Oweights)
	if err != nil {
		log.Println(err)
	}

	log.Println("Models saved")
}

func ModelHandler(w http.ResponseWriter, r *http.Request) {
	setStartTime(time.Now().UnixNano())
	timestamp := strconv.FormatInt(time.Now().UnixNano(), 10)

	var data Model
	err := json.NewDecoder(r.Body).Decode(&data)

	if err != nil {
		log.Print(err)
		return
	}

	log.Printf("recv,image,%s", timestamp)
	log.Println("GETTING MODEL")

	go saveModel(data)
	var timeDelta = time.Now().UnixNano() - startTime
    addBenchValue("Image-Edge", startTime, 4 ,timeDelta)
}

func setStartTime(newStartTime int64) {
       startTime = newStartTime
}

func sendBenchData() {
 		data, err := json.Marshal(benchValues)
  

 		req, err := http.NewRequest("POST", BenchMarkEndpoint, bytes.NewReader(data))
 		
 		if err != nil {
			log.Print(err)
			return
		}
        
		go func() {
			_, err = (&http.Client{}).Do(req)

			log.Println(req)
			if err != nil {
				log.Print(err)
				return
			}
		}()
		log.Print("Benchmark data sent to Benchmarking Endpoint.")

}
 

func addBenchValue(uuid string, tmpst int64, atype int, tmDelta int64 ){
	
	if len(benchValues)>100 {
		sendBenchData()
		benchValues = nil
	}
	benchVal := BenchmarkData{
		WorkerID: uuid, 
	    Timestamp: tmpst,  
	    ActType: atype,   
	    TimeDelta: tmDelta ,
	}
 	benchValues = append(benchValues,benchVal)
}

//test
func printSlice(s []BenchmarkData) {
	fmt.Printf("len=%d cap=%d %v\n", len(s), cap(s), s)    
 
}
func main() {

	http.HandleFunc("/image", ImageHandler)
	http.HandleFunc("/model", ModelHandler)

	log.Fatal(http.ListenAndServe(":"+os.Getenv("IMAGE_EDGE_PORT"), nil))
}
