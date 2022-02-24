package com.romanp.fyp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.romanp.fyp.adapters.BookRecyclerViewAdapter
import com.romanp.fyp.repositories.BookRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
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
    private val mockRepo = mock(BookRepository::class.java)

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Before
    fun setUp() {

        booksLiveData = MutableLiveData(
            mutableListOf(
                BookRecyclerViewAdapter.RecyclerBookInfo(1, "Orwell", "1984", 1L, 0)
            )
        )
        `when`(mockRepo.getRecyclerBookInfoList(org.mockito.kotlin.any())).thenReturn(booksLiveData)
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
    fun `book data should not be null`() = runBlocking {
        val booksCheck = liveBooks.value
        assertNotNull(booksCheck)
        return@runBlocking
    }

    @Test
    fun `test NLP Server Status`() = runBlocking {
        val nlpServerStatus = viewModel.getNLPServiceStatus()
        assertNull(nlpServerStatus.value)
        return@runBlocking
    }

//TODO: fix test
//    @Test
//    fun `addBooks should return updated live data`() = runBlocking {
//        val image = 2
//        val title = "Animal Farm"
//        val author = "Orwell"
//        val chapters: ArrayList<Chapter> = ArrayList()
//        val newBook = BookInfo(image, title, author, chapters)
//        val returnBookInfo = BookRecyclerViewAdapter.RecyclerBookInfo(image, author, title, 1)
//
//        `when`(mockRepo.getBookInfo(org.mockito.kotlin.any())).thenReturn(
//                    BookInfo(1, "Orwell", "1984", arrayListOf(), arrayListOf(), arrayListOf())
//                )
//            )
//        )
//        val partialMockViewModel = spy(viewModel)
////
//        doReturn(newBook).`when`(partialMockViewModel).loadSelectedBook(org.mockito.kotlin.any())
////        `when`(partialMockViewModel.loadSelectedBook(org.mockito.kotlin.any())).thenReturn(newBook)
////        `when`(partialMockViewModel.addBook(Uri.parse("/book_path"))).thenCallRealMethod()
////        `when`(partialMockViewModel.getBooks()).thenCallRealMethod()
//
//        partialMockViewModel.addBook(Uri.parse("/book_path"))
//        verify(mockRepo, times(1)).addBookInfo(org.mockito.kotlin.any(), org.mockito.kotlin.any())
//        verify(mockRepo, times(1)).getBookInfo(org.mockito.kotlin.any())
//        assertEquals(
//            returnBookInfo,
//            partialMockViewModel.getBooks().value?.get(1)
//        )
//
//        return@runBlocking
//    }

}