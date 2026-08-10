-- 补齐英语课程中的形状与图案词汇配图。

UPDATE lesson SET content = json_set(content,
    '$.items[3].image', 'english-shape/rectangle',
    '$.items[4].image', 'english-shape/oval'
) WHERE id = 199;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-shape/cube',
    '$.items[2].image', 'english-shape/cone',
    '$.items[3].image', 'english-shape/cylinder'
) WHERE id = 200;

UPDATE lesson SET content = json_set(content,
    '$.items[0].image', 'english-shape/stripe',
    '$.items[3].image', 'english-shape/diamond',
    '$.items[4].image', 'english-shape/spiral'
) WHERE id = 203;
