package com.englishapp.service.ai.asr;

import com.englishapp.voice.BaiduVoiceService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/** 按 app.agent.asr.provider 只装配一个 SpeechRecognizer。 */
class SpeechRecognizerSelectionTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withBean(AsrProperties.class)
            .withBean(BaiduVoiceService.class, () -> mock(BaiduVoiceService.class))
            .withUserConfiguration(BaiduRecognizer.class, DashScopeRecognizer.class);

    @Test
    void should_装配百度_当_未配置provider() {
        runner.run(ctx -> assertThat(ctx.getBean(SpeechRecognizer.class).provider()).isEqualTo("baidu"));
    }

    @Test
    void should_只装配百炼_当_provider为dashscope() {
        runner.withPropertyValues("app.agent.asr.provider=dashscope").run(ctx -> {
            assertThat(ctx).hasSingleBean(SpeechRecognizer.class);
            assertThat(ctx.getBean(SpeechRecognizer.class).provider()).isEqualTo("dashscope");
        });
    }
}
