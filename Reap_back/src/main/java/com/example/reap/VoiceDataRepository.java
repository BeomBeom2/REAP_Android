package com.example.reap;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface VoiceDataRepository extends MongoRepository<VoiceData, String> {
    // 여기에 추가적인 메서드를 정의할 수 있습니다.
}
