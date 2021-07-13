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
var edgeDeviceRoboterEndpoint string = fmt.Sprintf("http://%s:%s/picker", os.Getenv("IMAGE_EDGE_DEVICE_IP"), os.Getenv("IMAGE_EDGE_DEVICE_PORT"))

var imageAcceptanceRate = os.Getenv("IMAGE_ACCEPTANCE_RATE")

type LatencyData struct {
	WorkerID  string `json:"workerID"`
	Timestamp int64  `json:"timestamp"`
	ActType   int    `json:"type"`
	TimeDelta int64  `json:"timeDelta"`
}

type Model struct {
	Hweights []byte `json:hweights`
	Oweights []byte `json:oweights`
}

type Request struct {
	Img  string `json:"img"`
	UUID string `json:"uuid"`
}

type Pick struct {
	Ready bool   `json:"ready"`
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
		Ready: answer,
		UUID:  uuid,
	})

	if err != nil {
		log.Println(err)
		return
	}

	log.Printf("send,pick,%s,%t,%s", uuid, answer, timestamp)

	req, err := http.NewRequest("POST", edgeDeviceRoboterEndpoint, bytes.NewReader(data))

	if err != nil {
		log.Println(err)
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
	// startTime := time.Now().UnixNano()
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

	log.Print(blacks)
	log.Print(totalpixels)
	log.Print(blacks / totalpixels)

	imageAcceptanceRateFloat, _ := strconv.ParseFloat(imageAcceptanceRate, 32)
	answer := false

	// logLatency(startTime, 3)

	if blacks/totalpixels > imageAcceptanceRateFloat {
		// Plant can be picked
		answer = true
		go sendCloud(d, imageCloudSickEndpoint)
	}

	go sendRobotPick(d.UUID, answer)
}

func sendCloud(d Request, endPoint string) {
	startTime := time.Now().UnixNano()
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

	logLatency(startTime, 2)
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
	go sendCloud(data, imageCloudTrainEndpoint)
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
	// startTime := time.Now().UnixNano()
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
	// logLatency(startTime, 4)
}

func logLatency(startTime int64, actType int) {
	var endTime = time.Now().UnixNano()
	var timeDelta = endTime - startTime

	latencyData := LatencyData{
		WorkerID:  "Image-Edge",
		Timestamp: startTime,
		ActType:   actType,
		TimeDelta: timeDelta,
	}

	latencyJson, err := json.Marshal(latencyData)
	if err != nil {
		log.Println(err)
	}

	log.Printf("latency: %v", string(latencyJson))
}

func main() {
	http.HandleFunc("/image", ImageHandler)
	http.HandleFunc("/model", ModelHandler)

	log.Fatal(http.ListenAndServe(":"+os.Getenv("IMAGE_EDGE_PORT"), nil))
}
