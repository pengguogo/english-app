-- V63: 新增“火车零件大图鉴”章节。
-- 按蒸汽、柴油、电力和高速动车组四个时代介绍 100 个零件，并配套 20 道问答。

INSERT INTO unit (id, theme_id, name, sort_order, is_locked)
VALUES (147, 6, '火车零件大图鉴', 3, 0);

INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(589, 147, '蒸汽机车：锅炉与火焰', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"1. 烟囱","content":"烟囱把燃烧产生的烟和热气送到车顶外面。向上冲的气流还会帮助炉火烧得更旺。","image":"train-parts/part-001"},
    {"title":"2. 烟箱","content":"烟箱在锅炉最前面，收集穿过烟管的热气和烟灰，再把它们引向烟囱。","image":"train-parts/part-002"},
    {"title":"3. 锅炉","content":"锅炉像一个巨大的烧水壶。火把水加热成高压蒸汽，蒸汽就是机车的力量。","image":"train-parts/part-003"},
    {"title":"4. 火箱","content":"火箱是烧煤的地方，外面被锅炉水包围。它把煤的热量传给水。","image":"train-parts/part-004"},
    {"title":"5. 炉排","content":"炉排像一张结实的铁网，煤放在上面燃烧，空气可以从网孔下方进入。","image":"train-parts/part-005"},
    {"title":"6. 灰箱","content":"灰箱装在炉排下方，接住烧完的煤灰。停车后，工作人员会安全清理它。","image":"train-parts/part-006"},
    {"title":"7. 煤水车","content":"煤水车连在机车后面，带着煤和水一起旅行，像机车背后的补给背包。","image":"train-parts/part-007"},
    {"title":"8. 煤舱","content":"煤舱专门装煤，司炉把煤从这里送进火箱，让炉火持续燃烧。","image":"train-parts/part-008"},
    {"title":"9. 水箱","content":"水箱储存锅炉要用的水。水快用完时，机车就要到补水点加水。","image":"train-parts/part-009"},
    {"title":"10. 蒸汽穹顶","content":"蒸汽穹顶是锅炉顶部的小圆包，较干燥的蒸汽会聚集在这里。","image":"train-parts/part-010"},
    {"title":"11. 调节阀","content":"司机扳动调节阀，控制送往气缸的蒸汽多少，就像控制动力大小的水龙头。","image":"train-parts/part-011"},
    {"title":"12. 安全阀","content":"锅炉压力太高时，安全阀会自动放出一部分蒸汽，保护锅炉。","image":"train-parts/part-012"},
    {"title":"13. 压力表","content":"压力表告诉司机锅炉里的蒸汽压力。指针必须保持在安全范围内。","image":"train-parts/part-013"}
  ]
}', 1, 3),
(590, 147, '蒸汽机车：车轮怎样转', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"14. 汽笛","content":"蒸汽通过汽笛时会发出响亮的呜声，用来提醒车站和铁路附近的人。","image":"train-parts/part-014"},
    {"title":"15. 气缸","content":"高压蒸汽进入气缸，推动里面的活塞来回移动。这里是蒸汽变成动作的地方。","image":"train-parts/part-015"},
    {"title":"16. 活塞","content":"活塞像一块会滑动的圆饼，被蒸汽从两边轮流推动。","image":"train-parts/part-016"},
    {"title":"17. 活塞杆","content":"活塞杆把藏在气缸里的活塞动作传到外面的连杆机构。","image":"train-parts/part-017"},
    {"title":"18. 十字头","content":"十字头沿着直线滑动，让活塞杆不乱晃，并把力量交给主连杆。","image":"train-parts/part-018"},
    {"title":"19. 主连杆","content":"主连杆连接十字头和主动轮，把来回运动变成车轮的旋转。","image":"train-parts/part-019"},
    {"title":"20. 联动杆","content":"联动杆把几个大车轮连在一起，让它们一起用力转动。","image":"train-parts/part-020"},
    {"title":"21. 主动轮","content":"主动轮是接受蒸汽动力的大车轮，轮子越大，同样转一圈走得越远。","image":"train-parts/part-021"},
    {"title":"22. 导轮","content":"导轮装在机车前部，帮助长长的机车平稳进入弯道。","image":"train-parts/part-022"},
    {"title":"23. 从轮","content":"从轮支撑火箱和驾驶室的重量，让机车前后更平衡。","image":"train-parts/part-023"},
    {"title":"24. 制动闸瓦","content":"刹车时，闸瓦压住车轮踏面，用摩擦力让车轮慢下来。","image":"train-parts/part-024"},
    {"title":"25. 砂箱","content":"车轮打滑时，砂箱把少量干沙撒到钢轨上，帮助车轮抓得更牢。","image":"train-parts/part-025"}
  ]
}', 2, 3),
(591, 147, '蒸汽零件问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"哪个零件像大烧水壶，把水变成蒸汽？","options":["锅炉","砂箱","导轮"],"answer":0,"image":"train-parts/steam-boiler-plate"},
    {"question":"锅炉压力太高时，谁会自动放出蒸汽？","options":["汽笛","安全阀","煤舱"],"answer":1,"image":"train-parts/steam-boiler-plate"},
    {"question":"主连杆把来回运动变成什么？","options":["车轮旋转","煤灰","灯光"],"answer":0,"image":"train-parts/steam-running-plate"},
    {"question":"车轮打滑时，砂箱会撒下什么？","options":["水","机油","干沙"],"answer":2,"image":"train-parts/steam-running-plate"},
    {"question":"哪组顺序能让蒸汽机车前进？","options":["煤燃烧—水变蒸汽—活塞移动—车轮转动","车轮转动—水结冰—煤燃烧","汽笛鸣响—座椅移动—车门打开"],"answer":0,"image":"train-parts/steam-running-plate"}
  ]
}', 3, 3),
(592, 147, '柴油机车：发动机舱', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"26. 柴油机","content":"柴油机在气缸里燃烧柴油，把燃料的能量变成旋转的力量。","image":"train-parts/part-026"},
    {"title":"27. 气缸体","content":"气缸体是发动机结实的主体，里面有让活塞上下运动的圆筒。","image":"train-parts/part-027"},
    {"title":"28. 活塞","content":"燃烧的气体向下推活塞，活塞再把力量传给连杆。","image":"train-parts/part-028"},
    {"title":"29. 连杆","content":"连杆连接活塞和曲轴，把活塞的上下运动传给曲轴。","image":"train-parts/part-029"},
    {"title":"30. 曲轴","content":"曲轴把活塞的上下运动变成连续旋转，像发动机里的大摇柄。","image":"train-parts/part-030"},
    {"title":"31. 凸轮轴","content":"凸轮轴按准确时间打开和关闭气门，让空气进来、废气出去。","image":"train-parts/part-031"},
    {"title":"32. 喷油器","content":"喷油器把柴油喷成细小油雾，油雾更容易在气缸里燃烧。","image":"train-parts/part-032"},
    {"title":"33. 涡轮增压器","content":"涡轮增压器利用排气的力量压入更多空气，让发动机更有劲。","image":"train-parts/part-033"},
    {"title":"34. 空气滤清器","content":"空气滤清器拦住灰尘，让干净空气进入发动机。","image":"train-parts/part-034"},
    {"title":"35. 进气管","content":"进气管像空气通道，把过滤后的空气送到各个气缸。","image":"train-parts/part-035"},
    {"title":"36. 排气管","content":"排气管把燃烧后的废气安全引到车外。","image":"train-parts/part-036"},
    {"title":"37. 散热器","content":"散热器把冷却液带来的热量散到空气中，防止发动机过热。","image":"train-parts/part-037"},
    {"title":"38. 冷却风扇","content":"冷却风扇让空气快速穿过散热器，帮助发动机降温。","image":"train-parts/part-038"}
  ]
}', 4, 3),
(593, 147, '柴油机车：电传动', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"39. 燃油箱","content":"燃油箱储存柴油，通常装在车架下方，外壳很结实。","image":"train-parts/part-039"},
    {"title":"40. 机油滤清器","content":"机油滤清器捉住机油里的小杂质，保护不停运动的零件。","image":"train-parts/part-040"},
    {"title":"41. 主发电机","content":"柴油机带动主发电机旋转，把机械能变成电能。","image":"train-parts/part-041"},
    {"title":"42. 整流器","content":"整流器把电流整理成牵引系统需要的形式，再送给电动机。","image":"train-parts/part-042"},
    {"title":"43. 牵引电动机","content":"牵引电动机装在转向架附近，用电力转动车轮。","image":"train-parts/part-043"},
    {"title":"44. 蓄电池","content":"蓄电池在机车还没启动时，为控制、照明等设备提供电。","image":"train-parts/part-044"},
    {"title":"45. 控制柜","content":"控制柜里有许多电器元件，按司机指令分配和保护电力。","image":"train-parts/part-045"},
    {"title":"46. 司机控制器","content":"司机推动控制手柄，选择前进方向和牵引力大小。","image":"train-parts/part-046"},
    {"title":"47. 转向架","content":"转向架托住车体并能在弯道中转动，上面装着轮对、弹簧和电动机。","image":"train-parts/part-047"},
    {"title":"48. 车轴","content":"车轴把左右两个车轮牢牢连成一组，让它们一起转动。","image":"train-parts/part-048"},
    {"title":"49. 轴箱","content":"轴箱包住车轴两端的轴承，让车轴能顺畅旋转并承受重量。","image":"train-parts/part-049"},
    {"title":"50. 空气压缩机","content":"空气压缩机把空气压进储气罐，为列车的空气制动准备力量。","image":"train-parts/part-050"}
  ]
}', 5, 3),
(594, 147, '柴油零件问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"谁把柴油喷成细小油雾？","options":["喷油器","散热器","轴箱"],"answer":0,"image":"train-parts/diesel-engine-plate"},
    {"question":"发动机太热时，哪组零件帮助降温？","options":["散热器和冷却风扇","车轴和轴箱","蓄电池和控制柜"],"answer":0,"image":"train-parts/diesel-engine-plate"},
    {"question":"柴油电力机车里，主发电机做什么？","options":["把机械能变成电能","储存煤炭","打开车门"],"answer":0,"image":"train-parts/diesel-drive-plate"},
    {"question":"最后直接用电转动车轮的是谁？","options":["牵引电动机","燃油箱","空气滤清器"],"answer":0,"image":"train-parts/diesel-drive-plate"},
    {"question":"转向架上通常装着什么？","options":["轮对、弹簧和电动机","座椅和行李架","锅炉和烟囱"],"answer":0,"image":"train-parts/diesel-drive-plate"}
  ]
}', 6, 3),
(595, 147, '电力机车：从电线取电', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"51. 受电弓","content":"受电弓升到车顶上方，轻轻贴住接触网，把电送进机车。","image":"train-parts/part-051"},
    {"title":"52. 滑板","content":"滑板是受电弓最上面的长条，和电线接触时会慢慢磨损，需要检查更换。","image":"train-parts/part-052"},
    {"title":"53. 绝缘子","content":"绝缘子把带高压电的设备和金属车体隔开，防止电走错路。","image":"train-parts/part-053"},
    {"title":"54. 主断路器","content":"发生异常或需要断电时，主断路器会迅速切断高压电。","image":"train-parts/part-054"},
    {"title":"55. 避雷器","content":"雷电带来突然的高电压时，避雷器把危险电流引走，保护设备。","image":"train-parts/part-055"},
    {"title":"56. 高压电缆","content":"高压电缆像有厚外衣的电力通道，把电安全送到主变压器。","image":"train-parts/part-056"},
    {"title":"57. 主变压器","content":"主变压器把接触网的高电压变成机车不同设备需要的电压。","image":"train-parts/part-057"},
    {"title":"58. 冷却油泵","content":"冷却油泵让绝缘油循环流动，带走主变压器工作时产生的热。","image":"train-parts/part-058"},
    {"title":"59. 牵引变流器","content":"牵引变流器精细调整电流，让牵引电动机按需要快转或慢转。","image":"train-parts/part-059"},
    {"title":"60. 牵引电动机","content":"牵引电动机把电能变成旋转力量，再通过齿轮带动车轮。","image":"train-parts/part-060"},
    {"title":"61. 辅助变流器","content":"辅助变流器为风扇、空调、照明等辅助设备准备合适的电。","image":"train-parts/part-061"},
    {"title":"62. 蓄电池箱","content":"外部电源中断时，蓄电池箱还能为重要控制和照明设备供电。","image":"train-parts/part-062"},
    {"title":"63. 接地装置","content":"接地装置给电流提供安全回路，也帮助检修人员确认设备已经安全断电。","image":"train-parts/part-063"}
  ]
}', 7, 3),
(596, 147, '电力机车：走行与制动', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"64. 转向架","content":"转向架像车体脚下的小车，托住机车并帮助它通过弯道。","image":"train-parts/part-064"},
    {"title":"65. 轮对","content":"两个车轮和一根车轴组成轮对，钢轮沿着两根钢轨滚动。","image":"train-parts/part-065"},
    {"title":"66. 轴箱","content":"轴箱保护车轴两端的轴承，并把车体重量传给轮对。","image":"train-parts/part-066"},
    {"title":"67. 一级悬挂","content":"一级悬挂在轮对和转向架之间，先吸收来自钢轨的小颠簸。","image":"train-parts/part-067"},
    {"title":"68. 二级悬挂","content":"二级悬挂在转向架和车体之间，让驾驶室和设备少摇晃。","image":"train-parts/part-068"},
    {"title":"69. 齿轮传动","content":"大小齿轮互相咬合，把电动机的高速旋转变成适合车轮的转速。","image":"train-parts/part-069"},
    {"title":"70. 空气压缩机","content":"空气压缩机制造压缩空气，为制动和其他气动设备供气。","image":"train-parts/part-070"},
    {"title":"71. 主风缸","content":"主风缸储存压缩空气，像列车刹车系统的空气仓库。","image":"train-parts/part-071"},
    {"title":"72. 制动盘","content":"制动盘跟着车轴转动，刹车时被夹住后产生摩擦。","image":"train-parts/part-072"},
    {"title":"73. 制动夹钳","content":"制动夹钳把闸片压向制动盘，让列车安全减速。","image":"train-parts/part-073"},
    {"title":"74. 撒砂装置","content":"钢轨湿滑时，撒砂装置把沙送到轮轨接触处，增加抓地力。","image":"train-parts/part-074"},
    {"title":"75. 车钩","content":"车钩把机车和车辆可靠连在一起，也方便需要时分开。","image":"train-parts/part-075"}
  ]
}', 8, 3),
(597, 147, '电力零件问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"电力机车从接触网取电，最先接触电线的是谁？","options":["受电弓滑板","车钩","主风缸"],"answer":0,"image":"train-parts/electric-roof-plate"},
    {"question":"发生高压异常时，谁能迅速切断电路？","options":["主断路器","二级悬挂","制动盘"],"answer":0,"image":"train-parts/electric-roof-plate"},
    {"question":"主变压器的主要工作是什么？","options":["改变电压","储存空气","擦干车窗"],"answer":0,"image":"train-parts/electric-roof-plate"},
    {"question":"刹车时，制动夹钳夹住什么？","options":["受电弓","制动盘","车钩"],"answer":1,"image":"train-parts/electric-running-plate"},
    {"question":"一级和二级悬挂共同帮助列车做什么？","options":["减少颠簸和摇晃","制造柴油","升高电压"],"answer":0,"image":"train-parts/electric-running-plate"}
  ]
}', 9, 3),
(598, 147, '高速列车：车头与客室', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"76. 流线型车头","content":"圆滑修长的车头让空气更顺畅地绕过列车，减少高速行驶的阻力和噪声。","image":"train-parts/part-076"},
    {"title":"77. 车体外壳","content":"车体外壳挡住风雨，也要保持表面平整，帮助列车轻快前进。","image":"train-parts/part-077"},
    {"title":"78. 铝合金骨架","content":"轻而结实的铝合金骨架撑起车体，减轻重量也保护车内空间。","image":"train-parts/part-078"},
    {"title":"79. 挡风玻璃","content":"驾驶室挡风玻璃能承受高速气流，让司机清楚观察前方。","image":"train-parts/part-079"},
    {"title":"80. 驾驶台","content":"驾驶台集中显示速度、信号和列车状态，司机在这里发出控制指令。","image":"train-parts/part-080"},
    {"title":"81. 头灯","content":"头灯让列车在暗处更容易被看见，也照亮近处线路标志。","image":"train-parts/part-081"},
    {"title":"82. 雨刷","content":"雨刷扫掉挡风玻璃上的雨水，帮助司机在雨天保持视线清楚。","image":"train-parts/part-082"},
    {"title":"83. 塞拉门","content":"塞拉门关闭后会向内收紧，与车身表面接近平齐，既密封又减少风阻。","image":"train-parts/part-083"},
    {"title":"84. 车门机构","content":"车门机构带动车门开关，并检查门是否真正锁好后才允许发车。","image":"train-parts/part-084"},
    {"title":"85. 车窗","content":"多层车窗隔开高速气流和噪声，让旅客安全、安静地看风景。","image":"train-parts/part-085"},
    {"title":"86. 座椅","content":"座椅支撑旅客身体，靠背、扶手和安全材料让长途乘坐更舒适。","image":"train-parts/part-086"},
    {"title":"87. 行李架","content":"行李架放置较轻的随身行李，重物和大箱子要放到规定位置。","image":"train-parts/part-087"},
    {"title":"88. 空调风口","content":"空调风口把处理过的空气均匀送进车厢，调节温度并保持空气流动。","image":"train-parts/part-088"}
  ]
}', 10, 3),
(599, 147, '高速列车：车底的秘密', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"89. 高速转向架","content":"高速转向架要轻、稳又结实，带着轮对平顺通过直线和弯道。","image":"train-parts/part-089"},
    {"title":"90. 轮对","content":"高速轮对尺寸非常精确，还要定期检测，避免细小损伤变大。","image":"train-parts/part-090"},
    {"title":"91. 轴箱","content":"轴箱里的轴承让车轴高速旋转，传感器还会关注它的温度和状态。","image":"train-parts/part-091"},
    {"title":"92. 空气弹簧","content":"空气弹簧像充气软垫，支撑车体并隔开一部分震动。","image":"train-parts/part-092"},
    {"title":"93. 减振器","content":"减振器让车体的摆动更快停下来，帮助列车保持平稳。","image":"train-parts/part-093"},
    {"title":"94. 牵引电动机","content":"牵引电动机分布在动车的转向架上，让多节车厢一起出力。","image":"train-parts/part-094"},
    {"title":"95. 齿轮箱","content":"齿轮箱保护传动齿轮，把电动机的旋转可靠传给轮对。","image":"train-parts/part-095"},
    {"title":"96. 制动盘","content":"高速列车的制动盘把运动能量变成热量，帮助列车减速停车。","image":"train-parts/part-096"},
    {"title":"97. 受电弓","content":"高速受电弓要在强气流中稳定贴住接触网，连续获得电力。","image":"train-parts/part-097"},
    {"title":"98. 列车控制电脑","content":"列车控制电脑收集许多设备的信息，协调牵引、制动和故障提示。","image":"train-parts/part-098"},
    {"title":"99. 速度传感器","content":"速度传感器不断测量车轮和列车速度，把数据交给控制系统。","image":"train-parts/part-099"},
    {"title":"100. 应答器天线","content":"列车经过轨道应答器时，天线接收线路和限速等信息，帮助列车安全运行。","image":"train-parts/part-100"}
  ]
}', 11, 3),
(600, 147, '高速零件问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"流线型车头为什么圆滑修长？","options":["减少空气阻力和噪声","多装煤炭","让车轮变大"],"answer":0,"image":"train-parts/high-speed-body-plate"},
    {"question":"车门机构确认什么以后，列车才可以发车？","options":["车门已经锁好","行李架是空的","所有窗帘拉上"],"answer":0,"image":"train-parts/high-speed-body-plate"},
    {"question":"哪种零件像充气软垫，帮助隔开震动？","options":["空气弹簧","雨刷","头灯"],"answer":0,"image":"train-parts/high-speed-systems-plate"},
    {"question":"列车控制电脑会协调哪些工作？","options":["牵引、制动和故障提示","卖票和做饭","铺设钢轨"],"answer":0,"image":"train-parts/high-speed-systems-plate"},
    {"question":"从蒸汽机车到高速列车，哪件事一直很重要？","options":["烧煤越多越好","各零件合作并按规则检查安全","所有车轮一样大"],"answer":1,"image":"train-parts/high-speed-systems-plate"}
  ]
}', 12, 3);
