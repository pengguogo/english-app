-- event_id 由全局主键改为 (user_id, event_id) 联合主键，避免不同孩子相同 eventId 被静默丢弃。
-- SQLite 不能直接改主键：建新表 → 复制 → 校验行数 → 删旧表 → 改名。
CREATE TABLE character_attempt_new (
    event_id TEXT NOT NULL,
    user_id INTEGER NOT NULL,
    word TEXT NOT NULL,
    outcome TEXT NOT NULL,
    attempt_date TEXT NOT NULL,
    PRIMARY KEY (user_id, event_id)
);
INSERT INTO character_attempt_new (event_id, user_id, word, outcome, attempt_date)
SELECT event_id, user_id, word, outcome, attempt_date FROM character_attempt;

-- 行数不一致时 CHECK 失败，整个迁移回滚
CREATE TABLE character_attempt_copy_check (ok INTEGER NOT NULL CHECK (ok = 1));
INSERT INTO character_attempt_copy_check (ok)
SELECT (SELECT COUNT(*) FROM character_attempt) = (SELECT COUNT(*) FROM character_attempt_new);
DROP TABLE character_attempt_copy_check;

DROP TABLE character_attempt;
ALTER TABLE character_attempt_new RENAME TO character_attempt;
