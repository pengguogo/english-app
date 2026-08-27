-- V66: 为现有问答课追加高阶题。
-- 每课保留原有基础题，并增加需要综合信息、判断故障或选择处置方案的题目。

UPDATE lesson
SET content = json_insert(
    content,
    '$.items[#]', json('{"question":"货运车的车钩已经锁好，但制动软管还没连接。如果此时发车，最大的风险是什么？","options":["货运车可能无法跟随列车一起制动","货运车外面的雨水不会变干","旅客找不到自己的座位"],"answer":0,"image":"story/peng-zeyu-train-adventure-quiz"}'),
    '$.items[#]', json('{"question":"树枝已经清除，但主线检测还没有确认安全，而且列车已经晚点。此时最合适的做法是什么？","options":["立即通过，尽量追回时间","继续等待检测确认，并向旅客说明情况","让旅客下车检查轨道"],"answer":1,"image":"story/peng-zeyu-train-adventure-quiz"}')
)
WHERE id = 526 AND type = 'QUIZ';

UPDATE lesson
SET content = json_insert(
    content,
    '$.items[#]', json('{"question":"车票写着 Platform 3、Train 8、Seat 12A。彭泽宇在 2 号站台看见 Train 8，为什么还不能上车？","options":["座位号不是整数","站台信息不一致，需要先重新核对","高速列车只在雨天发车"],"answer":1,"image":"story/time-train-high-speed-quiz"}'),
    '$.items[#]', json('{"question":"一列车只有流线型车头，但轨道、供电或信号系统存在故障。它能安全高速运行吗？","options":["能，车头圆滑就足够了","不能，高速运行需要多个系统共同可靠工作","能，只要把车轮换大一些"],"answer":1,"image":"story/time-train-high-speed-quiz"}')
)
WHERE id = 536 AND type = 'QUIZ';

UPDATE lesson
SET content = json_insert(
    content,
    '$.items[#]', json('{"question":"司机发现压力表超过安全范围，同时安全阀没有放出蒸汽。这说明最危险的问题是什么？","options":["锅炉高压无法及时释放","煤水车装了太多水","导轮进入弯道太平稳"],"answer":0,"image":"train-parts/steam-boiler-plate"}'),
    '$.items[#]', json('{"question":"活塞和主连杆都在工作，但主动轮在湿轨上空转。哪项处理最能帮助机车恢复抓地力？","options":["继续加大蒸汽猛冲","用砂箱撒沙并平稳控制动力","打开汽笛让车轮慢下来"],"answer":1,"image":"train-parts/steam-running-plate"}')
)
WHERE id = 591 AND type = 'QUIZ';

UPDATE lesson
SET content = json_insert(
    content,
    '$.items[#]', json('{"question":"柴油机运转正常，但主发电机没有输出电能，最可能直接导致什么结果？","options":["牵引电动机得不到电，车轮没有动力","喷油器会把柴油变成煤","空气滤清器会自动打开车门"],"answer":0,"image":"train-parts/diesel-drive-plate"}'),
    '$.items[#]', json('{"question":"空气滤清器堵塞，同时冷却风扇停转，发动机可能同时出现哪两类问题？","options":["进气不足和散热不良","车门漏风和座椅摇晃","轮对变小和车钩脱落"],"answer":0,"image":"train-parts/diesel-engine-plate"}')
)
WHERE id = 594 AND type = 'QUIZ';

UPDATE lesson
SET content = json_insert(
    content,
    '$.items[#]', json('{"question":"接触网有电、受电弓也已升起，但主断路器因异常跳开。此时牵引电动机为什么难以继续工作？","options":["高压电的通路被切断了","制动盘把电全部储存起来了","二级悬挂让车体太平稳了"],"answer":0,"image":"train-parts/electric-roof-plate"}'),
    '$.items[#]', json('{"question":"雨天钢轨湿滑，驾驶室又明显摇晃。分别应依靠哪组装置改善这两个问题？","options":["撒砂装置；一级和二级悬挂","主变压器；车钩","受电弓；汽笛"],"answer":0,"image":"train-parts/electric-running-plate"}')
)
WHERE id = 597 AND type = 'QUIZ';

UPDATE lesson
SET content = json_insert(
    content,
    '$.items[#]', json('{"question":"速度传感器发生故障后，列车控制电脑首先缺少哪类关键信息？","options":["车轮和列车的实时速度","餐车剩余的食物数量","车窗外的云朵颜色"],"answer":0,"image":"train-parts/high-speed-systems-plate"}'),
    '$.items[#]', json('{"question":"为了同时减小高速气流影响并保持客室安静，哪组设计配合最合理？","options":["流线型车头、平齐密封的塞拉门和多层车窗","更大的行李架、更多座椅和更亮头灯","雨刷、车钩和空气压缩机"],"answer":0,"image":"train-parts/high-speed-body-plate"}')
)
WHERE id = 600 AND type = 'QUIZ';
