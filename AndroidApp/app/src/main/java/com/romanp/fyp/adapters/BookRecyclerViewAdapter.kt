package com.romanp.fyp.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.romanp.fyp.views.BookReaderActivity
import com.romanp.fyp.R
import com.romanp.fyp.database.BookDatabaseHelper
import java.io.Serializable

class BookRecyclerViewAdapter(
    private val context: Context,
    private val mList: MutableList<RecyclerBookInfo>,
//    private val onItemClicked: (Book) -> Unit
) : RecyclerView.Adapter<BookRecyclerViewAdapter.ViewHolder>() {
    companion object {
        private const val TAG = "CustomAdapter"
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
//    fun updateBookList(books:MutableList<RecyclerBookInfo>) {
//        Log.i(TAG, "mlist $mList \nbooks $books")
////        mList.clear()
//        Log.i(TAG, "mlist $mList \nbooks $books")
//        mList.addAll(books)
//        Log.i(TAG, "mlist $mList \nbooks $books")
//        notifyDataSetChanged()
//    }

    // Holds the views for adding it to image and text
    inner class ViewHolder(
        ItemView: View,
//        onItemClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(ItemView) {

//        init {
//            itemView.setOnClickListener {
//                onItemClicked(adapterPosition)
//            }
//        }

        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val titleTV: TextView = itemView.findViewById(R.id.titleTV)
        val authorTV: TextView = itemView.findViewById(R.id.authorTV)
        val deleteView: ImageView = itemView.findViewById(R.id.deleteButton)
//        val context = itemView.context


//        fun onClick(v: View?) {
//            Toast.makeText(context, "The Item Clicked is: $position", Toast.LENGTH_SHORT).show()
//        }

        fun bind(position: Int) {

            val itemsViewModel = mList[position]

            // sets the image to the imageview from our itemHolder class
            imageView.setImageResource(itemsViewModel.image)

            // sets the text to the textview from our itemHolder class
            titleTV.text = itemsViewModel.title
            authorTV.text = itemsViewModel.author
            itemView.setOnClickListener {
                Toast.makeText(context,"Clicked ${itemsViewModel.title}" , Toast.LENGTH_SHORT).show();

                Log.i(TAG, "Clicked ${itemsViewModel.title}")
                val EXTRA_MESSAGE = "Book"
                Log.i(TAG, " object $itemsViewModel")
                val intent = Intent(context, BookReaderActivity::class.java)
//                    .apply {
//                    putExtra(EXTRA_MESSAGE, itemsViewModel)
//                }
                intent.putExtra(EXTRA_MESSAGE, itemsViewModel.id)
                context.startActivity(intent)
            }

            deleteView.setOnClickListener{
                Toast.makeText(context,"Delete ${itemsViewModel.title}" , Toast.LENGTH_SHORT).show();
                val myDB = BookDatabaseHelper(context)
                myDB.deleteBook(itemsViewModel.id)
                mList.removeAt(position)
                notifyDataSetChanged()
            }
            //TODO No op
        }
    }

    data class RecyclerBookInfo(
        val image: Int,
        val author: String,
        val title: String,
        val id: Long
    ) : Serializable
}
