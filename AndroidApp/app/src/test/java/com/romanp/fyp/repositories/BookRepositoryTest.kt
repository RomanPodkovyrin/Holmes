package com.romanp.fyp.repositories

import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.models.book.Chapter
import kotlinx.coroutines.runBlocking
import org.junit.*
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
        ArrayList()
    )


    private val bookRepository = BookRepository.getInstance()


    @Test
    fun `database should be empty`() = runBlocking {
        val recyclerBookInfo = bookRepository.getRecyclerBookInfoList(getApplication())
        recyclerBookInfo.value?.let { Assert.assertTrue(it.isEmpty()) }
        return@runBlocking
    }


    /*
    runBlocking {} to ensure that all running coroutines get a chance to complete before the test function returns.
     */
    @Test
    fun `adds new book into db`() = runBlocking {
        try {
            bookRepository.getBookInfo(getApplication(), 1)
            Assert.fail("Should throw an error, because this ide does not exist")
        } catch (e: Exception) {
            Assert.assertEquals(
                "Unexpected error",
                "java.lang.Exception: Problem getting a book from repository",
                e.toString()
            )
        }

        bookRepository.addBookInfo(getApplication(), currentBookExpected)
        //TODO: finish test
//        val bookFromDB = bookRepository.getBookInfo(getApplication(),1)
//        Assert.assertEquals("Book from DB matches the expected", currentBookExpected, bookFromDB)
        return@runBlocking
    }
}