package com.server

import com.mongodb.BasicDBObject
import com.server.models.ProcessedBook
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.server.plugins.configureRouting
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

//import io.ktor.network.tls.certificates.*
//import java.io.File

fun main() {
//
//    val keyStoreFile = File("build/keystore.jks")
//    val keystore = generateCertificate(
//        file = keyStoreFile,
//        keyAlias = "sampleAlias",
//        keyPassword = "Mir11Nash",
//        jksPassword = "foobar"
//    )

    // Setup KMongo DB
    val client = KMongo.createClient("mongodb://localhost:27017").coroutine
    val database = client.getDatabase("Book")
    val col = database.getCollection<ProcessedBook>()




    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
//        applicationEngineEnvironment {
//            sslConnector(
//                keyStore = keystore,
//                keyAlias = "sampleAlias",
//                keyStorePassword = { "foobar".toCharArray() },
//                privateKeyPassword = { "foobar".toCharArray() }) {
//                port = 8443
//                keyStorePath = keyStoreFile
//            }
//        }
        configureRouting(col)
    }.start(wait = true)
}
