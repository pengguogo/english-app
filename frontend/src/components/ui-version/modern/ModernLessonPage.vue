<!--
  ModernLessonPage.vue - 新版课时页
  用途: 复用原有课时分发与完成逻辑，仅重构外层头部、进度摘要与新版容器皮肤。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getLessonById, getLessonsByUnit } from '../../../api/lesson'
import { getUnitsByTheme } from '../../../api/unit'
import { completeLesson } from '../../../api/progress'
import { scorePronunciation } from '../../../api/voice'
import { recordWrongAnswer } from '../../../api/wrongAnswer'
import { useSafeBack } from '../../../composables/useSafeBack'
import { stopActiveTts } from '../../../composables/useTts'
import { findNextReadingLesson } from '../../../utils/continuousPlayback'
import { useLessonBookmark } from '../../../composables/useLessonBookmark'
import { useStudyTimer } from '../../../composables/useStudyTimer'
import StarBar from '../../StarBar.vue'
import BackBar from '../../BackBar.vue'
import AppButton from '../../AppButton.vue'
import MascotFeedback from '../../MascotFeedback.vue'
import WordLesson from '../../lesson-templates/WordLesson.vue'
import CharacterLesson from '../../lesson-templates/CharacterLesson.vue'
import SentenceLesson from '../../lesson-templates/SentenceLesson.vue'
import LessonComplete from '../../lesson-templates/LessonComplete.vue'
import LessonReview from '../../lesson-templates/LessonReview.vue'
import ReadingLesson from '../../lesson-templates/ReadingLesson.vue'
import QuizLesson from '../../lesson-templates/QuizLesson.vue'
import CalculateLesson from '../../lesson-templates/CalculateLesson.vue'
import PhonicsLesson from '../../lesson-templates/PhonicsLesson.vue'
import DialogueLesson from '../../lesson-templates/DialogueLesson.vue'
import { getLessonTypeText } from './themeUnitModernShared'

const route = useRoute()
const router = useRouter()
const { safeBack } = useSafeBack()

const lesson = ref(null)
const isLoading = ref(true)
const errorMsg = ref('')

const currentIndex = ref(0)
const currentScore = ref(null)
const currentStars = ref(0)
const scoreMessage = ref('')
const isScoring = ref(false)
const isComplete = ref(false)
const showReview = ref(false)
const isSubmitting = ref(false)
const isProgressSaved = ref(false)
const saveError = ref('')
const mascotFeedback = ref(null)
let lessonLoadVersion = 0
let mascotFeedbackTimer = null

const bestScores = ref([])
const answerResults = ref([])
const engagedItems = ref([])

const restoreBookmark = useLessonBookmark(lesson, isLoading, currentIndex, bestScores,
  engagedItems, isProgressSaved, route)

const currentItem = computed(() => {
  if (!lesson.value || !lesson.value.content) return null
  const items = lesson.value.content.items
  if (!Array.isArray(items) || items.length === 0) return null
  return items[currentIndex.value] || items[0]
})

const currentText = computed(() => {
  if (!currentItem.value) return ''
  return currentItem.value.word || currentItem.value.sentence || currentItem.value.text || ''
})

const totalItems = computed(() => {
  if (!lesson.value || !lesson.value.content) return 0
  return lesson.value.content.items?.length ?? 0
})

const isLastItem = computed(() => currentIndex.value >= totalItems.value - 1)
const currentItemEngaged = computed(() => engagedItems.value[currentIndex.value] === true)
const isFruitPilot = computed(() => Number(route.query.themeId) === 1)
const isActivelyStudying = computed(() => !isLoading.value && !!lesson.value && !isComplete.value && !showReview.value)
useStudyTimer(isActivelyStudying, computed(() => lesson.value?.id))

const totalBestScore = computed(() => {
  const validScores = bestScores.value.filter(score => typeof score === 'number')
  if (validScores.length === 0) return 0
  const sum = validScores.reduce((accumulator, score) => accumulator + score, 0)
  return Math.round(sum / validScores.length)
})

const totalStars = computed(() => scoreToStars(totalBestScore.value))

const lessonTemplate = computed(() => {
  if (!lesson.value) return null

  switch (lesson.value.type) {
    case 'WORD':
      return currentItem.value?.recognition ? CharacterLesson : WordLesson
    case 'SENTENCE':
      return SentenceLesson
    case 'READING':
      return ReadingLesson
    case 'QUIZ':
      return QuizLesson
    case 'CALCULATE':
      return CalculateLesson
    case 'PHONICS':
      return PhonicsLesson
    case 'DIALOGUE':
      return DialogueLesson
    default:
      return null
  }
})

const lessonTemplateProps = computed(() => {
  if (currentItem.value?.recognition) return { lessonId: lesson.value.id, items: lesson.value.content.items }
  if (lesson.value?.type !== 'READING') return {}
  return {
    continuousPlayback: Number(route.query.subjectId) === 4,
    autoStartContinuous: route.query.continuous === '1',
    isContinuousAdvancing: isSubmitting.value
  }
})

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

onMounted(loadLesson)
watch(() => route.params.lessonId, loadLesson)
onBeforeUnmount(() => {
  stopActiveTts()
  clearMascotFeedback()
})

function scoreToStars(score) {
  if (score >= 80) return 3
  if (score >= 60) return 2
  if (score >= 40) return 1
  return 0
}

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

function normalizeContent(raw) {
  const items = []
  if (Array.isArray(raw.words)) {
    raw.words.forEach((word) => {
      const meta = wordMetaMap[word.toLowerCase()] || {}
      items.push({
        word,
        emoji: meta.emoji || '🔤',
        phonetic: meta.phonetic || '',
        translation: meta.translation || ''
      })
    })
  } else if (Array.isArray(raw.sentences)) {
    raw.sentences.forEach((sentence) => {
      items.push({ sentence, emoji: '💬', phonetic: '', translation: '' })
    })
  } else if (Array.isArray(raw.items)) {
    const phonicsMeta = raw.type === 'PHONICS'
      ? {
          letter: raw.letter,
          pronunciation: raw.pronunciation,
          sound: raw.sound,
          tip: raw.tip
        }
      : {}
    raw.items.forEach(item => items.push({ ...phonicsMeta, ...item }))
  }
  return { items }
}

async function loadLesson() {
  stopActiveTts()
  clearMascotFeedback()
  const version = ++lessonLoadVersion
  isLoading.value = true
  errorMsg.value = ''
  isScoring.value = false
  currentIndex.value = 0
  isComplete.value = false
  showReview.value = false
  isProgressSaved.value = false
  saveError.value = ''
  resetCurrentScoreState()

  try {
    const data = await getLessonById(route.params.lessonId)
    if (version !== lessonLoadVersion) return

    const content = typeof data.content === 'string' ? JSON.parse(data.content) : data.content
    if (content?.picturebook === true) {
      await router.replace({ path: `/picturebooks/${data.unitId}`, query: { lesson: String(data.id) } })
      return
    }

    if (typeof data.content === 'string') {
      data.content = normalizeContent(JSON.parse(data.content))
    } else if (data.content && typeof data.content === 'object') {
      data.content = normalizeContent(data.content)
    }

    lesson.value = data
    bestScores.value = new Array(totalItems.value).fill(
      ['WORD', 'SENTENCE', 'PHONICS', 'DIALOGUE'].includes(data.type) ? null : 0
    )
    answerResults.value = new Array(totalItems.value).fill(null)
    engagedItems.value = new Array(totalItems.value).fill(false)
    restoreBookmark()
  } catch (error) {
    if (version !== lessonLoadVersion) return
    errorMsg.value = '加载课时失败,请返回重试'
    console.error('加载课时失败:', error)
  } finally {
    if (version === lessonLoadVersion) {
      isLoading.value = false
    }
  }
}

async function handleRecorded(wavBlob) {
  if (!currentItem.value || !currentText.value) return
  const sourceLesson = lesson.value
  const sourceIndex = currentIndex.value
  const sourceVersion = lessonLoadVersion
  markCurrentItemEngaged()
  isScoring.value = true
  scoreMessage.value = '评分中...'
  currentScore.value = null

  try {
    const result = await scorePronunciation(wavBlob, currentText.value)
    if (lesson.value !== sourceLesson || lessonLoadVersion !== sourceVersion || currentIndex.value !== sourceIndex) return
    if (!Number.isFinite(result.score)) {
      currentStars.value = 0
      scoreMessage.value = result.feedback || '评测未完成，请重试，不计成绩'
      return
    }
    currentScore.value = result.score
    currentStars.value = scoreToStars(result.score)
    scoreMessage.value = result.feedback || ''
    updateBestScore(currentIndex.value, result.score)
    showMascotFeedback(
      result.score >= 80 ? 'happy' : 'encourage',
      result.score >= 80 ? '发音真清楚，收下一颗星星！' : '已经开口啦，慢慢说会更棒。'
    )
  } catch (error) {
    if (lesson.value !== sourceLesson || lessonLoadVersion !== sourceVersion || currentIndex.value !== sourceIndex) return
    currentStars.value = 0
    scoreMessage.value = '评分失败，请重试，本次不计成绩'
    console.error('发音评测失败:', error)
  } finally {
    if (lesson.value === sourceLesson && lessonLoadVersion === sourceVersion) isScoring.value = false
  }
}

function updateBestScore(index, score) {
  const prevBest = bestScores.value[index] || 0
  bestScores.value[index] = Math.max(prevBest, score)
}

function handleAnswered({ correct, userAnswer, correctAnswer, score, assisted, firstWrong = !correct }) {
  answerResults.value[currentIndex.value] = correct
  const resultScore = typeof score === 'number' ? score : (correct ? 100 : 0)
  updateBestScore(currentIndex.value, resultScore)
  currentScore.value = resultScore
  currentStars.value = scoreToStars(resultScore)
  scoreMessage.value = correct
    ? (assisted ? '提示后答对了，再复习一次会更牢！' : '回答正确！')
    : '先听提示，再试一次！'
  showMascotFeedback(
    correct ? 'happy' : 'encourage',
    correct ? '找到正确水果啦！' : '没关系，再听一遍就能找到。'
  )

  if (firstWrong) {
    recordWrongAnswerSilently({ userAnswer, correctAnswer })
  }
}

function markCurrentItemEngaged() {
  engagedItems.value[currentIndex.value] = true
}

function handleListened() {
  markCurrentItemEngaged()
  showMascotFeedback('happy', '听到啦！跟着 Mimi 说一遍吧。')
}

function showMascotFeedback(mood, message) {
  if (!isFruitPilot.value) return
  clearTimeout(mascotFeedbackTimer)
  mascotFeedback.value = { mood, message, id: Date.now() }
  mascotFeedbackTimer = setTimeout(() => {
    mascotFeedback.value = null
  }, 2400)
}

function clearMascotFeedback() {
  clearTimeout(mascotFeedbackTimer)
  mascotFeedbackTimer = null
  mascotFeedback.value = null
}

function skipCurrentItem() {
  engagedItems.value[currentIndex.value] = true
  recordWrongAnswerSilently({
    userAnswer: '稍后复习',
    correctAnswer: currentText.value || currentItem.value?.text || '完成学习'
  })
  nextItem()
}

async function recordWrongAnswerSilently({ userAnswer, correctAnswer }) {
  try {
    await recordWrongAnswer({
      lessonId: parseInt(route.params.lessonId),
      lessonName: lesson.value?.name || '',
      questionIndex: currentIndex.value,
      questionType: lesson.value?.type,
      questionSnapshot: JSON.stringify(currentItem.value),
      userAnswer: String(userAnswer ?? ''),
      correctAnswer: String(correctAnswer ?? '')
    })
  } catch (error) {
    console.error('记录错题失败:', error)
  }
}

function resetCurrentScoreState() {
  currentScore.value = null
  currentStars.value = 0
  scoreMessage.value = ''
}

function nextItem() {
  stopActiveTts()
  clearMascotFeedback()
  if (currentIndex.value < totalItems.value - 1) {
    currentIndex.value++
    resetCurrentScoreState()
  } else {
    showReview.value = true
  }
}

function prevItem() {
  stopActiveTts()
  clearMascotFeedback()
  if (currentIndex.value > 0) {
    currentIndex.value--
    resetCurrentScoreState()
  }
}

async function advanceContinuousPlayback(reviewPassed = false) {
  if (!reviewPassed) {
    showReview.value = true
    return
  }
  if (isSubmitting.value || !lesson.value) return
  const sourceLessonId = Number(lesson.value.id)
  const sourceUnitId = Number(lesson.value.unitId)
  isSubmitting.value = true

  try {
    await completeLesson(sourceLessonId, totalStars.value, totalBestScore.value)
    if (Number(route.params.lessonId) !== sourceLessonId) return
    isProgressSaved.value = true

    const themeId = Number(route.query.themeId || 0)
    const units = themeId
      ? await getUnitsByTheme(themeId)
      : [{ id: sourceUnitId }]
    const next = await findNextReadingLesson(
      sourceLessonId,
      sourceUnitId,
      units,
      getLessonsByUnit
    )
    if (Number(route.params.lessonId) !== sourceLessonId) return

    if (!next) {
      isComplete.value = true
      clearContinuousQuery()
      return
    }

    await router.replace({
      path: `/lesson/${next.lesson.id}`,
      query: {
        ...route.query,
        unitId: String(next.unitId),
        continuous: '1'
      }
    })
  } catch (error) {
    console.error('自动进入下一节阅读课失败:', error)
    alert('自动播放下一课失败,请重试')
  } finally {
    isSubmitting.value = false
  }
}

async function handleReviewPassed() {
  if (route.query.continuous === '1') {
    await advanceContinuousPlayback(true)
    return
  }
  isSubmitting.value = true
  saveError.value = ''
  try {
    await completeLesson(lesson.value.id, totalStars.value, totalBestScore.value)
    isProgressSaved.value = true
    showReview.value = false
    isComplete.value = true
    showMascotFeedback('celebrate', '复习全对，本课完成！')
  } catch (error) {
    saveError.value = '保存失败，请重新提交复习'
  } finally {
    isSubmitting.value = false
  }
}

function clearContinuousQuery() {
  if (!route.query.continuous) return
  const query = { ...route.query }
  delete query.continuous
  router.replace({ query })
}

function goBack() {
  safeBack(buildThemeFallback())
}

function buildThemeFallback() {
  if (!route.query.themeId) return { path: '/' }
  const query = {}
  if (route.query.themeName) query.themeName = String(route.query.themeName)
  if (route.query.subjectId) query.subjectId = String(route.query.subjectId)
  if (route.query.subjectName) query.subjectName = String(route.query.subjectName)
  return { path: `/theme/${route.query.themeId}`, query }
}

async function finishLesson() {
  if (isSubmitting.value) return
  isSubmitting.value = true
  saveError.value = ''
  try {
    if (!isProgressSaved.value) {
      await completeLesson(
        route.params.lessonId,
        totalStars.value,
        totalBestScore.value
      )
    }
    const query = {}
    if (route.query.themeId) query.themeId = String(route.query.themeId)
    if (route.query.themeName) query.themeName = String(route.query.themeName)
    if (route.query.subjectId) query.subjectId = String(route.query.subjectId)
    if (route.query.subjectName) query.subjectName = String(route.query.subjectName)
    const destination = route.query.themeId
      ? { path: `/theme/${route.query.themeId}`, query }
      : { path: '/' }
    router.replace(destination)
  } catch (error) {
    console.error('保存进度失败:', error)
    saveError.value = '保存失败，请检查网络后重试'
  } finally {
    isSubmitting.value = false
  }
}
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
  background: rgba(107, 124, 255, 0.1);
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
  background: transparent;
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
  border: 1px solid color-mix(in srgb, var(--brand-accent) 12%, white);
  background: rgba(255, 255, 255, 0.86);
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
