package com.englishapp.service.ai.asr;

/**
 * 识别结果。
 *
 * @param text      去标点、去首尾空白后的识别文本，非空
 * @param provider  供应商标识：baidu / dashscope
 * @param latencyMs 上游调用耗时（毫秒，不含本地校验）
 */
public record AsrResult(String text, String provider, long latencyMs) {
}
