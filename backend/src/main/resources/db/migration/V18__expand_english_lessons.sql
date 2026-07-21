-- ============================================================
-- V18: 英语课时扩展（36→60课）
-- 1. UPDATE 汪汪队 Lesson 22 补充角色 image
-- 2. UPDATE 小砾工程 Lesson 35-37 替换为官方角色
-- 3. INSERT 24 节新课（ID 110-130）
-- ============================================================

-- Part 1: 汪汪队 Lesson 22 补充 image 字段
-- 使用 json_set 精确更新，避免 REPLACE 的 emoji Unicode 问题
UPDATE lesson SET content = json_set(content, '$.items[0].image', 'paw-patrol/chase') WHERE id = 22;
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'paw-patrol/marshall') WHERE id = 22;
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'paw-patrol/skye') WHERE id = 22;
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'paw-patrol/rubble') WHERE id = 22;
UPDATE lesson SET content = json_set(content, '$.items[4].image', 'paw-patrol/zuma') WHERE id = 22;
UPDATE lesson SET content = json_set(content, '$.items[5].image', 'paw-patrol/rocky') WHERE id = 22;

-- Part 2: 小砾工程 Lesson 35-37 替换为官方角色
-- Lesson 35: 认识工程狗狗 - 替换全部 8 个单词为官方角色
UPDATE lesson SET content = '{"type":"WORD","items":[{"word":"Rubble","phonetic":"/ˈrʌbəl/","translation":"小砾(队长)","emoji":"🐶","image":"rubble-crew/rubble"},{"word":"Mix","phonetic":"/mɪks/","translation":"米克斯(材料)","emoji":"🎨","image":"rubble-crew/mix"},{"word":"Wheeler","phonetic":"/ˈwiːlər/","translation":"惠勒(清洁)","emoji":"🧹","image":"rubble-crew/wheeler"},{"word":"Charger","phonetic":"/ˈtʃɑːrdʒər/","translation":"充电器(能量)","emoji":"⚡","image":"rubble-crew/charger"},{"word":"Motor","phonetic":"/ˈmoʊtər/","translation":"摩托(拆除)","emoji":"💥","image":"rubble-crew/motor"},{"word":"Crane","phonetic":"/kreɪn/","translation":"鹤姑姑(起重)","emoji":"🏗️","image":"rubble-crew/auntie-crane"},{"word":"Speed Meister","phonetic":"/spiːd maɪstər/","translation":"极速大师(反派)","emoji":"🏎️","image":"rubble-crew/speed-meister"},{"word":"Grandpa","phonetic":"/ˈɡrænpɑː/","translation":"格雷维尔爷爷","emoji":"👴","image":"rubble-crew/grandpa-gravel"}]}' WHERE id = 35;

-- Lesson 36: 狗狗的工具 - 替换为工程家族工具
UPDATE lesson SET content = '{"type":"WORD","items":[{"word":"bulldozer","phonetic":"/ˈbʊldoʊzər/","translation":"推土机","emoji":"🚧"},{"word":"crane","phonetic":"/kreɪn/","translation":"起重机","emoji":"🏗️"},{"word":"cement mixer","phonetic":"/sɪˈment ˈmɪksər/","translation":"搅拌机","emoji":"🪨"},{"word":"cleaner","phonetic":"/ˈkliːnər/","translation":"清洁车","emoji":"🧹"},{"word":"forklift","phonetic":"/ˈfɔːrklɪft/","translation":"叉车","emoji":"📦"},{"word":"demolition truck","phonetic":"/ˌdeməˈlɪʃən trʌk/","translation":"拆除车","emoji":"💥"}]}' WHERE id = 36;

-- Lesson 37: 一起建房子 - 替换为工程家族场景
UPDATE lesson SET content = '{"type":"SENTENCE","items":[{"sentence":"Rubble drives the bulldozer.","translation":"小砾开推土机。","emoji":"🚧"},{"sentence":"Mix makes the cement.","translation":"米克斯做水泥。","emoji":"🎨"},{"sentence":"Crane lifts the walls.","translation":"鹤姑姑吊墙板。","emoji":"🏗️"},{"sentence":"Wheeler cleans the site.","translation":"惠勒清理工地。","emoji":"🧹"},{"sentence":"They build a house together!","translation":"他们一起建房子！","emoji":"🏠"}]}' WHERE id = 37;

-- ========== Part 3: INSERT 24 节新课 ==========

-- === 水果乐园 (Unit 1-3) 新增 6 课 ===
-- Lesson 110: 更多浆果 (WORD)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(110, 1, '更多浆果', 'WORD', '{"type":"WORD","items":[{"word":"blueberry","phonetic":"/ˈbluːberi/","translation":"蓝莓","emoji":"🫐"},{"word":"strawberry","phonetic":"/ˈstrɔːberi/","translation":"草莓","emoji":"🍓"},{"word":"raspberry","phonetic":"/ˈræzberi/","translation":"树莓","emoji":"🟥"},{"word":"blackberry","phonetic":"/ˈblækberi/","translation":"黑莓","emoji":"⚫"},{"word":"cranberry","phonetic":"/ˈkrænberi/","translation":"蔓越莓","emoji":"🔴"},{"word":"gooseberry","phonetic":"/ˈɡʊsberi/","translation":"鹅莓","emoji":"🟢"}]}', 5, 3);

-- Lesson 111: 热带水果 (WORD)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(111, 2, '热带水果', 'WORD', '{"type":"WORD","items":[{"word":"mango","phonetic":"/ˈmæŋɡoʊ/","translation":"芒果","emoji":"🥭"},{"word":"pineapple","phonetic":"/ˈpaɪnæpəl/","translation":"菠萝","emoji":"🍍"},{"word":"coconut","phonetic":"/ˈkoʊkənʌt/","translation":"椰子","emoji":"🥥"},{"word":"papaya","phonetic":"/pəˈpaɪə/","translation":"木瓜","emoji":"🟠"},{"word":"dragon fruit","phonetic":"/ˈdræɡən fruːt/","translation":"火龙果","emoji":"🐉"},{"word":"lychee","phonetic":"/ˈlaɪtʃi/","translation":"荔枝","emoji":"⚪"}]}', 4, 3);

-- Lesson 112: 表达喜欢 (SENTENCE)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(112, 3, '表达喜欢', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"I like apples.","translation":"我喜欢苹果。","emoji":"🍎"},{"sentence":"She loves bananas.","translation":"她爱香蕉。","emoji":"🍌"},{"sentence":"He wants a mango.","translation":"他想要芒果。","emoji":"🥭"},{"sentence":"We eat grapes together.","translation":"我们一起吃葡萄。","emoji":"🍇"},{"sentence":"They enjoy pineapple.","translation":"他们喜欢菠萝。","emoji":"🍍"}]}', 4, 3);

-- Lesson 113: 字母 F 发音 (PHONICS)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(113, 1, '字母F发音', 'PHONICS', '{"type":"PHONICS","letter":"F","pronunciation":"/ef/","sound":"/f/","tip":"F 开头单词常发 /f/ 音，上牙咬下唇","items":[{"word":"fruit","phonetic":"/fruːt/","translation":"水果","emoji":"🍎","image":""},{"word":"flower","phonetic":"/ˈflaʊər/","translation":"花","emoji":"🌸","image":""},{"word":"fish","phonetic":"/fɪʃ/","translation":"鱼","emoji":"🐟","image":""}]}', 6, 3);

-- Lesson 114: 水果店对话 (DIALOGUE)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(114, 2, '水果店对话', 'DIALOGUE', '{"type":"DIALOGUE","scene":"水果店","tip":"学习购买水果的简单对话","items":[{"speaker":"Mimi","text":"Hello! I want an apple.","translation":"你好！我想要一个苹果。","audio":""},{"speaker":"Seller","text":"Here you are. Do you like bananas?","translation":"给你。你喜欢香蕉吗？","audio":""},{"speaker":"Mimi","text":"Yes, I like bananas. Thank you!","translation":"是的，我喜欢香蕉。谢谢！","audio":""}]}', 5, 3);

-- === 交通工具 (Unit 4-6) 新增 6 课 ===
-- Lesson 115: 特种车辆 (WORD)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(115, 4, '特种车辆', 'WORD', '{"type":"WORD","items":[{"word":"ambulance","phonetic":"/ˈæmbjələns/","translation":"救护车","emoji":"🚑"},{"word":"fire truck","phonetic":"/ˈfaɪər trʌk/","translation":"消防车","emoji":"🚒"},{"word":"police car","phonetic":"/pəˈliːs kɑːr/","translation":"警车","emoji":"🚓"},{"word":"garbage truck","phonetic":"/ˈɡɑːrbɪdʒ trʌk/","translation":"垃圾车","emoji":"🗑️"},{"word":"tow truck","phonetic":"/toʊ trʌk/","translation":"拖车","emoji":"🚛"},{"word":"taxi","phonetic":"/ˈtæksi/","translation":"出租车","emoji":"🚕"}]}', 5, 3);

-- Lesson 116: 出行方式 (SENTENCE)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(116, 5, '出行方式', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"I go by bus.","translation":"我坐公交去。","emoji":"🚌"},{"sentence":"She goes by train.","translation":"她坐火车去。","emoji":"🚂"},{"sentence":"He flies by plane.","translation":"他坐飞机去。","emoji":"✈️"},{"sentence":"We ride bikes.","translation":"我们骑车。","emoji":"🚲"},{"sentence":"They take a taxi.","translation":"他们打车。","emoji":"🚕"}]}', 4, 3);

-- Lesson 117: 字母 C 发音 (PHONICS)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(117, 4, '字母C发音', 'PHONICS', '{"type":"PHONICS","letter":"C","pronunciation":"/siː/","sound":"/k/","tip":"C 开头单词常发 /k/ 音","items":[{"word":"car","phonetic":"/kɑːr/","translation":"小汽车","emoji":"🚗","image":""},{"word":"bus","phonetic":"/bʌs/","translation":"公交车","emoji":"🚌","image":""},{"word":"cycle","phonetic":"/ˈsaɪkəl/","translation":"骑行","emoji":"🚲","image":""}]}', 6, 3);

-- Lesson 118: 车站问路 (DIALOGUE)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(118, 5, '车站问路', 'DIALOGUE', '{"type":"DIALOGUE","scene":"火车站","tip":"学习在车站问路的对话","items":[{"speaker":"Mimi","text":"Where is the train?","translation":"火车在哪里？","audio":""},{"speaker":"Conductor","text":"The train is at platform 2.","translation":"火车在2号站台。","audio":""},{"speaker":"Mimi","text":"Thank you!","translation":"谢谢！","audio":""}]}', 5, 3);

-- === 汪汪队 (Unit 7-9) 新增 6 课 ===
-- Lesson 119: 救援口令 (SENTENCE)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(119, 7, '救援口令', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"Ryder needs us!","translation":"莱德需要我们！","emoji":"👦"},{"sentence":"Chase is on the case!","translation":"阿奇出动！","emoji":"🐕"},{"sentence":"Ready for action!","translation":"准备行动！","emoji":"⚡"},{"sentence":"No job is too big, no pup is too small!","translation":"没有完不成的任务，没有太小的小狗！","emoji":"🐶"},{"sentence":"Paw Patrol is on a roll!","translation":"汪汪队出发！","emoji":"🚀"}]}', 5, 3);

-- Lesson 120: 帮助别人 (SENTENCE)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(120, 8, '帮助别人', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"I can help you.","translation":"我能帮助你。","emoji":"🤝"},{"sentence":"Marshall saves the day.","translation":"毛毛拯救了大家。","emoji":"🔥"},{"sentence":"Skye flies to the rescue.","translation":"天天飞去救援。","emoji":"🚁"},{"sentence":"The team works together.","translation":"队伍齐心协力。","emoji":"🦮"},{"sentence":"Everyone is happy.","translation":"大家都很开心。","emoji":"😊"}]}', 4, 3);

-- Lesson 121: 角色辨认 (QUIZ)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(121, 9, '角色辨认', 'QUIZ', '{"type":"QUIZ","items":[{"question":"Who is the fire dog?","options":["Marshall","Chase","Skye"],"answer":0,"image":""},{"question":"Who can fly?","options":["Rubble","Skye","Zuma"],"answer":1,"image":""},{"question":"Who is the police dog?","options":["Rocky","Zuma","Chase"],"answer":2,"image":""},{"question":"Who loves recycling?","options":["Rocky","Marshall","Rubble"],"answer":0,"image":""}]}', 4, 3);

-- Lesson 122: 字母 M 发音 (PHONICS)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(122, 7, '字母M发音', 'PHONICS', '{"type":"PHONICS","letter":"M","pronunciation":"/em/","sound":"/m/","tip":"M 开头单词常发 /m/ 音，双唇紧闭","items":[{"word":"Marshall","phonetic":"/ˈmɑːrʃəl/","translation":"毛毛","emoji":"🔥","image":"paw-patrol/marshall"},{"word":"Mayor","phonetic":"/ˈmeɪər/","translation":"市长","emoji":"👨‍💼","image":"paw-patrol/mayor-humdinger"},{"word":"mountain","phonetic":"/ˈmaʊntən/","translation":"山","emoji":"⛰️","image":""}]}', 6, 3);

-- Lesson 123: 救援对话 (DIALOGUE)
-- 注意：修复了原计划中 translation 字段的中文冒号 typo（统一使用英文冒号）
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(123, 8, '救援对话', 'DIALOGUE', '{"type":"DIALOGUE","scene":"冒险湾","tip":"学习救援场景的对话","items":[{"speaker":"Ryder","text":"Paw Patrol, to the Lookout!","translation":"汪汪队，集合！","audio":""},{"speaker":"Chase","text":"Chase is on the case!","translation":"阿奇出动！","audio":""},{"speaker":"Mimi","text":"Wow, they are so brave!","translation":"哇，他们好勇敢！","audio":""}]}', 5, 3);

-- Lesson 124: 团队故事 (READING)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(124, 9, '团队故事', 'READING', '{"type":"READING","items":[{"title":"The Great Rescue","content":"One day in Adventure Bay, a kitten was stuck on a tree. Ryder called the Paw Patrol. Chase came first. Marshall brought the ladder. Skye flew up to the kitten. Together they saved the kitten. Everyone cheered!","image":""}]}', 5, 3);

-- === 小砾工程 (Unit 10-11) 新增 6 课 ===
-- Lesson 125: 家族成员登场 (WORD)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(125, 11, '家族成员登场', 'WORD', '{"type":"WORD","items":[{"word":"Rubble","phonetic":"/ˈrʌbəl/","translation":"小砾(队长)","emoji":"🐶","image":"rubble-crew/rubble"},{"word":"Mix","phonetic":"/mɪks/","translation":"米克斯(材料)","emoji":"🎨","image":"rubble-crew/mix"},{"word":"Wheeler","phonetic":"/ˈwiːlər/","translation":"惠勒(清洁)","emoji":"🧹","image":"rubble-crew/wheeler"},{"word":"Charger","phonetic":"/ˈtʃɑːrdʒər/","translation":"充电器(能量)","emoji":"⚡","image":"rubble-crew/charger"},{"word":"Motor","phonetic":"/ˈmoʊtər/","translation":"摩托(拆除)","emoji":"💥","image":"rubble-crew/motor"},{"word":"Crane","phonetic":"/kreɪn/","translation":"鹤姑姑(起重)","emoji":"🏗️","image":"rubble-crew/auntie-crane"}]}', 4, 3);

-- Lesson 126: 家族的车辆 (WORD)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(126, 10, '家族的车辆', 'WORD', '{"type":"WORD","items":[{"word":"bulldozer","phonetic":"/ˈbʊldoʊzər/","translation":"推土机","emoji":"🚧"},{"word":"crane","phonetic":"/kreɪn/","translation":"起重机","emoji":"🏗️"},{"word":"mixer","phonetic":"/ˈmɪksər/","translation":"搅拌机","emoji":"🪨"},{"word":"cleaner","phonetic":"/ˈkliːnər/","translation":"清洁车","emoji":"🧹"},{"word":"forklift","phonetic":"/ˈfɔːrklɪft/","translation":"叉车","emoji":"📦"},{"word":"demolition truck","phonetic":"/ˌdeməˈlɪʃən trʌk/","translation":"拆除车","emoji":"💥"}]}', 4, 3);

-- Lesson 127: 成员找车辆 (QUIZ)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(127, 11, '成员找车辆', 'QUIZ', '{"type":"QUIZ","items":[{"question":"Who drives the crane?","options":["Mix","Crane","Motor"],"answer":1,"image":""},{"question":"Who drives the bulldozer?","options":["Rubble","Wheeler","Charger"],"answer":0,"image":""},{"question":"Who cleans the site?","options":["Motor","Wheeler","Crane"],"answer":1,"image":""}]}', 5, 3);

-- Lesson 128: 字母 W 发音 (PHONICS)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(128, 10, '字母W发音', 'PHONICS', '{"type":"PHONICS","letter":"W","pronunciation":"/ˈdʌbəljuː/","sound":"/w/","tip":"W 开头单词常发 /w/ 音，嘴唇收圆","items":[{"word":"Wheeler","phonetic":"/ˈwiːlər/","translation":"惠勒","emoji":"🧹","image":"rubble-crew/wheeler"},{"word":"wheel","phonetic":"/wiːl/","translation":"轮子","emoji":"⚙️","image":""},{"word":"water","phonetic":"/ˈwɔːtər/","translation":"水","emoji":"💧","image":""}]}', 5, 3);

-- Lesson 129: 工地合作 (DIALOGUE)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(129, 11, '工地合作', 'DIALOGUE', '{"type":"DIALOGUE","scene":"建造者湾工地","tip":"学习工地合作的对话","items":[{"speaker":"Rubble","text":"Let us build a bridge!","translation":"我们一起来建桥！","audio":""},{"speaker":"Mix","text":"I will make the cement.","translation":"我来做水泥。","audio":""},{"speaker":"Speed Meister","text":"I will build it faster!","translation":"我会建得更快！","audio":""},{"speaker":"Mimi","text":"Wow, great teamwork!","translation":"哇，真棒的团队合作！","audio":""}]}', 6, 3);

-- Lesson 130: 建造者湾大工程 (READING)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(130, 10, '建造者湾大工程', 'READING', '{"type":"READING","items":[{"title":"Building a School","content":"In Builder Cove, Rubble and his family built a new school. Mix made strong cement. Crane lifted the heavy walls. Wheeler kept everything clean. Charger had lots of energy. Motor helped demolish the old building. Even Speed Meister tried to help! The school was beautiful. Everyone was happy!","image":""}]}', 6, 3);
