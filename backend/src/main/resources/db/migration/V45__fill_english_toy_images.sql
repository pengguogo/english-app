-- 补齐英语课程中高频玩具与游戏词汇的配图。
-- 图片 key 由 WordImageResolver 统一解析为 /images/<key>.jpg。

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-toy/teddy',
    '$.items[1].image', 'english-toy/doll',
    '$.items[2].image', 'english-toy/robot',
    '$.items[3].image', 'english-toy/puppet',
    '$.items[4].image', 'english-toy/block',
    '$.items[5].image', 'english-toy/puzzle'
) WHERE id = 214;

UPDATE lesson SET content = json_set(content,
    '$.items[2].image', 'english-toy/frisbee'
) WHERE id = 216 AND COALESCE(json_extract(content, '$.items[2].image'), '') = '';

UPDATE lesson SET content = json_set(content,
    '$.items[5].image', 'english-toy/microphone'
) WHERE id = 217 AND COALESCE(json_extract(content, '$.items[5].image'), '') = '';

UPDATE lesson SET content = json_set(content,
    '$.items[5].image', 'english-toy/robot'
) WHERE id = 250 AND COALESCE(json_extract(content, '$.items[5].image'), '') = '';
