package com.eibrahim.dizon.ocr

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.Response

data class OcrResponse(
    val status: String,
    val extracted_text: String?,
    val error: String?
)

interface OcrApiService {
    @Multipart
    @POST("/ocr")
    suspend fun performOcr(
        @Part file: MultipartBody.Part,
        @Query("language") language: String = "eng"
    ): Response<OcrResponse>
}