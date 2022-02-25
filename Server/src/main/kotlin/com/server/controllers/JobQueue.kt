package com.server.controllers

import com.server.plugins.BookInfo
import kotlinx.coroutines.*
import java.util.concurrent.LinkedBlockingQueue

class JobQueue
{

//    @OptIn(DelicateCoroutinesApi::class)
    fun start() {
        var bookInfo = BookInfo(1,"","", arrayListOf(), arrayListOf(), arrayListOf())
        val queue = LinkedBlockingQueue<BookInfo>()//ConcurrentLinkedQueue<BookInfo>()
        queue.add(bookInfo)
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                queue.take()
            }
//            println("${++i}: ${Thread.currentThread().name}")
        }
//        withContext(Dispatchers.IO) {
//            queue.take()
//        }
//        withContext(Dispatchers.IO) {
//            queue.put(bookInfo)
//        }
    }

}