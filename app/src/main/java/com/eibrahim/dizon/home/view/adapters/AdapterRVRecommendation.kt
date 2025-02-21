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
import com.eibrahim.dizon.core.remote.Property

class AdapterRVRecommendation(
    private val goToProp: ((id: Int) -> Unit)
) : RecyclerView.Adapter<AdapterRVRecommendation.CategoryViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_recommendation, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val property = differ.currentList[position]

        // Load property image using Glide
        property.images[0].let { url ->
            Glide.with(context)
                .load(url)
                .centerCrop()
                .into(holder.itemImageProp)
        }

        // Set property title, location, and price
        holder.itemTitleProp.text = property.title
        holder.itemLocationProp.text = "${property.address}, ${property.city}, ${property.country}"
        holder.itemPriceProp.text = "$${property.price}" // Format as needed

        // Set click listener for the entire item
        holder.itemView.setOnClickListener {
            goToProp(property.id)
        }
    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    fun submitList(propertyList: List<Property>) {
        differ.submitList(propertyList)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Property>() {
            override fun areItemsTheSame(oldItem: Property, newItem: Property): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Property, newItem: Property): Boolean =
                oldItem == newItem
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImageProp: ImageView = itemView.findViewById(R.id.itemImageRecommendation)
        val itemTitleProp: TextView = itemView.findViewById(R.id.itemTitleRecommendation)
        val itemLocationProp: TextView = itemView.findViewById(R.id.itemLocationRecommendation)
        val itemPriceProp: TextView = itemView.findViewById(R.id.itemPriceRecommendation)
    }
}
