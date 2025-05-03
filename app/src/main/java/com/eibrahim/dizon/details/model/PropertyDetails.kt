package com.eibrahim.dizon.details.model

import com.google.gson.annotations.SerializedName

data class PropertyDetails(
    val propertyId: Int,
    val title: String,
    val description: String,
    val locationUrl: String,
    val listingType: String,
    val price: Double,
    val bedrooms: Int,
    val bathrooms: Int,
    val propertyType: String,
    val listedAt: String,
    @SerializedName("imageUrls") val imageUrls: ImageUrls?, // Nullable
    val reviews: Reviews?, // Nullable
    val ownerInfo: OwnerInfo,
    val externalAmenities: Amenities?, // Nullable
    val internalAmenities: Amenities?, // Nullable
    val accessibilityAmenities: Amenities? // Nullable
)

data class ImageUrls(
    @SerializedName("\$values") val values: List<String>
)

data class Reviews(
    @SerializedName("\$values") val values: List<Any>
)

data class OwnerInfo(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String
)

data class Amenities(
    @SerializedName("\$values") val values: List<String>
)