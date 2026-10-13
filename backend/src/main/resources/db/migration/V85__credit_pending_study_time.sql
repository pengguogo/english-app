-- 将旧规则下未入账的学习时长按原日期补记，保留已有家长解锁状态。
INSERT INTO daily_study_time (user_id, study_date, seconds, updated_at)
SELECT user_id, study_date, MIN(300, SUM(pending_seconds)), CURRENT_TIMESTAMP
FROM lesson_study_session
WHERE pending_seconds > 0
GROUP BY user_id, study_date
ON CONFLICT(user_id, study_date) DO UPDATE SET
    seconds = MIN(300, daily_study_time.seconds + excluded.seconds),
    updated_at = CURRENT_TIMESTAMP;

-- 复习结果继续保留，暂存时间不再用于复习结算。
UPDATE lesson_study_session SET pending_seconds = 0 WHERE pending_seconds > 0;
