package com.eibrahim.dizon.vsr

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiVsrClient {
    private const val BASE_URL = "http://192.168.1.5:8000/"

    val vsrApiService: VsrApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
        retrofit.create(VsrApiService::class.java)
    }
}