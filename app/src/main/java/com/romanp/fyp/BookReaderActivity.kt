package com.romanp.fyp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import com.romanp.fyp.MainActivity.Companion.EXTRA_MESSAGE
import nl.siegmann.epublib.domain.Book



class BookReaderActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "BookReaderActivity"
    }
    var currentPage: Int = 0;

    var webViewBookContent :WebView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_reader)


        // Get the Intent
        val book :Book = intent.getSerializableExtra(EXTRA_MESSAGE) as Book

        val textView = findViewById<TextView>(R.id.textViewBookTitle).apply {
            text = book.title
        }
        webViewBookContent = findViewById<WebView>(R.id.webViewBookContent).apply {
            loadData(String(book.contents.get(currentPage).data), "text/html", "UTF-8")
        }

        val buttonNext = findViewById<Button>(R.id.buttonNext).apply {
            setOnClickListener {
                Log.i(TAG, "Next Button pressed")
                if (currentPage >= book.contents.size-1) {
                    Log.i(TAG,"Max page reached")
                    return@setOnClickListener
                }
                currentPage++
                updatePage(book, currentPage)

            }
        }
        val buttonBack = findViewById<Button>(R.id.buttonBack).apply {
            setOnClickListener {
                Log.i(TAG, "Back Button pressed")
                if (currentPage <= 0) {
                    Log.i(TAG,"Min page reached")
                    return@setOnClickListener
                }
                currentPage--
                updatePage(book, currentPage)

            }
        }
    }

    fun updatePage(book: Book, page: Int) {
        println("Contents ${String(book.contents.get(currentPage).data)}")
        webViewBookContent?.loadData(String(book.contents.get(currentPage).data), "text/html", "UTF-8")
    }
}