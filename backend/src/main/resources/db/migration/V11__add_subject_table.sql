-- V11: 新建学科表，支持多学科学习平台
CREATE TABLE subject (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(20) NOT NULL,
    icon_url VARCHAR(255),
    color VARCHAR(20),
    sort_order INTEGER DEFAULT 0,
    is_locked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO subject (id, name, code, icon_url, color, sort_order, is_locked) VALUES
(1, '英语', 'ENGLISH', NULL, '#6B7CFF', 1, FALSE),
(2, '语文', 'CHINESE', NULL, '#FF7043', 2, FALSE),
(3, '数学', 'MATH', NULL, '#26A69A', 3, FALSE),
(4, '课外', 'EXTRACURRICULAR', NULL, '#7E57C2', 4, FALSE);
