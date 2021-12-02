package com.romanp.fyp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
//        val myBook: Book = intent.extras?.get(EXTRA_MESSAGE) as Book
//        Log.i(TAG, "Output: ${intent.getStringExtra(EXTRA_MESSAGE)}")
//        intent.extras?.getSerializable(EXTRA_MESSAGE)

        Log.i(TAG, "check : ${intent.hasExtra("Book")}")
        val myBookInfo: BookInfo? = intent.getSerializableExtra("Book") as? BookInfo
        if (myBookInfo == null) {
            Toast.makeText(
                applicationContext,
                "There was an error loading your book",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }
        // Get the Intent
//        val selectedFile: Uri? = Uri.parse(intent.getStringExtra(EXTRA_MESSAGE))
//        var book: EpubBook
//        try {
//           book = loadBook(selectedFile)
//        } catch (e: Exception){
//            Toast.makeText(
//                applicationContext,
//                "There was an error loading your book",
//                Toast.LENGTH_SHORT
//            ).show()
//            finish()
//            return
//        }


//        val myBook: Book = BookUtil.processEpub(book)

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

    @Throws(Exception::class)
    fun loadBook(selectedFile: Uri?): nl.siegmann.epublib.domain.Book {
        Log.i(TAG, "Loading book")
//        selectedFile.e
        val inputStreamNameFinder: InputStream? = selectedFile?.let {
            contentResolver.openInputStream(it)
        }

        if (inputStreamNameFinder == null) {
            Log.e(TAG, "There was an error while loading book $selectedFile")
            finish()
            throw Error()
        }
        val epubReader: EpubReader = EpubReader()
        val book: EpubBook = epubReader.readEpub(inputStreamNameFinder)

//        val myBook: Book = BookUtil.processEpub(book)
        return book
    }

    fun updatePage(chapters: ArrayList<Chapter>, page: Int) {
        webViewBookContent?.loadData(chapters[page].text, "text/html", "UTF-8")
        textView = findViewById<TextView>(R.id.textViewBookTitle).apply {
            text = "$bookTitle | ${chapters[page].chapterTitle}"
        }
    }
}