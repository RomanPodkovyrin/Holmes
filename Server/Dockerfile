# Loading java runtime
FROM openjdk:11-jdk-slim

WORKDIR /src
COPY . /src

RUN apt-get update
# Helps when running on windows system
RUN apt-get install -y dos2unix
RUN dos2unix gradlew

# generate the jar file
RUN bash gradlew fetchJar

WORKDIR /run
RUN cp /src/build/libs/*.jar /run/server.jar

# Port where the server is runnig
EXPOSE 8080

# When the server starts run the jar file
CMD java -jar /run/server.jar

# Dockerfile structure
# /src
# - Location from where jar will be built

# /run
# - Has the jar file in it