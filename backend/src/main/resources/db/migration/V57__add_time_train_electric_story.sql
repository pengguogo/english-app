-- V57: 时空列车五部曲之四——铁路电气化
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(119, 5, '时空列车4：闪电轨道', 18, 0);

INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(533, 119, '闪电轨道上的电力机车', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"看不见的动力","content":"彭泽宇来到一座电气化山区车站。银色电力机车头顶升起受电弓，轻轻接触架空电线。工程师解释，electricity 是电，pantograph 是受电弓。电能通过接触网进入机车，再驱动电动机，不需要在车上燃烧煤或柴油。","image":"story/time-train-electric-01"},
    {"title":"三座变电站","content":"线路沿途有 3 座变电站，每座负责 10 千米区间，合起来服务 30 千米铁路。彭泽宇把 10+10+10 写在检查板上。工程师提醒，高压电非常危险，只有断电、验电并确认接地后，专业人员才能靠近设备。","image":"story/time-train-electric-02"},
    {"title":"受电弓要升还是降","content":"发车前，司机按顺序检查：车门关闭、信号开放、受电弓升起、电压正常。up 表示向上，down 表示向下。若进入没有架空线的检修区，受电弓必须先降下。彭泽宇发现，正确顺序能避免遗漏关键步骤。","image":"story/time-train-electric-03"},
    {"title":"电压突然下降","content":"列车行至第二供电区时，仪表电压下降。司机没有强行加速，而是切断牵引并报告控制中心。控制员发现一段接触网被大风吹来的塑料布挂住，于是封锁区间、远程断电，再派检修人员处理。Power off 表示断电。","image":"story/time-train-electric-04"},
    {"title":"安全的五个步骤","content":"检修人员依次完成五步：封锁线路、切断电源、确认无电、连接接地线、清除异物。彭泽宇把步骤卡从 1 排到 5，任何一步都不能跳过。清除塑料布后，他们还检查接触线是否损伤，确认合格才撤除接地线。","image":"story/time-train-electric-05"},
    {"title":"下坡回收能量","content":"恢复供电后，列车驶上山顶。下坡时，现代电力机车能在制动过程中把一部分运动能量转回电能。显示屏记录第一段回收 6 格、第二段回收 4 格，共 10 格。save energy 表示节约能源，但安全制动仍是第一目标。","image":"story/time-train-electric-06"},
    {"title":"安静穿过山谷","content":"电力机车平稳通过山谷，站台旁的人听不到蒸汽机车的排气声，也看不到柴油机车的烟。彭泽宇知道，发电过程可能在远方，所以节能仍需要整个系统共同努力。clean、quiet 分别表示清洁和安静。","image":"story/time-train-electric-07"},
    {"title":"通往高速时代的电光","content":"列车到站后，控制中心把一张线路图送给彭泽宇。图上许多列车按分钟运行，电气化为更快、更密集的铁路服务提供了基础。怀表忽然显示 1964，银色电光组成一辆流线型高速列车，邀请他前往最后一站。","image":"story/time-train-electric-08"}
  ]
}', 1, 3),
(534, 119, '闪电轨道问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"电力机车怎样从架空电线获得电能？","options":["通过受电弓接触电线","通过烟囱吸收闪电","通过车轮收集雨水"],"answer":0,"image":"story/time-train-electric-quiz"},
    {"question":"接触网挂上异物后，工作人员为什么先断电和接地？","options":["让塑料布变轻","防止高压电伤害检修人员","让列车开得更快"],"answer":1,"image":"story/time-train-electric-quiz"},
    {"question":"pantograph 在故事中是什么？","options":["受电弓","压力表","货运车"],"answer":0,"image":"story/time-train-electric-quiz"},
    {"question":"Power off 表示什么？","options":["升起受电弓","打开车门","切断电源"],"answer":2,"image":"story/time-train-electric-quiz"},
    {"question":"3 座变电站各负责 10 千米，共负责多少千米？","options":["20 千米","30 千米","40 千米"],"answer":1,"image":"story/time-train-electric-quiz"},
    {"question":"两段制动分别回收 6 格和 4 格能量，共回收多少格？","options":["9 格","10 格","11 格"],"answer":1,"image":"story/time-train-electric-quiz"},
    {"question":"高压设备检修时，哪种做法正确？","options":["只要戴帽子就能直接触碰","由专业人员按断电、验电、接地等步骤操作","让儿童帮忙拉电线"],"answer":1,"image":"story/time-train-electric-quiz"},
    {"question":"电气化铁路给彭泽宇的启示是什么？","options":["看不见的能量也需要清楚规则和系统协作","电力不会有危险","有电就不需要信号"],"answer":0,"image":"story/time-train-electric-quiz"}
  ]
}', 2, 3);
