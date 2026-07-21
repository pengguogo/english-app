-- 错题集表:记录用户在各课时中答错的题目,用于错题回顾与掌握度统计
CREATE TABLE wrong_answer (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    lesson_id INTEGER NOT NULL,
    lesson_name VARCHAR(100),
    question_index INTEGER NOT NULL,
    question_type VARCHAR(20) NOT NULL,
    question_snapshot TEXT,
    user_answer TEXT,
    correct_answer TEXT,
    wrong_count INTEGER DEFAULT 1,
    is_resolved BOOLEAN DEFAULT FALSE,
    last_wrong_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id),
    FOREIGN KEY (lesson_id) REFERENCES lesson(id),
    UNIQUE(user_id, lesson_id, question_index)
);
CREATE INDEX idx_wrong_answer_user_id ON wrong_answer(user_id);
CREATE INDEX idx_wrong_answer_lesson_id ON wrong_answer(lesson_id);
