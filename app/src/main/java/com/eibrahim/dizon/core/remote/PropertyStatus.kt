package com.eibrahim.dizon.core.remote

data class PropertyStatus(
    val property_type: String, // e.g., "House", "Home", "Apartment", "Condo", "Commercial"
    val condition: String,     // e.g., "New", "renovated", "needs repair"
    val status: String         // e.g., "Available", "Sold", "Under Offer"
)