-- 补齐英语课程中高频家庭成员与朋友词汇的配图。
-- 图片 key 由 WordImageResolver 统一解析为 /images/<key>.jpg。

UPDATE lesson SET content = json_set(content,
    '$.items[1].image', 'english-family/friend'
) WHERE id = 29;

UPDATE lesson SET content = json_set(content,
    '$.items[4].image', 'english-family/friend'
) WHERE id = 30;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-family/mother',
    '$.items[1].image', 'english-family/father',
    '$.items[2].image', 'english-family/sister',
    '$.items[3].image', 'english-family/brother',
    '$.items[4].image', 'english-family/grandma',
    '$.items[5].image', 'english-family/grandpa'
) WHERE id = 154;

UPDATE lesson SET content = json_set(content,
    '$.items[5].image', 'english-family/family'
) WHERE id = 155;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-family/friend'
) WHERE id = 159;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-family/family',
    '$.items[1].image', 'english-family/friend'
) WHERE id = 257;
