- [Holmes - your private book detector](#holmes---your-private-book-detector)
  - [Demo](#demo)
  - [Architecture](#architecture)
  - [Character Appearances](#character-appearances)
  - [Character Network](#character-network)
  - [UI](#ui)
  - [Future](#future)
  - [Setup](#setup)

<br>
<br>
<br>
<img src=".img/holmes_icon.png" width="400"  />

# Holmes - your private book detector

Holmes is an app and platform that allows anyone to visualise their books with just their smartphone. By finding all characters and locations within an EPUB book using NLP named entity recognition. It can give visualisations of characters that can help readers remember the plot of chapters by looking at which characters appeared where and with whom they have interacted. 

## Demo

<img src=".img/ui.gif" style="zoom:47%;" />
<img src=".img/pie.gif" style="zoom:47%;"  />
<img src=".img/lollipop.gif" style="zoom:47%;"  />
<img src=".img/network.gif" style="zoom:47%;"  />
## Architecture
The application is made up of 4 modules which help process and present the book
<img src=".img/processingSequence.png" width="500"  />


The server part of the application is run in docker for development and deployment simplicity. 

<img src=".img/dockerisation.png" width="500"  />


The project is using GitHub Actions as a CI pipeline
<img src=".img/CIpipeline.png" width="600"  />

<!-- <img src=".img/NetworkSequence.png" width="600"  /> -->

<!-- <img src=".img/networkSteps.png" width="500"  /> -->


When developing Android application, to make the application maintainable and easy to read, the MVVM (Model View ViewModel) architecture was used. 

<img src=".img/mvvm.png" width="300"  />


## Character Appearances
To show a number of times character appeared in a book, two visualisations are used:
- Pie Chart 
<img src=".img/AliceChapter4Pie.jpg" width="200"  />

<!-- <img src=".img/AliceLogLollipopWhole.jpg" width="200"  /> -->

<!-- <img src=".img/AliceLollipopChapter4.jpg" width="200"  /> -->
- Lollipop Chart
<img src=".img/AliceLollipopWhole.jpg" width="200"  />


## Character Network


The character network allows to visualise connection between characters in the book. 
<img src=".img/CharacterNetworkDiagram.png" width="300"  />

This is an interactive visualisation, which allows user to define it's parameters.

<img src=".img/networkControls.jpg" width="200"  />

When it comes to network itself, it can get a bit messy

<img src=".img/GatsbyNetworkChapter2L74C15T.jpg" width="300"  />

Therefore by clicking a character, all their links will be highlighted. 

<img src=".img/HighlightedGatsbyNetworkChapter2L74C15T.jpg" width="300"  />


<!-- <img src=".img/GatsbyNetworkAverageL29C21.jpg" width="200"  /> -->

<!-- <img src=".img/GatsbyNetworkChapter2l100c100T.jpg" width="200"  /> -->


<!-- <img src=".img/GatsbyNetworkMeanL29C21.jpg" width="200"  /> -->

<!-- <img src=".img/GatsbyNetworkMedianL29C21.jpg" width="200"  /> -->


## UI

<!-- <img src=".img/entityList.jpg" width="200"  /> -->

<!-- <img src=".img/EntityProfile.jpg" width="200"  /> -->


<!-- <img src=".img/mainActivity.jpg" width="200"  /> -->

When reading the book, location and characters will be highlighted in text, which can be clicked to view in which chapters they appear. 

<img src=".img/readerInterface.jpg" width="200"  />


<!-- <img src=".img/visualisationMenu.jpg" width="200"  /> -->



## Future
As a proposal for future visualisation, the following prototype was developed.
<img src=".img/future.png" width="500"  />

<!-- <img src=".img/tokenVSpunctuation.png" width="200"  /> -->
<!-- <img src=".img/CoreNLPPipeLines.png" width="500"  /> -->


For the full dissertation please click the image below. 

<a href=".pdf/UoB_Roman_Podkovyrin_Final_Year_Project.pdf" class="image fit"><img src=".img/dissertationIcon.png" alt=""></a>
## Setup
This project contains 3 main modules:
1. [`AndroidApp`](AndroidApp/)
2. [`coreNLP`](coreNLP/)
3. [`Server`](Server/)

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