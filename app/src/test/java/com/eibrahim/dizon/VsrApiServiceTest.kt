package com.eibrahim.dizon

import com.eibrahim.dizon.vsr.VsrApiService
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VsrApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: VsrApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))  // use mock base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(VsrApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `transcribeAudio should return successful transcription response`() = runBlocking {
        // Arrange
        val jsonResponse = """
            {
              "status": "success",
              "transcribed_text": "This is a test transcription",
              "error": null
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val dummyAudio = RequestBody.create("audio/wav".toMediaTypeOrNull(), byteArrayOf(1, 2, 3, 4))
        val multipartBody = MultipartBody.Part.createFormData("file", "audio.wav", dummyAudio)

        // Act
        val response = apiService.transcribeAudio(multipartBody)

        // Assert
        val body = response.body()
        assertEquals(true, response.isSuccessful)
        assertEquals("success", body?.status)
        assertEquals("This is a test transcription", body?.transcribed_text)
        assertEquals(null, body?.error)
    }
}
