package com.romanp.fyp.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.romanp.fyp.adapters.BookRecyclerViewAdapter
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.repositories.BookInfoRepository
import com.romanp.fyp.models.book.BookUtil.Companion.loadBook


class MainActivityViewModel(
    application: Application,
    private val repository: BookInfoRepository
) : AndroidViewModel(application) {

    companion object{
        private const val TAG = "MainActivityViewModel"
    }

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

    fun addBook(selectedFile: Uri?): Long {
        val book = loadSelectedBook( selectedFile)

        // TODO check the id
        val id = repository.addBookInfo(getApplication(), book)

        books.postValue(getBooks().value)


        return id
    }

    fun loadSelectedBook(selectedFile: Uri?): BookInfo {
       return loadBook(getApplication(), selectedFile)
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
