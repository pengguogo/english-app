package com.englishapp.service.ai.guide;

import static org.assertj.core.api.Assertions.assertThat;

import com.englishapp.dto.AgentGuideResponseDTO;
import com.englishapp.dto.AgentGuideResponseDTO.Type;
import com.englishapp.dto.AgentTopicDTO;
import com.englishapp.security.IntegrationTestBase;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

/** 基于真实迁移数据的向导行为：主题入口、route/image 合法性、判定类型 */
class GuideServiceIT extends IntegrationTestBase {

    @Autowired
    private GuideService guide;
    @Autowired
    private CourseIndex index;

    @Test
    void should_推荐该主题的课_当_用任一主题名检索() {
        for (CourseIndex.ThemeEntry t : index.allThemes()) {
            AgentGuideResponseDTO r = guide.guide(t.name(), null);
            assertThat(r.type()).as(t.name()).isEqualTo(Type.RECOMMEND);
            assertThat(r.candidates()).as(t.name()).isNotEmpty()
                    .allSatisfy(c -> assertThat(c.themeName()).isEqualTo(t.name()));
        }
    }

    @Test
    void should_route和image都合法_当_返回候选() {
        for (String text : List.of("恐龙", "火车", "小海龟回家", "汪汪队", "水果", "太空", "拼音")) {
            for (AgentGuideResponseDTO.Candidate c : guide.guide(text, null).candidates()) {
                assertThat(c.route()).matches("/lesson/\\d+\\?themeId=\\d+&unitId=\\d+&subjectId=\\d+"
                        + "|/picturebooks/\\d+\\?lesson=\\d+");
                assertThat(c.route()).contains(String.valueOf(c.lessonId()));
                if (c.image() != null) {
                    assertThat(new ClassPathResource("static" + c.image()).exists()).as(c.image()).isTrue();
                }
            }
        }
    }

    @Test
    void should_跳到绘本页_当_命中绘本课() {
        AgentGuideResponseDTO r = guide.guide("小海龟回家", null);
        assertThat(r.type()).isEqualTo(Type.RECOMMEND);
        assertThat(r.candidates().get(0).route()).startsWith("/picturebooks/162?lesson=");
    }

    @Test
    void should_返回NO_MATCH和4个主题_当_说的话与课程无关() {
        AgentGuideResponseDTO r = guide.guide("你好呀", null);
        assertThat(r.type()).isEqualTo(Type.NO_MATCH);
        assertThat(r.candidates()).isEmpty();
        assertThat(r.topics()).hasSize(4).extracting(AgentTopicDTO::name).contains("恐龙世界");
        assertThat(r.topics()).allSatisfy(t -> assertThat(t.route()).matches("/theme/\\d+\\?subjectId=\\d+"));
        assertThat(r.speech().length()).isLessThanOrEqualTo(40);
    }

    @Test
    void should_追问_当_前几名分散在不同主题() {
        AgentGuideResponseDTO r = guide.guide("狗狗", null);
        assertThat(r.type()).isEqualTo(Type.CLARIFY);
        assertThat(r.candidates()).hasSizeBetween(2, 3);
        assertThat(r.candidates().stream().map(AgentGuideResponseDTO.Candidate::themeName).distinct().count())
                .isEqualTo(r.candidates().size());
        assertThat(r.speech()).endsWith("？").hasSizeLessThanOrEqualTo(40);
    }

    @Test
    void should_排除当前课_当_传入lessonId() {
        AgentGuideResponseDTO r = guide.guide("霸王龙", 270);
        assertThat(r.candidates()).noneMatch(c -> c.lessonId() == 270);
    }

    @Test
    void should_返回热门主题_当_查询topics() {
        List<AgentTopicDTO> topics = guide.topics();
        assertThat(topics).hasSize(6);
        assertThat(topics.get(0).name()).isEqualTo("恐龙世界");
    }
}
