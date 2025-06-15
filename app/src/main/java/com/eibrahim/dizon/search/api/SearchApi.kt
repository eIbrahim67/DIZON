package com.eibrahim.dizon.search.api

import com.eibrahim.dizon.favorite.model.FavoritePropertiesResponse
import com.eibrahim.dizon.search.data.SearchPropertyResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchApi {
    @GET("/api/Properties/GetAll")
    suspend fun getAllProperties(
        @Query("PropertyType") propertyType: String?,
        @Query("City") city: String?,
        @Query("Governate") governate: String?,
        @Query("Bedrooms") bedrooms: Int?,
        @Query("Bathrooms") bathrooms: Int?,
        @Query("SortBy") sortBy: String?,
        @Query("Size") size: Double?,
        @Query("MaxPrice") maxPrice: Double?,
        @Query("MinPrice") minPrice: Double?,
        @Query("InternalAmenityIds") internalAmenityIds: List<Int>?,
        @Query("ExternalAmenityIds") externalAmenityIds: List<Int>?,
        @Query("AccessibilityAmenityIds") accessibilityAmenityIds: List<Int>?,
        @Query("PageSize") pageSize: Int,
        @Query("PageIndex") pageIndex: Int
    ): Response<SearchPropertyResponse>


    @POST("/api/Favorites/{propertyId}")
    suspend fun addToFavorites(@Path("propertyId") propertyId: Int): Response<Unit>

    @GET("/api/Favorites")
    suspend fun getFavorites(): Response<FavoritePropertiesResponse>

    @GET("/api/Favorites/added/{propertyId}")
    suspend fun isPropertyInFavorites(@Path("propertyId") propertyId: Int): Response<Boolean>

    @DELETE("/api/Favorites/remove/{propertyId}")
    suspend fun removeFromFavorites(@Path("propertyId") propertyId: Int): Response<Unit>

}