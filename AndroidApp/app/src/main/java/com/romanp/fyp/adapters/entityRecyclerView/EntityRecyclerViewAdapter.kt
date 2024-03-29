package com.romanp.fyp.adapters.entityRecyclerView

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.romanp.fyp.R
import com.romanp.fyp.views.EntityProfileActivity
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

            itemView.setOnClickListener {
                Log.i(TAG, "Clicked ${itemsViewModel.name}")
                val intent =
                    Intent(context, EntityProfileActivity::class.java)
                intent.putExtra(EntityProfileActivity.BOOK_ID, itemsViewModel.id)
                intent.putExtra(EntityProfileActivity.ENTITY_NAME, itemsViewModel.name)
                intent.putExtra(
                    EntityProfileActivity.ENTITY_TYPE,
                    when (itemsViewModel.listType) {
                        true -> EntityProfileActivity.EntityType.CHARACTER.message
                        false -> EntityProfileActivity.EntityType.LOCATION.message
                    }
                )
                context.startActivity(intent)
            }


        }


    }

    data class RecyclerEntityInfo(
        val id: Long,
        val name: String,
        val listType: Boolean
    ) : Serializable
}
