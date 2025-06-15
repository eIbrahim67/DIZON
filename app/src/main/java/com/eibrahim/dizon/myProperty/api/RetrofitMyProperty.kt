package com.eibrahim.dizon.myProperty.api

import android.content.Context
import android.util.Log
import com.eibrahim.dizon.auth.AuthPreferences
import com.eibrahim.dizon.home.api.HomeApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitMyProperty {

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
                    Log.w("Ibra Home",token)
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

    val myPropertyApi: MyPropertyApi by lazy {
        retrofit.create(MyPropertyApi::class.java)
    }

}