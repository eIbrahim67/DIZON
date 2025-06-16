package com.eibrahim.dizon.propertyResults.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.propertyResults.api.RetrofitResult
import com.eibrahim.dizon.search.data.Property
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AllResultViewModel : ViewModel() {

    private val _favoriteIds = MutableLiveData<Set<Int>>() // store property IDs
    val favoriteIds: LiveData<Set<Int>> = _favoriteIds

    fun fetchFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitResult.resultApi.getFavorites()
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
                val isFavorite =
                    RetrofitResult.resultApi.isPropertyInFavorites(property.propertyId).body() ?: false
                if (isFavorite) {
                    val response = RetrofitResult.resultApi.removeFromFavorites(property.propertyId)
                    if (response.isSuccessful) {
                        currentIds.remove(property.propertyId)
                        Log.d("HomeViewModel", "Removed from favorites: ${property.propertyId}")
                    }
                } else {
                    val response = RetrofitResult.resultApi.addToFavorites(property.propertyId)
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