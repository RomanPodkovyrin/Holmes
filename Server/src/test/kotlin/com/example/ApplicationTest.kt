package com.example

import com.example.TestUtils.Companion.getFileFromPath
import com.google.gson.Gson
import com.server.controllers.CoreNLPController
import com.server.models.BookData
import com.server.models.Distance
import com.server.models.Entity
import com.server.models.Mention
import com.server.plugins.BookInfo
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
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {


    private val gson = Gson()

    private val apiSecrete =
        "k6qKl&YBBeflmieT47BBA5^&*nD&DueoZb0sjNRAR7XVNec!Oib5MpPJ43kxW5IYiF!Xvo3ZOEBegT8L7B*xq0sTlbfEo"

    @Mock
    private val mockDBrepo = Mockito.mock(DataBaseRepository::class.java)

    @Mock
    private val mockCoreNLPController = Mockito.mock(CoreNLPController::class.java)

    private val processedBooks: List<BookData> = arrayListOf(
        BookData(
            "1984", "Orwell", arrayListOf(
                Entity(
                    "Julia",
                    aliases = arrayListOf<String>().toSet(),
                    "PERSON",
                    arrayListOf(Mention(1, 6, 1, 2, 0.9)),
                    arrayListOf(arrayListOf(Mention(1, 6, 1, 2, 0.9)))
                ),
                Entity(
                    "Winston",
                    aliases = arrayListOf<String>().toSet(),
                    "PERSON",
                    arrayListOf(Mention(29, 35, 12, 13, 0.9)),
                    arrayListOf(arrayListOf(Mention(29, 35, 12, 13, 0.9)))
                )
            ), arrayListOf(
                Entity(
                    "London",
                    aliases = arrayListOf<String>().toSet(),
                    "CITY",
                    arrayListOf(Mention(10, 16, 4, 5, 0.99)),
                    arrayListOf(arrayListOf(Mention(10, 16, 4, 5, 0.99)))
                )
            ),
            arrayListOf(hashMapOf("Julia,Winston" to Distance(1f, 2, 2, hashMapOf('!' to 1f, '.' to 1f))))

        )
    )

    private val processedText = getFileFromPath("testing/processedText.json")?.readText()

    private val failedBook = BookInfo(1, "Failed Title", "Failed Author", arrayListOf(), arrayListOf(), arrayListOf())

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Before
    fun setUp() = runBlocking {

        Mockito.`when`(mockDBrepo.find(eq(BookData::title eq "1984"), any())).thenReturn(processedBooks)
        Mockito.`when`(mockDBrepo.find(eq(BookData::title eq "Failed Title"), any())).thenReturn(listOf())
        Mockito.`when`(mockDBrepo.find(eq(BookData::title eq "Night Manager"), any())).thenReturn(listOf())
        Mockito.`when`(mockDBrepo.find(eq(BookData::title eq "Sherlock Holmes"), any())).thenReturn(listOf())
        Mockito.`when`(mockDBrepo.insertOne(any())).thenReturn(Unit)
        Mockito.`when`(mockDBrepo.findFailed(eq(BookData::title eq "Failed Title"), any()))
            .thenReturn(listOf(failedBook))
        Mockito.`when`(mockDBrepo.findFailed(eq(BookData::title eq "Night Manager"), any())).thenReturn(listOf())
        Mockito.`when`(mockDBrepo.findFailed(eq(BookData::title eq "1984"), any())).thenReturn(listOf())
        Mockito.`when`(mockDBrepo.findFailed(eq(BookData::title eq "Sherlock Holmes"), any())).thenReturn(listOf())
        Mockito.`when`(mockDBrepo.insertOneFailed(any())).thenReturn(Unit)

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
            handleRequest(HttpMethod.Get, "/$apiSecrete").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(RoutingResponses.PING.message, response.content, "Should respond with a ping message")
            }
        }
    }

    @Test
    fun `testPing without api secrete`() {
        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    fun `test book that exists`() {
        val bookTitle = "1984"
        val bookAuthor = "George Orwell"
        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Get, "/check-book/$bookTitle/$bookAuthor/$apiSecrete").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                val json = response.content
                val actualProcessedBook = gson.fromJson(json, BookData::class.java)
                assertEquals<BookData>(processedBooks[0], actualProcessedBook, "Should return processed book")
            }
        }
    }

    @Test
    fun `test book that does not exists`() {
        val bookTitle = "Night Manager"
        val bookAuthor = "John le Carre"
        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Get, "/check-book/$bookTitle/$bookAuthor/$apiSecrete").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                assertEquals(
                    RoutingResponses.DOES_NOT_EXIST.message, response.content, "Should say that the book does not exist"
                )
            }
        }
    }

    @Test
    fun `test failed book`() {
        val bookTitle = "Failed Title"
        val bookAuthor = "Failed Author"
        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Get, "/check-book/$bookTitle/$bookAuthor/$apiSecrete").apply {
                assertEquals(HttpStatusCode.OK, response.status())

                assertEquals(
                    RoutingResponses.FAILED.message, response.content, "Should say that the book failed processing"
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
        val body =
            "{\"author\":\"$bookAuthor\",\"chapters\":[{\"chapterTitle\":\"Chapter 1\",\"text\":\"Whatever have you been doing with yourself, Watson? he asked in undisguised wonder, as we walked through London You are as thin as a lath and as brown as a nut.\"}],\"characters\":[],\"image\":1,\"locations\":[],\"title\":\"$bookTitle\"}"

        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Post, "/process-book/$bookTitle/$bookAuthor/$apiSecrete") {
                setBody(
                    body
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
            handleRequest(HttpMethod.Post, "/process-book/$bookTitle/$bookAuthor/$apiSecrete") {
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

    @Test
    fun `test book failing processing`() {
        val bookTitle = "Failed Title"
        val bookAuthor = "Failed Author"
        val body =
            "{\"author\":\"$bookAuthor\",\"chapters\":[{\"chapterTitle\":\"Chapter 1\",\"text\":\"Whatever have you been doing with yourself, Watson? he asked in undisguised wonder, as we walked through London You are as thin as a lath and as brown as a nut.\"}],\"characters\":[],\"image\":1,\"locations\":[],\"title\":\"$bookTitle\"}"
        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Post, "/process-book/$bookTitle/$bookAuthor/$apiSecrete") {
                setBody(
                    body
                )
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(
                    RoutingResponses.FAILED.message,
                    response.content,
                    "Book Failed processing"
                )

                verifyNoInteractions(mockCoreNLPController)

            }
        }
    }

    @Test
    fun `test for process book parameter input size limit`() {
        var bookTitle = "aaaaaaaaaabbbbbbbbbbccccccccccdddddddddde" // 41
        var bookAuthor = "Author"
        val body =
            "{\"author\":\"$bookAuthor\",\"chapters\":[{\"chapterTitle\":\"Chapter 1\",\"text\":\"Whatever have you been doing with yourself, Watson? he asked in undisguised wonder, as we walked through London You are as thin as a lath and as brown as a nut.\"}],\"characters\":[],\"image\":1,\"locations\":[],\"title\":\"$bookTitle\"}"
        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Post, "/process-book/$bookTitle/$bookAuthor/$apiSecrete") {
                setBody(
                    body
                )
            }.apply {
                assertEquals(HttpStatusCode.NotImplemented, response.status())
                verifyNoInteractions(mockCoreNLPController)

            }
        }



        bookTitle = "Title"
        bookAuthor = "aaaaaaaaaabbbbbbbbbbccccccccccdddddddddde"// 41
        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Post, "/process-book/$bookTitle/$bookAuthor/$apiSecrete") {
                setBody(
                    body
                )
            }.apply {
                assertEquals(HttpStatusCode.NotImplemented, response.status())
                verifyNoInteractions(mockCoreNLPController)

            }
        }
    }

    @Test
    fun `test checkBook for parameter input size limit`() {
        var bookTitle = "aaaaaaaaaabbbbbbbbbbccccccccccdddddddddde" // 41
        var bookAuthor = "Author"

        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Get, "/check-book/$bookTitle/$bookAuthor/$apiSecrete") {

            }.apply {
                assertEquals(HttpStatusCode.NotImplemented, response.status())
                verifyNoInteractions(mockCoreNLPController)

            }
        }

        bookTitle = "Title"
        bookAuthor = "aaaaaaaaaabbbbbbbbbbccccccccccdddddddddde" // 41

        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Get, "/check-book/$bookTitle/$bookAuthor/$apiSecrete") {

            }.apply {
                assertEquals(HttpStatusCode.NotImplemented, response.status())
                verifyNoInteractions(mockCoreNLPController)

            }
        }
    }

    @Test
    fun `test checkBook is sanitised`() {
        var bookTitle = "Titl;e"
        var bookAuthor = "Author"

        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Get, "/check-book/$bookTitle/$bookAuthor/$apiSecrete") {

            }.apply {
                assertEquals(HttpStatusCode.NotImplemented, response.status())
                verifyNoInteractions(mockCoreNLPController)

            }
        }

        bookTitle = "Title"
        bookAuthor = "Author$" // 41

        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Get, "/check-book/$bookTitle/$bookAuthor/$apiSecrete") {

            }.apply {
                assertEquals(HttpStatusCode.NotImplemented, response.status())
                verifyNoInteractions(mockCoreNLPController)

            }
        }
    }

    @Test
    fun `test processBook is sanitised`() {
        var bookTitle = "Titl;e"
        var bookAuthor = "Author"

        val body =
            "{\"author\":\"$bookAuthor\",\"chapters\":[{\"chapterTitle\":\"Chapter 1\",\"text\":\"Whatever have you been doing with yourself, Watson? he asked in undisguised wonder, as we walked through London You are as thin as a lath and as brown as a nut.\"}],\"characters\":[],\"image\":1,\"locations\":[],\"title\":\"$bookTitle\"}"
        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Post, "/process-book/$bookTitle/$bookAuthor/$apiSecrete") {
                setBody(
                    body
                )
            }.apply {
                assertEquals(HttpStatusCode.NotImplemented, response.status())
                verifyNoInteractions(mockCoreNLPController)

            }
        }

        bookTitle = "Title"
        bookAuthor = "Author$" // 41

        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Post, "/process-book/$bookTitle/$bookAuthor/$apiSecrete") {
                setBody(
                    body
                )
            }.apply {
                assertEquals(HttpStatusCode.NotImplemented, response.status())
                verifyNoInteractions(mockCoreNLPController)

            }
        }
    }


    @Test
    fun `test checkBook api key not present`() {
        val bookTitle = "Title"
        val bookAuthor = "Author"

        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Get, "/check-book/$bookTitle/$bookAuthor") {

            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                verifyNoInteractions(mockCoreNLPController)

            }
        }

    }

    @Test
    fun `test processBook api key not present`() {
        val bookTitle = "Title"
        val bookAuthor = "Author"

        val body =
            "{\"author\":\"$bookAuthor\",\"chapters\":[{\"chapterTitle\":\"Chapter 1\",\"text\":\"Whatever have you been doing with yourself, Watson? he asked in undisguised wonder, as we walked through London You are as thin as a lath and as brown as a nut.\"}],\"characters\":[],\"image\":1,\"locations\":[],\"title\":\"$bookTitle\"}"
        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Post, "/process-book/$bookTitle/$bookAuthor") {
                setBody(
                    body
                )
            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                verifyNoInteractions(mockCoreNLPController)

            }
        }
    }

    @Test
    fun `test checkBook wrong api key`() {
        val bookTitle = "Title"
        val bookAuthor = "Author"
        val wrongKey = "wrongKey"

        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Get, "/check-book/$bookTitle/$bookAuthor/$wrongKey") {

            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                verifyNoInteractions(mockCoreNLPController)

            }
        }

    }

    @Test
    fun `test processBook wrong api key`() {
        val bookTitle = "Title"
        val bookAuthor = "Author"
        val wrongKey = "wrongKey"

        val body =
            "{\"author\":\"$bookAuthor\",\"chapters\":[{\"chapterTitle\":\"Chapter 1\",\"text\":\"Whatever have you been doing with yourself, Watson? he asked in undisguised wonder, as we walked through London You are as thin as a lath and as brown as a nut.\"}],\"characters\":[],\"image\":1,\"locations\":[],\"title\":\"$bookTitle\"}"
        withTestApplication({ configureRouting(mockDBrepo, mockCoreNLPController) }) {
            handleRequest(HttpMethod.Post, "/process-book/$bookTitle/$bookAuthor/$wrongKey") {
                setBody(
                    body
                )
            }.apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                verifyNoInteractions(mockCoreNLPController)

            }
        }
    }
}