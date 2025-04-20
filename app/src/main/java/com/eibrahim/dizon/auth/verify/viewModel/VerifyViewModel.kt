package com.eibrahim.dizon.auth.verify.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.auth.api.RetrofitClient
import kotlinx.coroutines.launch

sealed class VerifyState {
    object Loading : VerifyState()
    data class Success(val response: VerifyResponse) : VerifyState()
    data class Error(val message: String) : VerifyState()
}

class VerifyViewModel : ViewModel() {
    private val _verifyState = MutableLiveData<VerifyState>()
    val verifyState: LiveData<VerifyState> get() = _verifyState

    fun verifyOtp(request: VerifyRequest) {
        viewModelScope.launch {
            _verifyState.value = VerifyState.Loading
            try {
                val response = RetrofitClient.api.verifyOtp(request)
                Log.d("VerifyViewModel", "Request URL: http://digitalpropertyapi.runasp.net/api/Authorization/verify-otp")
                Log.d("VerifyViewModel", "Response code: ${response.code()}")
                Log.d("VerifyViewModel", "Response body: ${response.body()}")
                Log.d("VerifyViewModel", "Response error: ${response.errorBody()?.string()}")
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.status.equals("success", ignoreCase = true) || it.status.equals("Success", ignoreCase = true)) {
                            _verifyState.value = VerifyState.Success(it)
                        } else {
                            _verifyState.value = VerifyState.Error("Verification failed: ${it.message}")
                        }
                    } ?: run {
                        _verifyState.value = VerifyState.Error("Empty response")
                    }
                } else {
                    _verifyState.value = VerifyState.Error("Request failed: ${response.message()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("VerifyViewModel", "Network error: ${e.message}", e)
                _verifyState.value = VerifyState.Error("Network error: ${e.message}")
            }
        }
    }
}