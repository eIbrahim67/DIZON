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
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Llama {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    /**
     * Makes a POST request to your local Flask chat endpoint and streams the response.
     *
     * @param jsonPayload The JSON string payload (including full chat history).
     * @param onMessageReceived Callback invoked for each line received.
     * @param onError Callback invoked if an error occurs.
     * @param onComplete Callback invoked when the stream is fully read.
     */
    fun getChatStream(
        jsonPayload: String,
        onMessageReceived: (String) -> Unit,
        onError: (Exception) -> Unit,
        onComplete: () -> Unit
    ) {
        val requestBody = jsonPayload.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("http://10.0.2.2:5000/chat") // Adjust URL if necessary.
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    onError(IOException("Unexpected response code: ${response.code}"))
                    response.close()
                    return
                }
                response.body?.let { body ->
                    try {
                        body.charStream().buffered().useLines { lines ->
                            lines.forEach { line ->
                                onMessageReceived(line)
                            }
                        }
                        onComplete()
                    } catch (e: Exception) {
                        onError(e)
                    }
                } ?: onError(IOException("Response body is null"))
            }
        })
    }

    /**
     * Suspends until the streaming response is complete.
     */
    suspend fun fetchChatResponse(jsonPayload: String): String = suspendCoroutine { cont ->
        val conversationBuilder = StringBuilder()
        getChatStream(
            jsonPayload,
            onMessageReceived = { line ->
                conversationBuilder.append(line).append("\n")
            },
            onError = { error ->
                cont.resumeWithException(error)
            },
            onComplete = {
                cont.resume(conversationBuilder.toString())
            }
        )
    }
}
