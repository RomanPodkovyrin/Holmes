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
import io.ktor.util.pipeline.*
import org.litote.kmongo.eq
import org.litote.kmongo.json
import java.util.concurrent.TimeUnit


fun Application.configureRouting(dbRepo: DataBaseRepository, coreNLPCont: CoreNLPController) {
    val gson = Gson()

    routing {

        /**
        Root used to ping the server
         */
        get("/") {
            log.debug("'/' ping from ${call.request.local.remoteHost}")
            call.respondText(RoutingResponses.PING.message)
        }

        /**
         * Checks if the book has already been processed
         */
        get("/check-book/{bookName}/{bookAuthor}") {
            val title = call.parameters["bookName"]
            val author = call.parameters["bookAuthor"]

            if (checkForValidInput(title, author)) return@get

            log.info("'/check-book' Check book called for book $title - $author\"")
            val failedList = getFailedBooks(dbRepo, title, author)
            if (failedList.isNotEmpty()) {
                log.info("$title - $author Book has failed processing")
                call.respondText(RoutingResponses.FAILED.message)
                return@get
            }

            val processedList = dbRepo.find(BookData::title eq title, BookData::author eq author)
            if (processedList.isEmpty()) {
                log.info("$title - $author Book does not exists")
                call.respondText(RoutingResponses.DOES_NOT_EXIST.message)
                return@get
            }

            val book = processedList.first()
            call.respondText(book.json)
        }

        /**
         * Processes the book
         */
        post("/process-book/{bookName}/{bookAuthor}") {
            val title = call.parameters["bookName"]
            val author = call.parameters["bookAuthor"]
            if (checkForValidInput(title, author)) return@post

            if (title.isNullOrEmpty() || author.isNullOrEmpty()) {
                call.response.status(HttpStatusCode(400, "title or author not given"))
                return@post
            }
            try {
                log.info("'/process-book' Process book is called for book $title - $author")

                val processedList = dbRepo.find(BookData::title eq title, BookData::author eq author).toList()
                if (processedList.isNotEmpty()) {
                    log.info("$title - $author Book has already been processed before")
                    call.respondText(RoutingResponses.ALREADY_PROCESSED.message)
                    return@post
                }

                val failedList = getFailedBooks(dbRepo, title, author)
                if (failedList.isNotEmpty()) {
                    log.info("$title - $author Book has failed processing")
                    call.respondText(RoutingResponses.FAILED.message)
                    return@post
                }

                val receivedText = call.receiveText()
                val bookInfo = gson.fromJson(receivedText, BookInfo::class.java)
                val bodyText = bookInfo.getBodyText()
                log.debug("Received Book of size ${bodyText.length} characters")

                call.respondText(RoutingResponses.RECEIVED.message)

                val beginTimer = System.currentTimeMillis()
                val requestContent = coreNLPCont.sendBookToCoreNLP(this, bodyText)

                if (requestContent == "ERROR: CORENLP") {
                    throw Exception("Can't process the book")
                }


                val bookData = extractUsefulTags(title, author, requestContent, bookInfo.chapters)
                val end = System.currentTimeMillis()
                val minutesTaken = TimeUnit.MILLISECONDS.toMinutes(end - beginTimer)
                val secondsTaken = TimeUnit.MILLISECONDS.toSeconds(end - beginTimer) % 60
                log.info("Time taken for $title: ${minutesTaken}m:${secondsTaken}s")
                dbRepo.insertOne(bookData)
            } catch (e: Exception) {
                log.info("Failed to process the book $title - $author | Error: ${e.printStackTrace()}")
                val failedList = getFailedBooks(dbRepo, title, author)
                if (failedList.isEmpty()) {
                    dbRepo.insertOneFailed(BookInfo(0, title, author, arrayListOf(), arrayListOf(), arrayListOf()))

                }


                return@post
            }
        }
    }
}

private fun PipelineContext<Unit, ApplicationCall>.checkForValidInput(
    title: String?,
    author: String?
): Boolean {
    val specialCharsList = arrayListOf("`", "\"", "\\", ";", "{", "}", "$")
    if (title == null || author == null || title.length > 40 || author.length > 40) {
        call.response.status(HttpStatusCode(501, "Not Implemented"))
        return true
    }
    specialCharsList.forEach { char ->
        if (title.contains(char) || author.contains(char)) {
            call.response.status(HttpStatusCode(501, "Not Implemented"))
            return true
        }
    }
    return false
}

private suspend fun getFailedBooks(
    dbRepo: DataBaseRepository,
    title: String?,
    author: String?
): List<BookInfo> {
    return dbRepo.findFailed(BookInfo::title eq title, BookInfo::author eq author)
}

