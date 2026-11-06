package com.englishapp.service.ai.asr;

/**
 * 中文短语音识别。实现类不重试或最多重试 1 次，所有失败统一抛 {@link AsrException}。
 */
public interface SpeechRecognizer {

    /**
     * 识别一段 16k / 单声道 / 16bit PCM WAV，时长 ≤ 15s。
     *
     * @param wav 完整 WAV 字节（含 RIFF 头）
     * @return 识别结果，text 已去标点和首尾空白，保证非空
     * @throws AsrException 音频不合格（INVALID_AUDIO）或上游失败/超时/空结果（UNAVAILABLE）
     */
    AsrResult recognize(byte[] wav);

    /**
     * 只做本地校验（16k/单声道/16bit PCM、非空、时长上限），不出网、不计时。
     * 供 Controller 在扣额度前调用；recognize 内部仍会再校验一次。
     *
     * @throws AsrException 不合格时 reason=INVALID_AUDIO
     */
    void validate(byte[] wav);

    /** 供应商标识：baidu / dashscope */
    String provider();
}
