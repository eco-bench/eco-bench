FROM golang:1.16.4-alpine3.13

RUN mkdir /app
 
COPY . /app
WORKDIR /app

RUN go build -o camera-edge-device . 

CMD [ "/app/camera-edge-device"] 
