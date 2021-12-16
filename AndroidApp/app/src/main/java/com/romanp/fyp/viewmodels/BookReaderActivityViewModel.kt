package com.romanp.fyp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.romanp.fyp.models.book.AlreadyOnTheFirstPageException
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.models.book.Chapter
import com.romanp.fyp.models.book.NoMorePagesException
import com.romanp.fyp.repositories.BookRepository

class BookReaderActivityViewModel : AndroidViewModel {

    companion object {
        private const val TAG = "MainActivityViewModel"
    }

    private lateinit var repository: BookRepository

    /**
     * @param application
     * @param repository
     * @param bookId id of the book to be loaded from repository
     */
    constructor(application: Application, repository: BookRepository, bookId: Long) : super(
        application
    ) {
        this@BookReaderActivityViewModel.repository = repository
        getBookInfo(bookId)
    }

    private lateinit var currentBook: BookInfo

    private var currentPage: Int = 0

    fun getCurrentBookInfo() = currentBook

    private fun getBookInfo(bookId: Long): BookInfo {
        currentBook = repository.getBookInfo(getApplication(), bookId)

        return currentBook
    }

    /**
     * @return next chapter
     * @throws NoMorePagesException
     */
    fun nextButton(): Chapter {
        if (currentPage >= currentBook.chapters.size - 1) {
            Log.i(TAG, "Max page reached")
            throw NoMorePagesException("Reached the last page of the book")
        }
        currentPage++
        return getCurrentChapter()
    }

    /**
     * @return previous chapter
     * @throws AlreadyOnTheFirstPageException
     */
    fun backButton(): Chapter {
        if (currentPage <= 0) {
            Log.i(TAG, "Min page reached")
            throw AlreadyOnTheFirstPageException("There is not page before this one")
        }
        currentPage--
        return getCurrentChapter()
    }

    fun getCurrentChapter(): Chapter {
        return currentBook.chapters[currentPage]
    }


}

class BookReaderViewModelFactory(
    private val application: Application,
    private val repository: BookRepository,
    private val bookId: Long
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BookReaderActivityViewModel(application, repository, bookId) as T
    }

}
