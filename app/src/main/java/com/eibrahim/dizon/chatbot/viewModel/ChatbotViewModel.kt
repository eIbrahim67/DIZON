package com.eibrahim.dizon.chatbot.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.core.local.Llama
import com.eibrahim.dizon.core.response.Response
import com.eibrahim.dizon.core.utils.UtilsFunctions.applyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatbotViewModel : ViewModel() {
    private val llama = Llama()
    private val _chatMessages = MutableLiveData<Response<String>>()
    val chatMessages: LiveData<Response<String>> get() = _chatMessages

    // Holds the full conversation history (each line prefixed with "User:" or "Bot:")
    private var conversationHistory = ""

    fun startChat(userMessage: String) {
        // Append the new user message.
        conversationHistory += "\nUser: $userMessage"

        // Build JSON payload from the conversation history.
        // (In production, use a JSON library for proper escaping.)
        val messagesJson = conversationHistory
            .trim()
            .lines()
            .filter { it.isNotBlank() }
            .joinToString(",") { line ->
                when {
                    line.startsWith("User:") -> """{"role": "user", "content": "${line.removePrefix("User:").trim()}"}"""
                    line.startsWith("Bot:") -> """{"role": "assistant", "content": "${line.removePrefix("Bot:").trim()}"}"""
                    else -> ""
                }
            }
        val jsonPayload = """
            {
              "messages": [
                $messagesJson
              ]
            }
        """.trimIndent()

        // Use your generic applyResponse to trigger the data fetch.
        applyResponse(
            liveDate = _chatMessages,
            viewModelScope = viewModelScope,
            dataFetch = {
                // Suspend until the bot response is fully received.
                val botResponse = llama.fetchChatResponse(jsonPayload)
                // Append the bot response to the conversation history.
                conversationHistory += "\nBot: $botResponse"
                botResponse
            }
        )
    }
}