<!--
  ModernThemePage.vue - 新版主题页
  用途: 复用真实主题/单元/课时数据，将主题入口升级为更完整的旅程页。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getThemes } from '../../../api/theme'
import { getUnitsByTheme } from '../../../api/unit'
import { getLessonsByUnit } from '../../../api/lesson'
import { getUnitProgress } from '../../../api/progress'
import { getThemeConfig } from '../../../config/themeConfig'
import { getUnitImage } from '../../../config/unitImages'
import { useSafeBack } from '../../../composables/useSafeBack'
import AppButton from '../../AppButton.vue'
import BackBar from '../../BackBar.vue'
import StarBar from '../../StarBar.vue'
import {
  getLessonStatusCopy,
  getLessonTypeText,
  getRecommendedLesson,
  getLessonTypeShort,
  toPercent
} from './themeUnitModernShared'

const route = useRoute()
const router = useRouter()
const { safeBack } = useSafeBack()
const units = ref([])
const isLoading = ref(true)
const errorMsg = ref('')
// 直链进入时路由里没有主题名，反查成功前保持为空，由主题视觉配置兜底
const resolvedThemeName = ref('')

const themeId = computed(() => Number(route.params.themeId))
const subjectId = computed(() => Number(route.query.subjectId || 0))
const subjectName = computed(() => String(route.query.subjectName || ''))
const themeName = computed(() => String(route.query.themeName || resolvedThemeName.value))
const themeVisual = computed(() => getThemeConfig(themeId.value, themeName.value))
const themeAccent = computed(() => themeVisual.value.scenes[0]?.color || 'var(--color-primary)')

const featuredUnit = computed(() => units.value[0] || null)
const otherUnits = computed(() => units.value.slice(1))
const completedLessonCount = computed(() => units.value.reduce((sum, unit) => sum + unit.completedLessons, 0))
const totalLessonCount = computed(() => units.value.reduce((sum, unit) => sum + unit.totalLessons, 0))
const completedUnitCount = computed(() => units.value.filter(unit => unit.completedLessons === unit.totalLessons).length)
const journeyPercent = computed(() => toPercent(completedLessonCount.value, totalLessonCount.value))
const featuredSuggestedLesson = computed(() => getRecommendedLesson(featuredUnit.value?.lessons || []))
const featuredSuggestedCopy = computed(() => getLessonStatusCopy(featuredSuggestedLesson.value?.progress))

onMounted(loadUnits)

async function loadUnits() {
  isLoading.value = true
  errorMsg.value = ''

  try {
    // 主题名反查与单元加载互不依赖，并发执行减少直链等待
    await Promise.all([resolveThemeName(), loadUnitList()])
  } catch (error) {
    errorMsg.value = '主题内容加载失败，请稍后再试。'
    console.error('加载主题内容失败:', error)
  } finally {
    isLoading.value = false
  }
}

/**
 * 直链进入时通过全量主题列表反查真实主题名。
 * 为什么不直接用配置兜底：主题配置只覆盖少数 id，新主题直链时会显示默认名，孩子会认不出来。
 */
async function resolveThemeName() {
  if (route.query.themeName) return
  try {
    const themes = await getThemes()
    const matched = themes.find(theme => theme.id === themeId.value)
    if (matched) resolvedThemeName.value = matched.name
  } catch (error) {
    // 反查失败不阻塞页面，主题视觉配置仍提供兜底标题
    console.warn('反查主题名失败:', error)
  }
}

/** 加载主题下全部单元及课时进度 */
async function loadUnitList() {
  const unitList = await getUnitsByTheme(themeId.value)
  units.value = await Promise.all(unitList.map(async (unit) => {
    const [lessons, progressList] = await Promise.all([
      getLessonsByUnit(unit.id),
      getUnitProgress(unit.id).catch(() => [])
    ])
    const progressMap = new Map(progressList.map(progress => [progress.lessonId, progress]))
    const lessonsWithProgress = lessons.map(lesson => ({
      ...lesson,
      progress: progressMap.get(lesson.id)
    }))
    const completedLessons = lessonsWithProgress.filter(item => item.progress?.status === 'COMPLETED').length

    return {
      ...unit,
      lessons: lessonsWithProgress,
      totalLessons: lessonsWithProgress.length,
      completedLessons,
      suggestedLesson: getRecommendedLesson(lessonsWithProgress)
    }
  }))
}

function getSceneConfig(index) {
  const scenes = themeVisual.value.scenes
  return scenes[index % scenes.length] || scenes[0]
}

function goBack() {
  const fallback = subjectId.value ? `/subject/${subjectId.value}` : '/'
  safeBack(fallback)
}

/** 判断某课时是否为当前推荐课，用于在列表里做「从这里开始」高亮 */
function isSuggestedLesson(lesson) {
  return lesson.id === featuredSuggestedLesson.value?.id
}

function openLesson(unit, lesson) {
  const query = {
    unitId: String(unit.id),
    // 透传单元名，避免单元页直链或刷新时标题回退成「第 N 单元」
    unitName: unit.name,
    themeId: String(themeId.value),
    themeName: themeVisual.value.title
  }

  if (subjectId.value) query.subjectId = String(subjectId.value)
  if (subjectName.value) query.subjectName = subjectName.value

  router.push({
    path: `/lesson/${lesson.id}`,
    query
  })
}

/** 进入单元任务板：其余单元在主题页收成摘要卡，点这里进单元页看全部课时 */
function openUnit(unit) {
  const query = {
    themeId: String(themeId.value),
    themeName: themeVisual.value.title,
    unitName: unit.name
  }

  if (subjectId.value) query.subjectId = String(subjectId.value)
  if (subjectName.value) query.subjectName = subjectName.value

  router.push({ path: `/unit/${unit.id}`, query })
}
</script>

<template>
  <main class="modern-theme modern-page-shell modern-page-shell--top-spaced" :style="{ '--brand-accent': themeAccent }">
    <BackBar :title="themeVisual.title" @back="goBack" />

    <section class="modern-brand-card modern-brand-hero theme-hero">
      <div class="theme-hero-copy">
        <p class="modern-brand-kicker">学习地图</p>
        <h1 class="modern-brand-title">{{ themeVisual.emoji }} {{ themeVisual.title }}</h1>
        <p class="modern-brand-desc">
          {{ themeVisual.description }}。从第一站开始，一课一课往前走。
        </p>
        <div class="modern-brand-badges">
          <span class="modern-brand-badge modern-brand-badge--solid">
            已完成 {{ completedLessonCount }} / {{ totalLessonCount }} 课
          </span>
          <span class="modern-brand-badge">走完单元 {{ completedUnitCount }} / {{ units.length }}</span>
          <span v-if="subjectName" class="modern-brand-badge">{{ subjectName }}</span>
        </div>
        <div class="modern-brand-progress" aria-label="主题进度">
          <div class="modern-brand-progress-fill" :style="{ width: `${journeyPercent}%` }"></div>
        </div>
      </div>

      <div class="theme-hero-side">
        <div class="modern-brand-sticker" aria-hidden="true">
          <span>{{ themeVisual.emoji }}</span>
          <strong>出发喽</strong>
        </div>
        <p class="modern-brand-caption">学完一课，就点亮一颗星星！</p>
      </div>
    </section>

    <section v-if="isLoading" class="modern-brand-card modern-brand-state" role="status">
      正在准备地图...
    </section>
    <section v-else-if="errorMsg" class="modern-brand-card modern-brand-state" role="alert">
      <p>{{ errorMsg }}</p>
      <AppButton variant="ghost" @click="loadUnits">再试一次</AppButton>
    </section>
    <section v-else-if="units.length === 0" class="modern-brand-card modern-brand-state">
      <p>这一站的内容马上就来。</p>
      <AppButton variant="ghost" @click="goBack">回到学科页</AppButton>
    </section>

    <template v-else>
      <section class="modern-brand-card modern-brand-panel featured-unit">
        <div class="featured-unit-main">
          <div class="featured-unit-top">
            <div class="featured-unit-copy">
              <span class="modern-brand-chip">
                {{ getSceneConfig(0).icon }} 第 1 站
              </span>
              <h2 class="modern-brand-section-title">{{ featuredUnit.name }}</h2>
              <p class="modern-brand-note">
                已完成 {{ featuredUnit.completedLessons }} / {{ featuredUnit.totalLessons }} 课。
                点下面带「从这里开始」的那一课。
              </p>
            </div>

            <div class="featured-unit-art modern-brand-surface">
              <img
                v-if="getUnitImage(featuredUnit.id)"
                :src="getUnitImage(featuredUnit.id)"
                :alt="featuredUnit.name"
                class="featured-unit-image"
              />
              <div v-else class="featured-unit-fallback" aria-hidden="true">{{ getSceneConfig(0).icon }}</div>
            </div>
          </div>

          <div class="featured-lesson-list">
            <button
              v-for="(lesson, lessonIndex) in featuredUnit.lessons"
              :key="lesson.id"
              type="button"
              class="modern-brand-card modern-brand-list-card featured-lesson-card"
              :class="{ 'featured-lesson-card--recommended': isSuggestedLesson(lesson) }"
              @click="openLesson(featuredUnit, lesson)"
            >
              <span class="modern-brand-index">{{ lessonIndex + 1 }}</span>
              <div class="featured-lesson-info">
                <div class="featured-lesson-head">
                  <h3>{{ lesson.name }}</h3>
                  <span class="modern-brand-badge">{{ getLessonTypeShort(lesson.type) }}</span>
                </div>
                <p class="modern-brand-note">{{ getLessonTypeText(lesson.type) }}</p>
              </div>
              <div class="featured-lesson-meta">
                <span v-if="isSuggestedLesson(lesson)" class="recommended-chip">
                  从这里开始 · {{ featuredSuggestedCopy.action }}
                </span>
                <StarBar
                  v-if="lesson.progress?.status === 'COMPLETED'"
                  :stars="lesson.progress?.stars || 0"
                  size="sm"
                />
                <span v-else-if="!isSuggestedLesson(lesson)" class="modern-brand-caption">{{ getLessonStatusCopy(lesson.progress).action }}</span>
              </div>
            </button>
          </div>
        </div>
      </section>

      <section class="other-units-section" aria-labelledby="other-units-title">
        <div class="modern-brand-section-head">
          <div>
            <p class="modern-brand-kicker">继续往前走</p>
            <h2 id="other-units-title" class="modern-brand-section-title">后面的单元</h2>
          </div>
        </div>

        <div class="unit-summary-list">
          <button
            v-for="(unit, index) in otherUnits"
            :key="unit.id"
            type="button"
            class="modern-brand-card modern-brand-list-card unit-summary-card"
            :style="{ '--brand-accent': getSceneConfig(index + 1).color }"
            @click="openUnit(unit)"
          >
            <span class="modern-brand-index">{{ getSceneConfig(index + 1).icon }}</span>
            <div class="unit-summary-copy">
              <h3>{{ unit.name }}</h3>
              <p class="modern-brand-note">已完成 {{ unit.completedLessons }} / {{ unit.totalLessons }} 课</p>
            </div>
            <span class="unit-summary-status">
              {{ unit.suggestedLesson ? getLessonStatusCopy(unit.suggestedLesson.progress).label : '还没开始' }}
            </span>
            <span class="unit-summary-arrow" aria-hidden="true">→</span>
          </button>
        </div>
      </section>
    </template>
  </main>
</template>

<style scoped>
@import './modern-brand.css';

.modern-theme {
  min-height: 100vh;
}

.theme-hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 168px;
  gap: var(--space-4);
  margin-bottom: var(--space-6);
}

.theme-hero-copy {
  display: grid;
  gap: var(--space-3);
}

.theme-hero-side {
  display: grid;
  justify-items: center;
  align-content: center;
  gap: var(--space-3);
}

.theme-hero-side p {
  text-align: center;
}

.featured-unit {
  margin-bottom: var(--space-6);
}

.featured-unit-main {
  display: grid;
  gap: var(--space-4);
}

.featured-unit-top {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 168px;
  gap: var(--space-4);
  align-items: center;
}

.featured-unit-copy {
  display: grid;
  gap: var(--space-3);
}

.featured-unit-copy h2,
.featured-lesson-card h3,
.unit-summary-card h3 {
  color: var(--text-primary);
}

.featured-unit-art {
  display: grid;
  place-items: center;
  min-height: 168px;
  padding: var(--space-3);
}

.featured-unit-image {
  width: 100%;
  height: 140px;
  object-fit: cover;
  border-radius: 20px;
}

.featured-unit-fallback {
  font-size: 72px;
}

.featured-lesson-list {
  display: grid;
  gap: var(--space-3);
}

.featured-lesson-card {
  padding: var(--space-4);
}

/* 推荐课高亮：黄色描边 + 浅黄底，让「下一课学什么」一眼可见 */
.featured-lesson-card--recommended {
  border: 2px solid var(--color-accent);
  background: color-mix(in srgb, var(--color-accent) 14%, var(--bg-card));
  box-shadow: var(--shadow-soft);
}

.featured-lesson-info {
  flex: 1;
  min-width: 0;
}

.featured-lesson-head {
  display: flex;
  align-items: start;
  justify-content: space-between;
  gap: var(--space-3);
  margin-bottom: var(--space-2);
}

.featured-lesson-meta {
  display: grid;
  gap: var(--space-2);
  align-items: center;
  justify-items: end;
  min-width: 96px;
}

.recommended-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-1) var(--space-2);
  color: color-mix(in srgb, var(--color-accent) 56%, var(--text-primary));
  font-size: var(--text-xs);
  font-weight: var(--font-bold);
  white-space: nowrap;
  border-radius: var(--radius-pill);
  background: var(--color-accent);
}

.other-units-section {
  padding-bottom: var(--space-8);
}

.unit-summary-list {
  display: grid;
  gap: var(--space-3);
}

.unit-summary-card {
  align-items: center;
  padding: var(--space-4);
}

.unit-summary-copy {
  flex: 1;
  min-width: 0;
}

.unit-summary-status {
  color: var(--text-secondary);
  font-size: var(--text-sm);
  white-space: nowrap;
}

.unit-summary-arrow {
  color: var(--brand-accent);
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
}

@media (max-width: 720px) {
  .theme-hero,
  .featured-unit-top {
    grid-template-columns: 1fr;
  }

  .theme-hero-side {
    justify-items: start;
  }

  .featured-lesson-card {
    align-items: center;
  }

  .featured-lesson-head,
  .featured-lesson-meta {
    flex-direction: column;
  }

  .featured-lesson-meta {
    justify-items: start;
    min-width: 0;
  }

  .unit-summary-status {
    display: none;
  }
}
</style>
