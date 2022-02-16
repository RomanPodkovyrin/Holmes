@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.server

import com.server.controllers.CoreNLPController
import com.server.plugins.configureRouting
import com.server.repository.DataBaseRepository
import com.typesafe.config.ConfigFactory
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.InputStream
import java.security.KeyStore
import java.util.*
import kotlin.system.exitProcess


fun main() {
    val mongodbUrl: String
    val mongodbPort: String
    val coreNlpUrl: String
    val coreNlpPort: String
    val certPath: String
    val certAlias: String
    val certPassword: String
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

        certPath = prop.getProperty("certPath")
        certAlias = prop.getProperty("certAlias")
        certPassword = prop.getProperty("certPassword")
    } catch (e: Exception) {
        println("Error Loading properties: $e")
//        log.error("Error while loading properties file $e")
        exitProcess(-1)
    }

    // Setup KMongo DB
    val dbRepo = DataBaseRepository("mongodb://$mongodbUrl:$mongodbPort", "Book")
    val coreNLPCont = CoreNLPController(coreNlpUrl, coreNlpPort)

    val keyStoreFile = {}.javaClass.getResourceAsStream(certPath)
    val keystore = KeyStore.getInstance(KeyStore.getDefaultType())

    keystore.load(keyStoreFile, certPassword.toCharArray())


    embeddedServer(Netty, applicationEngineEnvironment {
        sslConnector(keyStore = keystore,
            keyAlias = certAlias,
            keyStorePassword = { certPassword.toCharArray() },
            privateKeyPassword = { certPassword.toCharArray() }) {
            port = 8443
        }

        module {
            configureRouting(dbRepo, coreNLPCont)
        }
    }).start(wait = true)
}
