package com.englishapp.voice;

import com.englishapp.config.VoiceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "voice.provider", havingValue = "baidu")
public class BaiduVoiceService implements VoiceService {

    private static final Logger log = LoggerFactory.getLogger(BaiduVoiceService.class);

    private final VoiceProperties voiceProperties;

    public BaiduVoiceService(VoiceProperties voiceProperties) {
        this.voiceProperties = voiceProperties;
    }

    @Override
    public byte[] textToSpeech(String text) {
        log.info("TTS request for text: {}", text);
        return new byte[0];
    }

    @Override
    public String speechToText(byte[] audioData) {
        log.info("ASR request, audio length: {} bytes", audioData.length);
        return "";
    }
}
