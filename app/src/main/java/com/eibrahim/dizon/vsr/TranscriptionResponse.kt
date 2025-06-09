package com.eibrahim.dizon.vsr

data class TranscriptionResponse(
    val status: String,
    val transcribed_text: String?,
    val error: String?
)