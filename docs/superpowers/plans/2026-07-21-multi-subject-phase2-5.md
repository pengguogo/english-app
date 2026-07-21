# 多学科重构 - 阶段2~5 完整实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将英语单学科APP扩展为多学科学习平台，完成英语迁移（阶段2）、课外火车模块（阶段3）、语文模块（阶段4）、数学模块（阶段5），实现4学科117+课时、5种课型、115+张AI配图的完整学习平台。

**Architecture:** 前端 LessonView 作为按 `lesson.type` 分发的路由器，WORD/SENTENCE/READING/QUIZ/CALCULATE 五种模板组件各司其职。后端 WordImageResolver 已支持多子目录图片URL解析（如 `train/steam-locomotive` → `/images/train/steam-locomotive.jpg`），新增学科只需 Flyway 迁移播种内容 + AI生成配图，零后端代码改动。每个阶段独立可验证、可回滚。

**Tech Stack:** Java 17 + Spring Boot 3.2 + JPA + Flyway + SQLite | Vue 3 + Vite + Pinia + Vue Router | 百度TTS+ASR | GenerateImage AI配图

---

## 全局文件结构总览

### 前端新增文件（阶段2-5合计）

```
frontend/src/components/lesson-templates/
├── LessonComplete.vue      # 阶段2: 通用结算页（所有课型复用）
├── WordLesson.vue           # 阶段2: WORD 模板（单词/汉字/拼音/数字认字）
├── SentenceLesson.vue       # 阶段2: SENTENCE 模板（句型/古诗朗诵）
├── ReadingLesson.vue        # 阶段3: READING 模板（图文翻页阅读）
├── QuizLesson.vue           # 阶段3: QUIZ 模板（选择题判对错）
└── CalculateLesson.vue      # 阶段5: CALCULATE 模板（计算题判对错）
```

### 前端修改文件

```
frontend/src/views/LessonView.vue          # 阶段2: 重构为 type 分发器
frontend/src/components/lesson-templates/   # 阶段2: 新建目录
```

### 后端新增文件（Flyway 迁移）

```
backend/src/main/resources/db/migration/
├── V14__seed_train_content.sql     # 阶段3: 课外火车 4主题 8单元 24课时
├── V15__seed_chinese_content.sql   # 阶段4: 语文 4主题 10单元 30课时
└── V16__seed_math_content.sql      # 阶段5: 数学 4主题 9单元 27课时
```

### 后端静态图片目录

```
backend/src/main/resources/static/images/
├── words/      # 现有 47 张英语单词配图
├── train/      # 阶段3: ~30 张火车配图
├── chinese/    # 阶段4: ~40 张语文配图
├── math/       # 阶段5: ~25 张数学配图
└── story/      # 阶段4: 寓言故事场景插图
```

### 阶段依赖关系

```
阶段2（英语迁移）── 无依赖，可立即开始
    │
    ├── 阶段3（课外火车）── 依赖阶段2的 LessonView 分发器
    │       └── 需要 ReadingLesson + QuizLesson 模板
    │
    ├── 阶段4（语文）── 依赖阶段2的 LessonView 分发器 + 阶段3的 ReadingLesson
    │       └── 复用 Word/Sentence/Reading 模板
    │
    └── 阶段5（数学）── 依赖阶段2的 LessonView 分发器 + 阶段3的 QuizLesson
            └── 新增 CalculateLesson 模板，复用 Quiz 模板
```

---

## 阶段2：英语迁移（LessonView 重构 + 模板提取 + 配图补全）

**目标：** 将 LessonView 从672行单文件重构为按 `lesson.type` 分发的路由器，提取 WordLesson/SentenceLesson/LessonComplete 三个模板组件，补全英语课程缺失的配图。

**详细计划：** 已存在于 `docs/superpowers/plans/2026-07-21-multi-subject-phase2.md`，包含 Task 1-5 完整代码。本阶段在此基础上新增 Task 6（配图补全）。

### Task 2.1~2.4：模板提取与 LessonView 重构

> 完整代码见 `docs/superpowers/plans/2026-07-21-multi-subject-phase2.md`，此处仅列任务清单。

- [ ] **Task 2.1:** 创建 `LessonComplete.vue` 通用结算页组件
- [ ] **Task 2.2:** 创建 `WordLesson.vue` WORD 模板组件
- [ ] **Task 2.3:** 创建 `SentenceLesson.vue` SENTENCE 模板组件
- [ ] **Task 2.4:** 重构 `LessonView.vue` 为按 type 分发的路由器

### Task 2.5：补全英语课程缺失配图

**背景：** 当前只有 Lesson 22/23/35/36 四个课时有 image 字段，其余 12 个 WORD 课时用 emoji 替代。需补全配图提升视觉体验。

**Files:**
- Create: `backend/src/main/resources/db/migration/V14_patch_english_images.sql`（临时命名，实际合并到阶段3的 V14 中或单独 V14a）
- Create: `backend/src/main/resources/static/images/words/*.jpg`（AI生成）

**需要补全配图的 WORD 课时清单：**

| Lesson ID | 课时名称 | 需补全的单词 | 图片 key |
|-----------|---------|-------------|---------|
| 1 | 认识水果 | apple/banana/orange/grape | apple/banana/orange/grape |
| 3 | 水果进阶 | watermelon/strawberry/cherry/peach | watermelon/strawberry/cherry/peach |
| 6 | 认识汽车 | car/bus/bike/train | car/bus/bike/train |
| 7 | 天空飞行 | plane/helicopter/balloon/rocket | plane/helicopter/balloon/rocket |
| 8 | 水上交通 | boat/ship/submarine | boat/ship/submarine |
| 11 | 认识水果2 | (同 Lesson 1 扩展) | (复用) |
| 13 | 认识汽车2 | (同 Lesson 6 扩展) | (复用) |
| 15 | 天空飞行2 | (同 Lesson 7 扩展) | (复用) |
| 17 | 水上交通2 | (同 Lesson 8 扩展) | (复用) |
| 27-30 | 汪汪队单词课 | paw-patrol 角色相关 | (复用已有) |

**注意：** 实际需补全的图片约 12-16 张（部分单词可复用已有图片）。为避免 Flyway 版本号冲突，英语配图补全的 SQL 合并到阶段3的 V14 迁移文件中，作为该文件的第一个 section。

- [ ] **Step 1: AI 生成英语补全配图**

使用 GenerateImage 工具逐张生成，风格统一为"扁平2.5D、圆润、暖色调、儿童友好、浅色纯色背景"。

每张图片的 prompt 模板：
```
[PURPOSE]: 英语儿童学习APP单词配图
[DESCRIPTION]: 扁平2.5D风格的{单词对应物体}，圆润可爱，暖色调，浅黄色纯色背景，无文字，儿童友好插画风格，1024x1024正方形
```

- [ ] **Step 2: 在 V14 迁移文件中追加英语 image 字段补全**

在 `V14__seed_train_content.sql` 的开头追加英语配图补全 section（使用 `json_set` 精确更新）：

```sql
-- ============================================================
-- Section 0: 补全英语 WORD 课程的 image 字段
-- 使用 json_set 精确更新（避免 V9 的 REPLACE emoji 匹配失败问题）
-- ============================================================

-- Lesson 1: 认识水果
UPDATE lesson SET content = json_set(content, '$.items[0].image', 'apple') WHERE id = 1;
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'banana') WHERE id = 1;
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'orange') WHERE id = 1;
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'grape') WHERE id = 1;
-- ... 以此类推
```

- [ ] **Step 3: 验证英语配图显示**

启动后端，访问 `GET /api/v1/lessons/1`，确认 content 中 items 的 image 字段已变为完整 URL（如 `http://localhost:8080/images/words/apple.jpg`），且图片可正常访问。

### Task 2.6：阶段2集成验证

- [ ] **Step 1: 启动后端和前端**

```bash
# 终端1: 启动后端
cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && ./run.sh

# 终端2: 启动前端
cd frontend && npm run dev
```

- [ ] **Step 2: 验证 WORD 类型课时**

打开 `http://localhost:5173/lesson/2`，验证：图片显示、听发音、跟读评分、切换、结算全流程正常。

- [ ] **Step 3: 验证 SENTENCE 类型课时**

打开 `http://localhost:5173/lesson/5`，验证句型课全流程正常。

- [ ] **Step 4: 验证未支持课型占位提示**

打开一个 READING/QUIZ/CALCULATE 类型课时（当前数据库中无此类型数据，可跳过或手动插入测试数据），确认显示"该课型正在开发中，敬请期待！"。

- [ ] **Step 5: 提交**

```bash
git add -A
git commit -m "feat(phase2): 英语迁移完成 - LessonView重构为分发器+模板提取+配图补全

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## 阶段3：课外火车模块（Reading/Quiz模板 + V14迁移 + AI配图）

**目标：** 创建 ReadingLesson 和 QuizLesson 两个前端模板组件，通过 Flyway V14 播种课外火车学科内容（4主题8单元24课时），AI生成约30张火车配图。

**数据结构设计：**

READING 类型 content JSON：
```json
{
  "type": "READING",
  "items": [
    {
      "title": "客运火车",
      "content": "客运火车是用来运送旅客的火车...",
      "image": "train/passenger-train"
    }
  ]
}
```

QUIZ 类型 content JSON：
```json
{
  "type": "QUIZ",
  "items": [
    {
      "question": "哪种火车跑得最快？",
      "options": ["蒸汽火车", "高铁", "货车", "地铁"],
      "answer": 1,
      "image": "train/high-speed-quiz"
    }
  ]
}
```

### Task 3.1：创建 ReadingLesson 模板组件

**Files:**
- Create: `frontend/src/components/lesson-templates/ReadingLesson.vue`

**设计思路：** READING 类型为图文翻页阅读，不涉及录音评分。展示标题+正文+配图，提供 AudioButton 朗读全文，翻页按钮切换下一篇。读完后由 LessonView 统一进入结算页。

- [ ] **Step 1: 创建 ReadingLesson.vue**

```vue
<!--
  ReadingLesson.vue - READING 类型课时模板
  用途: 图文翻页阅读，展示标题+正文+配图，支持朗读全文和翻页。
        适用于语文寓言故事、课外火车科普阅读。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description READING 类型课时学习模板，图文展示+翻页阅读+朗读功能。
 */
import { computed } from 'vue'
import AudioButton from '../AudioButton.vue'
import AppButton from '../AppButton.vue'
import mascotCompanion from '../../assets/mascot/mascot-companion.jpg'

/**
 * 组件 Props
 * @property {Object} currentItem 当前阅读项 { title, content, image }
 * @property {number} currentIndex 当前索引
 * @property {number} totalItems 总项数
 * @property {boolean} isLastItem 是否最后一项
 */
const props = defineProps({
  /** 当前阅读项对象 */
  currentItem: { type: Object, default: null },
  /** 当前索引（从0开始） */
  currentIndex: { type: Number, default: 0 },
  /** 总项数 */
  totalItems: { type: Number, default: 0 },
  /** 是否最后一项 */
  isLastItem: { type: Boolean, default: false }
})

/**
 * 组件 Emits
 * @event next 切换下一项/完成
 */
const emit = defineEmits({
  /** 切换下一篇或完成阅读 */
  next: null
})

/**
 * 当前阅读项的朗读文本（标题+正文拼接）。
 */
const readAloudText = computed(() => {
  if (!props.currentItem) return ''
  const title = props.currentItem.title || ''
  const content = props.currentItem.content || ''
  return title ? `${title}。${content}` : content
})

/**
 * 进度百分比（0-100）。
 */
const progressPercent = computed(() => {
  if (props.totalItems === 0) return 0
  return Math.round(((props.currentIndex + 1) / props.totalItems) * 100)
})
</script>

<template>
  <div class="card-area">
    <!-- 顶部进度条 -->
    <div class="progress-section">
      <div class="progress-info">
        <span>第 {{ currentIndex + 1 }} / {{ totalItems }} 页</span>
      </div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
      </div>
    </div>

    <!-- 小老鼠陪伴插画 -->
    <img :src="mascotCompanion" alt="小老鼠陪伴" class="mascot-companion" />

    <!-- 阅读卡片: 配图 + 标题 + 正文 -->
    <div class="reading-card">
      <div v-if="currentItem.image" class="reading-image-wrapper">
        <img :src="currentItem.image" :alt="currentItem.title" class="reading-image" />
      </div>
      <h1 class="reading-title">{{ currentItem.title }}</h1>
      <p class="reading-content">{{ currentItem.content }}</p>
    </div>

    <!-- 操作区: 朗读 + 翻页 -->
    <div class="actions">
      <AudioButton :text="readAloudText" />
      <AppButton
        variant="primary"
        size="lg"
        block
        @click="emit('next')"
      >
        {{ isLastItem ? '完成阅读' : '下一页 →' }}
      </AppButton>
    </div>
  </div>
</template>

<style scoped>
.card-area {
  max-width: 480px;
  margin: 0 auto;
  position: relative;
}

.progress-section { margin-bottom: var(--space-5); }
.progress-info { text-align: center; color: var(--text-tertiary); font-size: var(--text-sm); margin-bottom: var(--space-2); }
.progress-bar { height: 8px; background: var(--bg-card); border-radius: var(--radius-pill); overflow: hidden; box-shadow: var(--shadow-soft); }
.progress-fill { height: 100%; background: var(--gradient-primary); border-radius: var(--radius-pill); transition: width var(--duration-slow) var(--ease-smooth); }

.mascot-companion { position: absolute; top: 0; right: 0; width: 80px; height: 80px; object-fit: contain; border-radius: var(--radius-md); z-index: 1; opacity: 0.9; }

.reading-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-card);
}

.reading-image-wrapper { margin-bottom: var(--space-4); text-align: center; }
.reading-image { width: 100%; max-width: 320px; height: 220px; object-fit: cover; border-radius: var(--radius-md); box-shadow: var(--shadow-soft); }

.reading-title { font-size: var(--text-lg); color: var(--text-primary); font-weight: var(--font-bold); margin-bottom: var(--space-3); text-align: center; }

.reading-content { font-size: var(--text-base); color: var(--text-secondary); line-height: 1.8; text-align: justify; }

.actions { display: flex; flex-direction: column; gap: var(--space-3); }

@media (max-width: 480px) {
  .mascot-companion { width: 64px; height: 64px; }
  .reading-image { height: 180px; }
}
</style>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/components/lesson-templates/ReadingLesson.vue
git commit -m "feat(lesson): 新增ReadingLesson模板(图文翻页阅读)

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

### Task 3.2：创建 QuizLesson 模板组件

**Files:**
- Create: `frontend/src/components/lesson-templates/QuizLesson.vue`

**设计思路：** QUIZ 类型为选择题，用户点选答案后即时反馈对错，不涉及录音。答对率作为评分依据（答对率×100=分数）。组件通过 `emit('answered', isCorrect)` 上报答题结果，LessonView 统一记录和计算最终分数。

- [ ] **Step 1: 创建 QuizLesson.vue**

```vue
<!--
  QuizLesson.vue - QUIZ 类型课时模板
  用途: 选择题学习，展示题目+选项，选择后即时判对错并反馈。
        适用于数学图形/应用题、课外火车互动问答。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description QUIZ 类型课时学习模板，选择题答题+即时反馈。
 */
import { ref, computed, watch } from 'vue'
import StarBar from '../StarBar.vue'
import AppButton from '../AppButton.vue'
import mascotCompanion from '../../assets/mascot/mascot-companion.jpg'

/**
 * 组件 Props
 * @property {Object} currentItem 当前题目 { question, options[], answer, image }
 * @property {number} currentIndex 当前题号
 * @property {number} totalItems 总题数
 * @property {boolean} isLastItem 是否最后一题
 */
const props = defineProps({
  /** 当前题目对象 */
  currentItem: { type: Object, default: null },
  /** 当前题号（从0开始） */
  currentIndex: { type: Number, default: 0 },
  /** 总题数 */
  totalItems: { type: Number, default: 0 },
  /** 是否最后一题 */
  isLastItem: { type: Boolean, default: false }
})

/**
 * 组件 Emits
 * @event answered 答题完成，上报是否正确
 * @event next 切换下一题/完成
 */
const emit = defineEmits({
  /** 答题完成，参数为是否答对 */
  answered: null,
  /** 切换下一题或完成 */
  next: null
})

// 选中的选项索引（null 表示未选）
const selectedIndex = ref(null)
// 是否已答题（选中后锁定选项）
const hasAnswered = ref(false)

/**
 * 当前题是否答对。
 */
const isCorrect = computed(() => {
  if (!hasAnswered.value || selectedIndex.value === null) return false
  return selectedIndex.value === props.currentItem?.answer
})

/**
 * 切换题目时重置答题状态。
 */
watch(() => props.currentIndex, () => {
  selectedIndex.value = null
  hasAnswered.value = false
})

/**
 * 进度百分比。
 */
const progressPercent = computed(() => {
  if (props.totalItems === 0) return 0
  return Math.round(((props.currentIndex + 1) / props.totalItems) * 100)
})

/**
 * 选择选项：首次选择时锁定并上报答题结果。
 * @param {number} index 选项索引
 */
function selectOption(index) {
  if (hasAnswered.value) return
  selectedIndex.value = index
  hasAnswered.value = true
  const correct = index === props.currentItem?.answer
  emit('answered', correct)
}

/**
 * 获取选项的样式类（答题后高亮对错）。
 * @param {number} index 选项索引
 * @return {string} CSS 类名
 */
function getOptionClass(index) {
  if (!hasAnswered.value) return 'option-default'
  if (index === props.currentItem?.answer) return 'option-correct'
  if (index === selectedIndex.value && !isCorrect.value) return 'option-wrong'
  return 'option-disabled'
}
</script>

<template>
  <div class="card-area">
    <!-- 顶部进度条 -->
    <div class="progress-section">
      <div class="progress-info">
        <span>第 {{ currentIndex + 1 }} / {{ totalItems }} 题</span>
      </div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
      </div>
    </div>

    <!-- 小老鼠陪伴插画 -->
    <img :src="mascotCompanion" alt="小老鼠陪伴" class="mascot-companion" />

    <!-- 题目卡片 -->
    <div class="quiz-card">
      <div v-if="currentItem.image" class="quiz-image-wrapper">
        <img :src="currentItem.image" :alt="currentItem.question" class="quiz-image" />
      </div>
      <h1 class="quiz-question">{{ currentItem.question }}</h1>

      <!-- 选项列表 -->
      <div class="options">
        <button
          v-for="(option, index) in currentItem.options"
          :key="index"
          :class="['option', getOptionClass(index)]"
          :disabled="hasAnswered"
          @click="selectOption(index)"
        >
          <span class="option-label">{{ String.fromCharCode(65 + index) }}</span>
          <span class="option-text">{{ option }}</span>
          <span v-if="hasAnswered && index === currentItem.answer" class="option-icon">✓</span>
          <span v-if="hasAnswered && index === selectedIndex && !isCorrect" class="option-icon">✗</span>
        </button>
      </div>

      <!-- 答题反馈 -->
      <div v-if="hasAnswered" class="feedback-area">
        <p :class="['feedback-text', isCorrect ? 'feedback-correct' : 'feedback-wrong']">
          {{ isCorrect ? '回答正确！太棒了！' : '答错了，正确答案是 ' + String.fromCharCode(65 + currentItem.answer) + '：' + currentItem.options[currentItem.answer] }}
        </p>
      </div>
    </div>

    <!-- 下一题/完成按钮（答题后显示） -->
    <AppButton
      v-if="hasAnswered"
      variant="primary"
      size="lg"
      block
      @click="emit('next')"
    >
      {{ isLastItem ? '完成答题' : '下一题 →' }}
    </AppButton>
  </div>
</template>

<style scoped>
.card-area { max-width: 480px; margin: 0 auto; position: relative; }

.progress-section { margin-bottom: var(--space-5); }
.progress-info { text-align: center; color: var(--text-tertiary); font-size: var(--text-sm); margin-bottom: var(--space-2); }
.progress-bar { height: 8px; background: var(--bg-card); border-radius: var(--radius-pill); overflow: hidden; box-shadow: var(--shadow-soft); }
.progress-fill { height: 100%; background: var(--gradient-primary); border-radius: var(--radius-pill); transition: width var(--duration-slow) var(--ease-smooth); }

.mascot-companion { position: absolute; top: 0; right: 0; width: 80px; height: 80px; object-fit: contain; border-radius: var(--radius-md); z-index: 1; opacity: 0.9; }

.quiz-card { background: var(--bg-card); border-radius: var(--radius-lg); padding: var(--space-6); margin-bottom: var(--space-6); box-shadow: var(--shadow-card); }

.quiz-image-wrapper { margin-bottom: var(--space-4); text-align: center; }
.quiz-image { width: 100%; max-width: 280px; height: 200px; object-fit: cover; border-radius: var(--radius-md); box-shadow: var(--shadow-soft); }

.quiz-question { font-size: var(--text-lg); color: var(--text-primary); font-weight: var(--font-bold); margin-bottom: var(--space-5); text-align: center; line-height: 1.5; }

.options { display: flex; flex-direction: column; gap: var(--space-3); }

.option { display: flex; align-items: center; gap: var(--space-3); padding: var(--space-3) var(--space-4); border-radius: var(--radius-md); border: 2px solid var(--border-light); background: var(--bg-card); cursor: pointer; transition: all var(--duration-fast) var(--ease-smooth); font-size: var(--text-base); text-align: left; }

.option-default:hover { border-color: var(--color-primary); background: var(--bg-muted); }
.option-correct { border-color: var(--color-success); background: rgba(76, 217, 100, 0.1); }
.option-wrong { border-color: var(--color-warning); background: rgba(255, 107, 107, 0.1); }
.option-disabled { opacity: 0.5; }

.option-label { display: inline-flex; align-items: center; justify-content: center; width: 32px; height: 32px; border-radius: var(--radius-pill); background: var(--color-primary); color: white; font-weight: var(--font-bold); font-size: var(--text-sm); flex-shrink: 0; }
.option-correct .option-label { background: var(--color-success); }
.option-wrong .option-label { background: var(--color-warning); }

.option-text { flex: 1; color: var(--text-primary); }

.option-icon { font-size: 1.2rem; font-weight: bold; }
.option-correct .option-icon { color: var(--color-success); }
.option-wrong .option-icon { color: var(--color-warning); }

.feedback-area { margin-top: var(--space-4); padding: var(--space-3); border-radius: var(--radius-md); text-align: center; }
.feedback-correct { color: var(--color-success); font-weight: var(--font-bold); }
.feedback-wrong { color: var(--color-warning); font-weight: var(--font-bold); }

@media (prefers-reduced-motion: no-preference) {
  .option-correct { animation: correctPulse var(--duration-normal) var(--ease-bounce); }
  .option-wrong { animation: wrongShake var(--duration-fast) var(--ease-smooth); }
}

@keyframes correctPulse { 0%, 100% { transform: scale(1); } 50% { transform: scale(1.02); } }
@keyframes wrongShake { 0%, 100% { transform: translateX(0); } 25% { transform: translateX(-4px); } 75% { transform: translateX(4px); } }

@media (max-width: 480px) {
  .mascot-companion { width: 64px; height: 64px; }
  .quiz-image { height: 160px; }
}
</style>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/components/lesson-templates/QuizLesson.vue
git commit -m "feat(lesson): 新增QuizLesson模板(选择题判对错)

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

### Task 3.3：在 LessonView 分发器中注册新模板

**Files:**
- Modify: `frontend/src/views/LessonView.vue`

**改动说明：** 在阶段2重构的 LessonView 基础上，新增 ReadingLesson 和 QuizLesson 的导入和分发。同时为 QUIZ/READING 类型添加答题结果追踪逻辑。

- [ ] **Step 1: 在 LessonView 中导入新模板**

在 `<script setup>` 的 import 区域追加：

```javascript
import ReadingLesson from '../components/lesson-templates/ReadingLesson.vue'
import QuizLesson from '../components/lesson-templates/QuizLesson.vue'
```

- [ ] **Step 2: 扩展 lessonTemplate 计算属性**

将 `lessonTemplate` 计算属性的 switch 语句扩展：

```javascript
const lessonTemplate = computed(() => {
  if (!lesson.value) return null
  switch (lesson.value.type) {
    case 'WORD':
      return WordLesson
    case 'SENTENCE':
      return SentenceLesson
    case 'READING':
      return ReadingLesson
    case 'QUIZ':
      return QuizLesson
    default:
      // CALCULATE 等后续阶段实现
      return null
  }
})
```

- [ ] **Step 3: 添加 QUIZ 答题追踪逻辑**

在 LessonView 的状态区新增答题记录，并在模板中处理 `@answered` 事件：

```javascript
// QUIZ/CALCULATE 答题记录：每题是否答对
const answerResults = ref([])

/**
 * QUIZ/CALCULATE 答题回调：记录每题对错，计算得分。
 * 答对率 × 100 = 分数，映射到 bestScores 供结算使用。
 * @param {boolean} correct 是否答对
 */
function handleAnswered(correct) {
  answerResults.value[currentIndex.value] = correct
  // 答对=100分，答错=0分，复用 bestScores 机制
  updateBestScore(currentIndex.value, correct ? 100 : 0)
  // 更新当前显示分数
  currentScore.value = correct ? 100 : 0
  currentStars.value = scoreToStars(correct ? 100 : 0)
  scoreMessage.value = correct ? '回答正确！' : '答错了，再接再厉！'
}
```

- [ ] **Step 4: 在模板中为 QUIZ 组件绑定 answered 事件**

在 `<component :is="lessonTemplate">` 上追加 `@answered="handleAnswered"`：

```vue
<component
  v-else-if="lessonTemplate && currentItem"
  :is="lessonTemplate"
  :current-item="currentItem"
  :current-index="currentIndex"
  :total-items="totalItems"
  :current-score="currentScore"
  :current-stars="currentStars"
  :score-message="scoreMessage"
  :is-scoring="isScoring"
  :is-last-item="isLastItem"
  @recorded="handleRecorded"
  @answered="handleAnswered"
  @next="nextItem"
/>
```

- [ ] **Step 5: Commit**

```bash
git add frontend/src/views/LessonView.vue
git commit -m "feat(lesson): LessonView注册ReadingLesson和QuizLesson模板

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

### Task 3.4：Flyway V14 迁移 - 课外火车内容

**Files:**
- Create: `backend/src/main/resources/db/migration/V14__seed_train_content.sql`

**内容结构：** 4主题(theme id 5-8) × 2单元(unit id 12-19) × 3课时(lesson id 38-61) = 24课时。其中 READING 18个 + QUIZ 6个。所有 theme 的 `subject_id=4`（课外）。

- [ ] **Step 1: 创建 V14 迁移文件**

```sql
-- ============================================================
-- V14: 课外火车模块内容播种
-- 4个主题(5-8) × 2单元(12-19) × 3课时(38-61) = 24课时
-- 课时类型: READING 18个 + QUIZ 6个
-- 所有 theme 的 subject_id=4(课外)
-- image key 使用 train/ 前缀,由 WordImageResolver 解析为 /images/train/xxx.jpg
-- ============================================================

-- ============================================================
-- Section 0: 补全英语 WORD 课程的 image 字段（阶段2遗留）
-- 使用 json_set 精确更新（避免 V9 的 REPLACE emoji 匹配失败问题）
-- ============================================================
UPDATE lesson SET content = json_set(content, '$.items[0].image', 'apple') WHERE id = 1;
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'banana') WHERE id = 1;
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'orange') WHERE id = 1;
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'grape') WHERE id = 1;
-- （实际执行时需覆盖所有缺失配图的 WORD 课时，此处省略重复行）

-- ============================================================
-- Section 1: 新增主题（subject_id=4 课外）
-- ============================================================
INSERT INTO theme (id, name, sort_order, is_locked, subject_id) VALUES
(5, '火车大家族', 1, 0, 4),
(6, '火车的零件', 2, 1, 4),
(7, '火车怎么跑', 3, 1, 4),
(8, '火车的时间旅行', 4, 1, 4);

-- ============================================================
-- Section 2: 新增单元
-- ============================================================
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(12, 5, '认识火车', 1, 0),
(13, 5, '特殊火车', 2, 1),
(14, 6, '火车外面', 1, 0),
(15, 6, '火车里面', 2, 1),
(16, 7, '火车的力量', 1, 0),
(17, 7, '动力问答', 2, 1),
(18, 8, '火车的过去', 1, 0),
(19, 8, '火车的未来', 2, 1);

-- ============================================================
-- Section 3: 课时内容 - 主题5 火车大家族
-- ============================================================

-- Lesson 38: 客运火车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(38, 12, '客运火车', 'READING',
'{"type":"READING","items":['
|| '{"title":"什么是客运火车","content":"客运火车是用来运送旅客的火车。它有舒适的座位和大大的窗户，让旅客可以欣赏沿途的风景。","image":"train/passenger-train"},'
|| '{"title":"动车组列车","content":"动车组列车是一种速度很快的客运火车。它有很多节车厢连在一起，每节车厢都有自己的动力，可以跑得又快又稳。","image":"train/emu-train"},'
|| '{"title":"卧铺火车","content":"卧铺火车有可以躺下睡觉的床位。如果要坐很久的火车，旅客可以在火车上睡觉，第二天醒来就到目的地了。","image":"train/sleeper-train"}'
|| ']}', 1, 3);

-- Lesson 39: 货运火车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(39, 12, '货运火车', 'READING',
'{"type":"READING","items":['
|| '{"title":"什么是货运火车","content":"货运火车是用来运送货物的火车。它可以把煤炭、粮食、汽车等重物运到很远的地方，力气可大了。","image":"train/freight-train"},'
|| '{"title":"集装箱火车","content":"集装箱火车把货物装在大大的铁箱子里。到站后可以用吊车把箱子搬到卡车上，非常方便。","image":"train/container-train"},'
|| '{"title":"特殊货运火车","content":"有些货运火车很特别，比如运汽车的火车有好几层，运石油的火车像一个大油罐。","image":"train/special-freight"}'
|| ']}', 2, 3);

-- Lesson 40: 火车家族问答 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(40, 12, '火车家族问答', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"客运火车主要运送什么？","options":["旅客","煤炭","汽车","信件"],"answer":0,"image":"train/passenger-quiz"},'
|| '{"question":"动车组列车的特点是？","options":["跑得慢","速度很快","只有一节","不能坐人"],"answer":1,"image":""},'
|| '{"question":"卧铺火车有什么？","options":["游泳池","可以睡觉的床位","厨房","电影院"],"answer":1,"image":""},'
|| '{"question":"货运火车主要运送什么？","options":["旅客","货物","宠物","报纸"],"answer":1,"image":""},'
|| '{"question":"集装箱火车的好处是？","options":["跑得快","方便装卸","颜色好看","声音小"],"answer":1,"image":""}'
|| ']}', 3, 3);

-- Lesson 41: 高铁列车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(41, 13, '高铁列车', 'READING',
'{"type":"READING","items":['
|| '{"title":"什么是高铁","content":"高铁是高速铁路列车的简称。它的速度非常快，每小时可以跑300公里以上，比汽车快多了。","image":"train/high-speed-rail"},'
|| '{"title":"高铁的样子","content":"高铁的车头尖尖的，像一颗子弹。这样设计可以减少空气阻力，让火车跑得更快。","image":"train/bullet-train"},'
|| '{"title":"坐高铁的体验","content":"坐高铁非常舒适，车厢里很安静，桌子可以放东西。从北京到上海只要4个多小时呢。","image":"train/hsr-interior"}'
|| ']}', 1, 3);

-- Lesson 42: 地铁和轻轨 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(42, 13, '地铁和轻轨', 'READING',
'{"type":"READING","items":['
|| '{"title":"什么是地铁","content":"地铁是在地下隧道里行驶的火车。城市里有很多地铁站，人们可以坐地铁去上班、上学，不用担心堵车。","image":"train/subway"},'
|| '{"title":"地铁的好处","content":"地铁不会堵车，准时又快捷。而且地铁用电驱动，不会排放废气，很环保。","image":"train/subway-platform"},'
|| '{"title":"轻轨列车","content":"轻轨是在地面上或高架上行驶的小火车。它比地铁轻一些，但运量也很大，很多城市都有轻轨。","image":"train/light-rail"}'
|| ']}', 2, 3);

-- Lesson 43: 磁悬浮列车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(43, 13, '磁悬浮列车', 'READING',
'{"type":"READING","items":['
|| '{"title":"什么是磁悬浮","content":"磁悬浮列车是一种神奇的火车。它不接触铁轨，而是悬浮在铁轨上方飞驰，速度非常快。","image":"train/maglev"},'
|| '{"title":"磁悬浮的原理","content":"磁悬浮列车利用磁铁的同极相斥原理，让火车浮起来。因为不接触铁轨，所以没有摩擦，跑得特别快。","image":"train/maglev-principle"},'
|| '{"title":"最快的火车","content":"上海的磁悬浮列车是世界上跑得最快的商用火车之一，最高速度每小时430公里，像飞一样！","image":"train/shanghai-maglev"}'
|| ']}', 3, 3);

-- ============================================================
-- Section 4: 课时内容 - 主题6 火车的零件
-- ============================================================

-- Lesson 44: 车头和车厢 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(44, 14, '车头和车厢', 'READING',
'{"type":"READING","items":['
|| '{"title":"火车的车头","content":"火车的车头叫做机车。它是火车的心脏，装有强大的发动机，拉着后面所有的车厢往前跑。","image":"train/locomotive"},'
|| '{"title":"客运车厢","content":"客运车厢里有一排排的座位，还有小桌子。窗户大大的，可以看风景。有些车厢还有电视呢。","image":"train/passenger-carriage"},'
|| '{"title":"货运车厢","content":"货运车厢有很多种：有装煤炭的敞车，有装粮食的棚车，还有装汽车的特种车厢。","image":"train/freight-carriage"}'
|| ']}', 1, 3);

-- Lesson 45: 车轮和铁轨 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(45, 14, '车轮和铁轨', 'READING',
'{"type":"READING","items":['
|| '{"title":"火车的车轮","content":"火车的车轮是钢做的，非常坚硬。车轮紧紧卡在铁轨上，这样火车就不会跑偏了。","image":"train/train-wheels"},'
|| '{"title":"铁轨的样子","content":"铁轨是两条平行的钢轨。火车就在铁轨上跑，铁轨铺在枕木上，枕木下面还有石子固定。","image":"train/railway-track"},'
|| '{"title":"道岔的作用","content":"道岔是可以移动的铁轨零件。它让火车可以从一条轨道换到另一条，像岔路口一样。","image":"train/railway-switch"}'
|| ']}', 2, 3);

-- Lesson 46: 零件问答 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(46, 14, '零件问答', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"火车的车头叫做什么？","options":["车尾","机车","车厢","轮子"],"answer":1,"image":""},'
|| '{"question":"火车的车轮是什么材料？","options":["木头","塑料","钢铁","橡胶"],"answer":2,"image":""},'
|| '{"question":"铁轨铺在什么上面？","options":["枕木","沙子","水泥","水"],"answer":0,"image":""},'
|| '{"question":"道岔的作用是？","options":["让火车停下","换轨道","鸣笛","开灯"],"answer":1,"image":""},'
|| '{"question":"货运车厢有几种？","options":["1种","2种","很多种","只有1种"],"answer":2,"image":""}'
|| ']}', 3, 3);

-- Lesson 47: 车门和窗户 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(47, 15, '车门和窗户', 'READING',
'{"type":"READING","items":['
|| '{"title":"自动门","content":"现在的火车门是自动开关的。到站后门会自动打开，发车前会自动关闭，还会发出提示音。","image":"train/automatic-door"},'
|| '{"title":"大大的窗户","content":"火车的窗户很大，是用两层玻璃做的。这样可以隔音隔热，坐在车里看风景很清楚。","image":"train/train-windows"},'
|| '{"title":"紧急逃生窗","content":"火车的窗户旁边有一把小锤子。遇到紧急情况时，用锤子敲碎玻璃就可以逃生。","image":"train/emergency-window"}'
|| ']}', 1, 3);

-- Lesson 48: 座椅和行李架 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(48, 15, '座椅和行李架', 'READING',
'{"type":"READING","items":['
|| '{"title":"舒适的座椅","content":"火车的座椅可以调节角度，想休息时可以往后靠。座椅中间有扶手，旁边有小桌子。","image":"train/train-seats"},'
|| '{"title":"行李架","content":"车厢上方有行李架，旅客把行李放在上面。这样走廊就不会被堵住，大家走起来很方便。","image":"train/luggage-rack"},'
|| '{"title":"车厢里的设施","content":"车厢里有洗手间、饮水机和垃圾桶。高铁还有插座可以充电，非常方便。","image":"train/carriage-facilities"}'
|| ']}', 2, 3);

-- Lesson 49: 车内设施问答 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(49, 15, '车内设施问答', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"火车门是怎么开关的？","options":["手动","自动","不关","用绳子拉"],"answer":1,"image":""},'
|| '{"question":"火车窗户用什么玻璃？","options":["一层玻璃","两层玻璃","塑料","不装玻璃"],"answer":1,"image":""},'
|| '{"question":"紧急逃生用什么？","options":["钥匙","小锤子","螺丝刀","剪刀"],"answer":1,"image":""},'
|| '{"question":"行李放在哪里？","options":["地上","行李架","座位上","门口"],"answer":1,"image":""},'
|| '{"question":"高铁有什么特别设施？","options":["游泳池","充电插座","厨房","床"],"answer":1,"image":""}'
|| ']}', 3, 3);

-- ============================================================
-- Section 5: 课时内容 - 主题7 火车怎么跑
-- ============================================================

-- Lesson 50: 蒸汽机车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(50, 16, '蒸汽机车', 'READING',
'{"type":"READING","items":['
|| '{"title":"最早的火车","content":"最早的火车是蒸汽机车。它烧煤炭把水烧成蒸汽，用蒸汽的力量推动车轮转动，所以叫蒸汽火车。","image":"train/steam-locomotive"},'
|| '{"title":"蒸汽机车的外形","content":"蒸汽机车前面有一个大烟囱，烧煤时会产生很多烟。车头是黑色的，前面有一个大大的圆形锅炉。","image":"train/steam-engine-front"},'
|| '{"title":"蒸汽机的力量","content":"蒸汽机车的力气很大，但是速度不快。而且烧煤会产生黑烟，对环境不太友好。","image":"train/steam-smoke"}'
|| ']}', 1, 3);

-- Lesson 51: 柴油机车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(51, 16, '柴油机车', 'READING',
'{"type":"READING","items":['
|| '{"title":"柴油发动机","content":"柴油机车用柴油发动机产生动力。它不需要烧煤炭，比蒸汽机车干净多了，力气也更大。","image":"train/diesel-locomotive"},'
|| '{"title":"柴油车的优点","content":"柴油机车跑得比蒸汽机车快，加一次油可以跑很远。很多货运火车到现在还在用柴油机车。","image":"train/diesel-freight"},'
|| '{"title":"内燃动车组","content":"有些短途客运火车也用柴油发动机，叫做内燃动车组。它比电动火车便宜，适合没有电线的铁路。","image":"train/diesel-multiple-unit"}'
|| ']}', 2, 3);

-- Lesson 52: 电力机车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(52, 16, '电力机车', 'READING',
'{"type":"READING","items":['
|| '{"title":"用电的火车","content":"电力机车用电来驱动。铁轨上方有电线，火车的受电弓接触电线取电，电动机转动带动车轮。","image":"train/electric-locomotive"},'
|| '{"title":"电力机车的好处","content":"电力机车速度快、力气大、不排废气、噪音小。现在大部分客运火车都是电力机车。","image":"train/electric-train"},'
|| '{"title":"高铁也是电力","content":"中国的高铁全部是电力驱动。电力让高铁可以跑得又快又稳，每小时超过300公里。","image":"train/hsr-electric"}'
|| ']}', 3, 3);

-- Lesson 53: 蒸汽怎么跑 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(53, 17, '蒸汽怎么跑', 'READING',
'{"type":"READING","items":['
|| '{"title":"烧水产生蒸汽","content":"蒸汽机车先在锅炉里烧煤炭，把水烧开产生大量蒸汽。蒸汽就像一个看不见的大力士。","image":"train/steam-boiler"},'
|| '{"title":"蒸汽推动活塞","content":"蒸汽进入气缸推动活塞来回运动。活塞连着车轮的连杆，把来回运动变成车轮的转动。","image":"train/steam-piston"},'
|| '{"title":"车轮转起来","content":"活塞推动连杆，连杆带动车轮转动。车轮一转，火车就往前跑了。呜呜！","image":"train/steam-wheels"}'
|| ']}', 1, 3);

-- Lesson 54: 电力怎么跑 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(54, 17, '电力怎么跑', 'READING',
'{"type":"READING","items":['
|| '{"title":"架空电线供电","content":"电力机车的铁轨上方有架空电线。电线里有电，火车顶上的受电弓像一只手，摸着电线取电。","image":"train/pantograph"},'
|| '{"title":"电进电动机","content":"电从电线下来，经过变压器变压，进入电动机。电动机像一个大马达，一通电就转起来。","image":"train/electric-motor"},'
|| '{"title":"电动机带动车轮","content":"电动机转动后，通过齿轮带动车轮旋转。车轮一转，火车就跑起来了。又快又安静！","image":"train/electric-drive"}'
|| ']}', 2, 3);

-- Lesson 55: 动力问答 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(55, 17, '动力问答', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"蒸汽机车烧什么？","options":["柴油","煤炭","电","天然气"],"answer":1,"image":""},'
|| '{"question":"柴油机车用什么发动机？","options":["蒸汽机","柴油发动机","电动机","风车"],"answer":1,"image":""},'
|| '{"question":"电力机车从哪里取电？","options":["电池","架空电线","太阳能","轮子"],"answer":1,"image":""},'
|| '{"question":"高铁是什么驱动？","options":["蒸汽","柴油","电力","人力"],"answer":2,"image":""},'
|| '{"question":"蒸汽机车的活塞做什么运动？","options":["转圈","来回运动","不动","上下跳"],"answer":1,"image":""}'
|| ']}', 3, 3);

-- ============================================================
-- Section 6: 课时内容 - 主题8 火车的时间旅行
-- ============================================================

-- Lesson 56: 最早的火车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(56, 18, '最早的火车', 'READING',
'{"type":"READING","items":['
|| '{"title":"蒸汽时代的开始","content":"200多年前，英国人发明了第一台蒸汽火车。从此人类有了不用马拉的火车，可以跑得又快又远。","image":"train/first-steam"},'
|| '{"title":"斯蒂芬森的火箭号","content":"1829年，斯蒂芬森造了一辆叫火箭号的蒸汽机车。它跑得比马还快，让大家惊呆了。","image":"train/rocket-locomotive"},'
|| '{"title":"火车来到中国","content":"100多年前，中国有了第一条铁路。虽然当时很多人没见过火车，但很快火车就成了重要的交通工具。","image":"train/china-first-railway"}'
|| ']}', 1, 3);

-- Lesson 57: 越来越快 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(57, 18, '越来越快', 'READING',
'{"type":"READING","items":['
|| '{"title":"从蒸汽到柴油","content":"蒸汽火车跑了100多年后，人们发明了柴油机车。柴油火车比蒸汽火车快多了，也不用一直加煤了。","image":"train/steam-to-diesel"},'
|| '{"title":"电力时代来临","content":"后来又发明了电力机车。电力火车更快更干净，渐渐地蒸汽火车和柴油火车都被淘汰了。","image":"train/electric-era"},'
|| '{"title":"速度的飞跃","content":"从蒸汽火车每小时几十公里，到现在高铁每小时300多公里，火车的速度提高了将近10倍！","image":"train/speed-evolution"}'
|| ']}', 2, 3);

-- Lesson 58: 火车历史问答 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(58, 18, '火车历史问答', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"第一台蒸汽火车在哪里发明？","options":["中国","美国","英国","日本"],"answer":2,"image":""},'
|| '{"question":"火箭号是谁造的？","options":["爱迪生","斯蒂芬森","牛顿","瓦特"],"answer":1,"image":""},'
|| '{"question":"蒸汽火车之后是什么火车？","options":["马车","柴油火车","高铁","磁悬浮"],"answer":1,"image":""},'
|| '{"question":"电力火车比蒸汽火车？","options":["更慢","更快更干净","更脏","更吵"],"answer":1,"image":""},'
|| '{"question":"高铁每小时跑多少公里？","options":["50公里","100公里","300多公里","1000公里"],"answer":2,"image":""}'
|| ']}', 3, 3);

-- Lesson 59: 中国高铁 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(59, 19, '中国高铁', 'READING',
'{"type":"READING","items":['
|| '{"title":"中国高铁的故事","content":"中国高铁从零开始，现在已经是世界上最长的高铁网络。复兴号列车是中国自己造的高铁，跑得又快又稳。","image":"train/china-hsr"},'
|| '{"title":"复兴号","content":"复兴号是中国自主研发的高铁列车。它的速度每小时350公里，车头像一条飞龙，非常漂亮。","image":"train/fuxing-train"},'
|| '{"title":"和谐号","content":"和谐号是中国较早的高铁列车。虽然比复兴号早，但速度也很快，每小时250到350公里。","image":"train/hexie-train"}'
|| ']}', 1, 3);

-- Lesson 60: 磁悬浮列车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(60, 19, '磁悬浮列车', 'READING',
'{"type":"READING","items":['
|| '{"title":"未来的火车","content":"磁悬浮列车是未来的火车。它悬浮在轨道上飞驰，没有车轮和铁轨的摩擦，速度可以超过500公里每小时。","image":"train/future-maglev"},'
|| '{"title":"超级高铁","content":"科学家正在研究超级高铁。它在真空管道里跑，速度可能超过1000公里每小时，比飞机还快！","image":"train/hyperloop"},'
|| '{"title":"智能火车","content":"未来的火车会越来越聪明。它们可以自动驾驶，还能和手机连接，告诉你什么时候到站。","image":"train/smart-train"}'
|| ']}', 2, 3);

-- Lesson 61: 未来火车 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(61, 19, '未来火车', 'READING',
'{"type":"READING","items":['
|| '{"title":"环保火车","content":"未来的火车会更环保。它们用太阳能和风能发电，不会产生污染，保护我们的地球。","image":"train/eco-train"},'
|| '{"title":"太空火车","content":"也许有一天，我们可以坐火车去太空！科学家正在研究能在管道里飞驰的太空列车。","image":"train/space-train"},'
|| '{"title":"火车改变世界","content":"从蒸汽到磁悬浮，火车一直在进步。火车让旅行更方便，让世界变得更小，人们的距离更近了。","image":"train/train-future"}'
|| ']}', 3, 3);
```

- [ ] **Step 2: 启动后端验证迁移**

```bash
cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && ./run.sh
```

验证启动日志中 Flyway 迁移成功，无 SQL 错误。

- [ ] **Step 3: 验证数据完整性**

通过 API 验证课外火车学科内容：

```bash
# 验证学科列表
curl http://localhost:8080/api/v1/subjects | python3 -m json.tool

# 验证课外学科的主题
curl http://localhost:8080/api/v1/themes/subject/4 | python3 -m json.tool

# 验证某课时详情（READING类型）
curl http://localhost:8080/api/v1/lessons/38 | python3 -m json.tool

# 验证QUIZ类型课时
curl http://localhost:8080/api/v1/lessons/40 | python3 -m json.tool
```

确认：
- 4个课外主题（id 5-8）正确返回
- 24个课时（id 38-61）正确返回
- READING 课时的 content 中 image 字段被解析为完整 URL（如 `http://localhost:8080/images/train/passenger-train.jpg`）
- QUIZ 课时的 content 中 options 数组和 answer 索引正确

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/resources/db/migration/V14__seed_train_content.sql
git commit -m "feat(db): V14迁移-课外火车4主题24课时内容播种

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

### Task 3.5：AI 生成火车配图

**目标：** 为课外火车模块生成约30张配图，存放于 `backend/src/main/resources/static/images/train/`。

**配图清单（共 30 张）：**

| 图片 key | 用途 | 对应课时 |
|---------|------|---------|
| train/passenger-train | 客运火车 | L38 |
| train/emu-train | 动车组 | L38 |
| train/sleeper-train | 卧铺火车 | L38 |
| train/freight-train | 货运火车 | L39 |
| train/container-train | 集装箱火车 | L39 |
| train/special-freight | 特殊货运 | L39 |
| train/high-speed-rail | 高铁 | L41 |
| train/bullet-train | 子弹头高铁 | L41 |
| train/hsr-interior | 高铁内饰 | L41 |
| train/subway | 地铁 | L42 |
| train/subway-platform | 地铁站台 | L42 |
| train/light-rail | 轻轨 | L42 |
| train/maglev | 磁悬浮 | L43 |
| train/maglev-principle | 磁悬浮原理 | L43 |
| train/shanghai-maglev | 上海磁悬浮 | L43 |
| train/locomotive | 机车 | L44 |
| train/passenger-carriage | 客运车厢 | L44 |
| train/freight-carriage | 货运车厢 | L44 |
| train/train-wheels | 火车轮 | L45 |
| train/railway-track | 铁轨 | L45 |
| train/railway-switch | 道岔 | L45 |
| train/steam-locomotive | 蒸汽机车 | L50 |
| train/diesel-locomotive | 柴油机车 | L51 |
| train/electric-locomotive | 电力机车 | L52 |
| train/pantograph | 受电弓 | L54 |
| train/first-steam | 最早蒸汽火车 | L56 |
| train/china-hsr | 中国高铁 | L59 |
| train/fuxing-train | 复兴号 | L59 |
| train/future-maglev | 未来磁悬浮 | L60 |
| train/train-future | 火车未来 | L61 |

- [ ] **Step 1: 逐张生成火车配图**

使用 GenerateImage 工具逐张生成，保存路径为 `backend/src/main/resources/static/images/train/{key去掉train/前缀}.jpg`。

prompt 模板：
```
[PURPOSE]: 儿童教育APP课外火车科普配图
[DESCRIPTION]: 扁平2.5D风格的{具体火车/零件描述}，圆润可爱，暖色调，浅黄色纯色背景，无文字标注，儿童友好插画风格，1024x1024正方形
```

示例（第一张）：
```
[PURPOSE]: 儿童教育APP课外火车科普配图
[DESCRIPTION]: 扁平2.5D风格的客运火车，绿色车身，圆润车头，大大的窗户，圆润可爱，暖色调，浅黄色纯色背景，无文字标注，儿童友好插画风格，1024x1024正方形
```

- [ ] **Step 2: 验证图片可通过 URL 访问**

```bash
curl -I http://localhost:8080/images/train/passenger-train.jpg
```

确认返回 200 OK。

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/resources/static/images/train/
git commit -m "feat(images): 课外火车模块30张AI配图

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

### Task 3.6：阶段3集成验证

- [ ] **Step 1: 启动后端和前端**

```bash
# 终端1: 启动后端
cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && ./run.sh

# 终端2: 启动前端
cd frontend && npm run dev
```

- [ ] **Step 2: 验证课外火车学科入口**

打开 `http://localhost:5173/`，点击"课外"学科卡片，确认显示4个火车主题。

- [ ] **Step 3: 验证 READING 类型课时**

打开 `http://localhost:5173/lesson/38`（客运火车，READING），验证：
- 显示标题、正文、配图
- 朗读按钮可用（TTS 朗读中文）
- 翻页按钮切换下一篇
- 读完所有篇后进入结算页

- [ ] **Step 4: 验证 QUIZ 类型课时**

打开 `http://localhost:5173/lesson/40`（火车家族问答，QUIZ），验证：
- 显示题目和4个选项
- 点击选项后即时反馈对错
- 答对显示绿色✓，答错显示红色✗并标出正确答案
- 下一题按钮答题后出现
- 完成所有题后进入结算页，星星数基于答对率

- [ ] **Step 5: 验证解锁流程**

确认课外火车主题的单元解锁逻辑正常（首单元可学，后续单元需完成前一单元）。

- [ ] **Step 6: 提交**

```bash
git add -A
git commit -m "feat(phase3): 课外火车模块完成 - Reading/Quiz模板+V14迁移+30张配图

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## 阶段4：语文模块（V15迁移 + 复用模板 + AI配图）

**目标：** 通过 Flyway V15 播种语文学科内容（4主题8单元24课时），复用阶段2的 WordLesson/SentenceLesson 和阶段3的 ReadingLesson 模板，AI生成约40张语文配图。**无需新增前端组件。**

**内容结构：**

| 主题 | 类型 | 课时数 | 前端模板 | 配图需求 |
|------|------|--------|---------|---------|
| 拼音启蒙 | WORD | 6 | WordLesson | 无（用emoji替代） |
| 汉字识读 | WORD | 6 | WordLesson | ~24张 |
| 古诗朗诵 | SENTENCE | 6 | SentenceLesson | 无（用emoji替代） |
| 寓言故事 | READING | 6 | ReadingLesson | ~16张 |

**数据结构设计：**

WORD 类型（拼音）：
```json
{"type":"WORD","items":[{"word":"ā","phonetic":"ā","translation":"啊","emoji":"🗣️"}]}
```

WORD 类型（汉字）：
```json
{"type":"WORD","items":[{"word":"山","phonetic":"shān","translation":"山","emoji":"⛰️","image":"chinese/hanzi-shan"}]}
```

SENTENCE 类型（古诗）：
```json
{"type":"SENTENCE","items":[{"sentence":"床前明月光","translation":"明亮的月光照在床前","emoji":"🌙"}]}
```

READING 类型（寓言故事）：
```json
{"type":"READING","items":[{"title":"龟兔赛跑","content":"从前...","image":"story/tortoise-hare"}]}
```

### Task 4.1：Flyway V15 迁移 - 语文内容

**Files:**
- Create: `backend/src/main/resources/db/migration/V15__seed_chinese_content.sql`

**内容结构：** 4主题(theme id 9-12) × 2单元(unit id 20-27) × 3课时(lesson id 62-85) = 24课时。所有 theme 的 `subject_id=2`（语文）。

- [ ] **Step 1: 创建 V15 迁移文件**

```sql
-- ============================================================
-- V15: 语文模块内容播种
-- 4个主题(9-12) × 2单元(20-27) × 3课时(62-85) = 24课时
-- 课时类型: WORD 12个(拼音6+汉字6) + SENTENCE 6个(古诗) + READING 6个(寓言)
-- 所有 theme 的 subject_id=2(语文)
-- ============================================================

-- Section 1: 新增主题（subject_id=2 语文）
INSERT INTO theme (id, name, sort_order, is_locked, subject_id) VALUES
(9, '拼音启蒙', 1, 0, 2),
(10, '汉字识读', 2, 1, 2),
(11, '古诗朗诵', 3, 1, 2),
(12, '寓言故事', 4, 1, 2);

-- Section 2: 新增单元
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(20, 9, '单韵母', 1, 0),
(21, 9, '声母', 2, 1),
(22, 10, '自然汉字', 1, 0),
(23, 10, '生活汉字', 2, 1),
(24, 11, '五言绝句', 1, 0),
(25, 11, '经典古诗', 2, 1),
(26, 12, '经典寓言', 1, 0),
(27, 12, '启蒙故事', 2, 1);

-- ============================================================
-- Section 3: 主题9 拼音启蒙（WORD类型，无图片，用emoji）
-- ============================================================

-- Lesson 62: a o e (WORD, 6项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(62, 20, '单韵母 a o e', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"ā","phonetic":"ā","translation":"啊","emoji":"🗣️"},'
|| '{"word":"á","phonetic":"á","translation":"啊(二声)","emoji":"❓"},'
|| '{"word":"ǎ","phonetic":"ǎ","translation":"啊(三声)","emoji":"😮"},'
|| '{"word":"à","phonetic":"à","translation":"啊(四声)","emoji":"❗"},'
|| '{"word":"ō","phonetic":"ō","translation":"喔","emoji":"🐓"},'
|| '{"word":"ē","phonetic":"ē","translation":"鹅","emoji":"🦢"}'
|| ']}', 1, 3);

-- Lesson 63: i u ü (WORD, 6项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(63, 20, '单韵母 i u ü', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"ī","phonetic":"ī","translation":"衣","emoji":"👕"},'
|| '{"word":"ū","phonetic":"ū","translation":"乌","emoji":"🐦‍⬛"},'
|| '{"word":"ǖ","phonetic":"ǖ","translation":"鱼","emoji":"🐟"},'
|| '{"word":"í","phonetic":"í","translation":"仪","emoji":"🙇"},'
|| '{"word":"ú","phonetic":"ú","translation":"无","emoji":"🚫"},'
|| '{"word":"ǘ","phonetic":"ǘ","translation":"鱼(二声)","emoji":"🐠"}'
|| ']}', 2, 3);

-- Lesson 64: 复韵母 (WORD, 6项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(64, 20, '复韵母', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"ai","phonetic":"āi","translation":"爱","emoji":"❤️"},'
|| '{"word":"ei","phonetic":"ēi","translation":"欸","emoji":"🗣️"},'
|| '{"word":"ui","phonetic":"uī","translation":"威","emoji":"💪"},'
|| '{"word":"ao","phonetic":"āo","translation":"奥","emoji":"🏆"},'
|| '{"word":"ou","phonetic":"ōu","translation":"欧","emoji":"🌍"},'
|| '{"word":"iu","phonetic":"iū","translation":"优","emoji":"⭐"}'
|| ']}', 3, 3);

-- Lesson 65: b p m f (WORD, 6项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(65, 21, '声母 b p m f', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"bā","phonetic":"bā","translation":"八","emoji":"8️⃣"},'
|| '{"word":"pā","phonetic":"pā","translation":"趴","emoji":"🤸"},'
|| '{"word":"mā","phonetic":"mā","translation":"妈","emoji":"👩"},'
|| '{"word":"fā","phonetic":"fā","translation":"发","emoji":"📤"},'
|| '{"word":"bó","phonetic":"bó","translation":"伯","emoji":"👨"},'
|| '{"word":"pō","phonetic":"pō","translation":"坡","emoji":"⛰️"}'
|| ']}', 1, 3);

-- Lesson 66: d t n l (WORD, 6项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(66, 21, '声母 d t n l', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"dā","phonetic":"dā","translation":"搭","emoji":"🏗️"},'
|| '{"word":"tā","phonetic":"tā","translation":"他","emoji":"🧑"},'
|| '{"word":"ná","phonetic":"ná","translation":"拿","emoji":"✋"},'
|| '{"word":"lā","phonetic":"lā","translation":"拉","emoji":"🤝"},'
|| '{"word":"dú","phonetic":"dú","translation":"读","emoji":"📖"},'
|| '{"word":"tú","phonetic":"tú","translation":"图","emoji":"🖼️"}'
|| ']}', 2, 3);

-- Lesson 67: g k h (WORD, 6项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(67, 21, '声母 g k h', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"gē","phonetic":"gē","translation":"哥","emoji":"👦"},'
|| '{"word":"kē","phonetic":"kē","translation":"科","emoji":"🔬"},'
|| '{"word":"hē","phonetic":"hē","translation":"喝","emoji":"🥤"},'
|| '{"word":"gū","phonetic":"gū","translation":"姑","emoji":"👧"},'
|| '{"word":"kū","phonetic":"kū","translation":"哭","emoji":"😢"},'
|| '{"word":"hú","phonetic":"hú","translation":"湖","emoji":"🏞️"}'
|| ']}', 3, 3);

-- ============================================================
-- Section 4: 主题10 汉字识读（WORD类型，带图片）
-- ============================================================

-- Lesson 68: 山水日月 (WORD, 4项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(68, 22, '自然汉字：山水日月', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"山","phonetic":"shān","translation":"山","emoji":"⛰️","image":"chinese/hanzi-shan"},'
|| '{"word":"水","phonetic":"shuǐ","translation":"水","emoji":"💧","image":"chinese/hanzi-shui"},'
|| '{"word":"日","phonetic":"rì","translation":"太阳","emoji":"☀️","image":"chinese/hanzi-ri"},'
|| '{"word":"月","phonetic":"yuè","translation":"月亮","emoji":"🌙","image":"chinese/hanzi-yue"}'
|| ']}', 1, 3);

-- Lesson 69: 云雨风雪 (WORD, 4项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(69, 22, '自然汉字：云雨风雪', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"云","phonetic":"yún","translation":"云朵","emoji":"☁️","image":"chinese/hanzi-yun"},'
|| '{"word":"雨","phonetic":"yǔ","translation":"下雨","emoji":"🌧️","image":"chinese/hanzi-yu"},'
|| '{"word":"风","phonetic":"fēng","translation":"刮风","emoji":"💨","image":"chinese/hanzi-feng"},'
|| '{"word":"雪","phonetic":"xuě","translation":"下雪","emoji":"❄️","image":"chinese/hanzi-xue"}'
|| ']}', 2, 3);

-- Lesson 70: 花草树木 (WORD, 4项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(70, 22, '自然汉字：花草树木', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"花","phonetic":"huā","translation":"花朵","emoji":"🌸","image":"chinese/hanzi-hua"},'
|| '{"word":"草","phonetic":"cǎo","translation":"小草","emoji":"🌿","image":"chinese/hanzi-cao"},'
|| '{"word":"树","phonetic":"shù","translation":"大树","emoji":"🌳","image":"chinese/hanzi-shu"},'
|| '{"word":"木","phonetic":"mù","translation":"木头","emoji":"🪵","image":"chinese/hanzi-mu"}'
|| ']}', 3, 3);

-- Lesson 71: 上下左右 (WORD, 4项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(71, 23, '方位汉字：上下左右', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"上","phonetic":"shàng","translation":"上面","emoji":"⬆️","image":"chinese/hanzi-shang"},'
|| '{"word":"下","phonetic":"xià","translation":"下面","emoji":"⬇️","image":"chinese/hanzi-xia"},'
|| '{"word":"左","phonetic":"zuǒ","translation":"左边","emoji":"⬅️","image":"chinese/hanzi-zuo"},'
|| '{"word":"右","phonetic":"yòu","translation":"右边","emoji":"➡️","image":"chinese/hanzi-you"}'
|| ']}', 1, 3);

-- Lesson 72: 大小多少 (WORD, 4项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(72, 23, '比较汉字：大小多少', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"大","phonetic":"dà","translation":"大小的大","emoji":"🐘","image":"chinese/hanzi-da"},'
|| '{"word":"小","phonetic":"xiǎo","translation":"大小的小","emoji":"🐜","image":"chinese/hanzi-xiao"},'
|| '{"word":"多","phonetic":"duō","translation":"多少的多","emoji":"🌈","image":"chinese/hanzi-duo"},'
|| '{"word":"少","phonetic":"shǎo","translation":"多少的少","emoji":"🌱","image":"chinese/hanzi-shao"}'
|| ']}', 2, 3);

-- Lesson 73: 家庭汉字 (WORD, 4项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(73, 23, '家庭汉字：爸妈哥弟', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"爸","phonetic":"bà","translation":"爸爸","emoji":"👨","image":"chinese/hanzi-ba"},'
|| '{"word":"妈","phonetic":"mā","translation":"妈妈","emoji":"👩","image":"chinese/hanzi-ma"},'
|| '{"word":"哥","phonetic":"gē","translation":"哥哥","emoji":"👦","image":"chinese/hanzi-ge"},'
|| '{"word":"弟","phonetic":"dì","translation":"弟弟","emoji":"👶","image":"chinese/hanzi-di"}'
|| ']}', 3, 3);

-- ============================================================
-- Section 5: 主题11 古诗朗诵（SENTENCE类型，无图片）
-- ============================================================

-- Lesson 74: 静夜思 (SENTENCE, 4句)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(74, 24, '静夜思', 'SENTENCE',
'{"type":"SENTENCE","items":['
|| '{"sentence":"床前明月光","translation":"明亮的月光照在床前","emoji":"🌙"},'
|| '{"sentence":"疑是地上霜","translation":"好像是地上结了一层白霜","emoji":"❄️"},'
|| '{"sentence":"举头望明月","translation":"抬起头望着天上的明月","emoji":"🌕"},'
|| '{"sentence":"低头思故乡","translation":"低下头思念远方的家乡","emoji":"🏡"}'
|| ']}', 1, 3);

-- Lesson 75: 春晓 (SENTENCE, 4句)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(75, 24, '春晓', 'SENTENCE',
'{"type":"SENTENCE","items":['
|| '{"sentence":"春眠不觉晓","translation":"春天的夜晚睡得香甜不知不觉就到了天亮","emoji":"🌅"},'
|| '{"sentence":"处处闻啼鸟","translation":"到处都能听到鸟儿清脆的叫声","emoji":"🐦"},'
|| '{"sentence":"夜来风雨声","translation":"回想昨夜的风声和雨声","emoji":"🌧️"},'
|| '{"sentence":"花落知多少","translation":"不知有多少花瓣被风雨打落","emoji":"🌸"}'
|| ']}', 2, 3);

-- Lesson 76: 咏鹅 (SENTENCE, 4句)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(76, 24, '咏鹅', 'SENTENCE',
'{"type":"SENTENCE","items":['
|| '{"sentence":"鹅鹅鹅","translation":"大白鹅啊大白鹅","emoji":"🦢"},'
|| '{"sentence":"曲项向天歌","translation":"弯曲着脖子对着天空唱歌","emoji":"🎵"},'
|| '{"sentence":"白毛浮绿水","translation":"白色的羽毛漂浮在碧绿的水面上","emoji":"💧"},'
|| '{"sentence":"红掌拨清波","translation":"红色的脚掌拨动着清澈的水波","emoji":"🦶"}'
|| ']}', 3, 3);

-- Lesson 77: 悯农 (SENTENCE, 4句)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(77, 25, '悯农', 'SENTENCE',
'{"type":"SENTENCE","items":['
|| '{"sentence":"锄禾日当午","translation":"农民在正午烈日下给禾苗除草","emoji":"☀️"},'
|| '{"sentence":"汗滴禾下土","translation":"汗水滴落在禾苗下的泥土里","emoji":"💧"},'
|| '{"sentence":"谁知盘中餐","translation":"谁知道盘中的饭菜","emoji":"🍚"},'
|| '{"sentence":"粒粒皆辛苦","translation":"每一粒都是农民辛苦劳动得来的","emoji":"🌾"}'
|| ']}', 1, 3);

-- Lesson 78: 登鹳雀楼 (SENTENCE, 4句)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(78, 25, '登鹳雀楼', 'SENTENCE',
'{"type":"SENTENCE","items":['
|| '{"sentence":"白日依山尽","translation":"白色的太阳依傍着山峦渐渐落下","emoji":"🌇"},'
|| '{"sentence":"黄河入海流","translation":"黄河水奔腾着流入大海","emoji":"🌊"},'
|| '{"sentence":"欲穷千里目","translation":"想要看到更远的千里风光","emoji":"🔭"},'
|| '{"sentence":"更上一层楼","translation":"就要再登上更高的一层楼","emoji":"🏛️"}'
|| ']}', 2, 3);

-- Lesson 79: 望庐山瀑布 (SENTENCE, 4句)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(79, 25, '望庐山瀑布', 'SENTENCE',
'{"type":"SENTENCE","items":['
|| '{"sentence":"日照香炉生紫烟","translation":"阳光照射在香炉峰上生出紫色的烟雾","emoji":"🏔️"},'
|| '{"sentence":"遥看瀑布挂前川","translation":"远远望去瀑布像白练挂在山前","emoji":"💦"},'
|| '{"sentence":"飞流直下三千尺","translation":"飞腾的水流直冲而下有三千尺长","emoji":"💧"},'
|| '{"sentence":"疑是银河落九天","translation":"让人怀疑是银河从九天之上落下来","emoji":"✨"}'
|| ']}', 3, 3);

-- ============================================================
-- Section 6: 主题12 寓言故事（READING类型，带图片）
-- ============================================================

-- Lesson 80: 龟兔赛跑 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(80, 26, '龟兔赛跑', 'READING',
'{"type":"READING","items":['
|| '{"title":"比赛开始","content":"有一天，兔子嘲笑乌龟跑得慢。乌龟说：我们来比赛吧！兔子哈哈大笑，答应了。动物们都来观看。","image":"story/tortoise-hare-1"},'
|| '{"title":"兔子睡觉","content":"比赛开始了，兔子跑得飞快，很快就把乌龟远远甩在后面。兔子想：乌龟那么慢，我先睡一觉吧。于是它靠在大树下睡着了。","image":"story/tortoise-hare-2"},'
|| '{"title":"乌龟赢了","content":"乌龟虽然慢，但它一步一步不停地爬。等兔子醒来时，乌龟已经快到终点了！兔子拼命追，但已经太晚了。乌龟赢了比赛。","image":"story/tortoise-hare-3"}'
|| ']}', 1, 3);

-- Lesson 81: 拔苗助长 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(81, 26, '拔苗助长', 'READING',
'{"type":"READING","items":['
|| '{"title":"焦急的农夫","content":"有个农夫种了一片禾苗。他每天都去看，觉得禾苗长得太慢了。他焦急地想：怎样才能让禾苗长得快一些呢？","image":"story/pull-seedlings-1"},'
|| '{"title":"拔高禾苗","content":"农夫想出了一个好办法！他跑到田里，把每棵禾苗都往上拔了一点。拔了一整天，累得腰酸背痛，但心里很高兴。","image":"story/pull-seedlings-2"},'
|| '{"title":"禾苗枯了","content":"农夫回到家高兴地说：今天太累了，但禾苗都长高了！儿子跑去田里一看，禾苗全都枯死了。原来拔苗反而害了禾苗。","image":"story/pull-seedlings-3"}'
|| ']}', 2, 3);

-- Lesson 82: 井底之蛙 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(82, 26, '井底之蛙', 'READING',
'{"type":"READING","items":['
|| '{"title":"井里的青蛙","content":"有一只青蛙一直住在一口枯井里。它每天在井里跳来跳去，觉得这就是整个世界。它对自己的生活很满意。","image":"story/frog-well-1"},'
|| '{"title":"海龟来了","content":"一天，一只海龟路过井边。青蛙骄傲地说：我的家多好呀！海龟，你见过这么好的地方吗？海龟笑了笑。","image":"story/frog-well-2"},'
|| '{"title":"大海的故事","content":"海龟告诉青蛙：大海又大又深，比你的井大多了！青蛙听了非常惊讶，才知道自己看到的天空只是井口那么大的一小块。","image":"story/frog-well-3"}'
|| ']}', 3, 3);

-- Lesson 83: 小马过河 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(83, 27, '小马过河', 'READING',
'{"type":"READING","items":['
|| '{"title":"小马的任务","content":"马妈妈让小马把一袋麦子送到磨坊去。小马高兴地出发了。走着走着，一条小河挡住了去路。河水哗哗地流着。","image":"story/little-horse-1"},'
|| '{"title":"问别人","content":"小马问老牛：牛伯伯，水深吗？老牛说：很浅，刚到小腿。小马刚要过河，松鼠喊：别过！水很深，会淹死的！小马不知道该听谁的。","image":"story/little-horse-2"},'
|| '{"title":"自己试试","content":"小马回家问妈妈。妈妈说：光听别人说不行，要自己动脑筋。小马回到河边，小心地下了水。原来河水既不像老牛说的那么浅，也不像松鼠说的那么深。","image":"story/little-horse-3"}'
|| ']}', 1, 3);

-- Lesson 84: 狐假虎威 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(84, 27, '狐假虎威', 'READING',
'{"type":"READING","items":['
|| '{"title":"老虎抓狐狸","content":"一只老虎抓住了一只狐狸。狐狸眼珠一转，说：你不能吃我！我是天帝派来的百兽之王，你不信就跟在我后面走一趟。","image":"story/fox-tiger-1"},'
|| '{"title":"百兽逃跑","content":"老虎半信半疑，跟着狐狸往前走。森林里的小动物们看到狐狸大摇大摆地走来，身后还跟着老虎，吓得全都逃跑了。","image":"story/fox-tiger-2"},'
|| '{"title":"老虎上当","content":"老虎以为动物们真的怕狐狸，就放了它。其实动物们怕的是狐狸身后的老虎呀！狐狸借着老虎的威风吓跑了大家。","image":"story/fox-tiger-3"}'
|| ']}', 2, 3);

-- Lesson 85: 守株待兔 (READING, 3项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(85, 27, '守株待兔', 'READING',
'{"type":"READING","items":['
|| '{"title":"意外的收获","content":"一个农夫在田里干活。突然，一只兔子慌慌张张地跑过来，撞在树桩上死了。农夫白捡了一只兔子，高兴极了。","image":"story/wait-rabbit-1"},'
|| '{"title":"放下农具","content":"农夫想：种田多辛苦呀，不如在这里等兔子撞树桩。于是他放下锄头，天天守在树桩旁边，等着兔子再来。","image":"story/wait-rabbit-2"},'
|| '{"title":"田地荒了","content":"日子一天天过去，农夫再也没有等到兔子。而他的田地因为没有人打理，长满了野草，庄稼全荒了。农夫后悔极了。","image":"story/wait-rabbit-3"}'
|| ']}', 3, 3);
```

- [ ] **Step 2: 启动后端验证迁移**

```bash
cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && ./run.sh
```

- [ ] **Step 3: 验证数据完整性**

```bash
# 验证语文学科的主题
curl http://localhost:8080/api/v1/themes/subject/2 | python3 -m json.tool

# 验证WORD类型课时（汉字识读，带图片）
curl http://localhost:8080/api/v1/lessons/68 | python3 -m json.tool

# 验证SENTENCE类型课时（古诗朗诵）
curl http://localhost:8080/api/v1/lessons/74 | python3 -m json.tool

# 验证READING类型课时（寓言故事，带图片）
curl http://localhost:8080/api/v1/lessons/80 | python3 -m json.tool
```

确认：
- 4个语文主题（id 9-12）正确返回
- WORD 课时 image 字段解析为 `http://localhost:8080/images/chinese/hanzi-xxx.jpg`
- READING 课时 image 字段解析为 `http://localhost:8080/images/story/xxx.jpg`
- SENTENCE 课时无 image 字段，正常显示 emoji

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/resources/db/migration/V15__seed_chinese_content.sql
git commit -m "feat(db): V15迁移-语文4主题24课时内容播种

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

### Task 4.2：AI 生成语文配图

**目标：** 为语文模块生成约40张配图。汉字识读存放于 `static/images/chinese/`，寓言故事存放于 `static/images/story/`。

**配图清单：**

汉字配图（24张，存放 `static/images/chinese/`）：

| 图片 key | 汉字 | 对应课时 |
|---------|------|---------|
| chinese/hanzi-shan | 山 | L68 |
| chinese/hanzi-shui | 水 | L68 |
| chinese/hanzi-ri | 日 | L68 |
| chinese/hanzi-yue | 月 | L68 |
| chinese/hanzi-yun | 云 | L69 |
| chinese/hanzi-yu | 雨 | L69 |
| chinese/hanzi-feng | 风 | L69 |
| chinese/hanzi-xue | 雪 | L69 |
| chinese/hanzi-hua | 花 | L70 |
| chinese/hanzi-cao | 草 | L70 |
| chinese/hanzi-shu | 树 | L70 |
| chinese/hanzi-mu | 木 | L70 |
| chinese/hanzi-shang | 上 | L71 |
| chinese/hanzi-xia | 下 | L71 |
| chinese/hanzi-zuo | 左 | L71 |
| chinese/hanzi-you | 右 | L71 |
| chinese/hanzi-da | 大 | L72 |
| chinese/hanzi-xiao | 小 | L72 |
| chinese/hanzi-duo | 多 | L72 |
| chinese/hanzi-shao | 少 | L72 |
| chinese/hanzi-ba | 爸 | L73 |
| chinese/hanzi-ma | 妈 | L73 |
| chinese/hanzi-ge | 哥 | L73 |
| chinese/hanzi-di | 弟 | L73 |

寓言故事配图（18张，存放 `static/images/story/`）：

| 图片 key | 场景 | 对应课时 |
|---------|------|---------|
| story/tortoise-hare-1 | 龟兔赛跑-比赛开始 | L80 |
| story/tortoise-hare-2 | 龟兔赛跑-兔子睡觉 | L80 |
| story/tortoise-hare-3 | 龟兔赛跑-乌龟赢了 | L80 |
| story/pull-seedlings-1 | 拔苗助长-焦急农夫 | L81 |
| story/pull-seedlings-2 | 拔苗助长-拔高禾苗 | L81 |
| story/pull-seedlings-3 | 拔苗助长-禾苗枯了 | L81 |
| story/frog-well-1 | 井底之蛙-井里青蛙 | L82 |
| story/frog-well-2 | 井底之蛙-海龟来了 | L82 |
| story/frog-well-3 | 井底之蛙-大海故事 | L82 |
| story/little-horse-1 | 小马过河-小马任务 | L83 |
| story/little-horse-2 | 小马过河-问别人 | L83 |
| story/little-horse-3 | 小马过河-自己试试 | L83 |
| story/fox-tiger-1 | 狐假虎威-老虎抓狐狸 | L84 |
| story/fox-tiger-2 | 狐假虎威-百兽逃跑 | L84 |
| story/fox-tiger-3 | 狐假虎威-老虎上当 | L84 |
| story/wait-rabbit-1 | 守株待兔-意外收获 | L85 |
| story/wait-rabbit-2 | 守株待兔-放下农具 | L85 |
| story/wait-rabbit-3 | 守株待兔-田地荒了 | L85 |

- [ ] **Step 1: 生成汉字配图（24张）**

prompt 模板：
```
[PURPOSE]: 儿童教育APP汉字识读配图
[DESCRIPTION]: 扁平2.5D风格的{汉字对应物体场景描述}，圆润可爱，暖色调，浅黄色纯色背景，无文字标注，儿童友好插画风格，1024x1024正方形
```

示例（山）：
```
[PURPOSE]: 儿童教育APP汉字识读配图
[DESCRIPTION]: 扁平2.5D风格的连绵青山，绿色圆润山峦，白云环绕，圆润可爱，暖色调，浅黄色纯色背景，无文字标注，儿童友好插画风格，1024x1024正方形
```

- [ ] **Step 2: 生成寓言故事配图（18张）**

prompt 模板：
```
[PURPOSE]: 儿童教育APP寓言故事场景配图
[DESCRIPTION]: 扁平2.5D风格的{故事场景描述}，动物角色圆润可爱，暖色调，浅黄色纯色背景，无文字标注，儿童友好插画风格，1024x1024正方形
```

- [ ] **Step 3: 验证图片可访问**

```bash
curl -I http://localhost:8080/images/chinese/hanzi-shan.jpg
curl -I http://localhost:8080/images/story/tortoise-hare-1.jpg
```

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/resources/static/images/chinese/ backend/src/main/resources/static/images/story/
git commit -m "feat(images): 语文模块42张AI配图(汉字24+故事18)

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

### Task 4.3：阶段4集成验证

- [ ] **Step 1: 启动后端和前端**

```bash
cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && ./run.sh
# 终端2
cd frontend && npm run dev
```

- [ ] **Step 2: 验证语文学科入口**

打开 `http://localhost:5173/`，点击"语文"学科卡片，确认显示4个语文主题。

- [ ] **Step 3: 验证 WORD 类型课时（拼音/汉字）**

打开 `http://localhost:5173/lesson/62`（单韵母 a o e，WORD），验证：
- 拼音显示在卡片中央
- 音标和中文释义正确显示
- 听发音（TTS 朗读中文拼音）
- 跟读评分功能正常
- 无图片的拼音课用 emoji 替代显示

打开 `http://localhost:5173/lesson/68`（山水日月，WORD 带图），验证图片正确显示。

- [ ] **Step 4: 验证 SENTENCE 类型课时（古诗）**

打开 `http://localhost:5173/lesson/74`（静夜思，SENTENCE），验证：
- 古诗原文显示
- 白话译文正确
- 朗读功能正常
- 跟读朗诵评分功能正常

- [ ] **Step 5: 验证 READING 类型课时（寓言故事）**

打开 `http://localhost:5173/lesson/80`（龟兔赛跑，READING），验证：
- 故事标题、正文、配图正确显示
- 朗读功能正常
- 翻页阅读正常
- 读完后进入结算页

- [ ] **Step 6: 提交**

```bash
git add -A
git commit -m "feat(phase4): 语文模块完成 - V15迁移+24课时+42张配图

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## 阶段5：数学模块（Calculate模板 + V16迁移 + AI配图）

**目标：** 创建 CalculateLesson 前端模板组件（数字输入答题），通过 Flyway V16 播种数学学科内容（4主题8单元24课时），复用阶段3的 QuizLesson 模板，AI生成约25张数学配图。

**内容结构：**

| 主题 | 类型 | 课时数 | 前端模板 | 配图需求 |
|------|------|--------|---------|---------|
| 数字认知 | WORD | 6 | WordLesson | ~24张 |
| 加减运算 | CALCULATE | 6 | CalculateLesson(新增) | 无 |
| 图形认知 | QUIZ | 6 | QuizLesson | ~8张 |
| 趣味应用题 | QUIZ | 6 | QuizLesson | ~5张 |

**数据结构设计：**

CALCULATE 类型 content JSON：
```json
{
  "type": "CALCULATE",
  "items": [
    {"question": "3 + 2 = ?", "answer": "5", "image": ""}
  ]
}
```

注意：CALCULATE 的 answer 是字符串类型（支持多位数），与 QUIZ 的 answer（数字索引）不同。

### Task 5.1：创建 CalculateLesson 模板组件

**Files:**
- Create: `frontend/src/components/lesson-templates/CalculateLesson.vue`

**设计思路：** CALCULATE 类型为数字输入题，用户通过大按钮数字键盘输入答案，提交后即时判对错。答对率作为评分依据。组件通过 `emit('answered', isCorrect)` 上报结果，与 QuizLesson 复用相同的答题追踪机制。

- [ ] **Step 1: 创建 CalculateLesson.vue**

```vue
<!--
  CalculateLesson.vue - CALCULATE 类型课时模板
  用途: 计算题学习，展示题目，用户通过数字键盘输入答案，即时判对错。
        适用于数学加减运算。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description CALCULATE 类型课时学习模板，数字输入答题+即时反馈。
 */
import { ref, computed, watch } from 'vue'
import AppButton from '../AppButton.vue'
import mascotCompanion from '../../assets/mascot/mascot-companion.jpg'

/**
 * 组件 Props
 * @property {Object} currentItem 当前题目 { question, answer, image }
 * @property {number} currentIndex 当前题号
 * @property {number} totalItems 总题数
 * @property {boolean} isLastItem 是否最后一题
 */
const props = defineProps({
  /** 当前题目对象 */
  currentItem: { type: Object, default: null },
  /** 当前题号（从0开始） */
  currentIndex: { type: Number, default: 0 },
  /** 总题数 */
  totalItems: { type: Number, default: 0 },
  /** 是否最后一题 */
  isLastItem: { type: Boolean, default: false }
})

/**
 * 组件 Emits
 * @event answered 答题完成，上报是否正确
 * @event next 切换下一题/完成
 */
const emit = defineEmits({
  /** 答题完成，参数为是否答对 */
  answered: null,
  /** 切换下一题或完成 */
  next: null
})

// 用户输入的答案
const userInput = ref('')
// 是否已提交（锁定输入）
const hasAnswered = ref(false)

/**
 * 当前题是否答对（字符串比较）。
 */
const isCorrect = computed(() => {
  if (!hasAnswered.value) return false
  return userInput.value === String(props.currentItem?.answer)
})

/**
 * 切换题目时重置状态。
 */
watch(() => props.currentIndex, () => {
  userInput.value = ''
  hasAnswered.value = false
})

/**
 * 进度百分比。
 */
const progressPercent = computed(() => {
  if (props.totalItems === 0) return 0
  return Math.round(((props.currentIndex + 1) / props.totalItems) * 100)
})

/**
 * 数字键盘按钮（0-9 + 删除 + 清空）。
 */
const keypadButtons = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '0']

/**
 * 点击数字键盘按钮，追加数字到输入。
 * @param {string} num 数字字符
 */
function pressKey(num) {
  if (hasAnswered.value) return
  // 限制最大输入长度（防止溢出）
  if (userInput.value.length >= 4) return
  userInput.value += num
}

/**
 * 删除最后一个字符。
 */
function deleteLast() {
  if (hasAnswered.value) return
  userInput.value = userInput.value.slice(0, -1)
}

/**
 * 清空输入。
 */
function clearInput() {
  if (hasAnswered.value) return
  userInput.value = ''
}

/**
 * 提交答案：判定对错并上报。
 */
function submitAnswer() {
  if (hasAnswered.value || userInput.value === '') return
  hasAnswered.value = true
  const correct = userInput.value === String(props.currentItem?.answer)
  emit('answered', correct)
}
</script>

<template>
  <div class="card-area">
    <!-- 顶部进度条 -->
    <div class="progress-section">
      <div class="progress-info">
        <span>第 {{ currentIndex + 1 }} / {{ totalItems }} 题</span>
      </div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
      </div>
    </div>

    <!-- 小老鼠陪伴插画 -->
    <img :src="mascotCompanion" alt="小老鼠陪伴" class="mascot-companion" />

    <!-- 题目卡片 -->
    <div class="calc-card">
      <div v-if="currentItem.image" class="calc-image-wrapper">
        <img :src="currentItem.image" :alt="currentItem.question" class="calc-image" />
      </div>
      <h1 class="calc-question">{{ currentItem.question }}</h1>

      <!-- 答案显示区 -->
      <div class="answer-display" :class="{
        'answer-correct': hasAnswered && isCorrect,
        'answer-wrong': hasAnswered && !isCorrect
      }">
        <span class="answer-text">{{ userInput || '?' }}</span>
        <span v-if="hasAnswered && !isCorrect" class="correct-answer">
          正确答案：{{ currentItem.answer }}
        </span>
      </div>

      <!-- 数字键盘 -->
      <div v-if="!hasAnswered" class="keypad">
        <button
          v-for="num in keypadButtons.slice(0, 9)"
          :key="num"
          class="key-btn"
          @click="pressKey(num)"
        >{{ num }}</button>
        <button class="key-btn key-action" @click="clearInput">C</button>
        <button class="key-btn" @click="pressKey('0')">0</button>
        <button class="key-btn key-action" @click="deleteLast">⌫</button>
      </div>

      <!-- 答题反馈 -->
      <div v-if="hasAnswered" class="feedback-area">
        <p :class="['feedback-text', isCorrect ? 'feedback-correct' : 'feedback-wrong']">
          {{ isCorrect ? '回答正确！太棒了！' : '答错了，再接再厉！' }}
        </p>
      </div>
    </div>

    <!-- 提交/下一题按钮 -->
    <AppButton
      v-if="!hasAnswered"
      variant="primary"
      size="lg"
      block
      :disabled="userInput === ''"
      @click="submitAnswer"
    >提交答案</AppButton>

    <AppButton
      v-if="hasAnswered"
      variant="primary"
      size="lg"
      block
      @click="emit('next')"
    >{{ isLastItem ? '完成答题' : '下一题 →' }}</AppButton>
  </div>
</template>

<style scoped>
.card-area { max-width: 480px; margin: 0 auto; position: relative; }

.progress-section { margin-bottom: var(--space-5); }
.progress-info { text-align: center; color: var(--text-tertiary); font-size: var(--text-sm); margin-bottom: var(--space-2); }
.progress-bar { height: 8px; background: var(--bg-card); border-radius: var(--radius-pill); overflow: hidden; box-shadow: var(--shadow-soft); }
.progress-fill { height: 100%; background: var(--gradient-primary); border-radius: var(--radius-pill); transition: width var(--duration-slow) var(--ease-smooth); }

.mascot-companion { position: absolute; top: 0; right: 0; width: 80px; height: 80px; object-fit: contain; border-radius: var(--radius-md); z-index: 1; opacity: 0.9; }

.calc-card { background: var(--bg-card); border-radius: var(--radius-lg); padding: var(--space-6); margin-bottom: var(--space-6); box-shadow: var(--shadow-card); }

.calc-image-wrapper { margin-bottom: var(--space-4); text-align: center; }
.calc-image { width: 100%; max-width: 240px; height: 180px; object-fit: cover; border-radius: var(--radius-md); box-shadow: var(--shadow-soft); }

.calc-question { font-size: var(--text-xl); color: var(--text-primary); font-weight: var(--font-bold); margin-bottom: var(--space-5); text-align: center; }

.answer-display { background: var(--bg-muted); border-radius: var(--radius-md); padding: var(--space-4); text-align: center; margin-bottom: var(--space-4); min-height: 60px; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-1); transition: all var(--duration-fast) var(--ease-smooth); }

.answer-correct { background: rgba(76, 217, 100, 0.15); }
.answer-wrong { background: rgba(255, 107, 107, 0.15); }

.answer-text { font-size: var(--text-xl); font-weight: var(--font-bold); color: var(--text-primary); }
.answer-correct .answer-text { color: var(--color-success); }
.answer-wrong .answer-text { color: var(--color-warning); }

.correct-answer { font-size: var(--text-sm); color: var(--color-success); }

.keypad { display: grid; grid-template-columns: repeat(3, 1fr); gap: var(--space-2); margin-bottom: var(--space-4); }

.key-btn { height: 56px; border: 2px solid var(--border-light); border-radius: var(--radius-md); background: var(--bg-card); font-size: var(--text-lg); font-weight: var(--font-bold); color: var(--text-primary); cursor: pointer; transition: all var(--duration-fast) var(--ease-smooth); }
.key-btn:hover { border-color: var(--color-primary); background: var(--bg-muted); }
.key-btn:active { transform: scale(0.95); }
.key-action { color: var(--color-warning); }

.feedback-area { margin-top: var(--space-4); padding: var(--space-3); border-radius: var(--radius-md); text-align: center; }
.feedback-correct { color: var(--color-success); font-weight: var(--font-bold); }
.feedback-wrong { color: var(--color-warning); font-weight: var(--font-bold); }

@media (prefers-reduced-motion: no-preference) {
  .answer-correct { animation: correctPulse var(--duration-normal) var(--ease-bounce); }
  .answer-wrong { animation: wrongShake var(--duration-fast) var(--ease-smooth); }
}

@keyframes correctPulse { 0%, 100% { transform: scale(1); } 50% { transform: scale(1.02); } }
@keyframes wrongShake { 0%, 100% { transform: translateX(0); } 25% { transform: translateX(-4px); } 75% { transform: translateX(4px); } }

@media (max-width: 480px) {
  .mascot-companion { width: 64px; height: 64px; }
  .key-btn { height: 48px; }
}
</style>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/components/lesson-templates/CalculateLesson.vue
git commit -m "feat(lesson): 新增CalculateLesson模板(数字输入计算题)

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

### Task 5.2：在 LessonView 分发器中注册 CalculateLesson

**Files:**
- Modify: `frontend/src/views/LessonView.vue`

- [ ] **Step 1: 导入 CalculateLesson 并扩展分发逻辑**

```javascript
import CalculateLesson from '../components/lesson-templates/CalculateLesson.vue'
```

在 `lessonTemplate` 计算属性的 switch 中追加：

```javascript
case 'CALCULATE':
  return CalculateLesson
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/views/LessonView.vue
git commit -m "feat(lesson): LessonView注册CalculateLesson模板

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

### Task 5.3：Flyway V16 迁移 - 数学内容

**Files:**
- Create: `backend/src/main/resources/db/migration/V16__seed_math_content.sql`

**内容结构：** 4主题(theme id 13-16) × 2单元(unit id 28-35) × 3课时(lesson id 86-109) = 24课时。所有 theme 的 `subject_id=3`（数学）。

- [ ] **Step 1: 创建 V16 迁移文件**

```sql
-- ============================================================
-- V16: 数学模块内容播种
-- 4个主题(13-16) × 2单元(28-35) × 3课时(86-109) = 24课时
-- 课时类型: WORD 6个(数字认知) + CALCULATE 6个(加减运算) + QUIZ 12个(图形+应用题)
-- 所有 theme 的 subject_id=3(数学)
-- ============================================================

-- Section 1: 新增主题（subject_id=3 数学）
INSERT INTO theme (id, name, sort_order, is_locked, subject_id) VALUES
(13, '数字认知', 1, 0, 3),
(14, '加减运算', 2, 1, 3),
(15, '图形认知', 3, 1, 3),
(16, '趣味应用题', 4, 1, 3);

-- Section 2: 新增单元
INSERT INTO unit (id, theme_id, name, sort_order, is_locked) VALUES
(28, 13, '认识1-5', 1, 0),
(29, 13, '认识6-10', 2, 1),
(30, 14, '10以内加法', 1, 0),
(31, 14, '10以内减法', 2, 1),
(32, 15, '平面图形', 1, 0),
(33, 15, '图形拼搭', 2, 1),
(34, 16, '生活应用', 1, 0),
(35, 16, '趣味问题', 2, 1);

-- ============================================================
-- Section 3: 主题13 数字认知（WORD类型，带图片）
-- ============================================================

-- Lesson 86: 数字1-5 (WORD, 5项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(86, 28, '认识数字1-5', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"1","phonetic":"yī","translation":"一","emoji":"1️⃣","image":"math/number-1"},'
|| '{"word":"2","phonetic":"èr","translation":"二","emoji":"2️⃣","image":"math/number-2"},'
|| '{"word":"3","phonetic":"sān","translation":"三","emoji":"3️⃣","image":"math/number-3"},'
|| '{"word":"4","phonetic":"sì","translation":"四","emoji":"4️⃣","image":"math/number-4"},'
|| '{"word":"5","phonetic":"wǔ","translation":"五","emoji":"5️⃣","image":"math/number-5"}'
|| ']}', 1, 3);

-- Lesson 87: 数量1-5 (WORD, 5项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(87, 28, '数量对应1-5', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"1个苹果","phonetic":"yī","translation":"一个苹果","emoji":"🍎","image":"math/apple-1"},'
|| '{"word":"2只小鸭","phonetic":"èr","translation":"两只小鸭","emoji":"🦆","image":"math/duck-2"},'
|| '{"word":"3朵花","phonetic":"sān","translation":"三朵花","emoji":"🌸","image":"math/flower-3"},'
|| '{"word":"4颗星","phonetic":"sì","translation":"四颗星","emoji":"⭐","image":"math/star-4"},'
|| '{"word":"5个气球","phonetic":"wǔ","translation":"五个气球","emoji":"🎈","image":"math/balloon-5"}'
|| ']}', 2, 3);

-- Lesson 88: 数字比大小 (WORD, 4项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(88, 28, '数字比大小', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"1<2","phonetic":"","translation":"1小于2","emoji":"1️⃣2️⃣"},'
|| '{"word":"3>1","phonetic":"","translation":"3大于1","emoji":"3️⃣1️⃣"},'
|| '{"word":"4>3","phonetic":"","translation":"4大于3","emoji":"4️⃣3️⃣"},'
|| '{"word":"5>2","phonetic":"","translation":"5大于2","emoji":"5️⃣2️⃣"}'
|| ']}', 3, 3);

-- Lesson 89: 数字6-10 (WORD, 5项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(89, 29, '认识数字6-10', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"6","phonetic":"liù","translation":"六","emoji":"6️⃣","image":"math/number-6"},'
|| '{"word":"7","phonetic":"qī","translation":"七","emoji":"7️⃣","image":"math/number-7"},'
|| '{"word":"8","phonetic":"bā","translation":"八","emoji":"8️⃣","image":"math/number-8"},'
|| '{"word":"9","phonetic":"jiǔ","translation":"九","emoji":"9️⃣","image":"math/number-9"},'
|| '{"word":"10","phonetic":"shí","translation":"十","emoji":"🔟","image":"math/number-10"}'
|| ']}', 1, 3);

-- Lesson 90: 数量6-10 (WORD, 5项, 带图片)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(90, 29, '数量对应6-10', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"6个草莓","phonetic":"liù","translation":"六个草莓","emoji":"🍓","image":"math/strawberry-6"},'
|| '{"word":"7个鸡蛋","phonetic":"qī","translation":"七个鸡蛋","emoji":"🥚","image":"math/egg-7"},'
|| '{"word":"8块糖果","phonetic":"bā","translation":"八块糖果","emoji":"🍬","image":"math/candy-8"},'
|| '{"word":"9支铅笔","phonetic":"jiǔ","translation":"九支铅笔","emoji":"✏️","image":"math/pencil-9"},'
|| '{"word":"10个手指","phonetic":"shí","translation":"十个手指","emoji":"🤚","image":"math/finger-10"}'
|| ']}', 2, 3);

-- Lesson 91: 序数词 (WORD, 5项)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(91, 29, '第一到第五', 'WORD',
'{"type":"WORD","items":['
|| '{"word":"第一","phonetic":"dì yī","translation":"第1名","emoji":"🥇"},'
|| '{"word":"第二","phonetic":"dì èr","translation":"第2名","emoji":"🥈"},'
|| '{"word":"第三","phonetic":"dì sān","translation":"第3名","emoji":"🥉"},'
|| '{"word":"第四","phonetic":"dì sì","translation":"第4名","emoji":"4️⃣"},'
|| '{"word":"第五","phonetic":"dì wǔ","translation":"第5名","emoji":"5️⃣"}'
|| ']}', 3, 3);

-- ============================================================
-- Section 4: 主题14 加减运算（CALCULATE类型，无图片）
-- ============================================================

-- Lesson 92: 5以内加法 (CALCULATE, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(92, 30, '5以内加法', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"1 + 2 = ?","answer":"3","image":""},'
|| '{"question":"2 + 3 = ?","answer":"5","image":""},'
|| '{"question":"1 + 4 = ?","answer":"5","image":""},'
|| '{"question":"3 + 2 = ?","answer":"5","image":""},'
|| '{"question":"2 + 2 = ?","answer":"4","image":""}'
|| ']}', 1, 3);

-- Lesson 93: 10以内加法 (CALCULATE, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(93, 30, '10以内加法', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"5 + 3 = ?","answer":"8","image":""},'
|| '{"question":"6 + 2 = ?","answer":"8","image":""},'
|| '{"question":"4 + 5 = ?","answer":"9","image":""},'
|| '{"question":"7 + 3 = ?","answer":"10","image":""},'
|| '{"question":"6 + 4 = ?","answer":"10","image":""}'
|| ']}', 2, 3);

-- Lesson 94: 加法练习 (CALCULATE, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(94, 30, '加法综合练习', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"3 + 4 = ?","answer":"7","image":""},'
|| '{"question":"5 + 5 = ?","answer":"10","image":""},'
|| '{"question":"2 + 6 = ?","answer":"8","image":""},'
|| '{"question":"1 + 8 = ?","answer":"9","image":""},'
|| '{"question":"4 + 4 = ?","answer":"8","image":""}'
|| ']}', 3, 3);

-- Lesson 95: 5以内减法 (CALCULATE, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(95, 31, '5以内减法', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"5 - 2 = ?","answer":"3","image":""},'
|| '{"question":"4 - 1 = ?","answer":"3","image":""},'
|| '{"question":"5 - 3 = ?","answer":"2","image":""},'
|| '{"question":"3 - 2 = ?","answer":"1","image":""},'
|| '{"question":"4 - 3 = ?","answer":"1","image":""}'
|| ']}', 1, 3);

-- Lesson 96: 10以内减法 (CALCULATE, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(96, 31, '10以内减法', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"10 - 3 = ?","answer":"7","image":""},'
|| '{"question":"9 - 4 = ?","answer":"5","image":""},'
|| '{"question":"8 - 2 = ?","answer":"6","image":""},'
|| '{"question":"10 - 6 = ?","answer":"4","image":""},'
|| '{"question":"7 - 5 = ?","answer":"2","image":""}'
|| ']}', 2, 3);

-- Lesson 97: 减法练习 (CALCULATE, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(97, 31, '减法综合练习', 'CALCULATE',
'{"type":"CALCULATE","items":['
|| '{"question":"10 - 5 = ?","answer":"5","image":""},'
|| '{"question":"8 - 3 = ?","answer":"5","image":""},'
|| '{"question":"9 - 7 = ?","answer":"2","image":""},'
|| '{"question":"6 - 4 = ?","answer":"2","image":""},'
|| '{"question":"10 - 8 = ?","answer":"2","image":""}'
|| ']}', 3, 3);

-- ============================================================
-- Section 5: 主题15 图形认知（QUIZ类型，部分带图片）
-- ============================================================

-- Lesson 98: 认识圆形和方形 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(98, 32, '认识圆形和方形', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"下面哪个是圆形？","options":["方块","圆球","三角形","长条"],"answer":1,"image":"math/shape-circle"},'
|| '{"question":"下面哪个是正方形？","options":["方块","球","圆柱","圆锥"],"answer":0,"image":"math/shape-square"},'
|| '{"question":"太阳是什么形状？","options":["方形","三角形","圆形","长方形"],"answer":2,"image":""},'
|| '{"question":"盒子是什么形状？","options":["圆形","方形","三角形","椭圆形"],"answer":1,"image":""},'
|| '{"question":"车轮是什么形状？","options":["方形","三角形","圆形","星形"],"answer":2,"image":""}'
|| ']}', 1, 3);

-- Lesson 99: 认识三角形 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(99, 32, '认识三角形', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"三角形有几条边？","options":["2条","3条","4条","5条"],"answer":1,"image":""},'
|| '{"question":"三角形有几个角？","options":["2个","3个","4个","5个"],"answer":1,"image":""},'
|| '{"question":"下面哪个是三角形？","options":["方块","三角尺","圆球","长条"],"answer":1,"image":"math/shape-triangle"},'
|| '{"question":"屋顶常是什么形状？","options":["圆形","三角形","正方形","椭圆形"],"answer":1,"image":""},'
|| '{"question":"红领巾是什么形状？","options":["圆形","三角形","正方形","长方形"],"answer":1,"image":""}'
|| ']}', 2, 3);

-- Lesson 100: 图形分类 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(100, 32, '图形分类', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"正方形有几条边？","options":["3条","4条","5条","6条"],"answer":1,"image":""},'
|| '{"question":"长方形和正方形有什么不同？","options":["边数不同","角度不同","边长","颜色"],"answer":2,"image":""},'
|| '{"question":"球是什么形状？","options":["圆形","球形","方形","三角形"],"answer":1,"image":"math/shape-sphere"},'
|| '{"question":"下面哪个不是平面图形？","options":["圆形","三角形","球","正方形"],"answer":2,"image":""},'
|| '{"question":"拼图游戏中常用什么形状？","options":["圆形","三角形","方形","以上都是"],"answer":3,"image":""}'
|| ']}', 3, 3);

-- Lesson 101: 图形拼搭1 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(101, 33, '图形拼搭入门', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"两个三角形可以拼成什么？","options":["圆形","正方形","球","圆柱"],"answer":1,"image":"math/shape-combine"},'
|| '{"question":"四个小正方形可以拼成什么？","options":["大正方形","圆形","三角形","球"],"answer":0,"image":""},'
|| '{"question":"房子通常由什么图形拼成？","options":["方形+三角形","圆形+方形","三角形+圆形","两个圆形"],"answer":0,"image":""},'
|| '{"question":"鱼可以由什么图形拼成？","options":["三角形+三角形","方形+方形","圆形+圆形","都是方形"],"answer":0,"image":""},'
|| '{"question":"火箭可以由什么图形拼成？","options":["三角形+方形","圆形+圆形","方形+方形","都是三角形"],"answer":0,"image":""}'
|| ']}', 1, 3);

-- Lesson 102: 图形拼搭2 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(102, 33, '图形拼搭进阶', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"一个正方形可以分成几个三角形？","options":["1个","2个","3个","4个"],"answer":1,"image":""},'
|| '{"question":"用圆形可以画出什么？","options":["太阳","月亮","车轮","以上都是"],"answer":3,"image":""},'
|| '{"question":"拼一座桥需要什么图形？","options":["两个三角形","两个圆形","两个方形","两个星形"],"answer":0,"image":""},'
|| '{"question":"钟面是什么形状？","options":["方形","三角形","圆形","星形"],"answer":2,"image":""},'
|| '{"question":"七巧板有几块？","options":["5块","6块","7块","8块"],"answer":2,"image":"math/tangram"}'
|| ']}', 2, 3);

-- Lesson 103: 图形综合 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(103, 33, '图形综合练习', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"积木常是什么形状？","options":["圆形","方形","三角形","各种形状"],"answer":3,"image":""},'
|| '{"question":"镜子常是什么形状？","options":["圆形","方形","三角形","圆形或方形"],"answer":3,"image":""},'
|| '{"question":"下面哪个有4条边？","options":["圆形","三角形","正方形","五边形"],"answer":2,"image":""},'
|| '{"question":"下面哪个有3个角？","options":["圆形","三角形","正方形","六边形"],"answer":1,"image":""},'
|| '{"question":"鸡蛋是什么形状？","options":["正圆形","椭圆形","三角形","方形"],"answer":1,"image":""}'
|| ']}', 3, 3);

-- ============================================================
-- Section 6: 主题16 趣味应用题（QUIZ类型，部分带图片）
-- ============================================================

-- Lesson 104: 简单应用题 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(104, 34, '简单应用题', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"小明有3个苹果，妈妈又给他2个，一共有几个？","options":["4个","5个","6个","3个"],"answer":1,"image":""},'
|| '{"question":"树上有5只鸟，飞走了2只，还剩几只？","options":["2只","3只","4只","5只"],"answer":1,"image":""},'
|| '{"question":"小红有4支铅笔，用了1支，还剩几支？","options":["1支","2支","3支","4支"],"answer":2,"image":""},'
|| '{"question":"桌上有2本书，又放了3本，一共有几本？","options":["4本","5本","6本","3本"],"answer":1,"image":""},'
|| '{"question":"池塘里有6条鱼，游走了3条，还剩几条？","options":["2条","3条","4条","5条"],"answer":1,"image":""}'
|| ']}', 1, 3);

-- Lesson 105: 购物问题 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(105, 34, '购物问题', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"一个冰激凌3元，一个蛋糕5元，一共多少元？","options":["7元","8元","9元","6元"],"answer":1,"image":""},'
|| '{"question":"一个气球2元，买3个要多少元？","options":["5元","6元","7元","8元"],"answer":1,"image":""},'
|| '{"question":"有10元，买了一个4元的面包，还剩多少元？","options":["4元","5元","6元","7元"],"answer":2,"image":""},'
|| '{"question":"一个苹果1元，买5个要多少元？","options":["4元","5元","6元","7元"],"answer":1,"image":""},'
|| '{"question":"一支铅笔2元，一把尺子1元，一共多少元？","options":["2元","3元","4元","5元"],"answer":1,"image":""}'
|| ']}', 2, 3);

-- Lesson 106: 比较问题 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(106, 34, '比较问题', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"小明5岁，小红7岁，谁大？","options":["小明","小红","一样大","不知道"],"answer":1,"image":""},'
|| '{"question":"3和5哪个大？","options":["3","5","一样大","不知道"],"answer":1,"image":""},'
|| '{"question":"10和8哪个小？","options":["10","8","一样","不知道"],"answer":1,"image":""},'
|| '{"question":"小红有5颗糖，小明有3颗，谁多？","options":["小红","小明","一样多","不知道"],"answer":0,"image":""},'
|| '{"question":"第一组4人，第二组6人，哪组人多？","options":["第一组","第二组","一样多","不知道"],"answer":1,"image":""}'
|| ']}', 3, 3);

-- Lesson 107: 趣味问题1 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(107, 35, '趣味问题', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"一只手有几个手指？","options":["4个","5个","6个","10个"],"answer":1,"image":""},'
|| '{"question":"一星期有几天？","options":["5天","6天","7天","8天"],"answer":2,"image":""},'
|| '{"question":"一年有几个季节？","options":["2个","3个","4个","5个"],"answer":2,"image":""},'
|| '{"question":"一双筷子有几根？","options":["1根","2根","3根","4根"],"answer":1,"image":""},'
|| '{"question":"一只猫有几条腿？","options":["2条","3条","4条","5条"],"answer":2,"image":""}'
|| ']}', 1, 3);

-- Lesson 108: 趣味问题2 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(108, 35, '数学脑筋急转弯', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"0和1之间有几个数？","options":["0个","1个","9个","无数个"],"answer":3,"image":""},'
|| '{"question":"最大的一位数是几？","options":["8","9","10","11"],"answer":1,"image":""},'
|| '{"question":"最小的两位数是几？","options":["0","1","10","11"],"answer":2,"image":""},'
|| '{"question":"5的前一个数是几？","options":["3","4","6","7"],"answer":1,"image":""},'
|| '{"question":"10的后一个数是几？","options":["9","10","11","12"],"answer":2,"image":""}'
|| ']}', 2, 3);

-- Lesson 109: 综合应用题 (QUIZ, 5题)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(109, 35, '综合应用题', 'QUIZ',
'{"type":"QUIZ","items":['
|| '{"question":"2只兔子加上3只兔子，一共几只？","options":["4只","5只","6只","3只"],"answer":1,"image":""},'
|| '{"question":"10块糖吃了4块，还剩几块？","options":["5块","6块","7块","4块"],"answer":1,"image":""},'
|| '{"question":"3朵花加3朵花，一共几朵？","options":["5朵","6朵","7朵","4朵"],"answer":1,"image":""},'
|| '{"question":"8个气球飞了5个，还剩几个？","options":["2个","3个","4个","5个"],"answer":1,"image":""},'
|| '{"question":"4个橘子加上6个橘子，一共几个？","options":["8个","9个","10个","11个"],"answer":2,"image":""}'
|| ']}', 3, 3);
```

- [ ] **Step 2: 启动后端验证迁移**

```bash
cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && ./run.sh
```

- [ ] **Step 3: 验证数据完整性**

```bash
# 验证数学学科的主题
curl http://localhost:8080/api/v1/themes/subject/3 | python3 -m json.tool

# 验证WORD类型课时（数字认知，带图片）
curl http://localhost:8080/api/v1/lessons/86 | python3 -m json.tool

# 验证CALCULATE类型课时（加减运算）
curl http://localhost:8080/api/v1/lessons/92 | python3 -m json.tool

# 验证QUIZ类型课时（图形认知）
curl http://localhost:8080/api/v1/lessons/98 | python3 -m json.tool
```

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/resources/db/migration/V16__seed_math_content.sql
git commit -m "feat(db): V16迁移-数学4主题24课时内容播种

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

### Task 5.4：AI 生成数学配图

**目标：** 为数学模块生成约25张配图，存放于 `backend/src/main/resources/static/images/math/`。

**配图清单（共 25 张）：**

| 图片 key | 用途 | 对应课时 |
|---------|------|---------|
| math/number-1 | 数字1 | L86 |
| math/number-2 | 数字2 | L86 |
| math/number-3 | 数字3 | L86 |
| math/number-4 | 数字4 | L86 |
| math/number-5 | 数字5 | L86 |
| math/apple-1 | 1个苹果 | L87 |
| math/duck-2 | 2只小鸭 | L87 |
| math/flower-3 | 3朵花 | L87 |
| math/star-4 | 4颗星 | L87 |
| math/balloon-5 | 5个气球 | L87 |
| math/number-6 | 数字6 | L89 |
| math/number-7 | 数字7 | L89 |
| math/number-8 | 数字8 | L89 |
| math/number-9 | 数字9 | L89 |
| math/number-10 | 数字10 | L89 |
| math/strawberry-6 | 6个草莓 | L90 |
| math/egg-7 | 7个鸡蛋 | L90 |
| math/candy-8 | 8块糖果 | L90 |
| math/pencil-9 | 9支铅笔 | L90 |
| math/finger-10 | 10个手指 | L90 |
| math/shape-circle | 圆形 | L98 |
| math/shape-square | 正方形 | L98 |
| math/shape-triangle | 三角形 | L99 |
| math/shape-sphere | 球形 | L100 |
| math/shape-combine | 图形拼搭 | L101 |
| math/tangram | 七巧板 | L102 |

- [ ] **Step 1: 逐张生成数学配图**

prompt 模板：
```
[PURPOSE]: 儿童教育APP数学识数配图
[DESCRIPTION]: 扁平2.5D风格的{数量+物体描述}，{数量}个{物体}排列整齐，圆润可爱，暖色调，浅黄色纯色背景，无文字标注，儿童友好插画风格，1024x1024正方形
```

示例（3朵花）：
```
[PURPOSE]: 儿童教育APP数学识数配图
[DESCRIPTION]: 扁平2.5D风格的3朵彩色花朵排列整齐，圆润可爱，暖色调，浅黄色纯色背景，无文字标注，儿童友好插画风格，1024x1024正方形
```

- [ ] **Step 2: 验证图片可访问**

```bash
curl -I http://localhost:8080/images/math/number-1.jpg
curl -I http://localhost:8080/images/math/shape-circle.jpg
```

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/resources/static/images/math/
git commit -m "feat(images): 数学模块25张AI配图

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

### Task 5.5：阶段5集成验证

- [ ] **Step 1: 启动后端和前端**

```bash
cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && ./run.sh
# 终端2
cd frontend && npm run dev
```

- [ ] **Step 2: 验证数学学科入口**

打开 `http://localhost:5173/`，点击"数学"学科卡片，确认显示4个数学主题。

- [ ] **Step 3: 验证 WORD 类型课时（数字认知）**

打开 `http://localhost:5173/lesson/86`（认识数字1-5，WORD），验证：
- 数字和对应图片正确显示
- 中文释义和拼音正确
- 听发音和跟读评分功能正常

- [ ] **Step 4: 验证 CALCULATE 类型课时（加减运算）**

打开 `http://localhost:5173/lesson/92`（5以内加法，CALCULATE），验证：
- 题目正确显示（如 "1 + 2 = ?"）
- 数字键盘可用（点击数字输入答案）
- 删除和清空功能正常
- 提交答案后即时反馈对错
- 答错时显示正确答案
- 完成所有题后进入结算页

- [ ] **Step 5: 验证 QUIZ 类型课时（图形认知）**

打开 `http://localhost:5173/lesson/98`（认识圆形和方形，QUIZ），验证：
- 题目和选项正确显示
- 带图片的题目图片正确加载
- 选择答案后即时反馈对错
- 完成所有题后进入结算页

- [ ] **Step 6: 验证全学科入口**

打开 `http://localhost:5173/`，验证首页显示4个学科卡片：
- 英语（蓝色）- 4主题 36课时
- 语文（橙色）- 4主题 24课时
- 数学（绿色）- 4主题 24课时
- 课外（紫色）- 4主题 24课时

- [ ] **Step 7: 提交**

```bash
git add -A
git commit -m "feat(phase5): 数学模块完成 - CalculateLesson模板+V16迁移+25张配图

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## 验收标准

### 全局验收清单

- [ ] 首页显示4个学科卡片（英语/语文/数学/课外），各学科颜色正确
- [ ] 每个学科有4个主题，每个主题2个单元，每个单元3课时
- [ ] 总课时数：英语36 + 语文24 + 数学24 + 课外24 = 108课时
- [ ] 5种课型全部可用：WORD / SENTENCE / READING / QUIZ / CALCULATE
- [ ] LessonView 按 lesson.type 正确分发到对应模板
- [ ] 不支持的课型显示"该课型正在开发中"占位提示
- [ ] 单元解锁逻辑正常（首单元可学，后续需完成前一单元）
- [ ] 进度记录正常（完成课时后记录最高分和星星）
- [ ] 所有图片通过后端静态资源 URL 访问，不使用前端硬编码

### 模板组件验收

| 模板 | 课型 | 关键功能 |
|------|------|---------|
| WordLesson | WORD | 图片/emoji展示、TTS发音、跟读评分 |
| SentenceLesson | SENTENCE | 句子展示、TTS朗读、跟读评分 |
| ReadingLesson | READING | 图文翻页、TTS朗读全文、翻页导航 |
| QuizLesson | QUIZ | 选择题、即时对错反馈、答案高亮 |
| CalculateLesson | CALCULATE | 数字键盘输入、即时对错反馈、正确答案展示 |
| LessonComplete | 通用 | 星级展示、平均分、完成按钮 |

### 配图验收

| 目录 | 数量 | 用途 |
|------|------|------|
| static/images/words/ | ~60张(47+13补全) | 英语单词配图 |
| static/images/train/ | ~30张 | 课外火车配图 |
| static/images/chinese/ | ~24张 | 语文汉字配图 |
| static/images/story/ | ~18张 | 语文寓言故事配图 |
| static/images/math/ | ~25张 | 数学数字和图形配图 |
| **合计** | **~157张** | |

### Flyway 迁移验收

| 版本 | 内容 | 状态 |
|------|------|------|
| V1-V13 | 现有迁移 | 已完成 |
| V14 | 课外火车 + 英语配图补全 | 阶段3 |
| V15 | 语文内容 | 阶段4 |
| V16 | 数学内容 | 阶段5 |
