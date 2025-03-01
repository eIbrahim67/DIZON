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
        conversationHistory.add(ChatMessage(content = userMessage, "user", true))
        // Define the system prompt if it hasn't been set yet
        if (conversationHistory.none { it.role == "system" }) {
            val systemMessage = ChatMessage(
                role = "system",
                content = """
                    You are Ebo, a friendly and knowledgeable property search assistant who communicates naturally like a human. Your primary role is to answer the user's questions in a conversational tone. Additionally, if the user's input contains property search criteria, extract these details into a structured JSON object. In every response, output a composite JSON object with two keys:

                    "message": Your natural language reply to the user and should follow this format:
                    message: "[your response message]"

                    "search_properties": A JSON object in the following format:
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
                        "bedrooms": {
                          "min": <integer or null>,
                          "max": <integer or null>
                        },
                        "bathrooms": {
                          "min": <integer or null>,
                          "max": <integer or null>
                        },
                        "square_footage": {
                          "min": <integer or null>,
                          "max": <integer or null>,
                          "unit": "<string>"
                        },
                        "lot_size": {
                          "min": <integer or null>,
                          "max": <integer or null>,
                          "unit": "<string>"
                        },
                        "budget": {
                          "min": <integer or null>,
                          "max": <integer or null>,
                          "currency": "<string>"
                        },
                        "transaction": "<buy or rent>",
                        "property_status": {
                          "condition": "<New property, renovated property, a property needs repair>",
                          "status": "<Available, Sold, Under Offer>"
                        },
                        "amenities": {
                          "interior": ["<amenity1>", "<amenity2>", ...],
                          "exterior": ["<amenity1>", "<amenity2>", ...],
                          "accessibility": ["<amenity1>", "<amenity2>", ...]
                        }
                      }
                    }

                    If the user's message does not indicate an interest in searching for properties, set "search_properties" to:
                    {
                      "action": "search_properties",
                      "parameters": {
                        "location": {
                          "country": "",
                          "state": "",
                          "city": "",
                          "street_address": ""
                        },
                        "property_type": "",
                        "bedrooms": {
                          "min": null,
                          "max": null
                        },
                        "bathrooms": {
                          "min": null,
                          "max": null
                        },
                        "square_footage": {
                          "min": null,
                          "max": null,
                          "unit": ""
                        },
                        "lot_size": {
                          "min": null,
                          "max": null,
                          "unit": ""
                        },
                        "budget": {
                          "min": null,
                          "max": null,
                          "currency": ""
                        },
                        "transaction": "",
                        "property_status": {
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

                    Always write your natural language reply to the user in the message section.
                    Always output only valid JSON and do not include any extra commentary or additional JSON blocks.
                    If it's time to indicate that property listings are available, simply include the brief message 'Please refer to the attached property options.' without generating detailed listings.

                    Now, please respond accordingly to the following input:
                    [User Input Here]
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
                            content = naturalMessage,
                            isFromUser = false
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
                                        content = resultMessage,
                                        isFromUser = false
                                    )
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    // If parsing fails, treat the whole response as a regular message.
                    conversationHistory.add(
                        ChatMessage(
                            role = "assistant",
                            content = botResponse,
                            isFromUser = false
                        )
                    )
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