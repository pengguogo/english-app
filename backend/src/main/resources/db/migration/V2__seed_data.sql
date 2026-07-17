-- 插入一个默认用户
INSERT INTO app_user (id, nickname) VALUES (1, '小朋友');

-- 插入主题：水果乐园
INSERT INTO theme (id, name, sort_order, is_locked) VALUES (1, '水果乐园', 1, 0);

-- 插入单元
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES (1, 1, 'Unit 1', 1, 0);
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES (2, 1, 'Unit 2', 2, 1);
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES (3, 1, 'Unit 3', 3, 1);

-- 插入课程
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(1, 1, '认识水果', 'WORD', '{"words":["apple","banana","orange"]}', 1, 3);
