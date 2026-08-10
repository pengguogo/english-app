-- 补齐数学“加减运算”主题 6 个课时、30 个计算题的概念配图。
-- 每张图只表达合并或拿走的运算语境，不展示数字、算式或答案。

UPDATE lesson SET content = json_set(content, '$.items[0].image', 'math-arithmetic/addition-within-five')
WHERE id = 92 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'math-arithmetic/addition-within-five')
WHERE id = 92 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[1].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'math-arithmetic/addition-within-five')
WHERE id = 92 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[2].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'math-arithmetic/addition-within-five')
WHERE id = 92 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[4].image', 'math-arithmetic/addition-within-five')
WHERE id = 92 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[4].image'), '') = '';

UPDATE lesson SET content = json_set(content, '$.items[0].image', 'math-arithmetic/addition-within-ten')
WHERE id = 93 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'math-arithmetic/addition-within-ten')
WHERE id = 93 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[1].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'math-arithmetic/addition-within-ten')
WHERE id = 93 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[2].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'math-arithmetic/addition-within-ten')
WHERE id = 93 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[4].image', 'math-arithmetic/addition-within-ten')
WHERE id = 93 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[4].image'), '') = '';

UPDATE lesson SET content = json_set(content, '$.items[0].image', 'math-arithmetic/addition-practice')
WHERE id = 94 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'math-arithmetic/addition-practice')
WHERE id = 94 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[1].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'math-arithmetic/addition-practice')
WHERE id = 94 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[2].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'math-arithmetic/addition-practice')
WHERE id = 94 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[4].image', 'math-arithmetic/addition-practice')
WHERE id = 94 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[4].image'), '') = '';

UPDATE lesson SET content = json_set(content, '$.items[0].image', 'math-arithmetic/subtraction-within-five')
WHERE id = 95 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'math-arithmetic/subtraction-within-five')
WHERE id = 95 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[1].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'math-arithmetic/subtraction-within-five')
WHERE id = 95 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[2].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'math-arithmetic/subtraction-within-five')
WHERE id = 95 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[4].image', 'math-arithmetic/subtraction-within-five')
WHERE id = 95 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[4].image'), '') = '';

UPDATE lesson SET content = json_set(content, '$.items[0].image', 'math-arithmetic/subtraction-within-ten')
WHERE id = 96 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'math-arithmetic/subtraction-within-ten')
WHERE id = 96 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[1].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'math-arithmetic/subtraction-within-ten')
WHERE id = 96 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[2].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'math-arithmetic/subtraction-within-ten')
WHERE id = 96 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[4].image', 'math-arithmetic/subtraction-within-ten')
WHERE id = 96 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[4].image'), '') = '';

UPDATE lesson SET content = json_set(content, '$.items[0].image', 'math-arithmetic/subtraction-practice')
WHERE id = 97 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[0].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'math-arithmetic/subtraction-practice')
WHERE id = 97 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[1].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'math-arithmetic/subtraction-practice')
WHERE id = 97 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[2].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'math-arithmetic/subtraction-practice')
WHERE id = 97 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[3].image'), '') = '';
UPDATE lesson SET content = json_set(content, '$.items[4].image', 'math-arithmetic/subtraction-practice')
WHERE id = 97 AND type = 'CALCULATE' AND COALESCE(json_extract(content, '$.items[4].image'), '') = '';
