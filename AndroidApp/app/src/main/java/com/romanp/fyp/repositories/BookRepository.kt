package com.romanp.fyp.repositories

import android.content.Context
import android.database.Cursor
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.romanp.fyp.R
import com.romanp.fyp.adapters.BookRecyclerViewAdapter
import com.romanp.fyp.database.BookDatabaseHelper
import com.romanp.fyp.models.book.BookInfo

/**
 * Singleton pattern
 */
class BookRepository {
    companion object {
        @Volatile
        private var instance: BookRepository? = null
        private var dataSet: ArrayList<BookRecyclerViewAdapter.RecyclerBookInfo> = ArrayList()

        fun getInstance(): BookRepository = instance ?: synchronized(this) {
            instance ?: BookRepository().also { instance = it }

        }

        private const val TAG = "BookRepository"

    }

    fun getBookInfo(context: Context, bookId: Long): BookInfo {
        val book = BookDatabaseHelper(context).getBook(bookId)
        if (book == null || book.image == -1) {
            Log.e(TAG, "Problem getting a book from repository")
            return throw Exception()
        }
        return book
    }


    fun getRecyclerBookInfoList(context: Context): MutableLiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>> {
        refreshBookInfo(context)

        val data = MutableLiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>>()
        data.value = dataSet
        return data
    }

    fun addBookInfo(context: Context, book: BookInfo): Long {
        val appDB: BookDatabaseHelper = BookDatabaseHelper(context)
        return appDB.addBook(book)
    }

    fun refreshBookInfo(context: Context) {
        dataSet.clear()
        // Get data from the database
        val myDB = BookDatabaseHelper(context)
        val cursor: Cursor? = myDB.getAllBooks()
        if (cursor == null || cursor.count == 0) {
            //No data
        } else {
            while (cursor.moveToNext()) {
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