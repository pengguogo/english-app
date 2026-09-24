-- 根据家庭讲述的真实经历改编；对话与心理描写为文学化表达。
INSERT INTO unit (theme_id, name, sort_order, is_locked)
VALUES (49, '深圳北站的半小时', 19, 0);

INSERT INTO lesson (unit_id, name, type, content, sort_order, star_reward)
VALUES
((SELECT id FROM unit WHERE theme_id = 49 AND name = '深圳北站的半小时'), '深圳北站的半小时', 'READING', '{"type":"READING","items":[
{"title":"去潮汕的动车","content":"今天，彭泽宇和妈妈来到深圳北站，准备乘动车去潮汕。候车大厅很大，人们拖着箱子来来往往。妈妈要去洗手间，彭泽宇留在附近等她。一个带轮子的行李箱就在身旁，他握住拉杆，轻轻推了一下，箱子咕噜咕噜向前滚，他觉得很好玩。","image":"station-safety/shenzhen-north-01"},
{"title":"再玩一下就回来","content":"彭泽宇把箱子推过去，又拉回来，给它转了个弯。眼睛跟着箱子走，脚也跟着走。他原以为自己只挪了几步，抬头时，刚才等妈妈的地方已经不在眼前。周围的指示牌、座椅和通道看起来都很像。好玩的行李箱突然变得沉甸甸的。","image":"station-safety/shenzhen-north-02"},
{"title":"走错的那条路","content":"他想回到原位，便选了一条看起来熟悉的路。走了一段，却没有看见妈妈，也没有看见刚才的位置。他又回头看，发现自己连从哪边过来的都说不清了。车站里人很多，越想快点找到妈妈，他越觉得每个方向都像可能是对的。可是继续猜方向，只会让彼此离得更远。","image":"station-safety/shenzhen-north-02"},
{"title":"妈妈出来了","content":"妈妈从洗手间出来，看见原来等候的地方没有彭泽宇，心一下揪紧了。她看向附近的座椅和通道，一声声喊他的名字：彭泽宇！彭泽宇！没有听到回答，她又往人群里找。她怕漏掉一个角落，怕他走到更远的地方，急得哭了，还是不停地寻找。","image":"station-safety/shenzhen-north-03"},
{"title":"服务台前的求助","content":"妈妈来到服务台，说明孩子走失，想请工作人员调监控寻找。她得知这里不能直接为她调取监控，需要报警。妈妈赶紧报警，并把孩子走失的位置和经过尽量说清楚。她一边等警察叔叔赶来，一边盯着来往的人群，眼泪止不住地掉。去潮汕的动车也快到时间了，但此刻最要紧的是找到彭泽宇。","image":"station-safety/shenzhen-north-03"},
{"title":"漫长的半小时","content":"对彭泽宇来说，刚才只是想玩一会儿行李箱；对妈妈来说，每一分钟都像被拉长了。她不知道孩子走到了哪条通道，也不知道他会不会继续走远。警察叔叔后来赶到，协助寻找。妈妈努力把自己记得的情况告诉他们。车站里的脚步声仍然匆忙，她只盼望下一眼能看到熟悉的身影。","image":"station-safety/shenzhen-north-04"},
{"title":"终于找到你了","content":"在警察叔叔的协助下，彭泽宇终于被找到了。妈妈看见他，快步走过去，把他紧紧抱住。她刚才急哭了，此时眼泪又落下来。彭泽宇也终于不用再猜哪条路能回到妈妈身边。两个人先确认彼此平安，才慢慢说起那个越玩越远的行李箱。","image":"station-safety/shenzhen-north-05"},
{"title":"差一点错过动车","content":"从走散到找到，过去了大约半个小时。他们差点错过去潮汕的动车。彭泽宇知道，动车能不能赶上固然着急，妈妈找不到他时的害怕却更难受。妈妈也明白，在人多的车站，短暂离开前需要把等候位置和求助办法讲清楚，并确认孩子有人照看。两人把这次经历当作一次认真练习安全方法的提醒。","image":"station-safety/shenzhen-north-05"},
{"title":"走散时怎么做","content":"如果在车站发现找不到家人，先停在安全的地方，不要凭感觉一条路接一条路地找，也不要跟陌生人离开。可以向穿制服的车站工作人员或警察求助，说清自己的名字、在哪里与家人走散，请他们帮忙联系家人。大人发现孩子走失，也要尽快找车站工作人员并报警，清楚说明最后见到孩子的位置。","image":"station-safety/shenzhen-north-04"},
{"title":"下一次，先看妈妈在哪里","content":"彭泽宇记住了：行李箱可以玩，不能玩着玩着离开约好的位置。想换地方，要先告诉妈妈，等妈妈回应后一起走。妈妈也会在需要离开时安排好照看和明确的等待地点。车站依旧很大，路也依旧很多；下次他们会先确认彼此在哪里，再一起走向要乘坐的动车。","image":"station-safety/shenzhen-north-01"}
]}', 1, 3),
((SELECT id FROM unit WHERE theme_id = 49 AND name = '深圳北站的半小时'), '车站安全问答', 'QUIZ', '{"type":"QUIZ","items":[
{"question":"彭泽宇为什么离开原来的位置？","options":["玩行李箱时不知不觉走远了","妈妈让他去找站台","警察叔叔带他走了"],"answer":0,"image":"station-safety/shenzhen-north-02"},
{"question":"妈妈从洗手间出来后做了什么？","options":["以为他会自己回来","到处寻找并焦急地喊他的名字","独自乘动车离开"],"answer":1,"image":"station-safety/shenzhen-north-03"},
{"question":"服务台无法直接调取监控时，妈妈接着怎么做？","options":["停止寻找","报警并说明情况","随意走进工作区"],"answer":1,"image":"station-safety/shenzhen-north-03"},
{"question":"在车站与家人走散，哪种做法更安全？","options":["不断猜路寻找","跟陌生人离开车站","留在安全处，找车站工作人员或警察求助"],"answer":2,"image":"station-safety/shenzhen-north-04"},
{"question":"想离开约好的等候位置，应该先做什么？","options":["告诉家人，得到回应后一起走","只要看得到行李箱就继续走","觉得路熟就直接走"],"answer":0,"image":"station-safety/shenzhen-north-01"}
]}', 2, 3);
