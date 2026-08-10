-- 为数学「数字认知」存量课程补齐配图；仅在 image 缺失或为空时写入。

-- Lesson 88: 数字比大小
UPDATE lesson SET content = json_set(content, '$.items[0].image', 'math-number/compare-1-2')
WHERE id = 88 AND type = 'WORD'
  AND json_extract(content, '$.items[0].word') = '1<2'
  AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';

UPDATE lesson SET content = json_set(content, '$.items[1].image', 'math-number/compare-3-1')
WHERE id = 88 AND type = 'WORD'
  AND json_extract(content, '$.items[1].word') = '3>1'
  AND COALESCE(json_extract(content, '$.items[1].image'), '') = '';

UPDATE lesson SET content = json_set(content, '$.items[2].image', 'math-number/compare-4-3')
WHERE id = 88 AND type = 'WORD'
  AND json_extract(content, '$.items[2].word') = '4>3'
  AND COALESCE(json_extract(content, '$.items[2].image'), '') = '';

UPDATE lesson SET content = json_set(content, '$.items[3].image', 'math-number/compare-5-2')
WHERE id = 88 AND type = 'WORD'
  AND json_extract(content, '$.items[3].word') = '5>2'
  AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';

-- Lesson 91: 第一到第五
UPDATE lesson SET content = json_set(content, '$.items[0].image', 'math-number/ordinal-first')
WHERE id = 91 AND type = 'WORD'
  AND json_extract(content, '$.items[0].word') = '第一'
  AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';

UPDATE lesson SET content = json_set(content, '$.items[1].image', 'math-number/ordinal-second')
WHERE id = 91 AND type = 'WORD'
  AND json_extract(content, '$.items[1].word') = '第二'
  AND COALESCE(json_extract(content, '$.items[1].image'), '') = '';

UPDATE lesson SET content = json_set(content, '$.items[2].image', 'math-number/ordinal-third')
WHERE id = 91 AND type = 'WORD'
  AND json_extract(content, '$.items[2].word') = '第三'
  AND COALESCE(json_extract(content, '$.items[2].image'), '') = '';

UPDATE lesson SET content = json_set(content, '$.items[3].image', 'math-number/ordinal-fourth')
WHERE id = 91 AND type = 'WORD'
  AND json_extract(content, '$.items[3].word') = '第四'
  AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';

UPDATE lesson SET content = json_set(content, '$.items[4].image', 'math-number/ordinal-fifth')
WHERE id = 91 AND type = 'WORD'
  AND json_extract(content, '$.items[4].word') = '第五'
  AND COALESCE(json_extract(content, '$.items[4].image'), '') = '';
