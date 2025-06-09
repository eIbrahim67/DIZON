package com.eibrahim.dizon.chatbot.data.network

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// Responsible for processing chat responses from the HTTP client.
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
                        //Converts the response body into a character stream.
                        body.charStream()
                            //Buffers the stream

                            /*Buffering the stream is essential for performance and efficiency when reading text data.
                            The `charStream()` method returns a `Reader`,
                            but that reader might not be buffered,
                            which means that every small read request could result in a separate I/O operation.
                            By calling `.buffered()`, we wrap the stream in a `BufferedReader`,
                            which reads data in larger chunks, reduces the number of I/O calls,
                            and efficiently provides methods like `useLines` for processing the stream line by line.
                            This approach is particularly beneficial when handling network streams
                            where reading efficiency can impact overall performance.*/

                            /*Buffering data is generally recommended because it minimizes the number of I/O operations
                            by reading larger chunks of data at once.*/
                            .buffered()
                            //processes it line by line
                            .useLines { lines ->
                                //For each line read, calls the onMessageReceived callback
                                lines.forEach { line ->
                                    onMessageReceived(line)
                                }
                            }
                        onComplete()
                    } catch (e: Exception) {
                        onError(e)
                    } finally {
                        // Ensure that the response is closed to avoid leaks.
                        response.close()
                    }
                } ?: onError(IOException("Response body is null"))
            }
        })
    }

}