package com.eibrahim.dizon.propertyResults.api

import com.eibrahim.dizon.favorite.model.FavoritePropertiesResponse
import com.eibrahim.dizon.home.model.RecommendedPropertyResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ResultApi {

    @POST("/api/Favorites/{propertyId}")
    suspend fun addToFavorites(@Path("propertyId") propertyId: Int): Response<Unit>

    @GET("/api/Favorites")
    suspend fun getFavorites(): Response<FavoritePropertiesResponse>

    @GET("/api/Favorites/added/{propertyId}")
    suspend fun isPropertyInFavorites(@Path("propertyId") propertyId: Int): Response<Boolean>

    @DELETE("/api/Favorites/remove/{propertyId}")
    suspend fun removeFromFavorites(@Path("propertyId") propertyId: Int): Response<Unit>

}