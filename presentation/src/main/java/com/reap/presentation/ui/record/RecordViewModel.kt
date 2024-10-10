package com.reap.presentation.ui.record

import android.annotation.SuppressLint
import android.app.Application
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.reap.domain.usecase.main.PostRecognizeUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
@HiltViewModel
class RecordViewModel @Inject constructor(
    private val postRecognizeUrlUseCase: PostRecognizeUrlUseCase,
    application: Application
) : AndroidViewModel(application) {
    private val recorder = AudioRecorder()
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _recordingTime = MutableStateFlow(0)
    val recordingTime: StateFlow<Int> = _recordingTime.asStateFlow()

    private val _volumeLevels = MutableStateFlow<List<Int>>(listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    val volumeLevels: StateFlow<List<Int>> = _volumeLevels.asStateFlow()

    private var timerJob: Job? = null

    fun startRecording() {
        val filePath = getApplication<Application>().filesDir.absolutePath + "/record.m4a"
        recorder.startRecording(filePath)
        _isRecording.value = true
        _isPaused.value = false
        startTimer()
    }

    fun pauseRecording() {
        if (_isRecording.value) {
            recorder.pauseRecording()  // Assuming AudioRecorder has a pause functionality
            _isPaused.value = true
            stopTimer()
        }
    }

    fun resumeRecording() {
        if (_isPaused.value) {
            recorder.resumeRecording()  // Assuming AudioRecorder has a resume functionality
            _isPaused.value = false
            startTimer()
        }
    }

    fun stopRecordingAndUpload() {
        recorder.stopRecording()
        _isRecording.value = false
        _isPaused.value = false
        stopTimer()
        uploadFile(Uri.fromFile(File(recorder.currentFilePath)))
    }

    private fun uploadFile(fileUri: Uri) {
        viewModelScope.launch {
            // 가정: 저장소 함수를 통해 업로드 처리
            // repository.uploadAudioFile(fileUri)
            // 업로드 응답 또는 오류 처리
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _recordingTime.value += 1
                updateVolumeLevels()
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    @SuppressLint("MissingPermission")
    private fun updateVolumeLevels() {
        viewModelScope.launch(Dispatchers.Default) {
            val bufferSize = AudioRecord.getMinBufferSize(
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            val audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            val audioBuffer = ShortArray(bufferSize)

            if (audioRecord.state == AudioRecord.STATE_INITIALIZED) {
                audioRecord.startRecording()
            }

            while (_isRecording.value && !_isPaused.value) {
                val readSize = audioRecord.read(audioBuffer, 0, bufferSize)
                if (readSize > 0) {
                    val rms = calculateRMS(audioBuffer, readSize)
                    val level = (rms / 32767.0 * 20).toInt().coerceIn(0, 20) // 데시벨 레벨을 0-10 범위로 정규화
                    val updatedLevels = _volumeLevels.value.toMutableList().apply {
                        removeAt(0)
                        add(level)
                    }
                    _volumeLevels.value = updatedLevels
                }
            }

            audioRecord.stop()
            audioRecord.release()
        }
    }

    private fun calculateRMS(buffer: ShortArray, readSize: Int): Double {
        var sum = 0.0
        for (i in 0 until readSize) {
            sum += buffer[i] * buffer[i]
        }
        return Math.sqrt(sum / readSize)
    }
}



