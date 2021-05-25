# Image processing cloud
The image-processing-cloud is supposed to run on an cloud worker. It receives base64 encoded disease infected plant images on route '/sick' and saves them into a database. It also receives base64 encoded plant images on route '/train' to train it's image processing model and downstream this new trained model to the edge.

TODO: connection to MongoDB

```bash
docker build -t image-processing-cloud .
```

```bash
 docker run -it --rm -p 5052:5052 --env-file ./.env image-processing-cloud
```

## MongoDB

```bash
docker run --name some-mongo -d mongo:tag
```

```bash
docker run -p 27017:27017 -v ~/Developer/eco-bench/dummy/mongodb-data:/data/db --name some-mongo -d mongo:tag
```