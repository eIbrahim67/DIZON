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

    // Initialize AuthPreferences (call this in your Application class or Activity)
    fun initAuthPreferences(context: Context) {
        authPreferences = AuthPreferences(context)
        Log.d("RetrofitClient", "AuthPreferences initialized with context")
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor { message ->
            Log.d("OkHttp", message) // Ensure logs are tagged with "OkHttp"
        }.apply {
            setLevel(HttpLoggingInterceptor.Level.BODY) // Log full request and response body
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain: Interceptor.Chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                authPreferences?.getToken()?.let { token ->
                    Log.d("RetrofitClient", "Adding Authorization header with token: $token")
                    requestBuilder.header("Authorization", "Bearer $token")
                } ?: run {
                    Log.d("RetrofitClient", "No token available for request")
                }
                val request = requestBuilder.build()
                chain.proceed(request)
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