package com.eibrahim.dizon.auth.register.viewModel

data class RegisterRequest(
    val firstName: String,
    val lastName: String?,
    val email: String,
    val phoneNumber: String?,
    val city: String?,
    val birthOfDate: String?,
    val password: String,
    val confirmPassword: String,
    val isTermsAccepted: Boolean
)