package com.romanp.fyp.repositories

import android.content.Context
import android.database.Cursor
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.romanp.fyp.R
import com.romanp.fyp.adapters.BookRecyclerViewAdapter
import com.romanp.fyp.database.BookDatabaseHelper
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.models.book.ProcessedBook

/**
 * Singleton pattern
 */
class BookRepository {
    companion object {
        @Volatile
        private var instance: BookRepository? = null
        private var dataSet: ArrayList<BookRecyclerViewAdapter.RecyclerBookInfo> = ArrayList()
        val data = MutableLiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>>()
        fun getInstance(): BookRepository = instance ?: synchronized(this) {
            instance ?: BookRepository().also { instance = it }

        }

        private const val TAG = "BookRepository"

    }

    fun getBookInfo(context: Context, bookId: Long): BookInfo {
        val book = BookDatabaseHelper(context).getBook(bookId)
        if (book == null) {
            Log.e(TAG, "Problem getting a book from repository")
            return throw Exception()
        }
        return book
    }


    fun getRecyclerBookInfoList(context: Context): MutableLiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>> {
        refreshBookInfo(context)


        data.value = dataSet
        return data
    }

    fun addBookInfo(context: Context, book: BookInfo): Long {
        val appDB: BookDatabaseHelper = BookDatabaseHelper(context)
        return appDB.addBook(book)
    }

    private fun refreshBookInfo(context: Context) {
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
                        image = R.drawable.ic_book_24,
                        author = cursor.getString(1),
                        title = cursor.getString(2),
                        id = cursor.getLong(0), //cursor.getColumnIndex("id")
                        processed = cursor.getInt(3) == 1
                    )
                )
            }
        }
    }

    /**
     * Updates book processed status to true and updates it's book object
     * @param processedBookInfo - Object to be updated
     * @return error code
     * 0: no issues
     * -1: error
     */
    fun updateBook(context: Context, id: Long, processedBookInfo: ProcessedBook): Int {
        try {
            val bookDBHelper = BookDatabaseHelper(context)
            val book = bookDBHelper.getBook(id)
                ?: throw java.lang.Exception("Issue when getting a book by id: $id")
            book.characters.addAll(processedBookInfo.chapters)
            book.locations.addAll(processedBookInfo.locations)
            if (bookDBHelper.updateBook(id, book, true) == 0) {
                throw java.lang.Exception("Issue when updating a book by id: $id")
            }
            return 0
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        return -1
    }

}