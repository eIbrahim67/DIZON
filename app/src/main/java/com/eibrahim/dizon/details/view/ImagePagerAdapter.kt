package com.eibrahim.dizon.details.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.eibrahim.dizon.R

class ImagePagerAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar) // Add progress bar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val url = imageUrls[position]
        Log.d("ImagePagerAdapter", "Loading image URL: $url")
        holder.progressBar.visibility = View.VISIBLE // Show progress bar

        if (url == "placeholder") {
            Glide.with(holder.itemView.context)
                .load(R.drawable.home)
                .centerCrop()
                .into(holder.imageView)
            holder.progressBar.visibility = View.GONE
        } else {
            Glide.with(holder.itemView.context)
                .load(url)
                .centerCrop()
                .thumbnail(0.25f) // Load a low-resolution thumbnail first
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache both original and thumbnail
                .placeholder(R.drawable.home)
                .error(R.drawable.home)
                .listener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("ImagePagerAdapter", "Failed to load image: $url, Error: ${e?.message}")
                        holder.progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: android.graphics.drawable.Drawable,
                        model: Any,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("ImagePagerAdapter", "Successfully loaded image: $url")
                        holder.progressBar.visibility = View.GONE
                        return false
                    }
                })
                .into(holder.imageView)
        }
    }

    override fun getItemCount(): Int = imageUrls.size
}