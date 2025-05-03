package com.eibrahim.dizon.auth.login.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.auth.AuthPreferences
import com.eibrahim.dizon.auth.api.GoogleLoginRequest
import com.eibrahim.dizon.auth.api.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import org.json.JSONObject

class LoginViewModel : ViewModel() {
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> get() = _loginState

    fun login(email: String, password: String, authPreferences: AuthPreferences) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val request = LoginRequest(email = email, password = password)
                Log.d("LoginViewModel", "Sending login request: $request")
                val response = RetrofitClient.api.login(request)
                Log.d("LoginViewModel", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("LoginViewModel", "Login response body: $body")
                    if (body != null && (body.status?.equals("success", ignoreCase = true) == true || body.token != null)) {
                        body.token?.let { token ->
                            authPreferences.saveToken(token)
                            Log.d("LoginViewModel", "Token saved: $token")
                        }
                        _loginState.value = LoginState.Success(body)
                    } else {
                        _loginState.value = LoginState.Error("Login failed: ${body?.message ?: "Invalid response from server"}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.d("LoginViewModel", "Raw error body: $errorBody")
                    val errorMessage = when (response.code()) {
                        400 -> "Bad request: Please check your email and password"
                        401 -> "Unauthorized: Invalid email or password"
                        403 -> "Forbidden: Account may be disabled"
                        else -> {
                            val json = JSONObject(errorBody ?: "{}")
                            val message = json.optString("message", "")
                            message.ifEmpty { "Login failed with code ${response.code()}. Please try again." }
                        }
                    }
                    _loginState.value = LoginState.Error(errorMessage)
                    Log.d("LoginViewModel", "Error message sent: $errorMessage")
                }
            } catch (e: HttpException) {
                Log.e("LoginViewModel", "Server error: ${e.message()}")
                _loginState.value = LoginState.Error("Server error: ${e.message()}")
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Network error: ${e.message}", e)
                _loginState.value = LoginState.Error("Network error: ${e.message ?: "Unable to connect to server"}")
            }
        }
    }

    fun loginWithGoogle(idToken: String, authPreferences: AuthPreferences) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                Log.d("LoginViewModel", "Starting Google login with idToken: $idToken")
                val request = GoogleLoginRequest(idToken = idToken)
                val response = RetrofitClient.api.loginGoogleWithToken(request)
                Log.d("LoginViewModel", "Google login response code: ${response.code()}")
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("LoginViewModel", "Google login response body: $body")
                    if (body != null && (body.status?.equals("success", ignoreCase = true) == true || body.token != null)) {
                        body.token?.let { token ->
                            authPreferences.saveToken(token)
                            Log.d("LoginViewModel", "Google token saved: $token")
                        }
                        _loginState.value = LoginState.Success(body)
                    } else {
                        _loginState.value = LoginState.Error("Google login failed: ${body?.message ?: "Invalid response from server"}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginViewModel", "Google login error body: $errorBody")
                    val errorMessage = when (response.code()) {
                        400 -> "Bad request: Invalid Google token"
                        401 -> "Unauthorized: Invalid Google token"
                        else -> {
                            val json = JSONObject(errorBody ?: "{}")
                            val message = json.optString("message", "")
                            message.ifEmpty { "Google login failed with code ${response.code()}" }
                        }
                    }
                    _loginState.value = LoginState.Error(errorMessage)
                }
            } catch (e: HttpException) {
                Log.e("LoginViewModel", "Google login server error: ${e.message()}")
                _loginState.value = LoginState.Error("Server error: ${e.message()}")
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Google login network error: ${e.message}", e)
                _loginState.value = LoginState.Error("Network error: ${e.message ?: "Unable to connect to server"}")
            }
        }
    }
}

sealed class LoginState {
    object Loading : LoginState()
    data class Success(val response: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}