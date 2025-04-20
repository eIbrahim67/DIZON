package com.eibrahim.dizon.auth.otp.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.auth.api.RetrofitClient
import kotlinx.coroutines.launch

sealed class OtpState {
    object Loading : OtpState()
    data class Success(val response: OtpResponse) : OtpState()
    data class Error(val message: String) : OtpState()
}

sealed class ResendOtpState {
    object Loading : ResendOtpState()
    data class Success(val response: ResendOtpResponse) : ResendOtpState()
    data class Error(val message: String) : ResendOtpState()
}

class OtpViewModel : ViewModel() {
    private val _otpState = MutableLiveData<OtpState>()
    val otpState: LiveData<OtpState> get() = _otpState

    private val _resendOtpState = MutableLiveData<ResendOtpState>()
    val resendOtpState: LiveData<ResendOtpState> get() = _resendOtpState

    fun verifyOtp(request: OtpRequest) {
        viewModelScope.launch {
            _otpState.value = OtpState.Loading
            try {
                val response = RetrofitClient.api.verifyOtpForReset(request)
                Log.d("OtpViewModel", "Verify OTP Request URL: http://digitalpropertyapi.runasp.net/api/Authorization/verify-otp")
                Log.d("OtpViewModel", "Verify OTP Response code: ${response.code()}")
                Log.d("OtpViewModel", "Verify OTP Response body: ${response.body()}")
                Log.d("OtpViewModel", "Verify OTP Response error: ${response.errorBody()?.string()}")
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.status.equals("success", ignoreCase = true)) {
                            _otpState.value = OtpState.Success(it)
                        } else {
                            _otpState.value = OtpState.Error("Verification failed: ${it.message}")
                        }
                    } ?: run {
                        _otpState.value = OtpState.Error("Empty response")
                    }
                } else {
                    _otpState.value = OtpState.Error("Request failed: ${response.message()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("OtpViewModel", "Verify OTP Network error: ${e.message}", e)
                _otpState.value = OtpState.Error("Network error: ${e.message}")
            }
        }
    }

    fun resendOtp(request: ResendOtpRequest) {
        viewModelScope.launch {
            _resendOtpState.value = ResendOtpState.Loading
            try {
                val response = RetrofitClient.api.resendOtp(request)
                Log.d("OtpViewModel", "Resend OTP Request URL: http://digitalpropertyapi.runasp.net/api/Authorization/resend-otp")
                Log.d("OtpViewModel", "Resend OTP Response code: ${response.code()}")
                Log.d("OtpViewModel", "Resend OTP Response body: ${response.body()}")
                Log.d("OtpViewModel", "Resend OTP Response error: ${response.errorBody()?.string()}")
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.status.equals("success", ignoreCase = true)) {
                            _resendOtpState.value = ResendOtpState.Success(it)
                        } else {
                            _resendOtpState.value = ResendOtpState.Error("Resend failed: ${it.message}")
                        }
                    } ?: run {
                        _resendOtpState.value = ResendOtpState.Error("Empty response")
                    }
                } else {
                    _resendOtpState.value = ResendOtpState.Error("Request failed: ${response.message()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("OtpViewModel", "Resend OTP Network error: ${e.message}", e)
                _resendOtpState.value = ResendOtpState.Error("Network error: ${e.message}")
            }
        }
    }
}