package com.romanp.fyp.models.book

import com.romanp.fyp.database.BookDatabaseHelper.Companion.gson
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import java.io.File
import java.net.URL


internal class BookUtilTest {


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

    //TODO: figure out how to do parameterized test
//    @ParameterizedTest(name = "isPalindrome should return true for {0}")
//    @ValueSource(strings = ["pg11", "pg84", "pg345", "pg1342", "pg2701", "pg64317", "pg66691"])
    @Test
    fun `processEpub returns the book with stripped info`() {
        val file = "pg84"
        val epub = getFileFromPath("epubs/$file.epub")
        val epubReader = EpubReader()
        val book: Book = epubReader.readEpub(epub?.inputStream())
        val processedBook = BookUtil.processEpub(book)

        val json = getFileFromPath("processedBooks/$file.json")
        val jsonBook = json?.readText()
        val bookObject = gson.fromJson(jsonBook, BookInfo::class.java)
        assertEquals("Same authors", bookObject.author, processedBook.author)
        assertEquals("Book title", bookObject.title, processedBook.title)
        assertEquals("Number of chapters", bookObject.chapters.size, processedBook.chapters.size)
        assertEquals("Image", bookObject.image, processedBook.image)
        assertEquals("Content of the book", bookObject, processedBook)

    }
}