package com.romanp.fyp.views

import android.content.Intent
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
import com.romanp.fyp.R
import com.romanp.fyp.adapters.BookRecyclerViewAdapter
import com.romanp.fyp.models.book.BookUtil
import com.romanp.fyp.utils.InjectorUtils
import com.romanp.fyp.viewmodels.MainActivityViewModel
import nl.siegmann.epublib.domain.Book as EpubBook
import nl.siegmann.epublib.epub.EpubReader
import org.jetbrains.annotations.TestOnly
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
        recyclerview = findViewById<RecyclerView>(R.id.recyclerViewBooks)


        // view model
        initialiseViewModel()
        initialiseRecyclerViewAdapter()


        // Finding layout views by id
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

    }

    private fun initialiseViewModel() {
        val factory = InjectorUtils.provideMainActivityViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory).get(MainActivityViewModel::class.java)
        viewModel.getBooks().observe(this, Observer {
            // Triggers when it's changed
            adapter.notifyDataSetChanged()
            Log.i(
                TAG,
                "Observer called it ${it as MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>}"
            )

        })
    }

    private fun initialiseRecyclerViewAdapter() {
        // This will pass the ArrayList to our Adapter
        adapter = BookRecyclerViewAdapter(this, viewModel.getBooks().value!!)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)
        // Setting the Adapter with the recyclerview

        recyclerview.adapter = adapter


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

                val id = viewModel.addBook(book)
                if (id < 0) {
                    Toast.makeText(applicationContext, "DB failed to save book", Toast.LENGTH_SHORT)
                        .show()
                    return@registerForActivityResult
                }

                adapter.notifyDataSetChanged()

            }
        }

    // TODO: temp for testing duplicate from BookReaderActivity
    @Throws(Exception::class)
    fun loadBook(selectedFile: Uri?): nl.siegmann.epublib.domain.Book {
//        Log.i(BookReaderActivity.TAG, "Loading book")
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

        return book
    }

//    buttonExtract.setOnClickListener {
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

    @TestOnly
    fun setTestViewModel(testViewModel: MainActivityViewModel) {
        viewModel = testViewModel
    }

}
