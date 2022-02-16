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
docker run --name mongodb -d -p 27017:27017 --network "fyp-api-network"  -v $HOME/db:/data/db mongo
```

```
docker run --network "fyp-api-network" --name CoreNLP -p 9000:9000 romanpod/corenlp
```

```
docker run --network "fyp-api-network" --name AppServer -p 8443:8443 romanpod/ktor-fyp-server
```

Or with one command

```
docker run --network "fyp-api-network" --name CoreNLP -p 9000:9000 romanpod/corenlp &
docker run --name mongodb -d -p 27017:27017 --network "fyp-api-network"  -v $HOME/db:/data/db mongo &
docker run --network "fyp-api-network" --name AppServer -p 8443:8443 romanpod/ktor-fyp-server
```

# SSL

How to generate certificate Where

- `<name>` - is the alias of the certificate
- `<jks-name>` - file name of jks
- `<ip-address>` - ip address of the server
- `<pem-name>` - file name of pem

```
keytool -genkey -alias <name> -keyalg RSA -keystore <jks-name>.jks -keysize 2048 -ext SAN=IP:<ip-address>

```

```
keytool -exportcert -alias <name> -keystore <jks-name>.jks -rfc -file <pem-file>.pem

```

Ex for server

``` 
keytool -genkey -alias holmesCertificate -keyalg RSA -keystore cert.jks -keysize 2048 -ext SAN=IP:108.61.173.161
```

``` 
keytool -exportcert -alias holmesCertificate -keystore cert.jks -rfc -file cert.pem

```

For local testing

``` 
keytool -genkey -alias holmesCertificateLocal -keyalg RSA -keystore certlocal.jks -keysize 2048 -ext SAN=IP:10.0.2.2
```

``` 
`keytool -exportcert -alias holmesCertificateLocal -keystore certlocal.jks -rfc -file certlocal.pem`
```
