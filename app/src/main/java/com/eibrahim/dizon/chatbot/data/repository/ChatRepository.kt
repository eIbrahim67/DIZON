package com.eibrahim.dizon.chatbot.data.repository

import com.eibrahim.dizon.chatbot.domain.model.ChatMessage
import com.eibrahim.dizon.core.response.ResponseEI
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getChatResponse(jsonPayload: String): Flow<ResponseEI<ChatMessage>>
}