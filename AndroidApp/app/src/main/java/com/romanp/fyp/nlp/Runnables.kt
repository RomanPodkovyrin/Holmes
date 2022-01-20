package com.romanp.fyp.nlp

import android.content.Context
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import com.romanp.fyp.adapters.BookRecyclerViewAdapter

class Runnables {
    internal class PingNLPAPIRunnable(
        private val context: Context,
        private var mainHandler: Handler,
        private var serviceStatus: MutableLiveData<Boolean> = MutableLiveData()
    ) : Runnable {
        override fun run() {
            CoreNlpAPI.pingServer(context, serviceStatus)

            mainHandler.postDelayed(this, 10000)
        }
    }

    internal class CheckNLPAPIRunnable(
        private val context: Context,
        private var mainHandler: Handler,
        private var books: MutableLiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>> =
            MutableLiveData()
    ) : Runnable {
        override fun run() {
            println("Book ${books.value.toString()}")
            books.value?.filterNot { it.processed }?.forEach { it ->
                print("Check $it")
                //TODO: now how do i update the repository if have the data
                CoreNlpAPI.checkBook(context, it.title, it.author, it.id)
                // update if there is
//                books.postValue(books.value!!.filter { x-> x.id == it.id}.map {it}.toMutableList())
            }

            mainHandler.postDelayed(this, 10000)
        }
    }
}