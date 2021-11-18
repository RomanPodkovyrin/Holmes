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
//    @Test
//    fun testBookProcessing() {
//
//    }
}