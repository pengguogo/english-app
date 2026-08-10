-- 补齐英语课程中的职业与社区词汇配图。

UPDATE lesson SET content = json_set(content,
    '$.items[2].image', 'english-career/teacher'
) WHERE id = 159;

UPDATE lesson SET content = json_set(content,
    '$.items[2].image', 'english-career/teacher'
) WHERE id = 234;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-career/driver',
    '$.items[1].image', 'english-career/farmer',
    '$.items[2].image', 'english-career/baker',
    '$.items[3].image', 'english-career/artist',
    '$.items[4].image', 'english-career/builder'
) WHERE id = 235;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-career/stethoscope'
) WHERE id = 236;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-career/hospital'
) WHERE id = 238;
