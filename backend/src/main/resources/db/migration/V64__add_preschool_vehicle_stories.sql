-- V64：新增三篇面向 3～6 岁儿童的车辆主题双语互动绘本。
-- 每篇四页，使用短句、重复动词和逐页配图降低启蒙阅读难度。

INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(148, 2, '红色小火车出发', 4, 0),
(149, 2, '忙碌的小挖机', 5, 0),
(150, 2, '红色消防车', 6, 0);

INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(601, 148, '红色小火车出发', 'SENTENCE', '{
  "type":"SENTENCE",
  "items":[
    {"sentence":"The red train goes.","translation":"红色小火车出发啦。","image":"vehicle-stories/little-red-train-01"},
    {"sentence":"It goes through the tunnel.","translation":"它穿过隧道。","image":"vehicle-stories/little-red-train-02"},
    {"sentence":"It crosses the bridge.","translation":"它开过小桥。","image":"vehicle-stories/little-red-train-03"},
    {"sentence":"The train is here!","translation":"小火车到站啦！","image":"vehicle-stories/little-red-train-04"}
  ]
}', 1, 3),
(602, 149, '忙碌的小挖机', 'SENTENCE', '{
  "type":"SENTENCE",
  "items":[
    {"sentence":"The digger sees dirt.","translation":"小挖机看见一堆土。","image":"vehicle-stories/busy-digger-01"},
    {"sentence":"Dig, dig, dig!","translation":"挖呀，挖呀，挖！","image":"vehicle-stories/busy-digger-02"},
    {"sentence":"The truck gets the dirt.","translation":"卡车装上了土。","image":"vehicle-stories/busy-digger-03"},
    {"sentence":"Good job, team!","translation":"做得好，工程车小队！","image":"vehicle-stories/busy-digger-04"}
  ]
}', 1, 3),
(603, 150, '红色消防车', 'SENTENCE', '{
  "type":"SENTENCE",
  "items":[
    {"sentence":"The fire truck waits.","translation":"消防车在等待。","image":"vehicle-stories/red-fire-truck-01"},
    {"sentence":"Go, go, go!","translation":"出发，出发，出发！","image":"vehicle-stories/red-fire-truck-02"},
    {"sentence":"Water puts out the fire.","translation":"水把小火扑灭了。","image":"vehicle-stories/red-fire-truck-03"},
    {"sentence":"The fire truck is home.","translation":"消防车回到消防站啦！","image":"vehicle-stories/red-fire-truck-04"}
  ]
}', 1, 3);
