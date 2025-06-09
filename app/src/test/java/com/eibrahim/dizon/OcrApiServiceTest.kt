package com.eibrahim.dizon

import com.eibrahim.dizon.ocr.OcrApiService
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

class OcrApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: OcrApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))  // use mock URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(OcrApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `performOcr should return successful response`() = runBlocking {
        // Arrange
        val jsonResponse = """
            {
              "status": "success",
              "extracted_text": "Hello World",
              "error": null
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        // Fake image part
        val dummyImage = RequestBody.create("image/*".toMediaTypeOrNull(), byteArrayOf(1, 2, 3))
        val multipartBody = MultipartBody.Part.createFormData("file", "image.png", dummyImage)

        // Act
        val response = apiService.performOcr(multipartBody)

        // Assert
        val body = response.body()
        assertEquals(true, response.isSuccessful)
        assertEquals("success", body?.status)
        assertEquals("Hello World", body?.extracted_text)
        assertEquals(null, body?.error)
    }
}
