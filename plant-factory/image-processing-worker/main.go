// source: https://github.com/OpenFogStack/smart-factory-fog-example

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

// TODO: change ports
var prodcntrlEndpoint string = fmt.Sprintf("http://%s:%s/discard", os.Getenv("CNTRL_IP"), os.Getenv("CNTRL_PORT"))

type Request struct {
	Img  string `json:"img"`
	UUID string `json:"uuid"`
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

func processImage(d Request) {

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

	if blacks/totalpixels > 0.5 {
		// there is a disease, send instruction to prod_cntrl
		fmt.Println("Disease alert!")

		type Request struct {
			UUID string `json:"uuid"`
		}

		// send data
		data, err := json.Marshal(Request{
			UUID: d.UUID,
		})

		req, err := http.NewRequest("POST", prodcntrlEndpoint, bytes.NewReader(data))

		if err != nil {
			return
		}

		log.Printf("send,cfd,%s,%s", d.UUID, strconv.FormatInt(time.Now().UnixNano(), 10))

		_, err = (&http.Client{}).Do(req)

		if err != nil {
			log.Print(err)
		}

	}
}

func main() {

	http.HandleFunc("/image", func(w http.ResponseWriter, r *http.Request) {
		timestamp := strconv.FormatInt(time.Now().UnixNano(), 10)

		var data Request
		err := json.NewDecoder(r.Body).Decode(&data)

		if err != nil {
			log.Print(err)
			return
		}

		log.Printf("recv,image,%s,%s", data.UUID, timestamp)

		go processImage(data)
	})

	log.Fatal(http.ListenAndServe(":5050", nil))
}
