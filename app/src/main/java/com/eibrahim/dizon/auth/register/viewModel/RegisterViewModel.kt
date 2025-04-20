package com.eibrahim.dizon.auth.register.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.auth.api.RetrofitClient
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> get() = _registerState

    fun registerUser(request: RegisterRequest) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val response = RetrofitClient.api.register(request)
                if (response.isSuccessful) {
                    response.body()?.let {
                        // Accept both "success" and "Success" (case-insensitive)
                        if (it.status.equals("success", ignoreCase = true) || it.status.equals("Success", ignoreCase = true)) {
                            _registerState.value = RegisterState.Success(it)
                        } else {
                            _registerState.value = RegisterState.Error("Registration failed: ${it.message}")
                        }
                    } ?: run {
                        _registerState.value = RegisterState.Error("Empty response")
                    }
                } else {
                    _registerState.value = RegisterState.Error("Request failed: ${response.message()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Network error: ${e.message}", e)
                _registerState.value = RegisterState.Error("Network error: ${e.message}")
            }
        }
    }
}