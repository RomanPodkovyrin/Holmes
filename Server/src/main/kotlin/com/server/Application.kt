package com.server

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.server.plugins.configureRouting
import io.ktor.application.*
import io.ktor.network.tls.certificates.*
import java.io.File


fun main() {
    
    val keyStoreFile = File("build/keystore.jks")
    val keystore = generateCertificate(
        file = keyStoreFile,
        keyAlias = "sampleAlias",
        keyPassword = "foobar",
        jksPassword = "foobar"
    )

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
