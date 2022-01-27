package com.server

import com.server.controllers.CoreNLPController
import com.server.plugins.configureRouting
import com.server.repository.DataBaseRepository
import com.typesafe.config.ConfigFactory
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.InputStream
import java.util.*
import kotlin.system.exitProcess


fun main() {
    var mongodbUrl = "localhost"
    var mongodbPort = "27017"
    var coreNlpUrl = "localhost"
    var coreNlpPort = "9000"

    // Load properties
    try {
        ConfigFactory.load()
        val fis: InputStream = { }.javaClass.getResourceAsStream("/server.properties")
        val prop = Properties()
        prop.load(fis)
        coreNlpUrl = prop.getProperty("coreNLP_url")
        coreNlpPort = prop.getProperty("coreNLP_port")

        mongodbUrl = prop.getProperty("mongodbUrl")
        mongodbPort = prop.getProperty("mongodbPort")
    } catch (e: Exception) {
        println("Error Loading properties: $e")
//        log.error("Error while loading properties file $e")
        exitProcess(-1)
    }

    // Setup KMongo DB
    val dbRepo = DataBaseRepository("mongodb://$mongodbUrl:$mongodbPort", "Book")
    val coreNLPCont = CoreNLPController(coreNlpUrl, coreNlpPort)

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {

        configureRouting(dbRepo, coreNLPCont)
    }.start(wait = true)
}
