package com.eibrahim.dizon.addproperty.viewmodel

data class Property(
    val propertyId: String,
    val ownerId: String,
    val title: String,
    val description: String,
    val price: Double,
    val location: String,
    val type: String, // "For Sale" or "For Rent"
    val size: Int,
    val rooms: Int,
    val beds: Int,
    val bathrooms: Int,
    val amenities: List<String>,
    val images: List<String>
)
