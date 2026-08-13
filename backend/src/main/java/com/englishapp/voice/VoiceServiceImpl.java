package com.englishapp.voice;

import com.englishapp.voice.dto.ScoreResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 统一编排百度场景化 TTS、缓存和发音评测。 */
@Service
public class VoiceServiceImpl implements VoiceService {
    private static final Logger log = LoggerFactory.getLogger(VoiceServiceImpl.class);
    private static final String CACHE_PROVIDER_ID = "baidu";

    private final BaiduVoiceService baiduVoiceService;
    private final TtsCache cache;

    public VoiceServiceImpl(BaiduVoiceService baiduVoiceService,
                            TtsCache cache) {
        this.baiduVoiceService = baiduVoiceService;
        this.cache = cache;
    }

    @Override
    public byte[] textToSpeech(String text, String lan, String voiceProfile, boolean cacheable) {
        VoiceProfile profile = VoiceProfile.fromKey(voiceProfile);
        String language = "zh".equalsIgnoreCase(lan) ? "zh" : "en";
        if (!baiduVoiceService.isAvailable()) return new byte[0];
        byte[] cached = cacheable
                ? cache.read(CACHE_PROVIDER_ID, text, language, profile)
                : null;
        if (cached != null) return cached;

        byte[] audio = baiduVoiceService.synthesize(text, language, profile);
        if (audio == null || audio.length == 0) {
            log.warn("百度 TTS 未返回可用音频: profile={}", profile.getKey());
            return new byte[0];
        }
        if (cacheable) cache.write(CACHE_PROVIDER_ID, text, language, profile, audio);
        return audio;
    }

    @Override
    public String speechToText(byte[] audioData) {
        return baiduVoiceService.speechToText(audioData);
    }

    @Override
    public ScoreResponse scorePronunciation(byte[] audioData, String text) {
        return baiduVoiceService.scorePronunciation(audioData, text);
    }
}
