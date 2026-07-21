-- ============================================================
-- V20: 100以内加减法内容播种
-- 新增主题17 "百以内加减" (subject_id=3 数学)
-- 3个单元(36-38) × 10个课时(134-143) = 50题
-- 课时类型: CALCULATE 9个(加减运算) + QUIZ 1个(应用题)
-- 难度递进: 整十数 → 两位数加减一位数 → 两位数加减两位数 → 综合
-- ============================================================

-- Section 1: 新增主题（subject_id=3 数学）
INSERT INTO theme (id, name, sort_order, is_locked, subject_id) VALUES
(17, '百以内加减', 5, 0, 3);

-- Section 2: 新增单元
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(36, 17, '100以内加法', 1, 0),
(37, 17, '100以内减法', 2, 0),
(38, 17, '加减混合', 3, 1);

-- ============================================================
-- Section 3: 单元36 100以内加法（CALCULATE类型）
-- ============================================================

-- Lesson 134: 整十数加法 (CALCULATE, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(134, 36, '整十数加法', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"20 + 30 = ?","answer":"50","image":""},'
|| '{"question":"40 + 50 = ?","answer":"90","image":""},'
|| '{"question":"10 + 60 = ?","answer":"70","image":""},'
|| '{"question":"30 + 40 = ?","answer":"70","image":""},'
|| '{"question":"50 + 20 = ?","answer":"70","image":""}'
|| ']}', 1, 3);

-- Lesson 135: 两位数加一位数 (CALCULATE, 5题,含进位)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(135, 36, '两位数加一位数', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"23 + 5 = ?","answer":"28","image":""},'
|| '{"question":"27 + 5 = ?","answer":"32","image":""},'
|| '{"question":"38 + 6 = ?","answer":"44","image":""},'
|| '{"question":"49 + 3 = ?","answer":"52","image":""},'
|| '{"question":"56 + 8 = ?","answer":"64","image":""}'
|| ']}', 2, 3);

-- Lesson 136: 两位数加两位数 (CALCULATE, 5题,含进位)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(136, 36, '两位数加两位数', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"23 + 14 = ?","answer":"37","image":""},'
|| '{"question":"28 + 35 = ?","answer":"63","image":""},'
|| '{"question":"47 + 26 = ?","answer":"73","image":""},'
|| '{"question":"59 + 34 = ?","answer":"93","image":""},'
|| '{"question":"36 + 48 = ?","answer":"84","image":""}'
|| ']}', 3, 3);

-- Lesson 137: 加法综合练习 (CALCULATE, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(137, 36, '加法综合练习', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"30 + 45 = ?","answer":"75","image":""},'
|| '{"question":"17 + 6 = ?","answer":"23","image":""},'
|| '{"question":"53 + 28 = ?","answer":"81","image":""},'
|| '{"question":"9 + 36 = ?","answer":"45","image":""},'
|| '{"question":"44 + 38 = ?","answer":"82","image":""}'
|| ']}', 4, 4);

-- ============================================================
-- Section 4: 单元37 100以内减法（CALCULATE类型）
-- ============================================================

-- Lesson 138: 整十数减法 (CALCULATE, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(138, 37, '整十数减法', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"50 - 30 = ?","answer":"20","image":""},'
|| '{"question":"80 - 40 = ?","answer":"40","image":""},'
|| '{"question":"90 - 60 = ?","answer":"30","image":""},'
|| '{"question":"70 - 20 = ?","answer":"50","image":""},'
|| '{"question":"60 - 10 = ?","answer":"50","image":""}'
|| ']}', 1, 3);

-- Lesson 139: 两位数减一位数 (CALCULATE, 5题,含退位)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(139, 37, '两位数减一位数', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"28 - 5 = ?","answer":"23","image":""},'
|| '{"question":"32 - 5 = ?","answer":"27","image":""},'
|| '{"question":"50 - 7 = ?","answer":"43","image":""},'
|| '{"question":"43 - 8 = ?","answer":"35","image":""},'
|| '{"question":"61 - 4 = ?","answer":"57","image":""}'
|| ']}', 2, 3);

-- Lesson 140: 两位数减两位数 (CALCULATE, 5题,含退位)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(140, 37, '两位数减两位数', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"37 - 14 = ?","answer":"23","image":""},'
|| '{"question":"52 - 28 = ?","answer":"24","image":""},'
|| '{"question":"60 - 35 = ?","answer":"25","image":""},'
|| '{"question":"73 - 48 = ?","answer":"25","image":""},'
|| '{"question":"81 - 29 = ?","answer":"52","image":""}'
|| ']}', 3, 3);

-- Lesson 141: 减法综合练习 (CALCULATE, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(141, 37, '减法综合练习', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"80 - 30 = ?","answer":"50","image":""},'
|| '{"question":"45 - 7 = ?","answer":"38","image":""},'
|| '{"question":"63 - 29 = ?","answer":"34","image":""},'
|| '{"question":"90 - 56 = ?","answer":"34","image":""},'
|| '{"question":"74 - 38 = ?","answer":"36","image":""}'
|| ']}', 4, 4);

-- ============================================================
-- Section 5: 单元38 加减混合（CALCULATE + QUIZ类型）
-- ============================================================

-- Lesson 142: 加减混合运算 (CALCULATE, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(142, 38, '加减混合运算', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"25 + 13 - 8 = ?","answer":"30","image":""},'
|| '{"question":"50 - 20 + 15 = ?","answer":"45","image":""},'
|| '{"question":"18 + 9 + 10 = ?","answer":"37","image":""},'
|| '{"question":"40 - 12 - 8 = ?","answer":"20","image":""},'
|| '{"question":"33 + 17 - 20 = ?","answer":"30","image":""}'
|| ']}', 1, 4);

-- Lesson 143: 100以内应用题 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(143, 38, '100以内应用题', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"小明有35元，买书用了18元，还剩多少元？","options":["15元","17元","18元","20元"],"answer":1,"image":""},'
|| '{"question":"一班有28人，二班有32人，一共多少人？","options":["50人","58人","60人","62人"],"answer":2,"image":""},'
|| '{"question":"停车场有45辆车，开走17辆，还剩多少辆？","options":["26辆","27辆","28辆","29辆"],"answer":2,"image":""},'
|| '{"question":"小红有23颗糖，小明比她多15颗，小明有多少颗？","options":["35颗","36颗","37颗","38颗"],"answer":3,"image":""},'
|| '{"question":"一根绳子长60米，剪去25米，还剩多少米？","options":["30米","33米","35米","40米"],"answer":2,"image":""}'
|| ']}', 2, 4);
