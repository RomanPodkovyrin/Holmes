# Loading java runtime
FROM zenika/kotlin:1.4.20-jdk12-alpine

WORKDIR /src
COPY . /src

RUN apk update
## Helps when running on windows system
#RUN apk add dos2unix
#RUN dos2unix gradlew

# to keep the size small
RUN apk add --no-cache bash

# generate the jar file
RUN bash gradlew fetchJar

WORKDIR /run
RUN cp /src/build/libs/*.jar /run/server.jar

# Port where the server is runnig
EXPOSE 8080

## Make a user and add to group
#RUN groupadd -r appAPI && useradd -g appAPI appAPI
#
## Set permissions
#RUN chown -R appAPI:appAPI /run
#
## Switch user
#USER appAPI

# When the server starts run the jar file
CMD java -jar /run/server.jar

# Dockerfile structure
# /src
# - Location from where jar will be built

# /run
# - Has the jar file in it