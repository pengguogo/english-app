-- 补齐英语课程中高频食物与餐桌词汇的配图。
-- 图片 key 由 WordImageResolver 统一解析为 /images/<key>.jpg。

UPDATE lesson SET content = json_set(content,
    '$.items[1].image', 'english-food/milk'
) WHERE id = 148 AND COALESCE(json_extract(content, '$.items[1].image'), '') = '';

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-food/milk',
    '$.items[1].image', 'english-food/egg',
    '$.items[2].image', 'english-food/bread'
) WHERE id = 171;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-food/bread'
) WHERE id = 184 AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-food/juice',
    '$.items[1].image', 'english-food/milk'
) WHERE id = 185;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-food/egg',
    '$.items[2].image', 'english-food/cheese'
) WHERE id = 186;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-food/plate',
    '$.items[1].image', 'english-food/bowl',
    '$.items[3].image', 'english-food/spoon'
) WHERE id = 189;

UPDATE lesson SET content = json_set(content,
    '$.items[3].image', 'english-food/juice'
) WHERE id = 256 AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';
