package com.englishapp.voice;

import java.util.Arrays;

/**
 * 与供应商无关的业务语音档案。
 * 课程只引用稳定的业务名称，具体音色和参数由后端映射。
 */
public enum VoiceProfile {
    DEFAULT("default", 5, 5, 5),
    MIMI_WARM("mimi-warm", 5, 6, 5),
    MIMI_EXCITED("mimi-excited", 6, 6, 6),
    MIMI_ENCOURAGING("mimi-encouraging", 4, 6, 5),
    STORY_NARRATOR("story-narrator", 4, 5, 5),
    CHALLENGE_CURIOUS("challenge-curious", 5, 6, 5),
    ENGLISH_TEACHER("english-teacher", 4, 5, 5),
    TRAIN_CONDUCTOR("train-conductor", 5, 4, 5);

    private final String key;
    private final int baiduSpeed;
    private final int baiduPitch;
    private final int baiduVolume;

    VoiceProfile(String key, int baiduSpeed, int baiduPitch, int baiduVolume) {
        this.key = key;
        this.baiduSpeed = baiduSpeed;
        this.baiduPitch = baiduPitch;
        this.baiduVolume = baiduVolume;
    }

    public static VoiceProfile fromKey(String key) {
        if (key == null || key.isBlank()) return DEFAULT;
        return Arrays.stream(values())
                .filter(profile -> profile.key.equalsIgnoreCase(key))
                .findFirst()
                .orElse(DEFAULT);
    }

    public String getKey() { return key; }
    public int getBaiduSpeed() { return baiduSpeed; }
    public int getBaiduPitch() { return baiduPitch; }
    public int getBaiduVolume() { return baiduVolume; }
}
