package com.romanp.fyp.views

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.*
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
    private lateinit var chapterSpinner: Spinner
    private lateinit var topLinksPercentageSeekBar: SeekBar
    private lateinit var topCharactersByMentionsSeekBar: SeekBar
    private lateinit var maxLinkValueTV: TextView
    private lateinit var maxMentionValueTV: TextView

    // Common
    private var selectedChapter: Int = 0

    // Network values
    private var topLinksPercentageValue: Float = 1F
    private var topCharacterByMentionsValue: Float = 1F


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

        topLinksPercentageSeekBar = findViewById(R.id.topLinksPercentageSeekBar)
        topCharactersByMentionsSeekBar = findViewById(R.id.topCharactersByMentionsSeekBar)
        maxLinkValueTV = findViewById(R.id.topLinksPercentageValueTV)
        maxMentionValueTV = findViewById(R.id.topCharactersByMentionsValueTV)

        initialiseViewModel(bookId)
        setUpWebView()

        setupControls()

    }

    private fun setupControls() {
        // Hide if Not network graph
        if (type != GraphType.CHARACTER_NETWORK) {
            topLinksPercentageSeekBar.visibility = View.GONE
            topCharactersByMentionsSeekBar.visibility = View.GONE
            maxLinkValueTV.visibility = View.GONE
            maxMentionValueTV.visibility = View.GONE
            findViewById<TextView>(R.id.topLinksPercentageTV).visibility = View.GONE
            findViewById<TextView>(R.id.topCharactersByMentionsTV).visibility = View.GONE
            findViewById<Spinner>(R.id.chapterSpinner).visibility= View.GONE
            findViewById<Spinner>(R.id.chapterSpinner2).visibility= View.GONE
            findViewById<TextView>(R.id.distanceMethodLabel).visibility= View.GONE
            return
        }
        setupChapterSpinner()
        setupNetworkControls()
    }

    private fun setupNetworkControls() {


        topLinksPercentageSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                maxLinkValueTV.text = "$progress ${getString(R.string.percentage)}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                topLinksPercentageValue = seekBar.progress / 100f
                Log.d(TAG, "topLinksPercentageSeekBar: Value selected ${seekBar.progress}")
                updateGraph()
            }

        })



        topCharactersByMentionsSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                maxMentionValueTV.text = "$progress ${getString(R.string.percentage)}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                topCharacterByMentionsValue = seekBar.progress / 100f
                Log.d(TAG, "topCharactersByMentionsSeekBar: Value selected ${seekBar.progress}")
                updateGraph()
            }

        })
    }

    private fun setupChapterSpinner() {
        // Setting up chapter spinner
        val chapterTitlesArray = viewModel.getCurrentBookInfo().chapters.map { it.chapterTitle }
        chapterSpinner = findViewById(R.id.chapterSpinner)
        val spinnerArrayAdapter = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_dropdown_item, chapterTitlesArray
        )
        chapterSpinner.adapter = spinnerArrayAdapter
        chapterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.i(TAG, "ChapterSpinner: Nothing selected")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.i(TAG, "ChapterSpinner: Clicked item $position")
                selectedChapter = position
                updateGraph()
            }
        }
    }

    private fun updateGraph() {
        when (type) {
            GraphType.PIE_CHART -> {}
            GraphType.CHARACTER_NETWORK -> {
                loadNetworkChartDirectly(
                    viewModel.getCurrentBookInfo(),
                    selectedChapter,
                    topLinksPercentageValue,
                    topCharacterByMentionsValue
                )
            }
        }
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
                    GraphType.PIE_CHART -> {
                        loadPieChart(viewModel.getCurrentBookInfo(), viewModel.getCharacters())

                    }
                    GraphType.CHARACTER_NETWORK -> {
                        loadNetworkChart(viewModel.getCurrentBookInfo())
                    }
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.d(
                    "WebView",
                    "Line ${consoleMessage.lineNumber()}: ${consoleMessage.message()} \n" +
                            "Source ID: ${consoleMessage.sourceId()}\n" +
                            "Message Level: ${consoleMessage.messageLevel()}"
                )
                return true
            }
        }

        webView.loadUrl(
            "file:///android_asset/" + "html/visualisation.html"
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

    private fun loadNetworkChartDirectly(
        book: BookInfo,
        chapter: Int,
        maxLink: Float,
        maxMention: Float
    ) {
        val chapterNumber = book.chapters.size
        val distances = book.characterDistanceByChapter
        val distancesJson: String = gson.toJson(distances).toString()
        val bookJson: String = gson.toJson(book.characters).toString()
        Log.i(TAG, "book: $bookJson \nchapters: $chapterNumber\ndistances: $distancesJson")
        findViewById<WebView>(R.id.WebViewGraph).loadUrl(
            "javascript:plotNetwork($chapter, $distancesJson, $bookJson, $maxLink, $maxMention)"
        )
    }


    fun loadNetworkChart(book: BookInfo) {
        Log.i(TAG, "Loading network chart")
        val chapterNumber = book.chapters.size
        val distances = book.characterDistanceByChapter
        val distancesJson: String = gson.toJson(distances).toString()
        val bookJson: String = gson.toJson(book.characters).toString()
        Log.i(TAG, "book: $bookJson \nchapters: $chapterNumber\ndistances: $distancesJson")
        findViewById<WebView>(R.id.WebViewGraph).loadUrl(
            "javascript:makeNetwork($bookJson, $chapterNumber, $distancesJson)"
        )
    }
}
