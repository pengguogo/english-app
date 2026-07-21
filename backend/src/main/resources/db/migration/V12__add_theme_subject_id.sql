-- V12: theme 表新增 subject_id 外键列
ALTER TABLE theme ADD COLUMN subject_id INTEGER;
UPDATE theme SET subject_id = 1 WHERE subject_id IS NULL;
