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
import com.eibrahim.dizon.home.model.Property

class AdapterRVProperties80(
    private val goToDetails: ((id: Int) -> Unit)? = null,
    private val onWishlistClick: ((Property) -> Unit)? = null
) : RecyclerView.Adapter<AdapterRVProperties80.PropertyViewHolder>() {

    private lateinit var context: Context

    private var favoriteIds: Set<Int> = emptySet()

    fun setFavorites(favIds: Set<Int>) {
        favoriteIds = favIds
        notifyDataSetChanged() // refresh all items to update icon
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_properties_80, parent, false)
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
        val maxLength = 30 // you can change this to your limit
        val title = property.title

        holder.itemTitleProp.text = if (title.length > maxLength) {
            title.substring(0, maxLength) + "..."
        } else {
            title
        }
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
        val itemImageProp: ImageView = itemView.findViewById(R.id.itemImageProperties80)
        val itemWishlist: ImageView = itemView.findViewById(R.id.itemAddToWishlist80)
        val itemSaleType: TextView = itemView.findViewById(R.id.itemSaleType80)
        val itemTitleProp: TextView = itemView.findViewById(R.id.itemTitleProperties80)
        val itemPriceProp: TextView = itemView.findViewById(R.id.itemPriceProperties80)
        val itemLocationProp: TextView = itemView.findViewById(R.id.itemLocationProperties80)
        val itemSize: TextView = itemView.findViewById(R.id.textView15)
        val itemBedrooms: TextView = itemView.findViewById(R.id.textView14)
        val itemBathrooms: TextView = itemView.findViewById(R.id.textView16)
    }
}
