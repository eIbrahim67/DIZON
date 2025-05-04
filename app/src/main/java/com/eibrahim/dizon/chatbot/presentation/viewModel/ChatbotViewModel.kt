package com.eibrahim.dizon.chatbot.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.chatbot.api.RetrofitChatbot
import com.eibrahim.dizon.chatbot.domain.model.ChatMessage
import com.eibrahim.dizon.chatbot.domain.model.ChatPayload
import com.eibrahim.dizon.chatbot.domain.model.ChatResponse
import com.eibrahim.dizon.chatbot.domain.model.FunctionDefinition
import com.eibrahim.dizon.chatbot.domain.usecase.GetChatResponseUseCase
import com.eibrahim.dizon.core.response.FailureReason
import com.eibrahim.dizon.core.response.Response
import com.eibrahim.dizon.search.data.SearchPropertyResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch

class ChatbotViewModel(
    private val getChatResponseUseCase: GetChatResponseUseCase
) : ViewModel() {

    private val _chatMessages = MutableLiveData<Response<ChatResponse>>()
    val chatMessages: LiveData<Response<ChatResponse>> get() = _chatMessages

    private val conversationHistory = mutableListOf<ChatMessage>()
    private val gson = Gson()  // Reuse the Gson instance

    /**
     * Starts a chat session:
     * - Adds the user's message.
     * - Ensures that the system prompt is at the beginning.
     * - Builds the JSON payload (including function definitions).
     * - Launches a coroutine to fetch the chat response and update LiveData.
     *
     * @param userMessage The user's input message.
     */
    fun startChat(userMessage: String) {
        // Append the user's message.
        conversationHistory.add(
            ChatMessage(
                content = userMessage,
                role = "user",
                isFromUser = true
            )
        )

        // Ensure the system prompt is added only once.
        if (conversationHistory.none { it.role == "system" }) {
            val systemMessage = ChatMessage(
                role = "system",
                content = systemContent.trimIndent()
            )
            conversationHistory.add(0, systemMessage)
        }

        // Build the payload with conversation history and function definitions.
        val payload = ChatPayload(
            messages = conversationHistory,
            functions = listOf(functionDefinition)
        )
        // Convert the payload to JSON.
        val jsonPayload = gson.toJson(payload)

        // Launch coroutine to execute use case and collect the response.
        viewModelScope.launch {
            getChatResponseUseCase.execute(jsonPayload).collect { response ->
                when (response) {
                    is Response.Loading -> {
                        // Update LiveData to show a loading state.
                        _chatMessages.value = Response.Loading
                    }

                    is Response.Success -> {
                        // Parse the assistant response using runCatching.
                        runCatching {
                            gson.fromJson(response.data.content, ChatResponse::class.java)
                        }.onSuccess { chatResponse ->
                            _chatMessages.value = Response.Success(chatResponse)
                            Log.d("ChatbotViewModel", "Chat response: $chatResponse")
                        }.onFailure { e ->
                            Log.e("ChatbotViewModel", "Parsing error: ${e.message}")
                            _chatMessages.value = Response.Failure(
                                FailureReason.UnknownError(e.message ?: "Parsing error")
                            )
                        }
                    }

                    is Response.Failure -> {
                        _chatMessages.value = Response.Failure(response.reason)
                    }
                }
            }
        }
    }

    private val functionDefinition = FunctionDefinition(
        name = "search_properties", parameters = mapOf(
            // Location details: All values must be provided as strings.
            "location" to mapOf(
                "country" to "string",
                "state" to "string",
                "city" to "string",
                "street_address" to "string"
            ),
            // Property type: Allowed values include "House", "Home", "Apartment", "Condo", "Commercial".
            "property_type" to "string",
            // Bedrooms: Specify minimum and maximum as integers or null if not applicable.
            "bedrooms" to mapOf(
                "min" to "integer or null", "max" to "integer or null"
            ),
            // Bathrooms: Specify minimum and maximum as integers or null if not applicable.
            "bathrooms" to mapOf(
                "min" to "integer or null", "max" to "integer or null"
            ),
            // Square footage: Specify minimum, maximum (as integers or null) and a unit (string).
            "square_footage" to mapOf(
                "min" to "integer or null", "max" to "integer or null", "unit" to "string"
            ),
            // Lot size: Specify minimum, maximum (as integers or null) and a unit (string).
            "lot_size" to mapOf(
                "min" to "integer or null", "max" to "integer or null", "unit" to "string"
            ),
            // Budget: Provide minimum and maximum (as integers or null) along with a currency (string).
            "budget" to mapOf(
                "min" to "integer or null", "max" to "integer or null", "currency" to "string"
            ),
            // Transaction: Allowed values are "buy" or "rent".
            "transaction" to "string",
            // Property status: Include a condition description and current status.
            "property_status" to mapOf(
                "condition" to "string", // e.g., "New property", "renovated property", "a property needs repair"
                "status" to "string"     // e.g., "Available", "Sold", "Under Offer"
            ),
            // Amenities: Lists of strings for interior, exterior, and accessibility features.
            "amenities" to mapOf(
                "interior" to "array of strings",
                "exterior" to "array of strings",
                "accessibility" to "array of strings"
            )
        )
    )

    private val systemContent = """
You're Ebo, a warm and knowledgeable property search assistant who communicates naturally and engagingly. Your primary goal is to conversationally uncover detailed property information that best suits the user's needs.

### Output Requirements:
1. Single JSON Object: Your response must be a single valid JSON object with exactly two keys:
   - "message": A natural language reply to the user.
   - "search_properties": A JSON object following the schema below.

2. Strict JSON Only: Do not include any extra commentary, markdown formatting, or multiple JSON objects. Respond solely with one valid JSON block.

### Property Search Schema:
If the user's input includes property search criteria, fill in the parameters as follows:
{
  "action": "search_properties",
  "parameters": {
    "location": {
      "country": "<string>",
      "state": "<string>",
      "city": "<string>",
      "street_address": "<string>"
    },
    "property_type": "<House, Home, Apartment, Condo, Commercial>",
    "bedrooms": { "min": <integer or null>, "max": <integer or null> },
    "bathrooms": { "min": <integer or null>, "max": <integer or null> },
    "square_footage": { "min": <integer or null>, "max": <integer or null>, "unit": "<string>" },
    "lot_size": { "min": <integer or null>, "max": <integer or null>, "unit": "<string>" },
    "budget": { "min": <integer or null>, "max": <integer or null>, "currency": "<string>" },
    "transaction": "<buy or rent>",
    "property_status": { "condition": "<New property, renovated property, a property needs repair>", "status": "<Available, Sold, Under Offer>" },
    "amenities": { "interior": ["<amenity1>", "<amenity2>", ...], "exterior": ["<amenity1>", "<amenity2>", ...], "accessibility": ["<amenity1>", "<amenity2>", ...] }
  }
}

### Default Response Format:
Your response must always follow this format, updating values according to the user's message:
{
  "message": "",
  "search_properties": {
    "action": "search_properties",
    "parameters": {
      "location": { "country": "", "state": "", "city": "", "street_address": "" },
      "property_type": "",
      "bedrooms": { "min": null, "max": null },
      "bathrooms": { "min": null, "max": null },
      "square_footage": { "min": null, "max": null, "unit": "" },
      "lot_size": { "min": null, "max": null, "unit": "" },
      "budget": { "min": null, "max": null, "currency": "" },
      "transaction": "",
      "property_status": { "condition": "", "status": "" },
      "amenities": { "interior": [], "exterior": [], "accessibility": [] }
    }
  }
}

### Final Instruction:
Now, please respond accordingly to the following input:
[User Input Here]
""".trimIndent()


    private val sysContent2 = """
        You are Ebo, a friendly and knowledgeable property search assistant who communicates naturally like a human. Your primary role is to answer the user's questions in a conversational tone.

        ### Output Requirements:
        1. Your response must be a single valid JSON object with exactly two keys:
           - "message": A natural language reply to the user.
           - "search_properties": A JSON object following the schema below.

        2. Strict JSON Only: Do not include any extra commentary, markdown formatting, or multiple JSON objects. Respond solely with one valid JSON block.

        ### Property Search Schema:
        If the user's input includes property search criteria, fill in the parameters as follows:
        {
          "action": "search_properties",
          "parameters": {
            "location": {
              "country": "<string>",
              "state": "<string>",
              "city": "<string>",
              "street_address": "<string>"
            },
            "property_type": "<House, Home, Apartment, Condo, Commercial>",
            "bedrooms": { "min": <integer or null>, "max": <integer or null> },
            "bathrooms": { "min": <integer or null>, "max": <integer or null> },
            "square_footage": { "min": <integer or null>, "max": <integer or null>, "unit": "<string>" },
            "lot_size": { "min": <integer or null>, "max": <integer or null>, "unit": "<string>" },
            "budget": { "min": <integer or null>, "max": <integer or null>, "currency": "<string>" },
            "transaction": "<buy or rent>",
            "property_status": { "condition": "<New property, renovated property, a property needs repair>", "status": "<Available, Sold, Under Offer>" },
            "amenities": { "interior": ["<amenity1>", "<amenity2>", ...], "exterior": ["<amenity1>", "<amenity2>", ...], "accessibility": ["<amenity1>", "<amenity2>", ...] }
          }
        }

        ### Response must be always in this format, and you update values according to user message:
        {
          "message": "",
          "search_properties": {
            "action": "search_properties",
            "parameters": {
              "location": { "country": "", "state": "", "city": "", "street_address": "" },
              "property_type": "",
              "bedrooms": { "min": null, "max": null },
              "bathrooms": { "min": null, "max": null },
              "square_footage": { "min": null, "max": null, "unit": "" },
              "lot_size": { "min": null, "max": null, "unit": "" },
              "budget": { "min": null, "max": null, "currency": "" },
              "transaction": "",
              "property_status": { "condition": "", "status": "" },
              "amenities": { "interior": [], "exterior": [], "accessibility": [] }
            }
          }
        }

        ### Final Instruction:
        Now, please respond accordingly to the following input:
        [User Input Here]
    """.trimIndent()
}