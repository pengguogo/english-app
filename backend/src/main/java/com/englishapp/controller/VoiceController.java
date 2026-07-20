package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.voice.VoiceService;
import com.englishapp.voice.dto.ScoreResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 语音 REST 控制器
 * <p>
 * 提供语音合成(TTS)与发音评测(/score)接口。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/voice")
public class VoiceController {

    private final VoiceService voiceService;

    /**
     * 构造语音控制器
     *
     * @param voiceService 语音服务
     */
    public VoiceController(VoiceService voiceService) {
        this.voiceService = voiceService;
    }

    /**
     * 文字转语音
     *
     * @param body 包含 text 字段(必填)和 lan 字段(可选,默认 "en")的 JSON
     * @return 音频二进制流
     */
    @PostMapping("/tts")
    public ResponseEntity<byte[]> textToSpeech(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        // 默认英文,支持中文(zh)
        String lan = body.getOrDefault("lan", "en");
        byte[] audio = voiceService.textToSpeech(text, lan);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(audio);
    }

    /**
     * 发音评测
     *
     * @param audio 录音文件(wav 16k 16bit mono)
     * @param text  参考文本
     * @return 评测结果(分数 + 反馈)
     * @throws IOException 当读取音频文件失败时抛出
     */
    @PostMapping("/score")
    public Result<ScoreResponse> scorePronunciation(
            @RequestParam("audio") MultipartFile audio,
            @RequestParam("text") String text) throws IOException {
        byte[] audioData = audio.getBytes();
        ScoreResponse response = voiceService.scorePronunciation(audioData, text);
        return Result.success(response);
    }
}
