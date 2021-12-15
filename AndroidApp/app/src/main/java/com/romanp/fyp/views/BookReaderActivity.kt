package com.romanp.fyp.views

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.romanp.fyp.R
import com.romanp.fyp.database.BookDatabaseHelper
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.models.book.Chapter
import nl.siegmann.epublib.domain.Book as EpubBook
import nl.siegmann.epublib.epub.EpubReader
import opennlp.tools.tokenize.SimpleTokenizer
import java.io.InputStream


class BookReaderActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "BookReaderActivity"
        val tokenizer: SimpleTokenizer = SimpleTokenizer.INSTANCE
    }

    private var currentPage: Int = 0

    private var webViewBookContent: WebView? = null
    private var textView: TextView? = null
    private var bookTitle: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "Opened Book Reader Activity")
        setContentView(R.layout.activity_book_reader)

        Log.i(TAG, "check : ${intent.hasExtra("Book")}")
        val bookId = intent.getLongExtra("Book", -1) //TODO: do i need this default?
        val myBookInfo: BookInfo? =
            BookDatabaseHelper(applicationContext).getBook(bookId)//intent.getSerializableExtra("Book") as? BookInfo
        if (myBookInfo == null || myBookInfo.image == -1) {
            Toast.makeText(
                applicationContext,
                "There was an error loading your book",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        val chapters: ArrayList<Chapter> = myBookInfo.chapters
        bookTitle = myBookInfo.title
        textView = findViewById<TextView>(R.id.textViewBookTitle).apply {
            text = bookTitle
        }
        webViewBookContent = findViewById<WebView>(R.id.webViewBookContent).apply {
            loadData(chapters[currentPage].text, "text/html", "UTF-8")
        }

        findViewById<Button>(R.id.buttonNext).apply {
            setOnClickListener {
                Log.i(TAG, "Next Button pressed")
                if (currentPage >= myBookInfo.chapters.size - 1) {
                    Log.i(TAG, "Max page reached")
                    return@setOnClickListener
                }
                currentPage++
                updatePage(chapters, currentPage)

            }
        }
        findViewById<Button>(R.id.buttonBack).apply {
            setOnClickListener {
                Log.i(TAG, "Back Button pressed")
                if (currentPage <= 0) {
                    Log.i(TAG, "Min page reached")
                    return@setOnClickListener
                }
                currentPage--
                updatePage(chapters, currentPage)

            }
        }
    }

    fun updatePage(chapters: ArrayList<Chapter>, page: Int) {
        webViewBookContent?.loadData(chapters[page].text, "text/html", "UTF-8")
        textView = findViewById<TextView>(R.id.textViewBookTitle).apply {
            text = "$bookTitle | ${chapters[page].chapterTitle}"
        }
    }
}