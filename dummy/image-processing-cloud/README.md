# Image processing worker
The image-processing-cloud is supposed to run on an cloud worker. It receives base64 encoded disease infected plant images on route '\sick' and saves them into a database.

TODO: connection to database

```bash
docker build -t image-processing-cloud .
```

```bash
docker run -it --rm -p 5051:5050 image-processing-cloud
```