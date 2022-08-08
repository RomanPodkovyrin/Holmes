# Holmes
## Your privet book detector

<img src=".img/AliceChapter4Pie.jpg" style="zoom:17%;" />

<img src=".img/AliceLogLollipopChapter4.jpg" style="zoom:17%;" />

<img src=".img/AliceLogLollipopWhole.jpg" style="zoom:17%;" />

<img src=".img/AliceLollipopChapter4.jpg" style="zoom:17%;" />

<img src=".img/AliceLollipopWhole.jpg" style="zoom:17%;" />

<img src=".img/AliceWholePie.jpg" style="zoom:17%;" />

<img src=".img/AndroidDB.png" style="zoom:17%;" />

<img src=".img/CharacterNetworkDiagram.png" style="zoom:17%;" />

<img src=".img/CIpipeline.png" style="zoom:17%;" />
<img src=".img/CoreNLPPipeLines.png" style="zoom:17%;" />

<img src=".img/dockerisation.png" style="zoom:17%;" />

<img src=".img/entityList.jpg" style="zoom:17%;" />

<img src=".img/EntityProfile.jpg" style="zoom:17%;" />

<img src=".img/future.png" style="zoom:17%;" />

<img src=".img/GatsbyNetworkAverageL29C21.jpg" style="zoom:17%;" />

<img src=".img/GatsbyNetworkChapter2l100c100T.jpg" style="zoom:17%;" />

<img src=".img/GatsbyNetworkChapter2L74C15T.jpg" style="zoom:17%;" />

<img src=".img/GatsbyNetworkMeanL29C21.jpg" style="zoom:17%;" />

<img src=".img/GatsbyNetworkMedianL29C21.jpg" style="zoom:17%;" />

<img src=".img/HighlightedGatsbyNetworkChapter2L74C15T.jpg" style="zoom:17%;" />

<img src=".img/mainActivity.jpg" style="zoom:17%;" />


<img src=".img/mvvm.png" style="zoom:17%;" />

<img src=".img/networkControls.jpg" style="zoom:17%;" />

<img src=".img/NetworkSequence.png" style="zoom:17%;" />

<img src=".img/networkSteps.png" style="zoom:17%;" />

<img src=".img/processingSequence.png" style="zoom:17%;" />

<img src=".img/readerInterface.jpg" style="zoom:17%;" />

<img src=".img/tokenVSpunctuation.png" style="zoom:17%;" />

<img src=".img/visualisationMenu.jpg" style="zoom:17%;" />

Final Year project
This project contains 3 main modules:
1. `AndroidApp`
2. `coreNLP`
3. `Server`

Each module contains README which documents how they can be run


Start coreNLP
```
docker pull romanpod/corenlp
docker run  --name CoreNLP -p 9000:9000 romanpod/corenlp
```

Start MongoDB
```
docker run --name mongodb -d -p 27017:27017  \
    -v $HOME/db:/data/db mongo --noscripting 
```

To start the server
in `Server/src/main/resources/server.properties` set the following
```
# Core NLP
# coreNLP_url = coreNLP
coreNLP_port = 9000

# Mongo DB
# mongodbUrl = mongodb
mongodbPort = 27017

# Certificates
# certPath = /certificate/cert.jks
# certAlias = holmesCertificate
certPassword = OyBhf&A3F8p&Shz6Xvw3ePisaRMXvBII3Q1i@wawKLw!$4#I3uRVtPe0WGQLQbd#8NaxmdI%!k@NppKSeVsfpDKkuxTWkm9CMmM


# Local Testing
mongodbUrl = localhost
coreNLP_url = localhost
certAlias = holmesCertificateLocal
certPath = /certificate/certlocal.jks
```
Run the server from Intellij for ease

To run android app open Android studio and start an emulator