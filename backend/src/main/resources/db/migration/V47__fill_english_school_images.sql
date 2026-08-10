-- 补齐英语“学校用品”主题及相关英语课程中的高频用品词汇配图。
-- 图片 key 由 WordImageResolver 统一解析为 /images/<key>.jpg。

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-school/bag',
    '$.items[1].image', 'english-school/pencil',
    '$.items[2].image', 'english-school/ruler',
    '$.items[3].image', 'english-school/crayon',
    '$.items[4].image', 'english-school/notebook',
    '$.items[5].image', 'english-school/eraser'
) WHERE id = 160;

UPDATE lesson SET content = json_set(content,
    '$.items[2].image', 'english-school/bag'
) WHERE id = 218;

UPDATE lesson SET content = json_set(content,
    '$.items[1].image', 'english-school/chalk'
) WHERE id = 236;

UPDATE lesson SET content = json_set(content,
    '$.items[1].image', 'english-school/backpack'
) WHERE id = 259;
