package com.englishapp.service.ai.record;

import com.englishapp.domain.AgentSession;
import com.englishapp.repository.AgentSessionRepository;
import java.time.Clock;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 保留期：每天北京时间 3 点删除 created_at 早于 180 天的会话（级联消息、答题，重算建议） */
@Component
public class RetentionJob {

    private static final Logger log = LoggerFactory.getLogger(RetentionJob.class);
    static final int RETENTION_DAYS = 180;

    private final AgentSessionRepository sessions;
    private final AgentRecordService records;
    private final Clock clock;

    public RetentionJob(AgentSessionRepository sessions, AgentRecordService records, Clock clock) {
        this.sessions = sessions;
        this.records = records;
        this.clock = clock;
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Shanghai")
    public void scheduled() {
        try {
            purge();
        } catch (RuntimeException e) {
            log.error("agent record retention failed", e);
        }
    }

    /** @return 删除的会话数 */
    public int purge() {
        List<AgentSession> expired = sessions.findByCreatedAtBefore(RecordTime.now(clock).minusDays(RETENTION_DAYS));
        records.deleteSessions(expired);
        log.info("agent record retention: deleted {} sessions", expired.size());
        return expired.size();
    }
}
