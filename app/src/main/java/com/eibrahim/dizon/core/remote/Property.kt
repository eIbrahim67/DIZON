package com.eibrahim.dizon.core.remote

data class Property(
    val id: Int,                      // Unique identifier for the property
    val title: String,                   // Short, descriptive title (e.g., "Elegant Family Home")
    val description: String,             // Detailed description of the property
    val address: String,                 // Street address of the property
    val city: String,                    // City where the property is located
    val state: String,                   // State or province
    val zipCode: String,                 // Zip or postal code
    val country: String,                 // Country
    val latitude: Double,                // Geographic coordinate for mapping
    val longitude: Double,               // Geographic coordinate for mapping
    val price: Double,                   // Listing price (e.g., in USD)
    val area: Double,                    // Total living area (in square feet or meters)
    val bedrooms: Int,                   // Number of bedrooms
    val bathrooms: Int,                  // Number of bathrooms
    val floors: Int,                     // Number of floors
    val images: List<String> = emptyList(), // List of image URLs or file paths
    val createdAt: Long = System.currentTimeMillis(), // Timestamp for when the property was created
    val updatedAt: Long? = null          // Optional timestamp for the last update
)
