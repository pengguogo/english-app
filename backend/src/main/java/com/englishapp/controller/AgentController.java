package com.englishapp.controller;

import com.englishapp.common.Result;
import com.englishapp.common.ResultCode;
import com.englishapp.dto.AgentAnswerRequestDTO;
import com.englishapp.dto.AgentAnswerResponseDTO;
import com.englishapp.dto.AgentAsrResponseDTO;
import com.englishapp.dto.AgentChatRequestDTO;
import com.englishapp.dto.AgentChatResponseDTO;
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
import com.englishapp.service.ai.explore.AgentTokens;
import com.englishapp.service.ai.explore.ExploreService;
import com.englishapp.service.ai.explore.QuizService;
import com.englishapp.service.ai.guide.GuideService;
import com.englishapp.service.ai.record.AgentRecordService;
import java.util.Optional;
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
 * 学习伙伴：语音识别、课程向导（阶段 1），探索讲解与答题（阶段 2）。
 * 不保存音频；已同意家庭的文字对话与答题落库（阶段 3），日志不记音频、识别正文、孩子原话与模型原文。
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
    private final ExploreService explore;
    private final QuizService quiz;
    private final AgentTokens tokens;
    private final AgentRecordService records;

    public AgentController(CurrentChild current, GuardianConsentRepository consents, QuotaService quota,
                           SpeechRecognizer recognizer, GuideService guide, ExploreService explore,
                           QuizService quiz, AgentTokens tokens, AgentRecordService records) {
        this.current = current;
        this.consents = consents;
        this.quota = quota;
        this.recognizer = recognizer;
        this.guide = guide;
        this.explore = explore;
        this.quiz = quiz;
        this.tokens = tokens;
        this.records = records;
    }

    /** 语音识别：需登录 + 已选孩子 + 家庭已同意；校验通过才消耗回合，额度被拒时不调 ASR */
    @PostMapping("/asr")
    public Result<AgentAsrResponseDTO> asr(@RequestPart("audio") MultipartFile audio) throws IOException {
        current.requireChildId();
        Long familyId = current.requireFamilyId();
        if (!consented(familyId)) {
            throw new AuthException(ResultCode.AGENT_CONSENT_REQUIRED);
        }
        if (audio.isEmpty() || audio.getSize() > MAX_AUDIO_BYTES) {
            throw new AsrException(AsrException.Reason.INVALID_AUDIO, "音频为空或超过 512KB：" + audio.getSize());
        }
        byte[] wav = audio.getBytes();
        // 本地校验格式与时长，坏文件（误触、录音失败）不消耗回合
        recognizer.validate(wav);
        QuotaService.Decision decision = quota.tryConsumeFamily(familyId);
        if (decision != QuotaService.Decision.ALLOWED) throw quotaRejected(decision);
        AsrResult r = recognizer.recognize(wav);
        log.info("agent asr ok: family={} provider={} latencyMs={}", familyId, r.provider(), r.latencyMs());
        return Result.success(new AgentAsrResponseDTO(r.text(), r.provider(), r.latencyMs(), tokens.turn(familyId)));
    }

    /**
     * 探索对话：游客与未同意家庭只走检索（课程原文讲解，不调模型）；
     * 已同意家庭可调模型，只有真的调模型的一轮才扣回合，带有效语音回合令牌时不再扣。
     */
    @PostMapping("/chat")
    public Result<AgentChatResponseDTO> chat(@Valid @RequestBody AgentChatRequestDTO request) {
        Optional<Integer> childId = current.currentChildId();
        Long familyId = childId.isPresent() ? current.requireFamilyId() : null;
        boolean modelAllowed = familyId != null && consented(familyId);
        ExploreService.Asker asker = new ExploreService.Asker(subject(childId), modelAllowed,
                modelAllowed && !quota.isUnlimited(familyId));
        boolean[] used = {false};
        ExploreService.TurnGate gate = () -> {
            if (used[0]) return QuotaService.Decision.ALLOWED;
            used[0] = true;
            if (tokens.validTurn(request.turnToken(), familyId)) return QuotaService.Decision.ALLOWED;
            return quota.tryConsumeFamily(familyId);
        };
        AgentChatRequestDTO.Context ctx = request.context();
        Integer lessonId = ctx == null ? null : ctx.lessonId();
        AgentChatResponseDTO r = explore.chat(request.text(), lessonId,
                ctx == null ? null : ctx.itemIndex(), asker, gate);
        // 只有已同意家庭（且已选孩子）才落库；记录失败不影响孩子得到回答
        if (!modelAllowed) return Result.success(r);
        try {
            Long sessionId = records.recordChat(familyId, childId.get(), request.sessionId(), request.text(),
                    lessonId, r);
            return Result.success(r.withSessionId(sessionId));
        } catch (RuntimeException e) {
            log.warn("agent record chat failed: family={} error={}", familyId, e.getClass().getSimpleName());
            return Result.success(r);
        }
    }

    /** 答题判分：令牌绑定答题人，换孩子或过期 → 400/4002 */
    @PostMapping("/answer")
    public Result<AgentAnswerResponseDTO> answer(@Valid @RequestBody AgentAnswerRequestDTO request) {
        Optional<Integer> childId = current.currentChildId();
        AgentAnswerResponseDTO r = quiz.answer(request.quizToken(), request.optionId(), subject(childId));
        if (childId.isEmpty() || request.sessionId() == null) return Result.success(r);
        Long familyId = current.requireFamilyId();
        if (!consented(familyId)) return Result.success(r);
        try {
            records.recordAnswer(childId.get(), request.sessionId(), request.quizToken(), request.optionId(),
                    r.correct());
        } catch (RuntimeException e) {
            log.warn("agent record answer failed: family={} error={}", familyId, e.getClass().getSimpleName());
        }
        return Result.success(r);
    }

    private boolean consented(Long familyId) {
        return consents.existsByFamilyIdAndConsentVersion(familyId, AuthService.CONSENT_VERSION);
    }

    private static String subject(Optional<Integer> childId) {
        return childId.map(id -> "child:" + id).orElse("guest");
    }

    private static AuthException quotaRejected(QuotaService.Decision d) {
        return new AuthException(switch (d) {
            case MINUTE_LIMIT -> ResultCode.AGENT_MINUTE_LIMIT;
            case DAILY_LIMIT -> ResultCode.AGENT_DAILY_LIMIT;
            default -> ResultCode.AGENT_BUDGET_EXHAUSTED;
        });
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
