package com.reap.presentation.ui.main

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
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
    private val _uploadStatus = MutableStateFlow<UploadStatus>(UploadStatus.Idle)
    val uploadStatus: StateFlow<UploadStatus> = _uploadStatus.asStateFlow()

    private val _onUploadSuccess = MutableStateFlow(false)
    val onUploadSuccess = _onUploadSuccess.asStateFlow()

    fun uploadAudioFile(uri: Uri) {
        viewModelScope.launch {
            _uploadStatus.value = UploadStatus.Uploading
            try {
                Log.e("MainViewModel", "uploadAudioFile")
                val mediaPart = prepareFilePart(uri)
                val fileId = postRecognizeUrlUseCase("TEST", mediaPart)
                _uploadStatus.value = UploadStatus.Success(fileId)
                _onUploadSuccess.value = true
            } catch (e: Exception) {
                _uploadStatus.value = UploadStatus.Error(e.message ?: "파일 업로드 중 에러가 발생했습니다.")
            }
        }
    }

    private fun prepareFilePart(fileUri: Uri): MultipartBody.Part {
        val context = getApplication<Application>()
        val contentResolver = context.contentResolver
        val fileInputStream = contentResolver.openInputStream(fileUri)

        // 원본 파일 이름 가져오기
        val originalFileName = getOriginalFileName(contentResolver, fileUri)

        // MIME 타입 가져오기
        val mimeType = contentResolver.getType(fileUri) ?: "audio/mp4"

        Log.e("MainViewModel", "Filename: $originalFileName, MIME Type: $mimeType")

        // 임시 파일 생성
        val tempFile = File(context.cacheDir, originalFileName)
        val fileOutputStream = FileOutputStream(tempFile)

        fileInputStream?.use { input ->
            fileOutputStream.use { output ->
                input.copyTo(output)
            }
        }

        val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("media", originalFileName, requestFile)
    }

    private fun getOriginalFileName(contentResolver: ContentResolver, fileUri: Uri): String {
        var fileName = "unknown_file"
        contentResolver.query(fileUri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                fileName = cursor.getString(nameIndex)
            }
        }
        return fileName
    }

    fun resetUploadSuccess() {
        _onUploadSuccess.value = false
    }
}

sealed class UploadStatus {
    data object Idle : UploadStatus()
    data object Uploading : UploadStatus()
    data class Success(val fileId: String) : UploadStatus()
    data class Error(val message: String) : UploadStatus()
}