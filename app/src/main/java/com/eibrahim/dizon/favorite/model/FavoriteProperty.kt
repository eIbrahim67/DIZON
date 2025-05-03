package com.eibrahim.dizon.favorite.model

import com.google.gson.annotations.SerializedName

data class FavoriteProperty(
    @SerializedName("propertyId") val propertyId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("mainImageUrl") val imageUrl: String?,
    @SerializedName("city") val city: String,
    @SerializedName("governate") val governate: String,
    @SerializedName("price") val price: Double,
    @SerializedName("listingType") val listingType: String,
    @SerializedName("addedToFavoritesAt") val addedToFavoritesAt: String? = null
)