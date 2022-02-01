package com.romanp.fyp.views

import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.romanp.fyp.R
import com.romanp.fyp.models.book.Entity
import com.romanp.fyp.repositories.BookRepository
import com.romanp.fyp.utils.ToastUtils


class BookGraphActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "BookGraphActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_graph)
        //TODO: pass type of graph as an enum to fun the right function
        //TODO: make a view model
        val bookId = intent.getLongExtra(
            "ID",
            -1
        )
        val book = try {
            BookRepository.getInstance().getBookInfo(applicationContext, bookId)
        } catch (e: Exception) {
            ToastUtils.toast(applicationContext, "Error Loading Book Data")
            return
        }


        val dataset = book.characters
        val webView = findViewById<WebView>(R.id.WebViewGraph)
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.builtInZoomControls = true
        webSettings.setSupportZoom(true)
        webSettings.useWideViewPort = true
        webView.webChromeClient = WebChromeClient()
        webView.setInitialScale(1)

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(
                view: WebView,
                url: String
            ) {

                // after the HTML page loads,
                // load the pie chart
                loadPieChart(dataset)
            }
        }

        // note the mapping from  file:///android_asset
        webView.loadUrl(
            "file:///android_asset/" +
                    "html/pieChart.html"
        )
    }

    fun loadPieChart(data: ArrayList<Entity>) {
        Log.i(TAG, "Loading Pie Chart")
        val gson = Gson()
        // the array as text
        val text: String = gson.toJson(data).toString()

        // pass the JSON to the JavaScript function
        findViewById<WebView>(R.id.WebViewGraph).loadUrl(
            "javascript:loadPieChart($text)"
        )
    }
}
