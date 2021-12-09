package com.romanp.fyp

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romanp.fyp.adapters.BookRecyclerViewAdapter
import com.romanp.fyp.database.BookDatabaseHelper
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.models.book.BookUtil
import com.romanp.fyp.viewmodels.MainActivityViewModel
import com.romanp.fyp.viewmodels.MainViewModelFactory
import nl.siegmann.epublib.domain.Book as EpubBook
import nl.siegmann.epublib.epub.EpubReader
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    private lateinit var adapter: BookRecyclerViewAdapter
    // ArrayList of class ItemsViewModel
    private val data = ArrayList<BookRecyclerViewAdapter.RecyclerBookInfo>()

    private lateinit var viewModel: MainActivityViewModel;
    private lateinit var recyclerview: RecyclerView

    companion object {
        private const val TAG = "MainActivity"
        const val EXTRA_MESSAGE = "com.example.MainActivity.book"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // getting the recyclerview by its id
        recyclerview= findViewById<RecyclerView>(R.id.recyclerViewBooks)


        // view model
        initialiseViewModel()

        initialiseAdapter()


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

    private fun initialiseViewModel() {
        val factory = MainViewModelFactory()
        viewModel = ViewModelProvider(this, factory).get(MainActivityViewModel::class.java)

        viewModel.getBooks().observe(this, Observer {
            // Triggers when it's changed
            adapter.notifyDataSetChanged()
    //            Log.i("data",it.toString())
    //            mainrecycler.adapter= NoteRecyclerAdapter(viewModel, it, this)

        })
    }

    private fun initialiseAdapter() {


        getBookData()
        // This will pass the ArrayList to our Adapter
        println(viewModel.getBooks())
        println(viewModel.getBooks().value)
        if (viewModel.getBooks().value != null) {
            adapter = BookRecyclerViewAdapter(this, viewModel.getBooks().value!!)
        }
//        adapter = BookRecyclerViewAdapter(this, data)
        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)
        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter
        adapter.notifyDataSetChanged()

        /// recycler view end
    }

    private fun getBookData() {
        // TODO: repeat in BookInfoRepository remove
        val myDB = BookDatabaseHelper(applicationContext)
        val cursor: Cursor? = myDB.getAllBooks()
        if (cursor == null || cursor.count == 0) {
            //No data
        } else {
            while (cursor.moveToNext()) {
                println("cursor ${cursor.toString()}")
                data.add(
                    BookRecyclerViewAdapter.RecyclerBookInfo(
                        R.drawable.ic_book_24,
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getLong(0),
                    )
                )
            }
        }
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

                //TODO: instead check with database
//                if (data.contains(book)) {
//                    Toast.makeText(applicationContext, "Book already loaded", Toast.LENGTH_SHORT).show()
//                    return@registerForActivityResult
//                }

                val appDB : BookDatabaseHelper = BookDatabaseHelper(applicationContext)
                val id = appDB.addBook(book)
                if (id < 0) {
                    Toast.makeText(applicationContext, "DB failed to save book", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }
                data.add(0, BookRecyclerViewAdapter.RecyclerBookInfo(book.image, book.author, book.title, id)) //TODO: send it name, title and ID
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
