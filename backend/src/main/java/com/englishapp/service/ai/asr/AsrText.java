package com.englishapp.service.ai.asr;

/** 识别文本清洗：去掉所有标点符号和首尾空白；清洗后为空视为失败。 */
final class AsrText {

    private AsrText() {
    }

    static String clean(String raw, String provider) {
        String text = raw == null ? "" : raw.replaceAll("\\p{P}", "").strip();
        if (text.isEmpty()) {
            throw new AsrException(AsrException.Reason.UNAVAILABLE, provider + " 识别结果为空");
        }
        return text;
    }
}
