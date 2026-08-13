package com.englishapp.voice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VoiceServiceImplTest {

    @Test
    void should_返回百度音频_当_供应商可用() {
        BaiduVoiceService baidu = mock(BaiduVoiceService.class);
        when(baidu.isAvailable()).thenReturn(true);
        when(baidu.synthesize(anyString(), anyString(), any())).thenReturn(new byte[]{9, 8});
        TtsCache cache = mock(TtsCache.class);
        VoiceServiceImpl service = new VoiceServiceImpl(baidu, cache);

        byte[] result = service.textToSpeech(
                "再试一次", "zh", "mimi-encouraging", false);

        assertThat(result).containsExactly(9, 8);
        verify(baidu).synthesize("再试一次", "zh", VoiceProfile.MIMI_ENCOURAGING);
    }

    @Test
    void should_跳过供应商调用_当_固定文本命中缓存() {
        BaiduVoiceService baidu = mock(BaiduVoiceService.class);
        when(baidu.isAvailable()).thenReturn(true);
        TtsCache cache = mock(TtsCache.class);
        when(cache.read("baidu", "欢迎回来", "zh", VoiceProfile.MIMI_WARM))
                .thenReturn(new byte[]{4, 5});
        VoiceServiceImpl service = new VoiceServiceImpl(baidu, cache);

        byte[] result = service.textToSpeech("欢迎回来", "zh", "mimi-warm", true);

        assertThat(result).containsExactly(4, 5);
        verify(baidu, never()).synthesize(anyString(), anyString(), any());
    }

}
