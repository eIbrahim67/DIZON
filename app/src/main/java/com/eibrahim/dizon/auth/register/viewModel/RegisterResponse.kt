package com.eibrahim.dizon.auth.register.viewModel

data class RegisterResponse(
    val status: String,
    val message: String,
    val data: UserData? = null
)

data class UserData(
    val user_id: String,
    val token: String
)