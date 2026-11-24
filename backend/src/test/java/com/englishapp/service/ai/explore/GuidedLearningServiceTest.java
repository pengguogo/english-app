package com.englishapp.service.ai.explore;

import com.englishapp.domain.UserProgress;
import com.englishapp.domain.enums.ProgressStatus;
import com.englishapp.repository.UserProgressRepository;
import com.englishapp.service.ai.explore.GuidedLearningService.Action;
import com.englishapp.service.ai.explore.GuidedLearningService.Book;
import com.englishapp.service.ai.explore.GuidedLearningService.Run;
import com.englishapp.service.ai.explore.GuidedLearningService.Turn;
import com.englishapp.service.ai.record.AgentRecordService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GuidedLearningServiceTest {
    private final JdbcTemplate jdbc = mock(JdbcTemplate.class);
    private final UserProgressRepository progress = mock(UserProgressRepository.class);
    private final AgentRecordService records = mock(AgentRecordService.class);
    private final GuidedLearningService guide = new GuidedLearningService(jdbc, progress, records);

    @Test
    void should_根据第一次作答分支且忽略旧页面重复提交_当_比较答对和答错() {
        when(jdbc.queryForObject(anyString(), org.mockito.ArgumentMatchers.<RowMapper<Book>>any()))
                .thenReturn(new Book(165, 647, null, false));
        Run intro = guide.advance(null, new Turn(Action.START, null, null), null, null);
        Run question = guide.advance(intro, new Turn(Action.NEXT, intro.revision(), null), null, null);

        Run book = guide.advance(question, new Turn(Action.ANSWER, question.revision(), "b"), null, null);
        Turn wrong = new Turn(Action.ANSWER, question.revision(), "a");
        Run hint = guide.advance(question, wrong, null, null);

        assertThat(book.step()).isEqualTo(TrainGuide.BOOK);
        Turn arrival = new Turn(Action.RETURN, book.revision(), null);
        assertThat(guide.advance(book, arrival, null, null)).isSameAs(book);
        assertThat(GuidedLearningService.confirmGuestReview(book, 648)).isSameAs(book);
        Run reviewed = GuidedLearningService.confirmGuestReview(book, 647);
        assertThat(guide.advance(reviewed, arrival, null, null).step()).isEqualTo(TrainGuide.RECAP);
        assertThat(hint.step()).isEqualTo(TrainGuide.HINT);
        assertThat(guide.advance(hint, wrong, null, null)).isSameAs(hint);
        assertThat(guide.screen(hint).image()).isNotEqualTo(guide.screen(question).image());
    }

    @Test
    void should_验证本次绘本进度并且只记录一次回顾作答_当_当前孩子带着已有历史完成记录回来() {
        var previous = LocalDateTime.of(2026, 11, 24, 10, 0);
        var bookRun = new Run(3, 8L, TrainGuide.BOOK, UUID.randomUUID(), 9L, new Book(165, 647, previous, false), null);
        var answer = new UserProgress();
        answer.setStatus(ProgressStatus.COMPLETED);
        answer.setCompletedAt(previous);
        when(progress.findByUserIdAndLessonId(3, 647)).thenReturn(Optional.of(answer));
        when(records.recordChat(eq(8L), eq(3), eq(9L), anyString(), eq(44), any())).thenReturn(9L);
        var completion = new Turn(Action.RETURN, bookRun.revision(), null);

        assertThat(guide.advance(bookRun, completion, 3, 8L)).isSameAs(bookRun);
        answer.setCompletedAt(previous.plusMinutes(3));
        Run recap = guide.advance(bookRun, completion, 3, 8L);
        var choice = new Turn(Action.ANSWER, recap.revision(), "a");
        Run done = guide.advance(recap, choice, 3, 8L);

        assertThat(recap.step()).isEqualTo(TrainGuide.RECAP);
        assertThat(done.step()).isEqualTo(TrainGuide.DONE);
        assertThat(guide.advance(done, choice, 3, 8L)).isSameAs(done);
        verify(records).recordAnswer(3, 9L, recap.revision().toString(), "a", true);
    }
}
