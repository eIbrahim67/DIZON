package com.eibrahim.dizon.details.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.details.model.PropertyApi
import com.eibrahim.dizon.auth.api.RetrofitClient
import com.eibrahim.dizon.details.model.PropertyDetails
import com.eibrahim.dizon.favorite.model.FavoriteRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class DetailsViewModel : ViewModel() {

    private val propertyApi: PropertyApi = RetrofitClient.propertyApi
    private val favoriteRepository = FavoriteRepository()
    private val _propertyDetails = MutableLiveData<PropertyDetails?>()
    val propertyDetails: LiveData<PropertyDetails?> = _propertyDetails
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    // In-memory cache
    private val propertyCache = mutableMapOf<Int, PropertyDetails>()
    private val CACHE_EXPIRATION_TIME = 60 * 60 * 1000L // 1 hour

    fun fetchPropertyDetails(propertyId: Int, context: Context? = null) {
        viewModelScope.launch {
            // Reset states to avoid showing stale data
            _error.postValue(null)
            _isFavorite.postValue(false) // Default to non-favorite until checked

            // Check in-memory cache first
            propertyCache[propertyId]?.let {
                Log.d("DetailsViewModel", "Using in-memory cache for property ID: $propertyId")
                _propertyDetails.postValue(it)
                checkIsFavorite(propertyId) // Check favorite status
                return@launch
            }

            // Check SharedPreferences cache
            context?.let {
                val cachedProperty = getCachedProperty(it, propertyId)
                if (cachedProperty != null) {
                    Log.d("DetailsViewModel", "Using SharedPreferences cache for ID: $propertyId")
                    propertyCache[propertyId] = cachedProperty // Store in memory
                    _propertyDetails.postValue(cachedProperty)
                    checkIsFavorite(propertyId) // Check favorite status
                    return@launch
                }
            }

            try {
                Log.d("DetailsViewModel", "Fetching property from API for ID: $propertyId")
                val response = propertyApi.getPropertyById(propertyId)
                if (response.isSuccessful) {
                    val property = response.body()
                    property?.let {
                        propertyCache[propertyId] = it // Store in memory
                        _propertyDetails.postValue(it)
                        // Cache to SharedPreferences
                        context?.let { ctx -> cacheProperty(ctx, propertyId, it) }
                        checkIsFavorite(propertyId) // Check favorite status
                    }
                } else {
                    when (response.code()) {
                        401 -> _error.postValue("Unauthorized: Please log in to view property details")
                        else -> _error.postValue("Failed to fetch property details: ${response.code()}")
                    }
                }
            } catch (e: IOException) {
                _error.postValue("Network error: ${e.message}")
            } catch (e: HttpException) {
                _error.postValue("HTTP error: ${e.message}")
            } catch (e: Exception) {
                _error.postValue("Unexpected error: ${e.message}")
            }
        }
    }

    private fun checkIsFavorite(propertyId: Int) {
        viewModelScope.launch {
            // Check local favorites first
            val isFavoriteLocally = favoriteRepository.isFavoriteLocally(propertyId)
            if (isFavoriteLocally != null) {
                _isFavorite.postValue(isFavoriteLocally)
                return@launch
            }
            // Fallback to API
            try {
                val response = propertyApi.isPropertyInFavorites(propertyId)
                Log.d("DetailsViewModel", "isPropertyInFavorites response for ID $propertyId: ${response.body()}")
                if (response.isSuccessful) {
                    _isFavorite.postValue(response.body() ?: false)
                } else {
                    Log.e("DetailsViewModel", "Failed to check favorite status: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("DetailsViewModel", "Error checking favorite status: ${e.message}")
            }
        }
    }

    fun toggleFavorite(propertyId: Int) {
        viewModelScope.launch {
            Log.d("DetailsViewModel", "toggleFavorite called for propertyId: $propertyId")
            val isCurrentlyFavorite = _isFavorite.value ?: false
            try {
                if (isCurrentlyFavorite) {
                    val result = favoriteRepository.removeFavorite(propertyId)
                    result.onSuccess {
                        _isFavorite.postValue(false)
                        _error.postValue("Property removed from favorites")
                    }.onFailure { e ->
                        _error.postValue("Failed to remove from favorites: ${e.message}")
                    }
                } else {
                    val response = propertyApi.addToFavorites(propertyId)
                    if (response.isSuccessful) {
                        _isFavorite.postValue(true)
                        _error.postValue("Property added to favorites")
                    } else {
                        _error.postValue("Failed to add to favorites: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _error.postValue("Error toggling favorite: ${e.message}")
            }
        }
    }

    private fun cacheProperty(context: Context, propertyId: Int, property: PropertyDetails?) {
        val prefs = context.getSharedPreferences("property_cache", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        property?.let {
            editor.putString("property_$propertyId", Gson().toJson(it))
            editor.putLong("property_timestamp_$propertyId", System.currentTimeMillis())
            editor.apply()
            Log.d("DetailsViewModel", "Cached property for ID: $propertyId")
        }
    }

    private fun getCachedProperty(context: Context, propertyId: Int): PropertyDetails? {
        val prefs = context.getSharedPreferences("property_cache", Context.MODE_PRIVATE)
        val json = prefs.getString("property_$propertyId", null)
        val timestamp = prefs.getLong("property_timestamp_$propertyId", 0L)
        val currentTime = System.currentTimeMillis()

        if (currentTime - timestamp > CACHE_EXPIRATION_TIME) {
            Log.d("DetailsViewModel", "Cache expired for property ID: $propertyId")
            return null
        }

        return json?.let {
            Log.d("DetailsViewModel", "Retrieved cached property for ID: $propertyId")
            Gson().fromJson(it, PropertyDetails::class.java)
        }
    }
}