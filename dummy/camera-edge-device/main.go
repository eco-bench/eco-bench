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
const interval int = 1000

const width int = 100
const height int = 100

type Pick struct {
	ready bool   `json:"ready"`
	UUID  string `json:"uuid"`
}

type Request struct {
	Img  string `json:"img"`
	UUID string `json:"uuid"`
}

func generateImage() string {
	upLeft := image.Point{0, 0}
	lowRight := image.Point{width, height}

	img := image.NewRGBA(image.Rectangle{upLeft, lowRight})

	// Set color for each pixel.
	for x := 0; x < width; x++ {
		for y := 0; y < height; y++ {
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
	ticker := time.NewTicker(time.Duration(interval) * time.Millisecond)

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
}

func main() {
	go sendImage()

	http.HandleFunc("/picker", pickerHandler)
	log.Fatal(http.ListenAndServe(":"+os.Getenv("IMAGE_EDGE_DEVICE_PORT"), nil))

}
