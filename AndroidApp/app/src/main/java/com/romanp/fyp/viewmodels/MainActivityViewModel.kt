package com.romanp.fyp.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
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

    /**
     * @return id if successful or -1 if failed
     */
    fun addBook(selectedFile: Uri?): Long {
        var book: BookInfo?
        try {
           book = loadSelectedBook( selectedFile)
        } catch (e: Exception) {
            Log.e(TAG, "Error while loading the book $selectedFile")
            return -1
        }


        // TODO check the id
        val id = repository.addBookInfo(getApplication(), book)

        if(id < 0) Log.e(TAG, "Error while saving book '${book.title}' to database")

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
