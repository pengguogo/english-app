-- ============================================================
-- V53: 为《彭泽宇的彩虹山谷列车》增加阅读理解问答
-- 共 5 道选择题，覆盖安全检查、异常处置、线路规则与故事主旨。
-- ============================================================

INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(526, 115, '彩虹山谷列车问答', 'QUIZ', '{
  "type":"QUIZ",
  "items":[
    {
      "question":"车门提示灯没有熄灭时，彭泽宇为什么没有立即发车？",
      "options":["提示灯说明可能有车门未安全关闭","他想等雨停后再出发","货运车还没有装满"],
      "answer":0,
      "image":"story/peng-zeyu-train-adventure-quiz"
    },
    {
      "question":"加挂行李货运车后，工作人员做了哪些安全检查？",
      "options":["只数了货箱的数量","检查车钩、制动软管和门锁，并进行制动试验","只擦干了货运车外面的雨水"],
      "answer":1,
      "image":"story/peng-zeyu-train-adventure-quiz"
    },
    {
      "question":"道岔附近的树枝清理后，列车为什么仍要等待？",
      "options":["旅客还想继续看风景","主线还需要进一步检测，必须等安全确认","司机忘记了前往彩虹谷的方向"],
      "answer":1,
      "image":"story/peng-zeyu-train-adventure-quiz"
    },
    {
      "question":"湿轨上车轮轻微打滑时，彭泽宇怎样让列车稳稳爬坡？",
      "options":["猛增牵引力快速冲坡","让旅客下车推列车","调小牵引力、启动撒砂并保持均匀低速"],
      "answer":2,
      "image":"story/peng-zeyu-train-adventure-quiz"
    },
    {
      "question":"这次旅程最重要的经验是什么？",
      "options":["只要开得快就能按时到达","遇到问题要先观察、报告、按规则处理并复查","所有问题都应该由司机一个人解决"],
      "answer":1,
      "image":"story/peng-zeyu-train-adventure-quiz"
    }
  ]
}', 2, 3);
