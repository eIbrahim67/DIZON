package com.eibrahim.dizon.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.eibrahim.dizon.auth.api.AuthApi
import com.eibrahim.dizon.auth.api.RetrofitClient
import com.eibrahim.dizon.auth.api.UserResponse
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val authApi: AuthApi = RetrofitClient.api

    private val _user = MutableLiveData<UserResponse?>()
    val user: LiveData<UserResponse?> get() = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                val response = authApi.getUser()
                if (response.isSuccessful) {
                    _user.postValue(response.body())
                } else {
                    _error.postValue("API Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Network Error: ${e.message}")
            }
        }
    }
}
