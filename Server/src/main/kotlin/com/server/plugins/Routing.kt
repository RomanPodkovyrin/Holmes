package com.server.plugins

import com.google.gson.Gson
import com.server.controllers.CoreNLPController
import com.server.models.BookData
import com.server.repository.DataBaseRepository
import com.server.responses.RoutingResponses
import com.server.utils.extractUsefulTags
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.litote.kmongo.eq
import org.litote.kmongo.json


fun Application.configureRouting(dbRepo: DataBaseRepository, coreNLPCont: CoreNLPController) {
    val gson = Gson()

    routing {

        /**
        Root used to ping the server
         */
        get("/") {
            log.info("'/' ping from ${call.request.local.remoteHost}")
            call.respondText(RoutingResponses.PING.message)
        }

        /**
         * Checks if the book has already been processed
         */
        get("/check-book/{bookName}/{bookAuthor}") {
            val title = call.parameters["bookName"]
            val author = call.parameters["bookAuthor"]
            log.info("'/check-book' Check book called for book $title - $author\"")

            val list = dbRepo.find(BookData::title eq title, BookData::author eq author)
            if (list.isEmpty()) {
                call.respondText(RoutingResponses.DOES_NOT_EXIST.message)
                return@get
            }

            val book = list.first()
            call.respondText(book.json)
        }

        /**
         * Processes the book
         */
        post("/process-book/{bookName}/{bookAuthor}") {
            val title = call.parameters["bookName"]
            val author = call.parameters["bookAuthor"]
            if (title.isNullOrEmpty() || author.isNullOrEmpty()) {
                //TODO: don't think this one is being called when not giving params, gives 404 instead
                //TODO: should i check it for other routes as well?
                call.response.status(HttpStatusCode(400, "title or author not given"))
                return@post
            }
            log.info("'/process-book' Process book is called for book $title - $author")

            val list = dbRepo.find(BookData::title eq title, BookData::author eq author).toList()
            if (list.isNotEmpty()) {
                log.info("Book has already been processed before")
                call.respondText(RoutingResponses.ALREADY_PROCESSED.message)
                return@post
            }

            //TODO: should i receive json instead?
            val receivedText = call.receiveText()
            val bookInfo = gson.fromJson(receivedText, BookInfo::class.java)
            val bodyText = bookInfo.getBodyText()
            log.debug("Received Book of size ${bodyText.length}")

            call.respondText(RoutingResponses.RECEIVED.message)

            val requestContent = coreNLPCont.sendBookToCoreNLP(this, bodyText)
            if (requestContent == "ERROR: CORENLP") {
                //TODO: never seen this one actually fire
                call.response.status(HttpStatusCode(500, "Server side error"))

                return@post
            }
            //TODO: check if timed out enter error state for the given book

            val bookData = extractUsefulTags(title, author, requestContent, bookInfo.chapters)
            dbRepo.insertOne(bookData)
        }
    }
}

