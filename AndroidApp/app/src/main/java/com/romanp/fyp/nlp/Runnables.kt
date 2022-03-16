package com.romanp.fyp.nlp

import android.app.ActivityManager
import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.romanp.fyp.adapters.BookRecyclerViewAdapter

class Runnables {
    companion object {
        private const val pingDelay = 10000L
        private const val delay = 10000L //millis
        fun isInBackground(): Boolean {
            val runningAppProcessInfo = ActivityManager.RunningAppProcessInfo()
            ActivityManager.getMyMemoryState(runningAppProcessInfo)
            return runningAppProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        }
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

            if (isInBackground()) {
                Log.i(TAG, "App running in Background, stopping ping runnable")
                //exitProcess(-1)
                return

            }

            mainHandler.postDelayed(this, pingDelay)
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

            if (isInBackground()) {
                Log.i(TAG, "App running in Background, stopping CheckNLPAPI runnable")
                return
            }

            mainHandler.postDelayed(this, delay)
        }
    }
}