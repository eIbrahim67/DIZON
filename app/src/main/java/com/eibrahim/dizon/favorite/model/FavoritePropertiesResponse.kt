package com.eibrahim.dizon.favorite.model

import com.google.gson.annotations.SerializedName

data class FavoritePropertiesResponse(
    @SerializedName("\$id") val id: String,
    @SerializedName("\$values") val values: List<FavoriteProperty>
)