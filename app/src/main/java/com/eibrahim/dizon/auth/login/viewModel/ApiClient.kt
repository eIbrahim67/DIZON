package com.eibrahim.dizon.auth.login.viewModel

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://digitalpropertyapi.runasp.net/"

    // Lazily initialize Retrofit instance
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Provide the ApiService instance
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}