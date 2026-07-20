-- 新增主题:交通工具乐园
INSERT INTO theme (id, name, sort_order, is_locked) VALUES (2, '交通工具乐园', 2, 0);

-- 新增单元:陆地交通(4 课时)、空中交通(3 课时)、水上交通(3 课时)
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(4, 2, '陆地交通', 1, 0),
(5, 2, '空中交通', 2, 1),
(6, 2, '水上交通', 3, 1);

-- Unit 4: 陆地交通(4 课时)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(2, 4, '认识汽车', 'WORD', '{"type":"WORD","items":[{"word":"car","phonetic":"/kɑːr/","translation":"小汽车","emoji":"🚗"},{"word":"bus","phonetic":"/bʌs/","translation":"公交车","emoji":"🚌"},{"word":"bike","phonetic":"/baɪk/","translation":"自行车","emoji":"🚲"},{"word":"truck","phonetic":"/trʌk/","translation":"卡车","emoji":"🚚"}]}', 1, 3),
(3, 4, '认识火车', 'WORD', '{"type":"WORD","items":[{"word":"train","phonetic":"/treɪn/","translation":"火车","emoji":"🚂"},{"word":"subway","phonetic":"/ˈsʌbweɪ/","translation":"地铁","emoji":"🚇"},{"word":"taxi","phonetic":"/ˈtæksi/","translation":"出租车","emoji":"🚕"},{"word":"police car","phonetic":"/pəˈliːs kɑːr/","translation":"警车","emoji":"🚓"}]}', 2, 3),
(4, 4, '我看见了', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"I see a car.","translation":"我看见一辆小汽车。","emoji":"🚗"},{"sentence":"I see a bus.","translation":"我看见一辆公交车。","emoji":"🚌"},{"sentence":"I see a train.","translation":"我看见一列火车。","emoji":"🚂"}]}', 3, 3),
(5, 4, '它是什么颜色', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"The car is red.","translation":"小汽车是红色的。","emoji":"🚗"},{"sentence":"The bus is yellow.","translation":"公交车是黄色的。","emoji":"🚌"},{"sentence":"The train is blue.","translation":"火车是蓝色的。","emoji":"🚂"}]}', 4, 3);

-- Unit 5: 空中交通(3 课时)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(6, 5, '认识飞机', 'WORD', '{"type":"WORD","items":[{"word":"airplane","phonetic":"/ˈerpleɪn/","translation":"飞机","emoji":"✈️"},{"word":"helicopter","phonetic":"/ˈhelɪkɑːptər/","translation":"直升机","emoji":"🚁"},{"word":"rocket","phonetic":"/ˈrɑːkɪt/","translation":"火箭","emoji":"🚀"},{"word":"balloon","phonetic":"/bəˈluːn/","translation":"气球","emoji":"🎈"}]}', 1, 3),
(7, 5, '飞得高', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"The airplane flies high.","translation":"飞机飞得高。","emoji":"✈️"},{"sentence":"The rocket goes up.","translation":"火箭向上飞。","emoji":"🚀"},{"sentence":"The balloon floats.","translation":"气球飘浮。","emoji":"🎈"}]}', 2, 3),
(8, 5, '我喜欢', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"I like the airplane.","translation":"我喜欢飞机。","emoji":"✈️"},{"sentence":"I like the helicopter.","translation":"我喜欢直升机。","emoji":"🚁"},{"sentence":"I like the rocket.","translation":"我喜欢火箭。","emoji":"🚀"}]}', 3, 3);

-- Unit 6: 水上交通(3 课时)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(9, 6, '认识船', 'WORD', '{"type":"WORD","items":[{"word":"boat","phonetic":"/boʊt/","translation":"小船","emoji":"⛵"},{"word":"ship","phonetic":"/ʃɪp/","translation":"大船","emoji":"🚢"},{"word":"submarine","phonetic":"/ˌsʌbməˈriːn/","translation":"潜水艇","emoji":"🤿"},{"word":"canoe","phonetic":"/kəˈnuː/","translation":"独木舟","emoji":"🛶"}]}', 1, 3),
(10, 6, '在水上', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"The boat is on the water.","translation":"小船在水面上。","emoji":"⛵"},{"sentence":"The ship is big.","translation":"大船很大。","emoji":"🚢"},{"sentence":"The submarine is under water.","translation":"潜水艇在水下。","emoji":"🤿"}]}', 2, 3),
(11, 6, '复习大挑战', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"I see a car and a bus.","translation":"我看见一辆小汽车和一辆公交车。","emoji":"🚗"},{"sentence":"The airplane flies in the sky.","translation":"飞机在天上飞。","emoji":"✈️"},{"sentence":"The boat is on the water.","translation":"小船在水面上。","emoji":"⛵"}]}', 3, 3);
