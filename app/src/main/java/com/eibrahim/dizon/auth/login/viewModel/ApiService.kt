package com.eibrahim.dizon.auth.login.viewModel

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/Authorization/Login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}