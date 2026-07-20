-- ============================================================
-- V7: 小砾工程队单元 (Rubble's Crew)
-- 挂载在主题 3(汪汪队立大功)下,作为第 4 个单元
-- 单元规划:
--   Lesson 32: 认识工具 (WORD, 8 单词,带 image 字段)
--   Lesson 33: 建筑材料 (WORD, 8 单词,带 image 字段)
--   Lesson 34: 小砾在建造 (SENTENCE, 5 句子)
-- 说明: WORD 课时的 items 增加 image 字段,
--       值为图片文件名(不含扩展名),前端通过映射表获取实际图片
-- ============================================================

-- 1. 新增单元:小砾工程队(主题 3 的第 4 个单元)
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(10, 3, '小砾工程队', 4, 0);

-- ============================================================
-- Lesson 32: 认识工具 (WORD, 8 单词,带 AI 生成图片)
-- ============================================================
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(32, 10, '认识工具', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"excavator","phonetic":"/ˈekskeɪveɪtər/","translation":"挖掘机","emoji":"🚜","image":"excavator"},'
|| '{"word":"bulldozer","phonetic":"/ˈbʊldoʊzər/","translation":"推土机","emoji":"🚧","image":"bulldozer"},'
|| '{"word":"crane","phonetic":"/kreɪn/","translation":"起重机","emoji":"🏗️","image":"crane"},'
|| '{"word":"cement","phonetic":"/sɪˈment/","translation":"水泥","emoji":"🏗️","image":"cement"},'
|| '{"word":"hammer","phonetic":"/ˈhæmər/","translation":"锤子","emoji":"🔨","image":"hammer"},'
|| '{"word":"wrench","phonetic":"/rentʃ/","translation":"扳手","emoji":"🔧","image":"wrench"},'
|| '{"word":"drill","phonetic":"/drɪl/","translation":"电钻","emoji":"🔩","image":"drill"},'
|| '{"word":"hard hat","phonetic":"/hɑːrd hæt/","translation":"安全帽","emoji":"⛑️","image":"hard-hat"}'
|| ']}', 1, 3);

-- ============================================================
-- Lesson 33: 建筑材料 (WORD, 8 单词,带 AI 生成图片)
-- ============================================================
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(33, 10, '建筑材料', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"brick","phonetic":"/brɪk/","translation":"砖","emoji":"🧱","image":"brick"},'
|| '{"word":"steel","phonetic":"/stiːl/","translation":"钢材","emoji":"🔩","image":"steel"},'
|| '{"word":"wood","phonetic":"/wʊd/","translation":"木头","emoji":"🪵","image":"wood"},'
|| '{"word":"concrete","phonetic":"/ˈkɑːnkriːt/","translation":"混凝土","emoji":"🪨","image":"concrete"},'
|| '{"word":"pipe","phonetic":"/paɪp/","translation":"管道","emoji":"🔧","image":"pipe"},'
|| '{"word":"nail","phonetic":"/neɪl/","translation":"钉子","emoji":"📌","image":"nail"},'
|| '{"word":"screw","phonetic":"/skruː/","translation":"螺丝","emoji":"🔩","image":"screw"},'
|| '{"word":"paint","phonetic":"/peɪnt/","translation":"油漆","emoji":"🎨","image":"paint"}'
|| ']}', 2, 3);

-- ============================================================
-- Lesson 34: 小砾在建造 (SENTENCE, 5 句子)
-- ============================================================
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(34, 10, '小砾在建造', 'SENTENCE',
'{"type":"SENTENCE","items":['
|| '{"sentence":"Rubble uses the excavator.","translation":"小砾用挖掘机。","emoji":"🚜"},'
|| '{"sentence":"The crane lifts the bricks.","translation":"起重机吊起砖块。","emoji":"🧱"},'
|| '{"sentence":"I wear a hard hat.","translation":"我戴着安全帽。","emoji":"⛑️"},'
|| '{"sentence":"Rubble builds a house.","translation":"小砾建一座房子。","emoji":"🏠"},'
|| '{"sentence":"The construction is fun!","translation":"建造真有趣!","emoji":"🎉"}'
|| ']}', 3, 3);