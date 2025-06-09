package com.eibrahim.dizon.chatbot.domain.model

data class ChatMessage(
    val content: String,
    val role: String = "assistant",
    val isFromUser: Boolean = false,
    var images: List<String>? = null // Use URLs or resource identifiers as needed.
)
