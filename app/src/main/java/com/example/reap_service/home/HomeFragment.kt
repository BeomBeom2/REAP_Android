package com.example.reap_service.home

import androidx.fragment.app.Fragment
import com.example.reap_service.databinding.FragmentHomeBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reap_service.R
import com.example.reap_service.recording.RecordType
import com.example.reap_service.recording.Recording
import com.example.reap_service.recording.RecordingBottomSheetFragment
import com.example.reap_service.search.CalendarFragment

class HomeFragment : Fragment(), RecordingBottomSheetFragment.RecordingListener {
    private var recordings: MutableList<Recording> = mutableListOf()
    private lateinit var  adapter : RecordingsAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupButton()
    }

    private fun setupButton() {
        binding.calendarButtonLayout.setOnClickListener {
            navigateToCalendarFragment()
        }
        binding.recordButtonLayout.setOnClickListener {
            val bottomSheet = RecordingBottomSheetFragment().apply {
                recordingListener = this@HomeFragment
            }
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
    }

    private fun navigateToCalendarFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, CalendarFragment())
        transaction.addToBackStack(null)  // Optional: Add transaction to the back stack
        transaction.commit()
    }

    private fun setupRecyclerView() {
        recordings = loadRecordings() // Implement this method to load recordings from storage
        adapter = RecordingsAdapter(recordings)
        binding.recyclerViewRecentRecordings.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewRecentRecordings.adapter = adapter
    }

    private fun loadRecordings(): MutableList<Recording> {
        return mutableListOf(
            Recording(RecordType.LECTURE,"Recording 1", "/path/to/file1.wav", "3:45", "2022-06-01"),
            Recording(RecordType.CHAT,"Recording 2", "/path/to/file2.wav", "2:30", "2022-06-02"),
            Recording(RecordType.MEETING,"Recording 3", "/path/to/file3.wav", "2:50", "2022-06-03"),
         )
    }

    override fun onRecordingAdded(newRecording: Recording) {
        recordings.add(newRecording)
        adapter.notifyDataSetChanged()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
