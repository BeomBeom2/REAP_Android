package com.example.Reaf_Service

data class VoiceData(
        val id: String? = null,
        val userId: String,
        val audioContent: String,
        val transcription: String,
        val timestamp: Long
)
