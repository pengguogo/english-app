package com.englishapp.service.ai.asr;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

/** 识别请求的 HTTP 发送：连接/读取超时来自 AsrProperties，不重试，失败统一转 UNAVAILABLE。 */
final class AsrHttp {

    record Response(int status, String body) {
    }

    private final HttpClient client;
    private final Duration readTimeout;

    AsrHttp(AsrProperties props) {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(props.getConnectTimeoutMs()))
                .build();
        this.readTimeout = Duration.ofMillis(props.getReadTimeoutMs());
    }

    Response postJson(String provider, String url, String json, Map<String, String> headers) {
        HttpRequest.Builder req = HttpRequest.newBuilder(URI.create(url))
                .timeout(readTimeout)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8));
        headers.forEach(req::header);
        try {
            HttpResponse<String> resp = client.send(req.build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return new Response(resp.statusCode(), resp.body());
        } catch (HttpTimeoutException e) {
            throw new AsrException(AsrException.Reason.UNAVAILABLE, provider + " 识别超时", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AsrException(AsrException.Reason.UNAVAILABLE, provider + " 识别被中断", e);
        } catch (IOException e) {
            throw new AsrException(AsrException.Reason.UNAVAILABLE, provider + " 网络错误", e);
        }
    }
}
