package com.eibrahim.dizon.chatbot.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.core.local.Llama
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatbotViewModel : ViewModel() {
    private val llama = Llama()
    private val _chatMessages = MutableLiveData<String>()
    val chatMessages: LiveData<String> get() = _chatMessages

    fun startChat(message: String) {
        // Define your JSON payload (adjust to include chat history as needed)
        val jsonPayload = """
            {
                "messages": [
                    {"role": "user", "content": "${message}"}
                ]
            }
        """.trimIndent()

        llama.getChatStream(jsonPayload,
            onMessageReceived = { message ->
                viewModelScope.launch(Dispatchers.Main) {
                    // Append the incoming message to the current text.
                    _chatMessages.value = message
                }
            },
            onError = { e ->
                viewModelScope.launch(Dispatchers.Main) {
                    _chatMessages.value = "Error: ${e.message}"
                }
            }
        )
    }
}