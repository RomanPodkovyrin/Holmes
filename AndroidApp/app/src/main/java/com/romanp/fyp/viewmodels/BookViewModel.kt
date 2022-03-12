package com.romanp.fyp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.models.book.Entity
import com.romanp.fyp.models.book.getBookInfoErrorState
import com.romanp.fyp.repositories.BookRepository

abstract class BookViewModel(
    application: Application,
    protected var repository: BookRepository,
    protected val bookId: Long
) : AndroidViewModel(
    application
) {


    companion object {
        private const val TAG = "BookViewModel"
    }

    protected lateinit var currentBook: BookInfo

    fun getBookID() = bookId

    fun getCurrentBookInfo() = currentBook

    /**
     * @return bookInfo with error state if there was a problem getting it
     */
    protected fun getBookInfo(bookId: Long): BookInfo {
        currentBook = try {
            repository.getBookInfo(getApplication(), bookId)
        } catch (e: Exception) {
            Log.e(TAG, "Problem Loading Book with ID: $bookId")
            getBookInfoErrorState()
        }
        return currentBook
    }

    fun getCharacters(): ArrayList<Entity> {
        val book = getCurrentBookInfo()
        return book.characters
    }

    fun getLocations(): ArrayList<Entity> {
        val book = getCurrentBookInfo()
        return book.locations
    }
}