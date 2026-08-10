-- 补齐英语“农场伙伴”和“动物宝宝”课程中的动物词汇配图。
-- 图片 key 由 WordImageResolver 统一解析为 /images/<key>.jpg。

UPDATE lesson SET content = json_set(content,
    '$.items[3].image', 'english-farm-baby/goat',
    '$.items[5].image', 'english-farm-baby/duck'
) WHERE id = 144;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-farm-baby/calf',
    '$.items[1].image', 'english-farm-baby/lamb',
    '$.items[2].image', 'english-farm-baby/chick',
    '$.items[3].image', 'english-farm-baby/kitten',
    '$.items[4].image', 'english-farm-baby/puppy',
    '$.items[5].image', 'english-farm-baby/foal'
) WHERE id = 145;
