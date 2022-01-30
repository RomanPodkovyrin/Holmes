package com.example

import com.example.TestUtils.Companion.getFileFromPath
import com.google.gson.Gson
import com.server.models.BookData
import com.server.utils.extractUsefulTags
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class BookProcessingTest {
    private val gson = Gson()
    private val nlpProcessedBook = getFileFromPath("testing/BookProcessingTest/alice.json")?.readText()
    @Before
    fun setUp() {

    }

    @After
    fun cleanUp() {
    }

    @Test
    fun `test tag Extraction`() {

        if (nlpProcessedBook != null) {
            val bookData = extractUsefulTags("Alice's Adventures in Wonderland","Lewis Carrol", nlpProcessedBook)
            val expectedBookDataJson = getFileFromPath("testing/BookProcessingTest/aliceBookData.json")?.readText()
            val expectedBookData = gson.fromJson(expectedBookDataJson, BookData::class.java)
            assertEquals(expectedBookData,bookData,"Extracted Book data should match")
        } else {
            fail("FAIL: Failed to load file for testing")
        }

    }
}