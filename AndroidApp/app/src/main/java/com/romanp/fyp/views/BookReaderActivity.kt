package com.romanp.fyp.views

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.romanp.fyp.R
import com.romanp.fyp.adapters.BookRecyclerViewAdapter
import com.romanp.fyp.models.book.AlreadyOnTheFirstPageException
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.models.book.Chapter
import com.romanp.fyp.models.book.NoMorePagesException
import com.romanp.fyp.utils.InjectorUtils
import com.romanp.fyp.utils.ToastUtils
import com.romanp.fyp.viewmodels.BookReaderActivityViewModel
import com.romanp.fyp.viewmodels.graph.GraphType
import com.romanp.fyp.views.BookGraphActivity.Companion.BOOK_ID_GRAPH
import com.romanp.fyp.views.BookGraphActivity.Companion.GRAPH_TYPE
import com.romanp.fyp.views.EntityProfileActivity.Companion.BOOK_ID
import com.romanp.fyp.views.EntityProfileActivity.Companion.ENTITY_NAME
import com.romanp.fyp.views.EntityProfileActivity.Companion.ENTITY_TYPE


class BookReaderActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "BookReaderActivity"
    }

    private lateinit var viewModel: BookReaderActivityViewModel


    private var textViewBookContent: TextView? = null
    private var bookTitleTV: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "Opened Book Reader Activity")

        val bookId = intent.getLongExtra(
            BookRecyclerViewAdapter.EXTRA_MESSAGE,
            -1
        )

        initialiseViewModel(bookId)
        setContentView(R.layout.activity_book_reader)

        val myBookInfo: BookInfo = viewModel.getCurrentBookInfo()
        if (myBookInfo.isError()) {
            ToastUtils.toast(
                applicationContext,
                "There was an error loading your book"
            )
            finish()
            return
        }


        bookTitleTV = findViewById<TextView>(R.id.textViewBookTitle).apply {
            text = viewModel.getCurrentChapter().chapterTitle
        }

        textViewBookContent = findViewById<TextView>(R.id.textViewBookContent).apply {
            val spannableString = highlightEntities(viewModel.getCurrentChapter().text)
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }

        setUpActionBar()
        setUpListeners()
    }

    private fun setUpActionBar() {
        val myBookInfo: BookInfo = viewModel.getCurrentBookInfo()

        val actionBar = supportActionBar

        // providing title for the ActionBar
        actionBar!!.title = myBookInfo.author

        // providing subtitle for the ActionBar
        actionBar.subtitle = myBookInfo.title
    }

    private fun setUpListeners() {
        findViewById<Button>(R.id.buttonCharacters).apply {
            setOnClickListener {
                viewModel.switchToEntityList(context, EntityType.CHARACTERS)
            }
        }
        findViewById<Button>(R.id.buttonLocations).apply {
            setOnClickListener {
                viewModel.switchToEntityList(context, EntityType.LOCATIONS)
            }
        }

        findViewById<Button>(R.id.buttonNext).apply {
            setOnClickListener {
                Log.i(TAG, "Next Button pressed")
                try {
                    updatePage(viewModel.nextButton())
                } catch (e: NoMorePagesException) {
                    ToastUtils.toast(context, "No More Pages")
                }


            }
        }
        findViewById<Button>(R.id.buttonBack).apply {
            setOnClickListener {
                Log.i(TAG, "Back Button pressed")
                try {
                    updatePage(viewModel.backButton())
                } catch (e: AlreadyOnTheFirstPageException) {
                    ToastUtils.toast(context, "Already on the first page")
                }

            }
        }
    }

    private fun highlightEntities(
        text: String,
    ): SpannableString {
        val str = SpannableString(text)
        val (characters, locations) = viewModel.getCurrentChapterEntityMentionsSpans()
        try {

            characters.forEach { (name, characterMentions) ->
                characterMentions.forEach { characterMention ->
                    str.setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            val intent =
                                Intent(applicationContext, EntityProfileActivity::class.java)
                            intent.putExtra(BOOK_ID, viewModel.getBookID())
                            intent.putExtra(ENTITY_NAME, name)
                            intent.putExtra(
                                ENTITY_TYPE,
                                EntityProfileActivity.EntityType.CHARACTER.message
                            )
                            startActivity(intent)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.color = Color.parseColor("#26D9D2")
                        }
                    }, characterMention.characterStart, characterMention.characterEnd, 0)
                }

            }
            locations.forEach { (name, locationMentions) ->
                locationMentions.forEach { locationMention ->
                    str.setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            val intent =
                                Intent(applicationContext, EntityProfileActivity::class.java)
                            intent.putExtra(BOOK_ID, viewModel.getBookID())
                            intent.putExtra(ENTITY_NAME, name)
                            intent.putExtra(
                                ENTITY_TYPE,
                                EntityProfileActivity.EntityType.LOCATION.message
                            )
                            startActivity(intent)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.color = Color.parseColor("#D9262D")
                        }
                    }, locationMention.characterStart, locationMention.characterEnd, 0)
                }

            }
        } catch (e: Exception) {
            Log.e(TAG, "$e")
        }

        return str
    }


    /**
     * method to inflate the options menu when
     * the user opens the menu for the first time
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_buttons, menu)
        return super.onCreateOptionsMenu(menu)
    }


    /**
     * methods to control the operations that will
     * happen when user clicks on the action buttons
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.pieChart -> {
                openPieChart()
            }
            R.id.network -> openCharacterNetwork()
            R.id.lollipop -> {
                openLollipopChart()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openLollipopChart() {
        val intent = Intent(this, BookGraphActivity::class.java)
        intent.putExtra(BOOK_ID_GRAPH, viewModel.getBookID())
        intent.putExtra(GRAPH_TYPE, GraphType.LOLLIPOP_CHART)
        startActivity(intent)
    }

    private fun openCharacterNetwork() {
        val intent = Intent(this, BookGraphActivity::class.java)
        intent.putExtra(BOOK_ID_GRAPH, viewModel.getBookID())
        intent.putExtra(GRAPH_TYPE, GraphType.CHARACTER_NETWORK)
        startActivity(intent)
    }

    private fun openPieChart() {
        val intent = Intent(this, BookGraphActivity::class.java)
        intent.putExtra(BOOK_ID_GRAPH, viewModel.getBookID())
        intent.putExtra(GRAPH_TYPE, GraphType.PIE_CHART)
        startActivity(intent)
    }

    private fun initialiseViewModel(bookId: Long) {
        val factory = InjectorUtils.provideBookReaderActivityViewModelFactory(application, bookId)
        viewModel = ViewModelProvider(this, factory)[BookReaderActivityViewModel::class.java]
    }


    private fun updatePage(chapters: Chapter) {
        val spannableString = highlightEntities(chapters.text)
        textViewBookContent?.text = spannableString
        bookTitleTV = findViewById<TextView>(R.id.textViewBookTitle).apply {
            text = chapters.chapterTitle
        }
    }


}