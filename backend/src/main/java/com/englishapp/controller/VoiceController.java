package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.voice.VoiceService;
import com.englishapp.voice.dto.ScoreResponse;
import com.englishapp.voice.dto.TtsRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
     * @param request 包含文本、语言、业务语音档案和缓存策略的请求
     * @return 音频二进制流;TTS 服务失败时返回 503
     */
    @PostMapping("/tts")
    public ResponseEntity<byte[]> textToSpeech(@Valid @RequestBody TtsRequest request) {
        byte[] audio = voiceService.textToSpeech(
                request.getText(), request.getLan(), request.getVoiceProfile(),
                !Boolean.FALSE.equals(request.getCacheable()));
        // 空音频说明 TTS 服务失败,返回 503 让前端识别错误而非创建无效 Audio
        if (audio == null || audio.length == 0) {
            return ResponseEntity.status(503).body(new byte[0]);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
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
