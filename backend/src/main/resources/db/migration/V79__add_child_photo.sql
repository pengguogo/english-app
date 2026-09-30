CREATE TABLE child_photo (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    taken_at DATE NOT NULL,
    caption VARCHAR(200),
    file_name VARCHAR(80) NOT NULL,
    content_type VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE INDEX idx_child_photo_user_id_taken_at ON child_photo(user_id, taken_at);
