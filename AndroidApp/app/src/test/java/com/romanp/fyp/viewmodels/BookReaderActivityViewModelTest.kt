package com.romanp.fyp.viewmodels

import com.romanp.fyp.models.book.AlreadyOnTheFirstPageException
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.models.book.Chapter
import com.romanp.fyp.models.book.NoMorePagesException
import com.romanp.fyp.repositories.BookRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class BookReaderActivityViewModelTest {

    private lateinit var viewModel: BookReaderActivityViewModel
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

    @Mock
    private val mockRepo = Mockito.mock(BookRepository::class.java)

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private val correctBookID = 2L
    private val invalidBookID = -1L

    @Before
    fun setUp() {
        Mockito.`when`(mockRepo.getBookInfo(any(), eq(invalidBookID)))
            .doAnswer { throw Exception("Problem getting a book from repository") }
        Mockito.`when`(mockRepo.getBookInfo(any(), eq(correctBookID)))
            .thenReturn(currentBookExpected)


        viewModel = BookReaderActivityViewModel(
            RuntimeEnvironment.getApplication(),
            mockRepo,
            correctBookID
        )
        Mockito.clearInvocations(mockRepo)
    }

    @After
    fun cleanUp() {
        Mockito.reset(mockRepo)
    }


    /*
    runBlocking {} to ensure that all running coroutines get a chance to complete before the test function returns.
     */
    @Test
    fun `returns correct initial book`() = runBlocking {
        val book = viewModel.getCurrentBookInfo()
        assertEquals("Same Initial book", currentBookExpected, book)
        return@runBlocking
    }

    @Test
    fun `starts on the right page`() = runBlocking {
        val chapter = viewModel.getCurrentChapter()
        assertEquals("Correct initial chapter", currentBookExpected.chapters[0], chapter)
    }

    @Test
    fun `next page function works`() = runBlocking {
        var chapter = viewModel.nextButton()
        assertEquals("Correct next chapter 1", currentBookExpected.chapters[1], chapter)
        chapter = viewModel.nextButton()
        assertEquals("Correct next chapter 1", currentBookExpected.chapters[2], chapter)
    }

    @Test
    fun `next page function throws Exception`() = runBlocking {
        var chapter = viewModel.nextButton()
        assertEquals("Correct next chapter 1", currentBookExpected.chapters[1], chapter)
        chapter = viewModel.nextButton()
        assertEquals("Correct next chapter 1", currentBookExpected.chapters[2], chapter)
        try {
            viewModel.nextButton()
        } catch (e: NoMorePagesException) {
            return@runBlocking
        }
        fail("Was expecting no more pages exception")
    }


    @Test
    fun `back page function works`() = runBlocking {
        var chapter = viewModel.nextButton()
        assertEquals("Correct next chapter 1", currentBookExpected.chapters[1], chapter)
        chapter = viewModel.backButton()
        assertEquals("Correct next chapter 0", currentBookExpected.chapters[0], chapter)
    }

    @Test
    fun `back page function throws Exception`() = runBlocking {
        try {
            viewModel.backButton()
        } catch (e: AlreadyOnTheFirstPageException) {
            return@runBlocking
        }
        fail("Was expecting no more pages exception")
    }

    @Test
    fun `Test invalid book id`() = runBlocking {
        val viewModelWithBadID =
            BookReaderActivityViewModel(
                RuntimeEnvironment.getApplication(),
                mockRepo,
                invalidBookID
            )
        val currentBookInfo = viewModelWithBadID.getCurrentBookInfo()
        verify(mockRepo, times(1)).getBookInfo(any(), eq(invalidBookID))
        assertTrue("Should give book info with error state", currentBookInfo.isError())
    }

    @Test
    fun `Test returns correct BookID`() = runBlocking {
        assertEquals("Expected to get correct book id", correctBookID, viewModel.getBookID())
    }


}