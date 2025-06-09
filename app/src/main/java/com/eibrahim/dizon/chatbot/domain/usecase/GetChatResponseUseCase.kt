package com.eibrahim.dizon.chatbot.domain.usecase

import com.eibrahim.dizon.chatbot.data.repository.ChatRepository
import com.eibrahim.dizon.chatbot.domain.model.ChatMessage
import com.eibrahim.dizon.core.response.ResponseEI
import kotlinx.coroutines.flow.Flow

class GetChatResponseUseCase(private val repository: ChatRepository) {
    suspend fun execute(jsonPayload: String): Flow<ResponseEI<ChatMessage>> {
        // You could add validation or other business rules here.
        return repository.getChatResponse(jsonPayload)
    }
}