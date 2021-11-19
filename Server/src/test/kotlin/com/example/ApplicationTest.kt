package com.example

import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*
import com.server.plugins.configureRouting

class ApplicationTest {

    @Test
    fun testRoot() {
        withTestApplication({ configureRouting() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("ping", response.content)
            }
        }
    }

    @Test
    fun testBookCheck() {
        val bookTitle = "1984"
        val bookAuthor = "George Orwell"
        withTestApplication({ configureRouting() }) {
            handleRequest(HttpMethod.Get, "/check-book/$bookTitle/$bookAuthor").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("IMPLEMENT $bookTitle and $bookAuthor", response.content)
            }
        }
    }

    @Test
    fun testBookCheckNoParams() {
        withTestApplication({ configureRouting() }) {
            handleRequest(HttpMethod.Get, "/check-book/").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    //TODO: test book processing.
    @Test
    fun testBookProcessingPersonAndLocation() {
        val bookTitle = "Sherlock Holmes: The Complete Novels and Stories Volume I"
        val bookAuthor = "Sir Arthur Conan Doyle"
        withTestApplication({ configureRouting() }) {
            handleRequest(HttpMethod.Post, "/process-book/$bookTitle/$bookAuthor"){
                setBody("\"Whatever have you been doing with yourself, Watson?\" " +
                        "he asked in undisguised wonder, as we walked through London" +
                        " \"You are as thin as a lath and as brown as a nut.\"")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(
                    "PERSON [{" +
                            "\"index\":10," +
                            "\"word\":\"Watson\"," +
                            "\"originalText\":\"Watson\"," +
                            "\"lemma\":\"Watson\"," +
                            "\"characterOffsetBegin\":45," +
                            "\"characterOffsetEnd\":51," +
                            "\"pos\":\"NNP\"," +
                            "\"ner\":\"PERSON\"," +
                            "\"before\":\" \"," +
                            "\"after\":\"\"}], " +
                        "LOCATION [{" +
                            "\"index\":11," +
                            "\"word\":\"London\"," +
                            "\"originalText\":\"London\"," +
                            "\"lemma\":\"London\"," +
                            "\"characterOffsetBegin\":107," +
                            "\"characterOffsetEnd\":113," +
                            "\"pos\":\"NNP\"," +
                            "\"ner\":\"CITY\"," +
                            "\"before\":\" \"," +
                            "\"after\":\" \"}]",
                    response.content)
            }
        }
    }
}