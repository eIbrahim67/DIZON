package com.eibrahim.dizon.auth.register.viewModel

sealed class RegisterState {
    object Loading : RegisterState()
    data class Success(val response: RegisterResponse) : RegisterState()
    data class Error(val message: String) : RegisterState()
}