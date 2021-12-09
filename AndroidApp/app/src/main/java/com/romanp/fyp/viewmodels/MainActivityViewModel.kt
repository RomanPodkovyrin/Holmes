package com.romanp.fyp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.romanp.fyp.adapters.BookRecyclerViewAdapter
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.repositories.BookInfoRepository

class MainActivityViewModel : ViewModel {
    var books: MutableLiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>> = MutableLiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>>()
    lateinit var repository: BookInfoRepository
    constructor() {
        if (books != null){
            return
        }
        repository = BookInfoRepository.getInstance()
        books = repository.getBookInfo()
    }

    fun getBooks() : LiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>>{

        return books
    }
}

class MainViewModelFactory(): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainActivityViewModel::class.java)){
            return MainActivityViewModel() as T
        }
        throw IllegalArgumentException ("UnknownViewModel")
    }

}
