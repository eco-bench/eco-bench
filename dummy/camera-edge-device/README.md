# Camera
The camera-edge-device simulates an edge camera device by generating base64 encoded images and sends them to the edge.

```bash
docker build -t camera-edge-device .
```

```bash
docker run -it --rm --env-file ./.env camera-edge-device
```

 ```JSON
  {"workerID":"Picking-Robot",
  "timestamp":1625001357227329800,
  "type":0,
  "timeDelta":449400}
 ```
