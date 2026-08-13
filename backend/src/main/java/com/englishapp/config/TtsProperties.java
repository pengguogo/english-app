package com.englishapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** TTS 编排与缓存配置。 */
@Component
@ConfigurationProperties(prefix = "voice.tts")
public class TtsProperties {
    private boolean cacheEnabled = true;
    private String cacheDir = ".tts-cache";

    public boolean isCacheEnabled() { return cacheEnabled; }
    public void setCacheEnabled(boolean cacheEnabled) { this.cacheEnabled = cacheEnabled; }
    public String getCacheDir() { return cacheDir; }
    public void setCacheDir(String cacheDir) { this.cacheDir = cacheDir; }
}
