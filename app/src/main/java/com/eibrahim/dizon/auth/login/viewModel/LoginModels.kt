package com.eibrahim.dizon.auth.login.viewModel

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String? = null, // Token on success, null on failure
    val message: String? = null // Error message if applicable
)