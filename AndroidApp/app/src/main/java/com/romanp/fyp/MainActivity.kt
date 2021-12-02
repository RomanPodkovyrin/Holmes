package com.romanp.fyp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romanp.fyp.adapters.CustomAdapter
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.models.book.BookUtil
import nl.siegmann.epublib.domain.Book as EpubBook
import nl.siegmann.epublib.epub.EpubReader
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    private lateinit var adapter: CustomAdapter
    // ArrayList of class ItemsViewModel
    private val data = ArrayList<BookInfo>()

    companion object {
        private const val TAG = "MainActivity"
        const val EXTRA_MESSAGE = "com.example.MainActivity.book"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // recycler view
        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerViewBooks)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)



        // This loop will create 20 Views containing
        // the image with the count of view
//        for (i in 1..6) {
//            data.add(Book(R.drawable.ic_book_24, " $i", "author", ArrayList()))
//        }

        // This will pass the ArrayList to our Adapter
        adapter = CustomAdapter(this,data)
//        {it ->
//            Log.i("", "Clicked ${it.title}")
//        }

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter
        adapter.notifyDataSetChanged()

        /// recycler view end


        // Finding layout views by id
//        val editText = findViewById<EditText>(R.id.editTextForNER)
//        val textOutPut = findViewById<TextView>(R.id.textViewExtractedNames)
//        val buttonExtract = findViewById<Button>(R.id.buttonExtractNames)
        val buttonLoadBook = findViewById<Button>(R.id.loadBookButton)
        val buttonGraph = findViewById<Button>(R.id.getGraph)

        buttonGraph.setOnClickListener {
            val intent = Intent(this, BookGraph::class.java)
            startActivity(intent)
        }


        buttonLoadBook.setOnClickListener {

            val intent = Intent()
                .setType("application/epub+zip")
                .setAction(Intent.ACTION_GET_CONTENT)

            resultLauncher.launch(Intent.createChooser(intent, "Select a file"))
        }

//        buttonExtract.setOnClickListener {
//            Log.i(TAG, "Extract Button clicked")
//            Log.i(TAG, editText.text.toString())
//
//            try {
//                Thread {
////                    CoreNLP_API.pingServer(applicationContext, textOutPut)
//                    CoreNlpAPI.nerTagger(applicationContext, editText.text.toString(), textOutPut)
//                }.start()
//
//            } catch (e: Exception) {
//                Log.e(TAG, "Error extracting names: $e")
//                Toast.makeText(
//                    applicationContext,
//                    getString(R.string.error_extracting_names),
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK || result.data == null) {
                Log.w(TAG, "Did not get data back from launched activity, user likely cancelled")
                Toast.makeText(applicationContext, "Loading cancelled", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }

            if (result.resultCode == RESULT_OK) {
                val selectedFile = result.data!!.data
                Log.i(TAG, "File selected $selectedFile")//The uri with the location of the file
                Log.i(TAG, "Loading book")

                val book = BookUtil.processEpub(loadBook(selectedFile))
                Log.i(TAG, "hash code ${book.hashCode()}")

                if (data.contains(book)) {
                    Toast.makeText(applicationContext, "Book already loaded", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }
                data.add(0, book)
                adapter.notifyItemInserted(0)


//                switchToBookActivity(book)
            }
        }

    fun switchToBookActivity(bookInfo: BookInfo) {
        val intent = Intent(this, BookReaderActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, bookInfo)
        }
        startActivity(intent)
    }

    //TODO: temp for testing duplicate from BookReaderActivity
    @Throws(Exception::class)
    fun loadBook(selectedFile: Uri?): nl.siegmann.epublib.domain.Book {
//        Log.i(BookReaderActivity.TAG, "Loading book")
//        selectedFile.e
        val inputStreamNameFinder: InputStream? = selectedFile?.let {
            contentResolver.openInputStream(it)
        }

        if (inputStreamNameFinder == null) {
//            Log.e(BookReaderActivity.TAG, "There was an error while loading book $selectedFile")
            finish()
            throw Error()
        }
        val epubReader: EpubReader = EpubReader()
        val book: EpubBook = epubReader.readEpub(inputStreamNameFinder)

//        val myBook: Book = BookUtil.processEpub(book)
        return book
    }

}
