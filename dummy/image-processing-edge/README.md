# Image processing edge
The image-processing-edge is supposed to run on an edge worker. It receives base64 encoded images on route '/image' and simulates plant disease recognition. It also send all plant images to the cloud for training the image recognision model and it this new trained models on route '/model'.

```bash
docker build -t image-processing-worker .
```

```bash
docker run -it --rm -p 5051:5051 --env-file ./.env image-processing-worker
```