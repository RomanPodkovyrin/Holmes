package com.server.plugins

import com.google.gson.JsonObject
import com.server.controllers.sendBookToCoreNLP
import com.server.utils.extractUsefulTags
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.*
import kotlin.system.exitProcess


fun Application.configureRouting() {
    var coreNlpUrl = "localhost"
    var coreNlpPort = "9000"
    // Load properties
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
            log.info("'/check-book' Check book called for book ${call.parameters["bookName"]} - ${call.parameters["bookAuthor"]}\"")
            call.respondText("IMPLEMENT ${call.parameters["bookName"]} and ${call.parameters["bookAuthor"]}")
        }

        // Processes the book
        post("/process-book/{bookName}/{bookAuthor}") {
            log.info("'/process-book' Process book is called for book ${call.parameters["bookName"]} - ${call.parameters["bookAuthor"]}")
            val text = call.receiveText()
            log.debug("Received body of size ${text.length}")

            val requestContent = sendBookToCoreNLP(this, coreNlpUrl, coreNlpPort, text)
            if (requestContent == "ERROR: CORENLP") {
                call.response.status(HttpStatusCode(500, "Server side error"))

                return@post
            }

            val (person: ArrayList<JsonObject>, location: ArrayList<JsonObject>) = extractUsefulTags(requestContent)

            log.info("Returning processed book")
            call.respondText("PERSON ${person}, LOCATION $location")
        }
    }
}

