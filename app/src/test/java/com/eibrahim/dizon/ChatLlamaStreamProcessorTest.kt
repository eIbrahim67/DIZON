package com.eibrahim.dizon

import com.eibrahim.dizon.chatbot.data.network.ChatLlamaStreamProcessor
import com.eibrahim.dizon.chatbot.data.network.HttpClient
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ChatLlamaStreamProcessorTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var fakeHttpClient: HttpClient
    private lateinit var processor: ChatLlamaStreamProcessor

    @Before
    fun setUp() {
        mockWebServer = MockWebServer().apply { start() }

        // Mockito-inline can mock final classes
        fakeHttpClient = mock()

        // Whenever post(...) is called, redirect to MockWebServer
        doAnswer { invocation ->
            val jsonPayload: String = invocation.getArgument(0)
            val callback: Callback = invocation.getArgument(2)

            val body = jsonPayload
                .toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder()
                .url(mockWebServer.url("/chat"))
                .post(body)
                .build()

            OkHttpClient()
                .newCall(request)
                .enqueue(callback)
            null
        }.`when`(fakeHttpClient).post(any(), any(), any())

        processor = ChatLlamaStreamProcessor(fakeHttpClient)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `stream success - emits each line then complete`() {
        val lines = listOf("Hello", "World", "!")
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(lines.joinToString("\n"))
                .setHeader("Content-Type", "text/plain")
        )

        val received = mutableListOf<String>()
        val latch = CountDownLatch(1)

        processor.getChatLlamaStream(
            jsonPayload = """{"foo":"bar"}""",
            onMessageReceived = { received.add(it) },
            onError           = { e -> fail("Should not get error: ${e.message}") },
            onReceiving       = { /*noop*/ },
            onComplete        = { latch.countDown() }
        )

        assertTrue("Did not complete in time", latch.await(5, TimeUnit.SECONDS))
        assertEquals(lines, received)
    }

    @Test
    fun `non-200 response triggers onError`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Server exploded")
        )

        var errorMsg: String? = null
        val latch = CountDownLatch(1)

        processor.getChatLlamaStream(
            jsonPayload = "{}",
            onMessageReceived = { fail("Should not receive messages") },
            onError           = {
                errorMsg = it.message
                latch.countDown()
            },
            onReceiving       = { /*noop*/ },
            onComplete        = { fail("Should not complete") }
        )

        assertTrue("Error callback not invoked", latch.await(3, TimeUnit.SECONDS))
        assertTrue(errorMsg!!.contains("Unexpected response code"))
    }

    @Test
    fun `empty body still completes with no messages`() {
        // Enqueue a 200 with an empty body
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("")  // empty
                .setHeader("Content-Type", "text/plain")
        )

        val received = mutableListOf<String>()
        val latch = CountDownLatch(1)
        var errorCalled = false

        processor.getChatLlamaStream(
            jsonPayload = "{}",
            onMessageReceived = { received.add(it) },
            onError           = {
                errorCalled = true
                latch.countDown()
            },
            onReceiving       = { /*noop*/ },
            onComplete        = { latch.countDown() }
        )

        assertTrue("Callback did not fire", latch.await(3, TimeUnit.SECONDS))
        assertFalse("onError should not have been called", errorCalled)
        assertTrue("No messages should have been received", received.isEmpty())
    }
}
