package com.englishapp.voice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** 语音合成请求。 */
public class TtsRequest {
    @NotBlank
    @Size(max = 4000)
    private String text;
    private String lan = "en";
    private String voiceProfile = "default";
    private Boolean cacheable = true;

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getLan() { return lan; }
    public void setLan(String lan) { this.lan = lan; }
    public String getVoiceProfile() { return voiceProfile; }
    public void setVoiceProfile(String voiceProfile) { this.voiceProfile = voiceProfile; }
    public Boolean getCacheable() { return cacheable; }
    public void setCacheable(Boolean cacheable) { this.cacheable = cacheable; }
}
