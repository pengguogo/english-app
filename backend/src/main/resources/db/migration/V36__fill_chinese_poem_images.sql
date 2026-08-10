-- V36：为语文「古诗朗诵」主题的 24 个诗句学习项补充配图。
-- 仅在 image 缺失或为空时写入，避免覆盖后续已有数据。

-- Lesson 74：静夜思
UPDATE lesson SET content = json_set(content, '$.items[0].image', 'chinese-poem/quiet-night-thought-1') WHERE id = 74 AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'chinese-poem/quiet-night-thought-2') WHERE id = 74 AND COALESCE(json_extract(content, '$.items[1].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'chinese-poem/quiet-night-thought-3') WHERE id = 74 AND COALESCE(json_extract(content, '$.items[2].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'chinese-poem/quiet-night-thought-4') WHERE id = 74 AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';

-- Lesson 75：春晓
UPDATE lesson SET content = json_set(content, '$.items[0].image', 'chinese-poem/spring-dawn-1') WHERE id = 75 AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'chinese-poem/spring-dawn-2') WHERE id = 75 AND COALESCE(json_extract(content, '$.items[1].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'chinese-poem/spring-dawn-3') WHERE id = 75 AND COALESCE(json_extract(content, '$.items[2].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'chinese-poem/spring-dawn-4') WHERE id = 75 AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';

-- Lesson 76：咏鹅
UPDATE lesson SET content = json_set(content, '$.items[0].image', 'chinese-poem/ode-to-goose-1') WHERE id = 76 AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'chinese-poem/ode-to-goose-2') WHERE id = 76 AND COALESCE(json_extract(content, '$.items[1].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'chinese-poem/ode-to-goose-3') WHERE id = 76 AND COALESCE(json_extract(content, '$.items[2].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'chinese-poem/ode-to-goose-4') WHERE id = 76 AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';

-- Lesson 77：悯农
UPDATE lesson SET content = json_set(content, '$.items[0].image', 'chinese-poem/farmers-toil-1') WHERE id = 77 AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'chinese-poem/farmers-toil-2') WHERE id = 77 AND COALESCE(json_extract(content, '$.items[1].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'chinese-poem/farmers-toil-3') WHERE id = 77 AND COALESCE(json_extract(content, '$.items[2].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'chinese-poem/farmers-toil-4') WHERE id = 77 AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';

-- Lesson 78：登鹳雀楼
UPDATE lesson SET content = json_set(content, '$.items[0].image', 'chinese-poem/stork-tower-1') WHERE id = 78 AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'chinese-poem/stork-tower-2') WHERE id = 78 AND COALESCE(json_extract(content, '$.items[1].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'chinese-poem/stork-tower-3') WHERE id = 78 AND COALESCE(json_extract(content, '$.items[2].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'chinese-poem/stork-tower-4') WHERE id = 78 AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';

-- Lesson 79：望庐山瀑布
UPDATE lesson SET content = json_set(content, '$.items[0].image', 'chinese-poem/lushan-waterfall-1') WHERE id = 79 AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'chinese-poem/lushan-waterfall-2') WHERE id = 79 AND COALESCE(json_extract(content, '$.items[1].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'chinese-poem/lushan-waterfall-3') WHERE id = 79 AND COALESCE(json_extract(content, '$.items[2].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'chinese-poem/lushan-waterfall-4') WHERE id = 79 AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';
