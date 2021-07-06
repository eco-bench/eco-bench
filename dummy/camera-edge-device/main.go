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

// update interval in milliseconds
var interval string = os.Getenv("INTERVAL")

var requestsAwaitAnswer []RequestAwait

var width string = os.Getenv("WIDTH")
var height string = os.Getenv("HEIGHT")

type Pick struct {
	Ready bool   `json:"ready"`
	UUID  string `json:"uuid"`
}

type LatencyData struct {
	WorkerID  string `json:"workerID"`
	Timestamp int64  `json:"timestamp"`
	ActType   int    `json:"type"`
	TimeDelta int64  `json:"timeDelta"`
}

type RequestAwait struct {
	UUID string `json:"uuid"`
	Time int64  `json:"time"`
}

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

func sendImage() {
	intervalInt, _ := strconv.Atoi(interval)
	ticker := time.NewTicker(time.Duration(intervalInt) * time.Millisecond)

	for range ticker.C {
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

		timeNow := time.Now().UnixNano()
		log.Printf("send,camera,%s,%s", id.String(), strconv.FormatInt(timeNow, 10))

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

		requestsAwaitAnswer = append(requestsAwaitAnswer, RequestAwait{
			UUID: id.String(),
			Time: timeNow,
		})
	}
}

func logLatency(data *Pick) {
	for _, request := range requestsAwaitAnswer {
		if data.UUID == request.UUID {
			var endTime = time.Now().UnixNano()
			var timeDelta = endTime - request.Time

			hostname, err := os.Hostname()
			if err != nil {
				log.Println(err)
				hostname = ""
			}

			latencyData := LatencyData{
				WorkerID:  hostname,
				Timestamp: request.Time,
				ActType:   0,
				TimeDelta: timeDelta,
			}

			latencyJson, err := json.Marshal(latencyData)
			if err != nil {
				log.Println(err)
			}

			log.Printf("latency: %v", string(latencyJson))
		}
	}
}

func pickerHandler(w http.ResponseWriter, r *http.Request) {
	timestamp := strconv.FormatInt(time.Now().UnixNano(), 10)

	var data Pick
	err := json.NewDecoder(r.Body).Decode(&data)

	if err != nil {
		log.Print(err)
		return
	}

	if data.Ready {
		log.Println("Picking the plant.")
	} else {
		log.Println("Not picking the plant.")
	}

	log.Printf("recv,pick,%s,%s,%t", data.UUID, timestamp, data.Ready)

	logLatency(&data)
}

func main() {
	go sendImage()

	http.HandleFunc("/picker", pickerHandler)
	log.Fatal(http.ListenAndServe(":"+os.Getenv("IMAGE_EDGE_DEVICE_PORT"), nil))

}
