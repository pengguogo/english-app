package com.englishapp.service;

import com.englishapp.domain.Lesson;
import com.englishapp.domain.LessonStudySession;
import com.englishapp.dto.ReviewQuestionDto;
import com.englishapp.dto.ReviewOptionDto;
import com.englishapp.dto.ReviewResultDto;
import com.englishapp.repository.LessonRepository;
import com.englishapp.repository.LessonStudySessionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LessonReviewService {
    private final LessonRepository lessons;
    private final LessonStudySessionRepository sessions;
    private final GameAccessService gameAccess;
    private final ObjectMapper mapper;

    public LessonReviewService(LessonRepository lessons, LessonStudySessionRepository sessions,
                               GameAccessService gameAccess, ObjectMapper mapper) {
        this.lessons = lessons;
        this.sessions = sessions;
        this.gameAccess = gameAccess;
        this.mapper = mapper;
    }

    public List<ReviewQuestionDto> questions(Integer lessonId) {
        Lesson lesson = lessons.findById(lessonId).orElseThrow(() -> new IllegalArgumentException("课时不存在"));
        return build(lesson).stream().map(q -> q.question).toList();
    }

    @Transactional
    public ReviewResultDto submit(Integer userId, Integer lessonId, List<Integer> answers) {
        Lesson lesson = lessons.findById(lessonId).orElseThrow(() -> new IllegalArgumentException("课时不存在"));
        List<Question> questions = build(lesson);
        if (answers == null || answers.size() != questions.size()) {
            throw new IllegalArgumentException("请完成全部复习题");
        }
        List<Integer> wrong = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            if (answers.get(i) == null || answers.get(i) != questions.get(i).answer) wrong.add(i);
        }
        if (!wrong.isEmpty()) return new ReviewResultDto(false, wrong, 0);
        int uid = userId == null ? 1 : userId;
        LessonStudySession session = sessions.findByUserIdAndLessonIdAndStudyDate(uid, lessonId, LocalDate.now())
                .orElseGet(() -> newSession(uid, lessonId));
        int credited = session.getPendingSeconds();
        session.setPendingSeconds(0);
        session.setPassed(true);
        sessions.save(session);
        if (credited > 0) gameAccess.recordStudyTime(uid, credited);
        return new ReviewResultDto(true, List.of(), credited);
    }

    public boolean hasPassed(Integer userId, Integer lessonId) {
        int uid = userId == null ? 1 : userId;
        return sessions.findByUserIdAndLessonIdAndStudyDate(uid, lessonId, LocalDate.now())
                .map(LessonStudySession::getPassed).orElse(false);
    }

    private List<Question> build(Lesson lesson) {
        try {
            JsonNode content = mapper.readTree(lesson.getContent());
            JsonNode items = content.path("items");
            if (!items.isArray()) items = content.path("words");
            if (!items.isArray()) items = content.path("sentences");
            if (!items.isArray() || items.isEmpty()) throw new IllegalArgumentException("课时没有可复习内容");
            List<String> labels = new ArrayList<>();
            for (JsonNode item : items) labels.add(label(item));
            List<Question> result = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                JsonNode item = items.get(i);
                if ("QUIZ".equals(lesson.getType().name()) && item.path("options").isArray()) {
                    List<ReviewOptionDto> options = new ArrayList<>();
                    for (JsonNode option : item.path("options")) {
                        options.add(option.isTextual()
                                ? new ReviewOptionDto(option.asText(), "", true)
                                : new ReviewOptionDto(option.path("text").asText(""),
                                        option.path("image").asText(""), option.path("showText").asBoolean(true)));
                    }
                    int answer = item.path("answer").asInt(-1);
                    if (answer < 0 || answer >= options.size()) throw new IllegalArgumentException("复习题答案无效");
                    String prompt = item.path("audioText").isMissingNode()
                            ? item.path("question").asText()
                            : item.path("question").asText() + "（" + item.path("audioText").asText() + "）";
                    result.add(new Question(new ReviewQuestionDto(i, prompt, image(item), options), answer));
                } else if ("CALCULATE".equals(lesson.getType().name())) {
                    String correct = item.path("answer").asText();
                    double number = item.path("answer").asDouble(Double.NaN);
                    if (correct.isBlank() || !Double.isFinite(number)) throw new IllegalArgumentException("计算题答案无效");
                    int answer = i % 3;
                    List<ReviewOptionDto> options = new ArrayList<>(List.of(
                            new ReviewOptionDto(String.valueOf((int) number + 1), "", true),
                            new ReviewOptionDto(String.valueOf((int) number + 2), "", true)));
                    options.add(answer, new ReviewOptionDto(correct, "", true));
                    result.add(new Question(new ReviewQuestionDto(i, item.path("question").asText(), image(item), options), answer));
                } else {
                    List<String> options = new ArrayList<>();
                    for (int j = 0; j < labels.size() && options.size() < 3; j++) {
                        String value = labels.get((i + j) % labels.size());
                        if (!options.contains(value)) options.add(value);
                    }
                    if (options.size() < 2) options.add("跳过本题");
                    int answer = i % options.size();
                    String correct = options.remove(0);
                    options.add(answer, correct);
                    List<ReviewOptionDto> choices = options.stream()
                            .map(value -> new ReviewOptionDto(value, "", true)).toList();
                    String translation = item.path("translation").asText("");
                    String prompt = translation.isBlank()
                            ? "看图或回忆：第 " + (i + 1) + " 项学习了什么？"
                            : "“" + translation + "”对应哪一项？";
                    result.add(new Question(new ReviewQuestionDto(i, prompt,
                            image(item), choices), answer));
                }
            }
            return result;
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalArgumentException("课时内容无法生成复习题", e);
        }
    }

    private String label(JsonNode item) {
        if (item.isTextual()) return item.asText();
        for (String field : List.of("word", "sentence", "title", "text", "content", "letter", "question")) {
            String value = item.path(field).asText("");
            if (!value.isBlank()) return value.length() > 70 ? value.substring(0, 70) + "…" : value;
        }
        return "本课学习项";
    }

    private String image(JsonNode item) { return item.path("image").asText(""); }

    private LessonStudySession newSession(int userId, int lessonId) {
        LessonStudySession session = new LessonStudySession();
        session.setUserId(userId);
        session.setLessonId(lessonId);
        session.setStudyDate(LocalDate.now());
        session.setPendingSeconds(0);
        session.setPassed(false);
        return session;
    }

    private record Question(ReviewQuestionDto question, int answer) {}
}
