package com.romanp.fyp.models.book

import org.junit.Assert
import org.junit.Test

class BookInfoTest {

    private val bookInfoWithText = BookInfo(
        0,
        "Test Title",
        "Test Author",
        arrayListOf(
            Chapter("Chapter 1", "Body Text of chapter 1."),
            Chapter("Chapter 2", "Body Text of chapter 2."),
            Chapter("Chapter 3", "Body Text of chapter 3.")
        ),
        arrayListOf(),
        arrayListOf()
    )

    private val bookInfoNoText = BookInfo(
        0,
        "Test Title",
        "Test Author",
        arrayListOf(),
        arrayListOf(),
        arrayListOf()
    )

    @Test
    fun `test bookInfo returns correct body text`() {
        val bodyText = bookInfoWithText.getBodyText()

        Assert.assertEquals(
            "Body text is returned correctly",
            "Body Text of chapter 1.Body Text of chapter 2.Body Text of chapter 3.",
            bodyText
        )
    }

    @Test
    fun `test bookInfo returns empty body text when there are no chapters`() {
        val bodyTex = bookInfoNoText.getBodyText()
        Assert.assertEquals(
            "Assert there is no body text as the are no chapters",
            "",
            bodyTex
        )
    }
}