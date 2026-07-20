-- ============================================================
-- V9: 给汪汪队和小砾工程队新单元的单词课时加 image 字段
-- 使用 REPLACE 在每个 item 的 emoji+translation 后插入 image 字段
-- 涉及课时: Lesson 22, 23, 35, 36
-- ============================================================

-- ============================================================
-- Lesson 22: 认识狗狗 (id=22)
-- ============================================================
UPDATE lesson SET content = REPLACE(content, '"emoji":"🐕","translation":"阿奇(警察狗)"', '"emoji":"🐕","translation":"阿奇(警察狗)","image":"chase"') WHERE id = 22;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🔥","translation":"毛毛(消防狗)"', '"emoji":"🔥","translation":"毛毛(消防狗)","image":"marshall"') WHERE id = 22;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🚁","translation":"天天(飞行狗)"', '"emoji":"🚁","translation":"天天(飞行狗)","image":"skye"') WHERE id = 22;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🚧","translation":"小砾(工程狗)"', '"emoji":"🚧","translation":"小砾(工程狗)","image":"rubble"') WHERE id = 22;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🌊","translation":"路马(水上救生狗)"', '"emoji":"🌊","translation":"路马(水上救生狗)","image":"zuma"') WHERE id = 22;
UPDATE lesson SET content = REPLACE(content, '"emoji":"♻️","translation":"灰灰(回收狗)"', '"emoji":"♻️","translation":"灰灰(回收狗)","image":"rocky"') WHERE id = 22;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🐶","translation":"小狗"', '"emoji":"🐶","translation":"小狗","image":"puppy"') WHERE id = 22;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🦮","translation":"队伍"', '"emoji":"🦮","translation":"队伍","image":"team"') WHERE id = 22;

-- ============================================================
-- Lesson 23: 狗狗的本领 (id=23)
-- ============================================================
UPDATE lesson SET content = REPLACE(content, '"emoji":"👮","translation":"警察"', '"emoji":"👮","translation":"警察","image":"police"') WHERE id = 23;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🔥","translation":"火"', '"emoji":"🔥","translation":"火","image":"fire-word"') WHERE id = 23;
UPDATE lesson SET content = REPLACE(content, '"emoji":"✈️","translation":"飞"', '"emoji":"✈️","translation":"飞","image":"fly"') WHERE id = 23;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🔨","translation":"建造"', '"emoji":"🔨","translation":"建造","image":"build"') WHERE id = 23;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🏊","translation":"游泳"', '"emoji":"🏊","translation":"游泳","image":"swim"') WHERE id = 23;
UPDATE lesson SET content = REPLACE(content, '"emoji":"♻️","translation":"回收"', '"emoji":"♻️","translation":"回收","image":"recycle"') WHERE id = 23;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🚑","translation":"救援"', '"emoji":"🚑","translation":"救援","image":"rescue"') WHERE id = 23;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🤝","translation":"帮助"', '"emoji":"🤝","translation":"帮助","image":"help"') WHERE id = 23;

-- ============================================================
-- Lesson 35: 认识工程狗狗 (id=35)
-- ============================================================
UPDATE lesson SET content = REPLACE(content, '"emoji":"🚧","translation":"推土犬(斗牛犬)"', '"emoji":"🚧","translation":"推土犬(斗牛犬)","image":"dozer"') WHERE id = 35;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🔧","translation":"管道犬(腊肠犬)"', '"emoji":"🔧","translation":"管道犬(腊肠犬)","image":"pipes"') WHERE id = 35;
UPDATE lesson SET content = REPLACE(content, '"emoji":"⚡","translation":"电钻犬(杰克罗素)"', '"emoji":"⚡","translation":"电钻犬(杰克罗素)","image":"bolt"') WHERE id = 35;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🪨","translation":"搅拌犬(比格犬)"', '"emoji":"🪨","translation":"搅拌犬(比格犬)","image":"mixer"') WHERE id = 35;
UPDATE lesson SET content = REPLACE(content, '"emoji":"📌","translation":"钉子犬(雪纳瑞)"', '"emoji":"📌","translation":"钉子犬(雪纳瑞)","image":"nails"') WHERE id = 35;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🔧","translation":"扳手犬(柯基)"', '"emoji":"🔧","translation":"扳手犬(柯基)","image":"wrench-corgi"') WHERE id = 35;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🔨","translation":"锤子犬(拳师犬)"', '"emoji":"🔨","translation":"锤子犬(拳师犬)","image":"hammer-boxer"') WHERE id = 35;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🏗️","translation":"起重犬(哈士奇)"', '"emoji":"🏗️","translation":"起重犬(哈士奇)","image":"crank"') WHERE id = 35;

-- ============================================================
-- Lesson 36: 狗狗的工具 (id=36)
-- ============================================================
UPDATE lesson SET content = REPLACE(content, '"emoji":"🚧","translation":"推土铲"', '"emoji":"🚧","translation":"推土铲","image":"dozer-blade"') WHERE id = 36;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🔧","translation":"管钳"', '"emoji":"🔧","translation":"管钳","image":"pipe-wrench"') WHERE id = 36;
UPDATE lesson SET content = REPLACE(content, '"emoji":"⚡","translation":"电钻"', '"emoji":"⚡","translation":"电钻","image":"power-drill-word"') WHERE id = 36;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🪨","translation":"搅拌机"', '"emoji":"🪨","translation":"搅拌机","image":"cement-mixer"') WHERE id = 36;
UPDATE lesson SET content = REPLACE(content, '"emoji":"📌","translation":"射钉枪"', '"emoji":"📌","translation":"射钉枪","image":"nail-gun"') WHERE id = 36;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🔧","translation":"活动扳手"', '"emoji":"🔧","translation":"活动扳手","image":"adjustable-wrench"') WHERE id = 36;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🔨","translation":"大锤"', '"emoji":"🔨","translation":"大锤","image":"sledgehammer"') WHERE id = 36;
UPDATE lesson SET content = REPLACE(content, '"emoji":"🏗️","translation":"链条葫芦"', '"emoji":"🏗️","translation":"链条葫芦","image":"chain-hoist"') WHERE id = 36;
