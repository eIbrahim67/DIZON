package com.eibrahim.dizon.chatbot.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.chatbot.model.ChatMessage
import com.eibrahim.dizon.chatbot.model.ChatPayload
import com.eibrahim.dizon.chatbot.model.FunctionDefinition
import com.eibrahim.dizon.core.local.Llama
import com.eibrahim.dizon.core.response.Response
import com.eibrahim.dizon.core.utils.UtilsFunctions.applyResponse
import com.google.gson.Gson
import com.google.gson.JsonObject

class ChatbotViewModel : ViewModel() {
    private val llama = Llama()
    private val _chatMessages = MutableLiveData<Response<String>>()
    val chatMessages: LiveData<Response<String>> get() = _chatMessages

    private val conversationHistory = mutableListOf<ChatMessage>()

    fun startChat(userMessage: String) {
        // Add user message to conversation history
        conversationHistory.add(ChatMessage(role = "user", content = userMessage))
        Log.d("teto", conversationHistory.toString())
        // Define the system prompt if it hasn't been set yet
        if (conversationHistory.none { it.role == "system" }) {
            val systemMessage = ChatMessage(
                role = "system",
                content = """
                    You are Ebo, a friendly and knowledgeable property search assistant who communicates naturally like a human. Your primary role is to answer the user's questions in a conversational tone. Additionally, if the user's input contains property search criteria, extract these details into a structured JSON object. In every response, output a composite JSON object with two keys:

                    1. "message": Your natural language reply to the user.
                    2. "search_properties": A JSON object in the following format:
                    
                    {
                      "action": "search_properties",
                      "parameters": {
                        "country": "<string>",
                        "state": "<string>",
                        "city": "<string>",
                        "street_address": "<string>",
                        "bedrooms": <integer or null>,
                        "bathrooms": <integer or null>,
                        "budget": <integer or null>,
                        "transaction": "<buy or rent>",
                        "property_status": {
                          "property_type": "<House, Home, Apartment, Condo, Commercial>",
                          "condition": "<New property, renovated property, a property needs repair>",
                          "status": "<Available, Sold, Under Offer>"
                        },
                        "amenities": {
                          "interior": [
                            "Air conditioning",
                            "Central heating",
                            "Fireplace",
                            "Hardwood or premium flooring",
                            "Built-in kitchen appliances (oven, refrigerator, dishwasher)",
                            "High-speed internet connectivity",
                            "Energy-efficient windows and insulation",
                            "Modern lighting fixtures"
                          ],
                          "exterior": [
                            "Swimming pool",
                            "Garage (with capacity specification)",
                            "Private garden",
                            "Patio or deck area",
                            "Balcony",
                            "Driveway",
                            "Outdoor lighting",
                            "Security system (alarm, cameras)",
                            "Fenced yard"
                          ],
                          "accessibility": [
                            "Wheelchair accessibility",
                            "Ramps or specialized doorways"
                          ]
                        }
                      }
                    }

                    For queries that do not involve property searches, output the natural conversation reply in "message" and use empty values for "search_properties" as follows:
                    
                    {
                      "action": "search_properties",
                      "parameters": {
                        "country": "",
                        "state": "",
                        "city": "",
                        "street_address": "",
                        "bedrooms": null,
                        "bathrooms": null,
                        "budget": null,
                        "transaction": "",
                        "property_status": {
                          "property_type": "",
                          "condition": "",
                          "status": ""
                        },
                        "amenities": {
                          "interior": [],
                          "exterior": [],
                          "accessibility": []
                        }
                      }
                    }
                    
                    Note: Always output only valid JSON and do not include any extra commentary or additional JSON blocks. Your mission is to simply fetch and extract search data when available, while engaging naturally with the user.
                    Note: When it's time to indicate that property listings are available, do not generate detailed listings yourself. Instead, simply include a brief message such as: 'Please refer to the attached property options.' This signals that the system will automatically present the relevant property suggestions, so you don't need to provide any further details.
        """.trimIndent()

            )
            if (conversationHistory.none { it.role == "system" }) {
                conversationHistory.add(0, systemMessage)
            }
        }

        // Define the function you want Llama to call when needed
        val functionDefinition = FunctionDefinition(
            name = "search_properties",
            parameters = mapOf(
                "location" to "string",
                "property_type" to "string",
                "bedrooms" to "integer",
                "budget" to "integer"
            )
        )

        // Build the payload including conversation and function definitions
        val payload = ChatPayload(
            messages = conversationHistory,
            functions = listOf(functionDefinition)
        )
        val jsonPayload = Gson().toJson(payload)

        // Call Llama and process its response
        // Call Llama and process its response
        applyResponse(
            liveDate = _chatMessages,
            viewModelScope = viewModelScope,
            dataFetch = {
                // This will fetch the response from your Llama endpoint
                val botResponse = llama.fetchChatResponse(jsonPayload)

                // Parse the response expecting a composite JSON with two keys: "message" and "search_properties"
                try {
                    val jsonObject = Gson().fromJson(botResponse, JsonObject::class.java)

                    // First, extract the natural language message
                    val naturalMessage = if (jsonObject.has("message")) {
                        jsonObject.get("message").asString
                    } else {
                        // Fallback if "message" key is missing
                        botResponse
                    }
                    // Add the natural language message to conversation history
                    Log.e("test msg", naturalMessage)
                    conversationHistory.add(
                        ChatMessage(
                            role = "assistant",
                            content = naturalMessage
                        )
                    )

                    // Next, check for the search_properties part
                    if (jsonObject.has("search_properties")) {
                        val spObject = jsonObject.getAsJsonObject("search_properties")
                        // Ensure it contains the expected action key
                        if (spObject.has("action") && spObject.get("action").asString == "search_properties") {
                            val parameters = spObject.getAsJsonObject("parameters")
                            // Extract search parameters (they might be empty or null)
                            val location = parameters.get("location")?.asString.orEmpty()
                            val propertyType = parameters.get("property_type")?.asString.orEmpty()
                            val bedrooms =
                                if (parameters.get("bedrooms")?.isJsonNull == true) null else parameters.get(
                                    "bedrooms"
                                )?.asInt
                            val budget =
                                if (parameters.get("budget")?.isJsonNull == true) null else parameters.get(
                                    "budget"
                                )?.asInt

                            // Optionally, only perform a search if at least one search parameter is provided
                            if (location.isNotEmpty() || propertyType.isNotEmpty() || (bedrooms != null && bedrooms > 0) || (budget != null && budget > 0)) {
                                val results = searchProperties(
                                    location,
                                    propertyType,
                                    bedrooms ?: 0,
                                    budget ?: 0
                                )
                                // Append the search results as another assistant message
                                val resultMessage = "Search Results:\n$results"
                                conversationHistory.add(
                                    ChatMessage(
                                        role = "assistant",
                                        content = resultMessage
                                    )
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    // If parsing fails, treat the whole response as a regular message.
                    conversationHistory.add(ChatMessage(role = "assistant", content = botResponse))
                }
                botResponse
            }
        )
    }

    fun searchProperties(
        location: String,
        propertyType: String,
        bedrooms: Int,
        budget: Int
    ): String {
        Log.d(
            "test prop",
            "Found properties in $location: [Property1, Property2, ...] matching $bedrooms bedrooms under $$budget."
        )
        return "Found properties in $location: [Property1, Property2, ...] matching $bedrooms bedrooms under $$budget."
    }

}