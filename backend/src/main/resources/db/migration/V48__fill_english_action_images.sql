-- 补齐英语课程中高频日常动作词汇的配图。
-- 图片 key 由 WordImageResolver 统一解析为 /images/<key>.jpg。

UPDATE lesson SET content = json_set(content,
    '$.items[5].image', 'english-action/clean'
) WHERE id = 148 AND COALESCE(json_extract(content, '$.items[5].image'), '') = '';

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-action/run'
) WHERE id = 153 AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';

UPDATE lesson SET content = json_set(content,
    '$.items[1].image', 'english-action/read',
    '$.items[4].image', 'english-action/share'
) WHERE id = 156;

UPDATE lesson SET content = json_set(content,
    '$.items[1].image', 'english-action/help'
) WHERE id = 158 AND COALESCE(json_extract(content, '$.items[1].image'), '') = '';

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-action/wash',
    '$.items[1].image', 'english-action/brush'
) WHERE id = 169;

UPDATE lesson SET content = json_set(content,
    '$.items[3].image', 'english-action/clean'
) WHERE id = 170 AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';

UPDATE lesson SET content = json_set(content,
    '$.items[4].image', 'english-action/wash'
) WHERE id = 177 AND COALESCE(json_extract(content, '$.items[4].image'), '') = '';

UPDATE lesson SET content = json_set(content,
    '$.items[3].image', 'english-action/wash'
) WHERE id = 192 AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';

UPDATE lesson SET content = json_set(content,
    '$.items[1].image', 'english-action/wait',
    '$.items[2].image', 'english-action/share'
) WHERE id = 193;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-action/run'
) WHERE id = 219 AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';

UPDATE lesson SET content = json_set(content,
    '$.items[5].image', 'english-action/share'
) WHERE id = 220 AND COALESCE(json_extract(content, '$.items[5].image'), '') = '';

UPDATE lesson SET content = json_set(content,
    '$.items[3].image', 'english-action/wait'
) WHERE id = 223 AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';

UPDATE lesson SET content = json_set(content,
    '$.items[5].image', 'english-action/read'
) WHERE id = 226 AND COALESCE(json_extract(content, '$.items[5].image'), '') = '';

UPDATE lesson SET content = json_set(content,
    '$.items[5].image', 'english-action/brush'
) WHERE id = 231 AND COALESCE(json_extract(content, '$.items[5].image'), '') = '';

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-action/help'
) WHERE id = 237 AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-action/clean'
) WHERE id = 248 AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';

UPDATE lesson SET content = json_set(content,
    '$.items[2].image', 'english-action/wait'
) WHERE id = 262 AND COALESCE(json_extract(content, '$.items[2].image'), '') = '';
