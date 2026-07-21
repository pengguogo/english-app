-- ============================================================
-- V16: 数学模块内容播种
-- 4个主题(13-16) × 2单元(28-35) × 3课时(86-109) = 24课时
-- 课时类型: WORD 6个(数字认知) + CALCULATE 6个(加减运算) + QUIZ 12个(图形+应用题)
-- 所有 theme 的 subject_id=3(数学)
-- ============================================================

-- Section 1: 新增主题（subject_id=3 数学）
INSERT INTO theme (id, name, sort_order, is_locked, subject_id) VALUES
(13, '数字认知', 1, 0, 3),
(14, '加减运算', 2, 1, 3),
(15, '图形认知', 3, 1, 3),
(16, '趣味应用题', 4, 1, 3);

-- Section 2: 新增单元
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(28, 13, '认识1-5', 1, 0),
(29, 13, '认识6-10', 2, 1),
(30, 14, '10以内加法', 1, 0),
(31, 14, '10以内减法', 2, 1),
(32, 15, '平面图形', 1, 0),
(33, 15, '图形拼搭', 2, 1),
(34, 16, '生活应用', 1, 0),
(35, 16, '趣味问题', 2, 1);

-- ============================================================
-- Section 3: 主题13 数字认知（WORD类型，带图片）
-- ============================================================

-- Lesson 86: 数字1-5 (WORD, 5项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(86, 28, '认识数字1-5', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"1","phonetic":"yī","translation":"一","emoji":"1️⃣","image":"math/number-1"},'
|| '{"word":"2","phonetic":"èr","translation":"二","emoji":"2️⃣","image":"math/number-2"},'
|| '{"word":"3","phonetic":"sān","translation":"三","emoji":"3️⃣","image":"math/number-3"},'
|| '{"word":"4","phonetic":"sì","translation":"四","emoji":"4️⃣","image":"math/number-4"},'
|| '{"word":"5","phonetic":"wǔ","translation":"五","emoji":"5️⃣","image":"math/number-5"}'
|| ']}', 1, 3);

-- Lesson 87: 数量1-5 (WORD, 5项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(87, 28, '数量对应1-5', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"1个苹果","phonetic":"yī","translation":"一个苹果","emoji":"🍎","image":"math/apple-1"},'
|| '{"word":"2只小鸭","phonetic":"èr","translation":"两只小鸭","emoji":"🦆","image":"math/duck-2"},'
|| '{"word":"3朵花","phonetic":"sān","translation":"三朵花","emoji":"🌸","image":"math/flower-3"},'
|| '{"word":"4颗星","phonetic":"sì","translation":"四颗星","emoji":"⭐","image":"math/star-4"},'
|| '{"word":"5个气球","phonetic":"wǔ","translation":"五个气球","emoji":"🎈","image":"math/balloon-5"}'
|| ']}', 2, 3);

-- Lesson 88: 数字比大小 (WORD, 4项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(88, 28, '数字比大小', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"1<2","phonetic":"","translation":"1小于2","emoji":"1️⃣2️⃣"},'
|| '{"word":"3>1","phonetic":"","translation":"3大于1","emoji":"3️⃣1️⃣"},'
|| '{"word":"4>3","phonetic":"","translation":"4大于3","emoji":"4️⃣3️⃣"},'
|| '{"word":"5>2","phonetic":"","translation":"5大于2","emoji":"5️⃣2️⃣"}'
|| ']}', 3, 3);

-- Lesson 89: 数字6-10 (WORD, 5项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(89, 29, '认识数字6-10', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"6","phonetic":"liù","translation":"六","emoji":"6️⃣","image":"math/number-6"},'
|| '{"word":"7","phonetic":"qī","translation":"七","emoji":"7️⃣","image":"math/number-7"},'
|| '{"word":"8","phonetic":"bā","translation":"八","emoji":"8️⃣","image":"math/number-8"},'
|| '{"word":"9","phonetic":"jiǔ","translation":"九","emoji":"9️⃣","image":"math/number-9"},'
|| '{"word":"10","phonetic":"shí","translation":"十","emoji":"🔟","image":"math/number-10"}'
|| ']}', 1, 3);

-- Lesson 90: 数量6-10 (WORD, 5项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(90, 29, '数量对应6-10', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"6个草莓","phonetic":"liù","translation":"六个草莓","emoji":"🍓","image":"math/strawberry-6"},'
|| '{"word":"7个鸡蛋","phonetic":"qī","translation":"七个鸡蛋","emoji":"🥚","image":"math/egg-7"},'
|| '{"word":"8块糖果","phonetic":"bā","translation":"八块糖果","emoji":"🍬","image":"math/candy-8"},'
|| '{"word":"9支铅笔","phonetic":"jiǔ","translation":"九支铅笔","emoji":"✏️","image":"math/pencil-9"},'
|| '{"word":"10个手指","phonetic":"shí","translation":"十个手指","emoji":"🤚","image":"math/finger-10"}'
|| ']}', 2, 3);

-- Lesson 91: 序数词 (WORD, 5项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(91, 29, '第一到第五', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"第一","phonetic":"dì yī","translation":"第1名","emoji":"🥇"},'
|| '{"word":"第二","phonetic":"dì èr","translation":"第2名","emoji":"🥈"},'
|| '{"word":"第三","phonetic":"dì sān","translation":"第3名","emoji":"🥉"},'
|| '{"word":"第四","phonetic":"dì sì","translation":"第4名","emoji":"4️⃣"},'
|| '{"word":"第五","phonetic":"dì wǔ","translation":"第5名","emoji":"5️⃣"}'
|| ']}', 3, 3);

-- ============================================================
-- Section 4: 主题14 加减运算（CALCULATE类型，无图片）
-- ============================================================

-- Lesson 92: 5以内加法 (CALCULATE, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(92, 30, '5以内加法', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"1 + 2 = ?","answer":"3","image":""},'
|| '{"question":"2 + 3 = ?","answer":"5","image":""},'
|| '{"question":"1 + 4 = ?","answer":"5","image":""},'
|| '{"question":"3 + 2 = ?","answer":"5","image":""},'
|| '{"question":"2 + 2 = ?","answer":"4","image":""}'
|| ']}', 1, 3);

-- Lesson 93: 10以内加法 (CALCULATE, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(93, 30, '10以内加法', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"5 + 3 = ?","answer":"8","image":""},'
|| '{"question":"6 + 2 = ?","answer":"8","image":""},'
|| '{"question":"4 + 5 = ?","answer":"9","image":""},'
|| '{"question":"7 + 3 = ?","answer":"10","image":""},'
|| '{"question":"6 + 4 = ?","answer":"10","image":""}'
|| ']}', 2, 3);

-- Lesson 94: 加法练习 (CALCULATE, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(94, 30, '加法综合练习', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"3 + 4 = ?","answer":"7","image":""},'
|| '{"question":"5 + 5 = ?","answer":"10","image":""},'
|| '{"question":"2 + 6 = ?","answer":"8","image":""},'
|| '{"question":"1 + 8 = ?","answer":"9","image":""},'
|| '{"question":"4 + 4 = ?","answer":"8","image":""}'
|| ']}', 3, 3);

-- Lesson 95: 5以内减法 (CALCULATE, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(95, 31, '5以内减法', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"5 - 2 = ?","answer":"3","image":""},'
|| '{"question":"4 - 1 = ?","answer":"3","image":""},'
|| '{"question":"5 - 3 = ?","answer":"2","image":""},'
|| '{"question":"3 - 2 = ?","answer":"1","image":""},'
|| '{"question":"4 - 3 = ?","answer":"1","image":""}'
|| ']}', 1, 3);

-- Lesson 96: 10以内减法 (CALCULATE, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(96, 31, '10以内减法', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"10 - 3 = ?","answer":"7","image":""},'
|| '{"question":"9 - 4 = ?","answer":"5","image":""},'
|| '{"question":"8 - 2 = ?","answer":"6","image":""},'
|| '{"question":"10 - 6 = ?","answer":"4","image":""},'
|| '{"question":"7 - 5 = ?","answer":"2","image":""}'
|| ']}', 2, 3);

-- Lesson 97: 减法练习 (CALCULATE, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(97, 31, '减法综合练习', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"10 - 5 = ?","answer":"5","image":""},'
|| '{"question":"8 - 3 = ?","answer":"5","image":""},'
|| '{"question":"9 - 7 = ?","answer":"2","image":""},'
|| '{"question":"6 - 4 = ?","answer":"2","image":""},'
|| '{"question":"10 - 8 = ?","answer":"2","image":""}'
|| ']}', 3, 3);

-- ============================================================
-- Section 5: 主题15 图形认知（QUIZ类型，部分带图片）
-- ============================================================

-- Lesson 98: 认识圆形和方形 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(98, 32, '认识圆形和方形', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"下面哪个是圆形？","options":["方块","圆球","三角形","长条"],"answer":1,"image":"math/shape-circle"},'
|| '{"question":"下面哪个是正方形？","options":["方块","球","圆柱","圆锥"],"answer":0,"image":"math/shape-square"},'
|| '{"question":"太阳是什么形状？","options":["方形","三角形","圆形","长方形"],"answer":2,"image":""},'
|| '{"question":"盒子是什么形状？","options":["圆形","方形","三角形","椭圆形"],"answer":1,"image":""},'
|| '{"question":"车轮是什么形状？","options":["方形","三角形","圆形","星形"],"answer":2,"image":""}'
|| ']}', 1, 3);

-- Lesson 99: 认识三角形 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(99, 32, '认识三角形', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"三角形有几条边？","options":["2条","3条","4条","5条"],"answer":1,"image":""},'
|| '{"question":"三角形有几个角？","options":["2个","3个","4个","5个"],"answer":1,"image":""},'
|| '{"question":"下面哪个是三角形？","options":["方块","三角尺","圆球","长条"],"answer":1,"image":"math/shape-triangle"},'
|| '{"question":"屋顶常是什么形状？","options":["圆形","三角形","正方形","椭圆形"],"answer":1,"image":""},'
|| '{"question":"红领巾是什么形状？","options":["圆形","三角形","正方形","长方形"],"answer":1,"image":""}'
|| ']}', 2, 3);

-- Lesson 100: 图形分类 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(100, 32, '图形分类', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"正方形有几条边？","options":["3条","4条","5条","6条"],"answer":1,"image":""},'
|| '{"question":"长方形和正方形有什么不同？","options":["边数不同","角度不同","边长","颜色"],"answer":2,"image":""},'
|| '{"question":"球是什么形状？","options":["圆形","球形","方形","三角形"],"answer":1,"image":"math/shape-sphere"},'
|| '{"question":"下面哪个不是平面图形？","options":["圆形","三角形","球","正方形"],"answer":2,"image":""},'
|| '{"question":"拼图游戏中常用什么形状？","options":["圆形","三角形","方形","以上都是"],"answer":3,"image":""}'
|| ']}', 3, 3);

-- Lesson 101: 图形拼搭1 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(101, 33, '图形拼搭入门', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"两个三角形可以拼成什么？","options":["圆形","正方形","球","圆柱"],"answer":1,"image":"math/shape-combine"},'
|| '{"question":"四个小正方形可以拼成什么？","options":["大正方形","圆形","三角形","球"],"answer":0,"image":""},'
|| '{"question":"房子通常由什么图形拼成？","options":["方形+三角形","圆形+方形","三角形+圆形","两个圆形"],"answer":0,"image":""},'
|| '{"question":"鱼可以由什么图形拼成？","options":["三角形+三角形","方形+方形","圆形+圆形","都是方形"],"answer":0,"image":""},'
|| '{"question":"火箭可以由什么图形拼成？","options":["三角形+方形","圆形+圆形","方形+方形","都是三角形"],"answer":0,"image":""}'
|| ']}', 1, 3);

-- Lesson 102: 图形拼搭2 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(102, 33, '图形拼搭进阶', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"一个正方形可以分成几个三角形？","options":["1个","2个","3个","4个"],"answer":1,"image":""},'
|| '{"question":"用圆形可以画出什么？","options":["太阳","月亮","车轮","以上都是"],"answer":3,"image":""},'
|| '{"question":"拼一座桥需要什么图形？","options":["两个三角形","两个圆形","两个方形","两个星形"],"answer":0,"image":""},'
|| '{"question":"钟面是什么形状？","options":["方形","三角形","圆形","星形"],"answer":2,"image":""},'
|| '{"question":"七巧板有几块？","options":["5块","6块","7块","8块"],"answer":2,"image":"math/tangram"}'
|| ']}', 2, 3);

-- Lesson 103: 图形综合 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(103, 33, '图形综合练习', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"积木常是什么形状？","options":["圆形","方形","三角形","各种形状"],"answer":3,"image":""},'
|| '{"question":"镜子常是什么形状？","options":["圆形","方形","三角形","圆形或方形"],"answer":3,"image":""},'
|| '{"question":"下面哪个有4条边？","options":["圆形","三角形","正方形","五边形"],"answer":2,"image":""},'
|| '{"question":"下面哪个有3个角？","options":["圆形","三角形","正方形","六边形"],"answer":1,"image":""},'
|| '{"question":"鸡蛋是什么形状？","options":["正圆形","椭圆形","三角形","方形"],"answer":1,"image":""}'
|| ']}', 3, 3);

-- ============================================================
-- Section 6: 主题16 趣味应用题（QUIZ类型，部分带图片）
-- ============================================================

-- Lesson 104: 简单应用题 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(104, 34, '简单应用题', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"小明有3个苹果，妈妈又给他2个，一共有几个？","options":["4个","5个","6个","3个"],"answer":1,"image":""},'
|| '{"question":"树上有5只鸟，飞走了2只，还剩几只？","options":["2只","3只","4只","5只"],"answer":1,"image":""},'
|| '{"question":"小红有4支铅笔，用了1支，还剩几支？","options":["1支","2支","3支","4支"],"answer":2,"image":""},'
|| '{"question":"桌上有2本书，又放了3本，一共有几本？","options":["4本","5本","6本","3本"],"answer":1,"image":""},'
|| '{"question":"池塘里有6条鱼，游走了3条，还剩几条？","options":["2条","3条","4条","5条"],"answer":1,"image":""}'
|| ']}', 1, 3);

-- Lesson 105: 购物问题 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(105, 34, '购物问题', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"一个冰激凌3元，一个蛋糕5元，一共多少元？","options":["7元","8元","9元","6元"],"answer":1,"image":""},'
|| '{"question":"一个气球2元，买3个要多少元？","options":["5元","6元","7元","8元"],"answer":1,"image":""},'
|| '{"question":"有10元，买了一个4元的面包，还剩多少元？","options":["4元","5元","6元","7元"],"answer":2,"image":""},'
|| '{"question":"一个苹果1元，买5个要多少元？","options":["4元","5元","6元","7元"],"answer":1,"image":""},'
|| '{"question":"一支铅笔2元，一把尺子1元，一共多少元？","options":["2元","3元","4元","5元"],"answer":1,"image":""}'
|| ']}', 2, 3);

-- Lesson 106: 比较问题 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(106, 34, '比较问题', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"小明5岁，小红7岁，谁大？","options":["小明","小红","一样大","不知道"],"answer":1,"image":""},'
|| '{"question":"3和5哪个大？","options":["3","5","一样大","不知道"],"answer":1,"image":""},'
|| '{"question":"10和8哪个小？","options":["10","8","一样","不知道"],"answer":1,"image":""},'
|| '{"question":"小红有5颗糖，小明有3颗，谁多？","options":["小红","小明","一样多","不知道"],"answer":0,"image":""},'
|| '{"question":"第一组4人，第二组6人，哪组人多？","options":["第一组","第二组","一样多","不知道"],"answer":1,"image":""}'
|| ']}', 3, 3);

-- Lesson 107: 趣味问题1 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(107, 35, '趣味问题', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"一只手有几个手指？","options":["4个","5个","6个","10个"],"answer":1,"image":""},'
|| '{"question":"一星期有几天？","options":["5天","6天","7天","8天"],"answer":2,"image":""},'
|| '{"question":"一年有几个季节？","options":["2个","3个","4个","5个"],"answer":2,"image":""},'
|| '{"question":"一双筷子有几根？","options":["1根","2根","3根","4根"],"answer":1,"image":""},'
|| '{"question":"一只猫有几条腿？","options":["2条","3条","4条","5条"],"answer":2,"image":""}'
|| ']}', 1, 3);

-- Lesson 108: 趣味问题2 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(108, 35, '数学脑筋急转弯', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"0和1之间有几个数？","options":["0个","1个","9个","无数个"],"answer":3,"image":""},'
|| '{"question":"最大的一位数是几？","options":["8","9","10","11"],"answer":1,"image":""},'
|| '{"question":"最小的两位数是几？","options":["0","1","10","11"],"answer":2,"image":""},'
|| '{"question":"5的前一个数是几？","options":["3","4","6","7"],"answer":1,"image":""},'
|| '{"question":"10的后一个数是几？","options":["9","10","11","12"],"answer":2,"image":""}'
|| ']}', 2, 3);

-- Lesson 109: 综合应用题 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(109, 35, '综合应用题', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"2只兔子加上3只兔子，一共几只？","options":["4只","5只","6只","3只"],"answer":1,"image":""},'
|| '{"question":"10块糖吃了4块，还剩几块？","options":["5块","6块","7块","4块"],"answer":1,"image":""},'
|| '{"question":"3朵花加3朵花，一共几朵？","options":["5朵","6朵","7朵","4朵"],"answer":1,"image":""},'
|| '{"question":"8个气球飞了5个，还剩几个？","options":["2个","3个","4个","5个"],"answer":1,"image":""},'
|| '{"question":"4个橘子加上6个橘子，一共几个？","options":["8个","9个","10个","11个"],"answer":2,"image":""}'
|| ']}', 3, 3);
