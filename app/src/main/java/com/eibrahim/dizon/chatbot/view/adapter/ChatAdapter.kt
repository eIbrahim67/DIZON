package com.eibrahim.dizon.chatbot.view.adapter

import android.print.PrintAttributes.Margins
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.dizon.R
import com.eibrahim.dizon.chatbot.model.Message
import com.google.android.material.card.MaterialCardView
import io.noties.markwon.Markwon

class ChatAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        val imageCard: MaterialCardView = itemView.findViewById(R.id.imageCard)
        val viewEnd: View = itemView.findViewById(R.id.viewEnd)
        val viewStart: View = itemView.findViewById(R.id.viewStart)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        //user
        if (position % 2 == 0) {
            holder.viewStart.visibility = View.VISIBLE
            holder.viewEnd.visibility = View.GONE
            holder.imageCard.visibility = View.GONE
        }
        //bot
        else {
            holder.viewEnd.visibility = View.VISIBLE
            holder.imageCard.visibility = View.VISIBLE
            holder.viewStart.visibility = View.GONE
        }

        val message = messages[position]
//        holder.messageTextView.text = message.content

        val markwon = Markwon.create(holder.itemView.context)
        markwon.setMarkdown(holder.messageTextView, message.content)
    }

    override fun getItemCount(): Int = messages.size
}