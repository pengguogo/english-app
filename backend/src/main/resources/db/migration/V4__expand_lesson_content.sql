-- 扩充课时内容:WORD 课时从 4 个单词扩充到 8 个,SENTENCE 课时从 3 个句子扩充到 5 个
-- 用户反馈单词量太少,此处补充更多学习项以提升学习体验

-- ============================================================
-- Unit 4: 陆地交通
-- ============================================================

-- Lesson 2: 认识汽车 (WORD) 4 -> 8 个单词
UPDATE lesson SET content =
'{"type":"WORD","items":[
{"word":"car","phonetic":"/kɑːr/","translation":"小汽车","emoji":"🚗"},
{"word":"bus","phonetic":"/bʌs/","translation":"公交车","emoji":"🚌"},
{"word":"bike","phonetic":"/baɪk/","translation":"自行车","emoji":"🚲"},
{"word":"truck","phonetic":"/trʌk/","translation":"卡车","emoji":"🚚"},
{"word":"ambulance","phonetic":"/ˈæmbjələns/","translation":"救护车","emoji":"🚑"},
{"word":"motorcycle","phonetic":"/ˈmoʊtərsaɪkəl/","translation":"摩托车","emoji":"🏍️"},
{"word":"fire engine","phonetic":"/ˈfaɪər ˈɛndʒɪn/","translation":"消防车","emoji":"🚒"},
{"word":"scooter","phonetic":"/ˈskuːtər/","translation":"滑板车","emoji":"🛴"}
]}'
WHERE id = 2;

-- Lesson 3: 认识火车 (WORD) 4 -> 8 个单词
UPDATE lesson SET content =
'{"type":"WORD","items":[
{"word":"train","phonetic":"/treɪn/","translation":"火车","emoji":"🚂"},
{"word":"subway","phonetic":"/ˈsʌbweɪ/","translation":"地铁","emoji":"🚇"},
{"word":"taxi","phonetic":"/ˈtæksi/","translation":"出租车","emoji":"🚕"},
{"word":"police car","phonetic":"/pəˈliːs kɑːr/","translation":"警车","emoji":"🚓"},
{"word":"tram","phonetic":"/træm/","translation":"有轨电车","emoji":"🚊"},
{"word":"van","phonetic":"/væn/","translation":"面包车","emoji":"🚐"},
{"word":"tractor","phonetic":"/ˈtræktər/","translation":"拖拉机","emoji":"🚜"},
{"word":"rickshaw","phonetic":"/ˈrɪkʃɔː/","translation":"三轮车","emoji":"🛺"}
]}'
WHERE id = 3;

-- Lesson 4: 我看见了 (SENTENCE) 3 -> 5 个句子
UPDATE lesson SET content =
'{"type":"SENTENCE","items":[
{"sentence":"I see a car.","translation":"我看见一辆小汽车。","emoji":"🚗"},
{"sentence":"I see a bus.","translation":"我看见一辆公交车。","emoji":"🚌"},
{"sentence":"I see a train.","translation":"我看见一列火车。","emoji":"🚂"},
{"sentence":"I see a bike.","translation":"我看见一辆自行车。","emoji":"🚲"},
{"sentence":"I see a truck.","translation":"我看见一辆卡车。","emoji":"🚚"}
]}'
WHERE id = 4;

-- Lesson 5: 它是什么颜色 (SENTENCE) 3 -> 5 个句子
UPDATE lesson SET content =
'{"type":"SENTENCE","items":[
{"sentence":"The car is red.","translation":"小汽车是红色的。","emoji":"🚗"},
{"sentence":"The bus is yellow.","translation":"公交车是黄色的。","emoji":"🚌"},
{"sentence":"The train is blue.","translation":"火车是蓝色的。","emoji":"🚂"},
{"sentence":"The bike is green.","translation":"自行车是绿色的。","emoji":"🚲"},
{"sentence":"The truck is white.","translation":"卡车是白色的。","emoji":"🚚"}
]}'
WHERE id = 5;

-- ============================================================
-- Unit 5: 空中交通
-- ============================================================

-- Lesson 6: 认识飞机 (WORD) 4 -> 8 个单词
UPDATE lesson SET content =
'{"type":"WORD","items":[
{"word":"airplane","phonetic":"/ˈerpleɪn/","translation":"飞机","emoji":"✈️"},
{"word":"helicopter","phonetic":"/ˈhelɪkɑːptər/","translation":"直升机","emoji":"🚁"},
{"word":"rocket","phonetic":"/ˈrɑːkɪt/","translation":"火箭","emoji":"🚀"},
{"word":"balloon","phonetic":"/bəˈluːn/","translation":"气球","emoji":"🎈"},
{"word":"kite","phonetic":"/kaɪt/","translation":"风筝","emoji":"🪁"},
{"word":"glider","phonetic":"/ˈɡlaɪdər/","translation":"滑翔机","emoji":"🛩️"},
{"word":"drone","phonetic":"/droʊn/","translation":"无人机","emoji":"🛸"},
{"word":"parachute","phonetic":"/ˈpærəʃuːt/","translation":"降落伞","emoji":"🪂"}
]}'
WHERE id = 6;

-- Lesson 7: 飞得高 (SENTENCE) 3 -> 5 个句子
UPDATE lesson SET content =
'{"type":"SENTENCE","items":[
{"sentence":"The airplane flies high.","translation":"飞机飞得高。","emoji":"✈️"},
{"sentence":"The rocket goes up.","translation":"火箭向上飞。","emoji":"🚀"},
{"sentence":"The balloon floats.","translation":"气球飘浮。","emoji":"🎈"},
{"sentence":"The kite flies in the sky.","translation":"风筝在天上飞。","emoji":"🪁"},
{"sentence":"The helicopter flies up.","translation":"直升机向上飞。","emoji":"🚁"}
]}'
WHERE id = 7;

-- Lesson 8: 我喜欢 (SENTENCE) 3 -> 5 个句子
UPDATE lesson SET content =
'{"type":"SENTENCE","items":[
{"sentence":"I like the airplane.","translation":"我喜欢飞机。","emoji":"✈️"},
{"sentence":"I like the helicopter.","translation":"我喜欢直升机。","emoji":"🚁"},
{"sentence":"I like the rocket.","translation":"我喜欢火箭。","emoji":"🚀"},
{"sentence":"I like the kite.","translation":"我喜欢风筝。","emoji":"🪁"},
{"sentence":"I like the balloon.","translation":"我喜欢气球。","emoji":"🎈"}
]}'
WHERE id = 8;

-- ============================================================
-- Unit 6: 水上交通
-- ============================================================

-- Lesson 9: 认识船 (WORD) 4 -> 8 个单词
UPDATE lesson SET content =
'{"type":"WORD","items":[
{"word":"boat","phonetic":"/boʊt/","translation":"小船","emoji":"⛵"},
{"word":"ship","phonetic":"/ʃɪp/","translation":"大船","emoji":"🚢"},
{"word":"submarine","phonetic":"/ˌsʌbməˈriːn/","translation":"潜水艇","emoji":"🤿"},
{"word":"canoe","phonetic":"/kəˈnuː/","translation":"独木舟","emoji":"🛶"},
{"word":"raft","phonetic":"/ræft/","translation":"木筏","emoji":"🪵"},
{"word":"yacht","phonetic":"/jɑːt/","translation":"游艇","emoji":"🛥️"},
{"word":"ferry","phonetic":"/ˈferi/","translation":"渡轮","emoji":"⛴️"},
{"word":"kayak","phonetic":"/ˈkaɪæk/","translation":"皮划艇","emoji":"🚣"}
]}'
WHERE id = 9;

-- Lesson 10: 在水上 (SENTENCE) 3 -> 5 个句子
UPDATE lesson SET content =
'{"type":"SENTENCE","items":[
{"sentence":"The boat is on the water.","translation":"小船在水面上。","emoji":"⛵"},
{"sentence":"The ship is big.","translation":"大船很大。","emoji":"🚢"},
{"sentence":"The submarine is under water.","translation":"潜水艇在水下。","emoji":"🤿"},
{"sentence":"The ferry carries people.","translation":"渡轮载人。","emoji":"⛴️"},
{"sentence":"The canoe is small.","translation":"独木舟很小。","emoji":"🛶"}
]}'
WHERE id = 10;

-- Lesson 11: 复习大挑战 (SENTENCE) 3 -> 5 个句子
UPDATE lesson SET content =
'{"type":"SENTENCE","items":[
{"sentence":"I see a car and a bus.","translation":"我看见一辆小汽车和一辆公交车。","emoji":"🚗"},
{"sentence":"The airplane flies in the sky.","translation":"飞机在天上飞。","emoji":"✈️"},
{"sentence":"The boat is on the water.","translation":"小船在水面上。","emoji":"⛵"},
{"sentence":"The train is very long.","translation":"火车很长。","emoji":"🚂"},
{"sentence":"The rocket goes up high.","translation":"火箭飞得很高。","emoji":"🚀"}
]}'
WHERE id = 11;
