package com.eibrahim.dizon.change_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.auth.api.AuthApi
import com.eibrahim.dizon.auth.api.ChangePasswordRequest
import com.eibrahim.dizon.auth.api.ChangePasswordResponse
import com.eibrahim.dizon.auth.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class ChangePasswordViewModel : ViewModel() {
    // Initialize the API client from RetrofitClient
    private val authApi: AuthApi = RetrofitClient.api

    // StateFlow to manage UI states (initial, loading, success, error)
    private val _changePasswordState = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Initial)
    val changePasswordState: StateFlow<ChangePasswordState> = _changePasswordState

    // Function to trigger the password change API call
    fun changePassword(oldPassword: String, newPassword: String, confirmNewPassword: String) {
        viewModelScope.launch {
            // Set state to loading while the API call is in progress
            _changePasswordState.value = ChangePasswordState.Loading
            try {
                // Create the request object and call the API
                val response: Response<ChangePasswordResponse> = authApi.changePassword(
                    ChangePasswordRequest(oldPassword, newPassword, confirmNewPassword)
                )
                if (response.isSuccessful) {
                    // On success, update state with the message from the response
                    _changePasswordState.value = ChangePasswordState.Success(
                        response.body()?.message ?: "Password changed successfully"
                    )
                } else {
                    // On failure, update state with the error message
                    _changePasswordState.value = ChangePasswordState.Error(
                        "Failed to change password: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                // Handle network or unexpected errors
                _changePasswordState.value = ChangePasswordState.Error("Error: ${e.message}")
            }
        }
    }
}

// Sealed class to represent different states of the password change process
sealed class ChangePasswordState {
    object Initial : ChangePasswordState()  // Default state before any action
    object Loading : ChangePasswordState()  // State during API call
    data class Success(val message: String) : ChangePasswordState()  // State on successful password change
    data class Error(val message: String) : ChangePasswordState()    // State on failure
}