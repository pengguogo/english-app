-- V13: 为 theme 表 subject_id 字段添加索引
-- subject_id 作为外键字段且高频查询（findBySubjectIdOrderBySortOrderAsc），需添加索引

CREATE INDEX idx_theme_subject_id ON theme(subject_id);
