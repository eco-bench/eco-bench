# Image processing edge
The image-processing-edge is supposed to run on an edge worker. It receives base64 encoded images on route '/image' and simulates plant disease recognition. It also send all plant images to the cloud for training the image recognision model and it this new trained models on route '/model'.

```bash
docker build -t image-processing-worker .
```

```bash
docker run -it --rm -p 5051:5051 --env-file ./.env image-processing-worker
```

<p><h3>Benchmark-Endpoint-Variablen</h3></p>
<ul>
  <li><b>benchmarkEndPointHost</b></li>
  <li><b>benchmarkEndpointPort</b></li>
  <li><b>benchmarkEndpointURL</b></li>
</ul>
<p>
Produziert einen JSON-Array mit Objekten (Benchmark-Typen: 3,4 siehe Drive):
  
</p>
 <br> 

 ```JSON
  {"workerID":"Image-Edge",
  "timestamp":1625001357227329800,
  "type":3,
  "timeDelta":449400}
 ```
