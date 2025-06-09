package com.eibrahim.dizon.addproperty.model

import java.io.File

data class AddPropertyData(
    val title: String,
    val description: String,
    val price: Double,
    val street: String,
    val city: String,
    val governate: String,
    val locationUrl: String,
    val propertyType: String,
    val listingType: String,
    val size: Int,
    val rooms: Int,
    val beds: Int,
    val bathrooms: Int,
    val imageFiles: List<File>,
    val internalAmenityIds: List<Int>,
    val externalAmenityIds: List<Int>,
    val accessibilityAmenityIds: List<Int>
)
