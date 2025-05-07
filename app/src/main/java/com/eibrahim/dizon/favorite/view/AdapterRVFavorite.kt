package com.eibrahim.dizon.favorite.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.eibrahim.dizon.R
import com.eibrahim.dizon.favorite.model.FavoriteProperty

class AdapterRVFavorite(
    private var favoriteList: List<FavoriteProperty>,
    private val onFavoriteClick: (propertyId: Int) -> Unit,
    private val onItemClick: (propertyId: Int) -> Unit
) : RecyclerView.Adapter<AdapterRVFavorite.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.itemImageProperties80)
        val title: TextView = itemView.findViewById(R.id.itemTitleProperties80)
        val price: TextView = itemView.findViewById(R.id.itemPriceProperties80)
        val location: TextView = itemView.findViewById(R.id.itemLocationProperties80)
        val favoriteButton: ImageView = itemView.findViewById(R.id.btn_love)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_favorite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val property = favoriteList[position]

        // Load image with optimized Glide settings
        property.imageUrl?.let { url ->
            Glide.with(holder.itemView.context)
                .load(url)
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.home)
                .error(R.drawable.home)
                .into(holder.image)
        } ?: Glide.with(holder.itemView.context)
            .load(R.drawable.home)
            .centerCrop()
            .into(holder.image)

        // Set title, price, and location
        holder.title.text = property.title
        holder.price.text = "$${String.format("%,.2f", property.price)}"
        holder.location.text = "${property.city}, ${property.governate}"

        // Set favorite button (always filled since this is the favorites list)
        holder.favoriteButton.setImageResource(R.drawable.ic_favorite_filled)
        holder.favoriteButton.imageTintList = null

        // Remove from favorites
        holder.favoriteButton.setOnClickListener {
            onFavoriteClick(property.propertyId)
        }

        // Navigate to details
        holder.itemView.setOnClickListener {
            onItemClick.invoke(property.propertyId)
        }
    }

    override fun getItemCount(): Int = favoriteList.size

    fun updateList(newList: List<FavoriteProperty>) {
        val diffCallback = FavoriteDiffCallback(favoriteList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        favoriteList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    private class FavoriteDiffCallback(
        private val oldList: List<FavoriteProperty>,
        private val newList: List<FavoriteProperty>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].propertyId == newList[newItemPosition].propertyId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}