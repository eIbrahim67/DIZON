package com.eibrahim.dizon.chatbot.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.chatbot.data.network.ChatLlamaStreamProcessor
import com.eibrahim.dizon.chatbot.domain.model.ChatMessage
import com.eibrahim.dizon.chatbot.domain.model.ChatPayload
import com.eibrahim.dizon.chatbot.domain.model.FunctionDefinition
import com.eibrahim.dizon.chatbot.domain.usecase.GetChatResponseUseCase
import com.eibrahim.dizon.core.response.Response
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatbotViewModel(
    private val getChatResponseUseCase: GetChatResponseUseCase

) : ViewModel() {

    private val _chatMessages = MutableLiveData<Response<ChatMessage>>()
    val chatMessages: LiveData<Response<ChatMessage>> get() = _chatMessages

    private val conversationHistory = mutableListOf<ChatMessage>()

    /**
     * Starts the chat session by updating the conversation history with the user's message,
     * ensuring the system prompt is included, building the payload (including function definitions),
     * and launching a coroutine to fetch the chat response.
     *
     * @param userMessage The new message provided by the user.
     */
    fun startChat(userMessage: String) {
        // Add user message to conversation history with named parameters.
        conversationHistory.add(
            ChatMessage(
                content = userMessage,
                role = "user",
                isFromUser = true
            )
        )

        // Define the system prompt if it hasn't been set yet.
        if (conversationHistory.none { it.role == "system" }) {
            val systemMessage = ChatMessage(
                role = "system",
                content = sysContent.trimIndent()
            )
            conversationHistory.add(0, systemMessage)
        }
        // Build the payload object with the conversation history and function definition.
        val payload = ChatPayload(
            messages = conversationHistory,
            functions = listOf(functionDefinition)
        )

        // Convert the payload to JSON.
        val jsonPayload = Gson().toJson(payload)

        // Launch a coroutine to call the use case and update the LiveData.
        viewModelScope.launch {
            // You can also handle intermediate states here (if needed) by updating LiveData.
            getChatResponseUseCase.execute(jsonPayload).collect { reponse ->
                _chatMessages.value = reponse
            }
        }
    }

    private val functionDefinition = FunctionDefinition(
        name = "search_properties", parameters = mapOf(
            "location" to mapOf(
                "country" to "string",
                "state" to "string",
                "city" to "string",
                "street_address" to "string"
            ),
            "property_type" to "string", // Allowed: "House", "Home", "Apartment", "Condo", "Commercial"
            "bedrooms" to mapOf(
                "min" to "integer or null", "max" to "integer or null"
            ),
            "bathrooms" to mapOf(
                "min" to "integer or null", "max" to "integer or null"
            ),
            "square_footage" to mapOf(
                "min" to "integer or null", "max" to "integer or null", "unit" to "string"
            ),
            "lot_size" to mapOf(
                "min" to "integer or null", "max" to "integer or null", "unit" to "string"
            ),
            "budget" to mapOf(
                "min" to "integer or null", "max" to "integer or null", "currency" to "string"
            ),
            "transaction" to "string", // Allowed: "buy", "rent"
            "property_status" to mapOf(
                "condition" to "string", // E.g., "New property", "renovated property", "a property needs repair"
                "status" to "string"     // E.g., "Available", "Sold", "Under Offer"
            ),
            "amenities" to mapOf(
                "interior" to "array of strings",
                "exterior" to "array of strings",
                "accessibility" to "array of strings"
            )
        )
    )
    private val sysContent = """
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

/*        applyResponse(
//            liveDate = _chatMessages,
//            viewModelScope = viewModelScope,
//            dataFetch = {
//                // This will fetch the response from your Llama endpoint
//                val botResponse = llama.fetchChatResponse(jsonPayload)
//
//                // Parse the response expecting a composite JSON with two keys: "message" and "search_properties"
//                try {
//                    val jsonObject = Gson().fromJson(botResponse, JsonObject::class.java)
//
//                    // First, extract the natural language message
//                    val naturalMessage = if (jsonObject.has("message")) {
//                        jsonObject.get("message").asString
//                    } else {
//                        // Fallback if "message" key is missing
//                        botResponse
//                    }
//                    // Add the natural language message to conversation history
//                    Log.e("test msg", naturalMessage)
//                    conversationHistory.add(
//                        ChatMessage(
//                            role = "assistant",
//                            content = naturalMessage,
//                            isFromUser = false
//                        )
//                    )
//
//                    // Next, check for the search_properties part
//                    if (jsonObject.has("search_properties")) {
//                        val spObject = jsonObject.getAsJsonObject("search_properties")
//                        // Ensure it contains the expected action key
//                        if (spObject.has("action") && spObject.get("action").asString == "search_properties") {
//                            val parameters = spObject.getAsJsonObject("parameters")
//                            // Extract search parameters (they might be empty or null)
//                            val location = parameters.get("location")?.asString.orEmpty()
//                            val propertyType = parameters.get("property_type")?.asString.orEmpty()
//                            val bedrooms =
//                                if (parameters.get("bedrooms")?.isJsonNull == true) null else parameters.get(
//                                    "bedrooms"
//                                )?.asInt
//                            val budget =
//                                if (parameters.get("budget")?.isJsonNull == true) null else parameters.get(
//                                    "budget"
//                                )?.asInt
//
//                            // Optionally, only perform a search if at least one search parameter is provided
//                            if (location.isNotEmpty() || propertyType.isNotEmpty() || (bedrooms != null && bedrooms > 0) || (budget != null && budget > 0)) {
//                                val results = searchProperties(
//                                    location,
//                                    propertyType,
//                                    bedrooms ?: 0,
//                                    budget ?: 0
//                                )
//                                // Append the search results as another assistant message
//                                val resultMessage = "Search Results:\n$results"
//                                conversationHistory.add(
//                                    ChatMessage(
//                                        role = "assistant",
//                                        content = resultMessage,
//                                        isFromUser = false
//                                    )
//                                )
//                            }
//                        }
//                    }
//                } catch (e: Exception) {
//                    // If parsing fails, treat the whole response as a regular message.
//                    conversationHistory.add(
//                        ChatMessage(
//                            role = "assistant",
//                            content = botResponse,
//                            isFromUser = false
//                        )
//                    )
//                }
//                botResponse
//            }
//        )

//    fun searchProperties(
//        location: String,
//        propertyType: String,
//        bedrooms: Int,
//        budget: Int
//    ): String {
//        Log.d(
//            "test prop",
//            "Found properties in $location: [Property1, Property2, ...] matching $bedrooms bedrooms under $$budget."
//        )
//        return "Found properties in $location: [Property1, Property2, ...] matching $bedrooms bedrooms under $$budget."
//    }

//                content = """
//                    You are Ebo, a friendly and knowledgeable property search assistant who communicates naturally like a human. Your primary role is to answer the user's questions in a conversational tone. Additionally, if the user's input contains property search criteria, extract these details into a structured JSON object. In every response, output a composite JSON object with two keys:
//
//                    "message": Your natural language reply to the user and should follow this format:
//                    message: "[your response message]"
//
//                    "search_properties": A JSON object in the following format:
//                    {
//                      "action": "search_properties",
//                      "parameters": {
//                        "location": {
//                          "country": "<string>",
//                          "state": "<string>",
//                          "city": "<string>",
//                          "street_address": "<string>"
//                        },
//                        "property_type": "<House, Home, Apartment, Condo, Commercial>",
//                        "bedrooms": {
//                          "min": <integer or null>,
//                          "max": <integer or null>
//                        },
//                        "bathrooms": {
//                          "min": <integer or null>,
//                          "max": <integer or null>
//                        },
//                        "square_footage": {
//                          "min": <integer or null>,
//                          "max": <integer or null>,
//                          "unit": "<string>"
//                        },
//                        "lot_size": {
//                          "min": <integer or null>,
//                          "max": <integer or null>,
//                          "unit": "<string>"
//                        },
//                        "budget": {
//                          "min": <integer or null>,
//                          "max": <integer or null>,
//                          "currency": "<string>"
//                        },
//                        "transaction": "<buy or rent>",
//                        "property_status": {
//                          "condition": "<New property, renovated property, a property needs repair>",
//                          "status": "<Available, Sold, Under Offer>"
//                        },
//                        "amenities": {
//                          "interior": ["<amenity1>", "<amenity2>", ...],
//                          "exterior": ["<amenity1>", "<amenity2>", ...],
//                          "accessibility": ["<amenity1>", "<amenity2>", ...]
//                        }
//                      }
//                    }
//
//                    If the user's message does not indicate an interest in searching for properties, set "search_properties" to:
//                    {
//                      "action": "search_properties",
//                      "parameters": {
//                        "location": {
//                          "country": "",
//                          "state": "",
//                          "city": "",
//                          "street_address": ""
//                        },
//                        "property_type": "",
//                        "bedrooms": {
//                          "min": null,
//                          "max": null
//                        },
//                        "bathrooms": {
//                          "min": null,
//                          "max": null
//                        },
//                        "square_footage": {
//                          "min": null,
//                          "max": null,
//                          "unit": ""
//                        },
//                        "lot_size": {
//                          "min": null,
//                          "max": null,
//                          "unit": ""
//                        },
//                        "budget": {
//                          "min": null,
//                          "max": null,
//                          "currency": ""
//                        },
//                        "transaction": "",
//                        "property_status": {
//                          "condition": "",
//                          "status": ""
//                        },
//                        "amenities": {
//                          "interior": [],
//                          "exterior": [],
//                          "accessibility": []
//                        }
//                      }
//                    }
//
//                    Always write your natural language reply to the user in the message section.
//                    Always output only valid JSON and do not include any extra commentary or additional JSON blocks.
//                    If it's time to indicate that property listings are available, simply include the brief message 'Please refer to the attached property options.' without generating detailed listings.
//
//                    Now, please respond accordingly to the following input:
//                    [User Input Here]
*/