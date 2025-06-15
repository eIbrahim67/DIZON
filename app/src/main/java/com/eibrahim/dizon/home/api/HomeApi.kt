package com.eibrahim.dizon.home.api

import com.eibrahim.dizon.favorite.model.FavoritePropertiesResponse
import com.eibrahim.dizon.home.model.RecommendedPropertyResponse
import retrofit2.Response // Ensure this import is present
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeApi {


    @GET("/api/Properties/Recommend")
    suspend fun getRecommendedProperties(
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int
    ): Response<RecommendedPropertyResponse>

    @POST("/api/Favorites/{propertyId}")
    suspend fun addToFavorites(@Path("propertyId") propertyId: Int): Response<Unit>

    @GET("/api/Favorites")
    suspend fun getFavorites(): Response<FavoritePropertiesResponse>

    @GET("/api/Favorites/added/{propertyId}")
    suspend fun isPropertyInFavorites(@Path("propertyId") propertyId: Int): Response<Boolean>

    @DELETE("/api/Favorites/remove/{propertyId}")
    suspend fun removeFromFavorites(@Path("propertyId") propertyId: Int): Response<Unit>
}