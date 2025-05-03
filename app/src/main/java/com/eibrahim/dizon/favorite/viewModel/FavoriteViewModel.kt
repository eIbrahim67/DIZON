package com.eibrahim.dizon.favorite.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.favorite.model.FavoriteProperty
import com.eibrahim.dizon.favorite.model.FavoriteRepository
import com.eibrahim.dizon.main.viewModel.MainViewModel
import kotlinx.coroutines.launch

class FavoriteViewModel(private val mainViewModel: MainViewModel) : ViewModel() {
    private val repository = FavoriteRepository()

    val favoriteProperties: LiveData<List<FavoriteProperty>> = repository.favoriteList
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var lastRemovedProperty: FavoriteProperty? = null
    private var lastRemovedPosition: Int = -1

    fun fetchFavorites() {
        viewModelScope.launch {
            val result = repository.fetchFavorites()
            result.onFailure { exception ->
                _error.postValue(exception.message)
            }
        }
    }

    fun removeFavorite(propertyId: Int) {
        viewModelScope.launch {
            // Optimistic update: remove property immediately
            val currentList = favoriteProperties.value?.toMutableList() ?: mutableListOf()
            val index = currentList.indexOfFirst { it.propertyId == propertyId }
            if (index != -1) {
                lastRemovedProperty = currentList[index]
                lastRemovedPosition = index
                currentList.removeAt(index)
                repository.updateFavoriteList(currentList)
                mainViewModel.notifyFavoriteRemoved(propertyId)
            }

            // Send request to server
            val result = repository.removeFavorite(propertyId)
            result.onSuccess {
                _error.postValue("Property removed from favorites")
            }.onFailure { exception ->
                // Revert optimistic update on failure
                lastRemovedProperty?.let { property ->
                    if (lastRemovedPosition != -1) {
                        currentList.add(lastRemovedPosition, property)
                        repository.updateFavoriteList(currentList)
                    }
                }
                _error.postValue(exception.message)
            }
        }
    }

    fun undoRemoveFavorite() {
        lastRemovedProperty?.let { property ->
            if (lastRemovedPosition != -1) {
                val currentList = favoriteProperties.value?.toMutableList() ?: mutableListOf()
                currentList.add(lastRemovedPosition, property)
                repository.updateFavoriteList(currentList)
                _error.postValue(null) // Clear error to prevent repeated Snackbars
            }
        }
        lastRemovedProperty = null
        lastRemovedPosition = -1
    }
}