package com.eibrahim.dizon.chatbot.model

data class FunctionDefinition(
    val name: String,
    val parameters: Map<String, String>  // This represents a schema: key (parameter name) -> type (e.g., "string", "integer")
)