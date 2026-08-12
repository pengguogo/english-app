-- V59: 新增三篇适合 4～6 岁儿童的故事与问答课程
-- 页数和题量依据情节调整，推理仅涉及明显线索、简单排除与顺序判断。

INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(121, 5, '彭泽宇的海底观测站', 20, 0),
(122, 5, '小小面包店的大订单', 21, 0),
(123, 5, '博物馆的午夜线索', 22, 0);

INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(537, 121, '海底观测站', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"蓝色任务卡","content":"彭泽宇来到海边的蓝鲸观测站。林博士交给他一张任务卡：乘小潜水器观察珊瑚，并找到需要帮助的小海龟。出发前，他们检查氧气、灯光和通信器。林博士说，在海里不能独自打开舱门，有发现要先报告。","image":"story/ocean-station-01"},
    {"title":"三种海底记号","content":"潜水器慢慢下沉，窗外出现三种记号：黄色浮标指向观测站，蓝色箭头指向珊瑚园，红色圆牌表示前方不能靠近。彭泽宇把颜色和方向念了一遍。他们沿着蓝色箭头前进，没有越过红色圆牌。","image":"story/ocean-station-02"},
    {"title":"少了一只小海龟","content":"珊瑚园里，两只小海龟正在吃海草，海龟妈妈却不停回头。记录板上画着三只小海龟。三只应该都在，可这里只看到两只。彭泽宇数了又数，知道还有一只没有回来。","image":"story/ocean-station-03"},
    {"title":"沙地上的线索","content":"细沙上有一排小小的鳍印，旁边还有被碰弯的海草。大鱼留下的痕迹又宽又深，这排痕迹却又小又浅。彭泽宇指着鳍印说，小海龟可能从这里游过。他们顺着痕迹慢慢寻找。","image":"story/ocean-station-04"},
    {"title":"会闪光的求救信号","content":"岩石后面闪了两下银光。原来小海龟的鳍被一段旧塑料绳缠住，贝壳碰到灯光才会反光。彭泽宇没有伸手乱拉，而是马上报告。林博士操纵机械臂，小心剪开塑料绳。","image":"story/ocean-station-05"},
    {"title":"先观察再帮助","content":"小海龟没有马上游走。林博士先观察它的鳍，又让它在潜水器旁轻轻划水。它能向左转，也能向右转，鳍上没有伤口。确认安全后，他们才带着它返回珊瑚园。","image":"story/ocean-station-06"},
    {"title":"三只都回来了","content":"海龟妈妈迎上来，三只小海龟排在她身边。彭泽宇再次清点：一、二、三，一只也不少。他还学会了 turtle 是海龟，sea 是大海。观测站把废绳装进回收箱，不让它继续伤害动物。","image":"story/ocean-station-07"},
    {"title":"海面上的约定","content":"潜水器回到金色海面。彭泽宇在任务卡上画下三颗星：认真观察、及时报告、保护海洋。他明白，找到线索很重要，按安全的方法帮助别人同样重要。大家约定以后不把塑料垃圾留在海边。","image":"story/ocean-station-08"}
  ]
}', 1, 3),
(538, 121, '海底线索问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"珊瑚园原来应该有几只小海龟？","options":["一只","两只","三只"],"answer":2,"image":"story/ocean-station-quiz"},
    {"question":"哪种痕迹更可能是小海龟留下的？","options":["又小又浅的鳍印","又宽又深的大鱼痕迹","天空里的云"],"answer":0,"image":"story/ocean-station-quiz"},
    {"question":"红色圆牌出现时应该怎么做？","options":["继续快速前进","不靠近危险区域","打开舱门看看"],"answer":1,"image":"story/ocean-station-quiz"},
    {"question":"林博士为什么先观察小海龟，再让它回家？","options":["确认它没有受伤","教它数数","等海水变甜"],"answer":0,"image":"story/ocean-station-quiz"},
    {"question":"turtle 是什么意思？","options":["珊瑚","海龟","潜水器"],"answer":1,"image":"story/ocean-station-quiz"},
    {"question":"发现海洋动物被绳子缠住，最安全的做法是什么？","options":["先告诉大人并用合适工具帮助","独自用力拉扯","假装没有看见"],"answer":0,"image":"story/ocean-station-quiz"}
  ]
}', 2, 3),
(539, 122, '面包店的大订单', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"一张大订单","content":"早晨，彭泽宇到暖麦面包店帮忙。幼儿园订了三篮点心：兔子班要 4 个圆面包，熊猫班要 3 个三角面包，小鹿班要 5 个星星饼干。店长把数字和形状画在订单卡上，大家先读清楚再动手。","image":"story/bakery-order-01"},
    {"title":"先做面包还是先烤","content":"彭泽宇差点把面团直接放进烤箱。店长指着步骤图：先揉面，再做形状，最后烘烤。彭泽宇按从一到三的顺序摆好图片。顺序正确，面包才会又松又香。","image":"story/bakery-order-02"},
    {"title":"圆圆的少一个","content":"第一只篮子里只有 3 个圆面包，可订单上写着 4。彭泽宇把面包和卡片一一对应，很快发现少了一个。他补做一个圆面包，现在 3 加 1 等于 4，兔子班的篮子完成了。","image":"story/bakery-order-03"},
    {"title":"贴错位置的小标签","content":"桌上有三张标签：circle、triangle 和 star。星星标签不小心贴到了三角面包篮上。彭泽宇比较形状：三角面包有三个尖角，应该配 triangle。他换回正确标签，三个篮子不再会送错。","image":"story/bakery-order-04"},
    {"title":"最后五分钟","content":"送货车五分钟后到达。店长装篮，彭泽宇核对，另一位店员系好丝带。大家各做一件事，没有争抢。最后，他们数出 4 个圆面包、3 个三角面包和 5 个星星饼干，数量全对。","image":"story/bakery-order-05"},
    {"title":"香香的谢谢","content":"点心准时送到幼儿园。孩子们看着形状找到自己的篮子，还送来一张大大的感谢卡。彭泽宇说，完成大订单的秘诀不是一个人做得飞快，而是看清要求、按照顺序、认真检查并一起合作。","image":"story/bakery-order-06"}
  ]
}', 1, 3),
(540, 122, '面包店订单问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"3 个圆面包再补几个，就有 4 个？","options":["1 个","2 个","3 个"],"answer":0,"image":"story/bakery-order-quiz"},
    {"question":"做面包的正确顺序是什么？","options":["烘烤、揉面、做形状","揉面、做形状、烘烤","做形状、送货、揉面"],"answer":1,"image":"story/bakery-order-quiz"},
    {"question":"有三个尖角的面包应该贴哪张标签？","options":["circle","star","triangle"],"answer":2,"image":"story/bakery-order-quiz"},
    {"question":"哪个班要 5 个星星饼干？","options":["小鹿班","兔子班","熊猫班"],"answer":0,"image":"story/bakery-order-quiz"},
    {"question":"大家为什么能按时完成订单？","options":["分工合作并认真核对","把数量都改小","没有看订单卡"],"answer":0,"image":"story/bakery-order-quiz"}
  ]
}', 2, 3),
(541, 123, '博物馆的午夜线索', 'READING', '{
  "type":"READING",
  "items":[
    {"title":"闭馆前的发现","content":"彭泽宇跟着妈妈参观自然博物馆。闭馆前，管理员发现展台上的恐龙蛋模型不见了。展柜没有损坏，门也一直锁着。大家并不害怕，因为模型一定还在馆里。他们决定看看清楚，再寻找答案。","image":"story/museum-clue-01"},
    {"title":"三种脚印","content":"展台旁有三种印子：管理员的长鞋印、彭泽宇的小鞋印，还有一串圆圆的轮子印。管理员说，今天只有清洁机器人装着小轮子。彭泽宇猜，圆印也许是一条重要线索。","image":"story/museum-clue-02"},
    {"title":"蓝色的纸屑","content":"轮子印旁粘着一小片蓝纸。恐龙展厅用绿色卡片，海洋展厅用蓝色卡片，太空展厅用紫色卡片。彭泽宇想了想：蓝纸更可能来自海洋展厅。他们沿着圆印向海洋展厅走去。","image":"story/museum-clue-03"},
    {"title":"岔路口的选择","content":"走廊分成左右两边。左边地面干净，没有圆印；右边还有两个淡淡的圆印，墙上画着大鲸鱼。彭泽宇选择右边，因为轮子线索还在，而且鲸鱼正是海洋展厅的标志。","image":"story/museum-clue-04"},
    {"title":"机器人肚子里的咚咚声","content":"海洋展厅门口，清洁机器人停在充电座上。它移动一下，肚子里就传出轻轻的咚声。管理员关闭电源，打开收集箱，里面除了纸屑，还有一个灰白色的恐龙蛋模型。谜底终于出现了。","image":"story/museum-clue-05"},
    {"title":"原来不是小偷","content":"监控画面显示，一张宣传纸滑到模型旁。机器人推着纸前进时，柔软的边刷把很轻的模型也扫进了收集箱。没有人偷东西，机器人也不是故意的。大家给展台加上透明挡边，避免模型再次掉落。","image":"story/museum-clue-06"},
    {"title":"小小线索侦探","content":"恐龙蛋模型回到原位。管理员送给彭泽宇一枚纸做的线索侦探徽章。他回想寻找过程：先看圆轮印，再看蓝纸，最后听见咚声。每条线索都很小，合在一起却能指向答案。","image":"story/museum-clue-07"}
  ]
}', 1, 3),
(542, 123, '博物馆线索问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {"question":"哪种印子最可能是清洁机器人留下的？","options":["长鞋印","小鞋印","圆圆的轮子印"],"answer":2,"image":"story/museum-clue-quiz"},
    {"question":"看到蓝色纸屑后，应该先去哪里寻找？","options":["海洋展厅","太空展厅","博物馆外面"],"answer":0,"image":"story/museum-clue-quiz"},
    {"question":"岔路口右边有什么两条线索？","options":["圆轮印和鲸鱼标志","长鞋印和月亮标志","蛋壳和绿色树叶"],"answer":0,"image":"story/museum-clue-quiz"},
    {"question":"恐龙蛋模型最后在哪里？","options":["管理员的口袋里","机器人的收集箱里","鲸鱼模型上面"],"answer":1,"image":"story/museum-clue-quiz"},
    {"question":"谁拿走了恐龙蛋模型？","options":["没有小偷，是机器人误扫进去的","彭泽宇偷偷拿走了","恐龙妈妈来取走了"],"answer":0,"image":"story/museum-clue-quiz"},
    {"question":"彭泽宇是怎样找到答案的？","options":["观察并把几条小线索连起来","随便选择一个房间","大声责怪机器人"],"answer":0,"image":"story/museum-clue-quiz"}
  ]
}', 2, 3);
