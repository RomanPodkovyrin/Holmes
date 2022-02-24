package com.romanp.fyp.nlp

import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.romanp.fyp.adapters.BookRecyclerViewAdapter

class Runnables {
    companion object {
        private const val delay = 10000L //millis
    }

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

            mainHandler.postDelayed(this, delay)
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
            books.value?.filterNot { it.processed == BookRecyclerViewAdapter.ProcessedState.SUCCESSFULLY_PROCESSED || it.processed == BookRecyclerViewAdapter.ProcessedState.FAILED }
                ?.forEach { it ->
                    Log.i(TAG, "Checking $it")
                    CoreNlpAPI.checkBook(context, it.title, it.author, it.id, books)
                }

            mainHandler.postDelayed(this, delay)
        }
    }
}