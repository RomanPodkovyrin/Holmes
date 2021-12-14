package com.romanp.fyp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.romanp.fyp.adapters.BookRecyclerViewAdapter
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.models.book.Chapter
import com.romanp.fyp.repositories.BookInfoRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.getApplication
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class MainViewActivityTest {
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var liveBooks: LiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>>

    @Spy
    private lateinit var booksLiveData: MutableLiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>>


    @Mock
    private val mockRepo = mock(BookInfoRepository::class.java)

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Before
    fun setUp() {

        booksLiveData = MutableLiveData(
            mutableListOf(
                BookRecyclerViewAdapter.RecyclerBookInfo(1, "Orwell", "1984", 1L)
            )
        )
        `when`(mockRepo.getBookInfo(org.mockito.kotlin.any())).thenReturn(booksLiveData)
        `when`(mockRepo.addBookInfo(org.mockito.kotlin.any(), org.mockito.kotlin.any())).thenReturn(
            1
        )

        viewModel = MainActivityViewModel(getApplication(), mockRepo)
        liveBooks = viewModel.getBooks()
        clearInvocations(mockRepo)
    }

    @After
    fun cleanUp() {
        reset(mockRepo)
    }


    /*
    runBlocking {} to ensure that all running coroutines get a chance to complete before the test function returns.
     */
    @Test
    fun `getBooks Should Return Books From Repo`() = runBlocking {
        var booksCheck = liveBooks.value
        assertNotNull(booksCheck)
        return@runBlocking
    }

    @Test
    fun `addBooks should return updated live data`() = runBlocking {
        val image = 2
        val title = "Animal Farm"
        val author = "Orwell"
        val chapters: ArrayList<Chapter> = ArrayList()
        val newBook = BookInfo(image, title, author, chapters)
        val returnBookInfo = BookRecyclerViewAdapter.RecyclerBookInfo(image, author, title, 1)

        `when`(mockRepo.getBookInfo(org.mockito.kotlin.any())).thenReturn(
            MutableLiveData(
                mutableListOf<BookRecyclerViewAdapter.RecyclerBookInfo>(
                    BookRecyclerViewAdapter.RecyclerBookInfo(1, "Orwell", "1984", 1L),
                    returnBookInfo
                )
            )
        )


        viewModel.addBook(newBook)
        verify(mockRepo, times(1)).addBookInfo(org.mockito.kotlin.any(), org.mockito.kotlin.any())
        verify(mockRepo, times(1)).getBookInfo(org.mockito.kotlin.any())
        assertEquals(
            returnBookInfo,
            viewModel.getBooks().value?.get(1)
        )

        return@runBlocking
    }

}