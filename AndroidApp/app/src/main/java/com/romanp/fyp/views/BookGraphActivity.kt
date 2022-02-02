package com.romanp.fyp.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.romanp.fyp.R
import com.romanp.fyp.models.book.Entity
import com.romanp.fyp.utils.InjectorUtils
import com.romanp.fyp.viewmodels.BookGraphActivityViewModel


class BookGraphActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "BookGraphActivity"
    }

    private lateinit var viewModel: BookGraphActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_graph)
        //TODO: pass type of graph as an enum to display right graph
        val bookId = intent.getLongExtra(
            "ID",
            -1
        )
        initialiseViewModel(bookId)

        setUpWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        val webView = findViewById<WebView>(R.id.WebViewGraph)
        val webSettings = webView.settings
        webSettings.blockNetworkLoads = true
//        webSettings.forceDark = WebSettings.FORCE_DARK_ON
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
                loadPieChart(viewModel.getCharacters())
            }
        }

        // note the mapping from  file:///android_asset
        webView.loadUrl(
            "file:///android_asset/" +
                    "html/pieChart.html"
        )
    }

    private fun initialiseViewModel(bookId: Long) {
        val factory = InjectorUtils.provideBookGraphActivityViewModelFactory(application, bookId)
        viewModel = ViewModelProvider(this, factory).get(BookGraphActivityViewModel::class.java)
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
