package com.romanp.fyp.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romanp.fyp.R
import com.romanp.fyp.adapters.BookRecyclerViewAdapter
import com.romanp.fyp.utils.InjectorUtils
import com.romanp.fyp.viewmodels.MainActivityViewModel
import org.jetbrains.annotations.TestOnly


class MainActivity : AppCompatActivity() {
    private lateinit var adapter: BookRecyclerViewAdapter

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var recyclerview: RecyclerView

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "$TAG Destroyed")
        viewModel.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "View Created")
        setContentView(R.layout.activity_main)

        recyclerview = findViewById<RecyclerView>(R.id.recyclerViewBooks)

        // view model
        initialiseViewModel()
        initialiseRecyclerViewAdapter()

        // Finding layout views by id
        val buttonLoadBook = findViewById<Button>(R.id.loadBookButton)



        buttonLoadBook.setOnClickListener {
            val intent = Intent()
                .setType("application/epub+zip")
                .setAction(Intent.ACTION_GET_CONTENT)

            resultLauncher.launch(Intent.createChooser(intent, "Select a file"))
        }

    }

    private fun initialiseViewModel() {
        Log.i(TAG, "Initialising view model")
        val factory = InjectorUtils.provideMainActivityViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory).get(MainActivityViewModel::class.java)
        viewModel.startThreads()//TODO: is this a good idea?
        viewModel.getBooks().observe(this, {
            // Triggers when it's changed
            adapter.notifyDataSetChanged()
            Log.i(
                TAG,
                "Book list got updated ${it as MutableList<BookRecyclerViewAdapter.RecyclerBookInfo>}"
            )

        })
        viewModel.getNLPServiceStatus().observe(this, {
            val tvServiceStatus = findViewById<TextView>(R.id.tvServiceStatus)
            tvServiceStatus.text = when (it) {
                true -> resources.getString(R.string.service_online)
                false -> resources.getString(R.string.service_offline)
            }
            Log.i(TAG, "Service Status got Updated to: $it")
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

                // TODO: Run in a thread while the book is being processed
                viewModel.addBook(selectedFile)

                adapter.notifyDataSetChanged()

            }
        }


    @TestOnly
    fun setTestViewModel(testViewModel: MainActivityViewModel) {
        viewModel = testViewModel
    }

}
