# 趣味性与易用性提升设计文档

> 日期: 2026-07-21
> 范围: 面向不识字用户（4-6 岁学前零基础，家长全程陪同）的导航重构 + 按钮图标化 + 吉祥物情感反馈
> 决策来源: brainstorming 对话（9 轮澄清）

## 一、设计目标

针对"使用者不认识字"的核心约束，从三个层面提升 APP 的趣味性与易用性：

1. **导航层**：首页重构，引入"继续学习"一键直达，学科入口图标化
2. **交互层**：所有操作按钮图标化（图标主 + 文字辅）
3. **反馈层**：吉祥物 Mimi 情感化反馈（CSS 动画 + AI 新插画）

### 目标用户画像

- 4-6 岁学前零基础儿童，认得少量单字/字母，读不懂完整句子
- 需要图标 + 语音辅助
- 家长全程陪同，家长负责讲解操作、读题、引导答题
- APP 只负责内容呈现，可保留较多文字（家长读给孩子）

## 二、导航重构

### 2.1 导航流程变化

**日常学习路径（1 次点击）**：

```
首页 [继续学习大卡片] → 课时页
```

家长陪同场景下，90% 的使用走这条路径，从"选 4 次"降到"点 1 次"。

**浏览探索路径（保留 4 层，但每层图标化）**：

```
首页 [学科横滑] → 学科页 [主题卡片] → 主题页 [单元卡片] → 单元页 [课时卡片] → 课时页
```

浏览路径仍 4 层，但每层卡片加图标 + 颜色区分，孩子能看图识别，家长减少读字负担。

### 2.2 首页 HomeView 重构

- **保留**：欢迎 header（Mimi welcome 图 + 飘浮装饰）
- **新增**：继续学习大卡片（黄边突出，显示上次课时名 + 一键按钮）
- **重构**：学科入口从网格 → 横滑圆形大图标（80px，图标主 + 小字辅助）
- **保留**：快捷功能区（错题集 / 我学过的）
- **不变**：`HomeView.vue` 文件位置、路由 `/`

### 2.3 首页布局（自上而下）

1. **欢迎 header**（渐变背景 + 吉祥物 welcome 图 + 飘浮云朵星星）
2. **继续学习大卡片**（白底 + 黄色边框 + 上次课时信息 + ▶ 继续学习按钮）
3. **学科横滑区**（4 个 80px 圆形大图标：ABC 英语 / 语 语文 / 123 数学 / 🚂 课外）
4. **快捷功能区**（2 列网格：📝 错题集 / 📚 我学过的）

首次使用（无进度）时，继续学习卡片显示"选择学科开始学习"引导，点击跳转学科横滑区。

## 三、后端 API 变化

### 3.1 新增端点

**`GET /api/progress/continue`**

返回当前用户（user_id=1）下一个待学课时：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "lessonId": 22,
    "lessonName": "第3课：交通工具",
    "lessonType": "WORD",
    "unitId": 5,
    "unitName": "陆地交通",
    "themeId": 2,
    "themeName": "交通主题",
    "subjectId": 1,
    "subjectName": "英语",
    "subjectCode": "ENGLISH",
    "subjectColor": "#6B7CFF"
  }
}
```

无进度时返回 `data: null`，前端显示"选择学科开始学习"引导。

### 3.2 实现位置

- `ProgressController.java` 新增 `getContinueLesson()` 端点
- `ProgressService` 接口新增 `getContinueLesson()` 方法
- `ProgressServiceImpl` 实现：
  1. 查询 `user_progress` 最新记录
  2. 找到对应 unit 的下一个未完成 lesson
  3. 若无记录返回 null
  4. MVP 阶段只查同 unit 内下一课；跨 unit 跳转作为后续迭代

### 3.3 数据层

- 复用 `user_progress` 表，**无需 Flyway 迁移**
- 固定 `user_id=1`
- 遵循 `{code, message, data}` 响应格式

## 四、按钮图标化组件设计

### 4.1 设计原则

图标主 + 文字辅：大图标在上（28-32px），小号文字在下（12px）。孩子看图标操作，家长可顺带教认字。

### 4.2 组件改造清单

| 组件 | 当前 | 改造后 | 图标 |
|------|------|--------|------|
| `AppButton.vue` | 纯文字按钮 | 新增 `icon` + `iconPosition` prop，支持"图标上文字下"布局 | 由调用方传入 |
| `AudioButton.vue` | 🔊 + "听发音"横排 | 大🔊图标在上，"听发音"小字在下，胶囊形保持 | 🔊扬声器 |
| `RecordButton.vue` | 🎤 + "跟读录音"横排 | 大🎤图标在上，"跟读录音"小字在下，录音中红点呼吸 | 🎤麦克风 |
| `BackBar.vue` | ← + "返回"文字 | ← 箭头图标为主，"返回"小字辅助，或纯图标（可选 prop） | ← 箭头 |
| `StarBar.vue` | 星星横排 | 不变（已图标化） | ⭐ |

### 4.3 课时页按钮改造

**WordLesson / SentenceLesson 操作区**：

- "听发音"按钮 → 大🔊 + 小字"听发音"
- "跟读录音"按钮 → 大🎤 + 小字"跟读"
- "上一项"按钮 → ← 大箭头 + 小字"上一个"
- "下一步"按钮 → → 大箭头 + 小字"下一个"（主色填充）

**QuizLesson 选项区**：

- A/B/C/D 选项标签 → 保留字母圆形标签（已有），选项内容若为图片则用大图替代文字
- "下一题"按钮同上

**LessonComplete 结算页**：

- "完成"按钮 → ✓ 大对勾 + 小字"完成"

### 4.4 约束

- 所有图标使用 SVG 内联或 emoji（不引入图标库）
- 图标颜色通过 `var(--xxx)` 引用令牌，禁止硬编码
- 按钮尺寸保持现有 `size` prop（md/lg），不新增尺寸

## 五、吉祥物情感反馈系统

### 5.1 分阶段交付

**阶段 A：CSS 动画（快速上线，1-2 天）**

复用现有 3 张图，加 CSS 动画类：

| 场景 | 触发 | 动画 | 用图 |
|------|------|------|------|
| 答对 | QUIZ `answered` correct=true | 跳跃 + 淡入，星星粒子环绕 | `mascot-celebrate.jpg` |
| 答错 | QUIZ `answered` correct=false | 左右摇头 + 淡入 | `mascot-companion.jpg` |
| 录音高分 | 评分 ≥80 | 小幅跳跃 | `mascot-celebrate.jpg` |
| 录音低分 | 评分 <60 | 轻微下垂 | `mascot-companion.jpg` |
| 课时完成 | `isComplete=true` | 庆祝跳跃（已有场景） | `mascot-celebrate.jpg` |

新增组件 `MascotFeedback.vue`（接收 `mood` prop：happy/encourage/sad/celebrate），内部根据 mood 切换图片 + 动画类。

**阶段 B：AI 新插画（3-5 天，可与阶段 A 并行启动）**

生成 2 张新姿态插画，替换阶段 A 的"凑合"方案：

| 新插画 | 姿态 | 文件名 | 用途 |
|--------|------|--------|------|
| 答对欢呼 | 双手举起 + 笑脸 + 星星环绕 | `mascot-cheer.png` | 答对/高分 |
| 答错鼓励 | 单手轻拍 + 温柔微笑 + 气球 | `mascot-encourage.png` | 答错/低分 |

AI 生成提示词遵循项目规范：灰色小老鼠 Mimi + 黄围巾 + 扁平 2.5D 手绘风格 + 透明背景，与现有 3 张保持一致。

### 5.2 反馈文案库

吉祥物旁边显示气泡文案（小字，家长可读给孩子）：

- 答对：`["太棒了！", "答对啦！", "你真聪明！", "Mimi 为你骄傲！"]`（随机）
- 答错：`["没关系，再试一次！", "差一点点哦！", "Mimi 相信你可以的！", "慢慢来，不着急。"]`（随机）

文案存前端常量，不进后端。

### 5.3 动画规范

- 所有动画包裹 `@media (prefers-reduced-motion: no-preference)`
- 跳跃动画：`translateY` 0→-12px→0，`cubic-bezier(0.34, 1.56, 0.64, 1)`，400ms
- 摇头动画：`rotate` 0→-8deg→8deg→-8deg→0，ease-in-out，500ms
- 星星粒子：3-5 个 ⭐ 绝对定位，`scale + fadeOut`，600ms，错峰延迟

### 5.4 吉祥物反馈数据流

```
QuizLesson @answered({correct})
  └─ LessonView handleAnswered()
       ├─ 记录分数/错题（已有逻辑）
       └─ 设置 mascotMood = correct ? 'happy' : 'encourage'
            └─ MascotFeedback 组件根据 mood 显示对应图 + 动画 + 文案
            └─ 3秒后 mascotMood 重置为 null（隐藏反馈层）
```

WordLesson 录音评分同理，根据分数区间设置 mood。

## 六、前端数据流

### 6.1 首页数据流

```
HomeView onMounted
  ├─ getContinueLesson() → 继续学习卡片数据
  └─ getSubjects() → 学科横滑数据

点击"继续学习"
  └─ router.push(`/lesson/${lessonId}`)

点击学科图标
  └─ router.push(`/subject/${subjectId}`)
```

### 6.2 前端 API 封装

`frontend/src/api/progress.js` 新增：

```js
export async function getContinueLesson() {
  const res = await http.get('/api/progress/continue')
  return res.data.data
}
```

## 七、实施顺序与分阶段交付

### 阶段 1：后端 API（1 天）

1. `ProgressService.getContinueLesson()` 接口 + 实现
2. `ProgressController` 新增端点
3. 单元测试 `ProgressServiceImplTest.should_returnContinueLesson_when_progressExists`
4. 集成测试验证无进度场景

### 阶段 2：首页重构（1 天）

1. `HomeView.vue` 新增继续学习卡片区块
2. 学科入口从网格改为横滑圆形大图标
3. `api/progress.js` 新增 `getContinueLesson()`
4. 手动测试首次使用 + 有进度两种场景

### 阶段 3：按钮图标化（1-2 天）

1. `AppButton.vue` 新增 `icon` + `iconPosition` prop
2. `AudioButton.vue` / `RecordButton.vue` / `BackBar.vue` 改造布局
3. `WordLesson` / `SentenceLesson` / `QuizLesson` / `LessonComplete` 调用方传入图标
4. 全局回归测试所有课时类型

### 阶段 4：吉祥物反馈 · CSS 动画（1-2 天）

1. 新建 `MascotFeedback.vue` 组件
2. 定义 4 种 mood（happy/encourage/sad/celebrate）的 CSS 动画类
3. `LessonView.vue` 引入 `mascotMood` 状态，在 `handleAnswered` / `handleRecorded` 中设置
4. 课时模板底部渲染 `<MascotFeedback :mood="mascotMood" />`
5. 文案库常量 + 随机选择逻辑

### 阶段 5：吉祥物反馈 · AI 新插画（3-5 天，可与阶段 4 并行启动）

1. 编写 AI 生成提示词（强调风格一致性）
2. 生成 `mascot-cheer.png` + `mascot-encourage.png`
3. 替换 `MascotFeedback.vue` 中的图片引用
4. 视觉验收（与现有 3 张图风格对比）

**总工期**：约 7-10 天（阶段 4-5 并行）。阶段 1-3 是"能用"，阶段 4-5 是"好玩"。

## 八、测试策略

### 8.1 后端测试

- `ProgressServiceImplTest` 新增：
  - `should_returnContinueLesson_when_progressExists`
  - `should_returnNull_when_noProgress`
  - `should_returnNextUncompletedLesson_when_unitHasMultipleLessons`
- 跨 unit 跳转逻辑留待后续迭代，本次不测试
- 覆盖率维持 80%+

### 8.2 前端测试

手动测试矩阵：

- **首页**：首次使用（无继续学习卡片） / 有进度（显示卡片） / 4 学科横滑
- **课时页**：5 种课型（WORD/SENTENCE/READING/QUIZ/CALCULATE）按钮图标化回归
- **吉祥物反馈**：答对/答错/高分/低分/课时完成 5 种 mood
- **响应式**：<480px / 480-800px / >800px 三档验证

### 8.3 验收标准

- 首页"继续学习"一键直达课时，2 次点击内开始学习
- 所有操作按钮有图标，孩子不看文字也能猜测功能
- 答对/答错时吉祥物有明显情感反馈（动画 + 文案）
- 现有功能无回归（错题集、我学过的、录音评分等正常）

## 九、风险与应对

| 风险 | 影响 | 应对 |
|------|------|------|
| AI 新插画风格与现有 Mimi 不一致 | 视觉割裂 | 阶段 A 先用 CSS 动画兜底；阶段 B 多轮生成对比，验收不通过则延后 |
| "继续学习"逻辑复杂（跨 unit/theme 跳转） | 后端实现超期 | MVP 只查同 unit 内下一课；跨 unit 跳转作为后续迭代 |
| 吉祥物反馈动画干扰学习节奏 | 孩子分心 | 动画 ≤600ms，3 秒后自动隐藏反馈层；可配置关闭 |
| 学科横滑在小屏被误触 | 误跳转 | 横滑区域加 `scroll-snap`，点击热区限制在图标圆内 |

## 十、不在本次范围内（YAGNI）

以下功能虽在讨论中提及，但本次不实现，留待后续迭代：

- **音效反馈**（答对叮咚/答错提示音）—— 需引入音频资产，本次聚焦视觉反馈
- **奖励收集系统**（星星/徽章累积解锁装扮）—— 实现重，需新建表，后续迭代
- **故事化包装**（主题串联故事线）—— 需内容改写，本次聚焦交互层
- **语音引导自动播放**（进入页面自动 TTS）—— 家长陪同场景下非必需
- **陪同模式 / 独立模式切换**—— 当前固定家长陪同场景，无需切换
- **跨 unit/theme 的"继续学习"跳转**—— MVP 只查同 unit 内下一课
