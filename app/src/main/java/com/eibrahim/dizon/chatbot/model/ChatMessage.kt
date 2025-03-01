package com.eibrahim.dizon.chatbot.model

data class ChatMessage(
    val content: String,
    val role: String = "bot",
    val isFromUser: Boolean =false
)