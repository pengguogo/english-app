-- 补齐英语“身体与健康”主题中的高频身体词汇配图。
-- 图片 key 由 WordImageResolver 统一解析为 /images/<key>.jpg。

UPDATE lesson SET content = json_set(content,
    '$.items[3].image', 'english-body/ear'
) WHERE id = 152;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-body/head',
    '$.items[2].image', 'english-body/eye',
    '$.items[3].image', 'english-body/ear',
    '$.items[4].image', 'english-body/nose',
    '$.items[5].image', 'english-body/mouth'
) WHERE id = 164;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-body/hand',
    '$.items[3].image', 'english-body/leg',
    '$.items[5].image', 'english-body/foot'
) WHERE id = 165;
