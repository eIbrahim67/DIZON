package com.eibrahim.dizon.main.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.auth.api.RetrofitClient
import com.eibrahim.dizon.auth.api.UserResponse
import com.eibrahim.dizon.chatbot.api.RetrofitChatbot
import com.eibrahim.dizon.search.data.SearchPropertyResponse
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel : ViewModel() {
    private val _navigateToFragment = MutableLiveData<Int?>()
    val navigateToFragment: LiveData<Int?> get() = _navigateToFragment

    /////////////////////// Favorite //////////////////////////////////////

    private val _favoriteRemoved = MutableLiveData<Int?>()

    fun notifyFavoriteRemoved(propertyId: Int) {
        _favoriteRemoved.postValue(propertyId)
    }


    private val _userData = MutableLiveData<UserResponse?>()
    val userData: LiveData<UserResponse?> get() = _userData


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

    // وظيفة لجلب بيانات المستخدم
    fun fetchUserData() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getUser()
                if (response.isSuccessful) {
                    _userData.postValue(response.body())
                } else {
                    _error.postValue("Failed to fetch user data: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Error fetching user data: ${e.message}")
            }
        }
    }


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
                val response: retrofit2.Response<SearchPropertyResponse> =
                    RetrofitChatbot.searchApi.getAllProperties(
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


    private val _searchType = MutableLiveData<String>()
    val searchType: LiveData<String> = _searchType

    // Function to update the entire AddPropertyData object
    fun setSearchType(data: String) {
        _searchType.value = data
    }

}