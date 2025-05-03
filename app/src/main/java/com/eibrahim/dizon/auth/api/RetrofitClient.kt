package com.eibrahim.dizon.auth.api

import android.content.Context
import android.util.Log
import com.eibrahim.dizon.auth.AuthPreferences
import com.eibrahim.dizon.details.model.PropertyApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://digitalpropertyapi.runasp.net/"

    private var authPreferences: AuthPreferences? = null

    fun initAuthPreferences(context: Context) {
        authPreferences = AuthPreferences(context)
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor { message ->
            Log.d("OkHttp", message)
        }.apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain: Interceptor.Chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                authPreferences?.getToken()?.let { token ->
                    requestBuilder.header("Authorization", "Bearer $token")
                } ?: Log.w("RetrofitClient", "No token available, proceeding without Authorization header")
                val request = requestBuilder.build()
                val response = chain.proceed(request)
                response
            }
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val propertyApi: PropertyApi by lazy {
        retrofit.create(PropertyApi::class.java)
    }
}