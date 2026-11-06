package com.englishapp.service.ai.explore;

import com.englishapp.dto.AgentAnswerResponseDTO;
import org.springframework.stereotype.Service;

/** 判分：只认签名令牌里的正确答案，不查库、不保存（阶段 3 再落库） */
@Service
public class QuizService {

    private final AgentTokens tokens;

    public QuizService(AgentTokens tokens) {
        this.tokens = tokens;
    }

    public AgentAnswerResponseDTO answer(String quizToken, String optionId, String subject) {
        AgentTokens.QuizKey key = tokens.verifyQuiz(quizToken, subject);
        boolean correct = key.answerId().equals(optionId);
        return correct
                ? new AgentAnswerResponseDTO(true, key.praise(), null)
                : new AgentAnswerResponseDTO(false, "没关系，再试一次", key.hint());
    }
}
