package com.romanp.fyp.repositories

import android.content.Context
import android.database.Cursor
import androidx.lifecycle.MutableLiveData
import com.romanp.fyp.R
import com.romanp.fyp.adapters.BookRecyclerViewAdapter
import com.romanp.fyp.database.BookDatabaseHelper

/**
 * Singleton pattern
 */
class BookInfoRepository {
    companion object {
        private lateinit var instance: BookInfoRepository
        private var dataSet: ArrayList<BookRecyclerViewAdapter.RecyclerBookInfo> = ArrayList()

        fun getInstance() :BookInfoRepository{
            if (instance == null){
                instance = BookInfoRepository()
            }
            return instance
        }

    }

    fun getBookInfo(context: Context) : MutableLiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>>{
        setBookInfo(context)

        val data = MutableLiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>>()
        data.value = dataSet
        return data
    }

    private fun setBookInfo(context: Context) {
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