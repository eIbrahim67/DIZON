package com.eibrahim.dizon.auth.login.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.auth.api.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import org.json.JSONObject

class LoginViewModel : ViewModel() {
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> get() = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val request = LoginRequest(email = email, password = password)
                val response = RetrofitClient.api.login(request)
                Log.d("LoginViewModel", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.status.equals("success", ignoreCase = true)) {
                        _loginState.value = LoginState.Success(body)
                    } else {
                        _loginState.value = LoginState.Error("Login failed: ${body?.message ?: "Unknown error"}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("LoginViewModel", "Raw error body: $errorBody")

                    val json = JSONObject(errorBody)
                    val message = json.optString("message", "")
                    val errorMessage = if (message.contains("credentials", true)) {
                        "Invalid email or password"
                    } else {
                        message.ifEmpty { "Login failed. Please try again." }
                    }
                    _loginState.value = LoginState.Error(errorMessage)

                    Log.d("LoginViewModel", "Error message sent: $errorMessage")
                    _loginState.value = LoginState.Error(errorMessage)
                }
            } catch (e: HttpException) {
                _loginState.value = LoginState.Error("Server error: ${e.message()}")
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Network error: ${e.message ?: "Unknown error"}")
            }
        }
    }
}

sealed class LoginState {
    object Loading : LoginState()
    data class Success(val response: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}