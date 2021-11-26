package com.romanp.fyp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.romanp.fyp.nlp.CoreNlpAPI


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        const val EXTRA_MESSAGE = "com.example.MainActivity.book"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_main)


        // Finding layout views by id
        val editText = findViewById<EditText>(R.id.editTextForNER)
        val textOutPut = findViewById<TextView>(R.id.textViewExtractedNames)
        val buttonExtract = findViewById<Button>(R.id.buttonExtractNames)
        val buttonLoadBook = findViewById<Button>(R.id.loadBookButton)
        val buttonGraph = findViewById<Button>(R.id.getGraph)

        buttonGraph.setOnClickListener {
            val intent = Intent(this, BookGraph::class.java)
            startActivity(intent)
        }


        buttonLoadBook.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            resultLauncher.launch(Intent.createChooser(intent, "Select a file"))
        }

        buttonExtract.setOnClickListener {
            Log.i(TAG, "Extract Button clicked")
            Log.i(TAG, editText.text.toString())

            try {
                Thread {
//                    CoreNLP_API.pingServer(applicationContext, textOutPut)
                    CoreNlpAPI.nerTagger(applicationContext, editText.text.toString(), textOutPut)
                }.start()

            } catch (e: Exception) {
                Log.e(TAG, "Error extracting names: $e")
                Toast.makeText(
                    applicationContext,
                    getString(R.string.error_extracting_names),
                    Toast.LENGTH_SHORT
                ).show()
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

                val intent = Intent(this, BookReaderActivity::class.java).apply {
                    putExtra(EXTRA_MESSAGE, selectedFile.toString())
                }
                startActivity(intent)
            }
        }

}