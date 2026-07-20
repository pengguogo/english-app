-- ============================================================
-- V6: 汪汪队立大功主题(PAW Patrol)
-- 参照交通工具主题(3 单元 10 课时,4+3+3 分布)构建
-- 单元规划:
--   Unit 7 狗狗小队   (4 课: 2 WORD + 2 SENTENCE)
--   Unit 8 救援行动   (3 课: 1 WORD + 2 SENTENCE)
--   Unit 9 团队精神   (3 课: 1 WORD + 2 SENTENCE)
-- 说明: 汪汪队立大功是深受小朋友喜爱的动画,以狗狗救援队为主线
--       学习狗狗名字、救援装备、团队合作等英语词汇和句子
-- ============================================================

-- 1. 新增主题:汪汪队立大功
INSERT INTO theme (id, name, sort_order, is_locked) VALUES (3, '汪汪队立大功', 3, 0);

-- 2. 新增单元
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(7, 3, '狗狗小队', 1, 0),
(8, 3, '救援行动', 2, 1),
(9, 3, '团队精神', 3, 1);

-- ============================================================
-- Unit 7: 狗狗小队 (4 课时)
-- ============================================================

-- Lesson 22: 认识狗狗 (WORD) 8 个单词
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(22, 7, '认识狗狗', 'WORD', '{"type":"WORD","items":[{"word":"Chase","phonetic":"/tʃeɪs/","translation":"阿奇(警察狗)","emoji":"🐕"},{"word":"Marshall","phonetic":"/ˈmɑːrʃəl/","translation":"毛毛(消防狗)","emoji":"🔥"},{"word":"Skye","phonetic":"/skaɪ/","translation":"天天(飞行狗)","emoji":"🚁"},{"word":"Rubble","phonetic":"/ˈrʌbəl/","translation":"小砾(工程狗)","emoji":"🚧"},{"word":"Zuma","phonetic":"/ˈzuːmə/","translation":"路马(水上救生狗)","emoji":"🌊"},{"word":"Rocky","phonetic":"/ˈrɑːki/","translation":"灰灰(回收狗)","emoji":"♻️"},{"word":"puppy","phonetic":"/ˈpʌpi/","translation":"小狗","emoji":"🐶"},{"word":"team","phonetic":"/tiːm/","translation":"队伍","emoji":"🦮"}]}', 1, 3);

-- Lesson 23: 狗狗的本领 (WORD) 8 个单词
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(23, 7, '狗狗的本领', 'WORD', '{"type":"WORD","items":[{"word":"police","phonetic":"/pəˈliːs/","translation":"警察","emoji":"👮"},{"word":"fire","phonetic":"/ˈfaɪər/","translation":"火","emoji":"🔥"},{"word":"fly","phonetic":"/flaɪ/","translation":"飞","emoji":"✈️"},{"word":"build","phonetic":"/bɪld/","translation":"建造","emoji":"🔨"},{"word":"swim","phonetic":"/swɪm/","translation":"游泳","emoji":"🏊"},{"word":"recycle","phonetic":"/ˌriːˈsaɪkəl/","translation":"回收","emoji":"♻️"},{"word":"rescue","phonetic":"/ˈreskjuː/","translation":"救援","emoji":"🚑"},{"word":"help","phonetic":"/help/","translation":"帮助","emoji":"🤝"}]}', 2, 3);

-- Lesson 24: 狗狗在做什么 (SENTENCE) 5 个句子
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(24, 7, '狗狗在做什么', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"Chase is a police dog.","translation":"阿奇是警犬。","emoji":"🐕"},{"sentence":"Marshall puts out fires.","translation":"毛毛灭火。","emoji":"🔥"},{"sentence":"Skye can fly.","translation":"天天会飞。","emoji":"🚁"},{"sentence":"Rubble builds things.","translation":"小砾会建造。","emoji":"🚧"},{"sentence":"The team helps people.","translation":"队伍帮助人们。","emoji":"🦮"}]}', 3, 3);

-- Lesson 25: 我喜欢哪只狗狗 (SENTENCE) 5 个句子
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(25, 7, '我喜欢哪只狗狗', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"I like Chase.","translation":"我喜欢阿奇。","emoji":"🐕"},{"sentence":"I like Marshall.","translation":"我喜欢毛毛。","emoji":"🔥"},{"sentence":"I like Skye.","translation":"我喜欢天天。","emoji":"🚁"},{"sentence":"I like Rubble.","translation":"我喜欢小砾。","emoji":"🚧"},{"sentence":"I like the puppy team.","translation":"我喜欢小狗队。","emoji":"🐶"}]}', 4, 3);

-- ============================================================
-- Unit 8: 救援行动 (3 课时)
-- ============================================================

-- Lesson 26: 救援装备 (WORD) 8 个单词
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(26, 8, '救援装备', 'WORD', '{"type":"WORD","items":[{"word":"helicopter","phonetic":"/ˈhelɪkɑːptər/","translation":"直升机","emoji":"🚁"},{"word":"truck","phonetic":"/trʌk/","translation":"卡车","emoji":"🚚"},{"word":"boat","phonetic":"/boʊt/","translation":"小船","emoji":"⛵"},{"word":"rope","phonetic":"/roʊp/","translation":"绳子","emoji":"🪢"},{"word":"ladder","phonetic":"/ˈlædər/","translation":"梯子","emoji":"🪜"},{"word":"flashlight","phonetic":"/ˈflæʃlaɪt/","translation":"手电筒","emoji":"🔦"},{"word":"whistle","phonetic":"/ˈwɪsəl/","translation":"哨子","emoji":"📣"},{"word":"badge","phonetic":"/bædʒ/","translation":"徽章","emoji":"🎖️"}]}', 1, 3);

-- Lesson 27: 去救援 (SENTENCE) 5 个句子
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(27, 8, '去救援', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"The team goes to rescue.","translation":"队伍去救援。","emoji":"🚑"},{"sentence":"Skye flies the helicopter.","translation":"天天开直升机。","emoji":"🚁"},{"sentence":"Marshall drives the fire truck.","translation":"毛毛开消防车。","emoji":"🚒"},{"sentence":"Chase uses the rope.","translation":"阿奇用绳子。","emoji":"🪢"},{"sentence":"Rubble uses the ladder.","translation":"小砾用梯子。","emoji":"🪜"}]}', 2, 3);

-- Lesson 28: 救援成功 (SENTENCE) 5 个句子
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(28, 8, '救援成功', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"The puppy saves the day.","translation":"小狗拯救了大家。","emoji":"🦸"},{"sentence":"Everyone is safe.","translation":"每个人都安全了。","emoji":"😌"},{"sentence":"Good job, team!","translation":"干得好,队伍!","emoji":"👍"},{"sentence":"The mission is done.","translation":"任务完成了。","emoji":"✅"},{"sentence":"The dogs are heroes.","translation":"狗狗们是英雄。","emoji":"🏆"}]}', 3, 3);

-- ============================================================
-- Unit 9: 团队精神 (3 课时)
-- ============================================================

-- Lesson 29: 团队合作 (WORD) 8 个单词
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(29, 9, '团队合作', 'WORD', '{"type":"WORD","items":[{"word":"together","phonetic":"/təˈɡeðər/","translation":"一起","emoji":"🤝"},{"word":"friend","phonetic":"/frend/","translation":"朋友","emoji":"👫"},{"word":"brave","phonetic":"/breɪv/","translation":"勇敢的","emoji":"🦁"},{"word":"strong","phonetic":"/strɔːŋ/","translation":"强壮的","emoji":"💪"},{"word":"fast","phonetic":"/fæst/","translation":"快的","emoji":"⚡"},{"word":"smart","phonetic":"/smɑːrt/","translation":"聪明的","emoji":"🧠"},{"word":"hero","phonetic":"/ˈhɪroʊ/","translation":"英雄","emoji":"🦸"},{"word":"mission","phonetic":"/ˈmɪʃən/","translation":"任务","emoji":"🎯"}]}', 1, 3);

-- Lesson 30: 一起合作 (SENTENCE) 5 个句子
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(30, 9, '一起合作', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"We work together.","translation":"我们一起合作。","emoji":"🤝"},{"sentence":"The dogs are brave.","translation":"狗狗们很勇敢。","emoji":"🦁"},{"sentence":"The dogs are strong.","translation":"狗狗们很强壮。","emoji":"💪"},{"sentence":"They are a team.","translation":"他们是一支队伍。","emoji":"🦮"},{"sentence":"Friends help each other.","translation":"朋友互相帮助。","emoji":"👫"}]}', 2, 3);

-- Lesson 31: 汪汪队复习大挑战 (SENTENCE) 5 个句子
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(31, 9, '复习大挑战', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"Chase is a police dog.","translation":"阿奇是警犬。","emoji":"🐕"},{"sentence":"Marshall puts out fires.","translation":"毛毛灭火。","emoji":"🔥"},{"sentence":"The team goes to rescue.","translation":"队伍去救援。","emoji":"🚑"},{"sentence":"We work together.","translation":"我们一起合作。","emoji":"🤝"},{"sentence":"The dogs are heroes.","translation":"狗狗们是英雄。","emoji":"🏆"}]}', 3, 3);
