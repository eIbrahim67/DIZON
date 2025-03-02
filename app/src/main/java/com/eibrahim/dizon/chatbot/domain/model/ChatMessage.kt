package com.eibrahim.dizon.chatbot.domain.model

data class ChatMessage(
    val content: String,
    val role: String = "bot",
    val isFromUser: Boolean =false
)