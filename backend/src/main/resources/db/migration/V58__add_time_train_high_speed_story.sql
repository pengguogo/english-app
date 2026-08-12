-- V58: 时空列车五部曲之五——1964 年新干线与现代高速铁路
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(120, 5, '时空列车5：追赶未来', 19, 0);

INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(535, 120, '追赶未来的高速列车', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"1964 年的白色列车","content":"彭泽宇来到 1964 年的日本，看到白色流线型列车停在东京站。工作人员告诉他，东海道新干线在这一年投入运营，是世界高速铁路发展的重要里程碑。high-speed train 表示高速列车，future 表示未来。","image":"story/time-train-high-speed-01"},
    {"title":"六小时三十分与四小时","content":"过去东京到大阪约需 6 小时 30 分，新干线开通时缩短到约 4 小时。彭泽宇把 6 小时 30 分减去 4 小时，算出节省约 2 小时 30 分。更快的旅行让相距很远的城市变得更近。","image":"story/time-train-high-speed-02"},
    {"title":"流线型的秘密","content":"工程师用风洞模型展示圆润车头。空气经过流线型表面时阻力更小，列车高速行驶会更平稳。彭泽宇比较方形积木和水滴形模型，学会 fast 是快，smooth 是平滑。速度提升需要车辆、轨道、供电和信号共同支持。","image":"story/time-train-high-speed-03"},
    {"title":"英文站台任务","content":"时空怀表提示他去 Platform 3。彭泽宇知道 platform 是站台，找到 3 号站台后又核对 Train 8、Seat 12A。数字和英语都必须读准确，不能只看一个信息就上车。工作人员还提醒他站在黄色安全线后。","image":"story/time-train-high-speed-04"},
    {"title":"三段时间之门","content":"回家路线经过三个时空节点：第一段 15 分钟，第二段 20 分钟，第三段 25 分钟。彭泽宇算出总共需要 60 分钟，也就是 1 小时。怀表只会在准时到达节点时开启下一扇门，他必须认真看时钟和运行图。","image":"story/time-train-high-speed-05"},
    {"title":"风雨中的减速命令","content":"高速列车进入山区时，监测系统发现强风。控制中心下达 Reduce speed，司机按规定降低速度。虽然会晚几分钟，但安全比追赶时刻更重要。彭泽宇想起每个时代的司机：面对异常，他们都会观察、报告、按规则处理。","image":"story/time-train-high-speed-06"},
    {"title":"五种动力排排队","content":"最后一道时空门要求按历史顺序排列五种列车：蒸汽机车、汽油轨道车、柴油电力机车、电力机车、高速列车。彭泽宇把卡片正确排好，并说 steam、petrol、diesel、electric、high-speed。五枚时空徽章同时发光。","image":"story/time-train-high-speed-07"},
    {"title":"梦醒后的车票","content":"高速列车穿过彩色隧道，彭泽宇回到自己的房间。天刚亮，火车书仍放在枕边，桌上却多了一张沾着煤灰、带着齿轮纹和蓝色电光的旧车票。他明白，动力和速度一直改变，但安全、合作、计算与学习永远不会过时。","image":"story/time-train-high-speed-08"}
  ]
}', 1, 3),
(536, 120, '追赶未来问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"1964 年投入运营并成为高速铁路重要里程碑的是什么？","options":["东海道新干线","雨山试车会","第一辆汽油轨道车"],"answer":0,"image":"story/time-train-high-speed-quiz"},
    {"question":"强风出现时，高速列车为什么要按命令减速？","options":["为了让乘客多看风景","为了在异常天气中保证安全","因为列车没有电"],"answer":1,"image":"story/time-train-high-speed-quiz"},
    {"question":"high-speed train 的意思是什么？","options":["货运汽车","高速列车","蒸汽锅炉"],"answer":1,"image":"story/time-train-high-speed-quiz"},
    {"question":"Reduce speed 表示什么？","options":["降低速度","打开车门","更换座位"],"answer":0,"image":"story/time-train-high-speed-quiz"},
    {"question":"6 小时 30 分缩短到 4 小时，大约节省多久？","options":["1 小时 30 分","2 小时 30 分","3 小时 30 分"],"answer":1,"image":"story/time-train-high-speed-quiz"},
    {"question":"15 分钟、20 分钟和 25 分钟合起来是多少分钟？","options":["50 分钟","55 分钟","60 分钟"],"answer":2,"image":"story/time-train-high-speed-quiz"},
    {"question":"高速列车能够安全运行，需要哪些系统共同配合？","options":["只需要流线型车头","车辆、轨道、供电、信号和人员共同配合","只需要更大的车轮"],"answer":1,"image":"story/time-train-high-speed-quiz"},
    {"question":"五次时空旅行中始终没有改变的是什么？","options":["所有列车都烧煤","所有列车速度相同","安全、合作、认真计算和持续学习"],"answer":2,"image":"story/time-train-high-speed-quiz"}
  ]
}', 2, 3);
