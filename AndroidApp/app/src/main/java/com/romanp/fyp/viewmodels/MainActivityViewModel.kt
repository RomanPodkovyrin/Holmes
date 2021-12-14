package com.romanp.fyp.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.romanp.fyp.R
import com.romanp.fyp.adapters.BookRecyclerViewAdapter
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.repositories.BookInfoRepository
import android.os.AsyncTask


class MainActivityViewModel(
    application: Application,
    private val repository: BookInfoRepository
) : AndroidViewModel(application) {

    //TODO: should it be MutableList or List
    var books: MutableLiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>> =
        MutableLiveData()
    var mIsUpdating: MutableLiveData<Boolean> = MutableLiveData()

    fun init() {
        if (books != null) {
            return
        }
        books = repository.getBookInfo(getApplication())
    }

    fun getBooks(): LiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>> {

        return repository.getBookInfo(getApplication())
    }

    fun addBook(book: BookInfo): Long {
        // TODO check the id
        val id = repository.addBookInfo(getApplication(), book)

        books.postValue(getBooks().value)


        return id
    }
}

class MainViewModelFactory(
    private val application: Application,
    private val repository: BookInfoRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainActivityViewModel(application, repository) as T
    }

}
