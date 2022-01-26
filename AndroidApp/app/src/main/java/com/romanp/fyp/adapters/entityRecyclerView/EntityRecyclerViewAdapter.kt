package com.romanp.fyp.adapters.entityRecyclerView

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.romanp.fyp.R
import java.io.Serializable

class EntityRecyclerViewAdapter(
    private val context: Context,
    private val mList: MutableList<RecyclerEntityInfo>
) : RecyclerView.Adapter<EntityRecyclerViewAdapter.ViewHolder>() {
    companion object {
        private const val TAG = "EntityRecyclerViewAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.entity_card_view_design, parent, false)

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

    // Holds the views for adding it to image and text
    inner class ViewHolder(
        ItemView: View
    ) : RecyclerView.ViewHolder(ItemView) {


        private val entityNameTV: TextView = itemView.findViewById(R.id.entityNameTV)


        fun bind(position: Int) {

            val itemsViewModel = mList[position]

            // sets the text to the textview from our itemHolder class
            entityNameTV.text = itemsViewModel.name
            entityNameTV.setOnClickListener {
                Toast.makeText(context, "Clicked ${itemsViewModel.name}", Toast.LENGTH_SHORT)
                    .show()

                Log.i(TAG, "Clicked ${itemsViewModel.name}")
            }


        }


    }

    data class RecyclerEntityInfo(
        val name: String
    ) : Serializable
}
