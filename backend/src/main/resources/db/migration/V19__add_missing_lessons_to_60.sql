-- ============================================================
-- V19: 补充 3 节缺失课时，使英语学科总课时达到 60 节
-- V18 注释声称新增 24 课，但实际只插入了 21 课
-- 水果乐园少 1 课，交通工具乐园少 2 课
-- 本迁移补齐这 3 节课
-- ============================================================

-- Lesson 131: 水果猜谜 (QUIZ) - 水果乐园 Unit 3
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(131, 3, '水果猜谜', 'QUIZ', '{"type":"QUIZ","items":[{"question":"What is red and round?","options":["apple","banana","grape"],"answer":0,"image":""},{"question":"What is yellow and long?","options":["apple","banana","orange"],"answer":1,"image":""},{"question":"What is purple and small?","options":["grape","watermelon","peach"],"answer":0,"image":""},{"question":"What is orange and round?","options":["orange","apple","pear"],"answer":0,"image":""}]}', 5, 3);

-- Lesson 132: 水上交通工具猜谜 (QUIZ) - 交通工具乐园 Unit 6
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(132, 6, '水上交通猜谜', 'QUIZ', '{"type":"QUIZ","items":[{"question":"What carries people across the ocean?","options":["ship","bus","train"],"answer":0,"image":""},{"question":"What is small and uses paddles?","options":["boat","plane","car"],"answer":0,"image":""},{"question":"What goes underwater?","options":["submarine","helicopter","truck"],"answer":0,"image":""},{"question":"What has a sail and uses wind?","options":["sailboat","rocket","bicycle"],"answer":0,"image":""}]}', 4, 3);

-- Lesson 133: 水上交通阅读 (READING) - 交通工具乐园 Unit 6
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(133, 6, '水上交通故事', 'READING', '{"type":"READING","items":[{"title":"A Day at the Harbor","content":"Today, Mimi went to the harbor. She saw a big ship carrying cars across the sea. A small boat was fishing near the dock. A submarine dived deep underwater. A sailboat glided quietly with the wind. Mimi loved watching the boats!","image":""}]}', 5, 3);
