package com.eibrahim.dizon.favorite.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.eibrahim.dizon.details.model.PropertyApi
import com.eibrahim.dizon.auth.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class FavoriteRepository {
    private val propertyApi: PropertyApi = RetrofitClient.propertyApi
    private val _favoriteList = MutableLiveData<List<FavoriteProperty>>()
    val favoriteList: LiveData<List<FavoriteProperty>> = _favoriteList

    suspend fun fetchFavorites(): Result<List<FavoriteProperty>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = propertyApi.getFavorites()
                if (response.isSuccessful) {
                    val favoritesResponse = response.body()
                    val favorites = favoritesResponse?.values ?: emptyList()
                    _favoriteList.postValue(favorites)
                    Log.d("FavoriteRepository", "Fetched ${favorites.size} favorite properties")
                    Result.success(favorites)
                } else {
                    val error = when (response.code()) {
                        401 -> "Unauthorized: Please log in to view favorites"
                        else -> "Failed to fetch favorites: ${response.code()}"
                    }
                    Log.e("FavoriteRepository", error)
                    Result.failure(Exception(error))
                }
            } catch (e: IOException) {
                Log.e("FavoriteRepository", "Network error: ${e.message}")
                Result.failure(Exception("Network error: Unable to connect to the server"))
            } catch (e: HttpException) {
                Log.e("FavoriteRepository", "HTTP error: ${e.message}")
                Result.failure(Exception("HTTP error: ${e.message}"))
            } catch (e: Exception) {
                Log.e("FavoriteRepository", "Unexpected error: ${e.message}")
                Result.failure(Exception("Unexpected error: ${e.message}"))
            }
        }
    }

    suspend fun removeFavorite(propertyId: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = propertyApi.removeFromFavorites(propertyId)
                if (response.isSuccessful) {
                    Log.d("FavoriteRepository", "Removed property $propertyId from favorites")
                    Result.success(Unit)
                } else {
                    val error = when (response.code()) {
                        401 -> "Unauthorized: Please log in to remove favorites"
                        404 -> "Property not found in favorites"
                        else -> "Failed to remove favorite: ${response.code()}"
                    }
                    Log.e("FavoriteRepository", error)
                    Result.failure(Exception(error))
                }
            } catch (e: IOException) {
                Log.e("FavoriteRepository", "Network error: ${e.message}")
                Result.failure(Exception("Network error: Unable to connect to the server"))
            } catch (e: HttpException) {
                Log.e("FavoriteRepository", "HTTP error: ${e.message}")
                Result.failure(Exception("HTTP error: ${e.message}"))
            } catch (e: Exception) {
                Log.e("FavoriteRepository", "Unexpected error: ${e.message}")
                Result.failure(Exception("Unexpected error: ${e.message}"))
            }
        }
    }

    fun isFavoriteLocally(propertyId: Int): Boolean? {
        return favoriteList.value?.any { it.propertyId == propertyId }
    }

    fun updateFavoriteList(newList: List<FavoriteProperty>) {
        _favoriteList.postValue(newList)
    }
}