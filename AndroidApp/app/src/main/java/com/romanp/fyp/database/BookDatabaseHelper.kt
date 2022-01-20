package com.romanp.fyp.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteBlobTooBigException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.romanp.fyp.models.book.BookInfo
import java.util.*


class BookDatabaseHelper(
    private val context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    companion object {
        val gson = Gson()
        private const val TAG = "BookDatabaseHelper"
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "BookLibrary.db"
        private const val TABLE_NAME = "book_library"
        private const val COL_ID = "id"
        private const val COL_AUTHOR = "author"
        private const val COL_TITLE = "title"
        private const val COL_DATA = "data"
        private const val COL_PROCESSED = "processed"
//        private const val COL_IMAGE = "image"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.i(TAG, "Database Scheme created")
        val CREATE_TABLE_QUERY: String = (
                "CREATE TABLE $TABLE_NAME (" +
                        "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$COL_AUTHOR TEXT," +
                        "$COL_TITLE TEXT," +
                        "$COL_DATA BLOB," +
                        "$COL_PROCESSED BOOLEAN NOT NULL CHECK ($COL_PROCESSED IN (0,1))," +
                        "UNIQUE($COL_AUTHOR, $COL_TITLE)" +
                        ")"
                )
        if (db != null) {
            db.execSQL(CREATE_TABLE_QUERY)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.i(TAG, "Database is upgraded from version $oldVersion to version $newVersion")
        val UPDATE_DATABASE_QUERY: String = (
                "DROP TABLE IF EXISTS $TABLE_NAME"
                )
        if (db != null) {
            db.execSQL(UPDATE_DATABASE_QUERY)
        }
        onCreate(db)
    }

    /**
     * @return -1 if an error occurred or the Id of the row inserted
     */
    fun addBook(book: BookInfo, processed: Boolean = false): Long {
        Log.i(TAG, "Adding a new book row '${book.title}'")
        val db: SQLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_AUTHOR, book.author)
        contentValues.put(COL_TITLE, book.title)
        contentValues.put(COL_DATA, gson.toJson(book))
        contentValues.put(COL_PROCESSED, processed)

        // Inserting Row
        var success = -1L
        try {
            success = db.insert(TABLE_NAME, null, contentValues)
        } catch (e: Exception) {
            Log.e(TAG, "Error :$e")
            return success
        }
        //2nd argument is String containing nullColumnHack
        if (success < 0) {
            Log.e(TAG, "Failed to add '${book.title}' to the db")
        } else {
            Log.i(TAG, "Added '${book.title}' at id $success")
        }

        db.close() // Closing database connection
        return success
    }

    fun getBook(id: Long): BookInfo {
        val query = "SELECT data FROM $TABLE_NAME WHERE id == $id"
        val db = writableDatabase
        var cursor: Cursor? = null
        if (db != null) {
            //TODO: sanitise SQL
            cursor = db.rawQuery(query, null)
        }
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val blob = cursor.getBlob(0)
                    val temp = blob.filter { e -> e != null }.toByteArray()
//                println(blob.toList().subList(0, blob.size - 1).reversed())
//                println("Getting ${String(blob).dropLast(1).reversed().filter { e -> e != null }}")
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    //TODO: figureout why null is being added at the end
                    val book: BookInfo =
                        gson.fromJson(String(blob).dropLast(1), BookInfo::class.java)
                    Log.i(TAG, "Success")
                    return book
                }
            }
//        println(cursor)
        } catch (e: SQLiteBlobTooBigException)     {
            //TODO: This error sometimes happens when the app is left running for too long
            Log.e(TAG, e.toString())
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        //TODO: use -1 to check for error
        return BookInfo(-1, "", "", ArrayList(), ArrayList(), ArrayList())
    }

    fun getAllBooks(): Cursor? {
        val query = "SELECT $COL_ID, $COL_AUTHOR, $COL_TITLE, $COL_PROCESSED FROM $TABLE_NAME"
        val db = writableDatabase
        var cursor: Cursor? = null
        if (db != null) {
            cursor = db.rawQuery(query, null)
        }
        return cursor
    }

    fun deleteBook(id: Long): Int {
        val db: SQLiteDatabase = writableDatabase
        val output = db.delete(TABLE_NAME, "$COL_ID=?", arrayOf(id.toString()))
        db.close()
        if (output == 0) {
            Log.e(TAG, "Failed to delete book with id=$id")
        }
        return output
    }

    fun updateBook(id: Long, book: BookInfo, processed: Boolean): Int {
        val contentValues = ContentValues()
        contentValues.put(COL_AUTHOR, book.author)
        contentValues.put(COL_TITLE, book.title)
        contentValues.put(COL_DATA, gson.toJson(book))
        contentValues.put(COL_PROCESSED, processed)

        val db: SQLiteDatabase = writableDatabase
        val output = db.update(TABLE_NAME, contentValues, "$COL_ID=?", arrayOf(id.toString()))
        db.close()
        if (output == 0) {
            Log.e(TAG, "Failed to update book $id")
        } else {
            Log.i(TAG, "Updated book $id")
        }
        return output
    }

}
