# Loading java runtime
FROM java:jre-alpine

WORKDIR /src
COPY . /src

RUN apk update
# Helps when running on windows system
# RUN apk install -y dos2unix
# RUN dos2unix gradlew

RUN apk add --update --no-cache \
	 unzip \
	 wget

# get the jar file
RUN wget http://nlp.stanford.edu/software/stanford-corenlp-4.3.1.zip
RUN unzip stanford-corenlp-4.3.1.zip && \
        rm stanford-corenlp-4.3.1.zip && \
        mv stanford-corenlp-4.3.1 CoreNLP

WORKDIR CoreNLP
RUN export CLASSPATH="`find . -name '*.jar'`"


ENV PORT 9000
ENV MAXCHAR 345000000000
ENV TIMEOUT 150000000
ENV THREADS 4
# Port where the server is runnig
EXPOSE $PORT

# WORKDIR CoreNLP
# When the server starts run the jar file
# CMD java -cp "*" -mx8g edu.stanford.nlp.pipeline.StanfordCoreNLPServer
CMD java -cp "*" -mx8g edu.stanford.nlp.pipeline.StanfordCoreNLPServer -port 9000 -maxCharLength ${MAXCHAR} -timeout ${TIMEOUT} -threads ${THREADS}

# Dockerfile structure
# /src
# - Location from where jar will be built

# /run
# - Has the jar file in it