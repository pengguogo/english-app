-- 补齐英语课程中高频衣物词汇的配图。
-- 图片 key 由 WordImageResolver 统一解析为 /images/<key>.jpg。

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-clothing/shirt',
    '$.items[1].image', 'english-clothing/t-shirt',
    '$.items[2].image', 'english-clothing/skirt',
    '$.items[3].image', 'english-clothing/pants',
    '$.items[4].image', 'english-clothing/shorts'
) WHERE id = 174;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-clothing/coat',
    '$.items[1].image', 'english-clothing/sweater',
    '$.items[2].image', 'english-clothing/jacket'
) WHERE id = 175;
