package com.romanp.fyp.nlp

import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.romanp.fyp.adapters.BookRecyclerViewAdapter

class Runnables {
    internal class PingNLPAPIRunnable(
        private val context: Context,
        private var mainHandler: Handler,
        private var serviceStatus: MutableLiveData<Boolean> = MutableLiveData()
    ) : Runnable {

        companion object {
            private const val TAG = "PingNLPAPIRunnable"
        }

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
        companion object {
            private const val TAG = "CheckNLPAPIRunnable"
        }

        override fun run() {
            books.value?.filterNot { it.processed }?.forEach { it ->
                Log.i(TAG, "Checking $it")
                //TODO: now how do i update the repository if have the data
                CoreNlpAPI.checkBook(context, it.title, it.author, it.id, books)
            }

            mainHandler.postDelayed(this, 10000)
        }
    }
}