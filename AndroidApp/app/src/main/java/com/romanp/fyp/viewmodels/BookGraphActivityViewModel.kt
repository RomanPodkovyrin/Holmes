package com.romanp.fyp.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.romanp.fyp.repositories.BookRepository

class BookGraphActivityViewModel : BookViewModel {


    companion object {
        private const val TAG = "BookGraphActivityViewModel"
    }


    constructor(application: Application, repository: BookRepository, bookId: Long) : super(
        application, repository, bookId
    ) {
        getBookInfo(bookId)
    }
}

class BookGraphActivityViewModelFactory(
    private val application: Application,
    private val repository: BookRepository,
    private val bookId: Long
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookGraphActivityViewModel(application, repository, bookId) as T
    }

}
