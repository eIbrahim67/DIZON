package com.eibrahim.dizon.home.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eibrahim.dizon.R
import com.eibrahim.dizon.home.model.PropertyListing

class AdapterRVProperties80(
    private val goToProp: ((id: Int) -> Unit)
) : RecyclerView.Adapter<AdapterRVProperties80.CategoryViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_properties_80, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val property = differ.currentList[position]

        // Load property image using Glide
        property.image.let { url ->
            Glide.with(context).load(url).centerCrop().into(holder.itemImageProp)
        }

        // Set property title, location, and price
        holder.itemTitleProp.text = property.type
        //holder.itemLocationProp.text = property.tags.toString()
        //holder.itemPriceProp.text = property.link // Format as needed

        // Set click listener for the entire item
        holder.itemView.setOnClickListener {
            //goToProp(property.id)
        }
    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    fun submitList(PropertyListingList: List<PropertyListing>) {
        differ.submitList(PropertyListingList)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PropertyListing>() {
            override fun areItemsTheSame(
                oldItem: PropertyListing, newItem: PropertyListing
            ): Boolean = oldItem.title == newItem.title

            override fun areContentsTheSame(
                oldItem: PropertyListing, newItem: PropertyListing
            ): Boolean = oldItem == newItem
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImageProp: ImageView = itemView.findViewById(R.id.itemImageProperties80)
        val itemTitleProp: TextView = itemView.findViewById(R.id.itemTitleProperties80)
        val itemLocationProp: TextView = itemView.findViewById(R.id.itemLocationProperties80)
        val itemPriceProp: TextView = itemView.findViewById(R.id.itemPriceProperties80)
    }
}
