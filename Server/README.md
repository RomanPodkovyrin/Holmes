# Server

# Testing
``` aidl
./gradlew test
```

## Build Docker file
Build from root using dockerfile
```
docker build -t server .
```

To run the container
```
docker run --network "fyp-api-network" --name AppServer -p 8080:8080 server
```
To stop
```
docker stop server
```

Save docker image as tar file
```
docker save -o <path for generated tar file> <image name>
docker save -o ~/Downloads/server.tar server
```

On the server load the image into docker
```
docker load -i <path to image tar file>
```


## Docker network
for security
```
docker network create fyp-api-network

```
add to image with `--network "fyp-api-network"`

## Corenlp

to download it 
```
docker pull frnkenstien/corenlp
```
```
 docker run --network "fyp-api-network" -p 9000:9000 --name coreNLP --rm -i -t frnkenstien/corenlp 
```

if running jar
```
java -mx4g -cp "*" edu.stanford.nlp.pipeline.StanfordCoreNLPServer -port 9000 -maxCharLength 345000000000 -timeout 150000000

```

`--name` is used to refer to this when accessing it from other docker images

## Custom corenlp images
```
docker run --network "fyp-api-network" --name CoreNLP -p 9000:9000 nlp
```

## To run on the Server
Download images
```
docker pull romanpod/ktor-fyp-server && docker pull romanpod/corenlp
```

Create the network
```
docker network create fyp-api-network
```

Start services 
```
docker run --name mongodb -d -p 27017:27017 mongo
```
```
docker run --network "fyp-api-network" --name CoreNLP -p 9000:9000 romanpod/corenlp & docker run --network "fyp-api-network" --name AppServer -p 8080:8080 romanpod/ktor-fyp-server
```
