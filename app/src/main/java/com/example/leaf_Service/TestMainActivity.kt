package com.example.leaf_Service

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.appcompat.app.AppCompatActivity
import com.example.Reaf_service.databinding.ActivityMainBinding
import java.util.Locale
import kotlinx.coroutines.*

private lateinit var binding: ActivityMainBinding

class TestMainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var speechRecognizer: SpeechRecognizer
    private var isListeningModeOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        // Text-To-Speech 초기화
        tts = TextToSpeech(this, this)

        // Speech-To-Text 초기화
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle?) {
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.let {
                        if (it in listOf("응", "어", "네", "예")) {
                            isListeningModeOn = true  // 청취 모드 활성화
                            startListening()
                        } else if (isListeningModeOn) {
                            saveToFile(it)  // 청취 모드에서 텍스트 파일로 저장
                        }
                    }
                }
                override fun onError(error: Int) = Unit
                override fun onReadyForSpeech(params: Bundle?) = Unit
                override fun onBeginningOfSpeech() = Unit
                override fun onRmsChanged(rmsdB: Float) = Unit
                override fun onBufferReceived(buffer: ByteArray) = Unit
                override fun onEndOfSpeech() = Unit
                override fun onPartialResults(partialResults: Bundle?) = Unit
                override fun onEvent(eventType: Int, params: Bundle?) = Unit
            })
        }
    }

    // Text-To-Speech 초기화 후 질문 실행
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.KOREAN
            tts.speak("청취 모드를 실행하시겠습니까?", TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    // 음성 인식 시작
    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN)
        }
        speechRecognizer.startListening(intent)
    }

    // 파일에 텍스트 저장
    private fun saveToFile(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            openFileOutput("recorded_text.txt", MODE_APPEND).use {
                it.write((text + "\n").toByteArray())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
        speechRecognizer.destroy()
    }
}



//class MainActivity11 : AppCompatActivity() {
//    private lateinit var speechRecognizer: SpeechRecognizer
//    private lateinit var apiService: ApiService  // ApiService 변수 선언
//    private val job = Job()
//    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//
//        val view = binding.root
//        setContentView(view)
//
//        // 권한 설정
//        requestPermission()
//
//        // Retrofit 인스턴스 생성
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://10.0.2.2:8080/") // 로컬 서버 주소, 실제 서버 주소로 변경 필요
//            .addConverterFactory(GsonConverterFactory.create()) // JSON 파싱을 위한 GSON 컨버터
//            .build()
//
//        apiService = retrofit.create(ApiService::class.java) // ApiService 생성
//
//        // RecognizerIntent 생성 및 수정
//        val intent  = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
//            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName) // 여분의 키
//            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR") // 언어 설정
//            // 블루투스 마이크를 사용하기 위한 오디오 소스 설정
//            putExtra(RecognizerIntent.EXTRA_AUDIO_SOURCE, MediaRecorder.AudioSource.VOICE_COMMUNICATION)
//        }
//
//        // <말하기> 버튼 눌러서 음성인식 시작
//        binding.btnSpeech.setOnClickListener {
//            // 새 SpeechRecognizer 를 만드는 팩토리 메서드
//            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@MainActivity)
//            speechRecognizer.setRecognitionListener(recognitionListener)    // 리스너 설정
//            speechRecognizer.startListening(intent)                         // 듣기 시작
//        }
//    }
//
//    // 권한 설정 메소드
//    private fun requestPermission() {
//        // 버전 체크, 권한 허용했는지 체크
//        if (Build.VERSION.SDK_INT >= 23 &&
//            ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECORD_AUDIO)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(this@MainActivity,
//                arrayOf(Manifest.permission.RECORD_AUDIO), 0)
//        }
//    }
//
//    // 리스너 설정
//    private val recognitionListener: RecognitionListener = object : RecognitionListener {
//        // 말하기 시작할 준비가 되면 호출
//        override fun onReadyForSpeech(params: Bundle) {
//            Toast.makeText(applicationContext, "음성인식 시작", Toast.LENGTH_SHORT).show()
//            binding.tvState.text = "이제 말씀하세요!"
//        }
//        // 말하기 시작했을 때 호출
//        override fun onBeginningOfSpeech() {
//            binding.tvState.text = "잘 듣고 있어요."
//        }
//        // 입력받는 소리의 크기를 알려줌
//        override fun onRmsChanged(rmsdB: Float) {}
//        // 말을 시작하고 인식이 된 단어를 buffer에 담음
//        override fun onBufferReceived(buffer: ByteArray) {}
//        // 말하기를 중지하면 호출
//        override fun onEndOfSpeech() {
//            binding.tvState.text = "끝!"
//        }
//        // 오류 발생했을 때 호출
//        override fun onError(error: Int) {
//            val message = when (error) {
//                SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
//                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
//                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
//                SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
//                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트웍 타임아웃"
//                SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
//                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER 가 바쁨"
//                SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
//                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
//                else -> "알 수 없는 오류임"
//            }
//            binding.tvState.text = "에러 발생: $message"
//        }
//
//        private fun sendVoiceDataToServer(voiceData: String) {
//            // ex. VoiceData 객체 생성
//            val dataToSend = VoiceData(userId = "userId", audioContent = voiceData, transcription = "", timestamp = System.currentTimeMillis())
//
//            // 서버로 전송
//            apiService.uploadVoiceData(dataToSend).enqueue(object : Callback<VoiceDataResponse> {
//                override fun onResponse(call: Call<VoiceDataResponse>, response: Response<VoiceDataResponse>) {
//                    if (response.isSuccessful) {
//                        // 성공적으로 서버로부터 응답을 받았을 때의 처리
//                        Toast.makeText(applicationContext, "데이터 전송 성공", Toast.LENGTH_SHORT).show()
//                    } else {
//                        // 서버로부터 에러 응답을 받았을 때의 처리
//                        Toast.makeText(applicationContext, "데이터 전송 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<VoiceDataResponse>, t: Throwable) {
//                    // 네트워크 문제 등으로 인한 실패 처리
//                    Toast.makeText(applicationContext, "데이터 전송 실패: ${t.message}", Toast.LENGTH_SHORT).show()
//                }
//            })
//        }
//
//        // 인식 결과가 준비되면 호출 (sendVoiceDataToServer 메서드 통해 서버로 데이터 전송)
//        override fun onResults(results: Bundle) {
//            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
//            matches?.let {
//                if (it.isNotEmpty()) {
//                    val resultText = it[0].toLowerCase()
//                    if (resultText.contains("예") || resultText.contains("어") || resultText.contains("응")) {
//                        coroutineScope.launch {
//                            startBackgroundListeningTask(resultText) // 인식된 음성을 파일에 저장
//                        }
//                    }
//                }
//            }
//        }
//        // 부분 인식 결과를 사용할 수 있을 때 호출
//        override fun onPartialResults(partialResults: Bundle) {}
//        // 향후 이벤트를 추가하기 위해 예약
//        override fun onEvent(eventType: Int, params: Bundle) {}
//    }
//
//    // 백그라운드에서 실행할 함수
//    private suspend fun startBackgroundListeningTask(transcribedText: String) {
//        val filename = "transcribed_speech.txt"
//        withContext(Dispatchers.IO) { // IO 스레드에서 실행
//            try {
//                val fileOutput = openFileOutput(filename, MODE_APPEND)
//                fileOutput.write((transcribedText + "\n").toByteArray())
//                fileOutput.close()
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(applicationContext, "Error writing file: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//
//            }
//        }
//        // 필요에 따라 메인 스레드에서 UI 업데이트
//        withContext(Dispatchers.Main) {
//            Toast.makeText(applicationContext, "Speech transcribed and saved to file", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        job.cancel() // 액티비티 종료 시 코루틴 작업 취소
//    }
//}
