package com.eibrahim.dizon.addproperty.viewmodel

data class Property(
    val title: String,
    val description: String,
    val price: Double,
    val location: String,
    val locationUrl: String,
    val propertyType: String, // e.g., "Apartment", "House"
    val listingType: String, // e.g., "For Sale", "For Rent"
    val size: Int,
    val beds: Int,
    val bathrooms: Int
)