// source: https://github.com/OpenFogStack/smart-factory-fog-example/tree/mockfog2

package main

import (
	"bufio"
	"bytes"
	"encoding/base64"
	"encoding/json"
	"fmt"
	"image"
	"image/color"
	"image/png"
	"io/ioutil"
	"log"
	"math/rand"
	"net/http"
	"os"
	"strconv"
	"time"

	"github.com/google/uuid"
)

// TODO: Needs to go if in cluster
var ImageEdgeEndpoint string = fmt.Sprintf("http://%s:%s/image", os.Getenv("IMAGE_EDGE_IP"), os.Getenv("IMAGE_EDGE_PORT"))
var BenchMarkEndpoint string = fmt.Sprintf("http://%s:%s/%s", os.Getenv("benchmarkEndPointHost"), os.Getenv("benchmarkEndpointPort"),os.Getenv("benchmarkEndpointURL"))
// update interval in milliseconds
var interval string = os.Getenv("INTERVAL")

var width string = os.Getenv("WIDTH")
var height string = os.Getenv("HEIGHT")
 
 
type Pick struct {
	ready bool   `json:"ready"`
	UUID  string `json:"uuid"`
}

 


type BenchmarkData struct {
	WorkerID string `json:"workerID"`
	Timestamp int64 `json:"timestamp"`
	ActType int `json:"type"`
	TimeDelta int64 `json:"timeDelta"`
}

// Zeit zwischenspeichern 
var startTime = time.Now().UnixNano()
var benchValues []BenchmarkData
 
type Request struct {
	Img  string `json:"img"`
	UUID string `json:"uuid"`
}

func generateImage() string {
	upLeft := image.Point{0, 0}

	widthInt, _ := strconv.Atoi(width)
	heightInt, _ := strconv.Atoi(height)

	lowRight := image.Point{widthInt, heightInt}

	img := image.NewRGBA(image.Rectangle{upLeft, lowRight})

	// Set color for each pixel.
	for x := 0; x < widthInt; x++ {
		for y := 0; y < heightInt; y++ {
			if rand.Intn(2) == 1 {
				img.Set(x, y, color.White)
			} else {
				img.Set(x, y, color.Black)
			}
		}
	}

	var r bytes.Buffer

	err := png.Encode(&r, img)

	if err != nil {
		return ""
	}

	reader := bufio.NewReader(&r)
	content, _ := ioutil.ReadAll(reader)

	// Encode as base64.
	encoded := base64.StdEncoding.EncodeToString(content)

	return encoded
}
 func setStartTime(newStartTime int64) {
       startTime = newStartTime
    }
 
func sendImage() {
 
	
	intervalInt, _ := strconv.Atoi(interval)
	ticker := time.NewTicker(time.Duration(intervalInt) * time.Millisecond)

	for range ticker.C {
         setStartTime(time.Now().UnixNano())
        fmt.Printf("startTime: %d\n", startTime) 
    
		img := generateImage()

		id, err := uuid.NewRandom()
		if err != nil {
			log.Print(err)
			return
		}

		data, err := json.Marshal(Request{
			Img:  img,
			UUID: id.String(),
		})

		if err != nil {
			log.Print(err)
			return
		}

		log.Printf("send,camera,%s,%s", id.String(), strconv.FormatInt(time.Now().UnixNano(), 10))
 		
 		req, err := http.NewRequest("POST", ImageEdgeEndpoint, bytes.NewReader(data))

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

	}
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

func pickerHandler(w http.ResponseWriter, r *http.Request) {
	timestamp := strconv.FormatInt(time.Now().UnixNano(), 10)

	var data Pick
	err := json.NewDecoder(r.Body).Decode(&data)

	if err != nil {
		log.Print(err)
		return
	}

	if data.ready {
		log.Println("Picking the plant.")
	} else {
		log.Println("Not picking the plant.")
	}

	log.Printf("recv,pick,%s,%t", timestamp, data.ready)
	
	//Ende des Vorgangs
 	var endTime = time.Now().UnixNano() 
    var timeDelta = endTime - startTime;
    addBenchValue("Picking-Robot",startTime,0,timeDelta) 
}

func main() {
	go sendImage()

	http.HandleFunc("/picker", pickerHandler)
	log.Fatal(http.ListenAndServe(":"+os.Getenv("IMAGE_EDGE_DEVICE_PORT"), nil))

}
