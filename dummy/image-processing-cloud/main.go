package main

import (
	// "compress/gzip"
	"bufio"
	"bytes"
	"context"
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

	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

var counter = 0
var N = os.Getenv("RATE_IN_IMAGES") // Sendrate in images e.g. 10 images reveiced before training is started
var maxEpochs = os.Getenv("EPOCHS") // Epochs
var trainingStatus = false
var db *mongo.Database

var imageEdgeTrainEndpoint string = fmt.Sprintf("http://%s:%s/model", os.Getenv("IMAGE_EDGE_IP"), os.Getenv("IMAGE_EDGE_PORT"))

type LatencyData struct {
	WorkerID  string `json:"workerID"`
	Timestamp int64  `json:"timestamp"`
	ActType   int    `json:"type"`
	TimeDelta int64  `json:"timeDelta"`
}

type Request struct {
	Img  string `json:"img"`
	UUID string `json:"uuid"`
}

type Model struct {
	Hweights []byte `json:hweights`
	Oweights []byte `json:oweights`
}

func sendModel(net *Network) {
	log.Println("Start sending Model")
	hWeights, err := net.hiddenWeights.MarshalBinary()
	if err != nil {
		log.Println("Error sending model.")
	}

	oWeights, err := net.outputWeights.MarshalBinary()
	if err != nil {
		log.Println("Error sending model.")
	}

	model := &Model{Hweights: hWeights, Oweights: oWeights}

	data, err := json.Marshal(model)

	if err != nil {
		return
	}

	req, err := http.NewRequest("POST", imageEdgeTrainEndpoint, bytes.NewReader(data))

	if err != nil {
		return
	}

	log.Printf("send,%s", strconv.FormatInt(time.Now().UnixNano(), 10))

	_, err = (&http.Client{}).Do(req)

	if err != nil {
		log.Print(err)
	}

	log.Println("Sended Model")
}

func trainData(d Request) {
	NInt, _ := strconv.Atoi(N)
	maxEpochsInt, _ := strconv.Atoi(maxEpochs)

	if counter >= NInt && !trainingStatus {
		trainingStatus = true
		log.Println("Taining starts")

		// source: https://github.com/sausheong/gonn
		net := CreateNetwork(784, 200, 10, 0.1)

		rand.Seed(time.Now().UTC().UnixNano())
		startTime := time.Now().UnixNano()

		for epochs := 0; epochs < maxEpochsInt; epochs++ { // epochs < 5
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
		// TODO: what is 0?
		logLatency("Training", startTime, 0)

		trainingStatus = false
		go sendModel(&net)
	} else {
		counter += 1
	}
}

func savePlant(d Request, path string) {
	collection := db.Collection(path)
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	res, err := collection.InsertOne(ctx, d)
	if err != nil {
		log.Println(err)
	}

	log.Println(res)
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

	go savePlant(data, "sick")
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

	go savePlant(data, "all")
	go trainData(data)
}

func logLatency(name string, startTime int64, actType int) {
	var endTime = time.Now().UnixNano()
	var timeDelta = endTime - startTime

	latencyData := LatencyData{
		WorkerID:  name,
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
	uri := "mongodb://" + os.Getenv("MONGODB_IP") + ":" + os.Getenv("MONGODB_PORT")
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	client, err := mongo.Connect(ctx, options.Client().ApplyURI(uri))
	if err != nil {
		panic(err)
	}
	defer func() {
		if err = client.Disconnect(ctx); err != nil {
			panic(err)
		}
	}()

	db = client.Database("plants")

	http.HandleFunc("/sick", SickHandler)
	http.HandleFunc("/train", TrainHandler)

	http.ListenAndServe(":"+os.Getenv("IMAGE_CLOUD_PORT"), nil)
}
