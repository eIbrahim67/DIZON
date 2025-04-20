package com.eibrahim.dizon.auth.login.viewModel

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String? = null
)