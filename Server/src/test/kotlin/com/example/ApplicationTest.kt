package com.example

import com.google.gson.Gson
import com.server.controllers.CoreNLPController
import com.server.models.Entity
import com.server.models.ProcessedBook
import com.server.plugins.configureRouting
import com.server.repository.DataBaseRepository
import com.server.responses.RoutingResponses
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.litote.kmongo.eq
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verifyNoInteractions
import java.io.File
import java.net.URL
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ApplicationTest {


    private fun getFileFromPath(fileName: String): File? {
        var resource: URL? = null
        try {
            resource = this::class.java.classLoader.getResource(fileName)

        } catch (e: Error) {
            fail("Problem accessing test files")
        }
        if (resource == null) {
            fail("Problem accessing test files")
            return null
        }

        return File(resource.file)
    }

    private val gson = Gson()

    @Mock
    private val mockDBrepo = Mockito.mock(DataBaseRepository::class.java)

    @Mock
    private val mockCoreNLPController = Mockito.mock(CoreNLPController::class.java)

    private val processedBooks: List<ProcessedBook> = arrayListOf(
        ProcessedBook(
            "1984", "Orwell", arrayListOf(
                Entity(1, 2, "", "", "Julia")
            ), arrayListOf(
                Entity(1, 2, "", "", "London")
            )

        )
    )

    private val processedText = getFileFromPath("testing/processedText.json")?.readText()

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Before
    fun setUp() = runBlocking {

        Mockito.`when`(mockDBrepo.find(eq(ProcessedBook::title eq "1984"), any())).thenReturn(processedBooks)
        Mockito.`when`(mockDBrepo.find(eq(ProcessedBook::title eq "Night Manager"), any())).thenReturn(listOf())
        Mockito.`when`(mockDBrepo.find(eq(ProcessedBook::title eq "Sherlock Holmes"), any())).thenReturn(listOf())
        Mockito.`when`(mockDBrepo.insertOne(any())).thenReturn(Unit)

        Mockito.`when`(mockCoreNLPController.sendBookToCoreNLP(any(), any())).thenReturn(processedText)
        Mockito.clearInvocations(mockCoreNLPController)
        Mockito.clearInvocations(mockDBrepo)
    }

    @After
    fun cleanUp() {
        Mockito.reset(mockDBrepo)
        Mockito.reset(mockCoreNLPController)
    }

    @Test
    fun testPing() {
        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(RoutingResponses.PING.message, response.content, "Should respond with a ping message")
            }
        }
    }

    @Test
    fun `test book that exists`() {
        val bookTitle = "1984"
        val bookAuthor = "George Orwell"
        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Get, "/check-book/$bookTitle/$bookAuthor").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val json = response.content
                val actualProcessedBook = gson.fromJson(json, ProcessedBook::class.java)
                assertEquals<ProcessedBook>(processedBooks[0], actualProcessedBook, "Should return processed book")
            }
        }
    }

    @Test
    fun `test book that does not exists`() {
        val bookTitle = "Night Manager"
        val bookAuthor = "John le Carre"
        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Get, "/check-book/$bookTitle/$bookAuthor").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                assertEquals(
                    RoutingResponses.DOES_NOT_EXIST.message, response.content, "Should say that the book does not exist"
                )
            }
        }
    }

    @Test
    fun `test check-book with no params`() {
        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Get, "/check-book/").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun `test new book received for processing`() {
        val bookTitle = "Sherlock Holmes"
        val bookAuthor = "Sir Arthur Conan Doyle"
        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Post, "/process-book/$bookTitle/$bookAuthor") {
                setBody(
                    "\"Whatever have you been doing with yourself, Watson?\" " + "he asked in undisguised wonder, as we walked through London" + " \"You are as thin as a lath and as brown as a nut.\""
                )
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(RoutingResponses.RECEIVED.message, response.content, "Should say the message was received")
            }
        }
    }

    @Test
    fun `test book has already been processed`() {
        val bookTitle = "1984"
        val bookAuthor = "George Orwell"
        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Post, "/process-book/$bookTitle/$bookAuthor") {
                setBody(
                    "Behind Winston's back the voice from the telescreen was still babbling away about pig-iron and the overfulfilment of the Ninth Three-Year Plan."
                )
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(
                    RoutingResponses.ALREADY_PROCESSED.message,
                    response.content,
                    "Book has already been processed"
                )

                verifyNoInteractions(mockCoreNLPController)
            }
        }
    }
}