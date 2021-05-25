# Camera
The camera-edge-device simulates an edge camera device by generating base6e encoded images and sends them to the edge.

```bash
docker build -t camera .
```

```bash
docker run -it --rm --env-file ./.env camera
```