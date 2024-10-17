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
import android.webkit.MimeTypeMap
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

    fun uploadAudioFile(uri: Uri) {
        viewModelScope.launch {
            try {
                val mediaPart = prepareFilePart(uri)
                val fileId = postRecognizeUrlUseCase.invoke("JJB", mediaPart)
                //_audioFileId.value = fileId
                _uploadStatus.value = UploadStatus.Success(fileId)
            } catch (e: Exception) {
                _uploadStatus.value = UploadStatus.Error(e.message ?: "An unknown error occurred.")
            }
        }
    }

    fun startRecording() {
        val filePath = getApplication<Application>().filesDir.absolutePath + "/record.m4a"
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

    fun stopRecordingAndUpload() {
        recorder.stopRecording()
        _recordingState.value = RecordingState.IDLE
        stopTimer()
        val uri = Uri.fromFile(File(recorder.currentFilePath))
        viewModelScope.launch {
            uploadAudioFile(uri)
        }
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
        val contentResolver = context.contentResolver
        val fileInputStream = contentResolver.openInputStream(fileUri)

        // 원본 파일 이름 가져오기
        val originalFileName = getOriginalFileName(contentResolver, fileUri)

        // MIME 타입 가져오기
        val mimeType = contentResolver.getType(fileUri)

        // MIME 타입에 따른 확장자 추출
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "unknown"

        // 파일 이름에 확장자 추가
        val filenameWithExtension = if (originalFileName.contains('.')) {
            originalFileName // 이미 확장자가 있는 경우 그대로 사용
        } else {
            "$originalFileName.$extension" // 확장자를 추가
        }

        Log.d("MainViewModel", "Filename: $filenameWithExtension, MIME Type: $mimeType")

        // 임시 파일 생성
        val tempFile = File(context.cacheDir, filenameWithExtension)
        val fileOutputStream = FileOutputStream(tempFile)

        fileInputStream?.use { input ->
            fileOutputStream.use { output ->
                input.copyTo(output)
            }
        }

        val requestFile = tempFile.asRequestBody(mimeType?.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("media", filenameWithExtension, requestFile)
    }

    private fun calculateRMS(buffer: ShortArray, readSize: Int): Double {
        var sum = 0.0
        for (i in 0 until readSize) {
            sum += buffer[i] * buffer[i]
        }
        return Math.sqrt(sum / readSize)
    }
}