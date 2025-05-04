package com.eibrahim.dizon.chatbot.presentation.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eibrahim.dizon.R
import com.eibrahim.dizon.chatbot.domain.model.ChatMessage
import com.eibrahim.dizon.search.data.Property
import com.google.android.material.card.MaterialCardView
import io.noties.markwon.Markwon

class ChatAdapter(
    private val messages: MutableList<ChatMessage>,
    private val properties: List<Property>? = null, // Add properties parameter
    private val goToAllResult: () -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        val finalResultCard: MaterialCardView = itemView.findViewById(R.id.finalResultCard)
        val imageItem1: ImageView = itemView.findViewById(R.id.imageItem1)
        val imageItem2: ImageView = itemView.findViewById(R.id.imageItem2)
        val imageItem3: ImageView = itemView.findViewById(R.id.imageItem3)
        val imageItem4: ImageView = itemView.findViewById(R.id.imageItem4)
        val seeAllResults: TextView = itemView.findViewById(R.id.seeAllResults)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layout = if (viewType == 0) R.layout.item_chat_user else R.layout.item_chat_bot
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isFromUser) 0 else 1
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        val markwon = Markwon.create(holder.itemView.context)

        markwon.setMarkdown(holder.messageTextView, message.content)

        if (!message.images.isNullOrEmpty()) {
            holder.finalResultCard.visibility = View.VISIBLE
            // Safely load up to 4 images
            message.images!!.getOrNull(0)
                ?.let { Glide.with(holder.imageItem1).load(it).into(holder.imageItem1) }
            message.images!!.getOrNull(1)
                ?.let { Glide.with(holder.imageItem2).load(it).into(holder.imageItem2) }
            message.images!!.getOrNull(2)
                ?.let { Glide.with(holder.imageItem3).load(it).into(holder.imageItem3) }
            message.images!!.getOrNull(3)
                ?.let { Glide.with(holder.imageItem4).load(it).into(holder.imageItem4) }

            // Handle "See All Results" click
            holder.seeAllResults.setOnClickListener {
                goToAllResult()
            }
        } else {
            holder.finalResultCard.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = messages.size

    /**
     * Update the images list of the last bot message and refresh that item.
     * @param newImages List of image URLs to display (up to 4).
     */
    fun updateLastBotMessageImages(newImages: List<String>) {
        // Find last index of a bot message
        val lastBotIndex = messages.indexOfLast { !it.isFromUser }
        if (lastBotIndex != -1) {
            // Update the model
            messages[lastBotIndex].images = newImages
            // Notify adapter to rebind this item
            notifyItemChanged(lastBotIndex)
        }
    }
}