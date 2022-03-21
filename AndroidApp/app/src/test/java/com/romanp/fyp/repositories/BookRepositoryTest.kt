package com.romanp.fyp.repositories

import com.romanp.fyp.adapters.BookRecyclerViewAdapter
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.models.book.Chapter
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.jupiter.api.fail
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.getApplication
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class BookRepositoryTest {

    private val currentBookExpected = BookInfo(
        1,
        "Orwell",
        "1984",
        arrayListOf(
            Chapter("Chapter 1", "text 1"),
            Chapter("Chapter 2", "text 2"),
            Chapter("Chapter 3", "text 3")
        ),
        ArrayList(),
        ArrayList(),
        ArrayList()
    )


    private val bookRepository = BookRepository.getInstance()

//    @Test
//    fun `should update Processed status`() = runBlocking {
//        val bookID = bookRepository.addBookInfo(getApplication(), currentBookExpected)
//        var recyclerBookInfo = bookRepository.getRecyclerBookInfoList(getApplication()).value
//        if (recyclerBookInfo != null) {
//            Assert.assertTrue(
//                "",
//                recyclerBookInfo.contains(
//                    BookRecyclerViewAdapter.RecyclerBookInfo(
//                        2131165287,
//                        "1984",
//                        "Orwell",
//                        1,
//                        false
//                    )
//                )
//            )
//        } else {
//            fail("Should contain BookInfo in the list")
//        }
//        bookRepository.updateBook(
//            getApplication(), 1, ProcessedBook(
//                "", "", arrayListOf(),
//                arrayListOf()
//            )
//        )
//        recyclerBookInfo = bookRepository.getRecyclerBookInfoList(getApplication()).value
//        if (recyclerBookInfo != null) {
//            Assert.assertEquals(
//                "",
//                recyclerBookInfo[0],
//                BookRecyclerViewAdapter.RecyclerBookInfo(2131165287, "1984", "Orwell", 1, true)
//            )
//        } else {
//            fail("Should contain BookInfo in the list")
//        }
//        return@runBlocking
//    }

    @Test
    fun `database should be empty`() = runBlocking {
        val recyclerBookInfo = bookRepository.getRecyclerBookInfoList(getApplication())
        recyclerBookInfo.value?.let { Assert.assertTrue(it.isEmpty()) }
        return@runBlocking
    }

    @Test
    fun `should return a list of BookInfo`() = runBlocking {
        val bookID = bookRepository.addBookInfo(getApplication(), currentBookExpected)
        println(bookID)
        val recyclerBookInfo = bookRepository.getRecyclerBookInfoList(getApplication()).value
        if (recyclerBookInfo != null) {
            Assert.assertEquals(
                "",
                recyclerBookInfo[0],
                BookRecyclerViewAdapter.RecyclerBookInfo(2131165287, "1984", "Orwell", 1, BookRecyclerViewAdapter.ProcessedState.PROCESSING)
            )
        } else {
            fail("Should contain BookInfo in the list")
        }
        return@runBlocking
    }

    /*
    runBlocking {} to ensure that all running coroutines get a chance to complete before the test function returns.
     */
    @Test
    fun `adds new book into db`() = runBlocking {
        try {
            bookRepository.getBookInfo(getApplication(), 1)
            Assert.fail("Should throw an error, because this id does not exist")
        } catch (e: Exception) {
            Assert.assertEquals(
                "Unexpected error",
                "java.lang.Exception: Problem getting a book from repository",
                e.toString()
            )
        }

        bookRepository.addBookInfo(getApplication(), currentBookExpected)
//        val bookFromDB = bookRepository.getBookInfo(getApplication(),bookID)
//        Assert.assertEquals("Book from DB matches the expected", currentBookExpected, bookFromDB)
        return@runBlocking
    }


}