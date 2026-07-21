# 英语课时扩展设计文档

**日期**: 2026-07-21
**状态**: 设计完成，待审阅
**作者**: TRAE Agent

## 背景与目标

当前英语学习 APP 共 108 课时，其中英语 36 课，每单元仅 3-4 课，内容偏少。本设计通过渐进式扩展，将英语课时从 36 增至 60，并引入两种新课时类型（PHONICS、DIALOGUE），提升学习体验的丰富度。

**核心约束**：
- 图片资源先不改，复用现有素材或用 emoji 顶替
- 速度优先，内容用 AI 生成 + 人工审核
- 不破坏现有功能和数据

## 当前英语结构

| 主题 | 单元数 | 课时数 | 现有类型 |
|------|--------|--------|----------|
| 水果乐园 | 3 | 10 | WORD/SENTENCE |
| 交通工具 | 3 | 10 | WORD/SENTENCE |
| 汪汪队立大功 | 3 | 10 | WORD/SENTENCE |
| 小砾工程队 | 2 | 6 | WORD/SENTENCE |
| **合计** | **11** | **36** | — |

## 扩展方案：深化现有主题

在现有 4 个主题内深化，不新增主题，插入 PHONICS/DIALOGUE 试点。

### 扩展后结构（36 → 60 课，+24 课）

| 主题 | 扩展前 | 扩展后 | 新增课时明细 |
|------|--------|--------|--------------|
| 水果乐园 | 10 | 16 | +3 WORD(浆果/热带水果) +1 SENTENCE(喜欢表达) +1 PHONICS(F/B/G字母) +1 DIALOGUE(水果店对话) |
| 交通工具 | 10 | 16 | +3 WORD(特种车辆) +1 SENTENCE(出行方式) +1 PHONICS(C/T/P字母) +1 DIALOGUE(车站问路) |
| 汪汪队 | 10 | 16 | +2 SENTENCE(请求/帮助) +1 QUIZ(角色辨认) +1 PHONICS(M/R/S字母) +1 DIALOGUE(救援对话) +1 READING(团队故事) |
| 小砾工程 | 6 | 12 | +1 WORD(工程家族成员) +1 WORD(家族专属车辆) +1 QUIZ(成员与车辆配对) +1 PHONICS(M/C/W字母) +1 DIALOGUE(工地合作对话) +1 READING(建造者湾大工程故事) |
| **合计** | **36** | **60** | **+24 课** |

### 汪汪队立大功官方角色（重点）

改用动画片《汪汪队立大功》(PAW Patrol) 官方角色，替换 V6 中的现有内容。本项目为个人自用，无版权风险。

**主要狗狗角色（6 只原班 + 常驻新成员）**：

| 角色名（英文） | 中文译名 | 品种 | 职责 | Emoji | 图片 |
|---|---|---|---|---|---|
| Chase | 阿奇 | 德国牧羊犬 | 警察犬/特工 | 🐕‍🦺 | ✅ `paw-patrol/chase.jpg` |
| Marshall | 毛毛 | 大麦町犬 | 消防犬/医疗 | 🚒 | ✅ `paw-patrol/marshall.jpg` |
| Skye | 天天 | 可卡颇犬 | 飞行犬 | 🚁 | ✅ `paw-patrol/skye.jpg` |
| Rubble | 小砾 | 英国斗牛犬 | 工程犬 | 🚧 | ✅ `paw-patrol/rubble.jpg` |
| Rocky | 灰灰 | 杂种犬 | 回收/维修 | ♻️ | ✅ `paw-patrol/rocky.jpg` |
| Zuma | 路马 | 拉布拉多犬 | 水上救生 | 🚤 | ✅ `paw-patrol/zuma.jpg` |
| Everest | 珠珠 | 哈士奇 | 雪地救援（S2 起） | ❄️ | ❌ 用 emoji |
| Tracker | 小克 | 柴犬 | 丛林救援（S3 起） | 🌴 | ❌ 用 emoji |
| Rex | 乐乐 | 伯恩山犬 | 恐龙专家（S7 起） | 🦖 | ❌ 用 emoji |
| Liberty | 莉柏蒂 | 腊肠犬 | 城市犬（大电影） | 🏙️ | ❌ 用 emoji |

**人类角色**：

| 角色名 | 中文译名 | 身份 | Emoji |
|---|---|---|---|
| Ryder | 莱德 | 10 岁队长，人类主角 | 👦 |
| Mayor Goodway | 古威市长 | 冒险湾市长 | 👩‍💼 |
| Alex | 亚历克斯 | 镇上的小男孩 | 🧒 |
| Katie | 凯蒂 | 宠物店老板 | 💇‍♀️ |
| Cap'n Turbot | 船长特伯 | 海洋生物学家 | ⚓ |

**反派角色**：

| 角色名 | 中文译名 | 特点 | Emoji |
|---|---|---|---|
| Mayor Humdinger | 韩丁纳市长 | 雾底城捣蛋市长，常搞破坏 | 😈 |
| Sweetie | 甜甜 | 巴金堡公主的贪心小狗，想篡位 | 👑 |
| Sid Swashbuckle | 席德 | 海盗，S5 反派 | 🏴‍☠️ |
| Arrby | 阿比 | 席德的腊肠犬跟班 | 🦮 |

**地点/建筑**：

| 名称 | 中文 | 说明 |
|---|---|---|
| Adventure Bay | 冒险湾 | 主舞台，汪汪队驻地 |
| Foggy Bottom | 雾底城 | 韩丁纳市长的地盘 |
| Barkingburg | 巴金堡 | 甜甜所在的城堡王国 |
| The Lookout | 瞭望塔 | 汪汪队总部 |

**数据迁移策略**：
- V18 中用 `UPDATE lesson SET content = ...` 替换汪汪队主题（Lesson 22-31）的 content，从现有内容改为官方角色
- Lesson 22-25「狗狗小队」单元：改为介绍 6 只原班狗狗（Chase/Marshall/Skye/Rubble/Rocky/Zuma）
- Lesson 26-28「救援行动」单元：改为围绕官方救援场景（火灾/海上/高山）
- Lesson 29-31「团队精神」单元：改为围绕莱德队长、韩丁纳反派、团队合作故事

新增课时内容安排：
- **SENTENCE 课「请求帮助」**：Ryder 接到求助，狗狗们响应的句型（"Ryder needs us!" / "Chase is on the case!"）
- **SENTENCE 课「救援口令」**：狗狗口号式句子（"Ready for action!" / "No job is too big, no pup is too small!"）
- **QUIZ 课「角色辨认」**：根据描述猜狗狗（消防犬→Marshall / 飞行犬→Skye）
- **PHONICS 课**：字母 M(Marshall/Mayor)、R(Rubble/Rocky)、S(Skye/Sweetie)
- **DIALOGUE 课「救援对话」**：Ryder 派任务，狗狗回答，Mimi 作为旁观者惊叹
- **READING 课「团队故事」**：6 只狗狗合作救援冒险湾的故事

### 小砾工程家族官方角色（重点）

改用动画片《小砾与工程家族》(Rubble & Crew) 官方 7 位角色，替换 V8 中的原创角色。本项目为个人自用，无版权风险。

| 角色名（英文） | 中文译名 | 品种/特点 | 工种 | Emoji | 图片 |
|---|---|---|---|---|---|
| Rubble | 小砾 | 英国斗牛犬，队长 | 领导者、工程专家 | 🐶 | ✅ `paw-patrol/rubble.jpg` |
| Grandpa Gravel | 格雷维尔爷爷 | 慈祥老狗 | 移动餐车、智慧顾问 | 👴 | ❌ 用 emoji |
| Auntie Crane | 鹤姑姑 | 阿姨 | 起重机操作、物资管理 | 🏗️ | ❌ 用 emoji |
| Mix | 米克斯 | 表兄弟，创造力 | 混凝土/胶水/油漆材料混合 | 🎨 | ❌ 用 emoji |
| Wheeler | 惠勒 | 表兄弟，有洁癖 | 清洁专家，驾驶清洁车 | 🧹 | ❌ 用 emoji |
| Charger | 充电器 | 表兄弟，精力充沛 | 爆发力、关键时刻能量 | ⚡ | ❌ 用 emoji |
| Motor | 摩托 | 最小表姐妹，小身材大力量 | 拆除专家 | 💥 | ❌ 用 emoji |

**反派角色**：

| 角色名 | 中文译名 | 特点 | Emoji |
|---|---|---|---|
| Speed Meister | 极速迈斯特 | 建造者湾的捣蛋鬼，追求速度不顾质量 | 🏎️ |

**地点/建筑**：

| 名称 | 中文 | 说明 |
|---|---|---|
| Builder Cove | 建造者湾 | 小砾工程家族驻地 |
| Rubble & Crew HQ | 工程家族总部 | 家族基地 |

**数据迁移策略**：
- V18 中用 `UPDATE lesson SET content = ...` 替换 Lesson 35/36/37 的 content，从原创角色改为官方角色
- Lesson 35「认识工程狗狗」→「认识工程家族」：7 位官方角色
- Lesson 36「狗狗的工具」→「家族的车辆」：7 辆专属工程车辆
- Lesson 37「一起建房子」→「一起建大桥」：围绕官方角色的合作句型

新增课时内容安排：
- **WORD 课 1「家族成员登场」**：7 位角色全介绍（Rubble/Grandpa Gravel/Auntie Crane/Mix/Wheeler/Charger/Motor）
- **WORD 课 2「家族的车辆」**：每角色的专属工程车辆（起重机/搅拌车/清洁车/推土机等）
- **QUIZ 课「成员找车辆」**：7 位角色与专属车辆配对
- **PHONICS 课**：字母 M(Mix/Motor)、C(Charger/Crane)、W(Wheeler)，用角色名字学字母
- **DIALOGUE 课「工地合作」**：家族成员协作建桥，Mimi 作为参观者与 Rubble 对话，Speed Meister 来捣乱
- **READING 课「建造者湾大工程」**：7 位家族成员齐心协力建造学校的故事，Speed Meister 试图破坏但失败

### 新增 24 课类型分布

- **深化现有类型**：14 课（WORD 8 + SENTENCE 4 + READING 2）
- **新增类型试点**：8 课（PHONICS 4 + DIALOGUE 4，每主题各 1 课）
- **强化练习**：2 课 QUIZ

### 设计原则

- **渐进式**：PHONICS/DIALOGUE 每主题仅 1 课试点，验证模式后再扩大
- **图片策略（优先级）**：
  1. **优先用动画片原版图片**：从网络获取汪汪队立大功/小砾工程家族的官方角色图、场景图，保存到 `backend/src/main/resources/static/images/paw-patrol/` 和 `backend/src/main/resources/static/images/rubble-crew/`
  2. **次选用现有图片**：已下载的 6 只原班狗狗图（`paw-patrol/chase.jpg` 等）继续使用
  3. **最后用 AI 生成**：网络找不到的图片（如特定场景、反派角色），用 AI 图像生成工具创建，风格统一为扁平卡通
  4. **兜底用 emoji**：实在无图的角色/场景用 emoji 顶替，确保功能可用
- **主题内逻辑**：每个主题的 PHONICS 字母从该主题词汇中提取（如水果主题学 F/B/G），形成知识闭环

## 数据模型

### LessonType 枚举扩展

```java
// backend/.../domain/enums/LessonType.java 新增
PHONICS,   // 字母/字母组合发音规则学习
DIALOGUE   // 情景对话练习
```

### PHONICS content 结构

```json
{
  "type": "PHONICS",
  "letter": "F",
  "pronunciation": "/ef/",
  "sound": "/f/",
  "tip": "F 开头单词常发 /f/ 音，上牙咬下唇",
  "items": [
    {"word": "fish", "phonetic": "/fɪʃ/", "translation": "鱼", "emoji": "🐟", "image": ""},
    {"word": "fruit", "phonetic": "/fruːt/", "translation": "水果", "emoji": "🍎", "image": ""},
    {"word": "flower", "phonetic": "/ˈflaʊər/", "translation": "花", "emoji": "🌸", "image": ""}
  ]
}
```

- `letter` + `sound` + `tip` 是 PHONICS 特有顶层字段
- `items` 复用 WORD 的 items 结构（word/phonetic/translation/emoji/image），可空 image
- 图片策略：复用现有同主题单词图，无图时用 emoji

### DIALOGUE content 结构

```json
{
  "type": "DIALOGUE",
  "scene": "水果店",
  "tip": "学习购买水果的简单对话",
  "items": [
    {"speaker": "Mimi", "text": "Hello! I want an apple.", "translation": "你好！我想要一个苹果。", "audio": ""},
    {"speaker": "Seller", "text": "Here you are. Do you like bananas?", "translation": "给你。你喜欢香蕉吗？", "audio": ""},
    {"speaker": "Mimi", "text": "Yes, I like bananas. Thank you!", "translation": "是的，我喜欢香蕉。谢谢！", "audio": ""}
  ]
}
```

- `scene` + `tip` 是 DIALOGUE 特有顶层字段
- `items` 含 `speaker`（说话人，Mimi/主题角色）+ `text`（英文）+ `translation`（中文）+ `audio`（TTS 音频路径）
  - `audio` 为空字符串时，前端调用后端 `/api/tts?text=<text>` 实时合成
  - `audio` 非空时，为预生成音频的相对路径（未来优化用）
- speaker 用 Mimi 作为固定主角（吉祥物），Seller 等 NPC 用文字标签，不需新配图

### WordImageResolver 兼容性

`WordImageResolver.resolveContent` 只处理 `items[*].image` 字段，对 PHONICS/DIALOGUE 完全兼容：
- PHONICS 的 items 有 image 字段，正常解析
- DIALOGUE 的 items 无 image 字段，解析器遍历时若找不到则跳过，不影响

**无需修改解析器**。

### Flyway 迁移策略

- 新增 `V18__expand_english_lessons.sql`，包含两部分：
  1. **UPDATE** 现有课时 content：
     - Lesson 22-31（汪汪队主题）：改为官方角色（Chase/Marshall/Skye/Rubble/Rocky/Zuma + Ryder + Mayor Humdinger）
     - Lesson 35/36/37（小砾工程主题）：改为官方角色（Rubble/Grandpa Gravel/Auntie Crane/Mix/Wheeler/Charger/Motor + Speed Meister）
  2. **INSERT** 24 节新课：4 主题各新增 6 课
- content 字段用 `json_set` 精确构建（避免 V9 的 emoji REPLACE 教训）
- 除上述 13 节课外，不修改其他现有课时

## 前端分发与新组件

### LessonView.vue 分发扩展

```javascript
// 新增 import
import PhonicsLesson from '../components/lesson-templates/PhonicsLesson.vue'
import DialogueLesson from '../components/lesson-templates/DialogueLesson.vue'

// switch case 新增
case 'PHONICS':   return PhonicsLesson
case 'DIALOGUE':  return DialogueLesson
```

### PhonicsLesson.vue 交互设计

- **顶部**：大号字母 "F" + 发音 /ef/ + 音标 /f/
- **主体**：字母书写动画（SVG path + stroke-dasharray 动画，可点击重播）
- **词汇区**：3 个示例单词卡片，点击听发音（复用 WordLesson 的 TTS 逻辑）
- **练习**：听音选字母（3 选 1，4 题）
- **完成**：解锁星星

### DialogueLesson.vue 交互设计

- **顶部**：场景标题 "水果店" + Mimi 吉祥物头像（复用 `frontend/src/assets/mascot/mascot-companion.jpg`）
- **主体**：聊天气泡式对话展示
  - Mimi 发言：左侧 + 吉祥物头像
  - NPC 发言：右侧 + 文字标签（如 "Seller" 文字徽章，不需配图）
- **交互**：逐句播放（TTS 朗读），气泡依次高亮
- **练习**：角色扮演模式，用户跟读 Mimi 的台词（复用录音评分能力）
- **完成**：解锁星星

### 复用现有能力

- TTS 服务：后端 `/api/tts` 接口，PHONICS 朗读字母和单词，DIALOGUE 朗读台词
- 录音评分：复用 LessonView 已有的录音组件
- StarBar / BackBar / LessonComplete：直接复用

## 内容生成工作流

### AI 生成 → 人工审核流程

```
1. 编写 prompt 模板（按课时类型）
   ├─ WORD/SENTENCE: 给定主题+数量，生成词汇/句型
   ├─ PHONICS: 给定字母，生成示例单词+发音规则
   └─ DIALOGUE: 给定场景+主角，生成对话台词
2. LLM 批量生成（每主题一次调用）
3. 输出 JSON 结构（符合 content schema）
4. 人工审核：
   ├─ 词汇适龄性（3-8 岁）
   ├─ 翻译准确性
   ├─ 发音/音标准确性
   └─ 对话自然度
5. 入库：审核通过的内容写入 V18 迁移文件
```

### Prompt 模板示例（PHONICS）

```
你是一位儿童英语教育专家，为 3-8 岁儿童设计自然拼读课程。
给定字母 {letter}，生成 3 个以该字母开头的常见单词，
要求：
- 单词难度适合启蒙阶段
- 每个单词含音标、中文翻译、emoji
- 提供字母发音规则说明（1 句话）
输出 JSON 格式：
{"letter":"F","pronunciation":"/ef/","sound":"/f/",
 "tip":"...","items":[{"word":"...","phonetic":"...","translation":"...","emoji":"..."}]}
```

### 内容生成脚本

- 位置：临时工作目录（不进仓库）
- 脚本调用 LLM API 生成内容，输出为 SQL 片段
- 人工审核后，将 SQL 片段整合进 `V18__expand_english_lessons.sql`

## 测试与验收

### 后端测试

- `LessonTypeTest`：验证枚举新增 PHONICS/DIALOGUE
- `LessonServiceTest`：验证新课时的 detail API 正确返回 content（含 image URL 解析）
- `WordImageResolverTest`：验证对 PHONICS content 的兼容性（items 有 image 解析，无 image 跳过）
- `LessonControllerTest`：验证列表/详情接口返回新课时不报错

### 前端测试

- 手动验证 4 个主题的 PHONICS 课：字母展示、词汇播放、练习答题
- 手动验证 4 个主题的 DIALOGUE 课：气泡展示、TTS 播放、跟读评分
- 验证现有 WORD/SENTENCE 课不受影响

### 验收标准

- 课时总数从 36 增至 60（英语）
- 4 个主题各含 1 课 PHONICS + 1 课 DIALOGUE
- 新课时前端可正常渲染、播放音频、完成答题
- Flyway V18 迁移成功，现有数据不受影响

## 风险与缓解

| 风险 | 缓解措施 |
|------|----------|
| PHONICS/DIALOGUE 前端组件开发工作量超预期 | 先做 1 个主题试点，验证后再铺开 |
| AI 生成内容质量参差 | 人工审核环节，不合格内容重生成 |
| TTS 对字母发音支持不佳 | 字母发音用预录音频或特殊 TTS 标记 |
| 现有课时受影响 | 除汪汪队(L22-31)和小砾工程(L35-37)共 13 课改用官方角色外，其他课时不动，迁移文件独立 |

## 后续演进

- 若 PHONICS/DIALOGUE 试点效果好，可在语文/数学学科引入类似新课型
- 图片资源优化可独立进行，不影响课时扩展
- 后续可新增主题（动物/家庭等），届时再考虑新配图
