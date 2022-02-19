package com.romanp.fyp.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.romanp.fyp.R
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.models.book.Entity
import com.romanp.fyp.utils.InjectorUtils
import com.romanp.fyp.viewmodels.BookGraphActivityViewModel
import com.romanp.fyp.viewmodels.graph.GraphType


class BookGraphActivity : AppCompatActivity() {
    companion object {
        val gson = Gson()
        private const val TAG = "BookGraphActivity"
        const val BOOKID_GRAPH = "ID_OF_BOOK"
        const val GRAPH_TYPE = "GRAPH_TYPE"
    }

    private lateinit var viewModel: BookGraphActivityViewModel
    private lateinit var type: GraphType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_graph)
        //TODO: pass type of graph as an enum to display right graph
        val bookId = intent.getLongExtra(
            BOOKID_GRAPH,
            -1
        )

        val extras = intent.extras
        if ((extras != null) && (extras.containsKey(GRAPH_TYPE))) {
            type = extras.getSerializable(GRAPH_TYPE) as GraphType
        }


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
                when (type) {
                    GraphType.PIE_CHART -> loadPieChart(
                        viewModel.getCurrentBookInfo(),
                        viewModel.getCharacters()
                    )
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.d("WebView", consoleMessage.message() + "\n" + consoleMessage.lineNumber())
                return true
            }
        }

        webView.loadUrl(
            "file:///android_asset/" +
                    when (type) {
                        GraphType.PIE_CHART -> "html/pieChart.html"
                    }
        )
    }

    private fun initialiseViewModel(bookId: Long) {
        val factory = InjectorUtils.provideBookGraphActivityViewModelFactory(application, bookId)
        viewModel = ViewModelProvider(this, factory).get(BookGraphActivityViewModel::class.java)
    }


    fun loadPieChart(book: BookInfo, data: ArrayList<Entity>) {
        val chapterNumber = book.chapters.size

        val dataJson: String = gson.toJson(data).toString()
        val bookJson: String = gson.toJson(book).toString()

        // pass the call JavaScript
        findViewById<WebView>(R.id.WebViewGraph).loadUrl(
            "javascript:createButtons($bookJson,$chapterNumber, $dataJson)"
        )
    }
}
