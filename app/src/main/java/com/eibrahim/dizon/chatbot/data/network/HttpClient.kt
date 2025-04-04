package com.eibrahim.dizon.chatbot.data.network

import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class HttpClient {

    private val clint = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    /**
     * Sends a POST request with the given JSON payload to the specified URL.
     *
     * @param jsonPayload The JSON body as a String.
     * @param url The endpoint URL.
     * @param callback The callback to handle the asynchronous response.
     */

    fun post(jsonPayload: String, url: String, callback: Callback) {

        val requestBody = jsonPayload.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        clint.newCall(request).enqueue(callback)

    }

}