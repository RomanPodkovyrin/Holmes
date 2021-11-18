package com.server.plugins

import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.client.request.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.http.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import java.io.FileInputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


private val client = HttpClient(CIO) {
    engine {
        requestTimeout = 0 // 0 to disable, or a millisecond value to fit your needs
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 120000 // 2 mins
    }
}


fun Application.configureRouting() {
    var coreNlpUrl = "localhost"
    var coreNlpPort = "9000"
    // Load properties
    try {
        val fis = FileInputStream("src/main/resources/server.properties")
        val prop = Properties()
        prop.load(fis)
        coreNlpUrl = prop.getProperty("coreNLP_url")
        coreNlpPort = prop.getProperty("coreNLP_port")
    } catch (e: Exception) {
        log.error("Error while loading properties file $e")
        exitProcess(-1)
    }

    routing {
        get("/") {
            log.info("'/' ping from ${call.request.local.remoteHost}")
            call.respondText("ping")
        }

        get("/check-book/{bookName}/{bookAuthor}") {
            log.info("'/check-book' Check book called for book ${call.parameters["bookName"]} - ${call.parameters["bookAuthor"]}\"")
            call.respondText("IMPLEMENT ${call.parameters["bookName"]} and ${call.parameters["bookAuthor"]}")
        }

        post("/process-book/{bookName}/{bookAuthor}") {
            log.info("'/process-book' Process book is called for book ${call.parameters["bookName"]} - ${call.parameters["bookAuthor"]}")
            val text = call.receiveText()
            log.debug("Received body of size ${text.length}")

            val requestContent = sendBookToCoreNLP(this, coreNlpUrl, coreNlpPort, text)
            if (requestContent == "ERROR: CORENLP"){
                call.response.status(HttpStatusCode(500,"Server side error" ))

                return@post
            }

            val (person: ArrayList<JsonObject>, location: ArrayList<JsonObject>) = extractUsefulTags(requestContent)

            log.info("Returning processed book")
            call.respondText("PERSON ${person}, LOCATION $location")
        }
    }
}

private fun extractUsefulTags(requestContent: String): Pair<ArrayList<JsonObject>, ArrayList<JsonObject>> {
    val jsonObject = JsonParser.parseString(requestContent).asJsonObject
    val person: ArrayList<JsonObject> = arrayListOf()
    val location: ArrayList<JsonObject> = arrayListOf()
    jsonObject.get("sentences").asJsonArray.forEach { sentence ->
        sentence.asJsonObject.get("tokens").asJsonArray.forEach { token ->
            val token = token.asJsonObject
            when (token.get("ner").asString) {
                "PERSON" -> person.add(token)
                "LOCATION" -> location.add(token)
            }
        }
    }
    return Pair(person, location)
}

private suspend fun sendBookToCoreNLP(
    pipelineContext: PipelineContext<Unit, ApplicationCall>,
    coreNlpUrl: String,
    coreNlpPort: String,
    text: String
): String {
    val response: Deferred<String> = pipelineContext.async {
        try {
            client.post("http://$coreNlpUrl:$coreNlpPort/") {
                timeout {
                    requestTimeoutMillis = 120000 // 2 mins
                }
                val properties: Map<String, Any> =
                    mapOf(
                        "annotators" to "tokenize,ssplit,ner",//,parse,depparse,coref,kbp,quote,pos
                        "outputFormat" to "json",

                        )
                parameter("properties", Gson().toJson(properties))
                body = text
            }
        } catch (e: Exception) {
            println("When calling Corenlp server :$e")
            //TODO: fix log not working outside of a request
//            log.error("When calling Corenlp server :$e")
            "ERROR: CORENLP"
        }
    }

//    log.info("Calling CoreNLP server")
    return response.await()
}
