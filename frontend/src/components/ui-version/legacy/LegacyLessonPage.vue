<!--
  LegacyLessonPage.vue - 老版课时学习页（分发器）
  用途: 老版展示层；课程加载、评分、错题与完成流程统一由 useLessonLearning 管理。
  作者: english-app
  创建日期: 2026-07-20
  修改: 2026-07-21 重构为按 type 分发的路由器
       2026-07-21 新增 PHONICS/DIALOGUE 分发
  迁移日期: 2026-09-29
-->
<script setup>
import { useLessonLearning } from '../../../composables/useLessonLearning'
import StarBar from '../../StarBar.vue'
import BackBar from '../../BackBar.vue'
import AppButton from '../../AppButton.vue'
import MascotFeedback from '../../MascotFeedback.vue'
import LessonComplete from '../../lesson-templates/LessonComplete.vue'
import LessonReview from '../../lesson-templates/LessonReview.vue'

const { lesson, isLoading, errorMsg, currentIndex, currentScore, currentStars, scoreMessage,
  isScoring, isComplete, showReview, isSubmitting, saveError, mascotFeedback, currentItem,
  currentText, totalItems, isLastItem, currentItemEngaged, totalBestScore, totalStars, lessonTemplate,
  lessonTemplateProps, loadLesson, handleRecorded, handleAnswered, handleListened, skipCurrentItem,
  nextItem, prevItem, advanceContinuousPlayback, handleReviewPassed, clearContinuousQuery, goBack, finishLesson } = useLessonLearning()
</script>

<template>
  <div class="lesson-view">
    <!-- 顶部栏 -->
    <BackBar @back="goBack">
      <template #right>
        <StarBar :stars="isComplete ? totalStars : currentStars" size="sm" />
      </template>
    </BackBar>

    <!-- 加载中 -->
    <div v-if="isLoading" class="state-tip" role="status" aria-live="polite">
      <div class="loading-dot"></div>
      <p>加载中...</p>
    </div>
    <!-- 加载失败 -->
    <div v-else-if="errorMsg" class="state-tip error" role="alert">
      <p>{{ errorMsg }}</p>
      <AppButton variant="ghost" @click="loadLesson">重新加载</AppButton>
    </div>

    <!-- 学习内容区 -->
    <template v-else>
      <!-- 已完成:结算页 -->
      <LessonComplete
        v-if="isComplete"
        :lesson-name="lesson.name"
        :total-stars="totalStars"
        :total-score="totalBestScore"
        :is-submitting="isSubmitting"
        :save-error="saveError"
        @finish="finishLesson"
      />
      <LessonReview v-else-if="showReview" :lesson-id="lesson.id" :save-error="saveError" @passed="handleReviewPassed" />

      <!-- 按类型分发到对应模板 -->
      <component
        v-else-if="lessonTemplate && currentItem"
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

      <!-- 未支持的课型:占位提示 -->
      <div v-else class="state-tip">
        <p>该课型正在开发中，敬请期待！</p>
        <p class="type-hint">课型: {{ lesson.type }}</p>
      </div>

      <MascotFeedback
        v-if="mascotFeedback"
        :key="mascotFeedback.id"
        :mood="mascotFeedback.mood"
        :message="mascotFeedback.message"
      />
    </template>
  </div>
</template>

<style scoped>
.lesson-view {
  min-height: 100dvh;
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

.state-tip p + .app-btn {
  margin-top: var(--space-3);
}

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
