package com.reap.presentation.ui.main

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
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
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val postRecognizeUrlUseCase: PostRecognizeUrlUseCase,
    application: Application
) : AndroidViewModel(application) {

    private val _uploadStatus = MutableStateFlow<UploadStatus>(UploadStatus.Idle)
    val uploadStatus: StateFlow<UploadStatus> = _uploadStatus.asStateFlow()

    private val _audioFileId = MutableStateFlow<String?>(null)
    val audioFileId: StateFlow<String?> = _audioFileId.asStateFlow()

    private val _onUploadSuccess = MutableStateFlow(false)
    val onUploadSuccess = _onUploadSuccess.asStateFlow()

    fun uploadAudioFile(uri: Uri) {
        viewModelScope.launch {
            _uploadStatus.value = UploadStatus.Uploading
            try {
                val mediaPart = prepareFilePart(uri)
                val fileId = postRecognizeUrlUseCase.invoke("TEST", mediaPart)
                _audioFileId.value = fileId
                _uploadStatus.value = UploadStatus.Success(fileId)
                _onUploadSuccess.value = true
            } catch (e: Exception) {
                _uploadStatus.value = UploadStatus.Error(e.message ?: "An unknown error occurred.")
            }
        }
    }

    private fun prepareFilePart(fileUri: Uri): MultipartBody.Part {
        val context = getApplication<Application>()
        val contentResolver = context.contentResolver

        try {
            // 파일을 InputStream으로 열기
            val inputStream = contentResolver.openInputStream(fileUri)
            val originalFileName = getOriginalFileName(contentResolver, fileUri) ?: "unknown_file"

            // MIME 타입 가져오기
            val mimeType = contentResolver.getType(fileUri) ?: "application/octet-stream"
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

            inputStream?.use { input ->
                fileOutputStream.use { output ->
                    input.copyTo(output)
                }
            }

            // 파일을 RequestBody로 변환
            val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
            return MultipartBody.Part.createFormData("media", filenameWithExtension, requestFile)

        } catch (e: FileNotFoundException) {
            Log.e("FileError", "File not found: ${e.message}")
            throw e
        } catch (e: IOException) {
            Log.e("FileError", "IO error: ${e.message}")
            throw e
        }
    }

    // URI에서 파일 이름을 안전하게 추출하는 함수
    private fun getOriginalFileName(contentResolver: ContentResolver, fileUri: Uri): String? {
        var fileName: String? = null
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

    fun resetUploadSuccess() {
        _onUploadSuccess.value = false
    }
}

sealed class UploadStatus {
    object Idle : UploadStatus()
    object Uploading : UploadStatus()
    data class Success(val fileId: String) : UploadStatus()
    data class Error(val message: String) : UploadStatus()
}