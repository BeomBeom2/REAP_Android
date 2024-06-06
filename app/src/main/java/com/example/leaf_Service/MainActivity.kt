package com.example.Reaf_Service

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.media.MediaRecorder
import com.example.leaf_service.databinding.ActivityMainBinding
import com.github.squti.androidwaverecorder.WaveRecorder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var waveRecorder: WaveRecorder
    private var isRecording = false
    private lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermission() // Ensure permissions are requested for RECORD_AUDIO and WRITE_EXTERNAL_STORAGE

        val storageDir = externalCacheDir?.absolutePath ?: ""
        filePath = "$storageDir/recorded_audio.wav"

        waveRecorder = WaveRecorder(filePath)

        // <말하기> 버튼 눌러서 음성인식 시작
        binding.btnSpeech.setOnClickListener {
            // 새 SpeechRecognizer 를 만드는 팩토리 메서드
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@MainActivity)
            speechRecognizer.setRecognitionListener(recognitionListener)    // 리스너 설정
            speechRecognizer.startListening(intent)                         // 듣기 시작
        }

        binding.btnRecord.setOnClickListener {
            toggleRecording()
            updateRecordButtonText()
        }

        binding.btnRecordSend.setOnClickListener {
            uploadFile(File(filePath))
        }
    }

    private fun toggleRecording() {
        if (isRecording) {
            waveRecorder.stopRecording()
        } else {
            waveRecorder.startRecording()
        }
        isRecording = !isRecording
    }

    private fun updateRecordButtonText() {
        if (isRecording) {
            binding.btnRecord.text = "녹음 끝내기"
        } else {
            binding.btnRecord.text = "녹음 시작"
        }
    }

    private fun uploadFile(file: File) {
        // Assuming Retrofit setup for file upload is correctly implemented
        val requestFile = file.asRequestBody("audio/wav".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("media", file.name, requestFile)

        RetrofitClient.instance.uploadAudioFile(body, "ko-KR".toRequestBody(), "sync".toRequestBody(), getCurrentDate())
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, "File Uploaded Successfully", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(applicationContext, "Upload Failed: ${response.message()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun getCurrentDate(): RequestBody {
        // "yyyy-MM-dd HH:mm:ss" 포맷으로 날짜와 시간을 설정합니다.
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
        val date = dateFormat.format(Date())
        return date.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    // 권한 설정 메소드
    private fun requestPermission() {
        // 버전 체크, 권한 허용했는지 체크
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        }
    }


    // 리스너 설정
    private val recognitionListener: RecognitionListener = object : RecognitionListener {
        // 말하기 시작할 준비가되면 호출
        override fun onReadyForSpeech(params: Bundle) {
            Toast.makeText(applicationContext, "음성인식 시작", Toast.LENGTH_SHORT).show()
            binding.tvState.text = "이제 말씀하세요!"
        }

        // 말하기 시작했을 때 호출
        override fun onBeginningOfSpeech() {
            binding.tvState.text = "잘 듣고 있어요."
        }

        // 입력받는 소리의 크기를 알려줌
        override fun onRmsChanged(rmsdB: Float) {}

        // 말을 시작하고 인식이 된 단어를 buffer에 담음
        override fun onBufferReceived(buffer: ByteArray) {}

        // 말하기를 중지하면 호출
        override fun onEndOfSpeech() {
            binding.tvState.text = "끝!"
        }

        // 오류 발생했을 때 호출
        override fun onError(error: Int) {
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
                SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트웍 타임아웃"
                SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER 가 바쁨"
                SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
                else -> "알 수 없는 오류임"
            }
            binding.tvState.text = "에러 발생: $message"
            stopSpeechRecognizer()
        }

        override fun onResults(results: Bundle) {
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (matches != null && matches.isNotEmpty()) {
                binding.textView.text = matches[0] // 첫 번째 인식 결과 사용
                sendTextToServer(matches[0])
            }
            // 음성 인식 종료 및 리소스 해제
            stopSpeechRecognizer()
        }

        private fun stopSpeechRecognizer() {
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
            speechRecognizer.destroy()
        }

        private fun sendTextToServer(text: String) {
            val requestBody = text.toRequestBody("text/plain".toMediaTypeOrNull())
            RetrofitClient.instance.sendQuestion(requestBody)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                applicationContext,
                                "Text uploaded successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Failed to upload text: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(
                            applicationContext,
                            "Error uploading text: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        // 부분 인식 결과를 사용할 수 있을 때 호출
        override fun onPartialResults(partialResults: Bundle) {}

        // 향후 이벤트를 추가하기 위해 예약
        override fun onEvent(eventType: Int, params: Bundle) {}
    }
}