package com.reap.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecordingDetailIntent(
    val timestamp: String,
    val elapsedTime: String,
    val speaker: String,
    val text: String
) : Parcelable
