package com.romanp.fyp.nlp

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.romanp.fyp.database.BookDatabaseHelper.Companion.gson
import com.romanp.fyp.models.book.ProcessedBook
import com.romanp.fyp.repositories.BookRepository
import com.romanp.fyp.utils.ToastUtils
import java.nio.charset.Charset


class CoreNlpAPI {


    companion object {
        //TODO: make properties file to change it there
        //        private const val url = "http://108.61.173.161:8080/"
        private const val url = "http://10.0.2.2:8080/"


        private const val TAG = "CoreNLPAPI"
        fun pingServer(applicationContext: Context, serviceStatus: MutableLiveData<Boolean>) {
            val queue = Volley.newRequestQueue(applicationContext)


            // Request a string response from the provided URL.
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    serviceStatus.postValue(true)
                    Log.d(TAG, "Got ping back with message: $response")
                },
                {
                    serviceStatus.postValue(false)
                    Log.e(TAG, "Failed when calling or waiting for response from $url: $it")
                })
            //TODO: set timeout

            // Add the request to the RequestQueue.
            queue.add(stringRequest)
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
            val queue = Volley.newRequestQueue(applicationContext)
            val url = "$url/process-book/$title/$author"

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
                                    "Already relieved"
                                )
                                ToastUtils.toast(applicationContext, "Already Processed")
                            }
                        }
                        Log.d(TAG, "Response is: $response")
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
            id: Long
        ) {
            val bookRepository = BookRepository.getInstance()

            Log.i(TAG, "Checking book $title $author")

            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(applicationContext)
            val url = "$url/check-book/$title/$author"

            val stringReq: StringRequest =
                object : StringRequest(Method.GET, url,
                    Response.Listener { response ->
                        Log.d(TAG, "Response is: $response")
                        if (response == ServerResponse.DOES_NOT_EXIST.message) {
                            return@Listener
                        }
                        //TODO: if response == processing failed {send book again}


                        val processedBookInfo: ProcessedBook =
                            gson.fromJson(response, ProcessedBook::class.java)
                        bookRepository.updateBook(applicationContext, id, processedBookInfo)

                        Log.d(TAG, "Book $id: $title processed ")


                    },
                    Response.ErrorListener { error ->

                        Log.e(
                            TAG,
                            "failed when calling or waiting for response from $url: \n $error"
                        )


                    }
                ) {
//                    override fun getBody(): ByteArray {
//                        return text.toByteArray(Charset.defaultCharset())
//                    }
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
