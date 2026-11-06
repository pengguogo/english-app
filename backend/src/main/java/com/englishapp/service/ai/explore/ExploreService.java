package com.englishapp.service.ai.explore;

import com.englishapp.dto.AgentChatResponseDTO;
import com.englishapp.dto.AgentChatResponseDTO.Explain;
import com.englishapp.dto.AgentChatResponseDTO.LearnMore;
import com.englishapp.dto.AgentChatResponseDTO.Quiz;
import com.englishapp.dto.AgentChatResponseDTO.Source;
import com.englishapp.dto.AgentChatResponseDTO.Type;
import com.englishapp.dto.AgentGuideResponseDTO;
import com.englishapp.dto.AgentTopicDTO;
import com.englishapp.common.ResultCode;
import com.englishapp.security.AuthException;
import com.englishapp.security.QuotaService;
import com.englishapp.security.TrialBudgetLedger;
import com.englishapp.service.ai.explore.ExplorePrompts.ParsedExplain;
import com.englishapp.service.ai.explore.ExplorePrompts.ParsedQuiz;
import com.englishapp.service.ai.guide.GuideService;
import com.englishapp.service.ai.llm.LlmClient;
import com.englishapp.service.ai.llm.LlmException;
import com.englishapp.service.ai.llm.LlmProperties;
import com.englishapp.service.ai.safety.ContentModerator;
import com.englishapp.service.ai.safety.ModerationResult;
import com.englishapp.service.ai.safety.SafetyCategory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 探索伙伴编排（设计 3.3 / 3.4 / 5.1）：
 * 输入审核 → 课程中答疑 / 课程推荐 → 学习项原文讲解（模型只出题）→ 模型生成讲解 → 校验 → 输出审核。
 * 任何一步失败都不展示未审核的生成内容。日志只记分类、type、耗时、token，不记孩子原话与模型原文。
 */
@Service
public class ExploreService {

    private static final Logger log = LoggerFactory.getLogger(ExploreService.class);
    static final int SAFE_TOPICS = 3;
    static final String FALLBACK_SPEECH = "让我想一想……我们先去看看课程吧";
    static final String UNKNOWN_SPEECH = "这个我还不知道，问问爸爸妈妈吧";
    /** 课程中答疑：孩子问的是“这个 / 它”等指代当前内容 */
    private static final String[] DEICTIC = {"这个", "这是", "它", "这里", "刚才", "为什么", "什么意思", "是什么"};

    /** 谁在问：决定能否调模型、答题令牌绑定谁、是否计预算 */
    public record Asker(String subject, boolean modelAllowed, boolean countBudget) { }

    /** 编排结果；usedModel=true 时调用方已扣额度 */
    public record Outcome(AgentChatResponseDTO response, boolean usedModel) { }

    /** 模型调用前的额度检查（由 Controller 实现：语音回合令牌有效时不再扣）；每轮最多调用一次 */
    @FunctionalInterface
    public interface TurnGate {
        QuotaService.Decision acquire();
    }

    private final ContentModerator moderator;
    private final GuideService guide;
    private final LearningItemIndex items;
    private final LlmClient llm;
    private final LlmProperties llmProps;
    private final AgentTokens tokens;
    private final TrialBudgetLedger budget;
    private final ObjectMapper mapper;

    public ExploreService(ContentModerator moderator, GuideService guide, LearningItemIndex items, LlmClient llm,
                          LlmProperties llmProps, AgentTokens tokens, TrialBudgetLedger budget, ObjectMapper mapper) {
        this.moderator = moderator;
        this.guide = guide;
        this.items = items;
        this.llm = llm;
        this.llmProps = llmProps;
        this.tokens = tokens;
        this.budget = budget;
        this.mapper = mapper;
    }

    public AgentChatResponseDTO chat(String text, Integer lessonId, Integer itemIndex, Asker asker, TurnGate gate) {
        long start = System.nanoTime();
        AgentChatResponseDTO r = route(text.trim(), lessonId, itemIndex, asker, gate);
        log.info("agent chat: type={} source={} model={} ms={}", r.type(),
                r.explain() == null ? "-" : r.explain().source(), asker.modelAllowed(),
                (System.nanoTime() - start) / 1_000_000);
        return r;
    }

    private AgentChatResponseDTO route(String text, Integer lessonId, Integer itemIndex, Asker asker, TurnGate gate) {
        ModerationResult in = moderator.checkInput(text);
        if (!in.allowed()) return safeReply(in.reply(), in.flagParent());
        boolean gentle = in.category() == SafetyCategory.DEATH_NATURE;

        // 1. 课程中答疑：孩子在课程页、问的是当前内容（指代词）→ 用当前学习项原文
        LearningItemIndex.Item current = items.get(lessonId, itemIndex);
        if (current != null && refersToCurrent(text)) {
            return explainItem(current, text, asker, gate, false);
        }
        // 2. 明确想学某课 → 课程推荐 / 追问（保留阶段 1 行为）
        AgentGuideResponseDTO g = guide.guide(text, lessonId);
        if (g.type() != AgentGuideResponseDTO.Type.NO_MATCH && wantsCourse(text)) {
            return fromGuide(g);
        }
        boolean canGenerate = asker.modelAllowed() && llm.enabled();
        // 3. 学习项命中 → 原文讲解 + 模型出题；原文答不上孩子的问题时改为实时生成
        // 不能让模型判断原文是否对题时（游客、未同意、无 Key），知识问题要求所有关键词都落在标题上
        boolean strict = !canGenerate && isQuestion(text);
        LearningItemIndex.Hit hit = strict ? items.best(text, 1.0) : items.best(text);
        if (hit != null) {
            AgentChatResponseDTO r = explainItem(hit.item(), text, asker, gate, canGenerate);
            if (r != null) return r;
            return generate(text, gentle, asker, gate);
        }
        // 4. 允许模型的用户：没表达想学课（知识问题、闲聊、分享）都交给豆豆生成；否则退回课程向导
        if (canGenerate) return generate(text, gentle, asker, gate);
        return fromGuide(g);
    }

    // ---------- 课程原文讲解 ----------

    /**
     * 原文讲解。checkRelevance=true 时让模型同时判断原文能否回答孩子的问题，不能则返回 null（调用方改为生成）。
     */
    private AgentChatResponseDTO explainItem(LearningItemIndex.Item item, String text, Asker asker, TurnGate gate,
                                             boolean checkRelevance) {
        Explain explain = new Explain(item.title(), clip(item.text(), ExplorePrompts.MAX_EXPLAIN), item.image(),
                Source.LESSON, item.lessonId(), item.itemIndex());
        LearnMore more = new LearnMore("去学《" + item.lessonName() + "》", item.route());
        Quiz quiz = null;
        // 额度不足时仍给课程原文讲解，只是不出题
        if (asker.modelAllowed() && llm.enabled() && gate.acquire() == QuotaService.Decision.ALLOWED) {
            JsonNode n = callModel(ExplorePrompts.quizSystem(),
                    ExplorePrompts.quizUser(item.title(), item.text(), text), asker);
            if (checkRelevance && n != null && n.path("relevant").isBoolean() && !n.path("relevant").asBoolean()) {
                return null;
            }
            ParsedQuiz q = n == null ? null : ExplorePrompts.parseQuiz(n);
            // 课程原文不过 checkOutput（人工审过）；模型生成的题目与选项必须过
            if (q != null && passesOutput(q)) quiz = toQuiz(q, asker);
        }
        // 正文在卡片里显示，speech 只说一句引导，避免气泡和卡片重复
        String speech = clip("我们来看看「" + item.title() + "」" + (quiz == null ? "吧" : "，看完答一道小题"),
                ExplorePrompts.MAX_SPEECH);
        return new AgentChatResponseDTO(Type.EXPLAIN, speech, explain, quiz, List.of(), List.of(), more, null);
    }

    // ---------- 实时生成 ----------

    private AgentChatResponseDTO generate(String text, boolean gentle, Asker asker, TurnGate gate) {
        QuotaService.Decision d = gate.acquire();
        // 额度不足：按契约返回 429/4291~4293，前端显示“今天聊够啦”
        if (d != QuotaService.Decision.ALLOWED) throw new AuthException(switch (d) {
            case MINUTE_LIMIT -> ResultCode.AGENT_MINUTE_LIMIT;
            case DAILY_LIMIT -> ResultCode.AGENT_DAILY_LIMIT;
            default -> ResultCode.AGENT_BUDGET_EXHAUSTED;
        });
        JsonNode n = callModel(ExplorePrompts.explainSystem(gentle), ExplorePrompts.explainUser(text), asker);
        if (n == null) return fallback();
        if (n.path("unknown").asBoolean(false)) return unknown();
        ParsedExplain p = ExplorePrompts.parseExplain(n);
        if (p == null) return fallback();
        ParsedQuiz q = p.quiz();
        List<String> all = new ArrayList<>(List.of(p.title(), p.explain()));
        if (q != null) all.addAll(quizTexts(q));
        ModerationResult out = moderator.checkOutput(all.toArray(String[]::new));
        if (!out.allowed()) {
            // 输出被拦：不泄露原文，统一安全话术
            return safeReply(out.reply(), false);
        }
        Explain explain = new Explain(p.title(), p.explain(), null, Source.GENERATED, null, null);
        return new AgentChatResponseDTO(Type.EXPLAIN, clip(p.explain(), ExplorePrompts.MAX_SPEECH), explain,
                q == null ? null : toQuiz(q, asker), List.of(), List.of(), null, null);
    }

    /** 调模型并解析 JSON；任何失败返回 null（降级），并按 token 记预算 */
    private JsonNode callModel(String system, String user, Asker asker) {
        try {
            LlmClient.Completion c = llm.complete(system, user);
            if (asker.countBudget()) {
                budget.record(c.promptTokens() / 1000.0 * llmProps.getInputCentsPerKToken()
                        + c.completionTokens() / 1000.0 * llmProps.getOutputCentsPerKToken());
            }
            log.info("agent llm ok: model={} ms={} in={} out={}", llmProps.getModel(), c.latencyMs(),
                    c.promptTokens(), c.completionTokens());
            JsonNode n = ExplorePrompts.readJson(mapper, c.content());
            if (n == null) log.warn("agent llm 返回不是合法 JSON 对象");
            return n;
        } catch (LlmException e) {
            log.warn("agent llm failed: {}", e.getMessage());
            return null;
        }
    }

    // ---------- 组装 ----------

    private boolean passesOutput(ParsedQuiz q) {
        return moderator.checkOutput(quizTexts(q).toArray(String[]::new)).allowed();
    }

    private static List<String> quizTexts(ParsedQuiz q) {
        List<String> t = new ArrayList<>(List.of(q.question(), q.praise(), q.hint()));
        q.options().forEach(o -> t.add(o.text()));
        return t;
    }

    private Quiz toQuiz(ParsedQuiz q, Asker asker) {
        String token = tokens.quiz(asker.subject(), new AgentTokens.QuizKey(q.answerId(), q.praise(), q.hint()));
        return new Quiz(q.question(), q.options(), token);
    }

    private AgentChatResponseDTO fromGuide(AgentGuideResponseDTO g) {
        Type t = switch (g.type()) {
            case RECOMMEND -> Type.RECOMMEND;
            case CLARIFY -> Type.CLARIFY;
            case NO_MATCH -> Type.NO_MATCH;
        };
        return new AgentChatResponseDTO(t, g.speech(), null, null, g.candidates(), g.topics(), null, null);
    }

    private AgentChatResponseDTO safeReply(String reply, boolean flagParent) {
        List<AgentTopicDTO> topics = guide.topics().stream().limit(SAFE_TOPICS).toList();
        return new AgentChatResponseDTO(Type.SAFE_REPLY, reply, null, null, List.of(), topics, null,
                flagParent ? Boolean.TRUE : null);
    }

    private AgentChatResponseDTO fallback() {
        return new AgentChatResponseDTO(Type.NO_MATCH, FALLBACK_SPEECH, null, null, List.of(),
                guide.topics().stream().limit(4).toList(), null, null);
    }

    private AgentChatResponseDTO unknown() {
        return new AgentChatResponseDTO(Type.NO_MATCH, UNKNOWN_SPEECH, null, null, List.of(),
                guide.topics().stream().limit(4).toList(), null, null);
    }

    /** 是不是在问一个知识问题 */
    static boolean isQuestion(String text) {
        return text.contains("为什么") || text.contains("怎么") || text.contains("什么") || text.contains("吗")
                || text.contains("？") || text.contains("?") || text.contains("多少") || text.contains("哪")
                || text.contains("会不会") || text.contains("能不能");
    }

    /** 课程意图：孩子要“学 / 看 / 听 / 玩”某个课程，而不是问一个知识问题 */
    static boolean wantsCourse(String text) {
        if (text.contains("有没有")) return true;
        boolean wants = text.contains("想") || text.contains("学") || text.contains("看") || text.contains("听")
                || text.contains("玩") || text.contains("讲");
        return wants && !isQuestion(text);
    }

    static boolean refersToCurrent(String text) {
        for (String d : DEICTIC) if (text.contains(d)) return true;
        return false;
    }

    static String clip(String s, int max) {
        if (s == null || s.length() <= max) return s;
        int cut = Math.max(s.lastIndexOf('。', max - 1), s.lastIndexOf('，', max - 1));
        return (cut > max / 2 ? s.substring(0, cut) : s.substring(0, max - 1)) + "…";
    }
}
