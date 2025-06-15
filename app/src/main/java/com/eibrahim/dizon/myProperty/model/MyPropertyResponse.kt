package com.eibrahim.dizon.myProperty.model

import com.google.gson.annotations.SerializedName

data class MyPropertyResponse(
    @SerializedName("\$id") val id: String,
    @SerializedName("\$values") val values: List<Property>
)

data class Property(
    val propertyId: Int,
    val title: String,
    val description: String,
    val price: Double,
    val propertyType: String,
    val size: Double,
    val bedrooms: Int,
    val bathrooms: Int,
    val street: String,
    val city: String,
    val governate: String,
    val listedAt: String,
    val propertyImages: ImageList,
    val ownerInfo: OwnerInfo,
    val internalAmenities: AmenitiesList,
    val externalAmenities: AmenitiesList,
    val accessibilityAmenities: AmenitiesList
)

data class ImageList(
    @SerializedName("\$id") val id: String,
    @SerializedName("\$values") val values: List<String>
)

data class OwnerInfo(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String
)

data class AmenitiesList(
    @SerializedName("\$id") val id: String,
    @SerializedName("\$values") val values: List<String>
)