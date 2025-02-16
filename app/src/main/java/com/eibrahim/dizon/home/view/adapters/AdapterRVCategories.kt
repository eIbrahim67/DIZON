package com.eibrahim.dizon.home.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.dizon.R
import com.eibrahim.dizon.core.remote.Category
import com.google.android.material.chip.Chip

class AdapterRVCategories(
    private val goToSearch: ((categoryName: String) -> Unit)? = null
) : RecyclerView.Adapter<AdapterRVCategories.CategoryViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv_categories, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = differ.currentList[position]
        holder.chipCategory.text = category.name

        // Optional: Set a click listener on the chip to trigger search or filtering.
        holder.chipCategory.setOnClickListener {
            goToSearch?.invoke(category.name)
        }
    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    fun submitList(categoryList: List<Category>) {
        differ.submitList(categoryList)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(
                oldItem: Category,
                newItem: Category
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Category,
                newItem: Category
            ): Boolean = oldItem == newItem
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chipCategory: Chip = itemView.findViewById(R.id.chipCategory)
    }
}
