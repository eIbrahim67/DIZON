package com.eibrahim.dizon.search.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.search.data.Property
import com.eibrahim.dizon.search.api.RetrofitSearch
import com.eibrahim.dizon.search.data.SearchPropertyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class SearchViewModel : ViewModel() {

    private val _properties = MutableLiveData<SearchPropertyResponse?>()
    val properties: LiveData<SearchPropertyResponse?> = _properties

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var pageIndex = 1
    private val pageSize = 5

    private var filterParams = FilterParams(
        propertyType = null,
        city = null,
        governate = null,
        bedrooms = null,
        bathrooms = null,
        sortBy = null,
        size = null,
        maxPrice = null,
        minPrice = null,
        internalAmenityIds = null,
        externalAmenityIds = null,
        accessibilityAmenityIds = null
    )

    data class FilterParams(
        val propertyType: String?,
        val city: String?,
        val governate: String?,
        val bedrooms: Int?,
        val bathrooms: Int?,
        val sortBy: String?,
        val size: Double?,
        val maxPrice: Double?,
        val minPrice: Double?,
        val internalAmenityIds: List<Int>?,
        val externalAmenityIds: List<Int>?,
        val accessibilityAmenityIds: List<Int>?
    )

    fun updateFilterParams(
        propertyType: String? = filterParams.propertyType,
        city: String? = filterParams.city,
        governate: String? = filterParams.governate,
        bedrooms: Int? = filterParams.bedrooms,
        bathrooms: Int? = filterParams.bathrooms,
        sortBy: String? = filterParams.sortBy,
        size: Double? = filterParams.size,
        maxPrice: Double? = filterParams.maxPrice,
        minPrice: Double? = filterParams.minPrice,
        internalAmenityIds: List<Int>? = filterParams.internalAmenityIds,
        externalAmenityIds: List<Int>? = filterParams.externalAmenityIds,
        accessibilityAmenityIds: List<Int>? = filterParams.accessibilityAmenityIds
    ) {
        filterParams = FilterParams(
            propertyType, city, governate, bedrooms, bathrooms, sortBy, size,
            maxPrice, minPrice, internalAmenityIds, externalAmenityIds, accessibilityAmenityIds
        )
    }

    fun loadAllProperties() {
        _loading.value = true

        viewModelScope.launch {
            try {
                val response: Response<SearchPropertyResponse> =
                    RetrofitSearch.searchApi.getAllProperties(
                        propertyType = filterParams.propertyType,
                        city = filterParams.city,
                        governate = filterParams.governate,
                        bedrooms = filterParams.bedrooms,
                        bathrooms = filterParams.bathrooms,
                        sortBy = filterParams.sortBy,
                        size = filterParams.size,
                        maxPrice = filterParams.maxPrice,
                        minPrice = filterParams.minPrice,
                        internalAmenityIds = filterParams.internalAmenityIds,
                        externalAmenityIds = filterParams.externalAmenityIds,
                        accessibilityAmenityIds = filterParams.accessibilityAmenityIds,
                        pageSize = pageSize,
                        pageIndex = pageIndex
                    )

                if (response.isSuccessful) {
                    val propertyResponse = response.body()
                    _properties.value = propertyResponse
                    _loading.value = false
                    Log.d("SearchViewModel", "Success: $propertyResponse")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    _error.value = errorMessage
                    _loading.value = false
                    Log.d("SearchViewModel", "Error: $errorMessage")
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Network error"
                _loading.value = false
                Log.e("SearchViewModel", "Exception: ${e.message}")
            }
        }
    }

    private val _favoriteIds = MutableLiveData<Set<Int>>() // store property IDs
    val favoriteIds: LiveData<Set<Int>> = _favoriteIds

    fun fetchFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitSearch.searchApi.getFavorites()
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
                val isFavorite = RetrofitSearch.searchApi.isPropertyInFavorites(property.propertyId).body() ?: false
                if (isFavorite) {
                    val response = RetrofitSearch.searchApi.removeFromFavorites(property.propertyId)
                    if (response.isSuccessful) {
                        currentIds.remove(property.propertyId)
                        Log.d("HomeViewModel", "Removed from favorites: ${property.propertyId}")
                    }
                } else {
                    val response = RetrofitSearch.searchApi.addToFavorites(property.propertyId)
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