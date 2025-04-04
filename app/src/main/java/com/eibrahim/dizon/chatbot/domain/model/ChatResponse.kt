package com.eibrahim.dizon.chatbot.domain.model

data class ChatResponse(
    val message: String,  // This key may be absent if you're only extracting search_properties
    val search_properties: SearchProperties
)

data class SearchProperties(
    val action: String,
    val parameters: SearchParameters
)

data class SearchParameters(
    val location: Location,
    val property_type: String,
    val bedrooms: Range,
    val bathrooms: Range,
    val square_footage: RangeWithUnit,
    val lot_size: RangeWithUnit,
    val budget: RangeWithCurrency,
    val transaction: String,
    val property_status: PropertyStatus,
    val amenities: Amenities,
    val displaySearchResults: Boolean
)

data class Location(
    val country: String,
    val state: String,
    val city: String,
    val street_address: String
)

data class Range(
    val min: Int?,
    val max: Int?
)

data class RangeWithUnit(
    val min: Int?,
    val max: Int?,
    val unit: String
)

data class RangeWithCurrency(
    val min: Int?,
    val max: Int?,
    val currency: String
)

data class PropertyStatus(
    val condition: String,
    val status: String
)

data class Amenities(
    val interior: List<String>,
    val exterior: List<String>,
    val accessibility: List<String>
)
