package com.eibrahim.dizon.auth.verify.viewModel

data class VerifyRequest(
    val email: String,
    val otp: String
)