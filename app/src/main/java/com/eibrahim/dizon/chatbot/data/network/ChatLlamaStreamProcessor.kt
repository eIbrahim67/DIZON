package com.eibrahim.dizon.chatbot.data.network

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Processes chat responses from an HTTP service using an HttpClient.
 *
 * @property httpClient The HTTP client used to send requests.
 */
class ChatLlamaStreamProcessor(private val httpClient: HttpClient) {

    private val url = "http://192.168.1.5:5000/chat"

    /**
     * Initiates a chat stream request.
     *
     * @param jsonPayload The JSON payload to send.
     * @param onMessageReceived Callback invoked for each line received.
     * @param onError Callback invoked when an error occurs.
     * @param onReceiving Callback invoked when the response starts streaming.
     * @param onComplete Callback invoked once the streaming completes.
     */
    fun getChatLlamaStream(
        jsonPayload: String,
        onMessageReceived: (String) -> Unit,
        onError: (Exception) -> Unit,
        onReceiving: () -> Unit,
        onComplete: () -> Unit
    ) {
        httpClient.post(jsonPayload, url, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e)
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    onError(IOException("Unexpected response code: ${response.code}"))
                    response.close()
                    return
                }
                onReceiving()
                response.body?.let { body ->
                    try {
                        body.charStream()
                            .buffered()
                            .useLines { lines ->
                                lines.forEach { line ->
                                    onMessageReceived(line)
                                }
                            }
                        onComplete()
                    } catch (e: Exception) {
                        onError(e)
                    } finally {
                        response.close()
                    }
                } ?: onError(IOException("Response body is null"))
            }
        })
    }
}