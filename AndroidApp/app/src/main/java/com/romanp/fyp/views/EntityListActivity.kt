package com.romanp.fyp.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romanp.fyp.R
import com.romanp.fyp.adapters.entityRecyclerView.EntityRecyclerViewAdapter
import com.romanp.fyp.models.book.BookInfo
import com.romanp.fyp.utils.InjectorUtils
import com.romanp.fyp.viewmodels.EntityListActivityViewModel

class EntityListActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MESSAGE_TYPE = "ENTITY_TYPE"
        const val EXTRA_MESSAGE = "EntityBookId"
    }

    private lateinit var viewModel: EntityListActivityViewModel

    private lateinit var adapter: EntityRecyclerViewAdapter

    private lateinit var recyclerview: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entitylist)
        val bookId = intent.getLongExtra(
            EXTRA_MESSAGE,
            -1
        )
        val listType = intent.getStringExtra(
            EXTRA_MESSAGE_TYPE
        ) == EntityType.CHARACTERS.message

        recyclerview = findViewById(R.id.recyclerViewEntities)

        initialiseViewMode(bookId, listType)
        initialiseRecyclerViewAdapter()
        setUpActionBar()
    }

    private fun setUpActionBar() {
        val myBookInfo: BookInfo = viewModel.getCurrentBookInfo()

        val actionBar = supportActionBar

        // providing title for the ActionBar
        actionBar!!.title = myBookInfo.author

        // providing subtitle for the ActionBar
        actionBar.subtitle = myBookInfo.title
    }

    private fun initialiseRecyclerViewAdapter() {
        // This will pass the ArrayList to our Adapter
        val data = viewModel.getCurrentList()

        //Sort Alphabetically
        val sortedList =
            if (data.isNotEmpty()) data.sortedWith(compareBy { it.name }) as MutableList<EntityRecyclerViewAdapter.RecyclerEntityInfo> else data
        adapter = EntityRecyclerViewAdapter(this, sortedList)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)
        // Setting the Adapter with the recyclerview

        recyclerview.adapter = adapter
    }

    private fun initialiseViewMode(bookId: Long, listType: Boolean) {
        val factory =
            InjectorUtils.provideEntityListActivityViewModelFactory(application, bookId, listType)
        viewModel = ViewModelProvider(this, factory)[EntityListActivityViewModel::class.java]
    }

}

enum class EntityType(val message: String) {
    CHARACTERS("Characters"),
    LOCATIONS("Locations")
}