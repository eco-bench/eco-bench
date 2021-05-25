FROM golang:1.16.4-alpine3.13

RUN mkdir /app
 
COPY . /app
WORKDIR /app

RUN mkdir /app/data
RUN go build -o image-processing-worker . 

EXPOSE 5051

CMD [ "/app/image-processing-worker" ]
