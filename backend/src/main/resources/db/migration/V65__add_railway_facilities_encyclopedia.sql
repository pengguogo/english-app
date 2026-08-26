-- V65：新增“铁路设施大图鉴”章节。
-- 从线路土建、车站货场、信号供电和安全养护四个方面介绍 100 个铁路配套设施。

INSERT INTO unit (id, theme_id, name, sort_order, is_locked)
VALUES (151, 6, '铁路设施大图鉴', 4, 0);

INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(604, 151, '线路土建：火车脚下的基础', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"1. 钢轨","content":"钢轨是火车前进的两条钢铁道路。它用光滑坚硬的表面承受车轮的重量，并引导列车方向。","image":"railway-facilities/facility-001"},
    {"title":"2. 轨枕","content":"轨枕横放在钢轨下面，保持两根钢轨的距离，还把列车重量分散到道床上。","image":"railway-facilities/facility-002"},
    {"title":"3. 扣件","content":"扣件像结实的夹子，把钢轨固定在轨枕上。它也能缓冲一部分震动，防止钢轨随意移动。","image":"railway-facilities/facility-003"},
    {"title":"4. 道砟","content":"道砟是轨枕下面许多棱角分明的小石头。它们帮助排水、分散重量，并让轨道保持稳定。","image":"railway-facilities/facility-004"},
    {"title":"5. 路基","content":"路基是整条铁路的地面基础，像轨道脚下宽厚的底座，必须压实并保持稳定。","image":"railway-facilities/facility-005"},
    {"title":"6. 路堤","content":"地面较低时，人们用土石填出高高的路堤，让铁路保持合适的高度和坡度。","image":"railway-facilities/facility-006"},
    {"title":"7. 路堑","content":"铁路穿过高地时，会向下挖出一条通道，叫作路堑。两侧边坡需要加固并注意排水。","image":"railway-facilities/facility-007"},
    {"title":"8. 排水沟","content":"排水沟把雨水引离路基，避免泥土被泡软或冲走，是保护线路的重要小水道。","image":"railway-facilities/facility-008"},
    {"title":"9. 涵洞","content":"涵洞藏在路基下方，让小河、雨水或动物从铁路下面通过，而不必截断轨道。","image":"railway-facilities/facility-009"},
    {"title":"10. 挡土墙","content":"挡土墙支撑路堤或路堑旁的土石，减少边坡滑动，保护附近的线路。","image":"railway-facilities/facility-010"},
    {"title":"11. 铁路桥","content":"铁路桥托着轨道跨过河流、山谷或道路。桥梁要定期检查，确认结构能够安全承重。","image":"railway-facilities/facility-011"},
    {"title":"12. 高架桥","content":"高架桥用一排桥墩把铁路举到空中，让列车连续跨过城市道路或低洼地带。","image":"railway-facilities/facility-012"},
    {"title":"13. 隧道","content":"隧道让铁路穿过山体或城市地下。里面会设置照明、排水、通风和安全通道。","image":"railway-facilities/facility-013"}
  ]
}', 1, 3),
(605, 151, '线路土建：转弯与指路', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"14. 隧道洞门","content":"洞门是隧道出入口的加固结构，挡住松动土石和雨水，也让入口轮廓清楚可见。","image":"railway-facilities/facility-014"},
    {"title":"15. 声屏障","content":"声屏障立在线路旁，阻挡和吸收一部分列车噪声，减少对附近居民的影响。","image":"railway-facilities/facility-015"},
    {"title":"16. 道岔","content":"道岔让列车从一条轨道转到另一条轨道，像铁路上的岔路口。","image":"railway-facilities/facility-016"},
    {"title":"17. 尖轨","content":"尖轨是道岔中可以左右移动的细长钢轨。它贴向哪一边，就把车轮引向对应线路。","image":"railway-facilities/facility-017"},
    {"title":"18. 辙叉","content":"辙叉位在两条钢轨交叉处，留出轮缘通过的小缝，让车轮顺利越过交叉点。","image":"railway-facilities/facility-018"},
    {"title":"19. 转辙机","content":"转辙机推动尖轨到正确位置，并检查它是否锁牢，再把状态传给信号系统。","image":"railway-facilities/facility-019"},
    {"title":"20. 渡线","content":"渡线由一组道岔和连接轨道组成，让列车在两条平行线路之间换道。","image":"railway-facilities/facility-020"},
    {"title":"21. 车挡","content":"车挡装在线路尽头，提醒车辆不能再向前，并在低速意外移动时提供最后一道缓冲。","image":"railway-facilities/facility-021"},
    {"title":"22. 安全线","content":"安全线是一段通向安全方向的短轨道，能防止没有停稳的车辆闯入正在使用的正线。","image":"railway-facilities/facility-022"},
    {"title":"23. 公里标","content":"公里标写着线路位置，帮助司机和维修人员准确说明自己在铁路的哪一段。","image":"railway-facilities/facility-023"},
    {"title":"24. 曲线标","content":"曲线标记录弯道的半径等信息，方便线路人员检查弯道是否保持正确形状。","image":"railway-facilities/facility-024"},
    {"title":"25. 坡度标","content":"坡度标告诉工作人员前方线路是上坡、下坡还是平坡，以及坡度变化情况。","image":"railway-facilities/facility-025"}
  ]
}', 2, 3),
(606, 151, '线路土建问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"哪种设施把雨水引离铁路路基？","options":["排水沟","声屏障","公里标"],"answer":0,"image":"railway-facilities/track-overview"},
    {"question":"道砟除了分散重量，还能帮助做什么？","options":["排水并稳定轨道","售卖车票","升起栏杆"],"answer":0,"image":"railway-facilities/track-overview"},
    {"question":"哪种设施让列车从一条轨道转向另一条？","options":["涵洞","道岔","挡土墙"],"answer":1,"image":"railway-facilities/route-overview"},
    {"question":"转辙机移动尖轨后，还要确认什么？","options":["尖轨已经锁牢","站台时钟走得快","桥下没有小船"],"answer":0,"image":"railway-facilities/route-overview"},
    {"question":"公里标的主要作用是什么？","options":["说明线路位置","阻挡噪声","支撑隧道"],"answer":0,"image":"railway-facilities/route-overview"}
  ]
}', 3, 3),
(607, 151, '车站货场：旅客出发', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"26. 站房","content":"站房是车站服务旅客的主要建筑，里面常有售票、候车、问询和其他服务空间。","image":"railway-facilities/facility-026"},
    {"title":"27. 售票厅","content":"售票厅帮助旅客购买或改签车票，也提供票务咨询。电子购票普及后，它仍能服务有需要的人。","image":"railway-facilities/facility-027"},
    {"title":"28. 候车室","content":"候车室给尚未上车的旅客休息，并通过广播和屏幕告诉大家检票与发车信息。","image":"railway-facilities/facility-028"},
    {"title":"29. 站台","content":"站台是旅客安全上下车的区域。它的高度和边缘位置要与列车及线路保持规定距离。","image":"railway-facilities/facility-029"},
    {"title":"30. 雨棚","content":"站台雨棚为等车的旅客遮雨挡晒，同时给照明、广播等设备提供安装位置。","image":"railway-facilities/facility-030"},
    {"title":"31. 站台安全线","content":"安全线提醒旅客在列车到来前与站台边缘保持距离，不能把它当作普通装饰线。","image":"railway-facilities/facility-031"},
    {"title":"32. 盲道","content":"盲道用凸起的纹理为视障旅客提供脚下提示，帮助他们辨认行走方向和危险边缘。","image":"railway-facilities/facility-032"},
    {"title":"33. 天桥","content":"车站天桥让旅客从轨道上方前往另一座站台，不必直接穿越铁路。","image":"railway-facilities/facility-033"},
    {"title":"34. 地道","content":"车站地道从轨道下方连接各站台，让人流与列车运行空间分开。","image":"railway-facilities/facility-034"},
    {"title":"35. 自动扶梯","content":"自动扶梯连续运送旅客上下楼。乘坐时要站稳、握好扶手，并照看随行儿童。","image":"railway-facilities/facility-035"},
    {"title":"36. 无障碍电梯","content":"无障碍电梯帮助轮椅使用者、老人和携带大件行李的旅客往返不同楼层。","image":"railway-facilities/facility-036"},
    {"title":"37. 检票闸机","content":"检票闸机读取车票或电子票，确认有效后打开通道，并帮助车站组织进出站人流。","image":"railway-facilities/facility-037"},
    {"title":"38. 信息显示屏","content":"信息显示屏展示车次、时间、站台和运行变化，旅客要同时留意屏幕与广播。","image":"railway-facilities/facility-038"}
  ]
}', 4, 3),
(608, 151, '车站货场：服务与装卸', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"39. 广播系统","content":"广播系统把发车、到站和安全提示传到车站各处，遇到变化时还能快速通知旅客。","image":"railway-facilities/facility-039"},
    {"title":"40. 站台时钟","content":"站台时钟提供统一而醒目的时间，方便旅客和工作人员核对运行安排。","image":"railway-facilities/facility-040"},
    {"title":"41. 站名牌","content":"站名牌清楚写出当前车站名称，有些还会标出相邻车站，帮助旅客确认位置。","image":"railway-facilities/facility-041"},
    {"title":"42. 站台屏蔽门","content":"屏蔽门把站台与轨道隔开，列车停准并确认安全后才会与车门配合打开。","image":"railway-facilities/facility-042"},
    {"title":"43. 行李寄存处","content":"行李寄存处让旅客临时存放符合规定的物品，取回时需要核对凭证。","image":"railway-facilities/facility-043"},
    {"title":"44. 公共卫生间","content":"车站卫生间是重要的基本服务设施，会设置清洁、通风和无障碍空间。","image":"railway-facilities/facility-044"},
    {"title":"45. 饮水处","content":"饮水处为旅客提供可以饮用的水，出水口和接水区域需要保持卫生。","image":"railway-facilities/facility-045"},
    {"title":"46. 货场","content":"货场是铁路集中接收、编组、装卸和交付货物的区域，通常与旅客活动区分开。","image":"railway-facilities/facility-046"},
    {"title":"47. 货运站台","content":"货运站台让仓库或车辆与货车车厢接近同一高度，方便安全搬运货物。","image":"railway-facilities/facility-047"},
    {"title":"48. 铁路仓库","content":"铁路仓库临时保存等待运输或领取的货物，能遮挡风雨并方便分类管理。","image":"railway-facilities/facility-048"},
    {"title":"49. 装卸起重机","content":"装卸起重机吊起集装箱和重物，把它们在火车与卡车或堆场之间移动。","image":"railway-facilities/facility-049"},
    {"title":"50. 驼峰调车场","content":"驼峰调车场有一个缓坡，货车越过坡顶后慢慢溜向不同股道，再组成新的列车。","image":"railway-facilities/facility-050"}
  ]
}', 5, 3),
(609, 151, '车站货场问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"哪种设施让旅客从轨道上方前往另一座站台？","options":["天桥","货场","饮水处"],"answer":0,"image":"railway-facilities/station-overview"},
    {"question":"盲道主要为哪类旅客提供脚下提示？","options":["视障旅客","列车司机","货运调度员"],"answer":0,"image":"railway-facilities/station-overview"},
    {"question":"信息显示屏通常会展示什么？","options":["车次、时间和站台","轨枕的重量","隧道的石头数量"],"answer":0,"image":"railway-facilities/station-overview"},
    {"question":"吊运集装箱和重物的是哪种设施？","options":["站名牌","装卸起重机","站台时钟"],"answer":1,"image":"railway-facilities/freight-overview"},
    {"question":"货场为什么通常与旅客活动区分开？","options":["便于安全组织装卸和车辆作业","让站房显得更高","为了遮住站名牌"],"answer":0,"image":"railway-facilities/freight-overview"}
  ]
}', 6, 3),
(610, 151, '信号通信：看得见的指令', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"51. 进站信号机","content":"进站信号机设在车站入口外，告诉司机是否可以进入车站以及应按什么条件行驶。","image":"railway-facilities/facility-051"},
    {"title":"52. 出站信号机","content":"出站信号机控制列车从车站进入前方线路。只有显示允许时，列车才能按规定出发。","image":"railway-facilities/facility-052"},
    {"title":"53. 调车信号机","content":"调车信号机为站内移动和重新编组车辆提供指示，外形和含义与正线信号有所不同。","image":"railway-facilities/facility-053"},
    {"title":"54. 进路表示器","content":"前方有多个方向时，进路表示器帮助司机确认已经为列车准备好哪一条路线。","image":"railway-facilities/facility-054"},
    {"title":"55. 信号楼","content":"传统信号楼里集中设置控制设备，工作人员曾在这里操纵附近的道岔和信号。","image":"railway-facilities/facility-055"},
    {"title":"56. 联锁设备","content":"联锁设备检查道岔、信号和线路状态，阻止互相冲突的行车路线同时建立。","image":"railway-facilities/facility-056"},
    {"title":"57. 轨道电路","content":"轨道电路利用钢轨中的电信号判断一段线路是否被列车占用，并把结果交给信号系统。","image":"railway-facilities/facility-057"},
    {"title":"58. 计轴器","content":"计轴器在区段两端数车轴。进入和离开的车轴数相同，系统才能判断列车已经完整通过。","image":"railway-facilities/facility-058"},
    {"title":"59. 地面应答器","content":"地面应答器装在线路中间，列车经过时会交换位置、限速或线路状态等信息。","image":"railway-facilities/facility-059"},
    {"title":"60. 闭塞设备","content":"闭塞设备把线路划分成受保护的区段，帮助列车之间保持安全间隔。","image":"railway-facilities/facility-060"},
    {"title":"61. 调度中心","content":"调度中心汇集大范围线路和列车信息，工作人员在这里协调运行并处理变化。","image":"railway-facilities/facility-061"},
    {"title":"62. 调度台","content":"调度台把线路图、列车位置和通信工具集中在屏幕与控制设备上，便于统一观察。","image":"railway-facilities/facility-062"},
    {"title":"63. 铁路无线电基站","content":"无线电基站在列车、车站和调度之间传递语音或数据，让移动中的列车保持联系。","image":"railway-facilities/facility-063"}
  ]
}', 7, 3),
(611, 151, '信号供电：隐形的守护网', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"64. 光纤通信柜","content":"光纤通信柜连接沿线光缆和设备，让信号、视频与调度数据快速传送。","image":"railway-facilities/facility-064"},
    {"title":"65. 监控摄像机","content":"监控摄像机帮助工作人员观察站台、道口和设备区域，但仍需要人员按规则判断和处理。","image":"railway-facilities/facility-065"},
    {"title":"66. 轴温探测站","content":"轴温探测站在列车通过时测量轴箱温度，发现异常过热会把信息发送给工作人员。","image":"railway-facilities/facility-066"},
    {"title":"67. 拖挂物探测器","content":"拖挂物探测器检查列车底部是否有不该垂下的物体，减少它们碰撞线路设备的风险。","image":"railway-facilities/facility-067"},
    {"title":"68. 动态轨道衡","content":"动态轨道衡在车辆缓慢通过时测量重量，帮助发现超载或各车轮受力不均。","image":"railway-facilities/facility-068"},
    {"title":"69. 铁路气象站","content":"铁路气象站测量风、雨、雪和温度，为运行调整和线路检查提供环境信息。","image":"railway-facilities/facility-069"},
    {"title":"70. 接触网支柱","content":"接触网支柱站在线路两旁，支撑横梁、悬挂线和接触线，并保持规定位置。","image":"railway-facilities/facility-070"},
    {"title":"71. 接触线","content":"接触线悬在电力列车上方，受电弓滑板贴着它移动，把电能送到列车。","image":"railway-facilities/facility-071"},
    {"title":"72. 张力补偿装置","content":"天气冷热会让电线伸缩，张力补偿装置用重锤或弹簧帮助接触线保持合适张力。","image":"railway-facilities/facility-072"},
    {"title":"73. 牵引变电所","content":"牵引变电所把公共电网的电转换成铁路需要的形式，再分送到接触网。","image":"railway-facilities/facility-073"},
    {"title":"74. 分相区","content":"分相区把不同供电区段安全隔开。电力列车通过时要按系统要求完成供电切换。","image":"railway-facilities/facility-074"},
    {"title":"75. 第三轨","content":"有些地铁不用架空电线，而由轨道旁带电的第三轨供电。它有防护罩并禁止人员靠近。","image":"railway-facilities/facility-075"}
  ]
}', 8, 3),
(612, 151, '信号供电问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"联锁设备为什么会阻止冲突路线同时建立？","options":["避免列车走进互相冲突的进路","让广播声音更大","给站台遮雨"],"answer":0,"image":"railway-facilities/signal-overview"},
    {"question":"计轴器怎样帮助判断列车完整通过？","options":["比较进入和离开的车轴数","测量车票大小","观察车身颜色"],"answer":0,"image":"railway-facilities/signal-overview"},
    {"question":"轴温探测站重点寻找什么异常？","options":["轴箱过热","站台积水","候车室太亮"],"answer":0,"image":"railway-facilities/detection-overview"},
    {"question":"牵引变电所把电送到哪里？","options":["接触网","排水沟","货运仓库"],"answer":0,"image":"railway-facilities/power-overview"},
    {"question":"张力补偿装置应对电线的哪种变化？","options":["因冷热而伸缩","因广播而振动","因车票而变色"],"answer":0,"image":"railway-facilities/power-overview"}
  ]
}', 9, 3),
(613, 151, '安全设施：道路遇见铁路', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"76. 平交道口","content":"平交道口是道路与铁路在同一高度相交的地方。行人和车辆必须遵守标志、灯光与栏杆提示。","image":"railway-facilities/facility-076"},
    {"title":"77. 道口栏杆","content":"列车接近时，道口栏杆会降下挡住来车方向。任何人都不能绕过或钻过已经关闭的栏杆。","image":"railway-facilities/facility-077"},
    {"title":"78. 道口闪光灯","content":"红色闪光灯交替闪烁，清楚提醒道路使用者列车正在接近或占用道口。","image":"railway-facilities/facility-078"},
    {"title":"79. 道口警铃","content":"警铃用连续声音补充灯光提示，让没有正看着信号的人也能注意到道口关闭。","image":"railway-facilities/facility-079"},
    {"title":"80. 道口标志牌","content":"醒目的交叉形或警告标志告诉人们前方有铁路，接近时要减速观察并服从信号。","image":"railway-facilities/facility-080"},
    {"title":"81. 道口铺面","content":"道口铺面填平两根钢轨之间和附近的路面，让汽车、自行车和行人较平稳地通过。","image":"railway-facilities/facility-081"},
    {"title":"82. 行人道口门","content":"行人道口门在列车接近时限制行人进入轨道，有些通道还会用折线路径提醒人先观察。","image":"railway-facilities/facility-082"},
    {"title":"83. 道口紧急电话","content":"车辆在道口发生故障等紧急情况时，成年人可按现场说明用专用电话联系铁路控制人员。","image":"railway-facilities/facility-083"},
    {"title":"84. 公路跨线桥","content":"公路跨线桥让汽车和行人从铁路上方通过，把道路交通与列车运行分开。","image":"railway-facilities/facility-084"},
    {"title":"85. 公路下穿通道","content":"下穿通道让道路从铁路下方穿过，是消除平面交叉的一种方式，同时要做好排水。","image":"railway-facilities/facility-085"},
    {"title":"86. 铁路防护栅栏","content":"防护栅栏划出铁路运行区域，阻止行人和动物随意进入线路。","image":"railway-facilities/facility-086"},
    {"title":"87. 入侵报警装置","content":"入侵报警装置用传感器发现有人或物体进入受保护区域，并把警报送给工作人员核实。","image":"railway-facilities/facility-087"},
    {"title":"88. 线路巡检车","content":"线路巡检车载着检测设备观察钢轨、轨道形状和沿线设施，帮助工作人员发现异常。","image":"railway-facilities/facility-088"}
  ]
}', 10, 3),
(614, 151, '养护基地：让铁路状态良好', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"89. 线路养护基地","content":"养护基地存放工具、材料和工程车辆，维修队从这里出发检查和修复线路。","image":"railway-facilities/facility-089"},
    {"title":"90. 机务段","content":"机务段是机车停放、检查、清洁和检修的基地，也为工作人员提供整备空间。","image":"railway-facilities/facility-090"},
    {"title":"91. 机车转盘","content":"机车转盘中间有一段可以旋转的轨道，能把机车转向或送入不同的检修股道。","image":"railway-facilities/facility-091"},
    {"title":"92. 检修地沟","content":"检修地沟位在轨道下方，让维修人员能在安全防护下观察车辆底部。","image":"railway-facilities/facility-092"},
    {"title":"93. 洗车库","content":"洗车库用喷淋和刷洗设备清洁列车外部，并收集处理清洗用水。","image":"railway-facilities/facility-093"},
    {"title":"94. 加油设施","content":"柴油机车在专用加油区补充燃料，现场设置防火、防泄漏和计量设备。","image":"railway-facilities/facility-094"},
    {"title":"95. 加砂设施","content":"加砂设施把干燥细沙装进机车砂箱，供车轮在湿滑线路上需要时使用。","image":"railway-facilities/facility-095"},
    {"title":"96. 水塔","content":"蒸汽铁路时代，水塔把大量水储存在高处，再通过水鹤快速加入机车水箱。","image":"railway-facilities/facility-096"},
    {"title":"97. 上煤台","content":"上煤台曾把煤炭提升并装入蒸汽机车的煤水车，是历史铁路的重要补给设施。","image":"railway-facilities/facility-097"},
    {"title":"98. 防雪棚","content":"多雪山区会用坚固的防雪棚罩住线路，让雪崩或大量积雪从棚顶越过。","image":"railway-facilities/facility-098"},
    {"title":"99. 雪崩防护网","content":"雪崩防护网设置在山坡上，拦住或减缓滑动积雪，降低雪冲向铁路的风险。","image":"railway-facilities/facility-099"},
    {"title":"100. 应急救援通道","content":"应急救援通道为消防、医疗和维修人员接近铁路提供路线，平时必须保持清楚畅通。","image":"railway-facilities/facility-100"}
  ]
}', 11, 3),
(615, 151, '安全养护问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"道路与铁路在同一高度相交的地方叫什么？","options":["平交道口","牵引变电所","驼峰调车场"],"answer":0,"image":"railway-facilities/crossing-overview"},
    {"question":"道口栏杆已经降下时，应该怎样做？","options":["停在安全位置等待","绕过栏杆抢行","从栏杆下面钻过"],"answer":0,"image":"railway-facilities/crossing-overview"},
    {"question":"哪种设施把道路交通与铁路从上下方向分开？","options":["跨线桥或下穿通道","站台时钟","加砂设施"],"answer":0,"image":"railway-facilities/crossing-overview"},
    {"question":"检修地沟为什么设在轨道下方？","options":["便于检查车辆底部","储存旅客行李","收集车票"],"answer":0,"image":"railway-facilities/maintenance-overview"},
    {"question":"防雪棚主要保护哪种环境中的铁路？","options":["多雪山区","室内车站","海上轮船"],"answer":0,"image":"railway-facilities/maintenance-overview"}
  ]
}', 12, 3);
