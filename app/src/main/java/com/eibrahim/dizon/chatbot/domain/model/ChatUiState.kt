package com.eibrahim.dizon.chatbot.domain.model

import com.eibrahim.dizon.search.data.Property

/**
 * Data class representing the UI state for the chatbot.
 */
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val properties: List<Property> = emptyList(),
    val isSendButtonVisible: Boolean = false,
    val isRecording: Boolean = false,
    val errorMessage: String? = null
)