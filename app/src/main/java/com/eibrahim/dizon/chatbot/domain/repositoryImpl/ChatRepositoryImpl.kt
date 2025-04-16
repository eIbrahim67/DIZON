package com.eibrahim.dizon.chatbot.domain.repositoryImpl

import com.eibrahim.dizon.chatbot.data.network.ChatLlamaStreamProcessor
import com.eibrahim.dizon.chatbot.data.repository.ChatRepository
import com.eibrahim.dizon.chatbot.domain.model.ChatMessage
import com.eibrahim.dizon.core.response.FailureReason
import com.eibrahim.dizon.core.response.Response
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ChatRepositoryImpl(
    private val streamProcessor: ChatLlamaStreamProcessor
) : ChatRepository {
    override suspend fun getChatResponse(jsonPayload: String): Flow<Response<ChatMessage>> = callbackFlow {
        // Emit a loading state immediately.
        trySend(Response.Loading)

        val conversationBuilder = StringBuilder()
        streamProcessor.getChatLlamaStream(
            jsonPayload = jsonPayload,
            onMessageReceived = { line ->
                conversationBuilder.append(line).append("\n")
                // Optionally, emit intermediate loading states or partial updates.
                trySend(Response.Loading)
            },
            onError = { e ->
                trySend(Response.Failure(FailureReason.UnknownError(e.toString())))
                close(e)
            },
            onReceiving = {
                // Could update UI to show shimmer/loading.
                trySend(Response.Loading)
            },
            onComplete = {
                trySend(Response.Success(ChatMessage(content = conversationBuilder.toString())))
                close()
            }
        )

        awaitClose { /* Optionally cancel the stream if needed */ }
    }
}