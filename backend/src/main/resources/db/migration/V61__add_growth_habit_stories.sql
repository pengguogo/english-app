-- V61: 新增“成长好习惯”课外主题，包含十篇 5～8 岁现实成长故事与配套问答。
-- 每篇故事以温和的自然后果推动主角发现影响、主动补救并形成可执行方法。

INSERT INTO theme (id, name, sort_order, is_locked, subject_id)
VALUES (49, '成长好习惯', 23, 0, 4);

INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(127, 49, '没有排队的小队长', 1, 0),
(128, 49, '渐渐变凉的早餐', 2, 0),
(129, 49, '来不及试跑的小车', 3, 0),
(130, 49, '走不动的寻宝队员', 4, 0),
(131, 49, '彩虹菜园的空白味道', 5, 0),
(132, 49, '消失的图书馆徽章', 6, 0),
(133, 49, '打哈欠的观察员', 7, 0),
(134, 49, '牙齿上的彩色警报', 8, 0),
(135, 49, '被平板挤走的搭建时间', 9, 0),
(136, 49, '谁弄坏了纸箱城堡', 10, 0);

INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(549, 127, '没有排队的小队长', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"胸前的小队长徽章","content":"星期五，彭泽宇和同学们来到城市科技馆。陈老师把蓝色徽章别在他胸前，请他带大家看清标志、一起行动。彭泽宇早就盯上机器人搬运站，恨不得立刻冲过去。","image":"growth-tasks/public-rules"},
    {"title":"等不及的绿按钮","content":"体验区地面有黄色等待线，墙上提示大家站在线后、听完讲解、轮到后再按按钮。彭泽宇绕过队伍提前启动机械臂，三个还没摆好的货箱被送进错误格子。","image":"growth-tasks/public-rules"},
    {"title":"重新排到队尾","content":"工作人员停止设备并重新设置任务，小组只好退出操作区，再次排到队尾。彭泽宇看见后来的人都站在线后，队伍虽然不快，却一直有序向前。","image":"growth-tasks/public-rules"},
    {"title":"徽章忽然变重了","content":"陈老师问他刚才是否真的更快。彭泽宇看着工作人员复位机械臂，又看见陪他等待的伙伴，终于发现自己的着急让所有人等得更久。","image":"growth-tasks/public-rules"},
    {"title":"把任务摆回来","content":"彭泽宇向伙伴道歉。在工作人员确认设备安全后，他协助把红、蓝、黄货箱放回起点，再退回黄色线后，把三条规则完整读给大家听。","image":"growth-tasks/public-rules"},
    {"title":"真正的小队长","content":"再次进场时，彭泽宇请排在最前面的安安先操作。轮到自己，他先检查货箱，再看指示灯，最后按下按钮。三个货箱稳稳到达正确位置。","image":"growth-tasks/public-rules"},
    {"title":"给行动的口诀","content":"离开科技馆时，彭泽宇扶正徽章，清楚地说：先看标志，听完规则，轮到我再行动。伙伴们跟着念了一遍，队伍整整齐齐走向出口。","image":"growth-tasks/public-rules"}
  ]
}', 1, 3),
(550, 127, '公共规则问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"彭泽宇为什么让全组等得更久？","options":["他提前启动设备，任务需要重新设置","科技馆突然闭馆","伙伴们不想体验机器人"],"answer":0,"image":"growth-tasks/public-rules"},
    {"question":"等待操作时应该站在哪里？","options":["机械臂旁边","黄色等待线后","货箱上面"],"answer":1,"image":"growth-tasks/public-rules"},
    {"question":"彭泽宇怎样弥补自己的错误？","options":["假装没看见","责怪工作人员","道歉并协助恢复货箱"],"answer":2,"image":"growth-tasks/public-rules"},
    {"question":"第二次轮到彭泽宇时，他先做了什么？","options":["检查货箱和指示灯","闭上眼睛按按钮","跑到队伍最前面"],"answer":0,"image":"growth-tasks/public-rules"},
    {"question":"公共规则能保护什么？","options":["只保护小队长的徽章","大家的安全、时间和公平","让队伍永远不移动"],"answer":1,"image":"growth-tasks/public-rules"}
  ]
}', 2, 3),

(551, 128, '渐渐变凉的早餐', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"会冒热气的早晨","content":"爸爸把燕麦粥、鸡蛋、香蕉和烤面包摆上桌，告诉彭泽宇可以按胃口选择吃多少，但吃饭时间要留在餐桌上。客厅里的玩具小火车还装着没送完的积木。","image":"growth-tasks/mealtime-focus"},
    {"title":"一次次离开餐桌","content":"彭泽宇喝一口牛奶就跑去送红积木，咬一口面包又去找蓝积木。爸爸提醒小火车可以先停一会儿，他却总说马上就好。","image":"growth-tasks/mealtime-focus"},
    {"title":"热气不见了","content":"分针走过一格又一格，粥不再冒热气，面包也失去了刚出炉时的脆响。离出门只剩十分钟，他还要整理水壶和书包。","image":"growth-tasks/mealtime-focus"},
    {"title":"被挤走的选书时间","content":"彭泽宇原本想比较三本故事书，刚伸手，出门计时器就响了。爸爸没有收走选书时间，是他一次次离开餐桌，把这段时间一点点挤没了。","image":"growth-tasks/mealtime-focus"},
    {"title":"玩具停车位","content":"晚上，彭泽宇用空鞋盒做了餐前玩具停车场。小火车、车厢和积木都停进盒子，放到餐桌看不见的矮柜上，等吃完饭再发车。","image":"growth-tasks/mealtime-focus"},
    {"title":"一顿完整的早餐","content":"第二天，小火车安静停车。彭泽宇尝到热粥的软、香蕉的甜和面包的脆，吃到肚子舒服便停下。距离出门还有七分钟，他认真选好了故事书。","image":"growth-tasks/mealtime-focus"},
    {"title":"给餐桌的口诀","content":"早餐、选书和火车运输都赶上了。彭泽宇推着车厢说：吃饭时只吃饭，结束后再痛快玩。","image":"growth-tasks/mealtime-focus"}
  ]
}', 1, 3),
(552, 128, '专心吃饭问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"早餐为什么渐渐变凉？","options":["彭泽宇不断离开餐桌玩火车","爸爸没有准备早餐","窗外正在下雪"],"answer":0,"image":"growth-tasks/mealtime-focus"},
    {"question":"什么时间被拖延一起挤走了？","options":["睡觉时间","选故事书的时间","周末时间"],"answer":1,"image":"growth-tasks/mealtime-focus"},
    {"question":"故事中的用餐要求是什么？","options":["必须吃完所有食物","只能吃香蕉","按胃口选择，吃饭时留在餐桌"],"answer":2,"image":"growth-tasks/mealtime-focus"},
    {"question":"玩具停车场有什么作用？","options":["让玩具暂时离开视线，减少分心","把玩具永远收走","让火车自己吃早餐"],"answer":0,"image":"growth-tasks/mealtime-focus"},
    {"question":"第二天彭泽宇为什么有时间选书？","options":["他没有上学","他专心完成了早餐","爸爸替他吃了早餐"],"answer":1,"image":"growth-tasks/mealtime-focus"}
  ]
}', 2, 3),

(553, 129, '来不及试跑的小车', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"七个空格的任务卡","content":"周老师宣布星期五举行纸板小车展示，并发下包含设计、找材料、制作、装车轮、试跑、调整和展示的任务卡。彭泽宇画出漂亮的闪电号，觉得做起来一定很快。","image":"growth-tasks/task-planning"},
    {"title":"明天再开始","content":"周一看漫画，周二搭车站，周三仍说来得及。周四晚上，任务卡还是一片空白，他才发现家里只有三个一样的瓶盖车轮。","image":"growth-tasks/task-planning"},
    {"title":"忙乱的闪电号","content":"彭泽宇匆忙剪纸板、贴车身、装车轴。爸爸建议先试跑，他看看已经很晚的时钟，觉得四个轮子都会转就一定没问题。","image":"growth-tasks/task-planning"},
    {"title":"斜坡上的小意外","content":"展示时，闪电号刚冲出一小段，右后轮便滚到旁边，车身歪倒在坡道中央。后面的同学需要等待老师清理坡道。","image":"growth-tasks/task-planning"},
    {"title":"做完以后还要检查","content":"安安捡回轮子，没有嘲笑他。周老师提醒，制作让东西出现，检查让东西可靠。彭泽宇向等待的同学道歉，把小车带到维修桌。","image":"growth-tasks/task-planning"},
    {"title":"三步重新出发","content":"彭泽宇换上大小相同的轮子，固定车轴，先在短斜坡试跑，再检查四个轮子，最后回到展示区。闪电号第二次平稳越过终点线。","image":"growth-tasks/task-planning"},
    {"title":"从今天做一小步","content":"回家后，他把新任务拆成今天设计、明天制作、后天试跑，并在第一个空格画上小脚印：先做一小步，明天少着急。","image":"growth-tasks/task-planning"}
  ]
}', 1, 3),
(554, 129, '任务计划问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"闪电号第一次展示为什么失败？","options":["轮子和车轴没有经过试跑检查","斜坡上没有终点","小车颜色不够亮"],"answer":0,"image":"growth-tasks/task-planning"},
    {"question":"拖到最后一晚还带来了什么问题？","options":["材料和检查时间都不够","纸板自动消失","老师取消了活动"],"answer":0,"image":"growth-tasks/task-planning"},
    {"question":"发现小车挡住坡道后，彭泽宇怎么做？","options":["把小车留在原地","向同学道歉并去维修","责怪安安"],"answer":1,"image":"growth-tasks/task-planning"},
    {"question":"第二次出发前的正确顺序是什么？","options":["展示、制作、设计","换轮子、试跑、检查","先比赛、再找材料"],"answer":1,"image":"growth-tasks/task-planning"},
    {"question":"怎样减少任务最后一天的着急？","options":["把任务拆成小步骤提前开始","等别人替自己完成","不看任务要求"],"answer":0,"image":"growth-tasks/task-planning"}
  ]
}', 2, 3),

(555, 130, '走不动的寻宝队员', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"公园寻宝邀请","content":"社区邀请孩子们沿安全路线寻找五枚动物印章。伙伴们提前用追球、跳格子和骑车做短时间练习。彭泽宇喜欢寻宝，却担心自己运动时落在最后。","image":"growth-tasks/active-play"},
    {"title":"坐着看别人动","content":"伙伴邀请他踢球、跳格子或散步，他连续几天都坐着看动画，说自己不擅长运动。没有人要求他得第一，他却一次也没有试。","image":"growth-tasks/active-play"},
    {"title":"第一枚印章后的休息","content":"寻宝当天，彭泽宇跑上小坡后呼吸变急、腿也发酸。带队的林叔叔让他到树荫下喝水休息，他因此错过了前面一段探索路线。","image":"growth-tasks/active-play"},
    {"title":"身体要慢慢准备","content":"林叔叔告诉他寻宝不要求最快，经常活动才能让身体陪自己走得更远。他让彭泽宇自己选择喜欢的玩法，彭泽宇决定先追黄色软球。","image":"growth-tasks/active-play"},
    {"title":"找到喜欢的运动","content":"接下来一周，他每天活动十分钟：雨天在家踏步，晴天追球，周末骑车。累了就停下喝水，他逐渐期待每天选择一种新玩法。","image":"growth-tasks/active-play"},
    {"title":"再走一次寻宝路线","content":"第二个周末，彭泽宇和爸爸重走路线。他不再猛冲，而是走一段、看路标、再走一段，终于到达树顶观察台，看见正在敲树干的啄木鸟。","image":"growth-tasks/active-play"},
    {"title":"每天动一点","content":"回程时他仍会累，也知道坐下休息、喝水再出发。他举起新印章说：不必样样第一，每天动一动就有进步。","image":"growth-tasks/active-play"}
  ]
}', 1, 3),
(556, 130, '运动习惯问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"彭泽宇一开始为什么不愿活动？","options":["担心自己落在最后","公园不允许运动","伙伴没有邀请他"],"answer":0,"image":"growth-tasks/active-play"},
    {"question":"身体出现什么信号时应该休息？","options":["呼吸变急、腿发酸","找到一枚印章","看见一棵树"],"answer":0,"image":"growth-tasks/active-play"},
    {"question":"林叔叔让彭泽宇怎样开始？","options":["必须参加比赛","选择喜欢的玩法慢慢练习","每天只看别人运动"],"answer":1,"image":"growth-tasks/active-play"},
    {"question":"下列哪项是彭泽宇尝试过的活动？","options":["追球、踏步和骑车","驾驶飞机","举起大石头"],"answer":0,"image":"growth-tasks/active-play"},
    {"question":"故事怎样看待运动进步？","options":["每天活动一点并找到乐趣","只有第一名才算运动","越累越不能休息"],"answer":0,"image":"growth-tasks/active-play"}
  ]
}', 2, 3),

(557, 131, '彩虹菜园的空白味道', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"味道地图","content":"学校菜园收获了五种颜色的蔬菜。周老师把洗净并安全处理好的蔬菜装盘，请大家记录颜色、气味、触感和入口后的感觉，并说明每个人都可以停在任何一步。","image":"growth-tasks/food-exploration"},
    {"title":"只看一眼就拒绝","content":"轮到绿黄瓜时，彭泽宇只看颜色就说不喜欢。他没有靠近观察，也没有闻或触碰，因此绿色味道格只能暂时空着。","image":"growth-tasks/food-exploration"},
    {"title":"空白格说不出味道","content":"分享时，同伴能说出胡萝卜的脆响和甜椒的青草气味。彭泽宇却说不出黄瓜是硬是软、是香是淡，发现自己还没有真正认识它。","image":"growth-tasks/food-exploration"},
    {"title":"从眼睛和手指开始","content":"他先看见浅绿色果肉和小籽，再用手指碰到凉凉的表面，接着闻到淡淡清香，最后用舌尖轻轻碰了一下切面。","image":"growth-tasks/food-exploration"},
    {"title":"一个新的味道词","content":"彭泽宇自愿咬了一小口，发现黄瓜凉凉的、脆脆的、味道很淡。他没有说最喜欢，只把真实感受写进绿色格子。","image":"growth-tasks/food-exploration"},
    {"title":"不同的做法","content":"几天后，食堂提供黄瓜片和酸奶黄瓜。他只选择一小勺酸奶黄瓜，发现自己愿意以后再尝这种做法，便画下一颗愿意再试的小星星。","image":"growth-tasks/food-exploration"},
    {"title":"先认识一下","content":"每个人的味道地图都不一样，也有人还没准备好品尝。彭泽宇说：可以暂时不喜欢，但先认识一下。","image":"growth-tasks/food-exploration"}
  ]
}', 1, 3),
(558, 131, '食物探索问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"味道地图可以记录什么？","options":["颜色、气味、触感和味道","只有食物价格","只有盘子大小"],"answer":0,"image":"growth-tasks/food-exploration"},
    {"question":"绿色格子为什么一开始是空白？","options":["黄瓜突然消失了","彭泽宇没有观察就拒绝了","老师不让他记录"],"answer":1,"image":"growth-tasks/food-exploration"},
    {"question":"彭泽宇探索黄瓜时先做了什么？","options":["先观察和触碰","闭眼吞下一大块","用甜食交换"],"answer":0,"image":"growth-tasks/food-exploration"},
    {"question":"故事中的老师怎样对待品尝？","options":["要求所有人吃完整盘","允许孩子在任何一步停下","用糖果奖励吃得最快的人"],"answer":1,"image":"growth-tasks/food-exploration"},
    {"question":"尝过一口后可以怎样表达？","options":["诚实描述感受，也可以暂时不喜欢","必须马上说最喜欢","嘲笑别人的选择"],"answer":0,"image":"growth-tasks/food-exploration"}
  ]
}', 2, 3),

(559, 132, '消失的图书馆徽章', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"明天要带的三样东西","content":"彭泽宇第二天要带借阅证、归还的故事书和阅读徽章参加活动。他转身搭积木机场，把外套、故事书和借阅证随手放在不同地方。","image":"growth-tasks/tidy-home"},
    {"title":"房间里的临时位置","content":"机场搭好后，他又画飞机，彩笔、剪刀和玩具散在各处。洗漱前，他只把脚边积木推到墙角，觉得早晨再找也来得及。","image":"growth-tasks/tidy-home"},
    {"title":"徽章去了哪里","content":"第二天，故事书从沙发缝找到，借阅证从旧背包翻出，徽章却不见了。全家寻找十几分钟，最后才在外套口袋里摸到它。","image":"growth-tasks/tidy-home"},
    {"title":"少掉的选书时间","content":"赶到图书馆时，自由选书已经开始。彭泽宇只来得及翻一本恐龙百科。没有人缩短活动，是早晨找东西用掉了原本的选书时间。","image":"growth-tasks/tidy-home"},
    {"title":"物品固定的家","content":"回家后，他把书放上矮书架，证件和徽章放进门边蓝色文件袋，积木回透明盒，外套挂上衣架。固定的家比临时角落更好记。","image":"growth-tasks/tidy-home"},
    {"title":"睡前一分钟检查","content":"下一次活动前，他照清单检查书、证件和徽章，只用一分钟。第二天直接出门，到图书馆后完整比较了三本恐龙百科。","image":"growth-tasks/tidy-home"},
    {"title":"东西回家，时间留下","content":"读完书后，彭泽宇把书和徽章送回固定位置，并贴上口诀：用完送回家，下次马上找到它。","image":"growth-tasks/tidy-home"}
  ]
}', 1, 3),
(560, 132, '整理习惯问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"图书馆活动需要带哪三样东西？","options":["故事书、借阅证和阅读徽章","积木、剪刀和外套","水壶、足球和雨伞"],"answer":0,"image":"growth-tasks/tidy-home"},
    {"question":"全家为什么找了很长时间？","options":["东西都没有固定位置","图书馆拿走了徽章","房间里没有灯"],"answer":0,"image":"growth-tasks/tidy-home"},
    {"question":"寻找东西带来了什么自然后果？","options":["自由选书时间变少","所有图书都不见了","活动永久取消"],"answer":0,"image":"growth-tasks/tidy-home"},
    {"question":"阅读徽章后来固定放在哪里？","options":["沙发缝里","蓝色文件袋里","积木机场里"],"answer":1,"image":"growth-tasks/tidy-home"},
    {"question":"睡前检查有什么帮助？","options":["第二天更快找到要带的东西","让物品自己走路","不用再归还图书"],"answer":0,"image":"growth-tasks/tidy-home"}
  ]
}', 2, 3),

(561, 133, '打哈欠的观察员', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"明早集合","content":"第二天清晨有湿地观察活动。彭泽宇准备好望远镜、记录本和铅笔，妈妈提醒他早点完成洗漱。八点半计时钟响时，积木观鸟台还差一层。","image":"growth-tasks/sleep-routine"},
    {"title":"一个又一个五分钟","content":"他说再搭五分钟，搭完又装楼梯，再翻开鸟类故事。等妈妈再次经过，已经很晚，他匆忙刷牙，躺下后脑子仍在想着积木。","image":"growth-tasks/sleep-routine"},
    {"title":"没听清的集合要求","content":"第二天，他眼皮发沉，站队时连打哈欠。老师要求听见两声短哨就停下，彭泽宇漏听了后半句，伙伴停下时他还迷糊向前走。","image":"growth-tasks/sleep-routine"},
    {"title":"错过第一声鸟鸣","content":"大家在芦苇旁安静观察。画眉鸟鸣响起时，彭泽宇正在低头揉眼，抬头只看见树叶摇晃。老师请他站到身边，并重新说明要求。","image":"growth-tasks/sleep-routine"},
    {"title":"精力要提前准备","content":"彭泽宇发现物品都准备好了，自己却没有休息好。他和妈妈画下收玩具、洗漱、选一本书、关灯的睡前路线。","image":"growth-tasks/sleep-routine"},
    {"title":"让玩具先休息","content":"当晚计时钟响时，积木桥还没完成。他拍照记录进度，把积木留在固定区域，按路线洗漱、读短故事并关灯。","image":"growth-tasks/sleep-routine"},
    {"title":"听见完整的清晨","content":"下一次观察时，彭泽宇精神充足，听清规则，也看见了树枝上的画眉鸟。他写下记录，并说：玩具也休息，我也充好电。","image":"growth-tasks/sleep-routine"}
  ]
}', 1, 3),
(562, 133, '睡眠习惯问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"彭泽宇为什么没有听清集合要求？","options":["前一晚一次次推迟睡觉，精神不足","老师没有说话","望远镜坏了"],"answer":0,"image":"growth-tasks/sleep-routine"},
    {"question":"精神不足让他错过了什么？","options":["第一声画眉鸟鸣","午饭时间","回家的汽车"],"answer":0,"image":"growth-tasks/sleep-routine"},
    {"question":"老师怎样保障他的安全？","options":["重新说明要求并请他站到身边","让他独自离队","取消所有人的观察"],"answer":0,"image":"growth-tasks/sleep-routine"},
    {"question":"下列哪项属于睡前路线？","options":["继续看很多集动画","收玩具、洗漱、读一本书、关灯","把闹钟藏起来"],"answer":1,"image":"growth-tasks/sleep-routine"},
    {"question":"没完成的积木可以怎样留到明天？","options":["拍照记录并放在固定区域","熬夜一定做完","全部推到地上"],"answer":0,"image":"growth-tasks/sleep-routine"}
  ]
}', 2, 3),

(563, 134, '牙齿上的彩色警报', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"十秒钟刷牙法","content":"彭泽宇早晚都只让牙刷在前牙上晃几下，觉得镜子里看起来很白就已经干净。爸爸提醒里面和后面也要刷，他总说已经完成。","image":"growth-tasks/brushing-teeth"},
    {"title":"口腔健康体验日","content":"学校邀请牙科医生做健康体验。在家长同意和专业人员指导下，牙菌斑显示工具让平时没刷净的牙齿边缘和后排位置出现明显颜色。","image":"growth-tasks/brushing-teeth"},
    {"title":"看起来白还不够","content":"彭泽宇看见平时正面看不到的角落，需要花更多时间重新清洁。他发现十秒钟只能产生泡沫，不能照顾到每颗牙。","image":"growth-tasks/brushing-teeth"},
    {"title":"给牙齿排队","content":"医生用牙齿模型示范固定路线：外侧、里面和咬合面都要刷到。儿童刷牙时，家长还要根据年龄和能力进行检查或协助。","image":"growth-tasks/brushing-teeth"},
    {"title":"两分钟计时歌","content":"回家后，爸爸播放两分钟计时歌。彭泽宇按固定顺序清洁各个区域，爸爸再帮他检查容易漏掉的位置。","image":"growth-tasks/brushing-teeth"},
    {"title":"早晚各一次","content":"彭泽宇在日历上给早晚刷牙分别画一个提醒圆点。几天后，固定路线越来越熟，不必再频繁停下来想下一步。","image":"growth-tasks/brushing-teeth"},
    {"title":"彩色警报的提醒","content":"他把刷牙路线图贴在镜子旁，念出口诀：早晚认真刷，里面外面都不落。","image":"growth-tasks/brushing-teeth"}
  ]
}', 1, 3),
(564, 134, '认真刷牙问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"十秒钟刷牙容易漏掉哪里？","options":["里面、后面和咬合面","只有牙刷手柄","镜子外面"],"answer":0,"image":"growth-tasks/brushing-teeth"},
    {"question":"牙菌斑显示工具帮助彭泽宇发现什么？","options":["平时没有刷净的位置","牙齿会唱歌","牙刷会变大"],"answer":0,"image":"growth-tasks/brushing-teeth"},
    {"question":"固定刷牙路线有什么好处？","options":["更不容易遗漏牙齿区域","可以不用牙刷","只刷一颗牙就够了"],"answer":0,"image":"growth-tasks/brushing-teeth"},
    {"question":"儿童练习刷牙时，大人可以怎样帮助？","options":["检查或协助容易漏掉的位置","让孩子随便刷几秒","用糖果代替刷牙"],"answer":0,"image":"growth-tasks/brushing-teeth"},
    {"question":"故事中的刷牙提醒是什么？","options":["想起来才刷一次","早晚认真刷，里面外面都不落","只刷看得见的前牙"],"answer":1,"image":"growth-tasks/brushing-teeth"}
  ]
}', 2, 3),

(565, 135, '被平板挤走的搭建时间', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"晚饭后的纸箱城堡","content":"彭泽宇和爸爸约好晚饭后看二十分钟动画，再用三十分钟搭纸箱城堡。爸爸也把手机放进充电盒，亲子搭建时全家都不用屏幕。","image":"growth-tasks/screen-balance"},
    {"title":"自动出现的下一集","content":"计时器响时，动画正好留下悬念。彭泽宇按掉计时器，自动播放又开始下一集，他把声音调小继续观看。","image":"growth-tasks/screen-balance"},
    {"title":"只搭好一扇门","content":"等他关掉平板，原定搭建时间只剩十分钟。他和爸爸刚剪出一扇门，洗漱提醒就响了，吊桥和瞭望塔都要留到以后。","image":"growth-tasks/screen-balance"},
    {"title":"屏幕占据的位置","content":"没有人拿走搭建时间，是他一次次选择继续播放。洗漱变得匆忙，睡前短故事也没有时间听完。","image":"growth-tasks/screen-balance"},
    {"title":"全家共同的计划","content":"他们重新制定规则：吃饭、搭建和睡前留出无屏幕时间，关闭自动播放，先确定结束点。爸爸也承诺亲子活动时不查看普通工作消息。","image":"growth-tasks/screen-balance"},
    {"title":"吊桥升起来了","content":"第二天看完约定内容，彭泽宇记下集数，亲手关闭平板。三十分钟里，他和爸爸做好吊桥和蓝色砖块，纸板城堡终于开放。","image":"growth-tasks/screen-balance"},
    {"title":"给别的快乐留位置","content":"他仍然会看喜欢的动画，也开始在结束时间停下。屏幕关闭后，还有积木、图书、户外游戏和家人在等待：时间到了就关闭，给别的快乐留位置。","image":"growth-tasks/screen-balance"}
  ]
}', 1, 3),
(566, 135, '屏幕管理问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"最初约定怎样安排晚饭后的时间？","options":["先看约定时长动画，再搭城堡","整晚只看动画","不做任何计划"],"answer":0,"image":"growth-tasks/screen-balance"},
    {"question":"什么功能让下一集自动开始？","options":["自动播放","手电筒","相机"],"answer":0,"image":"growth-tasks/screen-balance"},
    {"question":"延长屏幕时间挤走了什么？","options":["搭建、洗漱和睡前故事时间","学校操场","第二天的天气"],"answer":0,"image":"growth-tasks/screen-balance"},
    {"question":"新的计划为什么要求大人也遵守？","options":["家庭成员共同示范健康边界","让大人永远不用手机","因为孩子不能参与计划"],"answer":0,"image":"growth-tasks/screen-balance"},
    {"question":"计时器响后，彭泽宇怎样处理悬念？","options":["记下集数并关闭平板","偷偷再看很多集","把计时器藏起来"],"answer":0,"image":"growth-tasks/screen-balance"}
  ]
}', 2, 3),

(567, 136, '谁弄坏了纸箱城堡', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"两位建筑师","content":"彭泽宇邀请安安一起给纸箱城堡搭瞭望塔。他画了圆塔，安安觉得方塔更稳，没有先商量就把纸板剪成方形。彭泽宇回来后胸口紧紧的。","image":"growth-tasks/emotion-repair"},
    {"title":"没有说出口的生气","content":"安安介绍方塔时，彭泽宇没有说明失望，只皱眉抢走纸板和胶带。安安问了两遍，他都说随便，声音却越来越响。","image":"growth-tasks/emotion-repair"},
    {"title":"倒下的城墙","content":"安安想拿回材料，彭泽宇用力把旁边纸板推开。一块纸板撞上城墙，吊桥和半面墙倒了下来，合作也停住了。","image":"growth-tasks/emotion-repair"},
    {"title":"先让身体停下来","content":"爸爸先收好散落的剪刀，确认两个人没有受伤，再请他们分开坐一会儿。彭泽宇慢慢吸气、呼气，肩膀放松后说出自己为什么生气。","image":"growth-tasks/emotion-repair"},
    {"title":"把话听完整","content":"安安承认修改前应该商量，也说城墙被推倒让自己难过。彭泽宇为抢东西和推材料道歉，两人决定把方形底座和圆形塔顶组合起来。","image":"growth-tasks/emotion-repair"},
    {"title":"一起修回来的城堡","content":"彭泽宇扶住城墙，安安贴上加固条。他们重新制作折弯的吊桥。修复占用了装饰旗帜的时间，却让城堡重新站稳。","image":"growth-tasks/emotion-repair"},
    {"title":"情绪来了以后","content":"他们在城堡旁画下暂停、呼吸和说清楚三个图标。彭泽宇念道：先停一停，说出感受，再一起想办法。","image":"growth-tasks/emotion-repair"}
  ]
}', 1, 3),
(568, 136, '情绪表达问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"彭泽宇为什么生气？","options":["安安没有商量就修改了圆塔设计","城堡颜色太多","爸爸收好了剪刀"],"answer":0,"image":"growth-tasks/emotion-repair"},
    {"question":"没有说清感受后，问题怎样变大？","options":["他抢材料并推倒部分城墙","城堡自己飞走了","伙伴马上知道了原因"],"answer":0,"image":"growth-tasks/emotion-repair"},
    {"question":"爸爸到来后首先做什么？","options":["判断谁赢了","收好剪刀并确认安全","替孩子修完整座城堡"],"answer":1,"image":"growth-tasks/emotion-repair"},
    {"question":"两种设计最后怎样结合？","options":["方形底座配圆形塔顶","只留下坏掉的城墙","把材料全部丢掉"],"answer":0,"image":"growth-tasks/emotion-repair"},
    {"question":"生气时可以先做哪三个动作？","options":["停下、呼吸、说清楚","抢走、推倒、离开","忍住、猜测、责怪"],"answer":0,"image":"growth-tasks/emotion-repair"}
  ]
}', 2, 3);
