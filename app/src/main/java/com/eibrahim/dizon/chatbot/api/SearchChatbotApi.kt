package com.eibrahim.dizon.chatbot.api

import com.eibrahim.dizon.search.data.SearchPropertyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchChatbotApi {
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
}