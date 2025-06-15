package com.eibrahim.dizon.search.presentation.view.adapter

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
import com.eibrahim.dizon.search.data.Property

class AdapterRVSearch(
    private val goToDetails: ((id: Int) -> Unit)? = null,
    private val onWishlistClick: ((Property) -> Unit)? = null
) : RecyclerView.Adapter<AdapterRVSearch.PropertyViewHolder>() {

    private var favoriteIds: Set<Int> = emptySet()

    fun setFavorites(favIds: Set<Int>) {
        favoriteIds = favIds
        notifyDataSetChanged() // refresh all items to update icon
    }

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_properties, parent, false)
        return PropertyViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = differ.currentList[position]

        val isFavorite = favoriteIds.contains(property.propertyId)
        holder.itemWishlist.setImageResource(
            if (isFavorite) R.drawable.icon_solid_love else R.drawable.icon_selector_love
        )

        holder.itemWishlist.setOnClickListener {
            onWishlistClick?.invoke(property)
        }

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
        holder.itemWishlist.setOnClickListener {
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
        val itemImageProp: ImageView = itemView.findViewById(R.id.itemImageProperties)
        val itemWishlist: ImageView = itemView.findViewById(R.id.itemAddToWishlist)
        val itemSaleType: TextView = itemView.findViewById(R.id.itemSaleType)
        val itemTitleProp: TextView = itemView.findViewById(R.id.itemTitleProperties)
        val itemPriceProp: TextView = itemView.findViewById(R.id.itemPriceProperties)
        val itemLocationProp: TextView = itemView.findViewById(R.id.itemLocationProperties)
        val itemSize: TextView = itemView.findViewById(R.id.textSize)
        val itemBedrooms: TextView = itemView.findViewById(R.id.textBedNum)
        val itemBathrooms: TextView = itemView.findViewById(R.id.textBathNum)
    }
}
