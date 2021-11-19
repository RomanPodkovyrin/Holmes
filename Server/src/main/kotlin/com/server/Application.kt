package com.server

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.server.plugins.configureRouting
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
        configureRouting()
    }.start(wait = true)
}
