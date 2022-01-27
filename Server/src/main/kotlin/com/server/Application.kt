package com.server

import com.server.plugins.configureRouting
import com.server.repository.DataBaseRepository
import io.ktor.server.engine.*
import io.ktor.server.netty.*


fun main() {
    val address = "mongodb" // "localhost"

    // Setup KMongo DB
    val dbRepo = DataBaseRepository("mongodb://$address:27017", "Book")

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {

        configureRouting(dbRepo)
    }.start(wait = true)
}
