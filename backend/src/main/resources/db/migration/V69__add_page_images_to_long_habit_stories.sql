-- V69：为四篇成长好习惯长篇故事补齐逐页插图。
-- 仅更新 48 个阅读项的 image；保留全部正文、页序、问答与历史迁移。

UPDATE lesson
SET content = json_set(content,
    '$.items[0].image', 'growth-consequences/missed-exhibition-01',
    '$.items[1].image', 'growth-consequences/missed-exhibition-02',
    '$.items[2].image', 'growth-consequences/missed-exhibition-03',
    '$.items[3].image', 'growth-consequences/missed-exhibition-04',
    '$.items[4].image', 'growth-consequences/missed-exhibition-05',
    '$.items[5].image', 'growth-consequences/missed-exhibition-06',
    '$.items[6].image', 'growth-consequences/missed-exhibition-07',
    '$.items[7].image', 'growth-consequences/missed-exhibition-08',
    '$.items[8].image', 'growth-consequences/missed-exhibition-09',
    '$.items[9].image', 'growth-consequences/missed-exhibition-10',
    '$.items[10].image', 'growth-consequences/missed-exhibition-11',
    '$.items[11].image', 'growth-consequences/missed-exhibition-12'
)
WHERE id = 619 AND type = 'READING';

UPDATE lesson
SET content = json_set(content,
    '$.items[0].image', 'growth-consequences/damaged-memory-book-01',
    '$.items[1].image', 'growth-consequences/damaged-memory-book-02',
    '$.items[2].image', 'growth-consequences/damaged-memory-book-03',
    '$.items[3].image', 'growth-consequences/damaged-memory-book-04',
    '$.items[4].image', 'growth-consequences/damaged-memory-book-05',
    '$.items[5].image', 'growth-consequences/damaged-memory-book-06',
    '$.items[6].image', 'growth-consequences/damaged-memory-book-07',
    '$.items[7].image', 'growth-consequences/damaged-memory-book-08',
    '$.items[8].image', 'growth-consequences/damaged-memory-book-09',
    '$.items[9].image', 'growth-consequences/damaged-memory-book-10',
    '$.items[10].image', 'growth-consequences/damaged-memory-book-11',
    '$.items[11].image', 'growth-consequences/damaged-memory-book-12'
)
WHERE id = 621 AND type = 'READING';

UPDATE lesson
SET content = json_set(content,
    '$.items[0].image', 'growth-consequences/empty-stage-place-01',
    '$.items[1].image', 'growth-consequences/empty-stage-place-02',
    '$.items[2].image', 'growth-consequences/empty-stage-place-03',
    '$.items[3].image', 'growth-consequences/empty-stage-place-04',
    '$.items[4].image', 'growth-consequences/empty-stage-place-05',
    '$.items[5].image', 'growth-consequences/empty-stage-place-06',
    '$.items[6].image', 'growth-consequences/empty-stage-place-07',
    '$.items[7].image', 'growth-consequences/empty-stage-place-08',
    '$.items[8].image', 'growth-consequences/empty-stage-place-09',
    '$.items[9].image', 'growth-consequences/empty-stage-place-10',
    '$.items[10].image', 'growth-consequences/empty-stage-place-11',
    '$.items[11].image', 'growth-consequences/empty-stage-place-12'
)
WHERE id = 623 AND type = 'READING';

UPDATE lesson
SET content = json_set(content,
    '$.items[0].image', 'growth-consequences/hidden-crack-01',
    '$.items[1].image', 'growth-consequences/hidden-crack-02',
    '$.items[2].image', 'growth-consequences/hidden-crack-03',
    '$.items[3].image', 'growth-consequences/hidden-crack-04',
    '$.items[4].image', 'growth-consequences/hidden-crack-05',
    '$.items[5].image', 'growth-consequences/hidden-crack-06',
    '$.items[6].image', 'growth-consequences/hidden-crack-07',
    '$.items[7].image', 'growth-consequences/hidden-crack-08',
    '$.items[8].image', 'growth-consequences/hidden-crack-09',
    '$.items[9].image', 'growth-consequences/hidden-crack-10',
    '$.items[10].image', 'growth-consequences/hidden-crack-11',
    '$.items[11].image', 'growth-consequences/hidden-crack-12'
)
WHERE id = 625 AND type = 'READING';
