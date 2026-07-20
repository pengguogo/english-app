package com.englishapp.voice;

import com.englishapp.voice.dto.ScoreResponse;

/**
 * 语音服务接口
 * <p>
 * 定义语音合成(TTS)、语音识别(ASR)与发音评测能力,
 * 由具体的语音服务提供商(如百度)实现。
 * </p>
 *
 * @author englishapp
 * @since 1.0.0
 */
public interface VoiceService {
    /**
     * 文字转语音
     *
     * @param text 待合成语音的文本
     * @return 音频二进制数据
     */
    byte[] textToSpeech(String text);

    /**
     * 语音转文字
     *
     * @param audioData 音频二进制数据
     * @return 识别出的文本
     */
    String speechToText(byte[] audioData);

    /**
     * 发音评测
     *
     * @param audioData 音频数据(wav 16k 16bit mono)
     * @param text      参考文本
     * @return 评测结果(分数 + 反馈)
     */
    ScoreResponse scorePronunciation(byte[] audioData, String text);
}
