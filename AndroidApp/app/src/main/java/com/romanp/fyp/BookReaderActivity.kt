package com.romanp.fyp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.romanp.fyp.book.Book
import com.romanp.fyp.book.BookUtil
import com.romanp.fyp.book.Chapter
import com.romanp.fyp.MainActivity.Companion.EXTRA_MESSAGE
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
        setContentView(R.layout.activity_book_reader)


        // Get the Intent
        val selectedFile: Uri? = Uri.parse(intent.getStringExtra(EXTRA_MESSAGE))

        Log.i(TAG, "Loading book")
        val inputStreamNameFinder: InputStream? = selectedFile?.let {
            contentResolver.openInputStream(it)
        }

        if (inputStreamNameFinder == null) {
            Toast.makeText(
                applicationContext,
                "There was an error loading your book",
                Toast.LENGTH_SHORT
            ).show()
            Log.e(TAG, "There was an error while loading book $selectedFile")
            finish()
            return
        }
        val epubReader: EpubReader = EpubReader()
        val book: EpubBook = epubReader.readEpub(inputStreamNameFinder)

        val myBook: Book = BookUtil.processEpub(book)
        val chapters: ArrayList<Chapter> = myBook.chapters
        bookTitle = book.title
        textView = findViewById<TextView>(R.id.textViewBookTitle).apply {
            text = bookTitle
        }
        webViewBookContent = findViewById<WebView>(R.id.webViewBookContent).apply {
            loadData(chapters[currentPage].text, "text/html", "UTF-8")
        }

        findViewById<Button>(R.id.buttonNext).apply {
            setOnClickListener {
                Log.i(TAG, "Next Button pressed")
                if (currentPage >= book.contents.size - 1) {
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

    private fun updatePage(chapters: ArrayList<Chapter>, page: Int) {
        webViewBookContent?.loadData(chapters[page].text, "text/html", "UTF-8")
        textView = findViewById<TextView>(R.id.textViewBookTitle).apply {
            text = "$bookTitle | ${chapters[page].chapterTitle}"
        }
    }
}