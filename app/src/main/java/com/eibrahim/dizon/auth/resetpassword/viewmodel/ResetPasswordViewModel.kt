package com.eibrahim.dizon.auth.resetpassword.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.auth.api.RetrofitClient
import kotlinx.coroutines.launch

sealed class ResetPasswordState {
    object Loading : ResetPasswordState()
    data class Success(val response: ResetPasswordResponse) : ResetPasswordState()
    data class Error(val message: String) : ResetPasswordState()
}

class ResetPasswordViewModel : ViewModel() {
    private val _resetPasswordState = MutableLiveData<ResetPasswordState>()
    val resetPasswordState: LiveData<ResetPasswordState> get() = _resetPasswordState

    fun resetPassword(request: ResetPasswordRequest) {
        viewModelScope.launch {
            _resetPasswordState.value = ResetPasswordState.Loading
            try {
                val response = RetrofitClient.api.resetPassword(request)
                Log.d("ResetPasswordViewModel", "Request URL: http://digitalpropertyapi.runasp.net/api/Authorization/reset-password")
                Log.d("ResetPasswordViewModel", "Response code: ${response.code()}")
                Log.d("ResetPasswordViewModel", "Response body: ${response.body()}")
                Log.d("ResetPasswordViewModel", "Response error: ${response.errorBody()?.string()}")
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.status.equals("success", ignoreCase = true)) {
                            _resetPasswordState.value = ResetPasswordState.Success(it)
                        } else {
                            _resetPasswordState.value = ResetPasswordState.Error("Request failed: ${it.message}")
                        }
                    } ?: run {
                        _resetPasswordState.value = ResetPasswordState.Error("Empty response")
                    }
                } else {
                    _resetPasswordState.value = ResetPasswordState.Error("Request failed: ${response.message()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ResetPasswordViewModel", "Network error: ${e.message}", e)
                _resetPasswordState.value = ResetPasswordState.Error("Network error: ${e.message}")
            }
        }
    }
}