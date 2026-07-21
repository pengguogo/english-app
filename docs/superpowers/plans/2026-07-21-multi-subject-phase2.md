# 多学科重构 - 阶段2：英语迁移 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 LessonView 从硬编码的"听音→跟读→评分"流程重构为按 lesson.type 分发的路由器，提取 WordLesson 和 SentenceLesson 两个模板组件，确保英语学习体验与重构前完全一致。

**Architecture:** LessonView 保留课时加载、状态管理、完成提交等公共逻辑，根据 lesson.type 用 `<component :is>` 动态渲染对应模板。WordLesson 和 SentenceLesson 作为纯展示组件，通过 props 接收数据和状态，通过 emits 上报评分结果。公共样式提取到共享 CSS 或由各模板自包含。

**Tech Stack:** Vue 3 + Vite + Composition API (script setup)

---

## 文件结构

### 新增文件
- `frontend/src/components/lesson-templates/WordLesson.vue` — WORD 类型模板（单词认字+跟读评分）
- `frontend/src/components/lesson-templates/SentenceLesson.vue` — SENTENCE 类型模板（句型朗诵评分）
- `frontend/src/components/lesson-templates/LessonComplete.vue` — 通用结算页组件（所有课型复用）

### 修改文件
- `frontend/src/views/LessonView.vue` — 重构为分发器，保留公共逻辑，用动态组件渲染模板

---

## Task 1: 创建 LessonComplete 通用结算页组件

**Files:**
- Create: `frontend/src/components/lesson-templates/LessonComplete.vue`

- [ ] **Step 1: 创建 LessonComplete.vue**

创建 `frontend/src/components/lesson-templates/LessonComplete.vue`：

```vue
<!--
  LessonComplete.vue - 课时完成结算页
  用途: 展示课时完成后的庆祝插画、星星、平均分和完成按钮。
        所有课型（WORD/SENTENCE/READING/QUIZ/CALCULATE）共用此结算组件。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description 课时完成结算组件，展示最终成绩和庆祝动画。
 */
import StarBar from '../StarBar.vue'
import AppButton from '../AppButton.vue'
import mascotCelebrate from '../../assets/mascot/mascot-celebrate.jpg'

/**
 * 组件 Props
 * @property {string} lessonName 课时名称
 * @property {number} totalStars 最终获得星星数（0-3）
 * @property {number} totalScore 平均得分（0-100）
 * @property {boolean} isSubmitting 是否正在提交进度
 */
const props = defineProps({
  /** 课时名称 */
  lessonName: {
    type: String,
    default: ''
  },
  /** 最终获得星星数（0-3） */
  totalStars: {
    type: Number,
    default: 0
  },
  /** 平均得分（0-100） */
  totalScore: {
    type: Number,
    default: 0
  },
  /** 是否正在提交进度 */
  isSubmitting: {
    type: Boolean,
    default: false
  }
})

/**
 * 组件 Emits
 * @event finish 用户点击完成按钮
 */
const emit = defineEmits({
  /** 用户点击完成按钮时触发 */
  finish: null
})
</script>

<template>
  <div class="complete-area">
    <!-- 小老鼠庆祝插画（主视觉） -->
    <img :src="mascotCelebrate" alt="小老鼠庆祝" class="mascot-celebrate" />

    <h2>太棒了!</h2>
    <p class="complete-lesson-name">你完成了《{{ lessonName }}》</p>

    <StarBar :stars="totalStars" size="lg" />

    <p class="total-score">平均得分:{{ totalScore }} 分</p>

    <AppButton
      variant="success"
      size="lg"
      :disabled="isSubmitting"
      @click="emit('finish')"
    >
      {{ isSubmitting ? '保存中...' : '完成' }}
    </AppButton>
  </div>
</template>

<style scoped>
.complete-area {
  text-align: center;
  padding: var(--space-6) var(--space-4);
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  max-width: 480px;
  margin: var(--space-6) auto;
  box-shadow: var(--shadow-card);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
}

/* 小老鼠庆祝插画: 主视觉 */
.mascot-celebrate {
  width: 160px;
  height: 160px;
  object-fit: contain;
  border-radius: var(--radius-md);
}

@media (prefers-reduced-motion: no-preference) {
  .mascot-celebrate {
    animation: celebrateBounce var(--duration-slow) var(--ease-bounce) both;
  }
}

@keyframes celebrateBounce {
  0% {
    transform: scale(0) rotate(-180deg);
    opacity: 0;
  }
  60% {
    transform: scale(1.15) rotate(10deg);
    opacity: 1;
  }
  100% {
    transform: scale(1) rotate(0);
    opacity: 1;
  }
}

.complete-area h2 {
  font-size: var(--text-xl);
  color: var(--color-orange);
  font-weight: var(--font-bold);
}

.complete-lesson-name {
  color: var(--text-secondary);
  font-size: var(--text-base);
}

.total-score {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
}

/* 响应式: 手机端缩小吉祥物 */
@media (max-width: 480px) {
  .mascot-celebrate { width: 128px; height: 128px; }
}
</style>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/components/lesson-templates/LessonComplete.vue
git commit -m "feat(lesson): 新增LessonComplete通用结算页组件

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 2: 创建 WordLesson 模板组件

**Files:**
- Create: `frontend/src/components/lesson-templates/WordLesson.vue`

- [ ] **Step 1: 创建 WordLesson.vue**

创建 `frontend/src/components/lesson-templates/WordLesson.vue`。此组件负责 WORD 类型的学习交互（看图→听音→跟读→评分），从原 LessonView 提取相关逻辑：

```vue
<!--
  WordLesson.vue - WORD 类型课时模板
  用途: 单词认字学习，展示图片/emoji+英文+音标+释义，支持听发音和跟读评分。
        适用于英语单词课、语文汉字/拼音课、数学数字课。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description WORD 类型课时学习模板，展示当前学习项并提供听音+跟读评分交互。
 */
import { computed } from 'vue'
import StarBar from '../StarBar.vue'
import AudioButton from '../AudioButton.vue'
import RecordButton from '../RecordButton.vue'
import AppButton from '../AppButton.vue'
import mascotCompanion from '../../assets/mascot/mascot-companion.jpg'

/**
 * 组件 Props
 * @property {Object} currentItem 当前学习项 { word, phonetic, translation, emoji, image }
 * @property {number} currentIndex 当前索引（从0开始）
 * @property {number} totalItems 总项数
 * @property {number} currentScore 当前项评分（null 表示未评分）
 * @property {number} currentStars 当前项星星数
 * @property {string} scoreMessage 评分反馈文案
 * @property {boolean} isScoring 是否正在评分
 * @property {boolean} isLastItem 是否最后一项
 */
const props = defineProps({
  /** 当前学习项对象 */
  currentItem: {
    type: Object,
    default: null
  },
  /** 当前索引（从0开始） */
  currentIndex: {
    type: Number,
    default: 0
  },
  /** 总项数 */
  totalItems: {
    type: Number,
    default: 0
  },
  /** 当前项评分（null 表示未评分） */
  currentScore: {
    type: Number,
    default: null
  },
  /** 当前项星星数 */
  currentStars: {
    type: Number,
    default: 0
  },
  /** 评分反馈文案 */
  scoreMessage: {
    type: String,
    default: ''
  },
  /** 是否正在评分 */
  isScoring: {
    type: Boolean,
    default: false
  },
  /** 是否最后一项 */
  isLastItem: {
    type: Boolean,
    default: false
  }
})

/**
 * 组件 Emits
 * @event recorded 录音完成，传递 WAV Blob
 * @event next 切换下一项
 */
const emit = defineEmits({
  /** 录音完成，参数为 WAV Blob */
  recorded: null,
  /** 切换下一项 */
  next: null
})

/**
 * 当前学习项对应的文本（word 字段）。
 */
const currentText = computed(() => {
  if (!props.currentItem) return ''
  return props.currentItem.word || ''
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
    <!-- 顶部进度条 + 数字标注 -->
    <div class="progress-section">
      <div class="progress-info">
        <span>第 {{ currentIndex + 1 }} / {{ totalItems }} 项</span>
      </div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
      </div>
    </div>

    <!-- 小老鼠陪伴插画（右上角） -->
    <img :src="mascotCompanion" alt="小老鼠陪伴" class="mascot-companion" />

    <!-- 学习项卡片: 图片/emoji + 英文 + 音标 + 中文释义 -->
    <div class="item-card">
      <div v-if="currentItem.image" class="word-image-wrapper">
        <img :src="currentItem.image" :alt="currentItem.word" class="word-image" />
      </div>
      <div v-else class="emoji">{{ currentItem.emoji }}</div>
      <h1 class="word">{{ currentText }}</h1>
      <p v-if="currentItem.phonetic" class="phonetic">/{{ currentItem.phonetic }}/</p>
      <p class="translation">{{ currentItem.translation }}</p>
    </div>

    <!-- 操作区: 听发音 + 跟读录音 -->
    <div class="actions">
      <AudioButton :text="currentText" :translation="currentItem.translation || ''" />
      <RecordButton @recorded="(blob) => emit('recorded', blob)" />
    </div>

    <!-- 评分结果区 -->
    <div v-if="currentScore !== null" class="score-area">
      <div class="score-row">
        <span class="score">{{ currentScore }} 分</span>
        <StarBar :stars="currentStars" size="sm" />
      </div>
      <p class="feedback">{{ scoreMessage }}</p>
    </div>
    <div v-else-if="isScoring" class="score-area scoring">
      <div class="scoring-dot"></div>
      <p class="feedback">{{ scoreMessage }}</p>
    </div>

    <!-- 下一步 / 完成本课 -->
    <AppButton
      variant="primary"
      size="lg"
      block
      @click="emit('next')"
    >
      {{ isLastItem ? '完成本课' : '下一步 →' }}
    </AppButton>
  </div>
</template>

<style scoped>
.card-area {
  max-width: 480px;
  margin: 0 auto;
  position: relative;
}

/* 顶部进度条 */
.progress-section {
  margin-bottom: var(--space-5);
}

.progress-info {
  text-align: center;
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  margin-bottom: var(--space-2);
}

.progress-bar {
  height: 8px;
  background: var(--bg-card);
  border-radius: var(--radius-pill);
  overflow: hidden;
  box-shadow: var(--shadow-soft);
}

.progress-fill {
  height: 100%;
  background: var(--gradient-primary);
  border-radius: var(--radius-pill);
  transition: width var(--duration-slow) var(--ease-smooth);
}

/* 小老鼠陪伴插画 */
.mascot-companion {
  position: absolute;
  top: 0;
  right: 0;
  width: 80px;
  height: 80px;
  object-fit: contain;
  border-radius: var(--radius-md);
  z-index: 1;
  opacity: 0.9;
}

/* 学习项卡片 */
.item-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-8) var(--space-6);
  text-align: center;
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-card);
}

.emoji {
  font-size: 5rem;
  margin-bottom: var(--space-4);
}

.word-image-wrapper {
  margin-bottom: var(--space-4);
}

.word-image {
  width: 180px;
  height: 180px;
  object-fit: cover;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-soft);
}

.word {
  font-size: 2.5rem;
  margin-bottom: var(--space-2);
  color: var(--text-primary);
  word-break: break-word;
  font-weight: var(--font-bold);
}

.phonetic {
  color: var(--text-secondary);
  font-size: var(--text-lg);
  margin-bottom: var(--space-2);
}

.translation {
  color: var(--text-secondary);
  font-size: var(--text-base);
}

/* 操作区 */
.actions {
  display: flex;
  gap: var(--space-4);
  justify-content: center;
  margin-bottom: var(--space-6);
}

/* 评分结果区 */
.score-area {
  text-align: center;
  background: var(--bg-card);
  border-radius: var(--radius-md);
  padding: var(--space-4);
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-soft);
}

.score-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-3);
  margin-bottom: var(--space-2);
}

.score {
  font-size: 2rem;
  font-weight: var(--font-bold);
  color: var(--color-orange);
}

@media (prefers-reduced-motion: no-preference) {
  .score {
    animation: scoreBounce var(--duration-normal) var(--ease-bounce) both;
  }
}

@keyframes scoreBounce {
  0% { transform: translateY(8px) scale(0.8); opacity: 0; }
  60% { transform: translateY(-4px) scale(1.1); opacity: 1; }
  100% { transform: translateY(0) scale(1); opacity: 1; }
}

.feedback {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

.score-area.scoring {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-3);
}

.scoring-dot {
  width: 16px;
  height: 16px;
  border: 2px solid var(--border-light);
  border-top-color: var(--color-orange);
  border-radius: var(--radius-pill);
}

@media (prefers-reduced-motion: no-preference) {
  .scoring-dot { animation: spin 0.8s linear infinite; }
}

@keyframes spin { to { transform: rotate(360deg); } }

/* 响应式 */
@media (max-width: 480px) {
  .mascot-companion { width: 64px; height: 64px; }
  .word { font-size: 2rem; }
  .emoji { font-size: 4rem; }
}
</style>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/components/lesson-templates/WordLesson.vue
git commit -m "feat(lesson): 新增WordLesson模板组件(单词认字+跟读评分)

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 3: 创建 SentenceLesson 模板组件

**Files:**
- Create: `frontend/src/components/lesson-templates/SentenceLesson.vue`

- [ ] **Step 1: 创建 SentenceLesson.vue**

创建 `frontend/src/components/lesson-templates/SentenceLesson.vue`。与 WordLesson 结构类似，但展示 sentence 字段而非 word 字段：

```vue
<!--
  SentenceLesson.vue - SENTENCE 类型课时模板
  用途: 句型/古诗朗诵学习，展示句子+释义，支持听发音和跟读朗诵评分。
        适用于英语句型课、语文古诗朗诵课。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description SENTENCE 类型课时学习模板，展示当前句子并提供听音+跟读评分交互。
 */
import { computed } from 'vue'
import StarBar from '../StarBar.vue'
import AudioButton from '../AudioButton.vue'
import RecordButton from '../RecordButton.vue'
import AppButton from '../AppButton.vue'
import mascotCompanion from '../../assets/mascot/mascot-companion.jpg'

/**
 * 组件 Props
 * @property {Object} currentItem 当前学习项 { sentence, translation, emoji, image }
 * @property {number} currentIndex 当前索引
 * @property {number} totalItems 总项数
 * @property {number} currentScore 当前项评分
 * @property {number} currentStars 当前项星星数
 * @property {string} scoreMessage 评分反馈文案
 * @property {boolean} isScoring 是否正在评分
 * @property {boolean} isLastItem 是否最后一项
 */
const props = defineProps({
  currentItem: { type: Object, default: null },
  currentIndex: { type: Number, default: 0 },
  totalItems: { type: Number, default: 0 },
  currentScore: { type: Number, default: null },
  currentStars: { type: Number, default: 0 },
  scoreMessage: { type: String, default: '' },
  isScoring: { type: Boolean, default: false },
  isLastItem: { type: Boolean, default: false }
})

const emit = defineEmits({
  /** 录音完成，参数为 WAV Blob */
  recorded: null,
  /** 切换下一项 */
  next: null
})

/**
 * 当前学习项对应的文本（sentence 字段）。
 */
const currentText = computed(() => {
  if (!props.currentItem) return ''
  return props.currentItem.sentence || ''
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
        <span>第 {{ currentIndex + 1 }} / {{ totalItems }} 项</span>
      </div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
      </div>
    </div>

    <!-- 小老鼠陪伴插画 -->
    <img :src="mascotCompanion" alt="小老鼠陪伴" class="mascot-companion" />

    <!-- 句型卡片 -->
    <div class="item-card">
      <div v-if="currentItem.image" class="sentence-image-wrapper">
        <img :src="currentItem.image" :alt="currentItem.sentence" class="sentence-image" />
      </div>
      <div v-else class="emoji">{{ currentItem.emoji || '💬' }}</div>
      <h1 class="sentence-text">{{ currentText }}</h1>
      <p class="translation">{{ currentItem.translation }}</p>
    </div>

    <!-- 操作区 -->
    <div class="actions">
      <AudioButton :text="currentText" :translation="currentItem.translation || ''" />
      <RecordButton @recorded="(blob) => emit('recorded', blob)" />
    </div>

    <!-- 评分结果区 -->
    <div v-if="currentScore !== null" class="score-area">
      <div class="score-row">
        <span class="score">{{ currentScore }} 分</span>
        <StarBar :stars="currentStars" size="sm" />
      </div>
      <p class="feedback">{{ scoreMessage }}</p>
    </div>
    <div v-else-if="isScoring" class="score-area scoring">
      <div class="scoring-dot"></div>
      <p class="feedback">{{ scoreMessage }}</p>
    </div>

    <!-- 下一步 / 完成本课 -->
    <AppButton
      variant="primary"
      size="lg"
      block
      @click="emit('next')"
    >
      {{ isLastItem ? '完成本课' : '下一步 →' }}
    </AppButton>
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

.item-card { background: var(--bg-card); border-radius: var(--radius-lg); padding: var(--space-8) var(--space-6); text-align: center; margin-bottom: var(--space-6); box-shadow: var(--shadow-card); }
.emoji { font-size: 5rem; margin-bottom: var(--space-4); }
.sentence-image-wrapper { margin-bottom: var(--space-4); }
.sentence-image { width: 180px; height: 180px; object-fit: cover; border-radius: var(--radius-lg); box-shadow: var(--shadow-soft); }
.sentence-text { font-size: 1.8rem; margin-bottom: var(--space-2); color: var(--text-primary); word-break: break-word; font-weight: var(--font-bold); line-height: 1.4; }
.translation { color: var(--text-secondary); font-size: var(--text-base); }

.actions { display: flex; gap: var(--space-4); justify-content: center; margin-bottom: var(--space-6); }

.score-area { text-align: center; background: var(--bg-card); border-radius: var(--radius-md); padding: var(--space-4); margin-bottom: var(--space-6); box-shadow: var(--shadow-soft); }
.score-row { display: flex; align-items: center; justify-content: center; gap: var(--space-3); margin-bottom: var(--space-2); }
.score { font-size: 2rem; font-weight: var(--font-bold); color: var(--color-orange); }

@media (prefers-reduced-motion: no-preference) {
  .score { animation: scoreBounce var(--duration-normal) var(--ease-bounce) both; }
}

@keyframes scoreBounce {
  0% { transform: translateY(8px) scale(0.8); opacity: 0; }
  60% { transform: translateY(-4px) scale(1.1); opacity: 1; }
  100% { transform: translateY(0) scale(1); opacity: 1; }
}

.feedback { color: var(--text-secondary); font-size: var(--text-sm); }
.score-area.scoring { display: flex; align-items: center; justify-content: center; gap: var(--space-3); }
.scoring-dot { width: 16px; height: 16px; border: 2px solid var(--border-light); border-top-color: var(--color-orange); border-radius: var(--radius-pill); }

@media (prefers-reduced-motion: no-preference) {
  .scoring-dot { animation: spin 0.8s linear infinite; }
}

@keyframes spin { to { transform: rotate(360deg); } }

@media (max-width: 480px) {
  .mascot-companion { width: 64px; height: 64px; }
  .sentence-text { font-size: 1.5rem; }
  .emoji { font-size: 4rem; }
}
</style>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/components/lesson-templates/SentenceLesson.vue
git commit -m "feat(lesson): 新增SentenceLesson模板组件(句型朗诵评分)

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 4: 重构 LessonView 为分发器

**Files:**
- Modify: `frontend/src/views/LessonView.vue`

- [ ] **Step 1: 重写 LessonView.vue 为分发器**

用以下内容完整替换 `frontend/src/views/LessonView.vue`。LessonView 保留所有公共逻辑（课时加载、状态管理、评分、完成提交），根据 lesson.type 动态渲染对应模板组件。当 lesson.type 为 READING/QUIZ/CALCULATE 时，暂时渲染一个"敬请期待"占位组件（后续阶段实现）：

```vue
<!--
  LessonView.vue - 课时学习页（分发器）
  用途: 加载课时详情，根据 lesson.type 分发到对应的模板组件。
        WORD → WordLesson, SENTENCE → SentenceLesson,
        READING/QUIZ/CALCULATE → 占位（后续阶段实现）。
        公共逻辑（加载、评分、完成提交）由本组件统一管理。
  作者: english-app
  创建日期: 2026-07-20
  修改: 2026-07-21 重构为按 type 分发的路由器
-->
<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getLessonById } from '../api/lesson'
import { completeLesson } from '../api/progress'
import { scorePronunciation } from '../api/voice'
import StarBar from '../components/StarBar.vue'
import BackBar from '../components/BackBar.vue'
import WordLesson from '../components/lesson-templates/WordLesson.vue'
import SentenceLesson from '../components/lesson-templates/SentenceLesson.vue'
import LessonComplete from '../components/lesson-templates/LessonComplete.vue'

const route = useRoute()
const router = useRouter()

// ===== 页面状态 =====
const lesson = ref(null)
const isLoading = ref(true)
const errorMsg = ref('')

const currentIndex = ref(0)
const currentScore = ref(null)
const currentStars = ref(0)
const scoreMessage = ref('')
const isScoring = ref(false)
const isComplete = ref(false)
const isSubmitting = ref(false)

// 记录每个学习项的历史最佳分
const bestScores = ref([])

// ===== 计算属性 =====

/**
 * 当前学习项。
 */
const currentItem = computed(() => {
  if (!lesson.value || !lesson.value.content) return null
  const items = lesson.value.content.items
  if (!Array.isArray(items) || items.length === 0) return null
  return items[currentIndex.value] || items[0]
})

/**
 * 当前学习项对应的文本（WORD 取 word，SENTENCE 取 sentence）。
 */
const currentText = computed(() => {
  if (!currentItem.value) return ''
  return currentItem.value.word || currentItem.value.sentence || ''
})

/**
 * 学习项总数。
 */
const totalItems = computed(() => {
  if (!lesson.value || !lesson.value.content) return 0
  return lesson.value.content.items?.length ?? 0
})

/**
 * 是否为最后一个学习项。
 */
const isLastItem = computed(() => currentIndex.value >= totalItems.value - 1)

/**
 * 整个课时累计最佳分数（各 item 历史最佳成绩的平均值）。
 */
const totalBestScore = computed(() => {
  const validScores = bestScores.value.filter((s) => typeof s === 'number')
  if (validScores.length === 0) return 0
  const sum = validScores.reduce((acc, s) => acc + s, 0)
  return Math.round(sum / validScores.length)
})

/**
 * 整个课时最终获得的星星数。
 */
const totalStars = computed(() => scoreToStars(totalBestScore.value))

/**
 * 根据 lesson.type 返回对应的模板组件。
 * READING/QUIZ/CALCULATE 暂返回 null（渲染占位）。
 */
const lessonTemplate = computed(() => {
  if (!lesson.value) return null
  switch (lesson.value.type) {
    case 'WORD':
      return WordLesson
    case 'SENTENCE':
      return SentenceLesson
    default:
      // READING/QUIZ/CALCULATE 等后续阶段实现
      return null
  }
})

// ===== 生命周期 =====

onMounted(loadLesson)

// ===== 业务方法 =====

/**
 * 根据分数计算星级（0-3）。
 * @param {number} score 0-100 分数
 * @return {number} 0-3 星级
 */
function scoreToStars(score) {
  if (score >= 80) return 3
  if (score >= 60) return 2
  if (score >= 40) return 1
  return 0
}

/**
 * 简单的英文单词到 emoji 映射表。
 * 后端 content 仅返回单词字符串时，前端补充 emoji 与释义。
 */
const wordMetaMap = {
  apple: { emoji: '🍎', translation: '苹果', phonetic: 'ˈæpl' },
  banana: { emoji: '🍌', translation: '香蕉', phonetic: 'bəˈnɑːnə' },
  orange: { emoji: '🍊', translation: '橙子', phonetic: 'ˈɒrɪndʒ' },
  grape: { emoji: '🍇', translation: '葡萄', phonetic: 'greɪp' },
  car: { emoji: '🚗', translation: '小汽车', phonetic: 'kɑː' },
  bus: { emoji: '🚌', translation: '公交车', phonetic: 'bʌs' },
  bike: { emoji: '🚲', translation: '自行车', phonetic: 'baɪk' },
  train: { emoji: '🚂', translation: '火车', phonetic: 'treɪn' },
  plane: { emoji: '✈️', translation: '飞机', phonetic: 'pleɪn' },
  helicopter: { emoji: '🚁', translation: '直升机', phonetic: 'ˈhelɪkɒptə' },
  balloon: { emoji: '🎈', translation: '热气球', phonetic: 'bəˈluːn' },
  rocket: { emoji: '🚀', translation: '火箭', phonetic: 'ˈrɒkɪt' },
  boat: { emoji: '⛵', translation: '小船', phonetic: 'bəʊt' },
  ship: { emoji: '🚢', translation: '大船', phonetic: 'ʃɪp' },
  submarine: { emoji: '🤿', translation: '潜水艇', phonetic: 'ˌsʌbməˈriːn' }
}

/**
 * 将后端 content 原始格式统一转换为 { items: [...] } 结构。
 * @param {Object} raw 解析后的 content 对象
 * @return {Object} { items: [...] }
 */
function normalizeContent(raw) {
  const items = []
  if (Array.isArray(raw.words)) {
    raw.words.forEach((w) => {
      const meta = wordMetaMap[w.toLowerCase()] || {}
      items.push({
        word: w,
        emoji: meta.emoji || '🔤',
        phonetic: meta.phonetic || '',
        translation: meta.translation || ''
      })
    })
  } else if (Array.isArray(raw.sentences)) {
    raw.sentences.forEach((s) => {
      items.push({ sentence: s, emoji: '💬', phonetic: '', translation: '' })
    })
  } else if (Array.isArray(raw.items)) {
    raw.items.forEach((it) => items.push(it))
  }
  return { items }
}

/**
 * 加载课时详情并解析 content。
 */
async function loadLesson() {
  try {
    const data = await getLessonById(route.params.lessonId)
    if (typeof data.content === 'string') {
      data.content = normalizeContent(JSON.parse(data.content))
    } else if (data.content && typeof data.content === 'object') {
      data.content = normalizeContent(data.content)
    }
    lesson.value = data
    bestScores.value = new Array(totalItems.value).fill(0)
  } catch (e) {
    errorMsg.value = '加载课时失败,请返回重试'
    console.error('加载课时失败:', e)
  } finally {
    isLoading.value = false
  }
}

/**
 * 录音完成回调:调用发音评测接口并更新当前项评分。
 * @param {Blob} wavBlob RecordButton 抛出的 WAV 音频 Blob
 */
async function handleRecorded(wavBlob) {
  if (!currentItem.value || !currentText.value) return
  isScoring.value = true
  scoreMessage.value = '评分中...'
  currentScore.value = null
  try {
    const result = await scorePronunciation(wavBlob, currentText.value)
    currentScore.value = result.score
    currentStars.value = scoreToStars(result.score)
    scoreMessage.value = result.feedback || ''
    updateBestScore(currentIndex.value, result.score)
  } catch (e) {
    scoreMessage.value = '评分失败,请重试'
    console.error('发音评测失败:', e)
  } finally {
    isScoring.value = false
  }
}

/**
 * 更新指定学习项的历史最佳分。
 * @param {number} index 学习项索引
 * @param {number} score 本次评分
 */
function updateBestScore(index, score) {
  const prevBest = bestScores.value[index] || 0
  bestScores.value[index] = Math.max(prevBest, score)
}

/**
 * 重置当前项的评分展示状态。
 */
function resetCurrentScoreState() {
  currentScore.value = null
  currentStars.value = 0
  scoreMessage.value = ''
}

/**
 * 切换到下一个学习项。若已是最后一项，则进入完成结算页。
 */
function nextItem() {
  if (currentIndex.value < totalItems.value - 1) {
    currentIndex.value++
    resetCurrentScoreState()
  } else {
    isComplete.value = true
  }
}

/**
 * 完成课时:提交进度并导航回单元列表页。
 */
async function finishLesson() {
  if (isSubmitting.value) return
  isSubmitting.value = true
  try {
    await completeLesson(
      route.params.lessonId,
      totalStars.value,
      totalBestScore.value
    )
    router.push(`/unit/${lesson.value.unitId}`)
  } catch (e) {
    console.error('保存进度失败:', e)
    alert('保存失败,请重试')
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <div class="lesson-view">
    <!-- 顶部栏 -->
    <BackBar @back="router.back()">
      <template #right>
        <StarBar :stars="isComplete ? totalStars : currentStars" size="sm" />
      </template>
    </BackBar>

    <!-- 加载中 -->
    <div v-if="isLoading" class="state-tip">
      <div class="loading-dot"></div>
      <p>加载中...</p>
    </div>
    <!-- 加载失败 -->
    <div v-else-if="errorMsg" class="state-tip error">{{ errorMsg }}</div>

    <!-- 学习内容区 -->
    <template v-else>
      <!-- 已完成:结算页 -->
      <LessonComplete
        v-if="isComplete"
        :lesson-name="lesson.name"
        :total-stars="totalStars"
        :total-score="totalBestScore"
        :is-submitting="isSubmitting"
        @finish="finishLesson"
      />

      <!-- 按类型分发到对应模板 -->
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
        @next="nextItem"
      />

      <!-- 未支持的课型:占位提示 -->
      <div v-else class="state-tip">
        <p>该课型正在开发中，敬请期待！</p>
        <p class="type-hint">课型: {{ lesson.type }}</p>
      </div>
    </template>
  </div>
</template>

<style scoped>
.lesson-view {
  min-height: 100vh;
  padding: var(--space-4);
  background: var(--gradient-warm);
  box-sizing: border-box;
  position: relative;
}

.state-tip {
  text-align: center;
  padding: var(--space-8);
  color: var(--text-tertiary);
}
.state-tip.error { color: var(--color-warning); }

.type-hint {
  font-size: var(--text-sm);
  color: var(--text-tertiary);
  margin-top: var(--space-2);
}

.loading-dot {
  width: 32px;
  height: 32px;
  border: 3px solid var(--border-light);
  border-top-color: var(--color-primary);
  border-radius: var(--radius-pill);
  margin: 0 auto var(--space-3);
}

@media (prefers-reduced-motion: no-preference) {
  .loading-dot { animation: spin 0.8s linear infinite; }
}

@keyframes spin { to { transform: rotate(360deg); } }
</style>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/views/LessonView.vue
git commit -m "refactor(lesson): LessonView重构为按type分发的路由器

- 提取WordLesson和SentenceLesson为独立模板组件
- LessonComplete作为通用结算页组件
- READING/QUIZ/CALCULATE暂显示占位提示
- 英语学习流程体验与重构前完全一致

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 5: 集成验证

**Files:** 无代码变更，纯验证

- [ ] **Step 1: 启动后端和前端**

```bash
# 终端1: 启动后端
cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && ./run.sh

# 终端2: 启动前端
cd frontend && npm run dev
```

- [ ] **Step 2: 验证 WORD 类型课时**

打开 `http://localhost:5173/lesson/2`（交通工具-认识汽车，WORD 类型），验证：
- 显示单词"car"、音标、翻译
- 图片或 emoji 正常显示
- 听发音按钮可用
- 跟读录音后评分正常
- 下一项切换正常
- 完成后结算页显示星星和分数
- 点击完成跳转回单元列表

- [ ] **Step 3: 验证 SENTENCE 类型课时**

打开 `http://localhost:5173/lesson/5`（句型课，SENTENCE 类型），验证：
- 显示句子和翻译
- 听发音和跟读评分正常
- 完成流程正常

- [ ] **Step 4: 验证已有学习进度无回归**

检查用户的已完成课时（Lesson 2/3/4/5/35）进度数据未受影响。

- [ ] **Step 5: 提交最终状态**

```bash
git add -A
git commit -m "chore: 阶段2英语迁移完成，集成验证通过

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## 验收标准

阶段2完成后需满足：
1. LessonView 重构为按 lesson.type 分发的路由器
2. WordLesson 模板组件正确渲染 WORD 类型课时
3. SentenceLesson 模板组件正确渲染 SENTENCE 类型课时
4. LessonComplete 通用结算页组件正常工作
5. 英语学习流程体验与重构前完全一致（听音→跟读→评分→切换→结算）
6. READING/QUIZ/CALCULATE 类型显示"开发中"占位提示
7. 用户已有学习进度数据无回归
