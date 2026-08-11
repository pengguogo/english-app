# 《彭泽宇的彩虹山谷列车》AI 配图记录

用途：个人儿童学习应用中的课外中文阅读故事与分镜素材。

生成日期：2026-08-11。

生成方式：使用 Codex 内置 `imagegen` 工具逐张生成，分类为
`illustration-story`；第 2 幕另使用一次 `precise-object-edit` 修正乘客身份。
项目内已有的彭泽宇太空冒险图片仅作为主角外貌参考，铁路货运图片仅作为场景与
材质风格参考；其余人物、列车与故事场景均按本故事原创生成。

## 统一提示词约束

- 横向宽屏构图，适合阅读卡片展示。
- 高品质半写实 3D 儿童绘本风格，温暖、清晰、具有电影感。
- 主角保持参考图中的短黑发、圆润脸型、明亮眼睛和自然笑容。
- 彭泽宇始终穿深蓝色儿童司机制服、浅蓝衬衫、红色小领巾和深蓝帽。
- 列车始终为蓝白车身、细红腰线的短编组客车，装货后加挂一节橙色行李货运车。
- 危险操作由成年司机、乘务员、站务员或线路工作人员负责；彭泽宇在成年教练
  陪同下学习观察、报告、确认信号和低速驾驶。
- 无可读文字、字幕、水印、品牌标识、事故伤害或恐怖画面。

## 最终提示词组摘要

| 本地文件 | 场景与关键提示 |
|---|---|
| `peng-zeyu-train-adventure-01.jpg` | 清晨站台，彭泽宇与成年教练在驾驶室检查仪表和制动，旅客有序上车 |
| `peng-zeyu-train-adventure-02.jpg` | 女乘客背包带被车门夹住，乘务员处理后，彭泽宇在驾驶室复核车门状态 |
| `peng-zeyu-train-adventure-03.jpg` | 山谷站加挂橙色行李货车，装入图书、种子与急救物资并检查车钩和固定带 |
| `peng-zeyu-train-adventure-04.jpg` | 列车停入安全侧线，工作人员重新拉紧松动的蓝色固定带并逐项复查 |
| `peng-zeyu-train-adventure-05.jpg` | 阵雨中列车停在红灯前，线路人员清理侵入道岔区的树枝并检查尖轨 |
| `peng-zeyu-train-adventure-06.jpg` | 值班员转换并锁闭道岔，绿色信号亮起，单列火车低速驶入左侧备用线 |
| `peng-zeyu-train-adventure-07.jpg` | 湿叶缓坡上减小牵引并适量撒砂，列车稳步驶向山溪小桥和雨后彩虹 |
| `peng-zeyu-train-adventure-08.jpg` | 彩虹谷终点，旅客下车、物资卸载，彭泽宇与成年教练击掌庆祝 |

交付图片位于 `backend/src/main/resources/static/images/story/`，统一转换为 JPG；
图片 key 为 `story/peng-zeyu-train-adventure-01` 至
`story/peng-zeyu-train-adventure-08`。

章节封面 `frontend/src/assets/units/unit-115.webp` 由第 1 幕插图裁切并缩放生成。
