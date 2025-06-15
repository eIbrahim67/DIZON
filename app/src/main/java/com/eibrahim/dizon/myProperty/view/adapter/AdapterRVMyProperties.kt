package com.eibrahim.dizon.myProperty.view.adapter

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
import com.eibrahim.dizon.myProperty.model.Property

class AdapterRVMyProperties(
    private val goToDetails: ((id: Int) -> Unit)? = null,
    private val onWishlistClick: ((Property) -> Unit)? = null
) : RecyclerView.Adapter<AdapterRVMyProperties.PropertyViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_my_properties, parent, false)
        return PropertyViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = differ.currentList[position]

        // Load first property image using Glide
        val imageUrl = property.propertyImages.values.firstOrNull()
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(imageUrl)
                .centerCrop()
                .into(holder.itemImageProp)
        } else {
            holder.itemImageProp.setImageResource(R.drawable.circle_background)
        }

        // Bind text fields
        holder.itemTitleProp.text = property.title
        holder.itemPriceProp.text = String.format("%.0f LE", property.price)
        holder.itemLocationProp.text = listOf(property.street, property.city, property.governate)
            .filter { it.isNotBlank() }
            .joinToString(", ")

        // Amenities/info tags
        holder.itemSize.text = String.format("%.0f m²", property.size)
        holder.itemBedrooms.text = "${property.bedrooms} bedrooms"
        holder.itemBathrooms.text = "${property.bathrooms} bathrooms"

        // Sale type (you could derive this from a field if available)
        holder.itemSaleType.text = if (property.price > 0) "For Sale" else "For Rent"

        // Click listeners
        holder.itemView.setOnClickListener {
            goToDetails?.invoke(property.propertyId)
        }
        holder.itemSponsored.setOnClickListener {
            onWishlistClick?.invoke(property)
        }
    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    fun submitList(propertyList: List<Property>) {
        differ.submitList(propertyList)
    }

    override fun getItemCount(): Int = differ.currentList.size

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Property>() {
            override fun areItemsTheSame(oldItem: Property, newItem: Property): Boolean =
                oldItem.propertyId == newItem.propertyId

            override fun areContentsTheSame(oldItem: Property, newItem: Property): Boolean =
                oldItem == newItem
        }
    }

    class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImageProp: ImageView = itemView.findViewById(R.id.itemImageMyProperties)
        val itemSponsored: ImageView = itemView.findViewById(R.id.itemSponsored)
        val itemSaleType: TextView = itemView.findViewById(R.id.itemMySaleType)
        val itemTitleProp: TextView = itemView.findViewById(R.id.itemTitleMyProperties)
        val itemPriceProp: TextView = itemView.findViewById(R.id.itemPriceMyProperties)
        val itemLocationProp: TextView = itemView.findViewById(R.id.itemLocationMyProperties)
        val itemSize: TextView = itemView.findViewById(R.id.textMySize)
        val itemBedrooms: TextView = itemView.findViewById(R.id.textMyBedNum)
        val itemBathrooms: TextView = itemView.findViewById(R.id.textMyBathNum)
    }
}
