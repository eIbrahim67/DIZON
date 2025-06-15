package com.eibrahim.dizon.details.model

import com.eibrahim.dizon.favorite.model.FavoritePropertiesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PropertyApi {

    @Multipart
    @POST("/api/Properties/Create")
    suspend fun createProperty(
        @Part("Title") title: RequestBody,
        @Part("Description") description: RequestBody,
        @Part("Price") price: RequestBody,
        @Part("PropertyType") propertyType: RequestBody,
        @Part("Size") size: RequestBody,
        @Part("Bedrooms") bedrooms: RequestBody,
        @Part("Bathrooms") bathrooms: RequestBody,
        @Part("Street") street: RequestBody,
        @Part("City") city: RequestBody,
        @Part("Governate") governate: RequestBody,
        @Part("LocationUrl") locationUrl: RequestBody,
        @Part("ListingType") listingType: RequestBody,
        @Part images: List<MultipartBody.Part>,
        @Part internalAmenityIds: List<MultipartBody.Part>,
        @Part externalAmenityIds: List<MultipartBody.Part>,
        @Part accessibilityAmenityIds: List<MultipartBody.Part>
    ): Response<Unit>

    @GET("/api/Amenties/internal")
    suspend fun getInternalAmenities(): Response<AmenitiesResponse>

    @GET("/api/Amenties/external")
    suspend fun getExternalAmenities(): Response<AmenitiesResponse>

    @GET("/api/Amenties/accessibility")
    suspend fun getAccessibilityAmenities(): Response<AmenitiesResponse>

    @GET("/api/Properties/GetById/{id}")
    suspend fun getPropertyById(@Path("id") id: Int): Response<PropertyDetails>

    @POST("/api/Favorites/{propertyId}")
    suspend fun addToFavorites(@Path("propertyId") propertyId: Int): Response<Unit>

    @GET("/api/Favorites")
    suspend fun getFavorites(): Response<FavoritePropertiesResponse>

    @GET("/api/Favorites/added/{propertyId}")
    suspend fun isPropertyInFavorites(@Path("propertyId") propertyId: Int): Response<Boolean>

    @DELETE("/api/Favorites/remove/{propertyId}")
    suspend fun removeFromFavorites(@Path("propertyId") propertyId: Int): Response<Unit>

    @POST("/api/Payment/create-or-update-payment-intent")
    suspend fun fetchPaymentIntentClientSecret(@Body request: PaymentIntentRequest): PaymentIntentResponse


}

data class AmenitiesResponse(
    val `$id`: String,
    val `$values`: List<Amenity>
)

data class Amenity(
    val `$id`: String,
    val amenityId: Int,
    val name: String
)

data class PaymentIntentRequest(
    val currency: String,
    val paymentIntentId: String? = null // Optional, as it may not always be required
)

data class PaymentIntentResponse(
    val clientSecret: String
)