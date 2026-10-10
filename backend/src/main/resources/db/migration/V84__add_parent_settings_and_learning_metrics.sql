-- 家长密码只保存带盐摘要；实际时长与游戏解锁时长独立。
CREATE TABLE parent_game_setting (
    id INTEGER PRIMARY KEY,
    password_hash TEXT NOT NULL,
    failed_attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMP
);
ALTER TABLE daily_study_time ADD COLUMN parent_unlocked BOOLEAN NOT NULL DEFAULT FALSE;
CREATE TABLE study_time_event (
    id VARCHAR(36) PRIMARY KEY,
    user_id INTEGER NOT NULL,
    lesson_id INTEGER NOT NULL,
    study_date DATE NOT NULL,
    seconds INTEGER NOT NULL CHECK (seconds BETWEEN 1 AND 30)
);
CREATE INDEX idx_study_time_event_user_date ON study_time_event(user_id, study_date);
CREATE TABLE learning_attempt (
    id VARCHAR(36) PRIMARY KEY,
    user_id INTEGER NOT NULL,
    lesson_id INTEGER NOT NULL,
    question_index INTEGER NOT NULL,
    study_date DATE NOT NULL,
    first_correct BOOLEAN NOT NULL,
    assisted_correct BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX idx_learning_attempt_user_date ON learning_attempt(user_id, study_date);
