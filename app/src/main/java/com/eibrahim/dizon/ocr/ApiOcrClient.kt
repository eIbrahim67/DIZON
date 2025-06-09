package com.eibrahim.dizon.ocr

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiOcrClient {
    private const val BASE_URL = "http://192.168.1.5:3000/"

    val ocrApiService: OcrApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
        retrofit.create(OcrApiService::class.java)
    }
}