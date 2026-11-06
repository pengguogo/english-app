package com.englishapp.dto;

import com.englishapp.dto.AgentGuideResponseDTO.Candidate;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * /agent/chat 响应。正确答案不下发，凭 quiz.quizToken 到 /agent/answer 判分。
 *
 * @param type       RECOMMEND | CLARIFY | EXPLAIN | NO_MATCH | SAFE_REPLY
 * @param speech     给 TTS 的短句（≤60 字）
 * @param flagParent 是否需要标记给家长（阶段 2 只回传，不落库）
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AgentChatResponseDTO(Type type, String speech, Explain explain, Quiz quiz,
                                   List<Candidate> candidates, List<AgentTopicDTO> topics, LearnMore learnMore,
                                   Boolean flagParent) {

    public enum Type { RECOMMEND, CLARIFY, EXPLAIN, NO_MATCH, SAFE_REPLY }

    public enum Source { LESSON, GENERATED }

    public record Explain(String title, String text, String image, Source source, Integer lessonId, Integer itemIndex) { }

    public record Option(String id, String text) { }

    public record Quiz(String question, List<Option> options, String quizToken) { }

    public record LearnMore(String title, String route) { }
}
