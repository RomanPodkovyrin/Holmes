package com.romanp.fyp.nlp

import android.content.Context
import android.util.Log
import android.widget.TextView
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.nio.charset.Charset


class CoreNlpAPI {


    companion object {
        private const val url = "http://108.61.173.161:8080/"


        private const val TAG = "CoreNLPAPI"
        fun pingServer(applicationContext: Context, textView: TextView) {
            var message = ""
            val queue = Volley.newRequestQueue(applicationContext)


            // Request a string response from the provided URL.
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    message = "Response is: $response"
                    textView.text = message
                    Log.d(TAG, "Got ping back with message: $message")
                },
                {
                    message = "Failed to get anything"
                    Log.e(TAG, "failed when calling or waiting for response from $url: $it")
                })

            // Add the request to the RequestQueue.
            queue.add(stringRequest)
            Log.d(TAG, "exit")
        }


        fun nerTagger(applicationContext: Context, text: String, textView: TextView) {
            Log.i(TAG, "Starting NER Tagger")
            var names = ""

            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(applicationContext)
            val url = "$url/process-book/B/G"

            val stringReq: StringRequest =
                object : StringRequest(Method.POST, url,
                    Response.Listener { response ->
                        names = "Response is: $response"
                        textView.text = names
                        Log.d(TAG, names)
                    },
                    Response.ErrorListener { error ->

                        names = "Failed to get anything"
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
    }
}