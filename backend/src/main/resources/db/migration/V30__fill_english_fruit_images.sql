-- 补齐英语课程中高频水果词汇的配图。
-- 图片 key 由 WordImageResolver 统一解析为 /images/<key>.jpg。

UPDATE lesson SET content = json_set(content,
    '$.items[1].image', 'fruit/banana',
    '$.items[2].image', 'fruit/orange',
    '$.items[4].image', 'fruit/grape'
) WHERE id = 12;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'fruit/watermelon',
    '$.items[3].image', 'fruit/pineapple',
    '$.items[4].image', 'fruit/mango',
    '$.items[6].image', 'fruit/coconut',
    '$.items[7].image', 'fruit/avocado'
) WHERE id = 13;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'fruit/watermelon',
    '$.items[2].image', 'fruit/coconut',
    '$.items[3].image', 'fruit/avocado'
) WHERE id = 16;

UPDATE lesson SET content = json_set(content,
    '$.items[1].image', 'fruit/banana',
    '$.items[2].image', 'fruit/orange',
    '$.items[3].image', 'fruit/grape',
    '$.items[4].image', 'fruit/watermelon',
    '$.items[6].image', 'fruit/mango',
    '$.items[7].image', 'fruit/pineapple'
) WHERE id = 19;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'fruit/mango',
    '$.items[1].image', 'fruit/pineapple',
    '$.items[2].image', 'fruit/coconut'
) WHERE id = 111;

UPDATE lesson SET content = json_set(content,
    '$.items[3].image', 'fruit/banana',
    '$.items[5].image', 'fruit/orange'
) WHERE id = 185;

UPDATE lesson SET content = json_set(content,
    '$.items[1].image', 'fruit/orange'
) WHERE id = 194;
