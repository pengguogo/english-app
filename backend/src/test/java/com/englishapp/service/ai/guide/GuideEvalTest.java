package com.englishapp.service.ai.guide;

import static org.assertj.core.api.Assertions.assertThat;

import com.englishapp.dto.AgentGuideResponseDTO;
import com.englishapp.dto.AgentGuideResponseDTO.Candidate;
import com.englishapp.security.IntegrationTestBase;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

/**
 * 检索评测：真实跑全部 Flyway 迁移得到线上同款课程，按 agent/guide-eval.tsv 计算 Hit@3。
 * 命中：top3 候选里有期望 lessonId；期望列为 * 时，top3 里有期望主题的课即算命中。
 */
class GuideEvalTest extends IntegrationTestBase {

    private record Case(String text, Set<Integer> lessonIds, int themeId) { }

    @Autowired
    private GuideService guide;

    @Test
    void should_Hit3不低于0点8_当_跑检索评测集() throws IOException {
        List<Case> cases = loadCases();
        assertThat(cases).hasSizeGreaterThanOrEqualTo(40);

        int hit = 0;
        List<Long> nanos = new ArrayList<>();
        List<String> misses = new ArrayList<>();
        for (Case c : cases) {
            long start = System.nanoTime();
            AgentGuideResponseDTO r = guide.guide(c.text(), null);
            nanos.add(System.nanoTime() - start);
            List<Candidate> top3 = r.candidates().stream().limit(3).toList();
            boolean ok = top3.stream().anyMatch(cand -> c.lessonIds().isEmpty()
                    ? themeIdOf(cand) == c.themeId()
                    : c.lessonIds().contains(cand.lessonId()));
            if (ok) hit++;
            else misses.add(c.text() + " → " + r.type() + " " + top3.stream()
                    .map(x -> x.lessonId() + ":" + x.themeName() + "/" + x.title()).toList());
        }
        double hitAt3 = (double) hit / cases.size();
        String summary = String.format("[guide-eval] Hit@3 = %d/%d = %.3f, P90 = %.2fms",
                hit, cases.size(), hitAt3, p90Millis(nanos));
        System.out.println(summary);
        misses.forEach(m -> System.out.println("[guide-eval] MISS " + m));
        // 控制台编码不一定是 UTF-8，完整报告另写一份文件
        List<String> report = new ArrayList<>(List.of(summary));
        misses.forEach(m -> report.add("MISS " + m));
        Files.write(Path.of("target", "guide-eval-report.txt"), report, StandardCharsets.UTF_8);

        assertThat(hitAt3).isGreaterThanOrEqualTo(0.8);
    }

    private static int themeIdOf(Candidate c) {
        // route 里带 themeId；绘本课 route 不带主题，绘本只属于“绘本小火车”
        String route = c.route();
        int i = route.indexOf("themeId=");
        if (i < 0) return 52;
        String rest = route.substring(i + 8);
        int amp = rest.indexOf('&');
        return Integer.parseInt(amp < 0 ? rest : rest.substring(0, amp));
    }

    private static double p90Millis(List<Long> nanos) {
        List<Long> sorted = nanos.stream().sorted().toList();
        return sorted.get((int) Math.ceil(sorted.size() * 0.9) - 1) / 1_000_000.0;
    }

    private static List<Case> loadCases() throws IOException {
        String tsv = new ClassPathResource("agent/guide-eval.tsv").getContentAsString(StandardCharsets.UTF_8);
        return tsv.lines()
                .filter(l -> !l.isBlank() && !l.startsWith("#"))
                .map(l -> l.split("\t"))
                .map(p -> new Case(p[0], "*".equals(p[1]) ? Set.of()
                        : Arrays.stream(p[1].split("\\|")).map(Integer::valueOf).collect(Collectors.toSet()),
                        Integer.parseInt(p[2])))
                .toList();
    }
}
