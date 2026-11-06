package com.englishapp.service.ai.asr;

/**
 * 语音识别统一异常。message 不含音频和识别正文，可直接记日志。
 */
public class AsrException extends RuntimeException {

    public enum Reason {
        /** 音频格式不合格：非 16k/单声道/16bit WAV、为空或超过 15s，建议映射 400 */
        INVALID_AUDIO,
        /** 上游失败：未配置密钥、超时、网络错误、供应商报错、空结果，映射 503/5031 */
        UNAVAILABLE
    }

    private final Reason reason;

    public AsrException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public AsrException(Reason reason, String message, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
