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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import api.ApiService

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var apiService: ApiService  // ApiService 변수 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)


        val view = binding.root
        setContentView(view)

        // 권한 설정
        requestPermission()

        // Retrofit 인스턴스 생성
        val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // 로컬 서버 주소, 실제 서버 주소로 변경 필요
                .addConverterFactory(GsonConverterFactory.create()) // JSON 파싱을 위한 GSON 컨버터
                .build()

        apiService = retrofit.create(ApiService::class.java) // ApiService 생성

        // RecognizerIntent 생성 및 수정
        val intent  = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName) // 여분의 키
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR") // 언어 설정
            // 블루투스 마이크를 사용하기 위한 오디오 소스 설정
            putExtra(RecognizerIntent.EXTRA_AUDIO_SOURCE, MediaRecorder.AudioSource.VOICE_COMMUNICATION)
        }

        // <말하기> 버튼 눌러서 음성인식 시작
        binding.btnSpeech.setOnClickListener {
            // 새 SpeechRecognizer 를 만드는 팩토리 메서드
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@MainActivity)
            speechRecognizer.setRecognitionListener(recognitionListener)    // 리스너 설정
            speechRecognizer.startListening(intent)                         // 듣기 시작
        }

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
        }

        private fun sendVoiceDataToServer(voiceData: String) {
            // ex. VoiceData 객체 생성
            val dataToSend = VoiceData(userId = "userId", audioContent = voiceData, transcription = "", timestamp = System.currentTimeMillis())

            // 서버로 전송
            apiService.uploadVoiceData(dataToSend).enqueue(object : Callback<VoiceDataResponse> {
                override fun onResponse(call: Call<VoiceDataResponse>, response: Response<VoiceDataResponse>) {
                    if (response.isSuccessful) {
                        // 성공적으로 서버로부터 응답을 받았을 때의 처리
                        Toast.makeText(applicationContext, "데이터 전송 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        // 서버로부터 에러 응답을 받았을 때의 처리
                        Toast.makeText(applicationContext, "데이터 전송 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<VoiceDataResponse>, t: Throwable) {
                    // 네트워크 문제 등으로 인한 실패 처리
                    Toast.makeText(applicationContext, "데이터 전송 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // 인식 결과가 준비되면 호출 (sendVoiceDataToServer 메서드 통해 서버로 데이터 전송)
        override fun onResults(results: Bundle) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            matches?.let {
                if (it.isNotEmpty()) {
                    // 인식된 첫 번째 결과를 서버로 전송
                    sendVoiceDataToServer(it[0])
                }
            }
        }
        // 부분 인식 결과를 사용할 수 있을 때 호출
        override fun onPartialResults(partialResults: Bundle) {}
        // 향후 이벤트를 추가하기 위해 예약
        override fun onEvent(eventType: Int, params: Bundle) {}


    }
}