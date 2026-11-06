package com.englishapp.service.ai.explore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class LearningItemIndexTest {

    @Test
    void should_去掉疑问虚词再切2gram() {
        List<String> g = LearningItemIndex.grams(LearningItemIndex.normalize("霸王龙为什么那么大？"));
        assertTrue(g.contains("霸王"));
        assertTrue(g.contains("王龙"));
        assertTrue(g.stream().noneMatch(s -> s.contains("什")));
    }

    @Test
    void should_空查询无2gram() {
        assertEquals(List.of(), LearningItemIndex.grams(LearningItemIndex.normalize("吗？")));
    }
}
