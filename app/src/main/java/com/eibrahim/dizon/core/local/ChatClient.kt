package com.eibrahim.dizon.core.local

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.BufferedReader
import java.io.InputStreamReader

fun main() {
    // Create an OkHttpClient instance
    val client = OkHttpClient()

    // Prepare the JSON payload with your messages. Adjust as needed.
    val jsonPayload = """
        {
            "messages": [
                {"role": "user", "content": "Hello, how are you?"}
            ]
        }
    """.trimIndent()

    // Create a RequestBody with the JSON payload
    val requestBody = jsonPayload.toRequestBody("application/json".toMediaTypeOrNull())

    // Build the POST request targeting your Flask endpoint
    val request = Request.Builder()
        .url("http://127.0.0.1:5000/chat")
        .post(requestBody)
        .build()

    // Execute the request synchronously and process the streaming response
    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            println("Unexpected response code: ${response.code}")
            return
        }

        // Get the response as a byte stream and wrap it in a BufferedReader
        val inputStream = response.body?.byteStream()
        if (inputStream != null) {
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            // Read and print each line as it arrives
            while (reader.readLine().also { line = it } != null) {
                println(line)
            }
        } else {
            println("Response body is null")
        }
    }
}
