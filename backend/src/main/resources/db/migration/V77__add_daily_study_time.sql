CREATE TABLE daily_study_time (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    study_date DATE NOT NULL,
    seconds INTEGER NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, study_date),
    FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE INDEX idx_daily_study_time_user_date
    ON daily_study_time(user_id, study_date);
