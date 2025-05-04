package com.eibrahim.dizon.home.api

import com.eibrahim.dizon.home.model.RecommendedPropertyResponse
import retrofit2.Response // Ensure this import is present
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {
    @GET("/api/Properties/Recommend")
    suspend fun getRecommendedProperties(
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int
    ): Response<RecommendedPropertyResponse>


    @GET("/api/Favorites/")
    suspend fun addPropertyToFav(
        @Query("propertyId") propertyId: Int
    )
}