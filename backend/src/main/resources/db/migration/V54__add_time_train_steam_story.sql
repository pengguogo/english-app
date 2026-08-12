-- V54: 时空列车五部曲之一——1829 年英国蒸汽时代
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(116, 5, '时空列车1：蒸汽奇迹', 15, 0);

INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(527, 116, '雾夜里的蒸汽奇迹', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"会发光的旧车票","content":"夜里，彭泽宇读着火车书睡着了。一张旧车票忽然发出金光，怀表指针倒转到 1829 年。他睁开眼，发现自己站在英国雨山试车会旁，空气里有煤烟味。工程师告诉他：train 是火车，steam 是蒸汽。远处的火箭号正准备接受考验。","image":"story/time-train-steam-01"},
    {"title":"不是第一台，却很重要","content":"工程师解释，早在 1804 年，特里维西克的蒸汽机车就拉过货物；火箭号不是第一台蒸汽机车，但它把多管锅炉和喷气管等设计组合得更好。彭泽宇在记录板上写下 1804 和 1829，算出两者相差 25 年。创新常常是在前人经验上继续改进。","image":"story/time-train-steam-02"},
    {"title":"煤、水和蒸汽","content":"锅炉需要煤产生热量，再把水变成蒸汽。彭泽宇帮忙清点补给：4 筐煤加上 3 筐煤，一共有 7 筐。他学会 coal 是煤，water 是水。工程师提醒大家，锅炉很烫，只有受过训练的成年人才能操作，孩子必须站在安全线外。","image":"story/time-train-steam-03"},
    {"title":"十次往返的挑战","content":"雨山试车要求机车在约 1.5 英里的试验线上完成 10 次往返，用来模拟更长的旅程。彭泽宇把每次往返画成一个圆圈，每完成一次就涂亮一个。他发现还剩 3 个圆圈时，火箭号已经完成了 7 次。keep going 的意思是继续前进。","image":"story/time-train-steam-04"},
    {"title":"压力表的提醒","content":"第八次试跑前，压力表指针升得太快。彭泽宇没有碰设备，而是马上喊 Report the problem，并请工程师检查。工程师调小火力、确认安全阀工作正常。等指针回到安全范围后才继续。看到异常先报告，比急着完成比赛更重要。","image":"story/time-train-steam-05"},
    {"title":"铁轨上的小石块","content":"清晨的雾里，彭泽宇发现前方铁轨旁有几块松动的小石头。他举起红旗报告，工作人员封锁线路并清理障碍。红色表示 stop，绿色表示 go。线路重新检查两遍，绿色信号亮起后，火箭号才再次出发。安全规则在每个时代都一样重要。","image":"story/time-train-steam-06"},
    {"title":"火箭号完成试车","content":"火箭号平稳完成挑战，围观的人群欢呼起来。工程师说，它的成功让更多人相信蒸汽机车能够运送旅客和货物。彭泽宇数着 5 节车厢，每节坐 6 位体验者，一共能坐 30 人。他明白，速度、效率和安全必须一起进步。","image":"story/time-train-steam-07"},
    {"title":"下一站：新的动力","content":"旧车票再次发光，怀表上出现 engine、fuel 和一个新的年份。工程师送给彭泽宇一枚小齿轮，告诉他未来的机车会使用不同动力。蒸汽白雾变成金色时空隧道，彭泽宇挥手告别 1829 年，踏上下一段铁路历史。","image":"story/time-train-steam-08"}
  ]
}', 1, 3),
(528, 116, '蒸汽奇迹问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"火箭号为什么在铁路历史上很重要？","options":["它是世界上第一辆有轮子的车","它把多种有效设计组合起来，推动蒸汽机车成熟","它不需要煤和水"],"answer":1,"image":"story/time-train-steam-quiz"},
    {"question":"压力表指针异常时，彭泽宇首先做了什么？","options":["自己拆开锅炉","假装没有看见","立即报告工程师"],"answer":2,"image":"story/time-train-steam-quiz"},
    {"question":"英语单词 steam 表示什么？","options":["蒸汽","车站","车票"],"answer":0,"image":"story/time-train-steam-quiz"},
    {"question":"看到红色信号时，哪一句英语指令正确？","options":["Go!","Stop!","Fast!"],"answer":1,"image":"story/time-train-steam-quiz"},
    {"question":"4 筐煤加上 3 筐煤，一共有多少筐？","options":["6 筐","7 筐","8 筐"],"answer":1,"image":"story/time-train-steam-quiz"},
    {"question":"火箭号要完成 10 次往返，已经完成 7 次，还剩几次？","options":["2 次","3 次","4 次"],"answer":1,"image":"story/time-train-steam-quiz"},
    {"question":"为什么孩子不能靠近正在工作的蒸汽锅炉？","options":["锅炉很烫且有压力，需要专业人员操作","锅炉会把车票吹走","锅炉旁不能说英语"],"answer":0,"image":"story/time-train-steam-quiz"},
    {"question":"这篇故事最想告诉我们什么？","options":["比赛比安全重要","创新需要学习前人经验并认真检查","速度越快越不需要规则"],"answer":1,"image":"story/time-train-steam-quiz"}
  ]
}', 2, 3);
