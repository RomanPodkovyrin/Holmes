package com.romanp.fyp.viewmodels

import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.models.book.Chapter
import com.romanp.fyp.repositories.BookRepository
import kotlinx.coroutines.runBlocking
import org.junit.*
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
class BookGraphActivityViewModelTest {

    private lateinit var viewModel: BookGraphActivityViewModel
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


        viewModel = BookGraphActivityViewModel(
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
        Assert.assertEquals("Same Initial book", currentBookExpected, book)
        return@runBlocking
    }

    @Test
    fun `Test invalid book id`() = runBlocking {
        val viewModelWithBadID =
            BookGraphActivityViewModel(
                RuntimeEnvironment.getApplication(),
                mockRepo,
                invalidBookID
            )
        val currentBookInfo = viewModelWithBadID.getCurrentBookInfo()
        verify(mockRepo, times(1)).getBookInfo(any(), eq(invalidBookID))
        Assert.assertTrue("Should give book info with error state", currentBookInfo.isError())
    }

    @Test
    fun `Test returns correct BookID`() = runBlocking {
        Assert.assertEquals("Expected to get correct book id", correctBookID, viewModel.getBookID())
    }


}