package com.server.repository

import com.server.models.BookData
import com.server.plugins.BookInfo
import org.bson.conversions.Bson
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

class DataBaseRepository
    (connectionString: String, databaseName: String) {

    private val database: CoroutineDatabase
    private val processedBookCollection: CoroutineCollection<BookData>
    private val failedBookCollection: CoroutineCollection<BookInfo>


    init {
        // Setup KMongo DB
        val client = KMongo.createClient(connectionString).coroutine
        database = client.getDatabase(databaseName)
        processedBookCollection = database.getCollection("processedBooks")
        failedBookCollection = database.getCollection("failedBooks")
    }

    suspend fun find(bson: Bson, bson1: Bson): List<BookData> {
        return processedBookCollection.find(bson, bson1).toList()
    }

    suspend fun insertOne(processedBook: BookData) {
        processedBookCollection.insertOne(processedBook)
    }

    suspend fun findFailed(bson: Bson, bson1: Bson): List<BookInfo> {
        return failedBookCollection.find(bson, bson1).toList()
    }

    suspend fun insertOneFailed(processedBook: BookInfo) {
        failedBookCollection.insertOne(processedBook)
    }
}