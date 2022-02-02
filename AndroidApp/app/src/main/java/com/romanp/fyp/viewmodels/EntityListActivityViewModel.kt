package com.romanp.fyp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.romanp.fyp.adapters.entityRecyclerView.EntityRecyclerViewAdapter
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.models.book.Entity
import com.romanp.fyp.models.book.getBookInfoErrorState
import com.romanp.fyp.repositories.BookRepository

class EntityListActivityViewModel : AndroidViewModel {

    companion object {
        private const val TAG = "EntityListActivityViewModel"
    }

    private lateinit var currentBook: BookInfo
    private var bookListType: Boolean

    private var repository: BookRepository

    /**
     * @param application
     * @param repository
     * @param bookId id of the book to be loaded from repository
     * @param listType - true for character, false for locations
     */
    constructor(
        application: Application,
        repository: BookRepository,
        bookId: Long,
        listType: Boolean
    ) : super(
        application
    ) {
        bookListType = listType
        this.repository = repository
        getBookInfo(bookId)
    }

    fun getCurrentBookInfo() = currentBook


    private fun getBookInfo(bookId: Long): BookInfo {
        currentBook = try {
            repository.getBookInfo(getApplication(), bookId)
        } catch (e: Exception) {
            return getBookInfoErrorState()
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

    fun listType(): Boolean {
        return bookListType
    }

    /**
     * Returns correct type of the list, either characters or locations
     */
    fun getCurrentList(): ArrayList<EntityRecyclerViewAdapter.RecyclerEntityInfo> {
        val recyclerList = when (listType()) {
            true -> getCharacters()
            false -> getLocations()
        }.map { entity -> EntityRecyclerViewAdapter.RecyclerEntityInfo(entity.name) }

        return ArrayList(recyclerList)
    }
}

class EntityListActivityViewModelFactory(
    private val application: Application,
    private val repository: BookRepository,
    private val bookId: Long,
    private val listType: Boolean
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EntityListActivityViewModel(application, repository, bookId, listType) as T
    }

}