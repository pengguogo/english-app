-- 补齐英语“自然与天气”主题及相关英语课程中的高频自然词汇配图。
-- 图片 key 由 WordImageResolver 统一解析为 /images/<key>.jpg。

UPDATE lesson SET content = json_set(content,
    '$.items[3].image', 'english-nature/leaf'
) WHERE id = 151;

UPDATE lesson SET content = json_set(content,
    '$.items[2].image', 'english-nature/leaf'
) WHERE id = 181;

UPDATE lesson SET content = json_set(content,
    '$.items[5].image', 'english-nature/star'
) WHERE id = 199;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-nature/sun',
    '$.items[1].image', 'english-nature/moon',
    '$.items[2].image', 'english-nature/star',
    '$.items[5].image', 'english-nature/sky'
) WHERE id = 204;

UPDATE lesson SET content = json_set(content,
    '$.items[3].image', 'english-nature/thunder',
    '$.items[5].image', 'english-nature/storm'
) WHERE id = 205;

UPDATE lesson SET content = json_set(content,
    '$.items[3].image', 'english-nature/leaf',
    '$.items[5].image', 'english-nature/root'
) WHERE id = 209;

UPDATE lesson SET content = json_set(content,
    '$.items[1].image', 'english-nature/star',
    '$.items[2].image', 'english-nature/moon'
) WHERE id = 249;
