package com.romanp.fyp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.romanp.fyp.adapters.entityRecyclerView.EntityRecyclerViewAdapter
import com.romanp.fyp.repositories.BookRepository

class EntityListActivityViewModel
/**
 * @param application
 * @param repository
 * @param bookId id of the book to be loaded from repository
 * @param listType - true for character, false for locations
 */(application: Application, repository: BookRepository, bookId: Long, listType: Boolean) :
    BookViewModel(
        application, repository, bookId
    ) {

    companion object {
        private const val TAG = "EntityListActivityViewModel"
    }

    private var bookListType: Boolean = listType


    init {
        getBookInfo(bookId)
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
        }.map { entity ->
            EntityRecyclerViewAdapter.RecyclerEntityInfo(
                getBookID(),
                entity.name,
                listType()
            )
        }
        Log.i(TAG, "Getting Current List for type ${listType()}")
        return ArrayList(recyclerList)
    }
}

@Suppress("UNCHECKED_CAST")
class EntityListActivityViewModelFactory(
    private val application: Application,
    private val repository: BookRepository,
    private val bookId: Long,
    private val listType: Boolean
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EntityListActivityViewModel(application, repository, bookId, listType) as T
    }

}