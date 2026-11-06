package com.englishapp.service.ai.asr;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

/** 测试辅助：生成 WAV、启动本地 HttpServer 桩（不打真实网络）。 */
final class AsrTestSupport {

    private AsrTestSupport() {
    }

    static byte[] wav(int sampleRate, int channels, int bits, int durationMs) {
        int dataLen = (int) ((long) sampleRate * channels * (bits / 8) * durationMs / 1000);
        ByteBuffer b = ByteBuffer.allocate(44 + dataLen).order(ByteOrder.LITTLE_ENDIAN);
        b.put("RIFF".getBytes(StandardCharsets.US_ASCII)).putInt(36 + dataLen)
                .put("WAVE".getBytes(StandardCharsets.US_ASCII))
                .put("fmt ".getBytes(StandardCharsets.US_ASCII)).putInt(16)
                .putShort((short) 1).putShort((short) channels).putInt(sampleRate)
                .putInt(sampleRate * channels * bits / 8).putShort((short) (channels * bits / 8))
                .putShort((short) bits)
                .put("data".getBytes(StandardCharsets.US_ASCII)).putInt(dataLen);
        return b.array();
    }

    static byte[] validWav() {
        return wav(16000, 1, 16, 1000);
    }

    /** 单路由桩：记录最后一次请求体和 Authorization 头，按给定状态码/响应体回复，可延迟 */
    static final class Stub implements AutoCloseable {
        final HttpServer server;
        final AtomicReference<String> lastBody = new AtomicReference<>();
        final AtomicReference<String> lastAuth = new AtomicReference<>();
        volatile int status = 200;
        volatile String response = "{}";
        volatile long delayMs = 0;

        Stub() throws IOException {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/", ex -> {
                lastBody.set(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
                lastAuth.set(ex.getRequestHeaders().getFirst("Authorization"));
                if (delayMs > 0) {
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                }
                byte[] out = response.getBytes(StandardCharsets.UTF_8);
                ex.getResponseHeaders().add("Content-Type", "application/json");
                try {
                    ex.sendResponseHeaders(status, out.length);
                    try (OutputStream os = ex.getResponseBody()) {
                        os.write(out);
                    }
                } catch (IOException ignored) {
                    // 客户端超时断开
                }
            });
            server.start();
        }

        String url() {
            return "http://127.0.0.1:" + server.getAddress().getPort() + "/asr";
        }

        @Override
        public void close() {
            server.stop(0);
        }
    }

    static AsrProperties props(String url, int readTimeoutMs) {
        AsrProperties p = new AsrProperties();
        p.setReadTimeoutMs(readTimeoutMs);
        p.getBaidu().setEndpoint(url);
        p.getDashscope().setEndpoint(url);
        return p;
    }
}
