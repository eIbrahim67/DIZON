package com.eibrahim.dizon.chatbot.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.chatbot.model.ChatMessage
import com.eibrahim.dizon.chatbot.model.ChatPayload
import com.eibrahim.dizon.core.local.Llama
import com.eibrahim.dizon.core.response.Response
import com.eibrahim.dizon.core.utils.UtilsFunctions.applyResponse
import com.google.gson.Gson

class ChatbotViewModel : ViewModel() {
    private val llama = Llama()
    private val _chatMessages = MutableLiveData<Response<String>>()
    val chatMessages: LiveData<Response<String>> get() = _chatMessages

    private val conversationHistory = mutableListOf<ChatMessage>()

    fun startChat(userMessage: String) {

        conversationHistory.add(ChatMessage(role = "user", content = userMessage))

        val payload = ChatPayload(messages = conversationHistory)
        val jsonPayload = Gson().toJson(payload)

        applyResponse(
            liveDate = _chatMessages,
            viewModelScope = viewModelScope,
            dataFetch = {
                val botResponse = llama.fetchChatResponse(jsonPayload)
                conversationHistory.add(ChatMessage(role = "assistant", content = botResponse))
                botResponse
            }
        )
    }
}