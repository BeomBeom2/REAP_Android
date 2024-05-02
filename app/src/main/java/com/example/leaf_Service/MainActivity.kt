package com.example.leaf_Service

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.speech.tts.TextToSpeech
import androidx.lifecycle.lifecycleScope
import com.example.leaf_service.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.util.Locale

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
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
        lifecycleScope.launch(Dispatchers.IO) { // lifecycleScope 사용
            try {
                openFileOutput("recorded_text.txt", MODE_APPEND).use {
                    it.write((text + "\n").toByteArray())
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Text saved", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Failed to save text: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
        speechRecognizer.destroy()
    }
}
