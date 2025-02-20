package com.eibrahim.dizon.core.local

import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.IOException
import java.util.concurrent.TimeUnit

class Llama {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()
    /**
     * Makes a POST request to your local Flask chat endpoint and streams the response.
     *
     * @param jsonPayload The JSON string payload (including your messages).
     * @param onMessageReceived Callback invoked for each chunk received.
     * @param onError Callback invoked if an error occurs.
     */
    fun getChatStream(
        jsonPayload: String,
        onMessageReceived: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val requestBody = jsonPayload.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("http://10.0.2.2:5000/chat") // Adjust the URL if necessary.
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    onError(IOException("Unexpected response code: ${response.code}"))
                    return
                }
                response.body?.let { body ->
                    val reader = BufferedReader(InputStreamReader(body.byteStream()))
                    try {
                        var line: String?
                        val reply = StringBuilder()
                        while (reader.readLine().also { line = it } != null) {
                            reply.append(line)
                        }
                            onMessageReceived(reply.toString())
                    } catch (e: Exception) {
                        onError(e)
                    } finally {
                        reader.close()
                    }
                } ?: onError(IOException("Response body is null"))
            }
        })
    }
}
