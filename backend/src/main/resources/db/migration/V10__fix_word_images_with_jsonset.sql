-- ============================================================
-- V10: 修复 V9 未生效的 image 字段
-- V9 使用 REPLACE(content, emoji+translation, ...) 方式插入 image,
-- 但 SQLite 存储 emoji 为 Unicode 转义序列(如 \uD83D\uDC15),
-- 导致 REPLACE 匹配失败,image 字段未被添加。
-- 本迁移改用 json_set 精确设置每个 item 的 image 字段。
-- 涉及课时: Lesson 22, 23, 35, 36
-- ============================================================

-- ============================================================
-- Lesson 22: 认识狗狗 (id=22)
-- ============================================================
UPDATE lesson SET content = json_set(content, '$.items[0].image', 'chase') WHERE id = 22;
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'marshall') WHERE id = 22;
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'skye') WHERE id = 22;
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'rubble') WHERE id = 22;
UPDATE lesson SET content = json_set(content, '$.items[4].image', 'zuma') WHERE id = 22;
UPDATE lesson SET content = json_set(content, '$.items[5].image', 'rocky') WHERE id = 22;
UPDATE lesson SET content = json_set(content, '$.items[6].image', 'puppy') WHERE id = 22;
UPDATE lesson SET content = json_set(content, '$.items[7].image', 'team') WHERE id = 22;

-- ============================================================
-- Lesson 23: 狗狗的本领 (id=23)
-- ============================================================
UPDATE lesson SET content = json_set(content, '$.items[0].image', 'police') WHERE id = 23;
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'fire-word') WHERE id = 23;
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'fly') WHERE id = 23;
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'build') WHERE id = 23;
UPDATE lesson SET content = json_set(content, '$.items[4].image', 'swim') WHERE id = 23;
UPDATE lesson SET content = json_set(content, '$.items[5].image', 'recycle') WHERE id = 23;
UPDATE lesson SET content = json_set(content, '$.items[6].image', 'rescue') WHERE id = 23;
UPDATE lesson SET content = json_set(content, '$.items[7].image', 'help') WHERE id = 23;

-- ============================================================
-- Lesson 35: 认识工程狗狗 (id=35)
-- ============================================================
UPDATE lesson SET content = json_set(content, '$.items[0].image', 'dozer') WHERE id = 35;
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'pipes') WHERE id = 35;
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'bolt') WHERE id = 35;
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'mixer') WHERE id = 35;
UPDATE lesson SET content = json_set(content, '$.items[4].image', 'nails') WHERE id = 35;
UPDATE lesson SET content = json_set(content, '$.items[5].image', 'wrench-corgi') WHERE id = 35;
UPDATE lesson SET content = json_set(content, '$.items[6].image', 'hammer-boxer') WHERE id = 35;
UPDATE lesson SET content = json_set(content, '$.items[7].image', 'crank') WHERE id = 35;

-- ============================================================
-- Lesson 36: 狗狗的工具 (id=36)
-- ============================================================
UPDATE lesson SET content = json_set(content, '$.items[0].image', 'dozer-blade') WHERE id = 36;
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'pipe-wrench') WHERE id = 36;
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'power-drill-word') WHERE id = 36;
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'cement-mixer') WHERE id = 36;
UPDATE lesson SET content = json_set(content, '$.items[4].image', 'nail-gun') WHERE id = 36;
UPDATE lesson SET content = json_set(content, '$.items[5].image', 'adjustable-wrench') WHERE id = 36;
UPDATE lesson SET content = json_set(content, '$.items[6].image', 'sledgehammer') WHERE id = 36;
UPDATE lesson SET content = json_set(content, '$.items[7].image', 'chain-hoist') WHERE id = 36;
