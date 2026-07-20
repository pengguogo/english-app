-- ============================================================
-- V8: 小砾工程队独立主题 + 工程家族狗狗单元
-- 将单元 10(小砾工程队)从主题 3(汪汪队)独立为新主题 4
-- 新增单元 11(工程家族狗狗),包含 3 个课时:
--   Lesson 35: 认识工程狗狗 (WORD, 8 单词)
--   Lesson 36: 狗狗的工具   (WORD, 8 单词)
--   Lesson 37: 一起建房子   (SENTENCE, 5 句子)
-- 说明: 工程狗狗为原创角色,非汪汪队原版,避免版权问题
-- ============================================================

-- 1. 新增主题 4:小砾工程队
INSERT INTO theme (id, name, sort_order, is_locked) VALUES (4, '小砾工程队', 4, 0);

-- 2. 将单元 10 从主题 3 移到主题 4
UPDATE unit SET theme_id = 4 WHERE id = 10;

-- 3. 新增单元 11:工程家族狗狗(主题 4 的第 2 个单元)
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(11, 4, '工程家族狗狗', 2, 1);

-- ============================================================
-- Lesson 35: 认识工程狗狗 (WORD, 8 单词)
-- 原创工程狗狗角色,每只狗有专属工种和安全装备
-- ============================================================
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(35, 11, '认识工程狗狗', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"Dozer","phonetic":"/ˈdoʊzər/","translation":"推土犬(斗牛犬)","emoji":"🚧"},'
|| '{"word":"Pipes","phonetic":"/paɪps/","translation":"管道犬(腊肠犬)","emoji":"🔧"},'
|| '{"word":"Bolt","phonetic":"/boʊlt/","translation":"电钻犬(杰克罗素)","emoji":"⚡"},'
|| '{"word":"Mixer","phonetic":"/ˈmɪksər/","translation":"搅拌犬(比格犬)","emoji":"🪨"},'
|| '{"word":"Nails","phonetic":"/neɪlz/","translation":"钉子犬(雪纳瑞)","emoji":"📌"},'
|| '{"word":"Wrench","phonetic":"/rentʃ/","translation":"扳手犬(柯基)","emoji":"🔧"},'
|| '{"word":"Hammer","phonetic":"/ˈhæmər/","translation":"锤子犬(拳师犬)","emoji":"🔨"},'
|| '{"word":"Crank","phonetic":"/kræŋk/","translation":"起重犬(哈士奇)","emoji":"🏗️"}'
|| ']}', 1, 3);

-- ============================================================
-- Lesson 36: 狗狗的工具 (WORD, 8 单词)
-- 每只工程狗狗的专属工具
-- ============================================================
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(36, 11, '狗狗的工具', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"dozer blade","phonetic":"/ˈdoʊzər bleɪd/","translation":"推土铲","emoji":"🚧"},'
|| '{"word":"pipe wrench","phonetic":"/paɪp rentʃ/","translation":"管钳","emoji":"🔧"},'
|| '{"word":"power drill","phonetic":"/ˈpaʊər drɪl/","translation":"电钻","emoji":"⚡"},'
|| '{"word":"cement mixer","phonetic":"/sɪˈment ˈmɪksər/","translation":"搅拌机","emoji":"🪨"},'
|| '{"word":"nail gun","phonetic":"/neɪl ɡʌn/","translation":"射钉枪","emoji":"📌"},'
|| '{"word":"adjustable wrench","phonetic":"/əˈdʒʌstəbəl rentʃ/","translation":"活动扳手","emoji":"🔧"},'
|| '{"word":"sledgehammer","phonetic":"/ˈsleddʒæmər/","translation":"大锤","emoji":"🔨"},'
|| '{"word":"chain hoist","phonetic":"/tʃeɪn hɔɪst/","translation":"链条葫芦","emoji":"🏗️"}'
|| ']}', 2, 3);

-- ============================================================
-- Lesson 37: 一起建房子 (SENTENCE, 5 句子)
-- 围绕工程狗狗团队合作建房子的场景
-- ============================================================
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(37, 11, '一起建房子', 'SENTENCE',
'{"type":"SENTENCE","items":['
|| '{"sentence":"Dozer pushes the dirt.","translation":"推土犬推泥土。","emoji":"🚧"},'
|| '{"sentence":"Pipes lays the pipes.","translation":"管道犬铺管道。","emoji":"🔧"},'
|| '{"sentence":"Bolt drills the holes.","translation":"电钻犬钻孔。","emoji":"⚡"},'
|| '{"sentence":"Mixer pours the concrete.","translation":"搅拌犬浇混凝土。","emoji":"🪨"},'
|| '{"sentence":"They build a house together!","translation":"他们一起建房子!","emoji":"🏠"}'
|| ']}', 3, 3);