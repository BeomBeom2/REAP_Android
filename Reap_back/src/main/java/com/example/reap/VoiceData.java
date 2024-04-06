package com.example.reap;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "voiceData")
public class VoiceData {
    @Id
    private String id;
    private String userId;
    private byte[] audioContent; // 실제 음성 데이터
    private String transcription; // 음성 데이터의 텍스트 변환 결과
    private long timestamp; // 데이터가 생성된 시간

    // 기본 생성자, Getter, Setter
}
