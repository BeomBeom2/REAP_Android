package com.example.reap_service.recording

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.squti.androidwaverecorder.WaveRecorder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.reap_service.MainActivity
import com.example.reap_service.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.reap_service.databinding.FragmentRecordingBottomSheetBinding
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

class RecordingBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var waveRecorder: WaveRecorder
    private var isRecording = false
    private var recordingStartTime: String = ""
    private var filePath: String = ""
    private lateinit var storageDir: String
    var recordingListener: RecordingListener? = null
    private var _binding: FragmentRecordingBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecordingBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermission() //waveRecorder  초기화 전 Req Permission 체크 필요.

        storageDir = requireActivity().externalCacheDir?.absolutePath?:""

        //onViewCreated에서 waveRecorder를 만들지 않으면 AudioRecord 생성 못 함.
        recordingStartTime = getCurrentDateTime()
        filePath = "$storageDir/REAP_audio_$recordingStartTime.wav"
        Log.d("filePath Default set", "$filePath")
        waveRecorder = WaveRecorder(filePath)

        binding.btnRecording.setOnClickListener {
            toggleRecording()
            updateRecordButtonText()
        }

        binding.btnRecordingSend.setOnClickListener {
            uploadFile(File(filePath))
        }
    }

    private fun toggleRecording() {
        if (isRecording) {
            Log.d("filePath", "$filePath")
            waveRecorder.stopRecording()
        } else {
            recordingStartTime = getCurrentDateTime()
            filePath = "$storageDir/REAP_audio_$recordingStartTime.wav"
            waveRecorder = WaveRecorder(filePath)
            waveRecorder.startRecording()
        }
        isRecording = !isRecording
    }

    private fun updateRecordButtonText() {
        if (isRecording) {
            binding.btnRecording.text = "녹음 끝내기"
        } else {
            binding.btnRecording.text = "녹음 시작"
        }
    }
    // 권한 설정 메소드
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        }
    }

    private fun uploadFile(file: File) {
        val requestFile = file.asRequestBody("audio/wav".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("media", file.name, requestFile)
        val dateRequestBody = recordingStartTime.toRequestBody("text/plain".toMediaTypeOrNull())
        var flag : Boolean = false
        RetrofitClient.instance.uploadAudioFile(body, "ko-KR".toRequestBody(), "sync".toRequestBody(), dateRequestBody)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        flag = true
                        Toast.makeText(
                            requireContext(),
                            "File Uploaded Successfully",
                            Toast.LENGTH_LONG
                        ).show()


                    } else {
                        flag = false
                        Toast.makeText(requireContext(), "Upload Failed: ${response.message()}", Toast.LENGTH_LONG).show()
                        Log.d("uploadFile", "Upload Failed: ${response.message()}")
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.d("uploadFile", "Error: ${t.message}")
                }
            })
            if(flag){
                val newRecording = Recording(RecordType.CHAT, file.name, file.path, "Duration Placeholder", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(Date()))
                recordingListener?.onRecordingAdded(newRecording)
            }
    }


    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.KOREA)
        return dateFormat.format(Date())
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface RecordingListener {
        fun onRecordingAdded(newRecording: Recording)
    }
}
