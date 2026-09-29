<!--
  ModernUnitPage.vue - 新版单元页
  用途: 复用真实课时与进度数据，将单元入口升级为更清晰的任务板。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getLessonsByUnit } from '../../../api/lesson'
import { getUnitProgress } from '../../../api/progress'
import { useSafeBack } from '../../../composables/useSafeBack'
import AppButton from '../../AppButton.vue'
import BackBar from '../../BackBar.vue'
import StarBar from '../../StarBar.vue'
import {
  getLessonStatus,
  getLessonStatusCopy,
  getLessonTypeText,
  getLessonTypeShort,
  getRecommendedLesson,
  toPercent
} from './themeUnitModernShared'

const route = useRoute()
const router = useRouter()
const { safeBack } = useSafeBack()
const lessons = ref([])
const isLoading = ref(true)
const errorMsg = ref('')

const themeId = computed(() => Number(route.query.themeId || 0))
const themeName = computed(() => String(route.query.themeName || ''))
const subjectId = computed(() => Number(route.query.subjectId || 0))
const subjectName = computed(() => String(route.query.subjectName || ''))
const unitDisplayName = computed(() => String(route.query.unitName || `第 ${route.params.unitId} 单元`))
const unitAccent = computed(() => {
  if (themeId.value === 1) return 'var(--color-warning)'
  if (themeId.value === 2) return 'var(--color-success)'
  if (themeId.value === 3) return 'var(--color-primary)'
  return 'var(--color-primary)'
})

const completedCount = computed(() => lessons.value.filter(lesson => lesson.progress?.status === 'COMPLETED').length)
const inProgressCount = computed(() => lessons.value.filter(lesson => lesson.progress?.status === 'IN_PROGRESS').length)
const progressPercent = computed(() => toPercent(completedCount.value, lessons.value.length))
const suggestedLesson = computed(() => getRecommendedLesson(lessons.value))
const mainLesson = computed(() => suggestedLesson.value || lessons.value[0] || null)
const secondaryLessons = computed(() => lessons.value.filter(lesson => lesson.id !== mainLesson.value?.id))
const boardHint = computed(() => {
  if (!mainLesson.value) return '单元内容准备中'
  const statusCopy = getLessonStatusCopy(mainLesson.value.progress)
  return `建议先学“${mainLesson.value.name}”，当前状态：${statusCopy.label}`
})

onMounted(loadLessons)

async function loadLessons() {
  isLoading.value = true
  errorMsg.value = ''

  try {
    const [lessonList, progressList] = await Promise.all([
      getLessonsByUnit(route.params.unitId),
      getUnitProgress(route.params.unitId).catch(() => [])
    ])

    const progressMap = new Map(progressList.map(progress => [progress.lessonId, progress]))
    lessons.value = lessonList.map(lesson => ({
      ...lesson,
      progress: progressMap.get(lesson.id)
    }))
  } catch (error) {
    errorMsg.value = '单元任务板加载失败，请稍后重试。'
    console.error('加载单元任务板失败:', error)
  } finally {
    isLoading.value = false
  }
}

function openLesson(lesson) {
  const query = { unitId: String(route.params.unitId) }
  if (themeId.value) query.themeId = String(themeId.value)
  if (themeName.value) query.themeName = themeName.value
  if (subjectId.value) query.subjectId = String(subjectId.value)
  if (subjectName.value) query.subjectName = subjectName.value

  router.push({
    path: `/lesson/${lesson.id}`,
    query
  })
}

function goBack() {
  if (themeId.value) {
    const query = {}
    if (subjectId.value) query.subjectId = String(subjectId.value)
    if (subjectName.value) query.subjectName = subjectName.value
    if (themeName.value) query.themeName = themeName.value
    safeBack({ path: `/theme/${themeId.value}`, query })
    return
  }

  safeBack('/')
}
</script>

<template>
  <main class="modern-unit modern-page-shell modern-page-shell--top-spaced" :style="{ '--brand-accent': unitAccent }">
    <BackBar :title="unitDisplayName" @back="goBack" />

    <section class="modern-brand-card modern-brand-hero unit-hero">
      <div class="unit-hero-copy">
        <p class="modern-brand-kicker">单元任务板</p>
        <h1 class="modern-brand-title">{{ unitDisplayName }}</h1>
        <p class="modern-brand-desc">{{ boardHint }}</p>
        <div class="modern-brand-badges">
          <span class="modern-brand-badge modern-brand-badge--solid">完成 {{ completedCount }} / {{ lessons.length }}</span>
          <span class="modern-brand-badge">进行中 {{ inProgressCount }} 节</span>
          <span class="modern-brand-badge">{{ themeName || '当前主题' }}</span>
        </div>
        <div class="modern-brand-progress" aria-label="单元进度">
          <div class="modern-brand-progress-fill" :style="{ width: `${progressPercent}%` }"></div>
        </div>
      </div>

      <div class="modern-brand-sticker" aria-hidden="true">
        <span>1st</span>
        <strong>先学推荐课</strong>
      </div>
    </section>

    <section v-if="isLoading" class="modern-brand-card modern-brand-state" role="status">
      正在整理单元任务...
    </section>
    <section v-else-if="errorMsg" class="modern-brand-card modern-brand-state" role="alert">
      <p>{{ errorMsg }}</p>
      <AppButton variant="ghost" @click="loadLessons">重新加载</AppButton>
    </section>
    <section v-else-if="lessons.length === 0" class="modern-brand-card modern-brand-state">
      <p>这个单元暂时还没有课时。</p>
      <AppButton variant="ghost" @click="goBack">返回主题页</AppButton>
    </section>

    <template v-else>
      <section v-if="mainLesson" class="modern-brand-card modern-brand-panel main-lesson">
        <div class="main-lesson-top">
          <div class="main-lesson-copy">
            <span class="modern-brand-chip">建议先学</span>
            <h2>{{ mainLesson.name }}</h2>
            <p class="modern-brand-note">
              {{ getLessonTypeText(mainLesson.type) }} · {{ getLessonStatusCopy(mainLesson.progress).label }}
            </p>
          </div>
          <span class="main-lesson-sticker">{{ getLessonTypeShort(mainLesson.type) }}</span>
        </div>

        <div class="main-lesson-meta">
          <div class="modern-brand-badges">
            <span class="modern-brand-badge">{{ getLessonStatusCopy(mainLesson.progress).action }}</span>
            <span class="modern-brand-badge">第 {{ lessons.findIndex(item => item.id === mainLesson.id) + 1 }} 课</span>
          </div>
          <StarBar
            v-if="mainLesson.progress?.status === 'COMPLETED'"
            :stars="mainLesson.progress?.stars || 0"
            size="sm"
          />
        </div>

        <AppButton size="lg" @click="openLesson(mainLesson)">
          {{ getLessonStatusCopy(mainLesson.progress).action }}
        </AppButton>
      </section>

      <section class="task-list-section" aria-labelledby="task-list-title">
        <div class="modern-brand-section-head">
          <div>
            <p class="modern-brand-kicker">课时列表</p>
            <h2 id="task-list-title" class="modern-brand-section-title">其余任务</h2>
          </div>
          <p class="modern-brand-section-note">已完成、进行中、未开始都保持统一且清晰的状态表达。</p>
        </div>

        <div class="task-list">
          <button
            v-for="(lesson, index) in secondaryLessons"
            :key="lesson.id"
            type="button"
            class="modern-brand-card modern-brand-list-card modern-brand-panel task-card"
            :class="`status-${getLessonStatus(lesson.progress)}`"
            @click="openLesson(lesson)"
          >
            <span class="modern-brand-index">{{ index + 1 + (mainLesson ? 1 : 0) }}</span>
            <div class="task-card-copy">
              <div class="task-card-head">
                <h3>{{ lesson.name }}</h3>
                <span class="modern-brand-badge">{{ getLessonTypeShort(lesson.type) }}</span>
              </div>
              <p class="modern-brand-note">{{ getLessonTypeText(lesson.type) }}</p>
            </div>
            <div class="task-card-right">
              <StarBar
                v-if="lesson.progress?.status === 'COMPLETED'"
                :stars="lesson.progress?.stars || 0"
                size="sm"
              />
              <span class="status-chip" :class="`status-chip--${getLessonStatus(lesson.progress)}`">
                {{ getLessonStatusCopy(lesson.progress).label }}
              </span>
            </div>
          </button>
        </div>
      </section>
    </template>
  </main>
</template>

<style scoped>
@import './modern-brand.css';

.modern-unit {
  min-height: 100vh;
}

.unit-hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 120px;
  gap: var(--space-4);
  align-items: center;
  margin-bottom: var(--space-6);
}

.unit-hero-copy {
  display: grid;
  gap: var(--space-3);
}

.main-lesson,
.task-list-section {
  margin-bottom: var(--space-6);
}

.main-lesson {
  display: grid;
  gap: var(--space-4);
}

.main-lesson-top,
.main-lesson-meta,
.task-card-head {
  display: flex;
  align-items: start;
  justify-content: space-between;
  gap: var(--space-3);
}

.main-lesson-copy h2,
.task-card h3 {
  color: var(--text-primary);
}

.main-lesson-sticker {
  display: grid;
  place-items: center;
  min-width: 68px;
  min-height: 68px;
  color: var(--text-on-primary);
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  border-radius: 22px;
  background: linear-gradient(135deg, var(--brand-accent), var(--color-accent));
  box-shadow: var(--shadow-soft);
}

.task-list {
  display: grid;
  gap: var(--space-3);
}

.task-card {
  padding: var(--space-4);
}

.task-card-copy {
  flex: 1;
  min-width: 0;
}

.task-card-right {
  display: grid;
  gap: var(--space-2);
  justify-items: end;
  min-width: 92px;
}

.status-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 74px;
  min-height: 32px;
  padding: 0 var(--space-2);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  border-radius: var(--radius-pill);
}

.status-chip--completed {
  color: var(--color-success-hover);
  background: var(--color-success-bg);
}

.status-chip--in_progress {
  color: var(--color-primary);
  background: rgba(107, 124, 255, 0.12);
}

.status-chip--not_started {
  color: var(--text-secondary);
  background: var(--bg-muted);
}

.task-card.status-completed {
  --brand-accent: var(--color-success);
}

.task-card.status-in_progress {
  --brand-accent: var(--color-primary);
}

.task-card.status-not_started {
  --brand-accent: var(--color-orange);
}

@media (max-width: 720px) {
  .unit-hero {
    grid-template-columns: 1fr;
  }

  .main-lesson-top,
  .main-lesson-meta,
  .task-card,
  .task-card-head {
    flex-direction: column;
    align-items: start;
  }

  .task-card-right {
    justify-items: start;
    min-width: 0;
  }
}
</style>
