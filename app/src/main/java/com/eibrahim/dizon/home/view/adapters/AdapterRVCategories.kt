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
import com.eibrahim.dizon.core.remote.Category

class AdapterRVCategories(
    private val goToSearch: ((id: String) -> Unit)? = null
) :
    RecyclerView.Adapter<AdapterRVCategories.CategoryViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_categories, parent, false)
        parent.context
        return CategoryViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        differ.currentList[position].iconUrl?.let { url ->
            Glide
                .with(context)
                .load(url)
                .centerCrop()
                //.placeholder(R.drawable.placeholder_image_svg)
                .into(holder.categoryImage)
            //itemView.setOnClickListener { goToSearch?.let { it(category.strCategory) } }
        }

        differ.currentList[position].name.let { title ->
            holder.categoryTitle.text = title
        }

    }

    private val differ: AsyncListDiffer<Category> =
        AsyncListDiffer(this, DIFF_CALLBACK)


    fun submitList(articleList: List<Category>) {
        differ.submitList(articleList)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(
                oldItem: Category,
                newItem: Category
            ): Boolean =
                oldItem === newItem // this is data class

            override fun areContentsTheSame(
                oldItem: Category,
                newItem: Category
            ): Boolean = oldItem == newItem
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImage: ImageView = itemView.findViewById(R.id.itemCategoryImage)
        val categoryTitle: TextView = itemView.findViewById(R.id.itemCategoryTitle)

    }
}