package com.romanp.fyp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.romanp.fyp.Book.Book
import com.romanp.fyp.Book.BookUtil
import com.romanp.fyp.Book.Chapter
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
    var currentPage: Int = 0;

    var webViewBookContent :WebView? = null
    var textView: TextView? = null
    var bookTitle: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_reader)


        // Get the Intent
//        val book :Book = intent.getSerializableExtra(EXTRA_MESSAGE) as Book
        val selectedFile: Uri? = Uri.parse(intent.getStringExtra(EXTRA_MESSAGE))
//        Log.i(MainActivity.TAG, "File selected $selectedFile")//The uri with the location of the file
//        Log.i(MainActivity.TAG, "Loading book")

        val inputStreamNameFinder: InputStream? = selectedFile?.let {
            contentResolver.openInputStream(it)
        }

        if (inputStreamNameFinder == null) {
            Toast.makeText(applicationContext,"There was an error loading your book", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val epubReader: EpubReader = EpubReader()
        val book: EpubBook = epubReader.readEpub(inputStreamNameFinder)
//        book.guide.coverPage.data

        val myBook: Book = BookUtil.processEpub(book)
        val chapters: ArrayList<Chapter> = myBook.chapters
        bookTitle = book.title
        textView = findViewById<TextView>(R.id.textViewBookTitle).apply {
            text = bookTitle
        }
        webViewBookContent = findViewById<WebView>(R.id.webViewBookContent).apply {
            loadData(chapters[currentPage].text, "text/html", "UTF-8")
        }

        val buttonNext = findViewById<Button>(R.id.buttonNext).apply {
            setOnClickListener {
                Log.i(TAG, "Next Button pressed")
                if (currentPage >= book.contents.size-1) {
                    Log.i(TAG,"Max page reached")
                    return@setOnClickListener
                }
                currentPage++
                updatePage(chapters, currentPage)

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
                updatePage(chapters, currentPage)

            }
        }
    }

    fun updatePage(chapters: ArrayList<Chapter>, page: Int) {
        webViewBookContent?.loadData(chapters[page].text, "text/html", "UTF-8")
//        println(nlpUtil.posTagger(applicationContext,doc.body().text()))
        textView = findViewById<TextView>(R.id.textViewBookTitle).apply {
            text = "$bookTitle | ${chapters[page].chapterTitle}"
        }
    }
}