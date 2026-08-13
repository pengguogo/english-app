package com.englishapp.voice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VoiceProfileTest {

    @Test
    void should_返回对应档案_当_业务名称有效() {
        assertThat(VoiceProfile.fromKey("mimi-encouraging"))
                .isEqualTo(VoiceProfile.MIMI_ENCOURAGING);
    }

    @Test
    void should_降级为默认档案_当_业务名称未知() {
        assertThat(VoiceProfile.fromKey("unknown-profile"))
                .isEqualTo(VoiceProfile.DEFAULT);
    }
}
