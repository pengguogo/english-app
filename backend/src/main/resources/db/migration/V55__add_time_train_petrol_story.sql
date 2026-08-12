-- V55: 时空列车五部曲之二——20 世纪初汽油轨道车
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(117, 5, '时空列车2：燃油新声', 16, 0);

INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(529, 117, '会喝燃料的新车', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"来到 1905 年","content":"彭泽宇从时空隧道落到 1905 年的一座乡间车站。站台边没有高大的蒸汽锅炉，只有一辆像大客车的小型汽油轨道车。工程师说 petrol 是汽油，engine 是发动机。这种车适合旅客不多的支线，是铁路探索新动力的一次尝试。","image":"story/time-train-petrol-01"},
    {"title":"四冲程节拍","content":"工程师用模型演示发动机的四个步骤：吸气、压缩、做功、排气。彭泽宇跟着节拍数 one、two、three、four。四个步骤组成一个循环，两个循环共有 8 个步骤。燃料在发动机内部燃烧，所以它属于内燃动力。","image":"story/time-train-petrol-02"},
    {"title":"轻车的小任务","content":"汽油轨道车要把 12 位旅客送到集市。前站上来 5 人，后站上来 7 人，正好坐满。彭泽宇帮忙核对车票，用 full 表示满座，用 empty 表示空。小车不必牵引很多车厢，却能灵活服务乡村支线。","image":"story/time-train-petrol-03"},
    {"title":"燃料不能猜","content":"仪表显示油箱有 18 升燃料，预计去程需要 7 升，回程也需要 7 升。彭泽宇算出往返需要 14 升，还能留下 4 升安全余量。工程师说，不能只看“差不多”，必须根据仪表和计划确认燃料足够。enough 表示足够。","image":"story/time-train-petrol-04"},
    {"title":"奇怪的敲击声","content":"发动机忽然发出不均匀的敲击声。彭泽宇听见后立刻说 Slow down。司机减速并停进侧线，维修员检查火花塞和供油管。原来一根接头松了。大家关闭发动机、隔离燃料后才维修，没有带病继续行驶。","image":"story/time-train-petrol-05"},
    {"title":"上坡前的选择","content":"前方有一段长坡。司机可以一次带 3 辆小车，也可以分两次带较少车厢。工程师根据发动机功率选择只带 2 辆，避免过载。彭泽宇学会 heavy 是重，light 是轻。合适的载重比勉强多拉更安全。","image":"story/time-train-petrol-06"},
    {"title":"雨中的灯光","content":"返程时下起小雨，能见度降低。彭泽宇检查前灯、雨刷和喇叭，司机把速度从每小时 30 千米降到 20 千米。慢了 10 千米每小时，却能留出更多观察时间。slow 不代表失败，而是根据环境做出正确选择。","image":"story/time-train-petrol-07"},
    {"title":"动力继续变化","content":"汽油轨道车安全回站，工程师把一张发动机图纸交给彭泽宇。他说，未来更强壮的内燃机车会使用柴油，并通过发电机和电动机驱动车轮。怀表跳到 1955 年，远方传来低沉有力的新机车轰鸣。","image":"story/time-train-petrol-08"}
  ]
}', 1, 3),
(530, 117, '燃油新声问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"小型汽油轨道车为什么适合乡村支线？","options":["它灵活且适合较少旅客","它可以在没有铁轨的地方飞行","它能牵引无限多车厢"],"answer":0,"image":"story/time-train-petrol-quiz"},
    {"question":"发动机出现异常敲击声后，大家怎样处理？","options":["加速掩盖声音","减速停车并在隔离燃料后检查","让旅客自己修理"],"answer":1,"image":"story/time-train-petrol-quiz"},
    {"question":"petrol 在故事中表示什么？","options":["蒸汽","汽油","电力"],"answer":1,"image":"story/time-train-petrol-quiz"},
    {"question":"哪一个英语单词表示轻？","options":["heavy","light","full"],"answer":1,"image":"story/time-train-petrol-quiz"},
    {"question":"5 位旅客加上 7 位旅客，共有多少人？","options":["10 人","11 人","12 人"],"answer":2,"image":"story/time-train-petrol-quiz"},
    {"question":"油箱有 18 升，往返用掉 14 升，还剩多少升？","options":["3 升","4 升","5 升"],"answer":1,"image":"story/time-train-petrol-quiz"},
    {"question":"雨天为什么要降低速度？","options":["能见度降低，需要更多观察和制动时间","这样会让燃料变成水","因为雨天不能使用车灯"],"answer":0,"image":"story/time-train-petrol-quiz"},
    {"question":"故事中的工程师怎样做选择？","options":["只追求多拉车厢","根据仪表、载重和环境合理计划","完全凭感觉决定"],"answer":1,"image":"story/time-train-petrol-quiz"}
  ]
}', 2, 3);
