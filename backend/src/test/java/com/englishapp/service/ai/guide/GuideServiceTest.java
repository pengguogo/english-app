package com.englishapp.service.ai.guide;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.englishapp.dto.AgentGuideResponseDTO;
import com.englishapp.dto.AgentGuideResponseDTO.Type;
import java.util.List;
import org.junit.jupiter.api.Test;

/** 判定与 speech 模板（索引用替身） */
class GuideServiceTest {

    private final CourseIndex index = mock(CourseIndex.class);
    private final SynonymDictionary synonyms = new SynonymDictionary();
    private final GuideService service = new GuideService(index, synonyms);

    private static CourseDoc doc(int lessonId, int themeId, String theme, String title) {
        return new CourseDoc(lessonId, title, 1, themeId, theme, false, "/lesson/" + lessonId + "?themeId=" + themeId,
                null, List.of(lessonId), title, theme, title, "", "");
    }

    @Test
    void should_NO_MATCH_当_最高分低于阈值() {
        when(index.search(anyString())).thenReturn(List.of(new CourseIndex.Hit(doc(1, 1, "水果乐园", "认识西瓜"), 2.0)));
        when(index.allThemes()).thenReturn(List.of(new CourseIndex.ThemeEntry(1, "水果乐园", null, "/theme/1?subjectId=1", List.of(1))));
        AgentGuideResponseDTO r = service.guide("嗯", null);
        assertThat(r.type()).isEqualTo(Type.NO_MATCH);
        assertThat(r.candidates()).isEmpty();
        assertThat(r.topics()).extracting("name").containsExactly("水果乐园");
    }

    @Test
    void should_RECOMMEND同主题最多3个_当_第一主题明显领先() {
        when(index.search(anyString())).thenReturn(List.of(
                new CourseIndex.Hit(doc(1, 30, "恐龙世界", "霸王龙"), 20),
                new CourseIndex.Hit(doc(2, 30, "恐龙世界", "迅猛龙"), 15),
                new CourseIndex.Hit(doc(3, 30, "恐龙世界", "棘龙"), 14),
                new CourseIndex.Hit(doc(4, 30, "恐龙世界", "异特龙"), 13),
                new CourseIndex.Hit(doc(5, 3, "汪汪队立大功", "团队故事"), 8)));
        AgentGuideResponseDTO r = service.guide("霸王龙", null);
        assertThat(r.type()).isEqualTo(Type.RECOMMEND);
        assertThat(r.candidates()).extracting("lessonId").containsExactly(1, 2, 3);
        assertThat(r.speech()).isEqualTo("找到啦！恐龙世界里有《霸王龙》，点一下就出发");
    }

    @Test
    void should_CLARIFY跨主题_当_前几名分数接近() {
        when(index.search(anyString())).thenReturn(List.of(
                new CourseIndex.Hit(doc(1, 3, "汪汪队立大功", "认识狗狗"), 10),
                new CourseIndex.Hit(doc(2, 3, "汪汪队立大功", "狗狗的本领"), 10),
                new CourseIndex.Hit(doc(3, 40, "宠物百科", "小狗"), 9.5)));
        AgentGuideResponseDTO r = service.guide("狗狗", null);
        assertThat(r.type()).isEqualTo(Type.CLARIFY);
        assertThat(r.candidates()).extracting("lessonId").containsExactly(1, 3);
        assertThat(r.speech()).isEqualTo("你想看汪汪队立大功，还是宠物百科？");
    }

    @Test
    void should_speech不超过40字_当_名称很长() {
        CourseDoc longDoc = doc(1, 10, "汉字识读", "拼音与生活三 · 1（白菜西瓜）这是一个特别特别长的课程名字");
        assertThat(GuideService.recommendSpeech(List.of(longDoc)).length()).isLessThanOrEqualTo(40);
        CourseDoc a = doc(1, 1, "一个特别特别长的主题名字一", "x");
        CourseDoc b = doc(2, 2, "一个特别特别长的主题名字二", "x");
        CourseDoc c = doc(3, 3, "一个特别特别长的主题名字三", "x");
        assertThat(GuideService.clarifySpeech(List.of(a, b, c)).length()).isLessThanOrEqualTo(40);
    }
}
