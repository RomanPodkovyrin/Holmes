package com.romanp.fyp.views

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.romanp.fyp.R
import com.romanp.fyp.models.book.AlreadyOnTheFirstPageException
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.models.book.Chapter
import com.romanp.fyp.models.book.NoMorePagesException
import com.romanp.fyp.utils.InjectorUtils
import com.romanp.fyp.viewmodels.BookReaderActivityViewModel


class BookReaderActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "BookReaderActivity"
    }

    private lateinit var viewModel: BookReaderActivityViewModel


    private var webViewBookContent: WebView? = null
    private var textView: TextView? = null
    private var bookTitle: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bookId = intent.getLongExtra("Book", -1) //TODO: do i need this default?
        initialiseViewModel(bookId)

        Log.i(TAG, "Opened Book Reader Activity")
        setContentView(R.layout.activity_book_reader)


        val myBookInfo: BookInfo = viewModel.getCurrentBookInfo()
        if (myBookInfo.image == -1) {
            Toast.makeText(
                applicationContext,
                "There was an error loading your book",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        bookTitle = myBookInfo.title
        textView = findViewById<TextView>(R.id.textViewBookTitle).apply {
            text = bookTitle
        }
        webViewBookContent = findViewById<WebView>(R.id.webViewBookContent).apply {
            loadData(viewModel.getCurrentChapter().text, "text/html", "UTF-8")
        }

        findViewById<Button>(R.id.buttonNext).apply {
            setOnClickListener {
                Log.i(TAG, "Next Button pressed")
                try {
                    updatePage(viewModel.nextButton())
                } catch (e: NoMorePagesException) {
                    Toast.makeText(
                        applicationContext,
                        "No More Pages",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
        }
        findViewById<Button>(R.id.buttonBack).apply {
            setOnClickListener {
                Log.i(TAG, "Back Button pressed")
                try {
                    updatePage(viewModel.backButton())
                } catch (e: AlreadyOnTheFirstPageException) {

                }

            }
        }
    }

    private fun initialiseViewModel(bookId: Long) {
        val factory = InjectorUtils.provideBookReaderActivityViewModelFactor(application, bookId)
        viewModel = ViewModelProvider(this, factory).get(BookReaderActivityViewModel::class.java)
    }


    private fun updatePage(chapters: Chapter) {
        webViewBookContent?.loadData(chapters.text, "text/html", "UTF-8")
        textView = findViewById<TextView>(R.id.textViewBookTitle).apply {
            text = "$bookTitle | ${chapters.chapterTitle}"
        }
    }
}