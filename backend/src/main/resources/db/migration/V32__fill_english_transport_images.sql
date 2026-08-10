-- 补齐英语课程中高频交通工具词汇的配图。
-- 图片 key 由 WordImageResolver 统一解析为 /images/<key>.jpg。

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'transport/car',
    '$.items[1].image', 'transport/bus',
    '$.items[2].image', 'transport/bike',
    '$.items[3].image', 'transport/truck'
) WHERE id = 2;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'transport/train'
) WHERE id = 3;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'transport/car',
    '$.items[1].image', 'transport/bus',
    '$.items[2].image', 'transport/train',
    '$.items[3].image', 'transport/bike',
    '$.items[4].image', 'transport/truck'
) WHERE id = 4;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'transport/car',
    '$.items[1].image', 'transport/bus',
    '$.items[2].image', 'transport/train',
    '$.items[3].image', 'transport/bike',
    '$.items[4].image', 'transport/truck'
) WHERE id = 5;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'transport/airplane'
) WHERE id = 6;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'transport/airplane'
) WHERE id = 7;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'transport/airplane'
) WHERE id = 8;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'transport/boat',
    '$.items[1].image', 'transport/ship'
) WHERE id = 9;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'transport/boat',
    '$.items[1].image', 'transport/ship'
) WHERE id = 10;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'transport/car',
    '$.items[1].image', 'transport/airplane',
    '$.items[2].image', 'transport/boat',
    '$.items[3].image', 'transport/train'
) WHERE id = 11;

UPDATE lesson SET content = json_set(content,
    '$.items[1].image', 'transport/truck',
    '$.items[2].image', 'transport/boat'
) WHERE id = 26;

UPDATE lesson SET content = json_set(content,
    '$.items[2].image', 'transport/truck'
) WHERE id = 27;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'transport/bus',
    '$.items[1].image', 'transport/train',
    '$.items[3].image', 'transport/bike'
) WHERE id = 116;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'transport/car',
    '$.items[1].image', 'transport/bus'
) WHERE id = 117;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'transport/train',
    '$.items[1].image', 'transport/car',
    '$.items[3].image', 'transport/boat'
) WHERE id = 215;

UPDATE lesson SET content = json_set(content,
    '$.items[3].image', 'transport/boat'
) WHERE id = 246;
