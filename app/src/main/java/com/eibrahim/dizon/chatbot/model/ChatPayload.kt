package com.eibrahim.dizon.chatbot.model

data class ChatPayload(
    val messages: List<ChatMessage>,
    val functions: List<FunctionDefinition>? = null
)