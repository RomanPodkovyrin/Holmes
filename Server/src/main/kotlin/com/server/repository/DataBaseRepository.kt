package com.server.repository

import com.server.models.ProcessedBook
import org.bson.conversions.Bson
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

class DataBaseRepository {

    private val database: CoroutineDatabase
    private val collection: CoroutineCollection<ProcessedBook>


    constructor(connectionString: String, databaseName:String){
        // Setup KMongo DB
        val client = KMongo.createClient(connectionString).coroutine
        database = client.getDatabase(databaseName)
        collection = database.getCollection<ProcessedBook>()
    }

    suspend fun find(bson: Bson, bson1: Bson): List<ProcessedBook> {
        return collection.find(bson,bson1).toList()
    }

    suspend fun insertOne(processedBook: ProcessedBook) {
        collection.insertOne(processedBook)
    }

}