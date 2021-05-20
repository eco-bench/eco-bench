# Image processing worker
The image processing worker is supposed to run on an edge worker. It receives base64 encoded images on route '\image' and simulates plant disease recognition.

```bash
docker build -t image-processing-worker .
```

```bash
docker run -it --rm -p 5051:5050 image-processing-worker
```