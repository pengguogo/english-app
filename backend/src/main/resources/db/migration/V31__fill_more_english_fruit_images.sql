-- 继续补齐英语课程中的水果词汇配图。

UPDATE lesson SET content = json_set(content,
    '$.items[3].image', 'fruit/pear',
    '$.items[5].image', 'fruit/peach',
    '$.items[6].image', 'fruit/lemon',
    '$.items[7].image', 'fruit/cherry'
) WHERE id = 12;

UPDATE lesson SET content = json_set(content,
    '$.items[2].image', 'fruit/blueberry',
    '$.items[5].image', 'fruit/kiwi'
) WHERE id = 13;

UPDATE lesson SET content = json_set(content,
    '$.items[5].image', 'fruit/blueberry',
    '$.items[6].image', 'fruit/cherry'
) WHERE id = 16;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'fruit/blueberry'
) WHERE id = 110;

UPDATE lesson SET content = json_set(content,
    '$.items[3].image', 'fruit/papaya',
    '$.items[5].image', 'fruit/lychee'
) WHERE id = 111;
