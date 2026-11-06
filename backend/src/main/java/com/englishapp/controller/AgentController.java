package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.common.ResultCode;
import com.englishapp.dto.AgentAsrResponseDTO;
import com.englishapp.dto.AgentGuideRequestDTO;
import com.englishapp.dto.AgentGuideResponseDTO;
import com.englishapp.dto.AgentTopicDTO;
import com.englishapp.repository.GuardianConsentRepository;
import com.englishapp.security.AuthException;
import com.englishapp.security.CurrentChild;
import com.englishapp.security.QuotaService;
import com.englishapp.service.AuthService;
import com.englishapp.service.ai.asr.AsrException;
import com.englishapp.service.ai.asr.AsrResult;
import com.englishapp.service.ai.asr.SpeechRecognizer;
import com.englishapp.service.ai.guide.GuideService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * 学习伙伴阶段 1：语音识别 + 课程向导。不接大模型，不保存音频和对话，日志不记音频与识别正文。
 */
@RestController
@RequestMapping("/api/v1/agent")
public class AgentController {

    private static final Logger log = LoggerFactory.getLogger(AgentController.class);
    static final long MAX_AUDIO_BYTES = 512 * 1024;

    private final CurrentChild current;
    private final GuardianConsentRepository consents;
    private final QuotaService quota;
    private final SpeechRecognizer recognizer;
    private final GuideService guide;

    public AgentController(CurrentChild current, GuardianConsentRepository consents, QuotaService quota,
                           SpeechRecognizer recognizer, GuideService guide) {
        this.current = current;
        this.consents = consents;
        this.quota = quota;
        this.recognizer = recognizer;
        this.guide = guide;
    }

    /** 语音识别：需登录 + 已选孩子 + 家庭已同意；校验通过才消耗回合，额度被拒时不调 ASR */
    @PostMapping("/asr")
    public Result<AgentAsrResponseDTO> asr(@RequestPart("audio") MultipartFile audio) throws IOException {
        current.requireChildId();
        Long familyId = current.requireFamilyId();
        if (!consents.existsByFamilyIdAndConsentVersion(familyId, AuthService.CONSENT_VERSION)) {
            throw new AuthException(ResultCode.AGENT_CONSENT_REQUIRED);
        }
        if (audio.isEmpty() || audio.getSize() > MAX_AUDIO_BYTES) {
            throw new AsrException(AsrException.Reason.INVALID_AUDIO, "音频为空或超过 512KB：" + audio.getSize());
        }
        byte[] wav = audio.getBytes();
        // 本地校验格式与时长，坏文件（误触、录音失败）不消耗回合
        recognizer.validate(wav);
        QuotaService.Decision decision = quota.tryConsumeFamily(familyId);
        if (decision != QuotaService.Decision.ALLOWED) {
            throw new AuthException(switch (decision) {
                case MINUTE_LIMIT -> ResultCode.AGENT_MINUTE_LIMIT;
                case DAILY_LIMIT -> ResultCode.AGENT_DAILY_LIMIT;
                default -> ResultCode.AGENT_BUDGET_EXHAUSTED;
            });
        }
        AsrResult r = recognizer.recognize(wav);
        log.info("agent asr ok: family={} provider={} latencyMs={}", familyId, r.provider(), r.latencyMs());
        return Result.success(new AgentAsrResponseDTO(r.text(), r.provider(), r.latencyMs()));
    }

    /** 课程向导：游客可用，不计额度 */
    @PostMapping("/guide")
    public Result<AgentGuideResponseDTO> guide(@Valid @RequestBody AgentGuideRequestDTO request) {
        return Result.success(guide.guide(request.text(), request.lessonId()));
    }

    /** 热门主题入口：游客可用 */
    @GetMapping("/topics")
    public Result<List<AgentTopicDTO>> topics() {
        return Result.success(guide.topics());
    }

    @ExceptionHandler(AsrException.class)
    public ResponseEntity<Result<Void>> handleAsr(AsrException e) {
        // message 由 asr 保证不含音频和识别正文
        log.warn("agent asr failed: reason={} {}", e.getReason(), e.getMessage());
        return e.getReason() == AsrException.Reason.INVALID_AUDIO
                ? ResponseEntity.badRequest().body(Result.error(ResultCode.AGENT_AUDIO_INVALID))
                : ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Result.error(ResultCode.AGENT_ASR_UNAVAILABLE));
    }

    /** 非 multipart、缺 audio 字段：统一 400/4001 */
    @ExceptionHandler({MultipartException.class, MissingServletRequestPartException.class})
    public ResponseEntity<Result<Void>> handleBadUpload(Exception e) {
        return ResponseEntity.badRequest().body(Result.error(ResultCode.AGENT_AUDIO_INVALID));
    }
}
