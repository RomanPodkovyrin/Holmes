package com.example

import com.example.TestUtils.Companion.getFileFromPath
import com.google.gson.Gson
import com.server.models.BookData
import com.server.plugins.BookInfo
import com.server.utils.extractUsefulTags
import com.server.utils.getTextBetweenEntities
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


    private val fullText =
        "Alice was beginning to get very tired of sitting by her sister on the bank, and of having nothing to do: once or twice she had peeped into the book her sister was reading, but it had no pictures or conversations in it, “and what is the use of a book,” thought Alice “without pictures or conversations?”" +
                "So she was considering in her own mind (as well as she could, for the hot day made her feel very sleepy and stupid), whether the pleasure of making a daisy-chain would be worth the trouble of getting up and picking the daisies, when suddenly a White Rabbit with pink eyes ran close by her." +
                "here was nothing so very remarkable in that; nor did Alice think it so very much out of the way to hear the Rabbit say to itself, “Oh dear! Oh dear! I shall be late!” (when she thought it over afterwards, it occurred to her that she ought to have wondered at this, but at the time it all seemed quite natural); " +
                "but when the Rabbit actually took a watch out of its waistcoat-pocket, and looked at it, and then hurried on, Alice started to her feet, for it flashed across her mind that she had never before seen a rabbit with either a waistcoat-pocket, or a watch to take out of it, and burning with curiosity, she ran across the field after it, and fortunately was just in time to see it pop down a large rabbit-hole under the hedge."

    private val expectedExtractedText =
        "Alice was beginning to get very tired of sitting by her sister on the bank, and of having nothing to do: once or twice she had peeped into the book her sister was reading, but it had no pictures or conversations in it, “and what is the use of a book,” thought Alice “without pictures or conversations?”" +
                "So she was considering in her own mind (as well as she could, for the hot day made her feel very sleepy and stupid), whether the pleasure of making a daisy-chain would be worth the trouble of getting up and picking the daisies, when suddenly a White Rabbit"

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

    @Test
    fun `test getting text between two character locations`() {
        var extractedText = getTextBetweenEntities(fullText, Pair(0, 0), Pair(0, 558))
        assertEquals(expectedExtractedText, extractedText, "Should return correct extraction of the text")
        extractedText = getTextBetweenEntities(fullText, Pair(0, 558), Pair(0, 0))
        assertEquals(
            expectedExtractedText,
            extractedText,
            "Should return correct extraction of the text, location order should not matter"
        )
    }


}

