package com.server

import com.server.controllers.CoreNLPController
import com.server.plugins.configureRouting
import com.server.repository.DataBaseRepository
import com.typesafe.config.ConfigFactory
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.server.plugins.configureRouting
import io.ktor.application.*
import io.ktor.network.tls.certificates.*
import java.io.File

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

    val keyStoreFile = File("build/keystore.jks")
    val keystore = generateCertificate(
        file = keyStoreFile,
        keyAlias = "sampleAlias",
        keyPassword = "foobar",
        jksPassword = "foobar"
    )

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {

        configureRouting(dbRepo, coreNLPCont)
    }.start(wait = true)
    val environment = applicationEngineEnvironment {
        connector {
            port = 8080
        }
        sslConnector(
            keyStore = keystore,
            keyAlias = "sampleAlias",
            keyStorePassword = { "foobar".toCharArray() },
            privateKeyPassword = { "foobar".toCharArray() }) {
            port = 8443
            keyStorePath = keyStoreFile
        }
        module(Application::configureRouting)
    }
    embeddedServer(Netty,environment).start(wait = true)
}
