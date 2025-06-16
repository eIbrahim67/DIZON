package com.eibrahim.dizon.chatbot.domain.model

data class ChatPayload(
    val messages: List<ChatbotMessage>,
    val functions: List<FunctionDefinition>? = null
)