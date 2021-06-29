# Camera
The camera-edge-device simulates an edge camera device by generating base64 encoded images and sends them to the edge.

```bash
docker build -t camera .
```

```bash
docker run -it --rm --env-file ./.env camera
```
<p><h3>Benchmark-Endpoint-Variablen</h3></p>
<ul>
  <li><b>benchmarkEndPointHost</b></li>
  <li><b>benchmarkEndpointPort</b></li>
  <li><b>benchmarkEndpointURL</b></li>
</ul>
