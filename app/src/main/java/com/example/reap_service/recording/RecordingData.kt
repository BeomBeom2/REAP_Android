package com.example.reap_service.recording

enum class RecordType{
    CHAT,
    MEETING,
    LECTURE,
}

data class Recording(val recordType : RecordType, val fileName: String, val filePath: String, val duration: String, val dateCreated: String)

