-- V29：新增十篇火车主题科普故事，每篇八个阅读页并复用一张主图。

INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(105, 5, '暴雪中的开路先锋', 4, 0),
(106, 5, '星光卧铺列车', 5, 0),
(107, 5, '爬上云端的小火车', 6, 0),
(108, 5, '跨海列车奇遇记', 7, 0),
(109, 5, '沙漠里的生命列车', 8, 0),
(110, 5, '会给铁轨看病的医生', 9, 0),
(111, 5, '森林深处的木材列车', 10, 0),
(112, 5, '百岁蒸汽机车的生日', 11, 0),
(113, 5, '没有司机的地铁早班车', 12, 0),
(114, 5, '彩虹桥上的救援列车', 13, 0);

INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(512, 105, '暴雪中的开路先锋', 'READING', '{"type":"READING","items":[
{"title":"暴雪封住山路","content":"一夜之间，厚厚的积雪盖住了山区铁路。第一班客车停在安全车站等待，调度员小雪通知大家：在确认线路安全以前，任何列车都不能进入雪区。","image":"story/snowplow-train"},
{"title":"先让巡检员出发","content":"工务巡检员乘小车来到雪区边缘，测量积雪深度，查看道岔、信号机和山坡。他们把情况传回调度中心，建议派旋转式除雪车清理最深的一段。","image":"story/snowplow-train"},
{"title":"红色先锋做体检","content":"司机北叔检查除雪车的燃料、制动和车灯，维修员试转车头前方的巨大叶轮。叶轮能把积雪卷起并抛向轨道两侧，所有防护罩也必须牢牢关好。","image":"story/snowplow-train"},
{"title":"封锁区间再开工","content":"调度员确认雪区内没有其他列车，锁定通往作业区的道岔，并正式封锁线路。得到命令后，红色除雪车才亮起车灯，低速驶进纷飞的大雪。","image":"story/snowplow-train"},
{"title":"大雪堆挡在前方","content":"一处风口堆起了比车头还高的雪墙。北叔没有猛冲，而是先停车观察，再后退到安全位置，让叶轮保持稳定转速，分几次慢慢切开雪墙。","image":"story/snowplow-train"},
{"title":"轨道重新露出来","content":"白雪被抛向两侧，两条钢轨终于重新出现。除雪车继续清理站场和道岔，工务人员随后扫净活动部件里的冰雪，确认道岔能够正确转换。","image":"story/snowplow-train"},
{"title":"清完还要再检查","content":"清障完成不等于马上通车。巡检员徒步检查钢轨、路基和山坡，还用设备确认线路上没有障碍。调度中心收到合格报告后，才解除线路封锁。","image":"story/snowplow-train"},
{"title":"清晨列车安全通过","content":"天亮时，第一班客车按临时限速缓缓通过雪区，旅客从车窗看见停在一旁的红色先锋。小雪明白了：真正的准时，建立在一步不少的安全准备上。","image":"story/snowplow-train"}
]}', 1, 3),
(513, 106, '星光卧铺列车', 'READING', '{"type":"READING","items":[
{"title":"傍晚登上夜行列车","content":"小满第一次独自去外婆家。傍晚，他在爸爸陪同下找到正确车厢。列车员林阿姨核对车票和姓名，把他带到铺位，并记下目的站。","image":"story/night-sleeper-train"},
{"title":"行李也有安全位置","content":"小满把小背包装在身边，大行李则放进稳固的行李架。林阿姨提醒他，走廊和车门附近不能堆东西，否则列车晃动时容易绊倒旅客。","image":"story/night-sleeper-train"},
{"title":"车轮唱起晚餐歌","content":"列车平稳离站，餐车推来热饭和温水。小满坐在窗边，看城市灯光渐渐变成田野里的小光点。长途列车像一座沿着铁轨移动的小房子。","image":"story/night-sleeper-train"},
{"title":"卧铺车厢安静下来","content":"熄灯时间到了，大家拉好床帘，轻声交谈。卧铺上的护栏防止旅客在晃动中跌落。小满听着有节奏的车轮声，很快进入梦乡。","image":"story/night-sleeper-train"},
{"title":"半夜为什么停车","content":"夜里，小满感觉列车停了下来。林阿姨告诉他，这是一座会让站：他们的列车在侧线等候，另一列车从相邻线路安全通过，然后才能继续前进。","image":"story/night-sleeper-train"},
{"title":"星空陪着列车前进","content":"再次出发后，蓝色列车穿过安静平原。司机按照信号驾驶，列车员轮流值班，沿途车站也有人守护。旅客休息时，铁路仍在有序运转。","image":"story/night-sleeper-train"},
{"title":"清晨广播响起来","content":"天边变亮，广播提前提醒下一站旅客起床。小满整理被褥，检查车票、背包和水杯。林阿姨确认他没有落下物品，陪他走到车门附近等候。","image":"story/night-sleeper-train"},
{"title":"日出里的重逢","content":"列车准时停稳，车门打开后，小满才走下站台。外婆正在约定地点等他。原来漫长的夜路并不可怕，因为每一个岗位都在共同照顾这趟旅程。","image":"story/night-sleeper-train"}
]}', 1, 3),
(514, 107, '爬上云端的小火车', 'READING', '{"type":"READING","items":[
{"title":"山顶来了新任务","content":"高山气象站需要一套新仪器，可通往山顶的道路又陡又窄。黄色齿轨列车阿齿接下任务，把设备牢牢固定在车厢里，准备向云端出发。","image":"story/mountain-cog-train"},
{"title":"普通车轮怕陡坡","content":"普通列车靠车轮与钢轨之间的摩擦前进。山坡太陡时，车轮可能打滑。阿齿的两条钢轨中央多了一根带齿的轨道，这就是齿条。","image":"story/mountain-cog-train"},
{"title":"齿轮紧紧咬住齿条","content":"阿齿车底的齿轮与中央齿条一齿一齿咬合。电动机转动齿轮，列车便能稳稳向上爬。它速度不快，却能征服普通列车难走的陡坡。","image":"story/mountain-cog-train"},
{"title":"云雾忽然遮住山路","content":"半山腰起了浓雾，司机看不清远处的线路。他立刻降低速度，打开车灯，并通过无线电询问山顶站天气。慢下来，是山区铁路重要的安全办法。","image":"story/mountain-cog-train"},
{"title":"避让站里的相会","content":"上山线路只有一条，但中途设有一小段双线避让站。阿齿先停在规定位置，等下山列车完全进入另一条线，信号开放后再继续爬坡。","image":"story/mountain-cog-train"},
{"title":"最后一段最陡的坡","content":"山顶就在眼前，坡度却更大了。阿齿保持均匀速度，齿轮始终咬紧齿条，制动系统随时准备控制车辆。车内设备一点也没有晃动。","image":"story/mountain-cog-train"},
{"title":"仪器抵达观测站","content":"气象员把设备搬进观测站，很快测到了风速和云层变化。这些数据会帮助山下车站安排列车，也能提前提醒登山者注意坏天气。","image":"story/mountain-cog-train"},
{"title":"云海中的下山路","content":"下山时，阿齿仍然低速行驶，齿轨和制动共同控制速度。它望着脚下云海十分自豪：不必跑得最快，适合自己的本领同样了不起。","image":"story/mountain-cog-train"}
]}', 1, 3),
(515, 108, '跨海列车奇遇记', 'READING', '{"type":"READING","items":[
{"title":"铁路在海边消失了","content":"乐乐坐的列车来到海边港口，前方只剩蓝色大海。他正奇怪时，列车员指向一艘巨大的白色轮船：铁路没有结束，而是要登上火车轮渡。","image":"story/train-ferry"},
{"title":"船岸轨道先对准","content":"潮水会让船身升高或降低，码头用活动连接桥调整角度，让岸上铁轨与船内铁轨准确相接。工作人员反复检查后，调车作业才可以开始。","image":"story/train-ferry"},
{"title":"车厢慢慢登上轮船","content":"旅客先按安排离开车厢，调车机车再把车辆分成小组，低速推上轮渡。分批装载可以控制重量，让船的左右两侧保持平衡。","image":"story/train-ferry"},
{"title":"给车轮系好安全带","content":"车厢停到指定位置后，工作人员放好止轮器，再用固定装置锁住车辆。车钩状态也要检查，这些措施能防止车厢在海浪中移动。","image":"story/train-ferry"},
{"title":"带着铁路驶向大海","content":"船门关闭，轮渡缓缓离港。乐乐在客舱里看见海鸥掠过窗外。甲板下面，几条铁轨和一排排车厢正随着轮船一起跨越海峡。","image":"story/train-ferry"},
{"title":"风浪中的再次检查","content":"海面起了风，船员到车辆甲板检查固定装置和船体状态。驾驶台根据天气调整航向和速度。轮渡不是把火车随便装上船，而是一套严密的运输系统。","image":"story/train-ferry"},
{"title":"另一岸接住铁轨","content":"轮渡抵达对岸并稳稳靠泊。活动连接桥再次调整高度，船内铁轨与岸上铁路连成一线。调车机车按顺序把车辆慢慢拉回陆地。","image":"story/train-ferry"},
{"title":"列车继续远行","content":"车辆重新编组，完成制动检查后再次出发。乐乐终于明白，当高山或海洋挡住道路时，人们会用桥梁、隧道和轮渡创造新的连接。","image":"story/train-ferry"}
]}', 1, 3),
(516, 109, '沙漠里的生命列车', 'READING', '{"type":"READING","items":[
{"title":"小站发来缺水消息","content":"沙漠深处的铁路小站储水快要不足。值班员阿塔把剩余水量和需要人数报给供水中心，一列专门运输清水的蓝色罐车立即开始准备。","image":"story/desert-water-train"},
{"title":"干净罐车才能装水","content":"装水前，工作人员清洗罐体和管道，再检查内壁是否干净。检测合格的清水从专用管线注入罐车，饮用水不能和其他液体混装。","image":"story/desert-water-train"},
{"title":"封好每一个阀门","content":"罐车装到规定重量后，工人关闭阀门并装上封签，还取样检测水质。司机进行制动试验，确认整列车状态正常，才领取出发命令。","image":"story/desert-water-train"},
{"title":"蓝色列车进入沙海","content":"烈日下，供水列车沿着钢轨穿过沙丘。罐体里的防波结构能减小水的晃动，司机平稳加速和制动，避免液体来回冲击车辆。","image":"story/desert-water-train"},
{"title":"风沙盖住了道岔","content":"一阵大风把沙子吹进前方车站的道岔。道岔无法完全贴合时，信号不会允许列车通过。巡检员清除积沙并确认转换正常，列车耐心等待。","image":"story/desert-water-train"},
{"title":"水塔终于看见蓝车","content":"风沙减弱后，蓝色列车低速驶进小站。阿塔核对罐车编号和封签，接上经过消毒的卸水管，让清水缓缓流进高高的储水塔。","image":"story/desert-water-train"},
{"title":"每一滴都认真检查","content":"工作人员再次检测水质，并记录卸水量。水塔利用高度产生压力，把水送到车站、宿舍和维修区。大家节约使用，不让珍贵清水白白流走。","image":"story/desert-water-train"},
{"title":"运来的不只是货物","content":"小站恢复了正常生活，蓝色罐车清洗后踏上返程。阿塔望着远去的列车说，有些火车运送的不只是货物，还把生活的希望送到遥远地方。","image":"story/desert-water-train"}
]}', 1, 3),
(517, 110, '会给铁轨看病的医生', 'READING', '{"type":"READING","items":[
{"title":"电脑发现轻微异常","content":"一天，线路监测电脑发现一段高铁轨道的数据有一点变化。肉眼还看不出问题，工程师叶博士决定派黄色综合检测列车明明去做全面检查。","image":"story/track-inspection-train"},
{"title":"检测车带着许多眼睛","content":"明明的车顶、车底和车厢里装着摄像机、激光仪和超声波设备。出发前，工程师校准仪器，让每一个测量结果都有可靠的标准。","image":"story/track-inspection-train"},
{"title":"激光测量轨道姿势","content":"列车行驶时，激光设备快速测量两条钢轨的高度、距离和方向。轨道应该平顺地延伸，细小的高低或左右变化都会被电脑记录下来。","image":"story/track-inspection-train"},
{"title":"摄像机寻找松动零件","content":"高速摄像机连续拍摄轨枕、扣件和道床，图像系统寻找缺失或松动的部件。工程师还会复核可疑画面，不能只凭电脑猜测。","image":"story/track-inspection-train"},
{"title":"超声波听见内部裂纹","content":"钢轨表面看起来光滑，内部却可能藏着微小裂纹。超声波进入钢轨后遇到裂纹会产生不同回声，就像帮钢轨做一次特别的身体检查。","image":"story/track-inspection-train"},
{"title":"异常位置标上地图","content":"所有数据汇入检测车电脑，叶博士把异常位置精确标在电子地图上。经过多种数据互相验证，他们确认一处扣件需要更换，钢轨本身没有损伤。","image":"story/track-inspection-train"},
{"title":"夜间天窗完成维修","content":"末班车离开后，调度中心留出没有列车运行的维修天窗。工务人员封锁线路，更换扣件并检查周围部件，完成后还把工具逐件清点带走。","image":"story/track-inspection-train"},
{"title":"复检合格才能提速","content":"明明再次经过维修地点，数据全部恢复正常。调度中心确认复检合格后才恢复正常速度。看不见的小问题，也值得用最认真的办法对待。","image":"story/track-inspection-train"}
]}', 1, 3),
(518, 111, '森林深处的木材列车', 'READING', '{"type":"READING","items":[
{"title":"风暴吹倒许多树木","content":"强风过后，一批倒下的树木堵住森林防火通道。护林员青松决定清走倒木，同时保护仍然健康的树木和刚刚长出的幼苗。","image":"story/forest-narrow-gauge-train"},
{"title":"窄轨豆豆接到任务","content":"森林道路狭窄，绿色小机车豆豆沿窄轨铁路进入林区。窄轨两条钢轨的距离较小，车辆也更轻巧，适合小半径弯道和有限空间。","image":"story/forest-narrow-gauge-train"},
{"title":"只装规定的重量","content":"工人把倒木截成合适长度，再均匀放到平车上，用立柱和链条固定。每辆车都有载重上限，装得太高或偏向一侧都会影响安全。","image":"story/forest-narrow-gauge-train"},
{"title":"小弯道要慢慢通过","content":"豆豆牵着两节平车缓缓前进。窄轨铁路能转较小的弯，但载着木材时更要控制速度。司机观察前方，避免树枝或落石侵入线路。","image":"story/forest-narrow-gauge-train"},
{"title":"小鹿站在前方林边","content":"一只小鹿出现在轨道附近，司机立即停车并鸣笛提醒。护林员在安全距离观察，直到小鹿自己走回树林，列车才重新启动。","image":"story/forest-narrow-gauge-train"},
{"title":"木桥接受轻轻考验","content":"到达小木桥前，司机再次确认车重和速度。列车一辆接一辆低速通过，避免给桥梁突然增加冲击。桥下清澈溪水安静地流淌。","image":"story/forest-narrow-gauge-train"},
{"title":"倒木开始新的用途","content":"木材到达加工站后，被制成家具板材和步道材料。已经腐朽、适合留在森林的倒木则没有运走，它们会成为昆虫和真菌的家。","image":"story/forest-narrow-gauge-train"},
{"title":"幼苗守住绿色未来","content":"防火通道恢复畅通，护林员在受损区域补种幼苗。豆豆明白了：运输森林资源不是拿走一切，而是在使用与保护之间找到平衡。","image":"story/forest-narrow-gauge-train"}
]}', 1, 3),
(519, 112, '百岁蒸汽机车的生日', 'READING', '{"type":"READING","items":[
{"title":"老黑迎来百岁生日","content":"铁路博物馆准备举行周年运行活动，百岁蒸汽机车老黑将再次牵引纪念列车。少年志愿者安安擦亮车灯，认真听工程师讲解安全规则。","image":"story/heritage-steam-train"},
{"title":"老机器也要严格体检","content":"工程师检查锅炉、管道、阀门和压力表，还测量车轮与连杆。保存机车不是擦得漂亮就够了，承受高温高压的部件必须符合运行要求。","image":"story/heritage-steam-train"},
{"title":"煤和水各有任务","content":"煤炭在火箱里燃烧，把锅炉中的水加热成蒸汽。煤提供热量，水变成推动机器的力量。工作人员按规定装载，不能超过机车容量。","image":"story/heritage-steam-train"},
{"title":"蒸汽唤醒活塞连杆","content":"锅炉压力缓慢升高，司机轻轻开启汽门。蒸汽推动气缸里的活塞往复运动，连杆再把这种运动传给大动轮，老黑终于慢慢向前。","image":"story/heritage-steam-train"},
{"title":"纪念列车缓缓出站","content":"游客站在安全线后挥手，老黑牵着短短的纪念列车离开红砖车站。司机控制汽门，司炉员照看火床和水位，两人必须默契配合。","image":"story/heritage-steam-train"},
{"title":"老黑讲起年轻时代","content":"很多年前，老黑曾经拉过旅客、煤炭和邮件。后来更高效的柴油与电力机车接替了工作，它光荣退役，进入博物馆保存铁路记忆。","image":"story/heritage-steam-train"},
{"title":"回库以后安全熄火","content":"纪念运行结束，老黑回到机车库。工作人员不能一走了之，他们清理炉火、补充锅炉水，并检查有没有过热、松动或漏气的地方。","image":"story/heritage-steam-train"},
{"title":"机器保存时代记忆","content":"安安把当天记录放进档案。老黑虽然不再承担日常运输，却让孩子们亲眼看见蒸汽怎样驱动车轮。旧机器保存的，还有人们创造和劳动的故事。","image":"story/heritage-steam-train"}
]}', 1, 3),
(520, 113, '没有司机的地铁早班车', 'READING', '{"type":"READING","items":[
{"title":"清晨车库自动醒来","content":"天还没亮，银色地铁小银在车库里自动启动。车载电脑检查制动、车门、照明和通信，把结果发送给控制中心的工程师可可。","image":"story/driverless-metro"},
{"title":"人和系统共同确认","content":"小银能够自动驾驶，但并不是没人负责。可可查看车辆状态，车站工作人员检查站台和轨道，确认没有障碍后才允许第一班车上线。","image":"story/driverless-metro"},
{"title":"电脑按照许可前进","content":"控制系统给出安全速度和行驶许可，小银自动启动。它不断计算与前车的距离；如果前方区段没有开放，就会在安全位置停车等待。","image":"story/driverless-metro"},
{"title":"车门对准站台门","content":"到站时，小银用传感器精确判断位置，让每扇车门都与站台门对齐。列车完全停稳后，两道门才会同步打开，乘客依次上下车。","image":"story/driverless-metro"},
{"title":"背包带挡住车门","content":"一位乘客的背包带夹在门边，车门传感器立刻发现阻力，停止关闭并重新打开。站台工作人员帮助整理背包，提醒大家不要抢门。","image":"story/driverless-metro"},
{"title":"异常交给人来判断","content":"可可在控制中心收到车门警报，通过摄像画面和车站报告确认现场已经安全。她清除警报并重新授权，系统才允许小银继续发车。","image":"story/driverless-metro"},
{"title":"早高峰有序到来","content":"乘客渐渐增多，小银按照运行图自动调整速度和停车时间。工作人员在站台引导客流，控制中心同时观察整条线路的列车位置。","image":"story/driverless-metro"},
{"title":"自动不等于无人负责","content":"小银准时完成早班任务。可可说，自动化把重复动作交给电脑，把判断与应急留给受过训练的人，人与系统一起才能守护每一段旅程。","image":"story/driverless-metro"}
]}', 1, 3),
(521, 114, '彩虹桥上的救援列车', 'READING', '{"type":"READING","items":[
{"title":"雨后传来故障报告","content":"暴雨过后，一辆线路工程车在山谷桥旁发生故障，挡住了铁路。车上人员已经安全撤离，调度中心立即停止列车进入受影响区间。","image":"story/railway-rescue-crane"},
{"title":"先封锁再谈救援","content":"调度员锁定相关道岔，在信号系统中封锁线路，并通知两端车站。只有确认区间内没有其他车辆，黄色救援列车大力才能出发。","image":"story/railway-rescue-crane"},
{"title":"桥梁工程师先检查","content":"到达现场后，陈工先查看桥墩、钢梁、路基和轨道。起重机很重，如果地面或桥梁受损，贸然展开设备可能造成新的危险。","image":"story/railway-rescue-crane"},
{"title":"支腿撑起稳定平台","content":"确认作业位置安全后，救援人员展开起重机支腿，把重量传到坚实基础上。吊臂转动范围内设置警戒区，所有人员都站在吊物之外。","image":"story/railway-rescue-crane"},
{"title":"吊索牢牢连接工程车","content":"工作人员根据工程车重量选择吊索，并连接到规定吊点。指挥员检查每个连接，先把车辆轻轻提起一点，确认平衡后才继续起吊。","image":"story/railway-rescue-crane"},
{"title":"故障车辆平稳离轨","content":"起重机缓慢转动吊臂，把工程车放到救援平车上并牢牢固定。整个过程不追求快，每一个动作都听从同一名指挥员的明确信号。","image":"story/railway-rescue-crane"},
{"title":"复检线路和信号","content":"障碍移除后，工务人员测量轨道，信号人员检查电缆和设备。检测车低速通过确认线路状态，所有工具与人员也必须全部撤出限界。","image":"story/railway-rescue-crane"},
{"title":"彩虹下重新通车","content":"云层散开，一道彩虹跨过山谷。调度中心收到合格报告后解除封锁，第一列客车按临时限速安全通过。救援最重要的不是快，而是每一步都可靠。","image":"story/railway-rescue-crane"}
]}', 1, 3);
