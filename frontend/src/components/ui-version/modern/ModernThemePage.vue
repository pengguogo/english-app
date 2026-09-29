<!--
  ModernThemePage.vue - 新版主题页
  用途: 复用真实主题/单元/课时数据，将主题入口升级为更完整的旅程页。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
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

const themeId = computed(() => Number(route.params.themeId))
const subjectId = computed(() => Number(route.query.subjectId || 0))
const subjectName = computed(() => String(route.query.subjectName || ''))
const themeName = computed(() => String(route.query.themeName || ''))
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
  } catch (error) {
    errorMsg.value = '主题旅程加载失败，请稍后再试。'
    console.error('加载主题旅程失败:', error)
  } finally {
    isLoading.value = false
  }
}

function getSceneConfig(index) {
  const scenes = themeVisual.value.scenes
  return scenes[index % scenes.length] || scenes[0]
}

function goBack() {
  const fallback = subjectId.value ? `/subject/${subjectId.value}` : '/'
  safeBack(fallback)
}

function openLesson(unit, lesson) {
  const query = {
    unitId: String(unit.id),
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
</script>

<template>
  <main class="modern-theme modern-page-shell modern-page-shell--top-spaced" :style="{ '--brand-accent': themeAccent }">
    <BackBar :title="themeVisual.title" @back="goBack" />

    <section class="modern-brand-card modern-brand-hero theme-hero">
      <div class="theme-hero-copy">
        <p class="modern-brand-kicker">主题旅程页</p>
        <h1 class="modern-brand-title">{{ themeVisual.emoji }} {{ themeVisual.title }}</h1>
        <p class="modern-brand-desc">
          {{ themeVisual.description }}。先完成最上面的重点单元，再把整条主题路线继续走完。
        </p>
        <div class="modern-brand-badges">
          <span class="modern-brand-badge modern-brand-badge--solid">
            已完成 {{ completedLessonCount }} / {{ totalLessonCount }} 课
          </span>
          <span class="modern-brand-badge">完成单元 {{ completedUnitCount }} / {{ units.length }}</span>
          <span class="modern-brand-badge">{{ subjectName || '当前学科' }}</span>
        </div>
        <div class="modern-brand-progress" aria-label="主题进度">
          <div class="modern-brand-progress-fill" :style="{ width: `${journeyPercent}%` }"></div>
        </div>
      </div>

      <div class="theme-hero-side">
        <div class="modern-brand-sticker" aria-hidden="true">
          <span>{{ themeVisual.emoji }}</span>
          <strong>第 1 站</strong>
        </div>
        <p class="modern-brand-caption">把第一单元做完，后面的旅程会更顺。</p>
      </div>
    </section>

    <section v-if="isLoading" class="modern-brand-card modern-brand-state" role="status">
      正在整理主题旅程...
    </section>
    <section v-else-if="errorMsg" class="modern-brand-card modern-brand-state" role="alert">
      <p>{{ errorMsg }}</p>
      <AppButton variant="ghost" @click="loadUnits">重新加载</AppButton>
    </section>
    <section v-else-if="units.length === 0" class="modern-brand-card modern-brand-state">
      <p>这个主题暂时还没有学习内容。</p>
      <AppButton variant="ghost" @click="goBack">返回学科页</AppButton>
    </section>

    <template v-else>
      <section class="modern-brand-card modern-brand-panel featured-unit">
        <div class="featured-unit-main">
          <div class="featured-unit-top">
            <div class="featured-unit-copy">
              <span class="modern-brand-chip">
                {{ getSceneConfig(0).icon }} {{ getSceneConfig(0).label }}
              </span>
              <h2 class="modern-brand-section-title">{{ featuredUnit.name }}</h2>
              <p class="modern-brand-note">
                本单元已完成 {{ featuredUnit.completedLessons }} / {{ featuredUnit.totalLessons }} 课，
                建议先从“{{ featuredSuggestedLesson?.name || '第一课' }}”开始。
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

          <div v-if="featuredSuggestedLesson" class="suggested-lesson modern-brand-surface">
            <div>
              <p class="modern-brand-kicker">优先开始</p>
              <h3>{{ featuredSuggestedLesson.name }}</h3>
              <p class="modern-brand-note">
                {{ getLessonTypeText(featuredSuggestedLesson.type) }} · {{ featuredSuggestedCopy.label }}
              </p>
            </div>
            <div class="suggested-lesson-right">
              <StarBar
                v-if="featuredSuggestedLesson.progress?.status === 'COMPLETED'"
                :stars="featuredSuggestedLesson.progress?.stars || 0"
                size="sm"
              />
              <AppButton size="lg" @click="openLesson(featuredUnit, featuredSuggestedLesson)">
                {{ featuredSuggestedCopy.action }}
              </AppButton>
            </div>
          </div>

          <div class="featured-lesson-list">
            <button
              v-for="(lesson, lessonIndex) in featuredUnit.lessons"
              :key="lesson.id"
              type="button"
              class="modern-brand-card modern-brand-list-card featured-lesson-card"
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
                <StarBar
                  v-if="lesson.progress?.status === 'COMPLETED'"
                  :stars="lesson.progress?.stars || 0"
                  size="sm"
                />
                <span v-else class="modern-brand-caption">{{ getLessonStatusCopy(lesson.progress).action }}</span>
              </div>
            </button>
          </div>
        </div>
      </section>

      <section class="other-units-section" aria-labelledby="other-units-title">
        <div class="modern-brand-section-head">
          <div>
            <p class="modern-brand-kicker">后续章节</p>
            <h2 id="other-units-title" class="modern-brand-section-title">其余单元</h2>
          </div>
          <p class="modern-brand-section-note">第一单元更突出，其余单元保持轻量卡片，方便继续扩展。</p>
        </div>

        <div class="modern-brand-grid modern-brand-grid--two">
          <section
            v-for="(unit, index) in otherUnits"
            :key="unit.id"
            class="modern-brand-card modern-brand-panel unit-card"
            :style="{ '--brand-accent': getSceneConfig(index + 1).color }"
          >
            <div class="unit-card-head">
              <div>
                <span class="modern-brand-chip">{{ getSceneConfig(index + 1).icon }} 第 {{ index + 2 }} 单元</span>
                <h3>{{ unit.name }}</h3>
                <p class="modern-brand-note">已完成 {{ unit.completedLessons }} / {{ unit.totalLessons }} 课</p>
              </div>
              <span class="modern-brand-badge">{{ unit.suggestedLesson ? getLessonStatusCopy(unit.suggestedLesson.progress).label : '待开始' }}</span>
            </div>

            <div class="modern-brand-progress" aria-hidden="true">
              <div
                class="modern-brand-progress-fill"
                :style="{ width: `${toPercent(unit.completedLessons, unit.totalLessons)}%` }"
              ></div>
            </div>

            <div class="unit-card-lessons">
              <button
                v-for="(lesson, lessonIndex) in unit.lessons.slice(0, 3)"
                :key="lesson.id"
                type="button"
                class="modern-brand-surface mini-lesson"
                @click="openLesson(unit, lesson)"
              >
                <span>{{ lessonIndex + 1 }}</span>
                <div>
                  <strong>{{ lesson.name }}</strong>
                  <small>{{ getLessonTypeText(lesson.type) }}</small>
                </div>
              </button>
            </div>
          </section>
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
.suggested-lesson h3,
.featured-lesson-card h3,
.unit-card h3 {
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

.suggested-lesson {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
  padding: var(--space-4);
}

.suggested-lesson-right {
  display: grid;
  gap: var(--space-3);
  justify-items: end;
}

.featured-lesson-list {
  display: grid;
  gap: var(--space-3);
}

.featured-lesson-card {
  padding: var(--space-4);
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
  align-items: center;
  justify-items: end;
  min-width: 86px;
}

.other-units-section {
  padding-bottom: var(--space-8);
}

.unit-card {
  display: grid;
  gap: var(--space-4);
}

.unit-card-head {
  display: flex;
  align-items: start;
  justify-content: space-between;
  gap: var(--space-3);
}

.unit-card-lessons {
  display: grid;
  gap: var(--space-3);
}

.mini-lesson {
  display: grid;
  grid-template-columns: 32px minmax(0, 1fr);
  gap: var(--space-3);
  align-items: center;
  width: 100%;
  padding: var(--space-3);
  text-align: left;
}

.mini-lesson span {
  display: grid;
  place-items: center;
  width: 32px;
  height: 32px;
  color: var(--text-on-primary);
  font-weight: var(--font-bold);
  border-radius: var(--radius-pill);
  background: var(--brand-accent);
}

.mini-lesson strong {
  display: block;
  color: var(--text-primary);
}

.mini-lesson small {
  color: var(--text-tertiary);
}

@media (max-width: 720px) {
  .theme-hero,
  .featured-unit-top {
    grid-template-columns: 1fr;
  }

  .theme-hero-side,
  .suggested-lesson-right {
    justify-items: start;
  }

  .suggested-lesson,
  .unit-card-head,
  .featured-lesson-head {
    flex-direction: column;
    align-items: start;
  }

  .featured-lesson-card {
    align-items: center;
  }
}
</style>
