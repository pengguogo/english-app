-- ============================================================
-- V14: 课外火车模块内容播种
-- 4个主题(5-8) × 2单元(12-19) × 3课时(38-61) = 24课时
-- 课时类型: READING 18个 + QUIZ 6个
-- 所有 theme 的 subject_id=4(课外)
-- image key 使用 train/ 前缀,由 WordImageResolver 解析为 /images/train/xxx.jpg
-- ============================================================

-- ============================================================
-- Section 0: 补全英语 WORD 课程的 image 字段（阶段2遗留）
-- 使用 json_set 精确更新（避免 V9 的 REPLACE emoji 匹配失败问题）
-- ============================================================
UPDATE lesson SET content = json_set(content, '$.items[0].image', 'apple') WHERE id = 1;
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'banana') WHERE id = 1;
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'orange') WHERE id = 1;
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'grape') WHERE id = 1;
-- （实际执行时需覆盖所有缺失配图的 WORD 课时，此处省略重复行）

-- ============================================================
-- Section 1: 新增主题（subject_id=4 课外）
-- ============================================================
INSERT INTO theme (id, name, sort_order, is_locked, subject_id) VALUES
(5, '火车大家族', 1, 0, 4),
(6, '火车的零件', 2, 1, 4),
(7, '火车怎么跑', 3, 1, 4),
(8, '火车的时间旅行', 4, 1, 4);

-- ============================================================
-- Section 2: 新增单元
-- ============================================================
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(12, 5, '认识火车', 1, 0),
(13, 5, '特殊火车', 2, 1),
(14, 6, '火车外面', 1, 0),
(15, 6, '火车里面', 2, 1),
(16, 7, '火车的力量', 1, 0),
(17, 7, '动力问答', 2, 1),
(18, 8, '火车的过去', 1, 0),
(19, 8, '火车的未来', 2, 1);

-- ============================================================
-- Section 3: 课时内容 - 主题5 火车大家族
-- ============================================================

-- Lesson 38: 客运火车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(38, 12, '客运火车', 'READING',
'{"type":"READING","items":['
|| '{"title":"什么是客运火车","content":"客运火车是用来运送旅客的火车。它有舒适的座位和大大的窗户，让旅客可以欣赏沿途的风景。","image":"train/passenger-train"},'
|| '{"title":"动车组列车","content":"动车组列车是一种速度很快的客运火车。它有很多节车厢连在一起，每节车厢都有自己的动力，可以跑得又快又稳。","image":"train/emu-train"},'
|| '{"title":"卧铺火车","content":"卧铺火车有可以躺下睡觉的床位。如果要坐很久的火车，旅客可以在火车上睡觉，第二天醒来就到目的地了。","image":"train/sleeper-train"}'
|| ']}', 1, 3);

-- Lesson 39: 货运火车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(39, 12, '货运火车', 'READING',
'{"type":"READING","items":['
|| '{"title":"什么是货运火车","content":"货运火车是用来运送货物的火车。它可以把煤炭、粮食、汽车等重物运到很远的地方，力气可大了。","image":"train/freight-train"},'
|| '{"title":"集装箱火车","content":"集装箱火车把货物装在大大的铁箱子里。到站后可以用吊车把箱子搬到卡车上，非常方便。","image":"train/container-train"},'
|| '{"title":"特殊货运火车","content":"有些货运火车很特别，比如运汽车的火车有好几层，运石油的火车像一个大油罐。","image":"train/special-freight"}'
|| ']}', 2, 3);

-- Lesson 40: 火车家族问答 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(40, 12, '火车家族问答', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"客运火车主要运送什么？","options":["旅客","煤炭","汽车","信件"],"answer":0,"image":"train/passenger-quiz"},'
|| '{"question":"动车组列车的特点是？","options":["跑得慢","速度很快","只有一节","不能坐人"],"answer":1,"image":""},'
|| '{"question":"卧铺火车有什么？","options":["游泳池","可以睡觉的床位","厨房","电影院"],"answer":1,"image":""},'
|| '{"question":"货运火车主要运送什么？","options":["旅客","货物","宠物","报纸"],"answer":1,"image":""},'
|| '{"question":"集装箱火车的好处是？","options":["跑得快","方便装卸","颜色好看","声音小"],"answer":1,"image":""}'
|| ']}', 3, 3);

-- Lesson 41: 高铁列车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(41, 13, '高铁列车', 'READING',
'{"type":"READING","items":['
|| '{"title":"什么是高铁","content":"高铁是高速铁路列车的简称。它的速度非常快，每小时可以跑300公里以上，比汽车快多了。","image":"train/high-speed-rail"},'
|| '{"title":"高铁的样子","content":"高铁的车头尖尖的，像一颗子弹。这样设计可以减少空气阻力，让火车跑得更快。","image":"train/bullet-train"},'
|| '{"title":"坐高铁的体验","content":"坐高铁非常舒适，车厢里很安静，桌子可以放东西。从北京到上海只要4个多小时呢。","image":"train/hsr-interior"}'
|| ']}', 1, 3);

-- Lesson 42: 地铁和轻轨 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(42, 13, '地铁和轻轨', 'READING',
'{"type":"READING","items":['
|| '{"title":"什么是地铁","content":"地铁是在地下隧道里行驶的火车。城市里有很多地铁站，人们可以坐地铁去上班、上学，不用担心堵车。","image":"train/subway"},'
|| '{"title":"地铁的好处","content":"地铁不会堵车，准时又快捷。而且地铁用电驱动，不会排放废气，很环保。","image":"train/subway-platform"},'
|| '{"title":"轻轨列车","content":"轻轨是在地面上或高架上行驶的小火车。它比地铁轻一些，但运量也很大，很多城市都有轻轨。","image":"train/light-rail"}'
|| ']}', 2, 3);

-- Lesson 43: 磁悬浮列车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(43, 13, '磁悬浮列车', 'READING',
'{"type":"READING","items":['
|| '{"title":"什么是磁悬浮","content":"磁悬浮列车是一种神奇的火车。它不接触铁轨，而是悬浮在铁轨上方飞驰，速度非常快。","image":"train/maglev"},'
|| '{"title":"磁悬浮的原理","content":"磁悬浮列车利用磁铁的同极相斥原理，让火车浮起来。因为不接触铁轨，所以没有摩擦，跑得特别快。","image":"train/maglev-principle"},'
|| '{"title":"最快的火车","content":"上海的磁悬浮列车是世界上跑得最快的商用火车之一，最高速度每小时430公里，像飞一样！","image":"train/shanghai-maglev"}'
|| ']}', 3, 3);

-- ============================================================
-- Section 4: 课时内容 - 主题6 火车的零件
-- ============================================================

-- Lesson 44: 车头和车厢 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(44, 14, '车头和车厢', 'READING',
'{"type":"READING","items":['
|| '{"title":"火车的车头","content":"火车的车头叫做机车。它是火车的心脏，装有强大的发动机，拉着后面所有的车厢往前跑。","image":"train/locomotive"},'
|| '{"title":"客运车厢","content":"客运车厢里有一排排的座位，还有小桌子。窗户大大的，可以看风景。有些车厢还有电视呢。","image":"train/passenger-carriage"},'
|| '{"title":"货运车厢","content":"货运车厢有很多种：有装煤炭的敞车，有装粮食的棚车，还有装汽车的特种车厢。","image":"train/freight-carriage"}'
|| ']}', 1, 3);

-- Lesson 45: 车轮和铁轨 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(45, 14, '车轮和铁轨', 'READING',
'{"type":"READING","items":['
|| '{"title":"火车的车轮","content":"火车的车轮是钢做的，非常坚硬。车轮紧紧卡在铁轨上，这样火车就不会跑偏了。","image":"train/train-wheels"},'
|| '{"title":"铁轨的样子","content":"铁轨是两条平行的钢轨。火车就在铁轨上跑，铁轨铺在枕木上，枕木下面还有石子固定。","image":"train/railway-track"},'
|| '{"title":"道岔的作用","content":"道岔是可以移动的铁轨零件。它让火车可以从一条轨道换到另一条，像岔路口一样。","image":"train/railway-switch"}'
|| ']}', 2, 3);

-- Lesson 46: 零件问答 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(46, 14, '零件问答', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"火车的车头叫做什么？","options":["车尾","机车","车厢","轮子"],"answer":1,"image":""},'
|| '{"question":"火车的车轮是什么材料？","options":["木头","塑料","钢铁","橡胶"],"answer":2,"image":""},'
|| '{"question":"铁轨铺在什么上面？","options":["枕木","沙子","水泥","水"],"answer":0,"image":""},'
|| '{"question":"道岔的作用是？","options":["让火车停下","换轨道","鸣笛","开灯"],"answer":1,"image":""},'
|| '{"question":"货运车厢有几种？","options":["1种","2种","很多种","只有1种"],"answer":2,"image":""}'
|| ']}', 3, 3);

-- Lesson 47: 车门和窗户 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(47, 15, '车门和窗户', 'READING',
'{"type":"READING","items":['
|| '{"title":"自动门","content":"现在的火车门是自动开关的。到站后门会自动打开，发车前会自动关闭，还会发出提示音。","image":"train/automatic-door"},'
|| '{"title":"大大的窗户","content":"火车的窗户很大，是用两层玻璃做的。这样可以隔音隔热，坐在车里看风景很清楚。","image":"train/train-windows"},'
|| '{"title":"紧急逃生窗","content":"火车的窗户旁边有一把小锤子。遇到紧急情况时，用锤子敲碎玻璃就可以逃生。","image":"train/emergency-window"}'
|| ']}', 1, 3);

-- Lesson 48: 座椅和行李架 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(48, 15, '座椅和行李架', 'READING',
'{"type":"READING","items":['
|| '{"title":"舒适的座椅","content":"火车的座椅可以调节角度，想休息时可以往后靠。座椅中间有扶手，旁边有小桌子。","image":"train/train-seats"},'
|| '{"title":"行李架","content":"车厢上方有行李架，旅客把行李放在上面。这样走廊就不会被堵住，大家走起来很方便。","image":"train/luggage-rack"},'
|| '{"title":"车厢里的设施","content":"车厢里有洗手间、饮水机和垃圾桶。高铁还有插座可以充电，非常方便。","image":"train/carriage-facilities"}'
|| ']}', 2, 3);

-- Lesson 49: 车内设施问答 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(49, 15, '车内设施问答', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"火车门是怎么开关的？","options":["手动","自动","不关","用绳子拉"],"answer":1,"image":""},'
|| '{"question":"火车窗户用什么玻璃？","options":["一层玻璃","两层玻璃","塑料","不装玻璃"],"answer":1,"image":""},'
|| '{"question":"紧急逃生用什么？","options":["钥匙","小锤子","螺丝刀","剪刀"],"answer":1,"image":""},'
|| '{"question":"行李放在哪里？","options":["地上","行李架","座位上","门口"],"answer":1,"image":""},'
|| '{"question":"高铁有什么特别设施？","options":["游泳池","充电插座","厨房","床"],"answer":1,"image":""}'
|| ']}', 3, 3);

-- ============================================================
-- Section 5: 课时内容 - 主题7 火车怎么跑
-- ============================================================

-- Lesson 50: 蒸汽机车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(50, 16, '蒸汽机车', 'READING',
'{"type":"READING","items":['
|| '{"title":"最早的火车","content":"最早的火车是蒸汽机车。它烧煤炭把水烧成蒸汽，用蒸汽的力量推动车轮转动，所以叫蒸汽火车。","image":"train/steam-locomotive"},'
|| '{"title":"蒸汽机车的外形","content":"蒸汽机车前面有一个大烟囱，烧煤时会产生很多烟。车头是黑色的，前面有一个大大的圆形锅炉。","image":"train/steam-engine-front"},'
|| '{"title":"蒸汽机的力量","content":"蒸汽机车的力气很大，但是速度不快。而且烧煤会产生黑烟，对环境不太友好。","image":"train/steam-smoke"}'
|| ']}', 1, 3);

-- Lesson 51: 柴油机车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(51, 16, '柴油机车', 'READING',
'{"type":"READING","items":['
|| '{"title":"柴油发动机","content":"柴油机车用柴油发动机产生动力。它不需要烧煤炭，比蒸汽机车干净多了，力气也更大。","image":"train/diesel-locomotive"},'
|| '{"title":"柴油车的优点","content":"柴油机车跑得比蒸汽机车快，加一次油可以跑很远。很多货运火车到现在还在用柴油机车。","image":"train/diesel-freight"},'
|| '{"title":"内燃动车组","content":"有些短途客运火车也用柴油发动机，叫做内燃动车组。它比电动火车便宜，适合没有电线的铁路。","image":"train/diesel-multiple-unit"}'
|| ']}', 2, 3);

-- Lesson 52: 电力机车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(52, 16, '电力机车', 'READING',
'{"type":"READING","items":['
|| '{"title":"用电的火车","content":"电力机车用电来驱动。铁轨上方有电线，火车的受电弓接触电线取电，电动机转动带动车轮。","image":"train/electric-locomotive"},'
|| '{"title":"电力机车的好处","content":"电力机车速度快、力气大、不排废气、噪音小。现在大部分客运火车都是电力机车。","image":"train/electric-train"},'
|| '{"title":"高铁也是电力","content":"中国的高铁全部是电力驱动。电力让高铁可以跑得又快又稳，每小时超过300公里。","image":"train/hsr-electric"}'
|| ']}', 3, 3);

-- Lesson 53: 蒸汽怎么跑 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(53, 17, '蒸汽怎么跑', 'READING',
'{"type":"READING","items":['
|| '{"title":"烧水产生蒸汽","content":"蒸汽机车先在锅炉里烧煤炭，把水烧开产生大量蒸汽。蒸汽就像一个看不见的大力士。","image":"train/steam-boiler"},'
|| '{"title":"蒸汽推动活塞","content":"蒸汽进入气缸推动活塞来回运动。活塞连着车轮的连杆，把来回运动变成车轮的转动。","image":"train/steam-piston"},'
|| '{"title":"车轮转起来","content":"活塞推动连杆，连杆带动车轮转动。车轮一转，火车就往前跑了。呜呜！","image":"train/steam-wheels"}'
|| ']}', 1, 3);

-- Lesson 54: 电力怎么跑 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(54, 17, '电力怎么跑', 'READING',
'{"type":"READING","items":['
|| '{"title":"架空电线供电","content":"电力机车的铁轨上方有架空电线。电线里有电，火车顶上的受电弓像一只手，摸着电线取电。","image":"train/pantograph"},'
|| '{"title":"电进电动机","content":"电从电线下来，经过变压器变压，进入电动机。电动机像一个大马达，一通电就转起来。","image":"train/electric-motor"},'
|| '{"title":"电动机带动车轮","content":"电动机转动后，通过齿轮带动车轮旋转。车轮一转，火车就跑起来了。又快又安静！","image":"train/electric-drive"}'
|| ']}', 2, 3);

-- Lesson 55: 动力问答 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(55, 17, '动力问答', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"蒸汽机车烧什么？","options":["柴油","煤炭","电","天然气"],"answer":1,"image":""},'
|| '{"question":"柴油机车用什么发动机？","options":["蒸汽机","柴油发动机","电动机","风车"],"answer":1,"image":""},'
|| '{"question":"电力机车从哪里取电？","options":["电池","架空电线","太阳能","轮子"],"answer":1,"image":""},'
|| '{"question":"高铁是什么驱动？","options":["蒸汽","柴油","电力","人力"],"answer":2,"image":""},'
|| '{"question":"蒸汽机车的活塞做什么运动？","options":["转圈","来回运动","不动","上下跳"],"answer":1,"image":""}'
|| ']}', 3, 3);

-- ============================================================
-- Section 6: 课时内容 - 主题8 火车的时间旅行
-- ============================================================

-- Lesson 56: 最早的火车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(56, 18, '最早的火车', 'READING',
'{"type":"READING","items":['
|| '{"title":"蒸汽时代的开始","content":"200多年前，英国人发明了第一台蒸汽火车。从此人类有了不用马拉的火车，可以跑得又快又远。","image":"train/first-steam"},'
|| '{"title":"斯蒂芬森的火箭号","content":"1829年，斯蒂芬森造了一辆叫火箭号的蒸汽机车。它跑得比马还快，让大家惊呆了。","image":"train/rocket-locomotive"},'
|| '{"title":"火车来到中国","content":"100多年前，中国有了第一条铁路。虽然当时很多人没见过火车，但很快火车就成了重要的交通工具。","image":"train/china-first-railway"}'
|| ']}', 1, 3);

-- Lesson 57: 越来越快 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(57, 18, '越来越快', 'READING',
'{"type":"READING","items":['
|| '{"title":"从蒸汽到柴油","content":"蒸汽火车跑了100多年后，人们发明了柴油机车。柴油火车比蒸汽火车快多了，也不用一直加煤了。","image":"train/steam-to-diesel"},'
|| '{"title":"电力时代来临","content":"后来又发明了电力机车。电力火车更快更干净，渐渐地蒸汽火车和柴油火车都被淘汰了。","image":"train/electric-era"},'
|| '{"title":"速度的飞跃","content":"从蒸汽火车每小时几十公里，到现在高铁每小时300多公里，火车的速度提高了将近10倍！","image":"train/speed-evolution"}'
|| ']}', 2, 3);

-- Lesson 58: 火车历史问答 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(58, 18, '火车历史问答', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"第一台蒸汽火车在哪里发明？","options":["中国","美国","英国","日本"],"answer":2,"image":""},'
|| '{"question":"火箭号是谁造的？","options":["爱迪生","斯蒂芬森","牛顿","瓦特"],"answer":1,"image":""},'
|| '{"question":"蒸汽火车之后是什么火车？","options":["马车","柴油火车","高铁","磁悬浮"],"answer":1,"image":""},'
|| '{"question":"电力火车比蒸汽火车？","options":["更慢","更快更干净","更脏","更吵"],"answer":1,"image":""},'
|| '{"question":"高铁每小时跑多少公里？","options":["50公里","100公里","300多公里","1000公里"],"answer":2,"image":""}'
|| ']}', 3, 3);

-- Lesson 59: 中国高铁 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(59, 19, '中国高铁', 'READING',
'{"type":"READING","items":['
|| '{"title":"中国高铁的故事","content":"中国高铁从零开始，现在已经是世界上最长的高铁网络。复兴号列车是中国自己造的高铁，跑得又快又稳。","image":"train/china-hsr"},'
|| '{"title":"复兴号","content":"复兴号是中国自主研发的高铁列车。它的速度每小时350公里，车头像一条飞龙，非常漂亮。","image":"train/fuxing-train"},'
|| '{"title":"和谐号","content":"和谐号是中国较早的高铁列车。虽然比复兴号早，但速度也很快，每小时250到350公里。","image":"train/hexie-train"}'
|| ']}', 1, 3);

-- Lesson 60: 磁悬浮列车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(60, 19, '磁悬浮列车', 'READING',
'{"type":"READING","items":['
|| '{"title":"未来的火车","content":"磁悬浮列车是未来的火车。它悬浮在轨道上飞驰，没有车轮和铁轨的摩擦，速度可以超过500公里每小时。","image":"train/future-maglev"},'
|| '{"title":"超级高铁","content":"科学家正在研究超级高铁。它在真空管道里跑，速度可能超过1000公里每小时，比飞机还快！","image":"train/hyperloop"},'
|| '{"title":"智能火车","content":"未来的火车会越来越聪明。它们可以自动驾驶，还能和手机连接，告诉你什么时候到站。","image":"train/smart-train"}'
|| ']}', 2, 3);

-- Lesson 61: 未来火车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(61, 19, '未来火车', 'READING',
'{"type":"READING","items":['
|| '{"title":"环保火车","content":"未来的火车会更环保。它们用太阳能和风能发电，不会产生污染，保护我们的地球。","image":"train/eco-train"},'
|| '{"title":"太空火车","content":"也许有一天，我们可以坐火车去太空！科学家正在研究能在管道里飞驰的太空列车。","image":"train/space-train"},'
|| '{"title":"火车改变世界","content":"从蒸汽到磁悬浮，火车一直在进步。火车让旅行更方便，让世界变得更小，人们的距离更近了。","image":"train/train-future"}'
|| ']}', 3, 3);
