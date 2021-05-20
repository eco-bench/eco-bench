FROM golang:1.16.4-alpine3.13

RUN mkdir /app
 
COPY . /app
WORKDIR /app

RUN wget https://github.com/sausheong/gonn/raw/master/mnist_dataset/mnist.zip
RUN unzip mnist.zip
RUN rm mnist.zip
RUN mkdir /app/data
 
RUN go build -o image-processing-cloud . 

EXPOSE 5052
 
CMD [ "/app/image-processing-cloud" ]