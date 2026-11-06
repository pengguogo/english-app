package com.englishapp.service.ai.record;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/** 成长记录统一按北京时间存取：库里存北京本地时间，接口输出带 +08:00 的 ISO-8601 */
public final class RecordTime {

    public static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    private RecordTime() {
    }

    public static LocalDateTime now(Clock clock) {
        return LocalDateTime.now(clock.withZone(ZONE));
    }

    public static OffsetDateTime iso(LocalDateTime t) {
        return t == null ? null : t.truncatedTo(ChronoUnit.SECONDS).atZone(ZONE).toOffsetDateTime();
    }
}
