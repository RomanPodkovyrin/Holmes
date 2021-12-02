package com.romanp.fyp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import com.romanp.fyp.models.book.BookInfo

class BookDatabaseHelper(
    context: Context
) : SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {


    val gson = Gson()


    companion object{
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "BookLibrary.db"
        private val TABLE_NAME = "book_library"
        private val COL_ID = "id"
        private val COL_AUTHOR = "author"
        private val COL_TITLE = "title"
        private val COL_DATA = "data"
        private val COL_IMAGE = "image"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY: String = (
                "CREATE TABLE $TABLE_NAME (" +
                        "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$COL_AUTHOR TEXT," +
                        "$COL_TITLE TEXT," +
                        "$COL_DATA BLOB" +
                        ")"
                )
        if (db != null) {
            db.execSQL(CREATE_TABLE_QUERY)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val UPDATE_DATABASE_QUERY: String = (
                "DROP TABLE IF EXISTS $TABLE_NAME"
                )
        if (db != null) {
            db.execSQL(UPDATE_DATABASE_QUERY)
        }
        onCreate(db)
    }

    //method to insert data
    fun addEmployee(book: BookInfo):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
//        contentValues.put(COL_ID, book.userId)
        contentValues.put(COL_AUTHOR, book.author)
        contentValues.put(COL_TITLE,book.title )
        contentValues.put(COL_DATA, gson.toJson(book))
        // Inserting Row
        val success = db.insert(TABLE_NAME, null, contentValues)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
//    //method to read data
//    fun viewEmployee():List<EmpModelClass>{
//        val empList:ArrayList<EmpModelClass> = ArrayList<EmpModelClass>()
//        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"
//        val db = this.readableDatabase
//        var cursor: Cursor? = null
//        try{
//            cursor = db.rawQuery(selectQuery, null)
//        }catch (e: SQLiteException) {
//            db.execSQL(selectQuery)
//            return ArrayList()
//        }
//        var userId: Int
//        var userName: String
//        var userEmail: String
//        if (cursor.moveToFirst()) {
//            do {
//                userId = cursor.getInt(cursor.getColumnIndex("id"))
//                userName = cursor.getString(cursor.getColumnIndex("name"))
//                userEmail = cursor.getString(cursor.getColumnIndex("email"))
//                val emp= EmpModelClass(userId = userId, userName = userName, userEmail = userEmail)
//                empList.add(emp)
//            } while (cursor.moveToNext())
//        }
//        return empList
//    }
//    //method to update data
//    fun updateEmployee(emp: EmpModelClass):Int{
//        val db = this.writableDatabase
//        val contentValues = ContentValues()
//        contentValues.put(KEY_ID, emp.userId)
//        contentValues.put(KEY_NAME, emp.userName) // EmpModelClass Name
//        contentValues.put(KEY_EMAIL,emp.userEmail ) // EmpModelClass Email
//
//        // Updating Row
//        val success = db.update(TABLE_CONTACTS, contentValues,"id="+emp.userId,null)
//        //2nd argument is String containing nullColumnHack
//        db.close() // Closing database connection
//        return success
//    }
//    //method to delete data
//    fun deleteEmployee(emp: EmpModelClass):Int{
//        val db = this.writableDatabase
//        val contentValues = ContentValues()
//        contentValues.put(KEY_ID, emp.userId) // EmpModelClass UserId
//        // Deleting Row
//        val success = db.delete(TABLE_CONTACTS,"id="+emp.userId,null)
//        //2nd argument is String containing nullColumnHack
//        db.close() // Closing database connection
//        return success
//    }
}
