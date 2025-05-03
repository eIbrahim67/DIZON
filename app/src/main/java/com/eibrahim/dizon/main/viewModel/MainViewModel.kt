package com.eibrahim.dizon.main.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eibrahim.dizon.auth.AuthPreferences

class MainViewModel : ViewModel() {

    private val _navigateToFragment = MutableLiveData<Int?>()
    val navigateToFragment: LiveData<Int?> get() = _navigateToFragment

    fun navigateTo(fragmentName: Int?) {
        _navigateToFragment.value = fragmentName
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val authPreferences = AuthPreferences(context)
        val token = authPreferences.getToken()
        return !token.isNullOrEmpty()
    }

    /////////////////////// Favorite //////////////////////////////////////

    private val _favoriteRemoved = MutableLiveData<Int?>()
    val favoriteRemoved: LiveData<Int?> = _favoriteRemoved

    fun notifyFavoriteRemoved(propertyId: Int) {
        _favoriteRemoved.postValue(propertyId)
    }

    fun clearFavoriteRemoved() {
        _favoriteRemoved.postValue(null)
    }
}