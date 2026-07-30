# Mimi 全站吉祥物统一与动效 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将现有 Mimi（welcome/companion/celebrate）升级为“全站统一角色组件 + 场景化动效”，并替换所有分散的 `<img>` 用法，统一视觉包装与动画体验。

**Architecture:** 通过新增 `MimiMascot` 组件封装资源选择、尺寸体系、包装（贴纸感）与 motion preset（欢迎/陪伴/庆祝/头像/空状态）。页面与课型模板只关心“场景”和“尺寸”，不再各自维护 `mascot-*` 的 CSS 与图片导入。

**Tech Stack:** Vue 3 + Vite + CSS `transition`/`keyframes`（不引入动画库），设计令牌 `frontend/src/styles/tokens.css`。

---

## 文件结构与职责

**Create**
- `frontend/src/components/MimiMascot.vue`：吉祥物统一组件（资源、包装、动效、尺寸）
- `frontend/src/utils/mimiMascotPresets.js`：吉祥物场景 preset（纯函数，便于单测）
- `frontend/test/mimiMascotPresets.test.js`：Node 原生 `node --test` 单测（保证 preset 行为稳定）

**Modify**
- `frontend/src/styles/tokens.css`：补充吉祥物相关动效/描边/光晕令牌（避免硬编码）
- `frontend/src/views/HomeView.vue`：首页欢迎区使用 `MimiMascot`
- `frontend/src/components/lesson-templates/LessonComplete.vue`：完成页使用 `MimiMascot`
- `frontend/src/views/LearnedView.vue`、`frontend/src/views/WrongAnswersView.vue`：空状态使用 `MimiMascot`
- 课型模板：`frontend/src/components/lesson-templates/{Word,Sentence,Reading,Quiz,Calculate,Phonics,Dialogue}Lesson.vue`：右上角陪伴位/对话头像统一为 `MimiMascot`

---

### Task 1: 补充吉祥物令牌

**Files:**
- Modify: `frontend/src/styles/tokens.css`

- [ ] **Step 1: 在 tokens 增加吉祥物相关令牌**

在 `:root` 追加（放在动效令牌附近即可）：

```css
  /* ===== 吉祥物 Mimi 视觉令牌 ===== */
  /* 贴纸描边与柔光：用于让 jpg 也更“像贴纸” */
  --mascot-outline: rgba(255, 255, 255, 0.95);
  --mascot-outline-soft: rgba(255, 255, 255, 0.65);
  --mascot-glow: rgba(255, 217, 61, 0.35);

  /* 吉祥物动效（更慢更柔，避免喧宾夺主） */
  --duration-mascot-float: 3200ms;
  --duration-mascot-breathe: 2400ms;
  --duration-mascot-celebrate: 900ms;
```

- [ ] **Step 2: 本地校验 tokens 未破坏构建**

Run: `cd frontend && npm run build`

Expected: build 成功，无 CSS 语法错误。

- [ ] **Step 3: Commit**

```bash
git add frontend/src/styles/tokens.css
git commit -m "style(ui): add Mimi mascot design tokens"
```

---

### Task 2: 新增 Mimi 场景 preset（可测）

**Files:**
- Create: `frontend/src/utils/mimiMascotPresets.js`
- Test: `frontend/test/mimiMascotPresets.test.js`

- [ ] **Step 1: 新增 preset 工具文件（纯函数）**

```js
// frontend/src/utils/mimiMascotPresets.js
/**
 * mimiMascotPresets.js - Mimi 吉祥物场景预设
 * 用途: 统一管理各场景的资源选择与动效策略，保持页面侧只传 variant/size 即可。
 * 作者: english-app
 * 创建日期: 2026-07-30
 */

/**
 * @typedef {"welcome"|"companion"|"celebrate"|"avatar"|"empty"} MimiVariant
 */

/**
 * @typedef {"none"|"float"|"breathe"|"celebrate"} MimiMotion
 */

/**
 * 获取吉祥物场景动效预设。
 * @param {MimiVariant} variant 场景类型
 * @return {{ motion: MimiMotion }} 预设对象
 */
export function getMimiPreset(variant) {
  // Guard Clauses：未知 variant 回退 companion（更安全）
  if (!variant) return { motion: 'breathe' }

  if (variant === 'welcome') return { motion: 'float' }
  if (variant === 'companion') return { motion: 'breathe' }
  if (variant === 'celebrate') return { motion: 'celebrate' }

  // avatar/empty 需要克制一些：仅轻微呼吸，不做弹跳
  if (variant === 'avatar') return { motion: 'breathe' }
  if (variant === 'empty') return { motion: 'float' }

  return { motion: 'breathe' }
}
```

- [ ] **Step 2: 写单测（先写测试再实现时也可，本仓库允许直接落地实现，但测试必须补）**

```js
// frontend/test/mimiMascotPresets.test.js
import test from 'node:test'
import assert from 'node:assert/strict'
import { getMimiPreset } from '../src/utils/mimiMascotPresets.js'

test('should_returnFloat_when_variantIsWelcome', () => {
  assert.equal(getMimiPreset('welcome').motion, 'float')
})

test('should_returnBreathe_when_variantIsCompanion', () => {
  assert.equal(getMimiPreset('companion').motion, 'breathe')
})

test('should_returnCelebrate_when_variantIsCelebrate', () => {
  assert.equal(getMimiPreset('celebrate').motion, 'celebrate')
})

test('should_fallbackToBreathe_when_variantIsUnknown', () => {
  assert.equal(getMimiPreset('unknown').motion, 'breathe')
})
```

- [ ] **Step 3: 运行前端测试**

Run: `cd frontend && npm test`

Expected: 全部 PASS。

- [ ] **Step 4: Commit**

```bash
git add frontend/src/utils/mimiMascotPresets.js frontend/test/mimiMascotPresets.test.js
git commit -m "test(ui): add Mimi mascot presets with tests"
```

---

### Task 3: 新增 MimiMascot 统一组件

**Files:**
- Create: `frontend/src/components/MimiMascot.vue`

- [ ] **Step 1: 新增组件（支持 variant + size + 贴纸包装 + 动效）**

```vue
<!--
  MimiMascot.vue - Mimi 吉祥物统一组件
  用途: 全站统一欢迎/陪伴/庆祝/头像/空状态的吉祥物展示与动效，避免页面分散实现。
  作者: english-app
  创建日期: 2026-07-30
-->
<script setup>
/**
 * @description Mimi 吉祥物统一组件：根据 variant 选择资源与动效 preset，提供贴纸感包装与尺寸体系。
 */
import { computed } from 'vue'
import { getMimiPreset } from '../utils/mimiMascotPresets'

import mascotWelcome from '../assets/mascot/mascot-welcome.jpg'
import mascotCompanion from '../assets/mascot/mascot-companion.jpg'
import mascotCelebrate from '../assets/mascot/mascot-celebrate.jpg'

const props = defineProps({
  /** 场景类型：welcome/companion/celebrate/avatar/empty */
  variant: {
    type: String,
    default: 'companion'
  },
  /** 尺寸：sm/md/lg */
  size: {
    type: String,
    default: 'md'
  },
  /** 是否显示贴纸包装（头像场景可关闭） */
  hasSticker: {
    type: Boolean,
    default: true
  },
  /** 自定义 alt（无则按场景默认） */
  alt: {
    type: String,
    default: ''
  }
})

const preset = computed(() => getMimiPreset(props.variant))

const src = computed(() => {
  if (props.variant === 'welcome') return mascotWelcome
  if (props.variant === 'celebrate') return mascotCelebrate
  // companion/avatar/empty 默认使用 companion 图
  return mascotCompanion
})

const altText = computed(() => {
  if (props.alt) return props.alt
  if (props.variant === 'welcome') return '小老鼠 Mimi 欢迎'
  if (props.variant === 'celebrate') return '小老鼠 Mimi 庆祝'
  return '小老鼠 Mimi 陪伴'
})

const classes = computed(() => {
  return [
    'mimi',
    `mimi--${props.size}`,
    `mimi--${props.variant}`,
    `mimi--motion-${preset.value.motion}`,
    { 'mimi--sticker': props.hasSticker }
  ]
})
</script>

<template>
  <span class="mimi-wrap" :class="classes">
    <img class="mimi-img" :src="src" :alt="altText" />
  </span>
</template>

<style scoped>
/* 容器：统一占位与可控的贴纸包装 */
.mimi-wrap {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;
  transform-origin: 50% 65%;
}

/* 图片：尽量保持清晰，不做裁切 */
.mimi-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: var(--radius-md);
  display: block;
  box-shadow: var(--shadow-soft);
}

/* 尺寸体系 */
.mimi--sm { width: 56px; height: 56px; }
.mimi--md { width: 80px; height: 80px; }
.mimi--lg { width: 160px; height: 160px; }

/* 贴纸感：通过伪元素做“白边 + 柔光”，即使 jpg 也更像品牌角色 */
.mimi--sticker::before {
  content: '';
  position: absolute;
  inset: -6px;
  border-radius: calc(var(--radius-md) + 10px);
  background: radial-gradient(circle at 30% 20%, var(--mascot-glow) 0%, rgba(255, 255, 255, 0) 60%),
              linear-gradient(180deg, rgba(255, 255, 255, 0.88) 0%, rgba(255, 255, 255, 0.68) 100%);
  filter: blur(0.2px);
  z-index: 0;
}

.mimi--sticker::after {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: calc(var(--radius-md) + 6px);
  box-shadow:
    0 0 0 2px var(--mascot-outline),
    0 6px 18px rgba(107, 124, 255, 0.14);
  z-index: 0;
}

.mimi-img { position: relative; z-index: 1; }

/* 动效（遵循 prefers-reduced-motion） */
@media (prefers-reduced-motion: no-preference) {
  .mimi--motion-float { animation: mimiFloat var(--duration-mascot-float) var(--ease-smooth) infinite; }
  .mimi--motion-breathe { animation: mimiBreathe var(--duration-mascot-breathe) var(--ease-smooth) infinite; }
  .mimi--motion-celebrate { animation: mimiCelebrate var(--duration-mascot-celebrate) var(--ease-bounce) both; }
}

@keyframes mimiFloat {
  0%, 100% { transform: translateY(0) rotate(-1deg); }
  50% { transform: translateY(-8px) rotate(1deg); }
}

@keyframes mimiBreathe {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-3px) scale(1.03); }
}

@keyframes mimiCelebrate {
  0% { transform: scale(0.75) rotate(-10deg); opacity: 0; }
  55% { transform: scale(1.12) rotate(6deg); opacity: 1; }
  100% { transform: scale(1) rotate(0); opacity: 1; }
}

/* 移动端：缩小 20%（项目规范） */
@media (max-width: 480px) {
  .mimi--lg { width: 128px; height: 128px; }
  .mimi--md { width: 64px; height: 64px; }
}
</style>
```

- [ ] **Step 2: Commit**

```bash
git add frontend/src/components/MimiMascot.vue
git commit -m "feat(ui): add MimiMascot unified component"
```

---

### Task 4: 替换全站使用点为 MimiMascot

**Files:**
- Modify: `frontend/src/views/HomeView.vue`
- Modify: `frontend/src/components/lesson-templates/LessonComplete.vue`
- Modify: `frontend/src/views/LearnedView.vue`
- Modify: `frontend/src/views/WrongAnswersView.vue`
- Modify: `frontend/src/components/lesson-templates/WordLesson.vue`
- Modify: `frontend/src/components/lesson-templates/SentenceLesson.vue`
- Modify: `frontend/src/components/lesson-templates/ReadingLesson.vue`
- Modify: `frontend/src/components/lesson-templates/QuizLesson.vue`
- Modify: `frontend/src/components/lesson-templates/CalculateLesson.vue`
- Modify: `frontend/src/components/lesson-templates/PhonicsLesson.vue`
- Modify: `frontend/src/components/lesson-templates/DialogueLesson.vue`

- [ ] **Step 1: HomeView 替换欢迎图**

在 `HomeView.vue`：
- 删除 `mascotWelcome` 图片 import
- 引入 `MimiMascot`
- 将 `<img ... class="mascot" />` 替换为组件

示例替换：

```vue
<script setup>
import MimiMascot from '../components/MimiMascot.vue'
</script>

<template>
  ...
  <MimiMascot variant="welcome" size="lg" />
  ...
</template>
```

并删除旧 `.mascot` CSS（避免重复维护）。

- [ ] **Step 2: LessonComplete 替换庆祝图**

将 `<img :src="mascotCelebrate"...>` 替换为：

```vue
<MimiMascot variant="celebrate" size="lg" />
```

并删除旧 `.mascot-celebrate` 与 `@keyframes celebrateBounce`（动效交给组件统一）。

- [ ] **Step 3: 课型模板右上角陪伴位统一**

把所有模板里的：

```vue
<img :src="mascotCompanion" alt="小老鼠陪伴" class="mascot-companion" />
```

替换为：

```vue
<MimiMascot variant="companion" size="md" class="mascot-companion" />
```

注意：保留定位用的 `.mascot-companion { position: absolute; top: 0; right: 0; ... }`，但宽高交给组件 size，CSS 只负责布局即可。

- [ ] **Step 4: DialogueLesson 的头像位替换**

将 `<img :src="mascotCompanion" ... class="avatar" />` 替换为：

```vue
<MimiMascot variant="avatar" size="sm" :hasSticker="false" class="avatar" />
```

并把 `.avatar` 的 `width/height` 移除，仅保留 `border-radius`/阴影等必要样式（或直接删掉让组件负责）。

- [ ] **Step 5: 空状态页统一**

在 `LearnedView.vue`、`WrongAnswersView.vue`：

```vue
<MimiMascot variant="empty" size="lg" />
```

并删除 `.empty-mascot` 的宽高写死（保持响应式由组件处理）。

- [ ] **Step 6: 构建与基本回归**

Run:
- `cd frontend && npm test`
- `cd frontend && npm run build`

Expected:
- tests PASS
- build 成功

- [ ] **Step 7: Commit**

```bash
git add frontend/src/views/HomeView.vue \
  frontend/src/components/lesson-templates/LessonComplete.vue \
  frontend/src/views/LearnedView.vue frontend/src/views/WrongAnswersView.vue \
  frontend/src/components/lesson-templates/WordLesson.vue \
  frontend/src/components/lesson-templates/SentenceLesson.vue \
  frontend/src/components/lesson-templates/ReadingLesson.vue \
  frontend/src/components/lesson-templates/QuizLesson.vue \
  frontend/src/components/lesson-templates/CalculateLesson.vue \
  frontend/src/components/lesson-templates/PhonicsLesson.vue \
  frontend/src/components/lesson-templates/DialogueLesson.vue
git commit -m "refactor(ui): unify Mimi usage across pages and templates"
```

---

### Task 5: 视觉微调（贴纸感与不抢戏）

**Files:**
- Modify: `frontend/src/components/MimiMascot.vue`
- Modify: 若个别页面布局需要：相关 `.mascot-companion` 定位 CSS

- [ ] **Step 1: 统一“不过分抢戏”的规则**

对右上角陪伴位增加轻微透明度与缩放（仅布局类，不改变色值硬编码）：
- 页面布局类可加：`opacity: 0.92;`
- 或组件内部对 `variant="companion"` 增加轻微 `filter: saturate(1.05);`（若效果合适）

- [ ] **Step 2: 回归检查关键路径**

Run:
- `cd frontend && npm run dev`

人工检查：
- 首页欢迎区：Mimi 有漂浮、贴纸感边缘更“干净”
- 学习页右上角：呼吸感，不遮挡内容
- 对话头像：不贴纸（更像头像），但仍有轻微呼吸
- 完成页：入场弹跳（只触发一次）
- 空状态：轻漂浮（不喧宾夺主）

- [ ] **Step 3: Commit**

```bash
git add frontend/src/components/MimiMascot.vue
git commit -m "style(ui): refine Mimi sticker and motion presets"
```

---

## 完成标准（Definition of Done）

- 所有 `Mimi` 使用点都通过 `MimiMascot` 统一接入（欢迎/陪伴/庆祝/头像/空状态）。
- 满足项目动效规范：仅 CSS `transition` + `keyframes`，并包裹 `prefers-reduced-motion`。
- 前端构建通过：`cd frontend && npm run build`。
- 前端测试通过：`cd frontend && npm test`。

