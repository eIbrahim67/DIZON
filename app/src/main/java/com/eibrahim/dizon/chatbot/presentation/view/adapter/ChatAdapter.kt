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
import com.eibrahim.dizon.home.model.PropertyListing
import com.google.android.material.card.MaterialCardView
import io.noties.markwon.Markwon

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        val finalResultCard: MaterialCardView = itemView.findViewById(R.id.finalResultCard)
        val imageItem1: ImageView = itemView.findViewById(R.id.imageItem1)
        val imageItem2: ImageView = itemView.findViewById(R.id.imageItem2)
        val imageItem3: ImageView = itemView.findViewById(R.id.imageItem3)
        val imageItem4: ImageView = itemView.findViewById(R.id.imageItem4)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layout = if (viewType == 0) {
            R.layout.item_chat_user // User message layout
        } else {
            R.layout.item_chat_bot  // Bot message layout
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isFromUser) 0 else 1 // 0 for user, 1 for bot
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        val markwon = Markwon.create(holder.itemView.context)

        markwon.setMarkdown(holder.messageTextView, message.content)

        if (message.images!= null){
            holder.finalResultCard.visibility = View.VISIBLE
            Glide.with(holder.imageItem1.context)
                .load(message.images[0])
                .into(holder.imageItem1)
            Glide.with(holder.imageItem2.context)
                .load(message.images[1])
                .into(holder.imageItem2)
            Glide.with(holder.imageItem3.context)
                .load(message.images[2])
                .into(holder.imageItem3)
            Glide.with(holder.imageItem4.context)
                .load(message.images[3])
                .into(holder.imageItem4)
        }
    }

    override fun getItemCount(): Int = messages.size
}