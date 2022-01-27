package com.example

import com.google.gson.Gson
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
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    private val gson = Gson()

    @Mock
    private val mockDBrepo = Mockito.mock(DataBaseRepository::class.java)

    private val processedBooks: List<ProcessedBook> = arrayListOf(
        ProcessedBook(
            "1984", "Orwell", arrayListOf(
                Entity(1, 2, "", "", "Julia")
            ), arrayListOf(
                Entity(1, 2, "", "", "London")
            )

        )
    )


    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Before
    fun setUp() = runBlocking {

        Mockito.`when`(mockDBrepo.find(eq(ProcessedBook::title eq "1984"), any())).thenReturn(processedBooks)
        Mockito.`when`(mockDBrepo.find(eq(ProcessedBook::title eq "Night Manager"), any())).thenReturn(listOf())
        Mockito.`when`(mockDBrepo.insertOne(any())).thenReturn(Unit)

        Mockito.clearInvocations(mockDBrepo)
    }

    @After
    fun cleanUp() {
        Mockito.reset(mockDBrepo)
    }

    @Test
    fun testPing() {
        withTestApplication({ configureRouting(mockDBrepo,"","") }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(RoutingResponses.PING.message, response.content)
            }
        }
    }

    @Test
    fun `check book that exists`() {
        val bookTitle = "1984"
        val bookAuthor = "George Orwell"
        withTestApplication({ configureRouting(mockDBrepo,"","") }) {
            handleRequest(HttpMethod.Get, "/check-book/$bookTitle/$bookAuthor").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val json = response.content
                val actualProcessedBook = gson.fromJson(json, ProcessedBook::class.java)
                assertEquals<ProcessedBook>(processedBooks[0], actualProcessedBook)
            }
        }
    }

    @Test
    fun `check book that does not exists`() {
        val bookTitle = "Night Manager"
        val bookAuthor = "John le Carre"
        withTestApplication({ configureRouting(mockDBrepo,"","") }) {
            handleRequest(HttpMethod.Get, "/check-book/$bookTitle/$bookAuthor").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                assertEquals(RoutingResponses.DOES_NOT_EXIST.message, response.content)
            }
        }
    }

    @Test
    fun testBookCheckNoParams() {
        withTestApplication({ configureRouting(mockDBrepo,"","") }) {
            handleRequest(HttpMethod.Get, "/check-book/").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    //TODO: test book processing.
//    @Test
//    fun testBookProcessingPersonAndLocation() {
//        val bookTitle = "Sherlock Holmes: The Complete Novels and Stories Volume I"
//        val bookAuthor = "Sir Arthur Conan Doyle"
//        withTestApplication({ configureRouting() }) {
//            handleRequest(HttpMethod.Post, "/process-book/$bookTitle/$bookAuthor"){
//                setBody("\"Whatever have you been doing with yourself, Watson?\" " +
//                        "he asked in undisguised wonder, as we walked through London" +
//                        " \"You are as thin as a lath and as brown as a nut.\"")
//            }.apply {
//                assertEquals(HttpStatusCode.OK, response.status())
//                assertEquals(
//                    "PERSON [{" +
//                            "\"index\":10," +
//                            "\"word\":\"Watson\"," +
//                            "\"originalText\":\"Watson\"," +
//                            "\"lemma\":\"Watson\"," +
//                            "\"characterOffsetBegin\":45," +
//                            "\"characterOffsetEnd\":51," +
//                            "\"pos\":\"NNP\"," +
//                            "\"ner\":\"PERSON\"," +
//                            "\"before\":\" \"," +
//                            "\"after\":\"\"}], " +
//                        "LOCATION [{" +
//                            "\"index\":11," +
//                            "\"word\":\"London\"," +
//                            "\"originalText\":\"London\"," +
//                            "\"lemma\":\"London\"," +
//                            "\"characterOffsetBegin\":107," +
//                            "\"characterOffsetEnd\":113," +
//                            "\"pos\":\"NNP\"," +
//                            "\"ner\":\"CITY\"," +
//                            "\"before\":\" \"," +
//                            "\"after\":\" \"}]",
//                    response.content)
//            }
//        }
//    }
}