package com.eibrahim.dizon.chatbot.domain.model


data class FilterParamsChatBot(
    val propertyType: String? = null,
    val city: String? = null,
    val bedrooms: Int? = null,
    val bathrooms: Int? = null,
    val size: Double? = null,
    val maxPrice: Int? = null,
    val minPrice: Int? = null,
    val internalAmenityIds: List<Int>? = null,
    val externalAmenityIds: List<Int>? = null,
    val accessibilityAmenityIds: List<Int>? = null
)

