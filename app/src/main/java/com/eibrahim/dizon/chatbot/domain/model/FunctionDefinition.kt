package com.eibrahim.dizon.chatbot.domain.model

data class FunctionDefinition(
    val name: String,
    val parameters: Map<String, Any>  // This represents a schema: key (parameter name) -> type (e.g., "string", "integer")
)