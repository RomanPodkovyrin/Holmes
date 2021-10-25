package com.romanp.fyp

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.romanp.fyp.nlp.nlpUtil
import java.io.InputStream
import nl.siegmann.epublib.*
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }
//    val editText;
//    val textOutPut;
//    val buttonExtract;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



//        println
         setContentView(R.layout.activity_main)


        // Finding layout views by id
        val editText = findViewById<EditText>(R.id.editTextForNER)
        val textOutPut = findViewById<TextView>(R.id.textViewExtractedNames)
        val buttonExtract = findViewById<Button>(R.id.buttonExtractNames)
        val buttonLoadBook = findViewById<Button>(R.id.loadBookButton)
        val textBookTitle = findViewById<TextView>(R.id.book_title)

        buttonLoadBook.setOnClickListener {
            Log.i(TAG, "Loading book")
            val inputStreamNameFinder: InputStream = applicationContext.assets.open("ShortStory.epub")
            val epubReader: EpubReader = EpubReader()
            val book: Book = epubReader.readEpub(inputStreamNameFinder)
            Log.i(TAG, " book title ${book.title} ")
            textBookTitle.text = book.title
            findViewById<WebView>(R.id.web1).loadData(String(book.contents.get(6).data), "text/html", "UTF-8")

//                book.t

        }

        buttonExtract.setOnClickListener {
//            Thread {
//            }.start()
            Log.i(TAG,"Extract Button clicked")
            Log.i(TAG, editText.text.toString())


            try {
                Thread {
                    textOutPut.text = nlpUtil.nerTagger(applicationContext,editText.text.toString()).toString()
                }.start()

            } catch (e: Exception){
                Log.e(TAG, "Error extracting names: $e")
                Toast.makeText(getApplicationContext(),"There was an error extracting names",Toast.LENGTH_SHORT).show();
            }

        }

    }

}