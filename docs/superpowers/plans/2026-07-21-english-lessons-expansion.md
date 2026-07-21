# 英语课时扩展实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 英语课时从 36 扩展到 60，新增 PHONICS/DIALOGUE 两种课型，汪汪队和小砾工程主题改用官方角色。

**Architecture:** 后端新增枚举值 + Flyway V18 迁移（UPDATE 13 课 + INSERT 24 课）；前端新增 PhonicsLesson.vue 和 DialogueLesson.vue 两个模板组件，LessonView.vue 添加分发。图片优先网络下载动画片原版，不行用 AI 生成。

**Tech Stack:** Java 17 + Spring Boot 3.2 + JPA + Flyway + SQLite / Vue 3 + Vite + Pinia

**设计文档:** `docs/superpowers/specs/2026-07-21-english-lessons-expansion-design.md`

---

## 文件结构

### 后端修改/新增

| 文件 | 操作 | 职责 |
|------|------|------|
| `backend/src/main/java/com/englishapp/domain/enums/LessonType.java` | 修改 | 新增 PHONICS/DIALOGUE 枚举值 |
| `backend/src/main/resources/db/migration/V18__expand_english_lessons.sql` | 新增 | UPDATE 13 课 + INSERT 24 课 |
| `backend/src/main/resources/static/images/paw-patrol/` | 新增图片 | 汪汪队角色图（已有 6 张，补充反派/人类） |
| `backend/src/main/resources/static/images/rubble-crew/` | 新增目录+图片 | 小砾工程家族角色图 |
| `backend/src/test/.../LessonTypeTest.java` | 新增 | 枚举值测试 |
| `backend/src/test/.../WordImageResolverTest.java` | 修改 | 新增 PHONICS/DIALOGUE 兼容性测试 |

### 前端修改/新增

| 文件 | 操作 | 职责 |
|------|------|------|
| `frontend/src/components/lesson-templates/PhonicsLesson.vue` | 新增 | PHONICS 课型模板（字母学习+词汇+练习） |
| `frontend/src/components/lesson-templates/DialogueLesson.vue` | 新增 | DIALOGUE 课型模板（对话气泡+跟读） |
| `frontend/src/views/LessonView.vue` | 修改 | 添加 PHONICS/DIALOGUE 分发 case |

---

## Task 1: 后端 LessonType 枚举新增 PHONICS/DIALOGUE

**Files:**
- Modify: `backend/src/main/java/com/englishapp/domain/enums/LessonType.java`
- Test: `backend/src/test/java/com/englishapp/domain/enums/LessonTypeTest.java`

- [ ] **Step 1: 编写枚举测试**

```java
package com.englishapp.domain.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * LessonType 枚举测试
 * @author englishapp
 * @since 1.1.0
 */
class LessonTypeTest {

    /**
     * 验证枚举包含所有预期值
     */
    @Test
    void should_containAllExpectedTypes_when_enumValuesChecked() {
        LessonType[] types = LessonType.values();
        // 原有 7 个 + 新增 2 个 = 9 个
        assertEquals(9, types.length);
        assertTrue(containsType(types, "PHONICS"));
        assertTrue(containsType(types, "DIALOGUE"));
    }

    /**
     * 验证 PHONICS 可通过 valueOf 获取
     */
    @Test
    void should_returnPhonicsEnum_when_valueOfPhonicsCalled() {
        LessonType type = LessonType.valueOf("PHONICS");
        assertEquals("PHONICS", type.name());
    }

    /**
     * 验证 DIALOGUE 可通过 valueOf 获取
     */
    @Test
    void should_returnDialogueEnum_when_valueOfDialogueCalled() {
        LessonType type = LessonType.valueOf("DIALOGUE");
        assertEquals("DIALOGUE", type.name());
    }

    private boolean containsType(LessonType[] types, String name) {
        for (LessonType t : types) {
            if (t.name().equals(name)) return true;
        }
        return false;
    }
}
```

- [ ] **Step 2: 运行测试验证失败**

Run: `cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && mvn test -Dtest=LessonTypeTest`
Expected: FAIL（枚举值不存在）

- [ ] **Step 3: 修改 LessonType 枚举，新增 PHONICS 和 DIALOGUE**

在 `CALCULATE` 之后、`GAME` 之前插入：

```java
    /** 自然拼读：字母发音规则学习（英语启蒙） */
    PHONICS,
    /** 情景对话：角色扮演式对话练习（英语口语） */
    DIALOGUE,
```

- [ ] **Step 4: 运行测试验证通过**

Run: `cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && mvn test -Dtest=LessonTypeTest`
Expected: PASS

- [ ] **Step 5: 提交**

```bash
cd /Users/dawn/Desktop/english_app
git add backend/src/main/java/com/englishapp/domain/enums/LessonType.java backend/src/test/java/com/englishapp/domain/enums/LessonTypeTest.java
git commit -m "feat(lesson): 新增 PHONICS/DIALOGUE 课时类型枚举

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 2: WordImageResolver 兼容性测试

**Files:**
- Test: `backend/src/test/java/com/englishapp/service/WordImageResolverTest.java`

- [ ] **Step 1: 编写 PHONICS/DIALOGUE 兼容性测试**

在现有 `WordImageResolverTest.java` 中新增两个测试方法：

```java
    /**
     * 验证 PHONICS 类型 content 的 items.image 字段被正确解析为 URL
     */
    @Test
    void should_resolvePhonicsImageUrls_when_contentHasItemsWithImage() {
        String content = "{\"type\":\"PHONICS\",\"letter\":\"F\","
            + "\"items\":[{\"word\":\"fish\",\"image\":\"fish\",\"emoji\":\"🐟\"},"
            + "{\"word\":\"flower\",\"image\":\"\",\"emoji\":\"🌸\"}]}";
        String result = resolver.resolveContent(content);
        // 含 image 的应被解析为完整 URL
        assertTrue(result.contains("/images/words/fish.jpg"));
        // image 为空的不影响 JSON 结构
        assertTrue(result.contains("\"image\":\"\""));
    }

    /**
     * 验证 DIALOGUE 类型 content（无 image 字段）不报错
     */
    @Test
    void should_notFail_when_dialogueContentHasNoImageField() {
        String content = "{\"type\":\"DIALOGUE\",\"scene\":\"shop\","
            + "\"items\":[{\"speaker\":\"Mimi\",\"text\":\"Hello\"}]}";
        String result = resolver.resolveContent(content);
        // 应原样返回，不报错
        assertNotNull(result);
        assertTrue(result.contains("Hello"));
    }
```

- [ ] **Step 2: 运行测试验证通过**

Run: `cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && mvn test -Dtest=WordImageResolverTest`
Expected: PASS（现有解析器已兼容，因为只处理 items[*].image 字段）

- [ ] **Step 3: 提交**

```bash
cd /Users/dawn/Desktop/english_app
git add backend/src/test/java/com/englishapp/service/WordImageResolverTest.java
git commit -m "test(resolver): 新增 PHONICS/DIALOGUE 兼容性测试

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 3: 创建 PhonicsLesson.vue 组件

**Files:**
- Create: `frontend/src/components/lesson-templates/PhonicsLesson.vue`

- [ ] **Step 1: 创建 PhonicsLesson.vue**

组件职责：展示字母 + 发音 + 示例词汇 + 听音选字母练习。

参考 `WordLesson.vue` 的 Props/Emits 接口和样式令牌。

```vue
<!--
  PhonicsLesson.vue - PHONICS 类型课时模板
  用途: 字母发音规则学习，展示大号字母 + 发音 + 示例词汇，
        支持听发音和听音选字母练习。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description PHONICS 类型课时模板，展示字母发音规则并提供词汇学习+练习交互。
 */
import { computed, ref } from 'vue'
import StarBar from '../StarBar.vue'
import AudioButton from '../AudioButton.vue'
import AppButton from '../AppButton.vue'
import mascotCompanion from '../../assets/mascot/mascot-companion.jpg'

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
  recorded: null,
  next: null,
  prev: null,
  answered: null
})

// 当前词汇文本（用于 TTS）
const currentText = computed(() => {
  if (!props.currentItem) return ''
  return props.currentItem.word || ''
})

// 练习模式状态
const isPracticeMode = ref(false)
const selectedOption = ref(null)
const practiceAnswered = ref(false)

// 进度百分比
const progressPercent = computed(() => {
  if (props.totalItems === 0) return 0
  return Math.round(((props.currentIndex + 1) / props.totalItems) * 100)
})

// 生成练习选项（3选1，1个正确字母+2个干扰项）
const practiceOptions = computed(() => {
  if (!props.currentItem) return []
  const letter = props.currentItem.letter || ''
  // 简单的干扰项生成
  const allLetters = 'ABCDEFGHIJKLMNOPRSTW'.split('')
  const wrong = allLetters.filter(l => l !== letter).sort(() => Math.random() - 0.5).slice(0, 2)
  return [letter, ...wrong].sort(() => Math.random() - 0.5)
})

function selectOption(option) {
  if (practiceAnswered.value) return
  selectedOption.value = option
  practiceAnswered.value = true
  const correct = option === props.currentItem.letter
  emit('answered', {
    correct,
    userAnswer: option,
    correctAnswer: props.currentItem.letter
  })
}

function togglePractice() {
  isPracticeMode.value = !isPracticeMode.value
  selectedOption.value = null
  practiceAnswered.value = false
}
</script>

<template>
  <div class="card-area">
    <!-- 进度条 -->
    <div class="progress-section">
      <div class="progress-info">
        <span>第 {{ currentIndex + 1 }} / {{ totalItems }} 项</span>
      </div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
      </div>
    </div>

    <img :src="mascotCompanion" alt="小老鼠陪伴" class="mascot-companion" />

    <!-- 学习模式 -->
    <div v-if="!isPracticeMode" class="item-card">
      <h1 class="big-letter">{{ currentItem.letter }}</h1>
      <p v-if="currentItem.pronunciation" class="pronunciation">/{{ currentItem.pronunciation }}/</p>
      <p v-if="currentItem.sound" class="sound">发音: /{{ currentItem.sound }}/</p>
      <p v-if="currentItem.tip" class="tip">{{ currentItem.tip }}</p>

      <div v-if="currentItem.image" class="word-image-wrapper">
        <img :src="currentItem.image" :alt="currentItem.word" class="word-image" />
      </div>
      <div v-else class="emoji">{{ currentItem.emoji }}</div>

      <h2 class="word">{{ currentItem.word }}</h2>
      <p v-if="currentItem.phonetic" class="phonetic">/{{ currentItem.phonetic }}/</p>
      <p class="translation">{{ currentItem.translation }}</p>

      <div class="actions">
        <AudioButton :text="currentText" :translation="currentItem.translation || ''" />
      </div>
    </div>

    <!-- 练习模式 -->
    <div v-else class="practice-card">
      <h3 class="practice-title">听一听，选出发这个音的字母</h3>
      <div class="options-grid">
        <button
          v-for="opt in practiceOptions"
          :key="opt"
          class="option-btn"
          :class="{
            correct: practiceAnswered && opt === currentItem.letter,
            wrong: selectedOption === opt && opt !== currentItem.letter,
            disabled: practiceAnswered
          }"
          :disabled="practiceAnswered"
          @click="selectOption(opt)"
        >{{ opt }}</button>
      </div>
      <p v-if="practiceAnswered" class="practice-result">
        {{ selectedOption === currentItem.letter ? '回答正确！' : '答错了，正确答案是 ' + currentItem.letter }}
      </p>
    </div>

    <!-- 评分结果区 -->
    <div v-if="currentScore !== null" class="score-area">
      <div class="score-row">
        <span class="score">{{ currentScore }} 分</span>
        <StarBar :stars="currentStars" size="sm" />
      </div>
      <p class="feedback">{{ scoreMessage }}</p>
    </div>

    <!-- 操作按钮 -->
    <div class="action-row">
      <AppButton v-if="currentIndex > 0" variant="ghost" size="md" @click="emit('prev')">← 上一项</AppButton>
      <span v-else class="action-placeholder"></span>
      <AppButton variant="warning" size="md" @click="togglePractice">
        {{ isPracticeMode ? '返回学习' : '去练习' }}
      </AppButton>
      <AppButton variant="primary" size="md" class="action-next" @click="emit('next')">
        {{ isLastItem ? '完成本课' : '下一步 →' }}
      </AppButton>
    </div>
  </div>
</template>

<style scoped>
.card-area { max-width: 480px; margin: 0 auto; position: relative; }
.progress-section { margin-bottom: var(--space-5); }
.progress-info { text-align: center; color: var(--text-tertiary); font-size: var(--text-sm); margin-bottom: var(--space-2); }
.progress-bar { height: 8px; background: var(--bg-card); border-radius: var(--radius-pill); overflow: hidden; box-shadow: var(--shadow-soft); }
.progress-fill { height: 100%; background: var(--gradient-primary); border-radius: var(--radius-pill); transition: width var(--duration-slow) var(--ease-smooth); }
.mascot-companion { position: absolute; top: 0; right: 0; width: 80px; height: 80px; object-fit: contain; border-radius: var(--radius-md); z-index: 1; opacity: 0.9; }
.item-card { background: var(--bg-card); border-radius: var(--radius-lg); padding: var(--space-8) var(--space-6); text-align: center; margin-bottom: var(--space-6); box-shadow: var(--shadow-card); }
.big-letter { font-size: 6rem; font-weight: var(--font-bold); color: var(--color-primary); line-height: 1; margin-bottom: var(--space-3); }
.pronunciation { color: var(--text-secondary); font-size: var(--text-lg); margin-bottom: var(--space-2); }
.sound { color: var(--color-orange); font-size: var(--text-base); margin-bottom: var(--space-3); }
.tip { color: var(--text-tertiary); font-size: var(--text-sm); margin-bottom: var(--space-4); padding: var(--space-2) var(--space-3); background: var(--bg-page); border-radius: var(--radius-md); }
.emoji { font-size: 5rem; margin-bottom: var(--space-4); }
.word-image-wrapper { margin-bottom: var(--space-4); }
.word-image { width: 180px; height: 180px; object-fit: cover; border-radius: var(--radius-lg); box-shadow: var(--shadow-soft); }
.word { font-size: 2.5rem; margin-bottom: var(--space-2); color: var(--text-primary); font-weight: var(--font-bold); }
.phonetic { color: var(--text-secondary); font-size: var(--text-lg); margin-bottom: var(--space-2); }
.translation { color: var(--text-secondary); font-size: var(--text-base); }
.actions { display: flex; gap: var(--space-4); justify-content: center; margin-bottom: var(--space-4); }
.practice-card { background: var(--bg-card); border-radius: var(--radius-lg); padding: var(--space-8) var(--space-6); text-align: center; margin-bottom: var(--space-6); box-shadow: var(--shadow-card); }
.practice-title { font-size: var(--text-lg); color: var(--text-primary); margin-bottom: var(--space-6); }
.options-grid { display: flex; gap: var(--space-4); justify-content: center; margin-bottom: var(--space-4); }
.option-btn { width: 80px; height: 80px; font-size: 2rem; font-weight: var(--font-bold); border: 3px solid var(--border-light); border-radius: var(--radius-lg); background: var(--bg-card); color: var(--text-primary); cursor: pointer; transition: all var(--duration-fast) var(--ease-bounce); }
.option-btn:hover:not(.disabled) { border-color: var(--color-primary); transform: scale(1.05); }
.option-btn.correct { border-color: var(--color-success); background: var(--color-success); color: white; }
.option-btn.wrong { border-color: var(--color-warning); background: var(--color-warning); color: white; }
.option-btn.disabled { cursor: not-allowed; opacity: 0.6; }
.practice-result { font-size: var(--text-base); color: var(--text-secondary); margin-top: var(--space-3); }
.score-area { text-align: center; background: var(--bg-card); border-radius: var(--radius-md); padding: var(--space-4); margin-bottom: var(--space-6); box-shadow: var(--shadow-soft); }
.score-row { display: flex; align-items: center; justify-content: center; gap: var(--space-3); margin-bottom: var(--space-2); }
.score { font-size: 2rem; font-weight: var(--font-bold); color: var(--color-orange); }
.feedback { color: var(--text-secondary); font-size: var(--text-sm); }
.action-row { display: flex; align-items: center; justify-content: space-between; gap: var(--space-3); }
.action-placeholder { flex: 1; }
.action-next { flex: 1; }
@media (prefers-reduced-motion: no-preference) {
  .score { animation: scoreBounce var(--duration-normal) var(--ease-bounce) both; }
  .big-letter { animation: letterPop var(--duration-slow) var(--ease-bounce) both; }
}
@keyframes scoreBounce { 0% { transform: translateY(8px) scale(0.8); opacity: 0; } 60% { transform: translateY(-4px) scale(1.1); opacity: 1; } 100% { transform: translateY(0) scale(1); opacity: 1; } }
@keyframes letterPop { 0% { transform: scale(0.5); opacity: 0; } 60% { transform: scale(1.2); opacity: 1; } 100% { transform: scale(1); opacity: 1; } }
@media (max-width: 480px) { .mascot-companion { width: 64px; height: 64px; } .big-letter { font-size: 4rem; } .word { font-size: 2rem; } .emoji { font-size: 4rem; } .option-btn { width: 64px; height: 64px; font-size: 1.5rem; } }
</style>
```

- [ ] **Step 2: 提交**

```bash
cd /Users/dawn/Desktop/english_app
git add frontend/src/components/lesson-templates/PhonicsLesson.vue
git commit -m "feat(frontend): 新增 PhonicsLesson 模板组件

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 4: 创建 DialogueLesson.vue 组件

**Files:**
- Create: `frontend/src/components/lesson-templates/DialogueLesson.vue`

- [ ] **Step 1: 创建 DialogueLesson.vue**

组件职责：展示场景对话气泡 + 逐句 TTS 播放 + 跟读评分。

```vue
<!--
  DialogueLesson.vue - DIALOGUE 类型课时模板
  用途: 情景对话学习，展示聊天气泡式对话，支持逐句播放和角色扮演跟读。
  作者: english-app
  创建日期: 2026-07-21
-->
<script setup>
/**
 * @description DIALOGUE 类型课时模板，展示对话气泡并提供逐句播放+跟读评分交互。
 */
import { computed, ref } from 'vue'
import StarBar from '../StarBar.vue'
import AudioButton from '../AudioButton.vue'
import RecordButton from '../RecordButton.vue'
import AppButton from '../AppButton.vue'
import mascotCompanion from '../../assets/mascot/mascot-companion.jpg'

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
  recorded: null,
  next: null,
  prev: null
})

// 当前台词文本（用于 TTS 和跟读）
const currentText = computed(() => {
  if (!props.currentItem) return ''
  return props.currentItem.text || ''
})

// 是否为 Mimi 发言（左侧）
const isMimi = computed(() => {
  return props.currentItem?.speaker === 'Mimi'
})

// 进度百分比
const progressPercent = computed(() => {
  if (props.totalItems === 0) return 0
  return Math.round(((props.currentIndex + 1) / props.totalItems) * 100)
})
</script>

<template>
  <div class="card-area">
    <!-- 进度条 -->
    <div class="progress-section">
      <div class="progress-info">
        <span>第 {{ currentIndex + 1 }} / {{ totalItems }} 句</span>
      </div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
      </div>
    </div>

    <img :src="mascotCompanion" alt="小老鼠陪伴" class="mascot-companion" />

    <!-- 对话气泡 -->
    <div class="dialogue-card" :class="{ 'mimi-side': isMimi, 'npc-side': !isMimi }">
      <!-- Mimi 发言：左侧 + 头像 -->
      <div v-if="isMimi" class="avatar-wrapper">
        <img :src="mascotCompanion" alt="Mimi" class="avatar" />
        <span class="speaker-name">Mimi</span>
      </div>
      <div class="bubble">
        <p class="dialogue-text">{{ currentItem.text }}</p>
        <p class="dialogue-translation">{{ currentItem.translation }}</p>
      </div>
      <!-- NPC 发言：右侧 + 文字标签 -->
      <div v-if="!isMimi" class="avatar-wrapper npc">
        <span class="speaker-badge">{{ currentItem.speaker }}</span>
      </div>
    </div>

    <!-- 操作区: 听 + 跟读 -->
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

    <!-- 上一步 / 下一步 -->
    <div class="action-row">
      <AppButton v-if="currentIndex > 0" variant="ghost" size="md" @click="emit('prev')">← 上一句</AppButton>
      <span v-else class="action-placeholder"></span>
      <AppButton variant="primary" size="md" class="action-next" @click="emit('next')">
        {{ isLastItem ? '完成本课' : '下一句 →' }}
      </AppButton>
    </div>
  </div>
</template>

<style scoped>
.card-area { max-width: 480px; margin: 0 auto; position: relative; }
.progress-section { margin-bottom: var(--space-5); }
.progress-info { text-align: center; color: var(--text-tertiary); font-size: var(--text-sm); margin-bottom: var(--space-2); }
.progress-bar { height: 8px; background: var(--bg-card); border-radius: var(--radius-pill); overflow: hidden; box-shadow: var(--shadow-soft); }
.progress-fill { height: 100%; background: var(--gradient-primary); border-radius: var(--radius-pill); transition: width var(--duration-slow) var(--ease-smooth); }
.mascot-companion { position: absolute; top: 0; right: 0; width: 80px; height: 80px; object-fit: contain; border-radius: var(--radius-md); z-index: 1; opacity: 0.9; }
.dialogue-card { display: flex; align-items: flex-start; gap: var(--space-3); margin-bottom: var(--space-6); }
.dialogue-card.mimi-side { flex-direction: row; }
.dialogue-card.npc-side { flex-direction: row-reverse; }
.avatar-wrapper { display: flex; flex-direction: column; align-items: center; gap: var(--space-1); flex-shrink: 0; }
.avatar { width: 56px; height: 56px; border-radius: var(--radius-pill); object-fit: cover; box-shadow: var(--shadow-soft); }
.speaker-name { font-size: var(--text-xs); color: var(--text-tertiary); }
.speaker-badge { padding: var(--space-1) var(--space-2); background: var(--color-primary); color: white; border-radius: var(--radius-pill); font-size: var(--text-xs); font-weight: var(--font-bold); }
.bubble { background: var(--bg-card); border-radius: var(--radius-lg); padding: var(--space-4) var(--space-5); box-shadow: var(--shadow-card); max-width: 70%; }
.npc-side .bubble { background: var(--color-primary); color: white; }
.dialogue-text { font-size: var(--text-lg); font-weight: var(--font-bold); margin-bottom: var(--space-2); word-break: break-word; }
.dialogue-translation { font-size: var(--text-sm); opacity: 0.8; }
.actions { display: flex; gap: var(--space-4); justify-content: center; margin-bottom: var(--space-6); }
.score-area { text-align: center; background: var(--bg-card); border-radius: var(--radius-md); padding: var(--space-4); margin-bottom: var(--space-6); box-shadow: var(--shadow-soft); }
.score-row { display: flex; align-items: center; justify-content: center; gap: var(--space-3); margin-bottom: var(--space-2); }
.score { font-size: 2rem; font-weight: var(--font-bold); color: var(--color-orange); }
.feedback { color: var(--text-secondary); font-size: var(--text-sm); }
.action-row { display: flex; align-items: center; justify-content: space-between; gap: var(--space-3); }
.action-placeholder { flex: 1; }
.action-next { flex: 1; }
.score-area.scoring { display: flex; align-items: center; justify-content: center; gap: var(--space-3); }
.scoring-dot { width: 16px; height: 16px; border: 2px solid var(--border-light); border-top-color: var(--color-orange); border-radius: var(--radius-pill); }
@media (prefers-reduced-motion: no-preference) {
  .score { animation: scoreBounce var(--duration-normal) var(--ease-bounce) both; }
  .bubble { animation: bubblePop var(--duration-normal) var(--ease-bounce) both; }
}
@keyframes scoreBounce { 0% { transform: translateY(8px) scale(0.8); opacity: 0; } 60% { transform: translateY(-4px) scale(1.1); opacity: 1; } 100% { transform: translateY(0) scale(1); opacity: 1; } }
@keyframes bubblePop { 0% { transform: scale(0.8); opacity: 0; } 60% { transform: scale(1.05); opacity: 1; } 100% { transform: scale(1); opacity: 1; } }
@keyframes spin { to { transform: rotate(360deg); } }
@media (max-width: 480px) { .mascot-companion { width: 64px; height: 64px; } .avatar { width: 48px; height: 48px; } .bubble { max-width: 75%; padding: var(--space-3) var(--space-4); } .dialogue-text { font-size: var(--text-base); } }
</style>
```

- [ ] **Step 2: 提交**

```bash
cd /Users/dawn/Desktop/english_app
git add frontend/src/components/lesson-templates/DialogueLesson.vue
git commit -m "feat(frontend): 新增 DialogueLesson 模板组件

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 5: LessonView.vue 添加 PHONICS/DIALOGUE 分发

**Files:**
- Modify: `frontend/src/views/LessonView.vue`

- [ ] **Step 1: 在 import 区域新增两个组件导入**

在 `import CalculateLesson` 之后添加：

```javascript
import PhonicsLesson from '../components/lesson-templates/PhonicsLesson.vue'
import DialogueLesson from '../components/lesson-templates/DialogueLesson.vue'
```

- [ ] **Step 2: 在 lessonTemplate 的 switch 中新增两个 case**

在 `case 'CALCULATE': return CalculateLesson` 之后、`default` 之前添加：

```javascript
    case 'PHONICS':
      return PhonicsLesson
    case 'DIALOGUE':
      return DialogueLesson
```

- [ ] **Step 3: 提交**

```bash
cd /Users/dawn/Desktop/english_app
git add frontend/src/views/LessonView.vue
git commit -m "feat(frontend): LessonView 添加 PHONICS/DIALOGUE 分发

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 6: 下载/生成图片资源

**Files:**
- Create: `backend/src/main/resources/static/images/rubble-crew/` 目录及图片
- Create: `backend/src/main/resources/static/images/paw-patrol/` 补充图片

- [ ] **Step 1: 创建 rubble-crew 图片目录**

```bash
mkdir -p /Users/dawn/Desktop/english_app/backend/src/main/resources/static/images/rubble-crew
```

- [ ] **Step 2: 为小砾工程家族 7 位角色生成图片**

使用 GenerateImage 工具为以下角色生成图片（已有 Rubble 图，跳过）：
- Grandpa Gravel（格雷维尔爷爷）
- Auntie Crane（鹤姑姑）
- Mix（米克斯）
- Wheeler（惠勒）
- Charger（充电器）
- Motor（摩托）
- Speed Meister（极速迈斯特，反派）

图片风格：扁平卡通，白底，儿童友好，与现有 paw-patrol 角色图风格一致。

保存到 `backend/src/main/resources/static/images/rubble-crew/`。

- [ ] **Step 3: 为汪汪队补充角色生成图片**

为以下角色生成图片（已有 6 只原班狗狗，跳过）：
- Everest（珠珠）
- Tracker（小克）
- Mayor Humdinger（韩丁纳市长，反派）
- Sweetie（甜甜）
- Ryder（莱德队长）

保存到 `backend/src/main/resources/static/images/paw-patrol/`。

- [ ] **Step 4: 提交**

```bash
cd /Users/dawn/Desktop/english_app
git add backend/src/main/resources/static/images/
git commit -m "feat(images): 新增小砾工程家族和汪汪队补充角色图片

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 7: 编写 Flyway V18 迁移文件

**Files:**
- Create: `backend/src/main/resources/db/migration/V18__expand_english_lessons.sql`

这是最大的任务，分 3 个子步骤完成。

- [ ] **Step 1: 编写 UPDATE 语句（汪汪队 + 小砾工程现有课时改用官方角色）**

汪汪队主题 Lesson 22-31 已用官方角色名，主要补充 image 字段和丰富内容。
小砾工程主题 Lesson 35-37 需从原创角色替换为官方角色。

```sql
-- ============================================================
-- V18: 英语课时扩展（36→60课）
-- 1. UPDATE: 汪汪队主题 Lesson 22-31 补充 image 字段
-- 2. UPDATE: 小砾工程 Lesson 35-37 改用官方角色
-- 3. INSERT: 4 主题各新增 6 课，共 24 课
-- ============================================================

-- ========== Part 1: UPDATE 汪汪队主题 image 字段 ==========
-- 使用 json_set 精确设置 image 字段（避免 V9 的 REPLACE 教训）

-- Lesson 22: 认识狗狗 - 补充 image
UPDATE lesson SET content = json_set(content, '$.items[0].image', 'paw-patrol/chase') WHERE id = 22;
UPDATE lesson SET content = json_set(content, '$.items[1].image', 'paw-patrol/marshall') WHERE id = 22;
UPDATE lesson SET content = json_set(content, '$.items[2].image', 'paw-patrol/skye') WHERE id = 22;
UPDATE lesson SET content = json_set(content, '$.items[3].image', 'paw-patrol/rubble') WHERE id = 22;
UPDATE lesson SET content = json_set(content, '$.items[4].image', 'paw-patrol/zuma') WHERE id = 22;
UPDATE lesson SET content = json_set(content, '$.items[5].image', 'paw-patrol/rocky') WHERE id = 22;

-- Lesson 23-31: 同理补充 image（按需添加）
-- ...（根据实际 content items 逐个 json_set）

-- ========== Part 2: UPDATE 小砾工程 Lesson 35-37 改用官方角色 ==========
-- Lesson 35: 认识工程家族（替换为官方 7 角色）
UPDATE lesson SET content = '{"type":"WORD","items":[{"word":"Rubble","phonetic":"/ˈrʌbəl/","translation":"小砾(工程队长)","emoji":"🐶","image":"rubble-crew/rubble"},{"word":"Grandpa Gravel","phonetic":"/ˈɡrævəl/","translation":"格雷维尔爷爷","emoji":"👴","image":"rubble-crew/grandpa-gravel"},{"word":"Auntie Crane","phonetic":"/kreɪn/","translation":"鹤姑姑","emoji":"🏗️","image":"rubble-crew/auntie-crane"},{"word":"Mix","phonetic":"/mɪks/","translation":"米克斯(材料专家)","emoji":"🎨","image":"rubble-crew/mix"},{"word":"Wheeler","phonetic":"/ˈwiːlər/","translation":"惠勒(清洁专家)","emoji":"🧹","image":"rubble-crew/wheeler"},{"word":"Charger","phonetic":"/ˈtʃɑːrdʒər/","translation":"充电器(能量专家)","emoji":"⚡","image":"rubble-crew/charger"},{"word":"Motor","phonetic":"/ˈmoʊtər/","translation":"摩托(拆除专家)","emoji":"💥","image":"rubble-crew/motor"},{"word":"Speed Meister","phonetic":"/spiːd ˈmaɪstər/","translation":"极速迈斯特(捣蛋鬼)","emoji":"🏎️","image":"rubble-crew/speed-meister"}]}' WHERE id = 35;

-- Lesson 36: 家族的车辆
UPDATE lesson SET content = '{"type":"WORD","items":[{"word":"bulldozer","phonetic":"/ˈbʊldoʊzər/","translation":"推土机","emoji":"🚧"},{"word":"crane","phonetic":"/kreɪn/","translation":"起重机","emoji":"🏗️"},{"word":"cement mixer","phonetic":"/sɪˈment ˈmɪksər/","translation":"搅拌机","emoji":"🪨"},{"word":"cleaner truck","phonetic":"/ˈkliːnər trʌk/","translation":"清洁车","emoji":"🧹"},{"word":"forklift","phonetic":"/ˈfɔːrklɪft/","translation":"叉车","emoji":"📦"},{"word":"demolition vehicle","phonetic":"/ˌdeməˈlɪʃən ˈviːəkəl/","translation":"拆除车","emoji":"💥"},{"word":"food truck","phonetic":"/fuːd trʌk/","translation":"餐车","emoji":"🚚"},{"word":"race car","phonetic":"/reɪs kɑːr/","translation":"赛车","emoji":"🏎️"}]}' WHERE id = 36;

-- Lesson 37: 一起建大桥（围绕官方角色）
UPDATE lesson SET content = '{"type":"SENTENCE","items":[{"sentence":"Rubble is the leader.","translation":"小砾是队长。","emoji":"🐶"},{"sentence":"Mix makes the cement.","translation":"米克斯做水泥。","emoji":"🎨"},{"sentence":"Crane lifts the heavy parts.","translation":"鹤姑姑吊起重物。","emoji":"🏗️"},{"sentence":"Wheeler keeps the site clean.","translation":"惠勒保持工地整洁。","emoji":"🧹"},{"sentence":"The family builds a bridge.","translation":"家族建了一座桥。","emoji":"🌉"}]}' WHERE id = 37;
```

- [ ] **Step 2: 编写 INSERT 语句（24 节新课）**

新增 4 主题各 6 课。Lesson ID 从 110 开始（现有最大 109）。

```sql
-- ========== Part 3: INSERT 24 节新课 ==========

-- === 水果乐园 (Unit 1-3) 新增 6 课 ===
-- Lesson 110: 更多浆果 (WORD)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(110, 1, '更多浆果', 'WORD', '{"type":"WORD","items":[{"word":"blueberry","phonetic":"/ˈbluːberi/","translation":"蓝莓","emoji":"🫐"},{"word":"strawberry","phonetic":"/ˈstrɔːberi/","translation":"草莓","emoji":"🍓"},{"word":"raspberry","phonetic":"/ˈræzberi/","translation":"树莓","emoji":"🟥"},{"word":"blackberry","phonetic":"/ˈblækberi/","translation":"黑莓","emoji":"⚫"},{"word":"cranberry","phonetic":"/ˈkrænberi/","translation":"蔓越莓","emoji":"🔴"},{"word":"gooseberry","phonetic":"/ˈɡʊsberi/","translation":"鹅莓","emoji":"🟢"}]}', 5, 3);

-- Lesson 111: 热带水果 (WORD)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(111, 2, '热带水果', 'WORD', '{"type":"WORD","items":[{"word":"mango","phonetic":"/ˈmæŋɡoʊ/","translation":"芒果","emoji":"🥭"},{"word":"pineapple","phonetic":"/ˈpaɪnæpəl/","translation":"菠萝","emoji":"🍍"},{"word":"coconut","phonetic":"/ˈkoʊkənʌt/","translation":"椰子","emoji":"🥥"},{"word":"papaya","phonetic":"/pəˈpaɪə/","translation":"木瓜","emoji":"🟠"},{"word":"dragon fruit","phonetic":"/ˈdræɡən fruːt/","translation":"火龙果","emoji":"🐉"},{"word":"lychee","phonetic":"/ˈlaɪtʃi/","translation":"荔枝","emoji":"⚪"}]}', 4, 3);

-- Lesson 112: 表达喜欢 (SENTENCE)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(112, 3, '表达喜欢', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"I like apples.","translation":"我喜欢苹果。","emoji":"🍎"},{"sentence":"She loves bananas.","translation":"她爱香蕉。","emoji":"🍌"},{"sentence":"He wants a mango.","translation":"他想要芒果。","emoji":"🥭"},{"sentence":"We eat grapes together.","translation":"我们一起吃葡萄。","emoji":"🍇"},{"sentence":"They enjoy pineapple.","translation":"他们喜欢菠萝。","emoji":"🍍"}]}', 4, 3);

-- Lesson 113: 字母 F 发音 (PHONICS)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(113, 1, '字母F发音', 'PHONICS', '{"type":"PHONICS","letter":"F","pronunciation":"/ef/","sound":"/f/","tip":"F 开头单词常发 /f/ 音，上牙咬下唇","items":[{"word":"fruit","phonetic":"/fruːt/","translation":"水果","emoji":"🍎","image":""},{"word":"flower","phonetic":"/ˈflaʊər/","translation":"花","emoji":"🌸","image":""},{"word":"fish","phonetic":"/fɪʃ/","translation":"鱼","emoji":"🐟","image":""}]}', 6, 3);

-- Lesson 114: 水果店对话 (DIALOGUE)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(114, 2, '水果店对话', 'DIALOGUE', '{"type":"DIALOGUE","scene":"水果店","tip":"学习购买水果的简单对话","items":[{"speaker":"Mimi","text":"Hello! I want an apple.","translation":"你好！我想要一个苹果。","audio":""},{"speaker":"Seller","text":"Here you are. Do you like bananas?","translation":"给你。你喜欢香蕉吗？","audio":""},{"speaker":"Mimi","text":"Yes, I like bananas. Thank you!","translation":"是的，我喜欢香蕉。谢谢！","audio":""}]}', 5, 3);

-- === 交通工具 (Unit 4-6) 新增 6 课 ===
-- Lesson 115: 特种车辆 (WORD)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(115, 4, '特种车辆', 'WORD', '{"type":"WORD","items":[{"word":"ambulance","phonetic":"/ˈæmbjələns/","translation":"救护车","emoji":"🚑"},{"word":"fire truck","phonetic":"/ˈfaɪər trʌk/","translation":"消防车","emoji":"🚒"},{"word":"police car","phonetic":"/pəˈliːs kɑːr/","translation":"警车","emoji":"🚓"},{"word":"garbage truck","phonetic":"/ˈɡɑːrbɪdʒ trʌk/","translation":"垃圾车","emoji":"🗑️"},{"word":"tow truck","phonetic":"/toʊ trʌk/","translation":"拖车","emoji":"🚛"},{"word":"taxi","phonetic":"/ˈtæksi/","translation":"出租车","emoji":"🚕"}]}', 5, 3);

-- Lesson 116: 出行方式 (SENTENCE)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(116, 5, '出行方式', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"I go by bus.","translation":"我坐公交去。","emoji":"🚌"},{"sentence":"She goes by train.","translation":"她坐火车去。","emoji":"🚂"},{"sentence":"He flies by plane.","translation":"他坐飞机去。","emoji":"✈️"},{"sentence":"We ride bikes.","translation":"我们骑车。","emoji":"🚲"},{"sentence":"They take a taxi.","translation":"他们打车。","emoji":"🚕"}]}', 4, 3);

-- Lesson 117: 字母 C 发音 (PHONICS)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(117, 4, '字母C发音', 'PHONICS', '{"type":"PHONICS","letter":"C","pronunciation":"/siː/","sound":"/k/","tip":"C 开头单词常发 /k/ 音","items":[{"word":"car","phonetic":"/kɑːr/","translation":"小汽车","emoji":"🚗","image":""},{"word":"bus","phonetic":"/bʌs/","translation":"公交车","emoji":"🚌","image":""},{"word":"cycle","phonetic":"/ˈsaɪkəl/","translation":"骑行","emoji":"🚲","image":""}]}', 6, 3);

-- Lesson 118: 车站问路 (DIALOGUE)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(118, 5, '车站问路', 'DIALOGUE', '{"type":"DIALOGUE","scene":"火车站","tip":"学习在车站问路的对话","items":[{"speaker":"Mimi","text":"Where is the train?","translation":"火车在哪里？","audio":""},{"speaker":"Conductor","text":"The train is at platform 2.","translation":"火车在2号站台。","audio":""},{"speaker":"Mimi","text":"Thank you!","translation":"谢谢！","audio":""}]}', 5, 3);

-- === 汪汪队 (Unit 7-9) 新增 6 课 ===
-- Lesson 119: 救援口令 (SENTENCE)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(119, 7, '救援口令', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"Ryder needs us!","translation":"莱德需要我们！","emoji":"👦"},{"sentence":"Chase is on the case!","translation":"阿奇出动！","emoji":"🐕"},{"sentence":"Ready for action!","translation":"准备行动！","emoji":"⚡"},{"sentence":"No job is too big, no pup is too small!","translation":"没有完不成的任务，没有太小的小狗！","emoji":"🐶"},{"sentence":"Paw Patrol is on a roll!","translation":"汪汪队出发！","emoji":"🚀"}]}', 5, 3);

-- Lesson 120: 帮助别人 (SENTENCE)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(120, 8, '帮助别人', 'SENTENCE', '{"type":"SENTENCE","items":[{"sentence":"I can help you.","translation":"我能帮助你。","emoji":"🤝"},{"sentence":"Marshall saves the day.","translation":"毛毛拯救了大家。","emoji":"🔥"},{"sentence":"Skye flies to the rescue.","translation":"天天飞去救援。","emoji":"🚁"},{"sentence":"The team works together.","translation":"队伍齐心协力。","emoji":"🦮"},{"sentence":"Everyone is happy.","translation":"大家都很开心。","emoji":"😊"}]}', 4, 3);

-- Lesson 121: 角色辨认 (QUIZ)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(121, 9, '角色辨认', 'QUIZ', '{"type":"QUIZ","items":[{"question":"Who is the fire dog?","options":["Marshall","Chase","Skye"],"answer":0,"image":""},{"question":"Who can fly?","options":["Rubble","Skye","Zuma"],"answer":1,"image":""},{"question":"Who is the police dog?","options":["Rocky","Zuma","Chase"],"answer":2,"image":""},{"question":"Who loves recycling?","options":["Rocky","Marshall","Rubble"],"answer":0,"image":""}]}', 4, 3);

-- Lesson 122: 字母 M 发音 (PHONICS)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(122, 7, '字母M发音', 'PHONICS', '{"type":"PHONICS","letter":"M","pronunciation":"/em/","sound":"/m/","tip":"M 开头单词常发 /m/ 音，双唇紧闭","items":[{"word":"Marshall","phonetic":"/ˈmɑːrʃəl/","translation":"毛毛","emoji":"🔥","image":""},{"word":"Mayor","phonetic":"/ˈmeɪər/","translation":"市长","emoji":"👨‍💼","image":""},{"word":"mountain","phonetic":"/ˈmaʊntən/","translation":"山","emoji":"⛰️","image":""}]}', 6, 3);

-- Lesson 123: 救援对话 (DIALOGUE)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(123, 8, '救援对话', 'DIALOGUE', '{"type":"DIALOGUE","scene":"冒险湾","tip":"学习救援场景的对话","items":[{"speaker":"Ryder","text":"Paw Patrol, to the Lookout!","translation":"汪汪队，集合！","audio":""},{"speaker":"Chase","text":"Chase is on the case!","translation":"阿奇出动！","audio":""},{"speaker":"Mimi","text":"Wow, they are so brave!","translation："哇，他们好勇敢！","audio":""}]}', 5, 3);

-- Lesson 124: 团队故事 (READING)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(124, 9, '团队故事', 'READING', '{"type":"READING","items":[{"title":"The Great Rescue","content":"One day in Adventure Bay, a kitten was stuck on a tree. Ryder called the Paw Patrol. Chase came first. Marshall brought the ladder. Skye flew up to the kitten. Together they saved the kitten. Everyone cheered!","image":""}]}', 5, 3);

-- === 小砾工程 (Unit 10-11) 新增 6 课 ===
-- Lesson 125: 家族成员登场 (WORD)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(125, 11, '家族成员登场', 'WORD', '{"type":"WORD","items":[{"word":"Rubble","phonetic":"/ˈrʌbəl/","translation":"小砾(队长)","emoji":"🐶","image":"rubble-crew/rubble"},{"word":"Mix","phonetic":"/mɪks/","translation":"米克斯(材料)","emoji":"🎨","image":"rubble-crew/mix"},{"word":"Wheeler","phonetic":"/ˈwiːlər/","translation":"惠勒(清洁)","emoji":"🧹","image":"rubble-crew/wheeler"},{"word":"Charger","phonetic":"/ˈtʃɑːrdʒər/","translation":"充电器(能量)","emoji":"⚡","image":"rubble-crew/charger"},{"word":"Motor","phonetic":"/ˈmoʊtər/","translation":"摩托(拆除)","emoji":"💥","image":"rubble-crew/motor"},{"word":"Crane","phonetic":"/kreɪn/","translation":"鹤姑姑(起重)","emoji":"🏗️","image":"rubble-crew/auntie-crane"}]}', 4, 3);

-- Lesson 126: 家族的车辆 (WORD)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(126, 10, '家族的车辆', 'WORD', '{"type":"WORD","items":[{"word":"bulldozer","phonetic":"/ˈbʊldoʊzər/","translation":"推土机","emoji":"🚧"},{"word":"crane","phonetic":"/kreɪn/","translation":"起重机","emoji":"🏗️"},{"word":"mixer","phonetic":"/ˈmɪksər/","translation":"搅拌机","emoji":"🪨"},{"word":"cleaner","phonetic":"/ˈkliːnər/","translation":"清洁车","emoji":"🧹"},{"word":"forklift","phonetic":"/ˈfɔːrklɪft/","translation":"叉车","emoji":"📦"},{"word":"demolition truck","phonetic":"/ˌdeməˈlɪʃən trʌk/","translation":"拆除车","emoji":"💥"}]}', 4, 3);

-- Lesson 127: 成员找车辆 (QUIZ)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(127, 11, '成员找车辆', 'QUIZ', '{"type":"QUIZ","items":[{"question":"Who drives the crane?","options":["Mix","Crane","Motor"],"answer":1,"image":""},{"question":"Who drives the bulldozer?","options":["Rubble","Wheeler","Charger"],"answer":0,"image":""},{"question":"Who cleans the site?","options":["Motor","Wheeler","Crane"],"answer":1,"image":""}]}', 5, 3);

-- Lesson 128: 字母 W 发音 (PHONICS)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(128, 10, '字母W发音', 'PHONICS', '{"type":"PHONICS","letter":"W","pronunciation":"/ˈdʌbəljuː/","sound":"/w/","tip":"W 开头单词常发 /w/ 音，嘴唇收圆","items":[{"word":"Wheeler","phonetic":"/ˈwiːlər/","translation":"惠勒","emoji":"🧹","image":""},{"word":"wheel","phonetic":"/wiːl/","translation":"轮子","emoji":"⚙️","image":""},{"word":"water","phonetic":"/ˈwɔːtər/","translation":"水","emoji":"💧","image":""}]}', 5, 3);

-- Lesson 129: 工地合作 (DIALOGUE)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(129, 11, '工地合作', 'DIALOGUE', '{"type":"DIALOGUE","scene":"建造者湾工地","tip":"学习工地合作的对话","items":[{"speaker":"Rubble","text":"Let us build a bridge!","translation":"我们一起来建桥！","audio":""},{"speaker":"Mix","text":"I will make the cement.","translation":"我来做水泥。","audio":""},{"speaker":"Speed Meister","text":"I will build it faster!","translation":"我会建得更快！","audio":""},{"speaker":"Mimi","text":"Wow, great teamwork!","translation":"哇，真棒的团队合作！","audio":""}]}', 6, 3);

-- Lesson 130: 建造者湾大工程 (READING)
INSERT INTO lesson (id, unit_id, name, type, content, sort_order, star_reward) VALUES
(130, 10, '建造者湾大工程', 'READING', '{"type":"READING","items":[{"title":"Building a School","content":"In Builder Cove, Rubble and his family built a new school. Mix made strong cement. Crane lifted the heavy walls. Wheeler kept everything clean. Charger had lots of energy. Motor helped demolish the old building. Even Speed Meister tried to help! The school was beautiful. Everyone was happy!","image":""}]}', 6, 3);
```

- [ ] **Step 3: 运行迁移验证**

```bash
cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && ./run.sh
```

验证后端启动时 Flyway 自动执行 V18 迁移，检查日志无报错。

然后调用 API 验证：
- `GET /api/lessons/110` 确认新课可查询
- `GET /api/lessons/35` 确认 content 已更新为官方角色
- `GET /api/lessons/113` 确认 PHONICS 类型课时 content 正确

- [ ] **Step 4: 提交**

```bash
cd /Users/dawn/Desktop/english_app
git add backend/src/main/resources/db/migration/V18__expand_english_lessons.sql
git commit -m "feat(db): V18 迁移 - 英语课时扩展36→60课

- UPDATE 汪汪队/小砾工程 13 课改用官方角色
- INSERT 24 节新课(WORD/SENTENCE/PHONICS/DIALOGUE/QUIZ/READING)

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 8: 后端测试验证

**Files:**
- Test: `backend/src/test/java/com/englishapp/service/LessonServiceTest.java`

- [ ] **Step 1: 新增 LessonService 测试**

在现有 `LessonServiceTest.java` 中新增：

```java
    /**
     * 验证 PHONICS 类型课时详情接口正确返回 content
     */
    @Test
    void should_returnPhonicsContent_when_getLessonByIdForPhonics() {
        // 使用 V18 插入的 Lesson 113
        var dto = lessonService.getLessonById(113L);
        assertNotNull(dto);
        assertEquals("PHONICS", dto.getType());
        assertTrue(dto.getContent().contains("letter"));
    }

    /**
     * 验证 DIALOGUE 类型课时详情接口正确返回 content
     */
    @Test
    void should_returnDialogueContent_when_getLessonByIdForDialogue() {
        // 使用 V18 插入的 Lesson 114
        var dto = lessonService.getLessonById(114L);
        assertNotNull(dto);
        assertEquals("DIALOGUE", dto.getType());
        assertTrue(dto.getContent().contains("speaker"));
    }

    /**
     * 验证小砾工程 Lesson 35 已更新为官方角色
     */
    @Test
    void should_returnOfficialCharacters_when_getLesson35AfterMigration() {
        var dto = lessonService.getLessonById(35L);
        assertNotNull(dto);
        assertTrue(dto.getContent().contains("Rubble"));
        assertTrue(dto.getContent().contains("Mix"));
        assertFalse(dto.getContent().contains("Dozer"));
    }
```

- [ ] **Step 2: 运行测试**

Run: `cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && mvn test -Dtest=LessonServiceTest`
Expected: PASS

- [ ] **Step 3: 提交**

```bash
cd /Users/dawn/Desktop/english_app
git add backend/src/test/java/com/englishapp/service/LessonServiceTest.java
git commit -m "test(lesson): 新增 PHONICS/DIALOGUE 课时服务测试

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Task 9: 前端手动验证

- [ ] **Step 1: 启动后端**

```bash
cd backend && export JAVA_HOME=/Users/dawn/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && ./run.sh
```

- [ ] **Step 2: 启动前端**

```bash
cd frontend && npm run dev
```

- [ ] **Step 3: 验证清单**

在浏览器中验证以下内容：

1. **课时总数**：英语学科应有 60 课时
2. **PHONICS 课**：访问 Lesson 113，确认字母 F 展示、词汇卡片、练习模式正常
3. **DIALOGUE 课**：访问 Lesson 114，确认对话气泡、Mimi 头像、NPC 标签正常
4. **汪汪队课**：访问 Lesson 22，确认官方角色图片显示
5. **小砾工程课**：访问 Lesson 35，确认官方角色（Rubble/Mix 等），无原创角色（Dozer 等）
6. **现有课不受影响**：访问 Lesson 1（水果 WORD），确认正常
7. **完成课时**：完成一节 PHONICS 课，确认星星和进度保存

- [ ] **Step 4: 最终提交**

```bash
cd /Users/dawn/Desktop/english_app
git add -A
git commit -m "feat(lesson): 英语课时扩展完成（36→60课）

- 新增 PHONICS/DIALOGUE 两种课型
- 汪汪队/小砾工程改用官方角色
- 4 主题各新增 6 课

Co-Authored-By: TRAE Agent <noreply@trae.ai>"
```

---

## Self-Review 结果

**1. Spec coverage:**
- ✅ LessonType 枚举扩展 → Task 1
- ✅ PHONICS content 结构 → Task 7 (V18 SQL)
- ✅ DIALOGUE content 结构 → Task 7 (V18 SQL)
- ✅ WordImageResolver 兼容性 → Task 2
- ✅ Flyway V18 迁移 → Task 7
- ✅ PhonicsLesson.vue → Task 3
- ✅ DialogueLesson.vue → Task 4
- ✅ LessonView.vue 分发 → Task 5
- ✅ 图片获取 → Task 6
- ✅ 后端测试 → Task 8
- ✅ 前端验证 → Task 9

**2. Placeholder scan:** Task 7 Step 1 中 Lesson 23-31 的 image json_set 标注"按需添加"，实施时需逐条编写完整 SQL。其余无占位符。

**3. Type consistency:** PhonicsLesson/DialogueLesson 的 Props 接口与 WordLesson 一致（currentItem/currentIndex/totalItems 等），Emits 一致（recorded/next/prev/answered）。LessonView 的 switch case 与枚举值大小写一致。
