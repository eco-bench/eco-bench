package main

import (
	// "compress/gzip"
	"bufio"
	"encoding/csv"
	"encoding/json"
	"fmt"
	"io"
	"log"
	"math/rand"
	"net/http"
	"os"
	"strconv"
	"time"
)

var counter = 0
var N = 10
var trainingStatus = false

type Request struct {
	Img  string `json:"img"`
	UUID string `json:"uuid"`
}

func sendModel() {
	h, err := os.Open("data/hweights.model")
	defer h.Close()
	if err == nil {
		log.Println("Error sending hweight.model")
	}

	o, err := os.Open("data/oweights.model")
	if err == nil {
		log.Println("Error sending hweight.model")
	}

}

func trainData(d Request) {
	// Save data and every n values train the new model
	// Train model if counter of images is greater then n
	// Train with old and new data
	// Send new Trained model to edge worker

	if counter >= N && !trainingStatus {
		trainingStatus = true
		log.Println("Taining starts")

		// source: https://github.com/sausheong/gonn
		net := CreateNetwork(784, 200, 10, 0.1)

		rand.Seed(time.Now().UTC().UnixNano())
		t1 := time.Now()

		for epochs := 0; epochs < 1; epochs++ { // epochs < 5
			testFile, _ := os.Open("mnist_train.csv")
			r := csv.NewReader(bufio.NewReader(testFile))
			for {
				record, err := r.Read()
				if err == io.EOF {
					break
				}

				inputs := make([]float64, net.inputs)
				for i := range inputs {
					x, _ := strconv.ParseFloat(record[i], 64)
					inputs[i] = (x / 255.0 * 0.999) + 0.001
				}

				targets := make([]float64, 10)
				for i := range targets {
					targets[i] = 0.001
				}
				x, _ := strconv.Atoi(record[0])
				targets[x] = 0.999

				net.Train(inputs, targets)
			}
			testFile.Close()
		}
		elapsed := time.Since(t1)
		fmt.Printf("\nTime taken to train: %s\n", elapsed)

		save(net)
		trainingStatus = false
		go sendModel()
	} else {
		counter += 1
	}

}

func saveInfectedPlant(d Request) {

}

func SickHandler(w http.ResponseWriter, r *http.Request) {
	timestamp := strconv.FormatInt(time.Now().UnixNano(), 10)

	var data Request
	err := json.NewDecoder(r.Body).Decode(&data)

	if err != nil {
		log.Print(err)
		return
	}

	log.Printf("recv,image,%s,%s", data.UUID, timestamp)

	go saveInfectedPlant(data)
}

func TrainHandler(w http.ResponseWriter, r *http.Request) {
	timestamp := strconv.FormatInt(time.Now().UnixNano(), 10)
	var data Request
	err := json.NewDecoder(r.Body).Decode(&data)

	if err != nil {
		log.Print(err)
		return
	}

	log.Printf("recv,image,%s,%s", data.UUID, timestamp)

	go trainData(data)
}

func main() {
	// TODO: connect over network
	// db, _ = sql.Open("sqlite3", "./plants.db")

	http.HandleFunc("/sick", SickHandler)
	http.HandleFunc("/train", TrainHandler)

	log.Println("Listening on port 5050...")
	http.ListenAndServe(":5050", nil)
}
