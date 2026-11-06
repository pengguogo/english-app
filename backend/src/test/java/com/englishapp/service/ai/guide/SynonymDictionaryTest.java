package com.englishapp.service.ai.guide;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SynonymDictionaryTest {

    private final SynonymDictionary dict = new SynonymDictionary();

    @Test
    void should_扩展到整组_当_说出组内任一词() {
        List<Set<String>> groups = dict.groupsIn(QueryTerms.normalize("霸王龙好厉害"));
        assertThat(groups).anySatisfy(g -> assertThat(g).contains("恐龙", "dinosaur"));
    }

    @Test
    void should_不区分大小写_当_说英文() {
        assertThat(dict.groupsIn(QueryTerms.normalize("Dinosaur"))).isNotEmpty();
    }

    @Test
    void should_无命中_当_与课程无关() {
        assertThat(dict.groupsIn(QueryTerms.normalize("你好呀"))).isEmpty();
    }

    @Test
    void should_加载热门主题_当_启动() {
        assertThat(dict.hotTopics()).startsWith("恐龙世界");
    }
}
