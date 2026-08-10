-- 补齐英语课程中高频家居词汇的配图。
-- 图片 key 由 WordImageResolver 统一解析为 /images/<key>.jpg。

UPDATE lesson SET content = json_set(content,
    '$.items[3].image', 'home/clock'
) WHERE id = 201 AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'home/sofa',
    '$.items[1].image', 'home/lamp',
    '$.items[2].image', 'home/rug',
    '$.items[3].image', 'home/clock',
    '$.items[4].image', 'home/pillow'
) WHERE id = 224;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'home/bed',
    '$.items[1].image', 'home/blanket',
    '$.items[4].image', 'home/mirror'
) WHERE id = 225;

UPDATE lesson SET content = json_set(content,
    '$.items[2].image', 'home/pillow'
) WHERE id = 260 AND COALESCE(json_extract(content, '$.items[2].image'), '') = '';
