package com.romanp.fyp.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.romanp.fyp.R
import com.romanp.fyp.database.BookDatabaseHelper
import com.romanp.fyp.utils.ToastUtils
import com.romanp.fyp.views.BookReaderActivity
import java.io.Serializable

class BookRecyclerViewAdapter(
    private val context: Context,
    private var mList: MutableList<RecyclerBookInfo>
) : RecyclerView.Adapter<BookRecyclerViewAdapter.ViewHolder>() {
    companion object {
        private const val TAG = "BookRecyclerViewAdapter"
        const val EXTRA_MESSAGE = "BookId"
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateContent(newList: MutableList<RecyclerBookInfo>) {
        mList = newList
        notifyDataSetChanged()
    }

    // Holds the views for adding it to image and text
    inner class ViewHolder(
        ItemView: View
    ) : RecyclerView.ViewHolder(ItemView) {


        private val imageView: ImageView = itemView.findViewById(R.id.imageview)
        private val titleTV: TextView = itemView.findViewById(R.id.titleTV)
        private val authorTV: TextView = itemView.findViewById(R.id.authorTV)
        private val deleteView: ImageView = itemView.findViewById(R.id.deleteButton)
        private val failedImage: ImageView = itemView.findViewById(R.id.failedImage)
        private val progressBarProcessing: ProgressBar =
            itemView.findViewById(R.id.progressBarProcessing)


        fun bind(position: Int) {

            val itemsViewModel = mList[position]

            // sets the image to the imageview from our itemHolder class
            imageView.setImageResource(itemsViewModel.image)

            // sets the text to the textview from our itemHolder class
            titleTV.text = itemsViewModel.title
            authorTV.text = itemsViewModel.author
            itemView.setOnClickListener {
                Log.i(TAG, "Clicked ${itemsViewModel.title}")

                val intent = Intent(context, BookReaderActivity::class.java)
                intent.putExtra(EXTRA_MESSAGE, itemsViewModel.id)
                context.startActivity(intent)
            }
            /*
            might need to implement mvvm for adapter
            */
            when (itemsViewModel.processed) {
                ProcessedState.PROCESSING -> {
                    Log.i(TAG, "item in position $position is set to visible progress bar")
                    progressBarProcessing.visibility = View.VISIBLE
                    failedImage.visibility = View.INVISIBLE
                }
                ProcessedState.SUCCESSFULLY_PROCESSED -> {
                    progressBarProcessing.visibility = View.INVISIBLE
                    failedImage.visibility = View.INVISIBLE
                }
                ProcessedState.FAILED -> {
                    // Failed
                    failedImage.visibility = View.VISIBLE
                    progressBarProcessing.visibility = View.INVISIBLE
                }
            }

            deleteView.setOnClickListener {
                ToastUtils.toast(context, "Delete ${itemsViewModel.title}")
                val myDB = BookDatabaseHelper(context)
                myDB.deleteBook(itemsViewModel.id)
                mList.removeAt(position)
                notifyDataSetChanged()
            }
        }
    }

    data class RecyclerBookInfo(
        val image: Int,
        val author: String,
        val title: String,
        val id: Long,
        var processed: ProcessedState // 0 - processing, 1 - processed, 2 - failed
    ) : Serializable

    enum class ProcessedState(val message: Int) {
        PROCESSING(0),
        SUCCESSFULLY_PROCESSED(1),
        FAILED(2),
    }
}
