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

class AdapterRVProperties(
    private val goToProp: ((property: PropertyListing) -> Unit)
) : RecyclerView.Adapter<AdapterRVProperties.CategoryViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_properties, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val property = differ.currentList[position]

        // Load property image using Glide
        property.imageUrl.let { url ->
            Glide.with(context)
                .load(url)
                .centerCrop()
                .into(holder.itemImageProp)
        }

        // Set property title (or type as needed)
        holder.itemTitleProp.text = property.propertyType

        // Set click listener: pass the entire property listing object
        holder.itemView.setOnClickListener {
            goToProp(property)
        }
    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    fun submitList(propertyListingList: List<PropertyListing>) {
        differ.submitList(propertyListingList)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PropertyListing>() {
            override fun areItemsTheSame(oldItem: PropertyListing, newItem: PropertyListing): Boolean =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: PropertyListing, newItem: PropertyListing): Boolean =
                oldItem == newItem
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImageProp: ImageView = itemView.findViewById(R.id.itemImageProperties)
        val itemTitleProp: TextView = itemView.findViewById(R.id.itemTitleProperties)
        // Add more views if needed (e.g., location, price)
    }
}
