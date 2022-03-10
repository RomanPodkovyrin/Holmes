package com.example

import com.example.TestUtils.Companion.getFileFromPath
import com.google.gson.Gson
import com.server.models.BookData
import com.server.plugins.BookInfo
import com.server.utils.extractUsefulTags
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

class BookProcessingTest {
    private val gson = Gson()
    private val nlpProcessedBook = getFileFromPath("testing/BookProcessingTest/alice.json")?.readText()
    private val bookInfo = getFileFromPath("testing/BookProcessingTest/aliceBookInfo.json")?.readText()
    private val expectedBookDataJson = getFileFromPath("testing/BookProcessingTest/aliceBookData.json")?.readText()

    @Before
    fun setUp() {

    }

    @After
    fun cleanUp() {
    }

    @Test
    fun `test tag Extraction`() {
        val bookInfo = gson.fromJson(bookInfo, BookInfo::class.java)
        val expectedBookData = gson.fromJson(expectedBookDataJson, BookData::class.java)

        if (nlpProcessedBook != null) {
            val bookData = extractUsefulTags(
                bookInfo.title,
                bookInfo.author,
                nlpProcessedBook,
                bookInfo.chapters
            )

            assertNotNull(expectedBookData)
            assertEquals(expectedBookData.author, bookData.author, "Author should match")
            assertEquals(expectedBookData.title, bookData.title, "Title should match")
            assertEquals(
                expectedBookData.characters.size,
                bookData.characters.size,
                "Number of characters should match"
            )
            assertEquals(expectedBookData.locations.size, bookData.locations.size, "Number of locations should match")
            assertEquals(
                expectedBookData.characterDistanceByChapter.size,
                bookData.characterDistanceByChapter.size,
                "Distance by chapters size should match"
            )
            assertEquals(expectedBookData, bookData, "Extracted Book data should match")
        } else {
            fail("FAIL: Failed to load file for testing")
        }

    }

}

