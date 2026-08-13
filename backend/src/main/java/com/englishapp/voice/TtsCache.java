package com.englishapp.voice;

import com.englishapp.config.TtsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** 固定课程语音的本地磁盘缓存。 */
@Component
public class TtsCache {
    private static final Logger log = LoggerFactory.getLogger(TtsCache.class);
    private final TtsProperties properties;

    public TtsCache(TtsProperties properties) {
        this.properties = properties;
    }

    public byte[] read(String providerId, String text, String language, VoiceProfile profile) {
        if (!properties.isCacheEnabled()) return null;
        Path file = cacheFile(providerId, text, language, profile);
        if (!Files.isRegularFile(file)) return null;
        try {
            byte[] audio = Files.readAllBytes(file);
            return audio.length > 0 ? audio : null;
        } catch (IOException e) {
            log.warn("读取 TTS 缓存失败: {}", file, e);
            return null;
        }
    }

    public void write(String providerId, String text, String language,
                      VoiceProfile profile, byte[] audio) {
        if (!properties.isCacheEnabled() || audio == null || audio.length == 0) return;
        Path file = cacheFile(providerId, text, language, profile);
        try {
            Files.createDirectories(file.getParent());
            Path temp = Files.createTempFile(file.getParent(), "tts-", ".tmp");
            Files.write(temp, audio);
            try {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            log.warn("写入 TTS 缓存失败: {}", file, e);
        }
    }

    private Path cacheFile(String providerId, String text, String language, VoiceProfile profile) {
        String input = providerId + "\n" + language + "\n" + profile.getKey() + "\n" + text;
        return Path.of(properties.getCacheDir()).toAbsolutePath().normalize()
                .resolve(sha256(input) + ".mp3");
    }

    private String sha256(String input) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("当前 Java 环境不支持 SHA-256", e);
        }
    }
}
