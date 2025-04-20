package com.eibrahim.dizon.auth.forgetpassword.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.auth.api.RetrofitClient
import kotlinx.coroutines.launch

sealed class ForgetPasswordState {
    object Idle : ForgetPasswordState()
    object Loading : ForgetPasswordState()
    data class Success(val response: ForgetPasswordResponse) : ForgetPasswordState()
    data class Error(val message: String) : ForgetPasswordState()
}

class ForgetPasswordViewModel : ViewModel() {
    private val _forgetPasswordState = MutableLiveData<ForgetPasswordState>(ForgetPasswordState.Idle)
    val forgetPasswordState: LiveData<ForgetPasswordState> get() = _forgetPasswordState

    fun sendResetLink(request: ForgetPasswordRequest) {
        viewModelScope.launch {
            _forgetPasswordState.value = ForgetPasswordState.Loading
            try {
                val response = RetrofitClient.api.forgotPassword(request)

                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.status.equals("success", ignoreCase = true)) {
                            _forgetPasswordState.value = ForgetPasswordState.Success(it)
                        } else {
                            _forgetPasswordState.value = ForgetPasswordState.Error("Request failed: ${it.message}")
                        }
                    } ?: run {
                        _forgetPasswordState.value = ForgetPasswordState.Error("Empty response")
                    }
                } else {
                    _forgetPasswordState.value = ForgetPasswordState.Error("Request failed: ${response.message()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ForgetPasswordViewModel", "Network error: ${e.message}", e)
                _forgetPasswordState.value = ForgetPasswordState.Error("Network error: ${e.message}")
            }
        }
    }

    fun resetState() {
        Log.d("ForgetPasswordViewModel", "Resetting forgetPasswordState to Idle")
        _forgetPasswordState.value = ForgetPasswordState.Idle
    }
}