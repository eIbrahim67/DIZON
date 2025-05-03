package com.eibrahim.dizon.home.api

// RetrofitClient.kt
import android.content.Context
import android.util.Log
import com.eibrahim.dizon.auth.AuthPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHome {
    private const val BASE_URL = "http://digitalpropertyapi.runasp.net/"

    private var authPreferences: AuthPreferences? = null

    // Initialize AuthPreferences (call this in your Application class or Activity)
    fun initAuthPreferences(context: Context) {
        authPreferences = AuthPreferences(context)
        Log.d("RetrofitClient", "AuthPreferences initialized with context")
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor { message ->
            Log.d("OkHttp message", message) // Ensure logs are tagged with "OkHttp"
        }.apply {
            setLevel(HttpLoggingInterceptor.Level.BODY) // Log full request and response body
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain: Interceptor.Chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    val token1 = "eyJhbGciOiJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzA0L3htbGRzaWctbW9yZSNobWFjLXNoYTI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1laWRlbnRpZmllciI6WyI0NCIsIjQ0Il0sImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL25hbWUiOiJNb2FtZW4iLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9lbWFpbGFkZHJlc3MiOiJjYW5heTYzNTM0QGFzdGltZWkuY29tIiwiZXhwIjoxNzQ2ODgxOTkzLCJpc3MiOiJodHRwOi8vZGlnaXRhbHByb3BlcnR5YXBpLnJ1bmFzcC5uZXQifQ.tQxl2MVWfvBwGTFHFVuo4qCLCIUDP4N7J_TCtLo3tSY"
                    requestBuilder.header("Authorization", "Bearer $token1")
//                authPreferences?.getToken()?.let { token ->
//                    Log.d("RetrofitClient", "Adding Authorization header with token: $token1")
//                } ?: run {
//                    Log.d("RetrofitClient", "No token available for request")
//                }
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


    val homeApi: HomeApi by lazy {
        retrofit.create(HomeApi::class.java)
    }
}
