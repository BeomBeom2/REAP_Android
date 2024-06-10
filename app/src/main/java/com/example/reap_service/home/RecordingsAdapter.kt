package com.example.reap_service.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reap_service.R
import com.example.reap_service.databinding.FragmentHomeBinding
import com.example.reap_service.databinding.FragmentRecordingItemBinding
import com.example.reap_service.recording.RecordType
import com.example.reap_service.recording.Recording

class RecordingsAdapter(private val recordings: MutableList<Recording>) : RecyclerView.Adapter<RecordingsAdapter.ViewHolder>() {

    class ViewHolder(val binding: FragmentRecordingItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentRecordingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recording = recordings[position]
        holder.binding.textViewFileName.text = recording.fileName
        holder.binding.textViewDuration.text = recording.duration
        holder.binding.textViewDateCreated.text = recording.dateCreated
        holder.binding.imageViewRecordType.setImageResource(getIconForRecordType(recording.recordType))
    }

    fun addRecording(recording: Recording) {
        recordings.add(recording)
        notifyItemInserted(recordings.size - 1)
    }

    private fun getIconForRecordType(type: RecordType): Int {
        return when (type) {
            RecordType.CHAT -> R.mipmap.ic_record_type_chat_foreground
            RecordType.MEETING -> R.mipmap.ic_record_type_meeting_foreground
            RecordType.LECTURE -> R.mipmap.ic_record_type_lecture_foreground
            else -> R.mipmap.ic_record_type_chat_foreground
        }
    }

    override fun getItemCount() = recordings.size
}
