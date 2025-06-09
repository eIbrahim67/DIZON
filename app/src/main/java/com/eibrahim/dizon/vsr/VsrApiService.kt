package com.eibrahim.dizon.vsr

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface VsrApiService {
    @Multipart
    @POST("/transcribe")
    suspend fun transcribeAudio(
        @Part file: MultipartBody.Part,
        @Query("language") language: String = "en",
        @Query("model_size") modelSize: String = "small",
        @Query("task") task: String = "transcribe"
    ): Response<TranscriptionResponse>
}