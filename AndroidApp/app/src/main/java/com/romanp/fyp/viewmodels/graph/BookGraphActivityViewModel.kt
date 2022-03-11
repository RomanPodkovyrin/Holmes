package com.romanp.fyp.viewmodels.graph

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.romanp.fyp.repositories.BookRepository
import com.romanp.fyp.viewmodels.BookViewModel

class BookGraphActivityViewModel(
    application: Application,
    repository: BookRepository,
    bookId: Long
) : BookViewModel(
    application, repository, bookId
) {


    companion object {
        private const val TAG = "BookGraphActivityViewModel"
    }


    init {
        getBookInfo(bookId)
    }
}

@Suppress("UNCHECKED_CAST")
class BookGraphActivityViewModelFactory(
    private val application: Application,
    private val repository: BookRepository,
    private val bookId: Long
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        BookGraphActivityViewModel(application, repository, bookId) as T

}
