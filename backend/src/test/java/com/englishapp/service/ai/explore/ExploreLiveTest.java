package com.englishapp.service.ai.explore;

import com.englishapp.dto.AgentChatResponseDTO;
import com.englishapp.service.ai.llm.LlmClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * 真实模型冒烟（默认不跑）：mvn test -Dtest=ExploreLiveTest -Dgroups=live
 * 用本机 backend/.env 的 DASHSCOPE_API_KEY 和本机 english.db（只读查询）；只打印 type/source/耗时/是否出题。
 */
@Tag("live")
@SpringBootTest
class ExploreLiveTest {

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) throws Exception {
        Path copy = Files.createTempFile("explore-live", ".db");
        Files.copy(Path.of("english.db"), copy, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        r.add("spring.datasource.url", () -> "jdbc:sqlite:" + copy);
        r.add("spring.jpa.show-sql", () -> "false");
    }

    @Autowired ExploreService explore;
    @Autowired LlmClient llm;

    @Test
    void live() {
        Assumptions.assumeTrue(llm.enabled(), "未配置 DASHSCOPE_API_KEY");
        List<String> questions = List.of("霸王龙的牙齿", "恐龙为什么不见了", "冰淇淋为什么会化", "为什么会打嗝",
                "月亮为什么会变", "小猫为什么喜欢晒太阳", "天上为什么会有彩虹", "蚂蚁怎么认路", "我想看火车", "黑洞里面有什么",
                "你好呀", "我今天去公园玩了", "给我讲个小故事", "我喜欢小猫");
        ExploreService.Asker asker = new ExploreService.Asker("child:0", true, false);
        StringBuilder report = new StringBuilder();
        for (String q : questions) {
            long t = System.nanoTime();
            AgentChatResponseDTO r = explore.chat(q, null, null, asker, () -> com.englishapp.security.QuotaService.Decision.ALLOWED);
            long ms = (System.nanoTime() - t) / 1_000_000;
            report.append(String.format("%-14s type=%-10s source=%-9s quiz=%-5s %5dms | %s%n", q, r.type(),
                    r.explain() == null ? "-" : r.explain().source(), r.quiz() != null, ms,
                    r.explain() == null ? r.speech() : r.explain().title() + "：" + r.explain().text()));
            if (r.quiz() != null) report.append("    题：").append(r.quiz().question()).append(' ')
                    .append(r.quiz().options().stream().map(o -> o.id() + "." + o.text()).toList()).append('\n');
        }
        AgentChatResponseDTO ctx = explore.chat("这个是什么", 264, 1, asker, () -> com.englishapp.security.QuotaService.Decision.ALLOWED);
        report.append("context 264/1 type=").append(ctx.type()).append(" quiz=").append(ctx.quiz() != null).append('\n');
        try {
            Files.writeString(Path.of("target/explore-live-report.txt"), report);
        } catch (Exception ignored) { }
    }
}
