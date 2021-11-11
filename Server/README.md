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
docker run --name AppServer -p 8080:8080 server
```
To stop
```
docker stop server
```