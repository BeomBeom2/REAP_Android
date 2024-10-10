package com.reap.presentation.ui.record

import android.media.MediaRecorder
import android.os.Build
import java.io.File

class AudioRecorder {
    private var mediaRecorder: MediaRecorder? = null
    var currentFilePath: String? = null
    private var isPaused: Boolean = false

    fun startRecording(filePath: String) {
        currentFilePath = filePath
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(filePath)
            prepare()
            start()
        }
        isPaused = false
    }

    fun pauseRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mediaRecorder != null && !isPaused) {
            mediaRecorder?.pause()
            isPaused = true
        } else {
            throw UnsupportedOperationException("Pause is only supported on Android N or higher")
        }
    }

    fun resumeRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mediaRecorder != null && isPaused) {
            mediaRecorder?.resume()
            isPaused = false
        } else {
            throw UnsupportedOperationException("Resume is only supported on Android N or higher")
        }
    }

    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }

    fun getFileUri(): File? {
        return if (currentFilePath != null) File(currentFilePath!!) else null
    }
}

