package com.romanp.fyp.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.romanp.fyp.models.book.*
import com.romanp.fyp.repositories.BookRepository
import com.romanp.fyp.views.EntityListActivity
import com.romanp.fyp.views.EntityType

class BookReaderActivityViewModel : AndroidViewModel {

    companion object {
        private const val TAG = "BookReaderActivityViewModel"
    }

    private var repository: BookRepository
    private val bookId: Long

    /**
     * @param application
     * @param repository
     * @param bookId id of the book to be loaded from repository
     */
    constructor(application: Application, repository: BookRepository, bookId: Long) : super(
        application
    ) {
        this@BookReaderActivityViewModel.repository = repository
        this@BookReaderActivityViewModel.bookId = bookId
        getBookInfo(bookId)
    }

    fun getBookID() = bookId

    private lateinit var currentBook: BookInfo

    private var currentPage: Int = 0

    fun getCurrentBookInfo() = currentBook

    /**
     * @return bookInfo with error state if there was a problem getting it
     */
    private fun getBookInfo(bookId: Long): BookInfo {
        currentBook = try {
            repository.getBookInfo(getApplication(), bookId)
        } catch (e: Exception) {
            Log.e(TAG, "Problem Loading Book with ID: $bookId")
            getBookInfoErrorState()
        }
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

    fun switchToEntityList(context: Context, entityType: EntityType) {
        val intent = Intent(context, EntityListActivity::class.java)
        intent.putExtra(EntityListActivity.EXTRA_MESSAGE, getBookID())
        intent.putExtra(EntityListActivity.EXTRA_MESSAGE_TYPE, entityType.message)
        context.startActivity(intent)
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
