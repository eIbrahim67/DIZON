package com.eibrahim.dizon.favorite.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.eibrahim.dizon.favorite.model.FavoriteProperty
import com.eibrahim.dizon.favorite.model.FavoriteRepository

class FavoriteViewModel : ViewModel() {
    private val repository = FavoriteRepository()
    val favoriteProperties: LiveData<List<FavoriteProperty>> = repository.getFavorites()
}