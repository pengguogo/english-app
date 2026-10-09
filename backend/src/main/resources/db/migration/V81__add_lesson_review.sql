CREATE TABLE lesson_study_session (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    lesson_id INTEGER NOT NULL,
    study_date DATE NOT NULL,
    pending_seconds INTEGER NOT NULL DEFAULT 0,
    passed BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(user_id, lesson_id, study_date)
);
CREATE INDEX idx_lesson_study_session_user_date ON lesson_study_session(user_id, study_date);
