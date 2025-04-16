package com.eibrahim.dizon.favorite.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.eibrahim.dizon.R
import android.widget.TextView
import com.bumptech.glide.Glide
import com.eibrahim.dizon.favorite.model.FavoriteProperty
import com.google.android.material.imageview.ShapeableImageView

class AdapterRVFavorite(
    private var favoriteList: List<FavoriteProperty>
) : RecyclerView.Adapter<AdapterRVFavorite.FavoriteViewHolder>() {

    class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.itemImageProperties80)
        val titleTextView: TextView = itemView.findViewById(R.id.itemTitleProperties80)
        val priceTextView: TextView = itemView.findViewById(R.id.itemPriceProperties80)
        val locationTextView: TextView = itemView.findViewById(R.id.itemLocationProperties80)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_favorite, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val property = favoriteList[position]
        holder.titleTextView.text = property.title
        holder.priceTextView.text = property.price
        holder.locationTextView.text = property.location

        // Load image from URL using Glide
        Glide.with(holder.itemView.context)
            .load(property.imageUrl)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = favoriteList.size


    fun updateList(newList: List<FavoriteProperty>) {
        favoriteList = newList
        notifyDataSetChanged()
    }
}