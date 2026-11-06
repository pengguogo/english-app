package com.englishapp.service.ai.asr;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * WAV 入口校验：只接受 16k / 单声道 / 16bit PCM，时长不超过上限。
 * 按 RIFF chunk 遍历查找 fmt 和 data，兼容 fmt 后夹带 LIST 等块的文件。
 */
final class WavAudio {

    static final int SAMPLE_RATE = 16000;
    private static final int BYTES_PER_SECOND = SAMPLE_RATE * 2;

    private WavAudio() {
    }

    /** 校验通过返回时长（毫秒），否则抛 INVALID_AUDIO */
    static long validate(byte[] wav, int maxDurationMs) {
        if (wav == null || wav.length < 44) {
            throw invalid("音频为空或不是 WAV");
        }
        if (!"RIFF".equals(tag(wav, 0)) || !"WAVE".equals(tag(wav, 8))) {
            throw invalid("不是 RIFF/WAVE 格式");
        }
        ByteBuffer buf = ByteBuffer.wrap(wav).order(ByteOrder.LITTLE_ENDIAN);
        boolean fmtOk = false;
        long dataLen = -1;
        int pos = 12;
        while (pos + 8 <= wav.length) {
            String id = tag(wav, pos);
            long size = Integer.toUnsignedLong(buf.getInt(pos + 4));
            int body = pos + 8;
            if ("fmt ".equals(id)) {
                if (size < 16 || body + 16 > wav.length) {
                    throw invalid("fmt 块不完整");
                }
                int format = Short.toUnsignedInt(buf.getShort(body));
                int channels = Short.toUnsignedInt(buf.getShort(body + 2));
                int rate = buf.getInt(body + 4);
                int bits = Short.toUnsignedInt(buf.getShort(body + 14));
                if (format != 1 || channels != 1 || rate != SAMPLE_RATE || bits != 16) {
                    throw invalid("需要 16k/单声道/16bit PCM，实际 format=" + format
                            + " channels=" + channels + " rate=" + rate + " bits=" + bits);
                }
                fmtOk = true;
            } else if ("data".equals(id)) {
                // 以实际剩余字节为准，防止头里写的长度失真
                dataLen = Math.min(size, wav.length - body);
                break;
            }
            pos = (int) Math.min((long) body + size + (size & 1), Integer.MAX_VALUE);
        }
        if (!fmtOk) {
            throw invalid("缺少 fmt 块或 fmt 在 data 之后");
        }
        if (dataLen <= 0) {
            throw invalid("缺少音频数据");
        }
        long durationMs = dataLen * 1000 / BYTES_PER_SECOND;
        if (durationMs > maxDurationMs) {
            throw invalid("音频时长 " + durationMs + "ms 超过上限 " + maxDurationMs + "ms");
        }
        return durationMs;
    }

    private static String tag(byte[] b, int off) {
        return new String(b, off, 4, StandardCharsets.US_ASCII);
    }

    private static AsrException invalid(String msg) {
        return new AsrException(AsrException.Reason.INVALID_AUDIO, msg);
    }
}
