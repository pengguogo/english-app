package com.englishapp.voice;

import com.englishapp.config.TtsProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class TtsCacheTest {

    @TempDir
    Path tempDir;

    @Test
    void should_读取已写入音频_当_缓存键一致() {
        TtsProperties properties = new TtsProperties();
        properties.setCacheDir(tempDir.toString());
        TtsCache cache = new TtsCache(properties);
        byte[] expected = new byte[]{1, 2, 3};

        cache.write("baidu", "你好", "zh", VoiceProfile.MIMI_WARM, expected);

        assertThat(cache.read("baidu", "你好", "zh", VoiceProfile.MIMI_WARM))
                .isEqualTo(expected);
        assertThat(cache.read("other-provider", "你好", "zh", VoiceProfile.MIMI_WARM))
                .isNull();
    }
}
