package com.server.controllers

import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

private const val TIMEOUT = 30 * 60000L // 10 * 60000 = 10 minutes

private val client = HttpClient(CIO) {
    engine {
        requestTimeout = 0 // 0 to disable, or a millisecond value to fit your needs
    }
    install(HttpTimeout) {
        requestTimeoutMillis = TIMEOUT
    }
}

class CoreNLPController(private val coreNlpUrl: String, private val coreNlpPort: String) {


    suspend fun sendBookToCoreNLP(
        pipelineContext: PipelineContext<Unit, ApplicationCall>, text: String
    ): String {
        val response: Deferred<String> = pipelineContext.async {
            try {
                client.post("http://$coreNlpUrl:$coreNlpPort/") {
                    timeout {
                        requestTimeoutMillis = TIMEOUT
                    }
                    val properties: Map<String, Any> = mapOf(
                        "annotators" to "tokenize,ssplit,ner",//coref,parse,depparse,coref,kbp,quote,pos
                        "outputFormat" to "json",

                        )

                    parameter("properties", Gson().toJson(properties))
                    body = text
                }
            } catch (e: Exception) {
                println("When calling Corenlp server :$e")
                "ERROR: CORENLP"
            }
        }

        return response.await()
    }
}
