package com.englishapp.service.ai.asr;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WavAudioTest {

    @Test
    void should_返回时长_当_16k单声道16bit() {
        assertThat(WavAudio.validate(AsrTestSupport.wav(16000, 1, 16, 1500), 15000)).isEqualTo(1500);
    }

    @Test
    void should_拒绝_当_采样率不是16k() {
        assertInvalid(AsrTestSupport.wav(8000, 1, 16, 1000));
    }

    @Test
    void should_拒绝_当_双声道() {
        assertInvalid(AsrTestSupport.wav(16000, 2, 16, 1000));
    }

    @Test
    void should_拒绝_当_8bit() {
        assertInvalid(AsrTestSupport.wav(16000, 1, 8, 1000));
    }

    @Test
    void should_拒绝_当_超过15秒() {
        assertInvalid(AsrTestSupport.wav(16000, 1, 16, 15100));
    }

    @Test
    void should_通过_当_恰好15秒() {
        assertThat(WavAudio.validate(AsrTestSupport.wav(16000, 1, 16, 15000), 15000)).isEqualTo(15000);
    }

    @Test
    void should_拒绝_当_为空或非RIFF() {
        assertInvalid(null);
        assertInvalid(new byte[0]);
        byte[] notWav = AsrTestSupport.validWav();
        notWav[0] = 'X';
        assertInvalid(notWav);
    }

    @Test
    void should_拒绝_当_没有音频数据() {
        assertInvalid(AsrTestSupport.wav(16000, 1, 16, 0));
    }

    private static void assertInvalid(byte[] wav) {
        assertThatThrownBy(() -> WavAudio.validate(wav, 15000))
                .isInstanceOf(AsrException.class)
                .extracting(e -> ((AsrException) e).getReason())
                .isEqualTo(AsrException.Reason.INVALID_AUDIO);
    }
}
