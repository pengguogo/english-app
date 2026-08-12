-- V60: 三篇适合 4～6 岁儿童的轻推理故事
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(124,5,'消失的蜜蜂地图',23,0),(125,5,'暴雨中的森林邮局',24,0),(126,5,'会发光的星空露营地',25,0);

INSERT INTO lesson (id,unit_id,name,type,content,sort_order,star_reward) VALUES
(543,124,'消失的蜜蜂地图','READING','{"type":"READING","items":[
{"title":"花园里的空地图","content":"彭泽宇来到阳光花园。养蜂员周阿姨发现蜂箱旁的花朵地图被风吹走了。小蜜蜂找不到新开的花田，大家决定根据沿途线索重新画一张地图。","image":"story/bee-map-01"},
{"title":"黄色花粉","content":"蜂箱门口落着一点黄色花粉。花园里，红玫瑰的花粉颜色深，蓝铃花的花粉偏白，向日葵的花粉是明亮的黄色。彭泽宇猜，小蜜蜂先去过向日葵田。","image":"story/bee-map-02"},
{"title":"朝太阳的方向","content":"向日葵都朝着明亮的东边。花瓣旁还有一串小小的蜜蜂飞行记号，朝木桥方向延伸。彭泽宇在新地图上画出第一段路线：蜂箱、向日葵田、木桥。","image":"story/bee-map-03"},
{"title":"两座小桥","content":"前面有两座桥。左边石桥旁没有花，右边木桥旁开着三朵蓝铃花。一只蜜蜂正停在蓝花上。彭泽宇选择右边，因为地图线索和眼前的花都指向木桥。","image":"story/bee-map-04"},
{"title":"叶子上的水珠","content":"木桥后，一片叶子上留着几滴甜甜的花蜜，旁边有紫色花瓣。周阿姨提醒他不能尝，只能观察。远处的小坡上正开着一大片紫色薰衣草。","image":"story/bee-map-05"},
{"title":"找到新花田","content":"他们沿小路来到薰衣草坡，许多蜜蜂正在安全采蜜。彭泽宇把紫色花田补进地图，还画上回蜂箱的短路线。原来地图没有让花田消失，认真观察就能把路线找回来。","image":"story/bee-map-06"},
{"title":"给风留个办法","content":"新地图被装进透明夹板，再用绳子固定好。彭泽宇学会 bee 是蜜蜂，flower 是花。他还知道蜜蜂采蜜时会帮助花朵传递花粉，花园和蜜蜂互相需要。","image":"story/bee-map-07"}]}',1,3),
(544,124,'蜜蜂地图问答','QUIZ','{"type":"QUIZ","items":[
{"question":"黄色花粉最先指向哪片花田？","options":["向日葵田","红玫瑰园","没有花的石桥"],"answer":0,"image":"story/bee-map-quiz"},
{"question":"为什么选择右边的木桥？","options":["桥旁有蓝铃花和蜜蜂","木桥声音最大","左边写着答案"],"answer":0,"image":"story/bee-map-quiz"},
{"question":"紫色花瓣提示大家去哪里？","options":["薰衣草坡","停车场","池塘底"],"answer":0,"image":"story/bee-map-quiz"},
{"question":"发现不认识的花蜜可以尝吗？","options":["不可以，只观察并问大人","可以喝一大口","偷偷尝一点"],"answer":0,"image":"story/bee-map-quiz"},
{"question":"bee 是什么意思？","options":["地图","蜜蜂","木桥"],"answer":1,"image":"story/bee-map-quiz"},
{"question":"怎样防止新地图再被风吹走？","options":["装进夹板并固定","放在路中央","折成小船"],"answer":0,"image":"story/bee-map-quiz"}]}',2,3),
(545,125,'暴雨中的森林邮局','READING','{"type":"READING","items":[
{"title":"三件雨天包裹","content":"森林邮局要送出三件包裹：红屋的小兔要雨衣，树洞里的松鼠要药箱，山坡上的小鹿要图画书。雨越下越大，邮递员鹿叔叔和彭泽宇先查看天气图。","image":"story/forest-mail-01"},
{"title":"不能走的蓝桥","content":"地图上的蓝桥旁亮起红色叉号，表示河水太高不能通行。另一条黄色林间路虽然远一点，却有绿色安全标记。大家选择黄色路，安全比走得快更重要。","image":"story/forest-mail-02"},
{"title":"先送最需要的","content":"三个包裹中，松鼠的药箱最急。黄色路先经过树洞，再到红屋，最后通向山坡。彭泽宇给路线排好顺序：药箱、雨衣、图画书。","image":"story/forest-mail-03"},
{"title":"倒下的小树枝","content":"林间路上横着一根小树枝。大家没有冒雨搬动，而是停在安全棚里报告护林员。护林员穿好雨具、确认周围安全后清理道路，邮车才继续前进。","image":"story/forest-mail-04"},
{"title":"看颜色找门牌","content":"雨雾里看不清字，但树洞门口挂着棕色松果，红屋顶很醒目，山坡门前有鹿角形标志。彭泽宇用颜色和形状核对地点，三件包裹一件也没送错。","image":"story/forest-mail-05"},
{"title":"雨后的回信","content":"雨停后，三位收件人送来感谢卡。彭泽宇明白，雨天邮递不是勇敢地冲过去，而是看懂标记、先送急需物品、遇到危险及时求助。","image":"story/forest-mail-06"}]}',1,3),
(546,125,'森林邮路问答','QUIZ','{"type":"QUIZ","items":[
{"question":"蓝桥旁有红叉时应该怎么走？","options":["走有绿色安全标记的黄色路","冲过蓝桥","把标记拿掉"],"answer":0,"image":"story/forest-mail-quiz"},
{"question":"为什么先送松鼠的包裹？","options":["里面是急需的药箱","包裹最漂亮","树洞最远"],"answer":0,"image":"story/forest-mail-quiz"},
{"question":"路上有倒下的树枝时怎么办？","options":["在安全处报告护林员","独自冒雨搬动","闭眼开过去"],"answer":0,"image":"story/forest-mail-quiz"},
{"question":"红色屋顶是哪位收件人的家？","options":["小兔","松鼠","小鹿"],"answer":0,"image":"story/forest-mail-quiz"},
{"question":"雨天送信最重要的是什么？","options":["看安全标记并及时求助","一定走最短的路","谁也不告诉"],"answer":0,"image":"story/forest-mail-quiz"}]}',2,3),
(547,126,'会发光的星空露营地','READING','{"type":"READING","items":[
{"title":"太阳下山前","content":"彭泽宇和爸爸来到星空露营地。他们在天亮时搭好帐篷，记住洗手间、服务站和安全步道的位置。管理员提醒大家，天黑后要和大人在一起，不离开有灯的路线。","image":"story/starlight-camp-01"},
{"title":"草丛里的小灯","content":"夜幕降临，草丛里出现许多一闪一闪的绿光。它们飞得很低，没有电线，也不像手电筒一直亮。管理员说，这是萤火虫发出的光，大家只能远远观看。","image":"story/starlight-camp-02"},
{"title":"树上的圆眼睛","content":"树枝上传来轻轻的咕咕声，两个圆圆的影子一动不动。彭泽宇先听声音，再用红色观察灯照向树旁，发现是一只猫头鹰，不是迷路的小猫。","image":"story/starlight-camp-03"},
{"title":"谁踩过泥地","content":"步道边的泥地有一串小脚印：前面像小手，后面细长。旁边的垃圾桶盖没有关紧。管理员根据脚印判断，可能是浣熊来过，于是戴手套把桶盖锁好。","image":"story/starlight-camp-04"},
{"title":"月亮藏起来了","content":"一片云遮住月亮，步道变暗。彭泽宇没有追着萤火虫跑，而是停在原地拉住爸爸的手。爸爸打开暖黄色手电，他们沿反光标记慢慢回到帐篷。","image":"story/starlight-camp-05"},
{"title":"三颗明亮的星","content":"云飘走后，老师用三颗排成一行的亮星帮助大家找方向。彭泽宇学会 star 是星星，moon 是月亮。他只用眼睛观察，没有用强光照动物。","image":"story/starlight-camp-06"},
{"title":"安静的晚安","content":"回到帐篷前，彭泽宇复述今晚的线索：闪动绿光是萤火虫，咕咕声和圆眼影子是猫头鹰，小手似的脚印可能来自浣熊。安静观察，让夜晚既有趣又安全。","image":"story/starlight-camp-07"}]}',1,3),
(548,126,'星空露营问答','QUIZ','{"type":"QUIZ","items":[
{"question":"草丛里一闪一闪的绿光是什么？","options":["萤火虫","红绿灯","掉下来的星星"],"answer":0,"image":"story/starlight-camp-quiz"},
{"question":"咕咕声和树上的圆眼影子提示什么动物？","options":["猫头鹰","小鱼","蜜蜂"],"answer":0,"image":"story/starlight-camp-quiz"},
{"question":"发现像小手的脚印和没关好的垃圾桶，可能是谁来过？","options":["浣熊","长颈鹿","海龟"],"answer":0,"image":"story/starlight-camp-quiz"},
{"question":"步道突然变暗时，彭泽宇怎么做？","options":["停下并拉住爸爸的手","独自追萤火虫","跑进树林"],"answer":0,"image":"story/starlight-camp-quiz"},
{"question":"moon 是什么意思？","options":["月亮","帐篷","手电筒"],"answer":0,"image":"story/starlight-camp-quiz"},
{"question":"夜间观察动物应该怎样做？","options":["保持安静，不用强光照动物","抓回帐篷","大声叫它们过来"],"answer":0,"image":"story/starlight-camp-quiz"}]}',2,3);
