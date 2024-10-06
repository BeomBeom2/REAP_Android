package com.reap.presentation.ui.record

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.reap.domain.usecase.main.PostRecognizeUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
@HiltViewModel
class RecordViewModel@Inject constructor(
    private val postRecognizeUrlUseCase: PostRecognizeUrlUseCase,
    application: Application
) : AndroidViewModel(application) {
    private val recorder = AudioRecorder()
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    fun startRecording() {
        val filePath = getApplication<Application>().filesDir.absolutePath + "/record.m4a"
        recorder.startRecording(filePath)
        _isRecording.value = true
    }

    fun stopRecordingAndUpload() {
        recorder.stopRecording()
        _isRecording.value = false
        uploadFile(Uri.fromFile(File(recorder.currentFilePath)))
    }

    private fun uploadFile(fileUri: Uri) {
        viewModelScope.launch {
            // 가정: 저장소 함수를 통해 업로드 처리
            // repository.uploadAudioFile(fileUri)
            // 업로드 응답 또는 오류 처리
        }
    }
}


