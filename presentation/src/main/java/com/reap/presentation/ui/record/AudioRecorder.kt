package com.reap.presentation.ui.record

import android.media.MediaRecorder
import java.io.File

class AudioRecorder {
    private var mediaRecorder: MediaRecorder? = null
    var currentFilePath: String? = null

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
