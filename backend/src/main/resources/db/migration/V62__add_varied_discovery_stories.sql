-- V62: 新增“奇想与发现”课外主题，包含十篇长短不一的儿童故事与配套问答。
-- 阅读篇幅为 4～9 页，叙事覆盖观察、实验、合作、音乐、家庭记忆与时间想象。

INSERT INTO theme (id, name, sort_order, is_locked, subject_id)
VALUES (50, '奇想与发现', 24, 0, 4);

INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(137, 50, '屋顶上的天气信', 1, 0),
(138, 50, '一厘米森林', 2, 0),
(139, 50, '停电后的星星地图', 3, 0),
(140, 50, '四种声音的音乐会', 4, 0),
(141, 50, '少一块也能出发', 5, 0),
(142, 50, '雨伞下的慢慢路', 6, 0),
(143, 50, '旧纽扣的旅行箱', 7, 0),
(144, 50, '影子借走了什么', 8, 0),
(145, 50, '种子没有按时发芽', 9, 0),
(146, 50, '给未来的第十封信', 10, 0);

INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(569, 137, '屋顶上的天气信', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"一封空白的信","content":"周老师带大家来到有护栏的屋顶气象角。她发下一张空白天气信，请每组不用天气预报，只把此刻亲眼发现的天空寄给楼下班级。","image":"varied-stories/weather-letter"},
    {"title":"风先开口","content":"彭泽宇看云，安安看风向袋。云像白色小岛慢慢向东，风向袋却忽高忽低；雨量器里还留着昨夜的浅浅水线。三种线索说的并不是同一件事。","image":"varied-stories/weather-letter"},
    {"title":"没有画太阳","content":"彭泽宇本想画一个大太阳，却发现阳光正从云缝里一会儿出现、一会儿躲开。他把信分成三格：云在走，风在变，雨已经停。","image":"varied-stories/weather-letter"},
    {"title":"天空收到回信","content":"十分钟后，楼下同学回信：操场旗子也在忽高忽低。彭泽宇抬头笑了。天气信没有猜明天，只认真写清了现在。","image":"varied-stories/weather-letter"}
  ]
}', 1, 3),
(570, 137, '天气观察问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"天气信记录的是什么？","options":["一百年后的天气","此刻亲眼观察到的天空","想象中的太阳颜色"],"answer":1,"image":"varied-stories/weather-letter"},
    {"question":"孩子们观察了哪些线索？","options":["云、风向袋和雨量器","课桌、书包和铅笔","汽车、火车和轮船"],"answer":0,"image":"varied-stories/weather-letter"},
    {"question":"故事最后强调了哪种观察方法？","options":["只画最喜欢的天气","不确定时随便猜","写清自己真正看到的现象"],"answer":2,"image":"varied-stories/weather-letter"}
  ]
}', 2, 3),

(571, 138, '一厘米森林', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"公园里最小的任务","content":"自然观察员请大家寻找一片不超过手掌大的森林。彭泽宇望着高高的香樟树，觉得这么小的森林一定不存在。","image":"varied-stories/tiny-forest"},
    {"title":"石头边的绿色绒毯","content":"一滴水从石头边落下。他蹲到木栈道上，看见一片只有一厘米高的苔藓，细细的茎挤在一起，像无数撑着绿伞的小树。","image":"varied-stories/tiny-forest"},
    {"title":"访客从叶下经过","content":"放大镜里，一只瓢虫从苔藓边缘绕过，蕨芽卷成小问号。彭泽宇把手收回来，只在观察本上画，不碰它们的家。","image":"varied-stories/tiny-forest"},
    {"title":"尺子量不到的热闹","content":"安安用尺子量出最高的苔藓不到两厘米，可他们数不清里面藏着多少水珠、细叶和弯弯的小路。小并不等于简单。","image":"varied-stories/tiny-forest"},
    {"title":"一场很慢的雨","content":"树叶上的水一滴一滴落下。对彭泽宇只是一阵小雨，对苔藓森林却像云朵轮流经过，每一滴都让绿色亮一下。","image":"varied-stories/tiny-forest"},
    {"title":"把脚步放轻","content":"离开时，他在记录卡上写下新的发现：要看见很小的世界，眼睛要靠近，脚步要放轻，时间也要慢一点。","image":"varied-stories/tiny-forest"}
  ]
}', 1, 3),
(572, 138, '微观自然问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"彭泽宇在哪里找到了小森林？","options":["高楼屋顶上","石头边的一片苔藓里","玩具盒里面"],"answer":1,"image":"varied-stories/tiny-forest"},
    {"question":"他为什么没有用手碰瓢虫和苔藓？","options":["害怕自己的手变绿","观察员不许他画画","想保护小生境，只观察记录"],"answer":2,"image":"varied-stories/tiny-forest"},
    {"question":"“小并不等于简单”是什么意思？","options":["小地方也可能有丰富细节","小东西一定比大东西容易","尺子不能测量任何东西"],"answer":0,"image":"varied-stories/tiny-forest"},
    {"question":"怎样更容易发现微小世界？","options":["大声奔跑","靠近观察并放慢速度","把植物全部带回家"],"answer":1,"image":"varied-stories/tiny-forest"}
  ]
}', 2, 3),

(573, 139, '停电后的星星地图', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"灯忽然熄灭","content":"晚饭后，小区突然停电。空调声、电视声和台灯一起消失，房间先黑了一瞬，应急灯随后亮起柔和的光。","image":"varied-stories/power-outage-stars"},
    {"title":"没有完成的模型","content":"彭泽宇正准备给火箭模型贴最后一片银纸。他叹气说今晚什么也做不了，爸爸却指向窗外：楼群少了灯，天空反而多出了星星。","image":"varied-stories/power-outage-stars"},
    {"title":"阳台门里的观察站","content":"爸爸检查好门窗，让彭泽宇留在阳台门内。他们关掉应急灯一分钟，让眼睛慢慢适应黑暗，不点蜡烛，也不独自外出。","image":"varied-stories/power-outage-stars"},
    {"title":"七颗不肯排直的星","content":"北边有七颗较亮的星，有的像勺口，有的像弯柄。彭泽宇伸手在空中连线，怎么也连不成自己熟悉的方方正正。","image":"varied-stories/power-outage-stars"},
    {"title":"地图允许画错","content":"他在黑纸上点下七个小点，第一次把两颗位置画反。爸爸没有替他改，只问哪颗离窗框更近。彭泽宇重新比较，贴上一枚新的星点。","image":"varied-stories/power-outage-stars"},
    {"title":"楼下也亮起一颗星","content":"对面楼的窗边亮起一支小手电，轻轻晃了两下便停住。彭泽宇没有对着窗外乱照，只在纸上补下第八颗“邻居星”。","image":"varied-stories/power-outage-stars"},
    {"title":"电回来了","content":"灯光突然恢复，真正的星星又淡了下去。火箭模型还在桌上等银纸，星星地图却已经铺满了他们原本没计划过的夜晚。","image":"varied-stories/power-outage-stars"},
    {"title":"黑暗不是空白","content":"彭泽宇在地图角落画下一盏关闭的台灯。他发现，少掉一种光时，眼睛可能会看见另一种光。","image":"varied-stories/power-outage-stars"}
  ]
}', 1, 3),
(574, 139, '夜空观察问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"停电后为什么能看见更多星星？","options":["星星突然飞近了","火箭模型发出了星光","周围灯光变少，暗处的星更明显"],"answer":2,"image":"varied-stories/power-outage-stars"},
    {"question":"父子怎样保证观察安全？","options":["点很多蜡烛","留在阳台门内并使用应急灯","独自到楼顶寻找星星"],"answer":1,"image":"varied-stories/power-outage-stars"},
    {"question":"地图画错时，爸爸怎样帮助彭泽宇？","options":["替他完成整张图","用问题引导他重新比较位置","把画纸收走"],"answer":1,"image":"varied-stories/power-outage-stars"},
    {"question":"“邻居星”实际上是什么？","options":["一颗新行星","应急灯的电池","对面窗边短暂亮起的手电"],"answer":2,"image":"varied-stories/power-outage-stars"},
    {"question":"这个夜晚让彭泽宇发现了什么？","options":["计划改变也可能带来意外发现","停电时什么都不能做","星图必须一次画对"],"answer":0,"image":"varied-stories/power-outage-stars"}
  ]
}', 2, 3),

(575, 140, '四种声音的音乐会', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"不能用乐器的音乐课","content":"音乐老师把鼓和铃放在一旁，请四人小组只用教室里的安全物品编一段声音。彭泽宇觉得没有乐器就不会有音乐。","image":"varied-stories/four-sounds"},
    {"title":"沙沙、笃笃、呼呼","content":"安安摇装着豆子的密封罐，小屿轻敲木块，朵朵对着纸筒轻轻吹气。彭泽宇拍两下手，四种声音都有了。","image":"varied-stories/four-sounds"},
    {"title":"第一次全都抢着说","content":"四个人同时加快速度，沙沙和笃笃挤成一团，谁也听不清谁。老师没有叫停，只问他们有没有给安静留下位置。","image":"varied-stories/four-sounds"},
    {"title":"把空白也放进音乐","content":"他们排成拍手、停一拍、沙沙、笃笃、呼呼。轮到谁，谁就发声；没轮到的人听。短短的停顿让每种声音都有了形状。","image":"varied-stories/four-sounds"},
    {"title":"教室里的小音乐会","content":"演出只有二十秒。最后一个“呼”消失后，大家安静了一拍才笑起来。彭泽宇明白，音乐不只由声音组成，也由认真听组成。","image":"varied-stories/four-sounds"}
  ]
}', 1, 3),
(576, 140, '声音创作问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"四人用了哪四种发声方式？","options":["钢琴、小提琴、长笛和鼓","汽车、飞机、轮船和火车","豆罐、木块、纸筒和拍手"],"answer":2,"image":"varied-stories/four-sounds"},
    {"question":"第一次合奏为什么听不清？","options":["所有人同时抢着发声","教室里没有灯","物品完全没有声音"],"answer":0,"image":"varied-stories/four-sounds"},
    {"question":"故事认为音乐还需要什么？","options":["昂贵乐器","认真倾听和停顿","越响越好"],"answer":1,"image":"varied-stories/four-sounds"}
  ]
}', 2, 3),

(577, 141, '少一块也能出发', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"桌上的社区地图","content":"班级要用大拼图规划周末步行路线。彭泽宇负责河边，安安负责公园，小屿负责从学校到图书馆的街道。","image":"varied-stories/missing-puzzle"},
    {"title":"蓝色缺口","content":"快拼完时，河流中央留下一个手掌大的洞。盒子、桌下和收纳柜都找过了，那块蓝色拼片仍不见踪影。","image":"varied-stories/missing-puzzle"},
    {"title":"三种不同的着急","content":"彭泽宇想从洞上直接画线，安安坚持找到原片，小屿担心活动无法开始。三个人都在说办法，却没有人先听完别人。","image":"varied-stories/missing-puzzle"},
    {"title":"轮流当一分钟队长","content":"他们把计时器调成一分钟。队长只能总结前一个人的想法，再提出自己的办法。说到第三轮，三个办法第一次同时留在桌上。","image":"varied-stories/missing-puzzle"},
    {"title":"透明的临时桥","content":"安安继续登记缺片，小屿查原图，彭泽宇把透明描图纸盖在缺口上。三人共同画出河岸和步行桥，既不假装原片找到了，也不让路线停下。","image":"varied-stories/missing-puzzle"},
    {"title":"原来在展示板后面","content":"活动结束后，老师移动展示板，蓝色拼片轻轻掉了出来。它没有被谁藏起，只是早上布置时滑进了缝里。","image":"varied-stories/missing-puzzle"},
    {"title":"缺口留下的新办法","content":"他们换回原片，却把透明补片保存在盒盖里。彭泽宇说，完整当然很好，可遇到缺口时，合作能先搭一座临时桥。","image":"varied-stories/missing-puzzle"}
  ]
}', 1, 3),
(578, 141, '合作解题问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"拼图出现了什么问题？","options":["所有拼片都变成红色","河流中间缺少一块拼片","地图被雨淋湿了"],"answer":1,"image":"varied-stories/missing-puzzle"},
    {"question":"一分钟队长首先要做什么？","options":["命令所有人听自己","把计时器关掉","总结前一个人的想法"],"answer":2,"image":"varied-stories/missing-puzzle"},
    {"question":"透明补片有什么作用？","options":["保留缺片事实，同时补出临时路线","让原拼片消失","证明某个人弄丢了拼片"],"answer":0,"image":"varied-stories/missing-puzzle"},
    {"question":"故事中的“临时桥”指什么？","options":["合作找到的暂时解决方案","河上的真实大桥","展示板后面的缝隙"],"answer":0,"image":"varied-stories/missing-puzzle"}
  ]
}', 2, 3),

(579, 142, '雨伞下的慢慢路', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"校门口的黄色雨伞","content":"放学时下起小雨。彭泽宇撑开黄色大伞，正准备追上前面的同学，发现一年级的可可站在廊下反复系松开的鞋带。","image":"varied-stories/slow-rain-walk"},
    {"title":"你需要我做什么","content":"彭泽宇没有直接拉她，也没有替她系鞋带，只问要不要一起走。可可点点头，说自己能系好，只是需要伞别走开。","image":"varied-stories/slow-rain-walk"},
    {"title":"伞下只能走慢一点","content":"两个人的步子一大一小。彭泽宇走快时，雨水就落到可可肩上；他只好把脚步调成水洼边的小格子，一格一格向前。","image":"varied-stories/slow-rain-walk"},
    {"title":"错过一场水洼比赛","content":"伙伴在前面比赛谁绕过的水洼更多。彭泽宇没有赶上，却听可可说起每个水洼里的倒影：有树、有云，还有一把倒过来的黄伞。","image":"varied-stories/slow-rain-walk"},
    {"title":"门廊下的交换","content":"到了教学楼，可可把一张画着小雨点的贴纸送给他。彭泽宇摇头说不用交换，陪人走一段不是一场买卖。","image":"varied-stories/slow-rain-walk"},
    {"title":"慢路也有自己的风景","content":"雨停时，伙伴们已经进门。彭泽宇收伞前回头看，水洼里的云正在散开。他错过了比赛，却没有错过这条慢路。","image":"varied-stories/slow-rain-walk"}
  ]
}', 1, 3),
(580, 142, '雨天同行问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"可可真正需要什么帮助？","options":["别人替她完成所有事情","有人催她跑快一点","有人撑伞等她自己系好鞋带"],"answer":2,"image":"varied-stories/slow-rain-walk"},
    {"question":"彭泽宇怎样配合可可的步子？","options":["让她独自淋雨","放慢速度一起走","拉着她跨过水洼"],"answer":1,"image":"varied-stories/slow-rain-walk"},
    {"question":"他虽然错过比赛，却发现了什么？","options":["水洼倒影和慢路的风景","一辆新汽车","会说话的雨伞"],"answer":0,"image":"varied-stories/slow-rain-walk"},
    {"question":"为什么彭泽宇没有收下交换礼物？","options":["他讨厌所有贴纸","贴纸被雨冲走了","帮助不一定需要回报"],"answer":2,"image":"varied-stories/slow-rain-walk"}
  ]
}', 2, 3),

(581, 143, '旧纽扣的旅行箱', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"木盒里的小东西","content":"奶奶整理衣柜时取出一只木盒。里面没有珠宝，只有七颗纽扣、两张旧车票、几块布和一张边角卷起的黑白照片。","image":"varied-stories/button-suitcase"},
    {"title":"最普通的灰纽扣","content":"彭泽宇先拿起一颗灰纽扣，觉得它最不起眼。奶奶却说，它曾缝在爷爷小时候的校服上，陪他坐过每天清晨的第一班公交车。","image":"varied-stories/button-suitcase"},
    {"title":"车票上的两个小洞","content":"旧车票没有清楚的字，只留下两个剪票孔。奶奶记得那趟车经过石桥和粮店，却记不清站名。记忆有时也会缺一小块。","image":"varied-stories/button-suitcase"},
    {"title":"红布没有变成裙子","content":"一块红布原本要做连衣裙，后来赶上搬家，只做成一只装针线的小袋。奶奶笑着说，没有完成原计划的布，也有了别的用处。","image":"varied-stories/button-suitcase"},
    {"title":"蓝纽扣来自陌生外套","content":"一颗蓝纽扣让全家猜了很久。爸爸说是雨衣，妈妈说是书包，奶奶最后也摇头。不是每件旧物都一定有完整答案。","image":"varied-stories/button-suitcase"},
    {"title":"把不知道也记下来","content":"彭泽宇画了七个圆圈：知道的写成小故事，不确定的画问号，完全不知道的留白。他第一次觉得留白不是做错了。","image":"varied-stories/button-suitcase"},
    {"title":"一颗今天的纽扣","content":"妈妈找来一颗从彭泽宇旧外套上掉下的绿色纽扣。他记得穿那件外套第一次坐高铁，窗外田野快得像绿色的水。","image":"varied-stories/button-suitcase"},
    {"title":"旅行箱越来越轻","content":"他们没有把木盒塞满，只添了一张今天的合影和一句由爸爸代写的日期。小盒子很轻，装进去的时间却从爷爷小时候走到了今天。","image":"varied-stories/button-suitcase"},
    {"title":"故事会继续往里装","content":"彭泽宇合上木盒，把灰纽扣放在最上面。最普通的小东西也可能是一扇门，打开以后，家人的旧日子就会走出来。","image":"varied-stories/button-suitcase"}
  ]
}', 1, 3),
(582, 143, '家庭旧物问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"灰纽扣曾经在哪里？","options":["一顶王冠上","爷爷小时候的校服上","火箭模型上"],"answer":1,"image":"varied-stories/button-suitcase"},
    {"question":"红布后来做成了什么？","options":["大窗帘","校车座位","针线小袋"],"answer":2,"image":"varied-stories/button-suitcase"},
    {"question":"蓝纽扣的来历为什么留白？","options":["家人都不能确定","纽扣没有颜色","彭泽宇不愿意记录"],"answer":0,"image":"varied-stories/button-suitcase"},
    {"question":"彭泽宇往木盒里新放了什么？","options":["自己的绿色纽扣和今天的合影","一整套新衣服","昂贵的珠宝"],"answer":0,"image":"varied-stories/button-suitcase"},
    {"question":"故事怎样看待不完整的记忆？","options":["可以诚实保留不知道和留白","必须编出完整答案","不完整就没有价值"],"answer":0,"image":"varied-stories/button-suitcase"}
  ]
}', 2, 3),

(583, 144, '影子借走了什么', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"少了一只耳朵的影子","content":"午后的科学角，彭泽宇把纸兔放到台灯前。墙上的兔影只有一只耳朵，他翻遍桌面，也没找到掉下来的纸片。","image":"varied-stories/shadow-mystery"},
    {"title":"安安说影子借走了","content":"安安开玩笑说影子把耳朵借去玩了。彭泽宇没有马上相信，也没有马上反对，他们决定一次只改变一个地方。","image":"varied-stories/shadow-mystery"},
    {"title":"先转兔子，再移灯","content":"纸兔转半圈，两只耳朵都出现了；灯往右移，一只耳朵又躲到另一只后面。纸兔没有少，影子只是把重叠画在墙上。","image":"varied-stories/shadow-mystery"},
    {"title":"一张三格证据图","content":"他们画下灯、纸兔和墙的三个位置，把每次出现的影子贴在旁边。笑话变成了一个可以试验的问题。","image":"varied-stories/shadow-mystery"},
    {"title":"影子什么也没借","content":"离开前，彭泽宇把两只纸耳朵摸了一遍。影子没有拿走东西，却借给他们一个好问题：光从哪里来，形状就怎样落下。","image":"varied-stories/shadow-mystery"}
  ]
}', 1, 3),
(584, 144, '光影实验问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"兔影为什么看起来少了一只耳朵？","options":["纸兔真的少了耳朵","墙把耳朵吃掉了","两只耳朵的影子重叠了"],"answer":2,"image":"varied-stories/shadow-mystery"},
    {"question":"孩子们怎样进行实验？","options":["同时乱动所有物品","一次只改变一个位置","只用猜测不观察"],"answer":1,"image":"varied-stories/shadow-mystery"},
    {"question":"三格证据图记录了什么？","options":["灯、纸兔、墙和影子的关系","三只不同的兔子","教室里的全部家具"],"answer":0,"image":"varied-stories/shadow-mystery"},
    {"question":"故事中的玩笑最后变成了什么？","options":["可以试验的科学问题","真正的魔法","一场争吵"],"answer":0,"image":"varied-stories/shadow-mystery"}
  ]
}', 2, 3),

(585, 145, '种子没有按时发芽', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"三个写着日期的花盆","content":"班级种下三颗同样的菜豆。彭泽宇在花盆旁画好日期，猜第三天会同时冒出三棵绿色小苗。","image":"varied-stories/late-seed"},
    {"title":"第三天只有两顶绿帽子","content":"安安和小屿的花盆都裂开泥土，彭泽宇的仍然安静。他把耳朵贴近花盆，当然什么也没听见。","image":"varied-stories/late-seed"},
    {"title":"差点多浇一杯水","content":"他端起水杯，想让种子快一点。科学老师请他先摸土：里面仍湿润，再浇水可能让种子缺少空气。等待不等于不停加东西。","image":"varied-stories/late-seed"},
    {"title":"把失败改成记录","content":"彭泽宇写下光线、土壤湿度和每天的变化。第五天仍没有芽，他允许自己失望，也决定先不把花盆倒开。","image":"varied-stories/late-seed"},
    {"title":"第六天的细裂缝","content":"早晨，泥土中央出现一道像头发丝的裂缝。到下午，一段弯弯的浅绿茎顶着豆皮，慢慢抬起头。","image":"varied-stories/late-seed"},
    {"title":"三棵苗不需要齐步走","content":"三棵菜豆高度不同，叶子展开的时间也不同。彭泽宇把原来的“第三天一定发芽”改成“每天观察一次”。","image":"varied-stories/late-seed"},
    {"title":"给不知道留一点时间","content":"并非每颗种子都会发芽，观察也不能保证结果。但这一次，彭泽宇学会在答案出现前照顾、记录，再多等一会儿。","image":"varied-stories/late-seed"}
  ]
}', 1, 3),
(586, 145, '种植观察问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"第三天发生了什么？","options":["三个花盆同时开花","只有两个花盆发芽","所有种子都消失了"],"answer":1,"image":"varied-stories/late-seed"},
    {"question":"为什么不能立刻多浇一杯水？","options":["水杯颜色不对","植物永远不需要水","土壤仍湿，过多水会影响空气"],"answer":2,"image":"varied-stories/late-seed"},
    {"question":"等待期间彭泽宇做了什么？","options":["记录光线、湿度和变化","每天挖开种子","把花盆藏起来"],"answer":0,"image":"varied-stories/late-seed"},
    {"question":"故事告诉我们怎样面对暂时没有答案？","options":["不断增加操作","继续合理照顾和观察","马上编一个结果"],"answer":1,"image":"varied-stories/late-seed"}
  ]
}', 2, 3),

(587, 146, '给未来的第十封信', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"九封已经写好的信","content":"一年级结束前，周老师请每个孩子写十封给未来的信。前九封分别装着最喜欢的书、最难忘的雨、一次道歉和一个新朋友，第十个信封却一直空着。","image":"varied-stories/future-letter"},
    {"title":"未来喜欢什么","content":"彭泽宇想问长大后的自己会不会开火车、会不会住在月球、还喜不喜欢蓝色。他写了满满一页，又觉得这些问题像别人也会问。","image":"varied-stories/future-letter"},
    {"title":"操场边的旧时间盒","content":"班级把信放进木制时间盒，约定三年后打开。安安已经封好第十封，小屿在画信封，只有彭泽宇的空信封被风轻轻吹起。","image":"varied-stories/future-letter"},
    {"title":"去找今天","content":"周老师说，写给未来不一定要预测未来，也可以把今天保存下来。彭泽宇带着空信封绕校园走一圈，寻找只有今天才有的东西。","image":"varied-stories/future-letter"},
    {"title":"半截粉笔和一片树影","content":"黑板槽里有半截淡蓝粉笔，操场香樟树的影子刚好碰到第三块地砖。食堂窗口飘来玉米香，远处有人练习一首总弹错第三小节的曲子。","image":"varied-stories/future-letter"},
    {"title":"不能装进去的声音","content":"信封装不下树影和音乐。他便用句子记住它们，还画下自己的鞋带结和课桌角那道小划痕。普通的今天突然变得忙碌起来。","image":"varied-stories/future-letter"},
    {"title":"第十封没有问题","content":"彭泽宇没有再问未来会成为什么。他写道：今天我七岁，跑步还会喘，故事读到一半会着急，但我已经能自己修好纸车轮，也会停下来等朋友。","image":"varied-stories/future-letter"},
    {"title":"封口前的一句补充","content":"他在最后添上一句：三年后的我，如果你忘了今天，也没关系，请看看窗外有没有一片正在移动的树影。","image":"varied-stories/future-letter"},
    {"title":"时间盒关上以后","content":"十只信封一起躺进盒子。盖子合上，没有钟声，也没有光。彭泽宇却知道，未来不是盒子里的秘密；从明天开始的每一天，都会慢慢走到那里。","image":"varied-stories/future-letter"}
  ]
}', 1, 3),
(588, 146, '时间信箱问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"第十封信为什么迟迟没有写？","options":["信封被别人拿走了","老师不允许写字","彭泽宇不知道怎样写得像自己"],"answer":2,"image":"varied-stories/future-letter"},
    {"question":"周老师建议他保存什么？","options":["对未来的准确预言","只有今天才有的细节","昂贵的纪念品"],"answer":1,"image":"varied-stories/future-letter"},
    {"question":"哪些东西不能直接装进信封？","options":["树影、音乐和气味","纸张和图画","半截粉笔"],"answer":0,"image":"varied-stories/future-letter"},
    {"question":"第十封信主要写了什么？","options":["三年后的考试答案","去月球的路线","七岁的自己正在经历和学会的事"],"answer":2,"image":"varied-stories/future-letter"},
    {"question":"结尾怎样理解未来？","options":["未来只藏在时间盒里","每一天会一步步走向未来","未来可以一次猜准"],"answer":1,"image":"varied-stories/future-letter"}
  ]
}', 2, 3);
