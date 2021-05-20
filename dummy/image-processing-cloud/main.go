package main

import (
	// "compress/gzip"
	"database/sql"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"strconv"
	"time"

	_ "github.com/mattn/go-sqlite3"
)

var db *sql.DB

type Request struct {
	Img  string `json:"img"`
	UUID string `json:"uuid"`
}

// func compress(data bytes.Buffer) {
// 	var buf bytes.Buffer
// 	zw := gzip.NewWriter(&buf)
// 	_, err := zw.Write([]byte("A long time ago in a galaxy far, far away..."))
// 	if err != nil {
// 		log.Fatal(err)
// 	}

// 	if err := zw.Close(); err != nil {
// 		log.Fatal(err)
// 	}
// }

// func decompress(data bytes.Buffer) {
// 	zr, err := gzip.NewReader(&data)
// 	if err != nil {
// 		log.Fatal(err)
// 	}

// 	fmt.Printf("Name: %s\nComment: %s\nModTime: %s\n\n", zr.Name, zr.Comment, zr.ModTime.UTC())

// 	if _, err := io.Copy(os.Stdout, zr); err != nil {
// 		log.Fatal(err)
// 	}

// 	if err := zr.Close(); err != nil {
// 		log.Fatal(err)
// 	}
// }

func saveInfectedPlant(d Request) {
	// decoded, err := base64.StdEncoding.DecodeString(d.Img)
	tx, err := db.Begin()
	if err != nil {
		log.Fatal(err)
	}

	stmt, err := tx.Prepare("insert into plants(id, image) values(?, ?, ?, ?, ?)")
	if err != nil {
		log.Fatal(err)
	}
	defer stmt.Close()

	_, err = stmt.Exec(d.UUID, d.Img)
	if err != nil {
		log.Fatal(err)
	}

	tx.Commit()

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

// func DataHandler(w http.ResponseWriter, r *http.Request) {
// 	io.WriteString(w, time.Now().Format("2006-01-02 15:04:05"))
// 	buf := ioutil.ReadAll(r.Body)
// 	go compress(buf)
// }

func main() {
	// http.HandleFunc("/data", DataHandler)

	// TODO: connect over network
	db, _ = sql.Open("sqlite3", "./plants.db")
	// if err != nil {
	// 	// return nil, err
	// 	fmt.Println("Error")
	// }

	http.HandleFunc("/sick", SickHandler)
	fmt.Println("Listening on port 5050...")
	http.ListenAndServe(":5050", nil)
}
