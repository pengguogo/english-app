-- 补齐英语课程中高频地点与场景词汇的配图。
-- 图片 key 由 WordImageResolver 统一解析为 /images/<key>.jpg。

UPDATE lesson SET content = json_set(content,
    '$.items[1].image', 'english-place/school',
    '$.items[2].image', 'english-place/bakery',
    '$.items[4].image', 'english-place/farm',
    '$.items[5].image', 'english-place/airport'
) WHERE id = 238;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-place/park',
    '$.items[3].image', 'english-place/zoo',
    '$.items[4].image', 'english-place/market',
    '$.items[5].image', 'english-place/post-office'
) WHERE id = 239;

UPDATE lesson SET content = json_set(content,
    '$.items[1].image', 'english-place/airport'
) WHERE id = 261;
