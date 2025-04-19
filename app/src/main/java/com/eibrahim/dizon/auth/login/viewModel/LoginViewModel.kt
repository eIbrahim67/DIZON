package com.eibrahim.dizon.auth.login.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private val _loginState = MutableLiveData<Result<Boolean>>()
    val loginState: LiveData<Result<Boolean>> = _loginState

    fun login(email: String, password: String) {
        // Check for empty fields to maintain input validation
        if (email.isEmpty() || password.isEmpty()) {
            _loginState.value = Result.failure(Exception("Please fill in all fields"))
            return
        }

        // Create the login request object with email and password
        val loginRequest = LoginRequest(email, password)

        // Make the API call using the ApiClient's Retrofit service
        ApiClient.apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    // Check if the response indicates success (e.g., token is present)
                    if (loginResponse?.token != null) {
                        // Login successful; token can be saved for future use (not implemented here)
                        _loginState.value = Result.success(true)
                    } else {
                        // Login failed despite a successful HTTP response
                        _loginState.value = Result.failure(Exception(loginResponse?.message ?: "Login failed"))
                    }
                } else {
                    // Handle unsuccessful HTTP responses (e.g., 401 Unauthorized)
                    val errorBody = response.errorBody()?.string()
                    _loginState.value = Result.failure(Exception(errorBody ?: "Login failed: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // Handle network errors or other failures
                _loginState.value = Result.failure(Exception("Network error: ${t.message}"))
            }
        })
    }
}