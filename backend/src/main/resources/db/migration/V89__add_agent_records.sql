-- 学习伙伴成长记录（阶段 3）：只保存已同意家庭的文字对话与答题结果，不存录音；180 天后自动清理。
-- 时间列由 JPA 写入（SQLite 方言存毫秒）。SQLite 默认不开外键约束，级联删除由 AgentRecordService 显式执行。

-- 会话：30 分钟无活动结束；source_page = LESSON（课程页提问）| FREE（自由提问）
CREATE TABLE agent_session (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    child_id INTEGER NOT NULL,
    family_id INTEGER NOT NULL,
    topic VARCHAR(100),
    source_page VARCHAR(16) NOT NULL,
    lesson_id INTEGER,
    flagged INTEGER NOT NULL DEFAULT 0,
    flag_category VARCHAR(32),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (child_id) REFERENCES app_user(id),
    FOREIGN KEY (family_id) REFERENCES family(id)
);
CREATE INDEX idx_agent_session_family_id ON agent_session(family_id, updated_at);
CREATE INDEX idx_agent_session_child_id ON agent_session(child_id, updated_at);
CREATE INDEX idx_agent_session_created_at ON agent_session(created_at);

-- 消息：CHILD 存脱敏后的原话；AGENT 存实际展示的 speech（有讲解时前缀「【标题】」）
-- moderation = NONE | SAFE_REPLY | OUTPUT_BLOCKED；theme_id 只在课程原文讲解时填写（学习状态 SEEN 的依据）
CREATE TABLE agent_message (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    session_id INTEGER NOT NULL,
    role VARCHAR(8) NOT NULL,
    text VARCHAR(500) NOT NULL,
    moderation VARCHAR(16) NOT NULL DEFAULT 'NONE',
    category VARCHAR(32),
    theme_id INTEGER,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (session_id) REFERENCES agent_session(id) ON DELETE CASCADE
);
CREATE INDEX idx_agent_message_session_id ON agent_message(session_id, id);
CREATE INDEX idx_agent_message_theme_id ON agent_message(theme_id);

-- 答题：/agent/chat 下发题目时插入（chosen、correct 为空 = 未作答），/agent/answer 首次作答时写入，之后重选不改库
-- options_json = [{id, text}]，chosen = 选项 id；quiz_token_hash = sha256(quizToken) 的十六进制
CREATE TABLE agent_interaction (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    session_id INTEGER NOT NULL,
    child_id INTEGER NOT NULL,
    theme_id INTEGER,
    lesson_id INTEGER,
    item_index INTEGER,
    question VARCHAR(200) NOT NULL,
    options_json VARCHAR(1000) NOT NULL,
    chosen VARCHAR(4),
    correct INTEGER,
    quiz_token_hash VARCHAR(64) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (session_id) REFERENCES agent_session(id) ON DELETE CASCADE,
    FOREIGN KEY (child_id) REFERENCES app_user(id)
);
CREATE INDEX idx_agent_interaction_session_id ON agent_interaction(session_id);
CREATE UNIQUE INDEX idx_agent_interaction_quiz_token_hash ON agent_interaction(quiz_token_hash);
CREATE INDEX idx_agent_interaction_child_id ON agent_interaction(child_id, theme_id, created_at);

-- 复习建议：最近 3 次答题错 ≥2 时生成；依据消失（会话被删 / 过期）时按 child + theme 重新计算并删除
CREATE TABLE review_suggestion (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    child_id INTEGER NOT NULL,
    theme_id INTEGER NOT NULL,
    basis VARCHAR(200) NOT NULL,
    route VARCHAR(200) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (child_id) REFERENCES app_user(id)
);
CREATE INDEX idx_review_suggestion_child_id ON review_suggestion(child_id, theme_id);
