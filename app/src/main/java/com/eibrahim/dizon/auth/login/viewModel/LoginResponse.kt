package com.eibrahim.dizon.auth.login.viewModel

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("displayName")
    val displayName: String? = null,
    @SerializedName("token")
    val token: String? = null
)