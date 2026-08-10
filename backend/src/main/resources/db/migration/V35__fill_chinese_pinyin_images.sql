-- 为语文「拼音启蒙」主题补齐课程配图。
-- 已有非空 image 保持不变，仅补充缺失或空字符串。

UPDATE lesson
SET content = json_set(content,
    '$.items[0].image', COALESCE(NULLIF(json_extract(content, '$.items[0].image'), ''), 'chinese-pinyin/child-speaking-a'),
    '$.items[1].image', COALESCE(NULLIF(json_extract(content, '$.items[1].image'), ''), 'chinese-pinyin/child-speaking-a'),
    '$.items[2].image', COALESCE(NULLIF(json_extract(content, '$.items[2].image'), ''), 'chinese-pinyin/child-speaking-a'),
    '$.items[3].image', COALESCE(NULLIF(json_extract(content, '$.items[3].image'), ''), 'chinese-pinyin/child-speaking-a'),
    '$.items[4].image', COALESCE(NULLIF(json_extract(content, '$.items[4].image'), ''), 'chinese-pinyin/rooster-crowing'),
    '$.items[5].image', COALESCE(NULLIF(json_extract(content, '$.items[5].image'), ''), 'chinese-pinyin/white-goose'))
WHERE id = 62 AND type = 'WORD';

UPDATE lesson
SET content = json_set(content,
    '$.items[0].image', COALESCE(NULLIF(json_extract(content, '$.items[0].image'), ''), 'chinese-pinyin/shirt'),
    '$.items[1].image', COALESCE(NULLIF(json_extract(content, '$.items[1].image'), ''), 'chinese-pinyin/black-crow'),
    '$.items[2].image', COALESCE(NULLIF(json_extract(content, '$.items[2].image'), ''), 'chinese-pinyin/colorful-fish'),
    '$.items[3].image', COALESCE(NULLIF(json_extract(content, '$.items[3].image'), ''), 'chinese-pinyin/polite-bow'),
    '$.items[4].image', COALESCE(NULLIF(json_extract(content, '$.items[4].image'), ''), 'chinese-pinyin/empty-hands'),
    '$.items[5].image', COALESCE(NULLIF(json_extract(content, '$.items[5].image'), ''), 'chinese-pinyin/colorful-fish'))
WHERE id = 63 AND type = 'WORD';

UPDATE lesson
SET content = json_set(content,
    '$.items[0].image', COALESCE(NULLIF(json_extract(content, '$.items[0].image'), ''), 'chinese-pinyin/loving-family'),
    '$.items[1].image', COALESCE(NULLIF(json_extract(content, '$.items[1].image'), ''), 'chinese-pinyin/child-calling'),
    '$.items[2].image', COALESCE(NULLIF(json_extract(content, '$.items[2].image'), ''), 'chinese-pinyin/brave-child'),
    '$.items[3].image', COALESCE(NULLIF(json_extract(content, '$.items[3].image'), ''), 'chinese-pinyin/gold-medal'),
    '$.items[4].image', COALESCE(NULLIF(json_extract(content, '$.items[4].image'), ''), 'chinese-pinyin/globe-europe'),
    '$.items[5].image', COALESCE(NULLIF(json_extract(content, '$.items[5].image'), ''), 'chinese-pinyin/excellent-star'))
WHERE id = 64 AND type = 'WORD';

UPDATE lesson
SET content = json_set(content,
    '$.items[0].image', COALESCE(NULLIF(json_extract(content, '$.items[0].image'), ''), 'chinese-pinyin/eight-blocks'),
    '$.items[1].image', COALESCE(NULLIF(json_extract(content, '$.items[1].image'), ''), 'chinese-pinyin/child-lying-prone'),
    '$.items[2].image', COALESCE(NULLIF(json_extract(content, '$.items[2].image'), ''), 'chinese-pinyin/mother'),
    '$.items[3].image', COALESCE(NULLIF(json_extract(content, '$.items[3].image'), ''), 'chinese-pinyin/send-paper-plane'),
    '$.items[4].image', COALESCE(NULLIF(json_extract(content, '$.items[4].image'), ''), 'chinese-pinyin/friendly-uncle'),
    '$.items[5].image', COALESCE(NULLIF(json_extract(content, '$.items[5].image'), ''), 'chinese-pinyin/grassy-hillside'))
WHERE id = 65 AND type = 'WORD';

UPDATE lesson
SET content = json_set(content,
    '$.items[0].image', COALESCE(NULLIF(json_extract(content, '$.items[0].image'), ''), 'chinese-pinyin/building-blocks'),
    '$.items[1].image', COALESCE(NULLIF(json_extract(content, '$.items[1].image'), ''), 'chinese-pinyin/young-boy'),
    '$.items[2].image', COALESCE(NULLIF(json_extract(content, '$.items[2].image'), ''), 'chinese-pinyin/pick-up-toy'),
    '$.items[3].image', COALESCE(NULLIF(json_extract(content, '$.items[3].image'), ''), 'chinese-pinyin/pull-wagon'),
    '$.items[4].image', COALESCE(NULLIF(json_extract(content, '$.items[4].image'), ''), 'chinese-pinyin/reading-book'),
    '$.items[5].image', COALESCE(NULLIF(json_extract(content, '$.items[5].image'), ''), 'chinese-pinyin/drawing-picture'))
WHERE id = 66 AND type = 'WORD';

UPDATE lesson
SET content = json_set(content,
    '$.items[0].image', COALESCE(NULLIF(json_extract(content, '$.items[0].image'), ''), 'chinese-pinyin/older-brother'),
    '$.items[1].image', COALESCE(NULLIF(json_extract(content, '$.items[1].image'), ''), 'chinese-pinyin/science-experiment'),
    '$.items[2].image', COALESCE(NULLIF(json_extract(content, '$.items[2].image'), ''), 'chinese-pinyin/drinking-water'),
    '$.items[3].image', COALESCE(NULLIF(json_extract(content, '$.items[3].image'), ''), 'chinese-pinyin/friendly-aunt'),
    '$.items[4].image', COALESCE(NULLIF(json_extract(content, '$.items[4].image'), ''), 'chinese-pinyin/crying-child'),
    '$.items[5].image', COALESCE(NULLIF(json_extract(content, '$.items[5].image'), ''), 'chinese-pinyin/peaceful-lake'))
WHERE id = 67 AND type = 'WORD';
