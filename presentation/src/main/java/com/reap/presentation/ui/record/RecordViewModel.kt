package com.reap.presentation.ui.record

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.reap.domain.usecase.main.PostRecognizeUrlUseCase
import com.reap.presentation.ui.main.UploadStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val postRecognizeUrlUseCase: PostRecognizeUrlUseCase,
    application: Application
) : AndroidViewModel(application) {

    enum class RecordingState {
        IDLE, RECORDING, PAUSED
    }

    val recorder = AudioRecorder()
    private val _recordingState = MutableStateFlow(RecordingState.IDLE)
    val recordingState: StateFlow<RecordingState> = _recordingState.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _recordingTime = MutableStateFlow(0)
    val recordingTime: StateFlow<Int> = _recordingTime.asStateFlow()

    private val _volumeLevels = MutableStateFlow<List<Int>>(listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
    val volumeLevels: StateFlow<List<Int>> = _volumeLevels.asStateFlow()

    private val _uploadStatus = MutableStateFlow<UploadStatus>(UploadStatus.Idle)
    val uploadStatus: StateFlow<UploadStatus> = _uploadStatus.asStateFlow()

    private var timerJob: Job? = null

    fun uploadAudioFile(uri: Uri, topic : String) {
        viewModelScope.launch {
            try {
                val mediaPart = prepareFilePart(uri)
                val fileId = postRecognizeUrlUseCase(topic, mediaPart)
                _uploadStatus.value = UploadStatus.Success(fileId)
            } catch (e: Exception) {
                _uploadStatus.value = UploadStatus.Error(e.message ?: "파일 업로드 중 에러가 발생했습니다.")
            }
        }
    }

    fun resetUploadStatus() {
        _uploadStatus.value = UploadStatus.Idle
    }

    fun startRecording(fileName : String) {
        val filePath = getApplication<Application>().filesDir.absolutePath + "/$fileName.m4a"
        recorder.startRecording(filePath)
        _recordingState.value = RecordingState.RECORDING
        startTimer()
    }

    fun pauseRecording() {
        if (_recordingState.value == RecordingState.RECORDING) {
            recorder.pauseRecording()
            _recordingState.value = RecordingState.PAUSED
            stopTimer()
        }
    }

    fun stopRecordingAndUpload(topic: String, fileName: String) {
        recorder.stopRecording()
        _recordingState.value = RecordingState.IDLE
        stopTimer()

        // 기존 임시 파일 경로
        val tempFilePath = recorder.currentFilePath
        val tempFile = File(tempFilePath)

        // 사용자 지정 파일 이름으로 새로운 파일 경로 생성
        val newFilePath = tempFile.parent + "/$fileName.m4a"
        val newFile = File(newFilePath)

        // 파일 이름 변경 시도 (같은 디렉터리 내에서 이름 변경이므로 renameTo 사용)
        val success = tempFile.renameTo(newFile)
        if (success) {
            // 이름 변경 실패 시 처리 로직 (필요 시 로그 등 추가)
            Log.d("RecordViewModel", "$fileName 이름 변경에 실패")

            // 새 파일 경로를 URI로 변환
            val uri = Uri.fromFile(newFile)
            recorder.currentFilePath = newFilePath
            // 파일 업로드 실행
            viewModelScope.launch {
                uploadAudioFile(uri, topic)
            }
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

            while (_recordingState.value == RecordingState.RECORDING) {
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

    private fun getOriginalFileName(contentResolver: ContentResolver, fileUri: Uri): String {
        var fileName = "unknown"
        val cursor = contentResolver.query(fileUri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    private fun prepareFilePart(fileUri: Uri): MultipartBody.Part {
        val context = getApplication<Application>()
        val fileInputStream = context.contentResolver.openInputStream(fileUri)

        // `recorder.currentFilePath`에서 파일 이름과 확장자 추출
        val currentFilePath = recorder.currentFilePath ?: "recording.m4a"
        val file = File(currentFilePath)
        val originalFileName = file.name // 파일 이름과 확장자 포함된 이름 추출
        val mimeType = context.contentResolver.getType(fileUri) ?: "audio/mp4" // 기본 MIME 타입을 "audio/mp4"로 설정

        Log.d("MainViewModel", "Filename: $originalFileName, MIME Type: $mimeType")

        // 임시 파일 생성
        val tempFile = File(context.cacheDir, originalFileName)
        val fileOutputStream = FileOutputStream(tempFile)

        fileInputStream?.use { input ->
            fileOutputStream.use { output ->
                input.copyTo(output)
            }
        }
        Log.d("RecordViewModel", "Filename: $originalFileName, MIME Type: $mimeType")

        val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("media", originalFileName, requestFile)
    }


    private fun calculateRMS(buffer: ShortArray, readSize: Int): Double {
        var sum = 0.0
        for (i in 0 until readSize) {
            sum += buffer[i] * buffer[i]
        }
        return Math.sqrt(sum / readSize)
    }
}