<!--
  ModernLessonPage.vue - 新版课时页
  用途: 新版展示层；课程业务由 useLessonLearning 管理，此页保留头部和学习摘要。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import { computed } from 'vue'
import { useLessonLearning } from '../../../composables/useLessonLearning'
import StarBar from '../../StarBar.vue'
import BackBar from '../../BackBar.vue'
import AppButton from '../../AppButton.vue'
import MascotFeedback from '../../MascotFeedback.vue'
import LessonComplete from '../../lesson-templates/LessonComplete.vue'
import LessonReview from '../../lesson-templates/LessonReview.vue'
import { getLessonTypeText } from './themeUnitModernShared'

const { lesson, isLoading, errorMsg, currentIndex, currentScore, currentStars, scoreMessage,
  isScoring, isComplete, showReview, isSubmitting, saveError, mascotFeedback, currentItem,
  currentText, totalItems, isLastItem, currentItemEngaged, totalBestScore, totalStars, lessonTemplate,
  lessonTemplateProps, loadLesson, handleRecorded, handleAnswered, handleListened, skipCurrentItem,
  nextItem, prevItem, advanceContinuousPlayback, handleReviewPassed, clearContinuousQuery, goBack, finishLesson } = useLessonLearning()

const lessonTypeText = computed(() => currentItem.value?.recognition ? '汉字认读' : getLessonTypeText(lesson.value?.type))
const displayItemIndex = computed(() => {
  if (isComplete.value) return totalItems.value || 0
  return Math.min(currentIndex.value + 1, totalItems.value || 0)
})
const heroProgressPercent = computed(() => {
  if (!totalItems.value) return 0
  return Math.round((displayItemIndex.value / totalItems.value) * 100)
})
const brandAccent = computed(() => (isComplete.value ? 'var(--color-success)' : 'var(--color-primary)'))
const heroScoreText = computed(() => {
  if (isComplete.value) return `总分 ${totalBestScore.value} 分`
  if (currentScore.value !== null) return `当前 ${currentScore.value} 分`
  return currentItemEngaged.value ? '已开始练习' : '等待开始'
})
const heroHint = computed(() => {
  if (isComplete.value) {
    return `本课已完成，累计获得 ${totalStars.value} 颗星，准备返回主题继续前进。`
  }

  if (currentItem.value?.recognition) return '看图认识，再收起提示找字；独立认对才会积累认字天数。'
  const currentLabel = currentText.value || '当前学习项'
  const guideMap = {
    WORD: `先听“${currentLabel}”，再跟读拿到更高分。`,
    SENTENCE: `先理解句子，再完整开口说出“${currentLabel}”。`,
    READING: '先阅读内容，再继续朗读或进入连续播放。',
    QUIZ: '先看清题目，再选择答案或根据提示继续挑战。',
    CALCULATE: '先思考题目，再尝试把答案一步步做出来。',
    PHONICS: '先听字母音，再跟着练习拼读和辨音。',
    DIALOGUE: '先听对话，再用自己的声音把句子说完整。'
  }

  return guideMap[lesson.value?.type] || '继续当前学习任务。'
})
const stageTitle = computed(() => {
  if (!currentItem.value) return '当前学习任务'
  if (currentItem.value.recognition) return '汉字认读练习'
  return currentText.value || currentItem.value.translation || currentItem.value.question || '当前学习任务'
})
const stageCaption = computed(() => {
  if (isComplete.value) return '本课已经完成'
  if (scoreMessage.value) return scoreMessage.value
  return heroHint.value
})

</script>

<template>
  <main class="modern-lesson modern-page-shell modern-page-shell--top-spaced" :style="{ '--brand-accent': brandAccent }">
    <BackBar :title="currentItem?.recognition ? '认字小课堂' : (lesson?.name || '学习中')" @back="goBack">
      <template #right>
        <div class="lesson-bar-right">
          <span class="bar-chip">{{ displayItemIndex }} / {{ totalItems || 0 }}</span>
          <StarBar :stars="isComplete ? totalStars : currentStars" size="sm" />
        </div>
      </template>
    </BackBar>

    <section v-if="isLoading" class="modern-brand-card modern-brand-state" role="status">
      正在整理课时内容...
    </section>
    <section v-else-if="errorMsg" class="modern-brand-card modern-brand-state" role="alert">
      <p>{{ errorMsg }}</p>
      <AppButton variant="ghost" @click="loadLesson">重新加载</AppButton>
    </section>

    <template v-else>
      <section v-if="!currentItem?.recognition || isComplete" class="modern-brand-card modern-brand-hero lesson-hero">
        <div class="lesson-hero-copy">
          <p class="modern-brand-kicker">{{ isComplete ? '课时完成' : '当前课时' }}</p>
          <h1 class="modern-brand-title">{{ lesson.name }}</h1>
          <p class="modern-brand-desc">{{ heroHint }}</p>
          <div class="modern-brand-badges">
            <span class="modern-brand-badge modern-brand-badge--solid">第 {{ displayItemIndex }} / {{ totalItems }} 项</span>
            <span class="modern-brand-badge">{{ lessonTypeText }}</span>
            <span class="modern-brand-badge">{{ heroScoreText }}</span>
          </div>
          <div class="modern-brand-progress" aria-label="课时进度">
            <div class="modern-brand-progress-fill" :style="{ width: `${heroProgressPercent}%` }"></div>
          </div>
        </div>

        <div class="lesson-hero-side">
          <div class="modern-brand-sticker" aria-hidden="true">
            <span>{{ isComplete ? '✓' : displayItemIndex }}</span>
            <strong>{{ isComplete ? '完成' : '继续' }}</strong>
          </div>
          <p class="modern-brand-caption">{{ stageCaption }}</p>
        </div>
      </section>

      <section v-if="isComplete" class="modern-brand-card modern-brand-panel lesson-complete-shell">
        <div class="modern-brand-surface complete-summary">
          <span class="modern-brand-badge modern-brand-badge--solid">总分 {{ totalBestScore }} 分</span>
          <span class="modern-brand-badge">累计 {{ totalStars }} 星</span>
          <span class="modern-brand-badge">完成 {{ totalItems }} 项</span>
        </div>
        <LessonComplete
          :lesson-name="lesson.name"
          :total-stars="totalStars"
          :total-score="totalBestScore"
          :is-submitting="isSubmitting"
          :save-error="saveError"
          @finish="finishLesson"
        />
      </section>

      <LessonReview v-else-if="showReview" :lesson-id="lesson.id" :save-error="saveError" @passed="handleReviewPassed" />
      <section v-else-if="lessonTemplate && currentItem" class="modern-brand-card modern-brand-panel lesson-stage-shell">
        <div v-if="!currentItem?.recognition" class="modern-brand-surface stage-summary">
          <div>
            <p class="modern-brand-kicker">现在做什么</p>
            <h2>{{ stageTitle }}</h2>
            <p class="modern-brand-note">{{ stageCaption }}</p>
          </div>
          <div class="stage-summary-right">
            <span class="modern-brand-badge">{{ currentScore !== null ? `${currentScore} 分` : '等待评分' }}</span>
            <span class="modern-brand-badge">{{ currentStars ? `${currentStars} 星` : '先开始练习' }}</span>
          </div>
        </div>

        <component
          :is="lessonTemplate"
          :key="lesson.id"
          :current-item="currentItem"
          :current-index="currentIndex"
          :total-items="totalItems"
          :current-score="currentScore"
          :current-stars="currentStars"
          :score-message="scoreMessage"
          :is-scoring="isScoring"
          :is-last-item="isLastItem"
          :item-engaged="currentItemEngaged"
          v-bind="lessonTemplateProps"
          @recorded="handleRecorded"
          @answered="handleAnswered"
          @listened="handleListened"
          @skip="skipCurrentItem"
          @next="nextItem"
          @prev="prevItem"
          @continuous-finished="advanceContinuousPlayback"
          @continuous-stopped="clearContinuousQuery"
        />
      </section>

      <section v-else class="modern-brand-card modern-brand-state">
        <p>该课型正在开发中，敬请期待！</p>
        <p class="modern-brand-caption">课型: {{ lesson.type }}</p>
      </section>

      <MascotFeedback
        v-if="mascotFeedback"
        :key="mascotFeedback.id"
        :mood="mascotFeedback.mood"
        :message="mascotFeedback.message"
      />
    </template>
  </main>
</template>

<style scoped>
@import './modern-brand.css';

.modern-lesson {
  min-height: 100dvh;
}

.lesson-bar-right {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.bar-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 72px;
  min-height: 32px;
  padding: 0 var(--space-3);
  color: var(--color-primary);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  border-radius: var(--radius-pill);
  background: var(--color-primary-bg);
}

.lesson-hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 148px;
  gap: var(--space-4);
  align-items: center;
  margin-bottom: var(--space-6);
}

.lesson-hero-copy {
  display: grid;
  gap: var(--space-3);
}

.lesson-hero-side {
  display: grid;
  gap: var(--space-3);
  justify-items: center;
  text-align: center;
}

.lesson-complete-shell,
.lesson-stage-shell {
  display: grid;
  gap: var(--space-4);
}

.complete-summary,
.stage-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
  padding: var(--space-4);
}

.stage-summary h2 {
  color: var(--text-primary);
}

.stage-summary-right {
  display: grid;
  gap: var(--space-2);
  justify-items: end;
}

.lesson-complete-shell :deep(.complete-area) {
  max-width: 100%;
  margin: 0 auto;
  background: var(--color-transparent);
  box-shadow: none;
}

.lesson-stage-shell :deep(.card-area) {
  max-width: min(560px, 100%);
}

.lesson-stage-shell :deep(.item-card),
.lesson-stage-shell :deep(.score-area),
.lesson-stage-shell :deep(.progress-bar),
.lesson-stage-shell :deep(.question-card),
.lesson-stage-shell :deep(.sentence-card),
.lesson-stage-shell :deep(.phonics-card),
.lesson-stage-shell :deep(.dialogue-card),
.lesson-stage-shell :deep(.reading-card),
.lesson-stage-shell :deep(.quiz-card),
.lesson-stage-shell :deep(.calculate-card) {
  box-shadow: none;
}

.lesson-stage-shell :deep(.item-card),
.lesson-stage-shell :deep(.score-area),
.lesson-stage-shell :deep(.question-card),
.lesson-stage-shell :deep(.sentence-card),
.lesson-stage-shell :deep(.phonics-card),
.lesson-stage-shell :deep(.dialogue-card),
.lesson-stage-shell :deep(.reading-card),
.lesson-stage-shell :deep(.quiz-card),
.lesson-stage-shell :deep(.calculate-card) {
  border: 1px solid color-mix(in srgb, var(--brand-accent) 12%, var(--bg-card));
  background: var(--brand-surface-card);
}

@media (max-width: 720px) {
  .lesson-hero,
  .complete-summary,
  .stage-summary {
    grid-template-columns: 1fr;
    flex-direction: column;
    align-items: start;
  }

  .lesson-hero-side,
  .stage-summary-right {
    justify-items: start;
    text-align: left;
  }

  .lesson-bar-right {
    justify-content: flex-end;
  }
}
</style>
