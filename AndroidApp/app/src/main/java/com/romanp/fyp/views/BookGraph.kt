package com.romanp.fyp.views

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.romanp.fyp.R


class BookGraph : AppCompatActivity() {
    companion object {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_graph)


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
                loadPieChart()
            }
        }

        // note the mapping from  file:///android_asset
        webView.loadUrl(
            "file:///android_asset/" +
                    "html/piechart.html"
        )
    }

    fun loadPieChart() {
        println("Load pie")
        val dataset = intArrayOf(5, 10, 15, 20, 35)

        // the array as text
        val text: String = dataset.contentToString()

        // pass the array to the JavaScript function
        findViewById<WebView>(R.id.WebViewGraph).loadUrl(
            "javascript:loadPieChart($text)"
        )
    }
}
