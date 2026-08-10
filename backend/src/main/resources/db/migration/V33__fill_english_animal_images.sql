-- 补齐英语“动物朋友”课程中的高频动物词汇配图。
-- 图片 key 由 WordImageResolver 统一解析为 /images/<key>.jpg。

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-animal/cow',
    '$.items[1].image', 'english-animal/pig',
    '$.items[2].image', 'english-animal/sheep',
    '$.items[4].image', 'english-animal/horse'
) WHERE id = 144;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-animal/bear',
    '$.items[2].image', 'english-animal/fox',
    '$.items[4].image', 'english-animal/squirrel',
    '$.items[5].image', 'english-animal/owl'
) WHERE id = 149;
