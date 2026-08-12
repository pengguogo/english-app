-- V56: 时空列车五部曲之三——1950 年代柴油电力机车
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(118, 5, '时空列车3：柴油巨人', 17, 0);

INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(531, 118, '柴油巨人的长途接力', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"1955 年的蓝色巨人","content":"怀表把彭泽宇送到 1955 年。他面前是一台流线型柴油电力机车，低沉的发动机声像巨人呼吸。工程师解释：柴油机带动发电机，电能再驱动牵引电动机。diesel 是柴油，electric motor 是电动机，两种技术在一台机车里合作。","image":"story/time-train-diesel-01"},
    {"title":"十二节货车","content":"今天列车要运送木材、机器和粮食，共有 12 节货车。前 5 节装木材，中间 4 节装机器，其余 3 节装粮食。彭泽宇计算 5+4+3=12，并用 long 描述长列车。编组单必须与实际车厢顺序完全一致。","image":"story/time-train-diesel-02"},
    {"title":"双机重联","content":"山区坡度较大，一台机车力量不够，于是工作人员在前方连接两台柴油机车。每台可安全牵引 6 节重载货车，两台合作正好牵引 12 节。double 表示双倍。连接完成后，他们还要测试制动和通信，不能只看动力够不够。","image":"story/time-train-diesel-03"},
    {"title":"油量计划表","content":"每台机车油箱计划使用 40 格燃料，长途任务预计消耗 28 格。彭泽宇算出每台还能保留 12 格。司机说明，保留燃料是为了应对绕行和等待，不能把油量计划到刚好为零。remaining fuel 表示剩余燃料。","image":"story/time-train-diesel-04"},
    {"title":"轴温报警","content":"列车穿过荒原时，第九节货车的轴温监测器发出警报。彭泽宇立即报出 car nine。司机把列车停到安全地点，检查员用仪器确认轴承温度偏高。他们摘下故障货车安排检修，其余车厢重新进行制动试验。","image":"story/time-train-diesel-05"},
    {"title":"少一节再计算","content":"摘下第九节货车后，列车从 12 节变成 11 节。粮食车原有 3 节，故障车正是其中一节，所以还剩 2 节粮食车。彭泽宇更新编组单，知道数学结果也要跟着真实变化及时修改。accurate 表示准确。","image":"story/time-train-diesel-06"},
    {"title":"无线电接力","content":"前方隧道只能允许一列车通过。调度员说 Wait at the signal，司机在红灯前等待。对向列车驶出后，调度员确认区间空闲，信号才变绿。两台机车通过无线电同步牵引，平稳驶过长坡和隧道。","image":"story/time-train-diesel-07"},
    {"title":"巨人的力量来自合作","content":"货物安全到达终点。彭泽宇发现柴油机车不用频繁停车加水，却仍需要燃料管理、设备监测和团队协作。工程师送他一枚闪亮的电动机线圈。怀表上出现 overhead wire，下一段旅程将把他带到电气化铁路。","image":"story/time-train-diesel-08"}
  ]
}', 1, 3),
(532, 118, '柴油巨人问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"柴油电力机车怎样驱动车轮？","options":["柴油机带动发电机，再由电动机驱动车轮","把蒸汽直接吹向车轮","只靠下坡滑行"],"answer":0,"image":"story/time-train-diesel-quiz"},
    {"question":"轴温报警后，工作人员为什么摘下故障货车？","options":["为了让列车颜色更好看","为了检修过热轴承并保证其余列车安全","因为货车里没有乘客"],"answer":1,"image":"story/time-train-diesel-quiz"},
    {"question":"diesel 的中文意思是什么？","options":["柴油","电线","隧道"],"answer":0,"image":"story/time-train-diesel-quiz"},
    {"question":"调度员说 Wait at the signal，司机应该怎样做？","options":["在信号前等待","立即加速","关闭无线电"],"answer":0,"image":"story/time-train-diesel-quiz"},
    {"question":"5 节木材车、4 节机器车和 3 节粮食车一共有多少节？","options":["11 节","12 节","13 节"],"answer":1,"image":"story/time-train-diesel-quiz"},
    {"question":"40 格燃料预计用掉 28 格，还剩多少格？","options":["10 格","11 格","12 格"],"answer":2,"image":"story/time-train-diesel-quiz"},
    {"question":"双机重联后为什么还要测试制动和通信？","options":["动力足够不代表整列车已经安全协同","为了让喇叭声音更大","只是为了延长出发时间"],"answer":0,"image":"story/time-train-diesel-quiz"},
    {"question":"柴油巨人的真正力量来自哪里？","options":["只来自更大的发动机","来自合理计算、设备监测和团队合作","来自不停车地一直前进"],"answer":1,"image":"story/time-train-diesel-quiz"}
  ]
}', 2, 3);
