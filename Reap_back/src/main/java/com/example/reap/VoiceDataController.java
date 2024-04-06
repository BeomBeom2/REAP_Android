package com.example.reap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/voiceData")
public class VoiceDataController {

    @Autowired
    private VoiceDataRepository voiceDataRepository;

    @PostMapping
    public VoiceData addVoiceData(@RequestBody VoiceData voiceData) {
        return voiceDataRepository.save(voiceData);
    }

    // 기타 필요한 엔드포인트 구현
}
