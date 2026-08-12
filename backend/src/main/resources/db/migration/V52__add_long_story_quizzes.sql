-- ============================================================
-- V52: 为三篇长故事增加阅读理解问答
-- 每篇故事配套 5 道选择题，覆盖事实、因果、方法与主旨理解。
-- ============================================================

INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(523, 100, '恐龙岛故事问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {
      "question":"瞭望塔最先收到求救信号时，恐龙岛遇到了什么麻烦？",
      "options":["小冠走失，迁徙桥监测器也停止工作","所有恐龙都跑到了海里","救援队的车辆无法启动"],
      "answer":0,
      "image":"story/paw-patrol-dinosaur-island-quiz"
    },
    {
      "question":"阿奇看到溪边的脚印突然变乱，判断小冠当时怎么了？",
      "options":["正在和伙伴做游戏","受到了惊吓","停下来安心睡觉"],
      "answer":1,
      "image":"story/paw-patrol-dinosaur-island-quiz"
    },
    {
      "question":"大家为什么先保护恐龙蛋，再继续寻找小冠？",
      "options":["恐龙蛋能照亮山路","他们想把恐龙蛋带回家","眼前需要帮助的生命也应该先得到照顾"],
      "answer":2,
      "image":"story/paw-patrol-dinosaur-island-quiz"
    },
    {
      "question":"迁徙桥能够及时修好，最重要的原因是什么？",
      "options":["每个人分工合作，把任务衔接起来","小冠独自修好了整座桥","恐龙群一起把桥推回原位"],
      "answer":0,
      "image":"story/paw-patrol-dinosaur-island-quiz"
    },
    {
      "question":"故事最后告诉我们，真正的勇敢是什么？",
      "options":["遇到事情总是冲在最前面","了解不同的生命，并用合适的方法帮助它们","只完成自己最擅长的任务"],
      "answer":1,
      "image":"story/paw-patrol-dinosaur-island-quiz"
    }
  ]
}', 2, 3),
(524, 101, '苹果铁路旅行问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {
      "question":"苹果为什么要由卡车和火车接力运输？",
      "options":["两种车辆装的苹果颜色不同","铁路适合长距离，卡车适合连接果园、车站和门店","火车只能在夜里运输"],
      "answer":1,
      "image":"story/railway-logistics-quiz"
    },
    {
      "question":"苹果装进冷藏集装箱前，工作人员先做了什么？",
      "options":["挑选装箱、预冷并检查制冷设备","把苹果全部切开检查","让苹果在阳光下升温"],
      "answer":0,
      "image":"story/railway-logistics-quiz"
    },
    {
      "question":"集装箱换装时，苹果为什么不用重新拆箱？",
      "options":["吊车可以把整个集装箱平稳搬到铁路平车上","苹果会自己走进火车","铁路平车直接开进了果园"],
      "answer":0,
      "image":"story/railway-logistics-quiz"
    },
    {
      "question":"列车发车前，谁负责安排路径并确认前方区间安全？",
      "options":["门店收货员","果园工人","铁路调度员"],
      "answer":2,
      "image":"story/railway-logistics-quiz"
    },
    {
      "question":"为什么说门店签收还不是运输链路的终点？",
      "options":["苹果还要送回果园","空冷藏箱还要清洁检查并继续周转","司机必须把火车停在门店门口"],
      "answer":1,
      "image":"story/railway-logistics-quiz"
    }
  ]
}', 2, 3),
(525, 102, '月球星种故事问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {
      "question":"彭泽宇最早发现的蓝色求救信号有什么规律？",
      "options":["闪一下就一直不亮","闪三下，停一会儿，再闪两下","每隔一天只闪一次"],
      "answer":1,
      "image":"story/peng-zeyu-space-adventure-quiz"
    },
    {
      "question":"太空碎片让导航屏幕变暗后，彭泽宇怎样帮助飞船恢复导航？",
      "options":["打开舱门观察方向","凭训练记忆找到蓝色圆环通信按钮","让机器人追赶太空碎片"],
      "answer":1,
      "image":"story/peng-zeyu-space-adventure-quiz"
    },
    {
      "question":"车轮印消失后，彭泽宇靠什么继续寻找月芽号？",
      "options":["石头上方向一致的刮痕","地球发来的新地图","温室里绿芽的影子"],
      "answer":0,
      "image":"story/peng-zeyu-space-adventure-quiz"
    },
    {
      "question":"大家怎样在永夜谷里为月芽号补充电量？",
      "options":["点燃篝火给电池加热","把月芽号埋进温暖月尘","用反光板把山坡上的阳光送进谷中"],
      "answer":2,
      "image":"story/peng-zeyu-space-adventure-quiz"
    },
    {
      "question":"彭泽宇回到地球后，说自己害怕时会怎么做？",
      "options":["先记住规则，再和伙伴一起想办法","马上放弃任务回家","一个人悄悄解决所有问题"],
      "answer":0,
      "image":"story/peng-zeyu-space-adventure-quiz"
    }
  ]
}', 2, 3);
