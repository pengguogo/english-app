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
import ReadingLesson from '../components/lesson-templates/ReadingLesson.vue'
import QuizLesson from '../components/lesson-templates/QuizLesson.vue'
import CalculateLesson from '../components/lesson-templates/CalculateLesson.vue'

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

// QUIZ/CALCULATE 答题记录：每题是否答对
const answerResults = ref([])

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
    case 'READING':
      return ReadingLesson
    case 'QUIZ':
      return QuizLesson
    case 'CALCULATE':
      return CalculateLesson
    default:
      // 其他未支持的课型
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
 * QUIZ/CALCULATE 答题回调：记录每题对错，计算得分。
 * 答对率 × 100 = 分数，映射到 bestScores 供结算使用。
 * @param {boolean} correct 是否答对
 */
function handleAnswered(correct) {
  answerResults.value[currentIndex.value] = correct
  // 答对=100分，答错=0分，复用 bestScores 机制
  updateBestScore(currentIndex.value, correct ? 100 : 0)
  // 更新当前显示分数
  currentScore.value = correct ? 100 : 0
  currentStars.value = scoreToStars(correct ? 100 : 0)
  scoreMessage.value = correct ? '回答正确！' : '答错了，再接再厉！'
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
        @answered="handleAnswered"
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
