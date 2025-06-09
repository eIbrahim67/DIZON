package com.eibrahim.dizon.chatbot.domain.model

object ChatbotViewModelConst{
    const val TAG = "ChatbotViewModel"
    val FUNCTION_DEFINITION = FunctionDefinition(
        name = "search_properties",
        parameters = mapOf(
            "location" to mapOf(
                "country" to "string",
                "state" to "string",
                "city" to "string",
                "street_address" to "string"
            ),
            "property_type" to "string",
            "bedrooms" to mapOf("min" to "integer or null", "max" to "integer or null"),
            "bathrooms" to mapOf("min" to "integer or null", "max" to "integer or null"),
            "square_footage" to mapOf(
                "min" to "integer or null",
                "max" to "integer or null",
                "unit" to "string"
            ),
            "lot_size" to mapOf(
                "min" to "integer or null",
                "max" to "integer or null",
                "unit" to "string"
            ),
            "budget" to mapOf(
                "min" to "integer or null",
                "max" to "integer or null",
                "currency" to "string"
            ),
            "transaction" to "string",
            "property_status" to mapOf(
                "condition" to "string",
                "status" to "string"
            ),
            "amenities" to mapOf(
                "interior" to "array of strings",
                "exterior" to "array of strings",
                "accessibility" to "array of strings"
            )
        )
    )

    val SYSTEM_PROMPT = """
            You are Ebo, a friendly and knowledgeable property search assistant who communicates naturally. 
            Your goal is to uncover detailed property information that best suits the user's needs.

            ### Output Requirements:
            1. Single JSON Object: Respond with a single valid JSON object containing:
               - "message": A natural language reply to the user.
               - "search_properties": A JSON object following the schema below.
            2. Strict JSON Only: Do not include extra commentary, markdown, or multiple JSON objects.

            ### Property Search Schema:
            {
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

            ### Final Instruction:
            Respond to the user's input with a single JSON object, updating values as needed.
        """.trimIndent()
}