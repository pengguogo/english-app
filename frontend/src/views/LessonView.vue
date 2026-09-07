<!--
  LessonView.vue - 课时学习页（分发器）
  用途: 加载课时详情，根据 lesson.type 分发到对应的模板组件。
        WORD → WordLesson, SENTENCE → SentenceLesson,
        READING → ReadingLesson, QUIZ → QuizLesson,
        CALCULATE → CalculateLesson, PHONICS → PhonicsLesson,
        DIALOGUE → DialogueLesson。
        公共逻辑（加载、评分、完成提交）由本组件统一管理。
  作者: english-app
  创建日期: 2026-07-20
  修改: 2026-07-21 重构为按 type 分发的路由器
       2026-07-21 新增 PHONICS/DIALOGUE 分发
-->
<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getLessonById, getLessonsByUnit } from '../api/lesson'
import { getUnitsByTheme } from '../api/unit'
import { completeLesson } from '../api/progress'
import { scorePronunciation } from '../api/voice'
import { recordWrongAnswer } from '../api/wrongAnswer'
import { useSafeBack } from '../composables/useSafeBack'
import { stopActiveTts } from '../composables/useTts'
import StarBar from '../components/StarBar.vue'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import MascotFeedback from '../components/MascotFeedback.vue'
import WordLesson from '../components/lesson-templates/WordLesson.vue'
import SentenceLesson from '../components/lesson-templates/SentenceLesson.vue'
import LessonComplete from '../components/lesson-templates/LessonComplete.vue'
import ReadingLesson from '../components/lesson-templates/ReadingLesson.vue'
import QuizLesson from '../components/lesson-templates/QuizLesson.vue'
import CalculateLesson from '../components/lesson-templates/CalculateLesson.vue'
import PhonicsLesson from '../components/lesson-templates/PhonicsLesson.vue'
import DialogueLesson from '../components/lesson-templates/DialogueLesson.vue'
import { findNextReadingLesson } from '../utils/continuousPlayback'

const route = useRoute()
const router = useRouter()
const { safeBack } = useSafeBack()

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
const isProgressSaved = ref(false)
const saveError = ref('')
const mascotFeedback = ref(null)
let lessonLoadVersion = 0
let mascotFeedbackTimer = null

// 记录每个学习项的历史最佳分
const bestScores = ref([])

// QUIZ/CALCULATE 答题记录：每题是否答对
const answerResults = ref([])
// WORD/SENTENCE/DIALOGUE 每项是否至少听过或跟读过
const engagedItems = ref([])

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
  return currentItem.value.word || currentItem.value.sentence || currentItem.value.text || ''
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
const currentItemEngaged = computed(() => engagedItems.value[currentIndex.value] === true)
const isFruitPilot = computed(() => Number(route.query.themeId) === 1)

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
      // 其他未支持的课型
      return null
  }
})

const lessonTemplateProps = computed(() => {
  if (lesson.value?.type !== 'READING') return {}
  return {
    continuousPlayback: Number(route.query.subjectId) === 4,
    autoStartContinuous: route.query.continuous === '1',
    isContinuousAdvancing: isSubmitting.value
  }
})

// ===== 生命周期 =====

onMounted(loadLesson)
watch(() => route.params.lessonId, loadLesson)
onBeforeUnmount(() => {
  stopActiveTts()
  clearMascotFeedback()
})

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
    const phonicsMeta = raw.type === 'PHONICS'
      ? {
          letter: raw.letter,
          pronunciation: raw.pronunciation,
          sound: raw.sound,
          tip: raw.tip
        }
      : {}
    raw.items.forEach((it) => items.push({ ...phonicsMeta, ...it }))
  }
  return { items }
}

/**
 * 加载课时详情并解析 content。
 */
async function loadLesson() {
  stopActiveTts()
  clearMascotFeedback()
  const version = ++lessonLoadVersion
  isLoading.value = true
  errorMsg.value = ''
  currentIndex.value = 0
  isComplete.value = false
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
    bestScores.value = new Array(totalItems.value).fill(0)
    engagedItems.value = new Array(totalItems.value).fill(false)
  } catch (e) {
    if (version !== lessonLoadVersion) return
    errorMsg.value = '加载课时失败,请返回重试'
    console.error('加载课时失败:', e)
  } finally {
    if (version === lessonLoadVersion) {
      isLoading.value = false
    }
  }
}

/**
 * 录音完成回调:调用发音评测接口并更新当前项评分。
 * @param {Blob} wavBlob RecordButton 抛出的 WAV 音频 Blob
 */
async function handleRecorded(wavBlob) {
  if (!currentItem.value || !currentText.value) return
  markCurrentItemEngaged()
  isScoring.value = true
  scoreMessage.value = '评分中...'
  currentScore.value = null
  try {
    const result = await scorePronunciation(wavBlob, currentText.value)
    currentScore.value = result.score
    currentStars.value = scoreToStars(result.score)
    scoreMessage.value = result.feedback || ''
    updateBestScore(currentIndex.value, result.score)
    showMascotFeedback(
      result.score >= 80 ? 'happy' : 'encourage',
      result.score >= 80 ? '发音真清楚，收下一颗星星！' : '已经开口啦，慢慢说会更棒。'
    )
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
 * QUIZ/CALCULATE 答题回调：记录每题对错，计算得分。
 * 答对率 × 100 = 分数，映射到 bestScores 供结算使用。
 * 答错时静默上报错题到后端,失败不影响学习流程。
 * @param {Object} payload 答题结果 { correct, userAnswer, correctAnswer }
 */
function handleAnswered({ correct, userAnswer, correctAnswer, score, assisted, firstWrong = !correct }) {
  answerResults.value[currentIndex.value] = correct
  const resultScore = typeof score === 'number' ? score : (correct ? 100 : 0)
  updateBestScore(currentIndex.value, resultScore)
  // 更新当前显示分数
  currentScore.value = resultScore
  currentStars.value = scoreToStars(resultScore)
  scoreMessage.value = correct
    ? (assisted ? '提示后答对了，再复习一次会更牢！' : '回答正确！')
    : '先听提示，再试一次！'
  showMascotFeedback(
    correct ? 'happy' : 'encourage',
    correct ? '找到正确水果啦！' : '没关系，再听一遍就能找到。'
  )
  // 答错时静默上报错题,便于错题集展示与复习
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

/**
 * 静默上报错题到后端。
 * 内部辅助函数,失败仅打日志,不抛出异常,不影响学习流程。
 * @param {Object} extra 额外的答案信息 { userAnswer, correctAnswer }
 */
async function recordWrongAnswerSilently({ userAnswer, correctAnswer }) {
  try {
    await recordWrongAnswer({
      lessonId: parseInt(route.params.lessonId),
      lessonName: lesson.value?.name || '',
      questionIndex: currentIndex.value,
      questionType: lesson.value?.type,
      // 题目快照,序列化为 JSON 字符串存储,便于错题集展示原题
      questionSnapshot: JSON.stringify(currentItem.value),
      userAnswer: String(userAnswer ?? ''),
      correctAnswer: String(correctAnswer ?? '')
    })
  } catch (e) {
    // 错题上报失败不影响学习流程,仅记录日志
    console.error('记录错题失败:', e)
  }
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
  stopActiveTts()
  clearMascotFeedback()
  if (currentIndex.value < totalItems.value - 1) {
    currentIndex.value++
    resetCurrentScoreState()
  } else {
    isComplete.value = true
    showMascotFeedback('celebrate', '关卡完成，水果贴纸收集成功！')
  }
}

/**
 * 切换到上一个学习项。若已是第一项,则不做任何操作。
 * 重置当前项的评分状态,确保回到上一题时展示干净的答题界面。
 */
function prevItem() {
  stopActiveTts()
  clearMascotFeedback()
  if (currentIndex.value > 0) {
    currentIndex.value--
    resetCurrentScoreState()
  }
}

/**
 * 当前阅读课朗读完毕后保存进度,并继续主题内下一节阅读课。
 * 自动播放只跨课时和单元,不会跨越当前主题。
 */
async function advanceContinuousPlayback() {
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
  } catch (e) {
    console.error('自动进入下一节阅读课失败:', e)
    alert('自动播放下一课失败,请重试')
  } finally {
    isSubmitting.value = false
  }
}

/**
 * 用户主动停止时移除自动续播标记,避免刷新后意外继续播放。
 */
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

/**
 * 完成课时:提交进度并返回主题学习页。
 */
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
  } catch (e) {
    console.error('保存进度失败:', e)
    saveError.value = '保存失败，请检查网络后重试'
  } finally {
    isSubmitting.value = false
  }
}
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
