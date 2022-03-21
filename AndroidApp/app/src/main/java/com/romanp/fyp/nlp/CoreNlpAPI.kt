package com.romanp.fyp.nlp

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.romanp.fyp.adapters.BookRecyclerViewAdapter
import com.romanp.fyp.database.BookDatabaseHelper.Companion.gson
import com.romanp.fyp.models.book.BookData
import com.romanp.fyp.repositories.BookRepository
import com.romanp.fyp.utils.ToastUtils
import java.nio.charset.Charset


class CoreNlpAPI {


    companion object {
        private const val secret =
            "k6qKl&YBBeflmieT47BBA5^&*nD&DueoZb0sjNRAR7XVNec!Oib5MpPJ43kxW5IYiF!Xvo3ZOEBegT8L7B*xq0sTlbfEo"

//        private const val url = "https://95.179.198.203:8443/" //online server
//        private const val url = "https://192.168.129.26:8443/" //connected android device (find with ip addr)
        private const val url = "https://10.0.2.2:8443/" //localhost from emulator

        private const val TAG = "CoreNLPAPI"

        private var requestQueue: RequestQueue? = null

        private fun getRequestQueue(applicationContext: Context): RequestQueue {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(applicationContext)
            }
            return requestQueue as RequestQueue
        }

        fun pingServer(applicationContext: Context, serviceStatus: MutableLiveData<Boolean>) {
            val queue = getRequestQueue(applicationContext)
            val url = "$url/$secret"

            // Request a string response from the provided URL.
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->

                    when (response) {
                        ServerResponse.PING.message -> {
                            setServiceStatus(serviceStatus, true)
                            Log.d(TAG, "Got ping back with message: $response")
                        }
                        else -> setServiceStatus(serviceStatus, false)
                    }

                },
                {
                    setServiceStatus(serviceStatus, false)
                    Log.e(TAG, "Failed when calling or waiting for response from $url: $it")
                })
            //TODO: set timeout

            // Add the request to the RequestQueue.
            queue.add(stringRequest)
        }

        private fun setServiceStatus(serviceStatus: MutableLiveData<Boolean>, status: Boolean) {
            when (status) {
                true -> {
                    if ((serviceStatus.value == false || serviceStatus.value == null)
                    ) {
                        serviceStatus.postValue(true)
                    }
                }
                false -> {
                    if (serviceStatus.value == true) serviceStatus.postValue(false)

                }
            }
        }

        fun nerTagger(
            applicationContext: Context,
            text: String,
            title: String,
            author: String
        ) {
//            return withContext(Dispatchers.IO){}

            Log.i(TAG, "Contacting NER Tagger")

            // Instantiate the RequestQueue.
            val queue = getRequestQueue(applicationContext)
            val url = "$url/process-book/$title/$author/$secret"

            val stringReq: StringRequest =
                object : StringRequest(Method.POST, url,
                    Response.Listener { response ->
                        when (response) {
                            ServerResponse.RECEIVED.message -> {
                                Log.i(TAG, "Received")
                                ToastUtils.toast(applicationContext, "Being Processed")
                            }
                            ServerResponse.ALREADY_PROCESSED.message -> {
                                Log.i(
                                    TAG,
                                    "Already Processed"
                                )
                                // No actual difference between received and already processed
                                ToastUtils.toast(applicationContext, "Already Processed")
                            }
                        }
                        Log.d(TAG, "$title: $response")
                    },
                    Response.ErrorListener { error ->

                        Log.e(
                            TAG,
                            "failed when calling or waiting for response from $url: \n $error"
                        )


                    }
                ) {
                    override fun getBody(): ByteArray {
                        return text.toByteArray(Charset.defaultCharset())
                    }
                }


            // Add the request to the RequestQueue.
            stringReq.retryPolicy = object : RetryPolicy {
                override fun getCurrentTimeout(): Int {
                    return 120000
                }

                override fun getCurrentRetryCount(): Int {
                    return 3
                }

                @Throws(VolleyError::class)
                override fun retry(error: VolleyError) {
                    Log.e(TAG, "TimeoutError: Retrying?")
                }
            }
            queue.add(stringReq)

        }

        fun checkBook(
            applicationContext: Context,
            title: String,
            author: String,
            id: Long,
            books: MutableLiveData<MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>>
        ) {
            val bookRepository = BookRepository.getInstance()

            Log.i(TAG, "Checking book $title $author")

            // Instantiate the RequestQueue.
            val queue = getRequestQueue(applicationContext)
            val url = "$url/check-book/$title/$author/$secret"

            val stringReq: StringRequest =
                object : StringRequest(Method.GET, url,
                    Response.Listener { response ->
                        Log.d(TAG, "Response is: $response")
                        if (response == ServerResponse.DOES_NOT_EXIST.message) {
                            return@Listener
                        }

                        var processed =
                            BookRecyclerViewAdapter.ProcessedState.SUCCESSFULLY_PROCESSED
                        when (response) {
                            ServerResponse.DOES_NOT_EXIST.message -> return@Listener
                            ServerResponse.FAILED.message -> {

                                if (response == ServerResponse.FAILED.message) {
                                    Log.i(TAG, "Failed to process the book $title - $author")
                                    processed = BookRecyclerViewAdapter.ProcessedState.FAILED
                                    bookRepository.updateBookProcessedStatus(
                                        applicationContext,
                                        id,
                                        processed
                                    )
                                }
                            }
                            else -> {
                                val bookDataInfo: BookData =
                                    gson.fromJson(response, BookData::class.java)
                                bookRepository.updateBook(
                                    applicationContext,
                                    id,
                                    bookDataInfo,
                                    processed
                                )
                            }
                        }


                        // Update live data
                        books.postValue(
                            books.value!!
                                .map { bookInfo ->
                                    if (bookInfo.id == id) {
                                        BookRecyclerViewAdapter.RecyclerBookInfo(
                                            bookInfo.image,
                                            bookInfo.author,
                                            bookInfo.title,
                                            bookInfo.id,
                                            processed
                                        )
                                    } else {
                                        bookInfo
                                    }

                                }.toMutableList()
                        )

                        Log.d(TAG, "Book $id: $title processed ")


                    },
                    Response.ErrorListener { error ->

                        Log.e(
                            TAG,
                            "failed when calling or waiting for response from $url: \n $error"
                        )


                    }
                ) {
                }


            // Add the request to the RequestQueue.
            stringReq.retryPolicy = object : RetryPolicy {
                override fun getCurrentTimeout(): Int {
                    return 120000
                }

                override fun getCurrentRetryCount(): Int {
                    return 3
                }

                @Throws(VolleyError::class)
                override fun retry(error: VolleyError) {
                    Log.e(TAG, "TimeoutError: Retrying?")
                }
            }
            queue.add(stringReq)

        }
    }
}
