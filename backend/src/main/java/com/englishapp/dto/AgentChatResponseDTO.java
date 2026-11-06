package com.englishapp.dto;

import com.englishapp.dto.AgentGuideResponseDTO.Candidate;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * /agent/chat 响应。正确答案不下发，凭 quiz.quizToken 到 /agent/answer 判分。
 *
 * @param type       RECOMMEND | CLARIFY | EXPLAIN | NO_MATCH | SAFE_REPLY
 * @param speech     给 TTS 的短句（≤60 字）
 * @param flagParent 是否需要标记给家长
 * @param sessionId  成长记录会话 id：只有已同意家庭且已选孩子时返回
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AgentChatResponseDTO(Type type, String speech, Explain explain, Quiz quiz,
                                   List<Candidate> candidates, List<AgentTopicDTO> topics, LearnMore learnMore,
                                   Boolean flagParent, Long sessionId) {

    /** 编排层用：不带会话 id（由 Controller 落库后补上） */
    public AgentChatResponseDTO(Type type, String speech, Explain explain, Quiz quiz, List<Candidate> candidates,
                                List<AgentTopicDTO> topics, LearnMore learnMore, Boolean flagParent) {
        this(type, speech, explain, quiz, candidates, topics, learnMore, flagParent, null);
    }

    public AgentChatResponseDTO withSessionId(Long id) {
        return new AgentChatResponseDTO(type, speech, explain, quiz, candidates, topics, learnMore, flagParent, id);
    }

    public enum Type { RECOMMEND, CLARIFY, EXPLAIN, NO_MATCH, SAFE_REPLY }

    public enum Source { LESSON, GENERATED }

    public record Explain(String title, String text, String image, Source source, Integer lessonId, Integer itemIndex) { }

    public record Option(String id, String text) { }

    public record Quiz(String question, List<Option> options, String quizToken) { }

    public record LearnMore(String title, String route) { }
}
