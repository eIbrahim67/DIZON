package com.eibrahim.dizon.chatbot.domain.model

data class ChatbotMessage(
    val content: String,
    val role: String = "assistant",
    val isFromUser: Boolean = false
)
