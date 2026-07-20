-- ============================================================
-- V5: 水果乐园主题扩充
-- 参照交通工具主题(3 单元 10 课时,4+3+3 分布)补齐水果数据
-- 单元规划:
--   Unit 1 常见水果   (4 课: 2 WORD + 2 SENTENCE)
--   Unit 2 瓜果与浆果 (3 课: 1 WORD + 2 SENTENCE)
--   Unit 3 水果大集合 (3 课: 1 WORD + 2 SENTENCE)
-- 说明: V2 初始数据仅 1 单元 1 课时(旧格式),此处重命名为有意义的单元名,
--       删除旧课时后重新插入 10 节标准格式课时(WORD 8 词,SENTENCE 5 句)。
-- ============================================================

-- 1. 重命名水果主题下的 3 个单元(原为 "Unit 1/2/3",改为有意义的中文名)
UPDATE unit SET name = '常见水果' WHERE id = 1;
UPDATE unit SET name = '瓜果与浆果' WHERE id = 2;
UPDATE unit SET name = '水果大集合' WHERE id = 3;

-- 2. 清理 V2 中的旧课时(id=1,旧格式 {"words":[...]})及其可能关联的进度记录
DELETE FROM user_progress WHERE lesson_id = 1;
DELETE FROM lesson WHERE id = 1;

-- ============================================================
-- Unit 1: 常见水果 (4 课时)
-- ============================================================

-- Lesson 12: 认识苹果 (WORD) 8 个单词
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(12, 1, '认识苹果', 'WORD', '{"type":"WORD","items":[{"word":"apple","phonetic":"/ˈæpəl/","translation":"苹果","emoji":"🍎"},{"word":"banana","phonetic":"/bəˈnænə/","translation":"香蕉","emoji":"🍌"},{"word":"orange","phonetic":"/ˈɔːrɪndʒ/","translation":"橙子","emoji":"🍊"},{"word":"pear","phonetic":"/per/","translation":"梨","emoji":"🍐"},{"word":"grape","phonetic":"/ɡreɪp/","translation":"葡萄","emoji":"🍇"},{"word":"peach","phonetic":"/piːtʃ/","translation":"桃子","emoji":"🍑"},{"word":"lemon","phonetic":"/ˈlemən/","translation":"柠檬","emoji":"🍋"},{"word":"cherry","phonetic":"/ˈtʃeri/","translation":"樱桃","emoji":"🍒"}]}', 1, 3);

-- Lesson 13: 认识西瓜 (WORD) 8 个单词
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(13, 1, '认识西瓜', 'WORD', '{"type":"WORD","items":[{"word":"watermelon","phonetic":"/ˈwɔːtərmelən/","translation":"西瓜","emoji":"🍉"},{"word":"strawberry","phonetic":"/ˈstrɔːˌberi/","translation":"草莓","emoji":"🍓"},{"word":"blueberry","phonetic":"/ˈbluːˌberi/","translation":"蓝莓","emoji":"🫐"},{"word":"pineapple","phonetic":"/ˈpaɪˌnæpəl/","translation":"菠萝","emoji":"🍍"},{"word":"mango","phonetic":"/ˈmæŋɡoʊ/","translation":"芒果","emoji":"🥭"},{"word":"kiwi","phonetic":"/ˈkiːwiː/","translation":"猕猴桃","emoji":"🥝"},{"word":"coconut","phonetic":"/ˈkoʊkənʌt/","translation":"椰子","emoji":"🥥"},{"word":"avocado","phonetic":"/ˌævəˈkɑːdoʊ/","translation":"牛油果","emoji":"🥑"}]}', 2, 3);

-- Lesson 14: 我喜欢吃 (SENTENCE) 5 个句子
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(14, 1, '我喜欢吃', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"I like apples.","translation":"我喜欢苹果。","emoji":"🍎"},{"sentence":"I like bananas.","translation":"我喜欢香蕉。","emoji":"🍌"},{"sentence":"I like grapes.","translation":"我喜欢葡萄。","emoji":"🍇"},{"sentence":"I like peaches.","translation":"我喜欢桃子。","emoji":"🍑"},{"sentence":"I like cherries.","translation":"我喜欢樱桃。","emoji":"🍒"}]}', 3, 3);

-- Lesson 15: 它是什么颜色 (SENTENCE) 5 个句子
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(15, 1, '它是什么颜色', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"The apple is red.","translation":"苹果是红色的。","emoji":"🍎"},{"sentence":"The banana is yellow.","translation":"香蕉是黄色的。","emoji":"🍌"},{"sentence":"The grape is purple.","translation":"葡萄是紫色的。","emoji":"🍇"},{"sentence":"The orange is orange.","translation":"橙子是橙色的。","emoji":"🍊"},{"sentence":"The lemon is yellow.","translation":"柠檬是黄色的。","emoji":"🍋"}]}', 4, 3);

-- ============================================================
-- Unit 2: 瓜果与浆果 (3 课时)
-- ============================================================

-- Lesson 16: 认识瓜果 (WORD) 8 个单词
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(16, 2, '认识瓜果', 'WORD', '{"type":"WORD","items":[{"word":"watermelon","phonetic":"/ˈwɔːtərmelən/","translation":"西瓜","emoji":"🍉"},{"word":"melon","phonetic":"/ˈmelən/","translation":"甜瓜","emoji":"🍈"},{"word":"coconut","phonetic":"/ˈkoʊkənʌt/","translation":"椰子","emoji":"🥥"},{"word":"avocado","phonetic":"/ˌævəˈkɑːdoʊ/","translation":"牛油果","emoji":"🥑"},{"word":"strawberry","phonetic":"/ˈstrɔːˌberi/","translation":"草莓","emoji":"🍓"},{"word":"blueberry","phonetic":"/ˈbluːˌberi/","translation":"蓝莓","emoji":"🫐"},{"word":"cherry","phonetic":"/ˈtʃeri/","translation":"樱桃","emoji":"🍒"},{"word":"apple","phonetic":"/ˈæpəl/","translation":"苹果","emoji":"🍎"}]}', 1, 3);

-- Lesson 17: 味道怎么样 (SENTENCE) 5 个句子
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(17, 2, '味道怎么样', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"The strawberry is sweet.","translation":"草莓是甜的。","emoji":"🍓"},{"sentence":"The lemon is sour.","translation":"柠檬是酸的。","emoji":"🍋"},{"sentence":"The watermelon is sweet.","translation":"西瓜是甜的。","emoji":"🍉"},{"sentence":"The apple is crunchy.","translation":"苹果是脆的。","emoji":"🍎"},{"sentence":"The banana is soft.","translation":"香蕉是软的。","emoji":"🍌"}]}', 2, 3);

-- Lesson 18: 我想要 (SENTENCE) 5 个句子
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(18, 2, '我想要', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"I want an apple.","translation":"我想要一个苹果。","emoji":"🍎"},{"sentence":"I want a banana.","translation":"我想要一根香蕉。","emoji":"🍌"},{"sentence":"I want some grapes.","translation":"我想要一些葡萄。","emoji":"🍇"},{"sentence":"I want a peach.","translation":"我想要一个桃子。","emoji":"🍑"},{"sentence":"I want a strawberry.","translation":"我想要一颗草莓。","emoji":"🍓"}]}', 3, 3);

-- ============================================================
-- Unit 3: 水果大集合 (3 课时)
-- ============================================================

-- Lesson 19: 水果大集合 (WORD) 8 个单词(综合复习)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(19, 3, '水果大集合', 'WORD', '{"type":"WORD","items":[{"word":"apple","phonetic":"/ˈæpəl/","translation":"苹果","emoji":"🍎"},{"word":"banana","phonetic":"/bəˈnænə/","translation":"香蕉","emoji":"🍌"},{"word":"orange","phonetic":"/ˈɔːrɪndʒ/","translation":"橙子","emoji":"🍊"},{"word":"grape","phonetic":"/ɡreɪp/","translation":"葡萄","emoji":"🍇"},{"word":"watermelon","phonetic":"/ˈwɔːtərmelən/","translation":"西瓜","emoji":"🍉"},{"word":"strawberry","phonetic":"/ˈstrɔːˌberi/","translation":"草莓","emoji":"🍓"},{"word":"mango","phonetic":"/ˈmæŋɡoʊ/","translation":"芒果","emoji":"🥭"},{"word":"pineapple","phonetic":"/ˈpaɪˌnæpəl/","translation":"菠萝","emoji":"🍍"}]}', 1, 3);

-- Lesson 20: 我的一天 (SENTENCE) 5 个句子
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(20, 3, '我的一天', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"I eat an apple in the morning.","translation":"我早上吃一个苹果。","emoji":"🍎"},{"sentence":"I drink orange juice.","translation":"我喝橙汁。","emoji":"🍊"},{"sentence":"I like watermelon in summer.","translation":"我夏天喜欢西瓜。","emoji":"🍉"},{"sentence":"The banana is my favorite.","translation":"香蕉是我的最爱。","emoji":"🍌"},{"sentence":"Grapes are sweet.","translation":"葡萄是甜的。","emoji":"🍇"}]}', 2, 3);

-- Lesson 21: 复习大挑战 (SENTENCE) 5 个句子
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(21, 3, '复习大挑战', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"The apple is red.","translation":"苹果是红色的。","emoji":"🍎"},{"sentence":"The banana is yellow.","translation":"香蕉是黄色的。","emoji":"🍌"},{"sentence":"The watermelon is sweet.","translation":"西瓜是甜的。","emoji":"🍉"},{"sentence":"I like strawberries.","translation":"我喜欢草莓。","emoji":"🍓"},{"sentence":"The grape is purple.","translation":"葡萄是紫色的。","emoji":"🍇"}]}', 3, 3);
