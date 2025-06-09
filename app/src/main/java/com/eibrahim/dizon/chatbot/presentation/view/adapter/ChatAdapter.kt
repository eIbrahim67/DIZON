package com.eibrahim.dizon.chatbot.presentation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eibrahim.dizon.R
import com.eibrahim.dizon.chatbot.domain.model.ChatMessage
import com.eibrahim.dizon.databinding.ItemChatBotBinding
import com.eibrahim.dizon.databinding.ItemChatUserBinding
import com.eibrahim.dizon.search.data.Property
import io.noties.markwon.Markwon

/**
 * Adapter for displaying chatbot conversation messages and associated property images.
 */
class ChatAdapter(
    private val onSeeAllResultsClick: () -> Unit
) : ListAdapter<ChatAdapter.ChatItem, ChatAdapter.ChatViewHolder>(ChatItemDiffCallback()) {

    /**
     * Sealed class representing different types of chat items.
     */
    sealed class ChatItem {
        data class UserMessage(val message: ChatMessage) : ChatItem()
        data class BotMessage(val message: ChatMessage, val properties: List<Property>) : ChatItem()
    }

    /**
     * ViewHolder for chat messages, using View Binding for efficient view access.
     */
    sealed class ChatViewHolder : RecyclerView.ViewHolder {
        constructor(binding: ItemChatUserBinding) : super(binding.root)
        constructor(binding: ItemChatBotBinding) : super(binding.root)

        abstract fun bind(item: ChatItem)
    }

    private class UserViewHolder(
        private val binding: ItemChatUserBinding,
        private val markwon: Markwon
    ) : ChatViewHolder(binding) {
        override fun bind(item: ChatItem) {
            if (item is ChatItem.UserMessage) {
                markwon.setMarkdown(binding.messageTextView, item.message.content)
            }
        }
    }

    private class BotViewHolder(
        private val binding: ItemChatBotBinding,
        private val markwon: Markwon,
        private val onSeeAllResultsClick: () -> Unit
    ) : ChatViewHolder(binding) {
        override fun bind(item: ChatItem) {
            if (item is ChatItem.BotMessage) {
                markwon.setMarkdown(binding.messageTextView, item.message.content)
                if (item.message.images.isNullOrEmpty()) {
                    binding.finalResultCard.visibility = ViewGroup.GONE
                } else {
                    binding.finalResultCard.visibility = ViewGroup.VISIBLE
                    loadImages(item.message.images)
                    binding.seeAllResults.setOnClickListener { onSeeAllResultsClick() }
                }
            }
        }

        private fun loadImages(images: List<String>?) {
            if (images.isNullOrEmpty())
                return

            val imageViews = listOf(
                binding.imageItem1,
                binding.imageItem2,
                binding.imageItem3,
                binding.imageItem4
            )
            imageViews.forEachIndexed { index, imageView ->
                images.getOrNull(index)?.let { url ->
                    Glide.with(imageView)
                        .load(url)
                        .placeholder(R.color.gray_v2) // Ensure you have this drawable
                        .error(R.drawable.error_image) // Ensure you have this drawable
                        .into(imageView)
                } ?: imageView.setImageDrawable(null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val markwon = Markwon.create(parent.context)
        return when (viewType) {
            USER_MESSAGE -> UserViewHolder(
                ItemChatUserBinding.inflate(inflater, parent, false),
                markwon
            )

            BOT_MESSAGE -> BotViewHolder(
                ItemChatBotBinding.inflate(inflater, parent, false),
                markwon,
                onSeeAllResultsClick
            )

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is ChatItem.UserMessage -> USER_MESSAGE
        is ChatItem.BotMessage -> BOT_MESSAGE
    }

    /**
     * Updates the adapter with new messages and properties.
     */
    fun updateData(messages: List<ChatMessage>, properties: List<Property>) {
        val items = messages.map { message ->
            if (message.isFromUser) {
                ChatItem.UserMessage(message)
            } else {
                ChatItem.BotMessage(message, properties)
            }
        }
        submitList(items)
    }

    companion object {
        private const val USER_MESSAGE = 0
        private const val BOT_MESSAGE = 1
    }
}

/**
 * DiffUtil callback for efficient updates of chat items.
 */
private class ChatItemDiffCallback : DiffUtil.ItemCallback<ChatAdapter.ChatItem>() {
    override fun areItemsTheSame(
        oldItem: ChatAdapter.ChatItem,
        newItem: ChatAdapter.ChatItem
    ): Boolean {
        return when {
            oldItem is ChatAdapter.ChatItem.UserMessage && newItem is ChatAdapter.ChatItem.UserMessage ->
                oldItem.message.content == newItem.message.content

            oldItem is ChatAdapter.ChatItem.BotMessage && newItem is ChatAdapter.ChatItem.BotMessage ->
                oldItem.message.content == newItem.message.content && oldItem.message.images == newItem.message.images

            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: ChatAdapter.ChatItem,
        newItem: ChatAdapter.ChatItem
    ): Boolean {
        return oldItem == newItem
    }
}