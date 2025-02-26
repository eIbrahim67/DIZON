package com.eibrahim.dizon.core.remote

data class SearchPropertiesParameters(
    val country: String,
    val state: String,
    val city: String,
    val street_address: String,
    val bedrooms: Int?,   // nullable, if not provided
    val bathrooms: Int?,  // nullable, if not provided
    val budget: Int?,     // nullable, if not provided
    val transaction: String, // "buy" or "rent"
    val property_status: PropertyStatus,
    val amenities: Amenities
)