package com.reap.presentation.ui.main

import android.app.Application
import android.webkit.MimeTypeMap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.reap.domain.usecase.main.PostRecognizeUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
class MainViewModel @Inject constructor(
    private val postRecognizeUrlUseCase: PostRecognizeUrlUseCase,
    application: Application
) : AndroidViewModel(application) {
    private val _selectedAudioFile = MutableStateFlow<Uri?>(null)
    val selectedAudioFile: StateFlow<Uri?> = _selectedAudioFile.asStateFlow()

    private val _uploadStatus = MutableStateFlow<UploadStatus>(UploadStatus.Idle)
    val uploadStatus: StateFlow<UploadStatus> = _uploadStatus.asStateFlow()

    private val _audioFileId = MutableStateFlow<String?>(null)
    val audioFileId: StateFlow<String?> = _audioFileId.asStateFlow()

    fun selectAudioFile(uri: Uri) {
        _selectedAudioFile.value = uri
    }

    fun uploadAudioFile() {
        viewModelScope.launch {
            _uploadStatus.value = UploadStatus.Uploading
            try {
                val mediaPart = prepareFilePart(_selectedAudioFile.value!!)
                val fileId = postRecognizeUrlUseCase.invoke("JJB", "ko-KR", mediaPart)
                //Log.d("MainViewModel", fileId)

                _audioFileId.value = fileId
                _uploadStatus.value = UploadStatus.Success(fileId)
            } catch (e: Exception) {
                _uploadStatus.value = UploadStatus.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }
    private fun prepareFilePart(fileUri: Uri): MultipartBody.Part {
        val context = getApplication<Application>()
        val contentResolver = context.contentResolver
        val fileInputStream = contentResolver.openInputStream(fileUri)

        // MIME 타입으로부터 파일 확장자 추출
        val mimeType = contentResolver.getType(fileUri)
        var extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)

        // 일부 알려진 MIME 타입에 대해 수동 설정
        if (mimeType == "audio/x-m4a") {
            extension = "m4a"
        }

        // 파일 이름 설정 (확장자 포함)
        val filename = fileUri.lastPathSegment?.let {
            if (extension != null && it.contains('.').not()) it + ".$extension" else it
        } ?: "tempFile.$extension"

        Log.d("MainViewModel", "Filename: $filename, Extension: $extension")

        val file = File(context.cacheDir, filename)
        val fileOutputStream = FileOutputStream(file)

        fileInputStream?.copyTo(fileOutputStream)
        fileInputStream?.close()
        fileOutputStream.close()

        val requestFile = file.asRequestBody(mimeType?.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("media", file.name, requestFile)
    }
}

sealed class UploadStatus {
    object Idle : UploadStatus()
    object Uploading : UploadStatus()
    data class Success(val fileId: String) : UploadStatus()
    data class Error(val message: String) : UploadStatus()
}