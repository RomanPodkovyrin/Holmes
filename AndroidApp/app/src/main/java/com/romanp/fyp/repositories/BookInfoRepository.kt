package com.romanp.fyp.repositories

import android.content.Context
import android.database.Cursor
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.romanp.fyp.R
import com.romanp.fyp.adapters.BookRecyclerViewAdapter
import com.romanp.fyp.database.BookDatabaseHelper
import com.romanp.fyp.models.book.BookInfo

/**
 * Singleton pattern
 */
class BookInfoRepository {
    companion object {
        @Volatile
        private var instance: BookInfoRepository? = null
        private var dataSet: ArrayList<BookRecyclerViewAdapter.RecyclerBookInfo> = ArrayList()

        fun getInstance() :BookInfoRepository = instance ?: synchronized(this) {
            instance ?: BookInfoRepository().also { instance = it }

        }

    }

    fun getBookInfo(context: Context): MutableLiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>> {
        refreshBookInfo(context)

        val data = MutableLiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>>()
        data.value = dataSet
        println("dataset in repository $dataSet")
        return data
    }

    //TODO: implement
    fun addBookInfo(context: Context, book: BookInfo): Long {
        val appDB : BookDatabaseHelper = BookDatabaseHelper(context )
        val id = appDB.addBook(book)
        return id
    }

    fun refreshBookInfo(context: Context) {
        dataSet.clear()
        // Get data from the database
        // TODO: repeat in main remove
        val myDB = BookDatabaseHelper(context)
        val cursor: Cursor? = myDB.getAllBooks()
        if (cursor == null || cursor.count == 0) {
            //No data
        } else {
            while (cursor.moveToNext()) {
                println("cursor ${cursor.toString()}")
                dataSet.add(
                    BookRecyclerViewAdapter.RecyclerBookInfo(
                        R.drawable.ic_book_24,
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getLong(0),
                    )
                )
            }
        }
    }

}