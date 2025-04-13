package com.eibrahim.dizon.home.model

import java.io.Serializable

data class PropertyListing(
    val title: String,
    val imageUrl: String,
    val price: String,
    val description: String,
    val bedrooms: Int,
    val bathrooms: Int,
    val propertyType: String
) : Serializable
