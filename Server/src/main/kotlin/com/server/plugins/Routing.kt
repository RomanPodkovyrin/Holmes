package com.server.plugins

import com.google.gson.JsonObject
import com.server.controllers.sendBookToCoreNLP
import com.server.models.Entity
import com.server.models.ProcessedBook
import com.server.utils.extractUsefulTags
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.json
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


fun Application.configureRouting(collection: CoroutineCollection<ProcessedBook>) {
    var coreNlpUrl = "localhost"
    var coreNlpPort = "9000"
    // Load properties
    // TODO move to application and pass in the parameters for easy testability
    try {
        val fis = javaClass.getResourceAsStream("/server.properties")
        val prop = Properties()
        prop.load(fis)
        coreNlpUrl = prop.getProperty("coreNLP_url")
        coreNlpPort = prop.getProperty("coreNLP_port")
    } catch (e: Exception) {
        log.error("Error while loading properties file $e")
        exitProcess(-1)
    }

    routing {

        // Root used to ping the server
        get("/") {
            log.info("'/' ping from ${call.request.local.remoteHost}")
            call.respondText("ping")
        }

        // Checks if the book has already been processed
        // TODO: IMPLEMENT
        get("/check-book/{bookName}/{bookAuthor}") {
            val title = call.parameters["bookName"]
            val author = call.parameters["bookAuthor"]
            log.info("'/check-book' Check book called for book $title - $author\"")

            val list = collection.find(ProcessedBook::title eq title , ProcessedBook::author eq author).toList()
            if (list.isEmpty()){
                //TODO: make enum with messages
                call.respondText("Does not Exist")
                return@get
            }
            val book = collection.find(ProcessedBook::title eq title , ProcessedBook::author eq author).toList().first()
            call.respondText(book.json)
        }

        // Processes the book
        post("/process-book/{bookName}/{bookAuthor}") {
            val title = call.parameters["bookName"]
            val author = call.parameters["bookAuthor"]
            if (title.isNullOrEmpty() || author.isNullOrEmpty()){
                call.response.status(HttpStatusCode(400, "title or author not given"))

                return@post
            }
            log.info("'/process-book' Process book is called for book $title - $author")
            val text = call.receiveText()
            
            log.debug("Received body of size ${text.length}")
            val list = collection.find(ProcessedBook::title eq title , ProcessedBook::author eq author).toList()
            if (list.isNotEmpty()){

                log.info("Book has already been processed before")
                call.respondText("Already Processed")
                return@post
            }
            call.respondText("Received")

            val requestContent = sendBookToCoreNLP(this, coreNlpUrl, coreNlpPort, text)
            if (requestContent == "ERROR: CORENLP") {
                call.response.status(HttpStatusCode(500, "Server side error"))

                return@post
            }

            val (person: ArrayList<Entity>, location: ArrayList<Entity>) = extractUsefulTags(requestContent)
            collection.insertOne(ProcessedBook(title, author, characters = person, locations =  location))
//            log.info("Returning processed book")
//            call.respondText("PERSON ${person}, LOCATION $location")
        }
    }
}

