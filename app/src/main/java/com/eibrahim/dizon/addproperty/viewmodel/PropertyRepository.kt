package com.eibrahim.dizon.addproperty.viewmodel

import android.util.Log
import com.eibrahim.dizon.auth.api.RetrofitClient
import com.eibrahim.dizon.details.model.AmenitiesResponse
import com.eibrahim.dizon.details.model.PropertyApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class PropertyRepository {

    private val propertyApi: PropertyApi = RetrofitClient.propertyApi

    suspend fun addProperty(
        property: Property,
        imageFiles: List<File>,
        internalAmenityIds: List<Int>,
        externalAmenityIds: List<Int>,
        accessibilityAmenityIds: List<Int>
    ): Result<Unit> {
        return try {
            // Prepare request bodies
            val title = property.title.toRequestBody("text/plain".toMediaTypeOrNull())
            val description = property.description.toRequestBody("text/plain".toMediaTypeOrNull())
            val price = property.price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val propertyType = property.propertyType.toRequestBody("text/plain".toMediaTypeOrNull())
            val size = property.size.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val bedrooms = property.beds.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val bathrooms = property.bathrooms.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val locationParts = property.location.split(",").map { it.trim() }
            if (locationParts.size != 3) {
                throw IllegalArgumentException("Location must be in format: Street, City, Governate")
            }
            val street = locationParts[0].toRequestBody("text/plain".toMediaTypeOrNull())
            val city = locationParts[1].toRequestBody("text/plain".toMediaTypeOrNull())
            val governate = locationParts[2].toRequestBody("text/plain".toMediaTypeOrNull())
            val locationUrl = property.locationUrl.toRequestBody("text/plain".toMediaTypeOrNull())
            val listingType = property.listingType.toRequestBody("text/plain".toMediaTypeOrNull())

            // Prepare images
            val imageParts = imageFiles.map { file ->
                val requestFile = file.readBytes().toRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("Images", file.name, requestFile)
            }

            // Prepare amenity IDs as List<MultipartBody.Part>
            val internalAmenityParts = internalAmenityIds.map { id ->
                val requestBody = id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("InternalAmenityIds", null, requestBody)
            }
            val externalAmenityParts = externalAmenityIds.map { id ->
                val requestBody = id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("ExternalAmenityIds", null, requestBody)
            }
            val accessibilityAmenityParts = accessibilityAmenityIds.map { id ->
                val requestBody = id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("AccessibilityAmenityIds", null, requestBody)
            }

            // Log request data
            Log.d("PropertyRepository", "Sending request with data:")
            Log.d("PropertyRepository", "Title: ${property.title}")
            Log.d("PropertyRepository", "Description: ${property.description}")
            Log.d("PropertyRepository", "Price: ${property.price}")
            Log.d("PropertyRepository", "PropertyType: ${property.propertyType}")
            Log.d("PropertyRepository", "Size: ${property.size}")
            Log.d("PropertyRepository", "Bedrooms: ${property.beds}")
            Log.d("PropertyRepository", "Bathrooms: ${property.bathrooms}")
            Log.d("PropertyRepository", "Street: ${locationParts[0]}")
            Log.d("PropertyRepository", "City: ${locationParts[1]}")
            Log.d("PropertyRepository", "Governate: ${locationParts[2]}")
            Log.d("PropertyRepository", "LocationUrl: ${property.locationUrl}")
            Log.d("PropertyRepository", "ListingType: ${property.listingType}")
            Log.d("PropertyRepository", "Images: ${imageFiles.map { it.name }}")
            Log.d("PropertyRepository", "InternalAmenityIds: $internalAmenityIds")
            Log.d("PropertyRepository", "ExternalAmenityIds: $externalAmenityIds")
            Log.d("PropertyRepository", "AccessibilityAmenityIds: $accessibilityAmenityIds")

            // Call API
            val response = propertyApi.createProperty(
                title = title,
                description = description,
                price = price,
                propertyType = propertyType,
                size = size,
                bedrooms = bedrooms,
                bathrooms = bathrooms,
                street = street,
                city = city,
                governate = governate,
                locationUrl = locationUrl,
                listingType = listingType,
                images = imageParts,
                internalAmenityIds = internalAmenityParts,
                externalAmenityIds = externalAmenityParts,
                accessibilityAmenityIds = accessibilityAmenityParts
            )

            if (response.isSuccessful) {
                Log.d("PropertyRepository", "Property added successfully")
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("PropertyRepository", "Failed to add property: ${response.message()} - $errorBody")
                Result.failure(Exception("Failed to add property: ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error adding property: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getInternalAmenities(): Result<AmenitiesResponse> {
        return try {
            val response = propertyApi.getInternalAmenities()
            if (response.isSuccessful) {
                Log.d("PropertyRepository", "Internal amenities fetched successfully")
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("PropertyRepository", "Failed to fetch internal amenities: ${response.message()} - $errorBody")
                Result.failure(Exception("Failed to fetch internal amenities: ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error fetching internal amenities: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getExternalAmenities(): Result<AmenitiesResponse> {
        return try {
            val response = propertyApi.getExternalAmenities()
            if (response.isSuccessful) {
                Log.d("PropertyRepository", "External amenities fetched successfully")
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("PropertyRepository", "Failed to fetch external amenities: ${response.message()} - $errorBody")
                Result.failure(Exception("Failed to fetch external amenities: ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error fetching external amenities: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getAccessibilityAmenities(): Result<AmenitiesResponse> {
        return try {
            val response = propertyApi.getAccessibilityAmenities()
            if (response.isSuccessful) {
                Log.d("PropertyRepository", "Accessibility amenities fetched successfully")
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("PropertyRepository", "Failed to fetch accessibility amenities: ${response.message()} - $errorBody")
                Result.failure(Exception("Failed to fetch accessibility amenities: ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error fetching accessibility amenities: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getProperties(): Result<List<Property>> {
        return Result.success(emptyList())
    }
}