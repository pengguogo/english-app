package com.englishapp.service.ai.explore;

import com.englishapp.domain.UserProgress;
import com.englishapp.domain.enums.ProgressStatus;
import com.englishapp.dto.AgentChatResponseDTO;
import com.englishapp.dto.AgentChatResponseDTO.Explain;
import com.englishapp.dto.AgentChatResponseDTO.Option;
import com.englishapp.dto.AgentChatResponseDTO.Quiz;
import com.englishapp.repository.UserProgressRepository;
import com.englishapp.service.ai.record.AgentRecordService;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GuidedLearningService {
    private final JdbcTemplate jdbc;
    private final UserProgressRepository progress;
    private final AgentRecordService records;

    public GuidedLearningService(JdbcTemplate jdbc, UserProgressRepository progress, AgentRecordService records) {
        this.jdbc = jdbc;
        this.progress = progress;
        this.records = records;
    }

    public enum Action { START, NEXT, ANSWER, RETURN }

    public record Turn(Action action, UUID revision, String option) { }

    public record Run(Integer childId, Long recordingFamily, TrainGuide step, UUID revision,
                      Long recordId, Book book, String feedback) implements Serializable { }

    public record Book(int unitId, int lessonId, LocalDateTime previousCompletion, boolean guestReviewed) implements Serializable { }

    public record Screen(TrainGuide step, UUID revision, String title, String speech,
                         String image, List<Option> options, String feedback, BookLink book) { }

    public record BookLink(int unitId, int lessonId) { }

    /** 旧页面的重复提交只返回当前状态，不再次答题或完成活动。 */
    @Transactional
    public Run advance(Run run, Turn turn, Integer childId, Long recordingFamily) {
        if (turn.action() == Action.START && (run == null || run.step == TrainGuide.DONE && matches(run, turn))) {
            return move(null, TrainGuide.INTRO, childId, recordingFamily, null, null);
        }
        if (run == null || !matches(run, turn)) return run;
        return switch (turn.action()) {
            case START -> run;
            case NEXT -> run.step == TrainGuide.INTRO ? enterQuestion(run, TrainGuide.QUESTION) : run;
            case ANSWER -> answer(run, turn.option());
            case RETURN -> run.step == TrainGuide.BOOK && bookCompleted(run) ? enterQuestion(run, TrainGuide.RECAP) : run;
        };
    }

    private static boolean matches(Run run, Turn turn) {
        return run.revision.equals(turn.revision());
    }

    private Run answer(Run run, String option) {
        TrainGuide.Question question = run.step.question;
        if (question == null || question.options().stream().noneMatch(o -> o.id().equals(option))) return run;
        boolean correct = question.correct().equals(option);
        if (run.recordId != null) {
            records.recordAnswer(run.childId, run.recordId, run.revision.toString(), option, correct);
        }
        return switch (run.step) {
            case QUESTION -> correct ? toBook(run) : enterQuestion(run, TrainGuide.HINT);
            case HINT -> correct ? toBook(run) : retry(run, "再看看图，从最前面开始找。");
            case RECAP -> correct ? move(run, TrainGuide.DONE, run.childId, run.recordingFamily, run.book, null)
                    : retry(run, "想一想，哪样东西能让苹果一路凉凉的？");
            default -> run;
        };
    }

    private Run enterQuestion(Run run, TrainGuide step) {
        Run next = move(run, step, run.childId, run.recordingFamily, run.book, null);
        if (run.recordingFamily == null) return next;
        // 使用现有课程学习项的主题与复习建议，固定引导内容不调用模型。
        int itemIndex = step == TrainGuide.RECAP ? 2 : 0;
        var response = new AgentChatResponseDTO(AgentChatResponseDTO.Type.EXPLAIN, step.speech,
                new Explain(step.title, step.speech, step.image, AgentChatResponseDTO.Source.LESSON, 44, itemIndex),
                new Quiz(step.title, step.question.options(), next.revision.toString()), List.of(), List.of(), null, false);
        Long id = records.recordChat(run.recordingFamily, run.childId, run.recordId,
                step == TrainGuide.RECAP ? "回到豆豆，复习火车" : "参加火车探险", 44, response);
        return new Run(next.childId, next.recordingFamily, next.step, next.revision, id, next.book, next.feedback);
    }

    private Run toBook(Run run) {
        Book book = jdbc.queryForObject("""
                SELECT u.id AS unit_id, l.id AS lesson_id FROM lesson l JOIN unit u ON u.id = l.unit_id
                JOIN theme t ON t.id = u.theme_id
                WHERE t.name = '绘本小火车' AND u.name = '苹果坐上小火车'
                  AND l.sort_order = 1 AND l.type = 'READING'
                """, (rs, row) -> new Book(rs.getInt("unit_id"), rs.getInt("lesson_id"), null, false));
        var previous = completion(run.childId, book.lessonId);
        return move(run, TrainGuide.BOOK, run.childId, run.recordingFamily,
                new Book(book.unitId, book.lessonId, previous, false), null);
    }

    public static Run confirmGuestReview(Run run, int lessonId) {
        if (run == null || run.childId != null || run.step != TrainGuide.BOOK || run.book.lessonId != lessonId) return run;
        return new Run(run.childId, run.recordingFamily, run.step, run.revision, run.recordId,
                new Book(run.book.unitId, lessonId, null, true), run.feedback);
    }

    private boolean bookCompleted(Run run) {
        if (run.childId == null) return run.book.guestReviewed;
        LocalDateTime completed = completion(run.childId, run.book.lessonId);
        return completed != null && !Objects.equals(completed, run.book.previousCompletion);
    }

    private LocalDateTime completion(Integer childId, int lessonId) {
        if (childId == null) return null;
        return progress.findByUserIdAndLessonId(childId, lessonId)
                .filter(p -> p.getStatus() == ProgressStatus.COMPLETED)
                .map(UserProgress::getCompletedAt).orElse(null);
    }

    private static Run move(Run run, TrainGuide step, Integer childId, Long recordingFamily, Book book, String feedback) {
        return new Run(childId, recordingFamily, step, UUID.randomUUID(), run == null ? null : run.recordId, book, feedback);
    }

    private static Run retry(Run run, String feedback) {
        // 同一道题保留记录键，修正答案也只记录第一次作答。
        return new Run(run.childId, run.recordingFamily, run.step, run.revision, run.recordId, run.book, feedback);
    }

    public Screen screen(Run run) {
        if (run == null) return null;
        TrainGuide step = run.step;
        var book = step == TrainGuide.BOOK ? new BookLink(run.book.unitId, run.book.lessonId) : null;
        return new Screen(step, run.revision, step.title, step.speech, step.image,
                step.question == null ? List.of() : step.question.options(), run.feedback, book);
    }
}
