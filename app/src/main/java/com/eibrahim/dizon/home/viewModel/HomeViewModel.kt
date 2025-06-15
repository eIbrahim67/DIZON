package com.eibrahim.dizon.home.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.favorite.model.FavoriteProperty
import com.eibrahim.dizon.home.api.RetrofitHome
import com.eibrahim.dizon.home.model.Property
import com.eibrahim.dizon.home.model.RecommendedPropertyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class HomeViewModel : ViewModel() {

    private val _properties = MutableLiveData<RecommendedPropertyResponse?>()
    val properties: MutableLiveData<RecommendedPropertyResponse?> = _properties

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var pageIndex = 1
    private val pageSize = 5

    fun loadRecommendations() {
        Log.d("Test1", "1")
        viewModelScope.launch {
            try {
                val response: Response<RecommendedPropertyResponse> =
                    RetrofitHome.homeApi.getRecommendedProperties(pageIndex, pageSize)

                if (response.isSuccessful) {
                    val propertyResponse = response.body()
                    _properties.value = propertyResponse
                    // Handle the successful response, e.g., update LiveData or StateFlow
                    Log.d("Test2", "Success: $propertyResponse")
                    // Example: _properties.value = propertyResponse?.data?.values
                } else {
                    // Handle the error response
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Log.d("Test3", "Error: $errorMessage")
                }
            } catch (e: Exception) {
                // Handle network or other exceptions
                Log.e("Exception", ": ${e.message}")
            }
        }
    }

    private val _favoriteIds = MutableLiveData<Set<Int>>() // store property IDs
    val favoriteIds: LiveData<Set<Int>> = _favoriteIds

    fun fetchFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHome.homeApi.getFavorites()
                if (response.isSuccessful) {
                    val favoritesResponse = response.body()
                    val favorites = favoritesResponse?.values ?: emptyList()
                    val ids = favorites.map { it.propertyId }.toSet()
                    _favoriteIds.postValue(ids)

                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching favorites: ${e.message}")
            }
        }
    }

    fun toggleFavorite(property: Property) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentIds = _favoriteIds.value?.toMutableSet() ?: mutableSetOf()
            try {
                val isFavorite = RetrofitHome.homeApi.isPropertyInFavorites(property.propertyId).body() ?: false
                if (isFavorite) {
                    val response = RetrofitHome.homeApi.removeFromFavorites(property.propertyId)
                    if (response.isSuccessful) {
                        currentIds.remove(property.propertyId)
                        Log.d("HomeViewModel", "Removed from favorites: ${property.propertyId}")
                    }
                } else {
                    val response = RetrofitHome.homeApi.addToFavorites(property.propertyId)
                    if (response.isSuccessful) {
                        currentIds.add(property.propertyId)
                        Log.d("HomeViewModel", "Added to favorites: ${property.propertyId}")
                    }
                }
                _favoriteIds.postValue(currentIds)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error toggling favorite: ${e.message}")
            }
        }
    }


}