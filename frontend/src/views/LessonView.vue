<!--
  LessonView.vue - 课时核心学习页
  用途:实现"听发音 -> 跟读录音 -> 百度发音评分 -> 切换下一项 -> 完成课时"的完整学习闭环,
       支持单词课(WORD)与句型课(SENTENCE)两种课时类型。
       UI 美化:暖底背景 + 小老鼠陪伴插画 + 顶部进度条 + 美化评分区 + 结算页庆祝插画。
  作者:english-app
  创建日期:2026-07-20
-->
<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getLessonById } from '../api/lesson'
import { completeLesson } from '../api/progress'
import { scorePronunciation } from '../api/voice'
import StarBar from '../components/StarBar.vue'
import AudioButton from '../components/AudioButton.vue'
import RecordButton from '../components/RecordButton.vue'
import BackBar from '../components/BackBar.vue'
import AppButton from '../components/AppButton.vue'
import mascotCompanion from '../assets/mascot/mascot-companion.jpg'
import mascotCelebrate from '../assets/mascot/mascot-celebrate.jpg'

const route = useRoute()
const router = useRouter()

// ===== 页面状态 =====
const lesson = ref(null)          // 课时详情(加载后 content 已被 JSON.parse 解析为对象)
const isLoading = ref(true)       // 加载中标志,控制加载提示显隐
const errorMsg = ref('')           // 加载错误信息,空字符串表示无错误

const currentIndex = ref(0)        // 当前学习项索引
const currentScore = ref(null)    // 当前项本次评分(null 表示尚未评分)
const currentStars = ref(0)       // 当前项本次获得星星数
const scoreMessage = ref('')      // 评分反馈文案
const isScoring = ref(false)      // 评分请求中标志,防止重复触发
const isComplete = ref(false)      // 课时是否进入完成结算状态
const isSubmitting = ref(false)   // 完成课时提交中标志,防止重复提交

// 记录每个学习项的历史最佳分,用于最终结算
// bestScores[i] = 第 i 项历史最高分(0-100),允许孩子多次重试取最佳
const bestScores = ref([])

// ===== 计算属性 =====

/**
 * 当前学习项。
 * lesson.value.content 已在 onMounted 中被 JSON.parse 解析为 { items: [...] },
 * 这里通过索引返回当前 item;边界情况下回退到第一项,避免 undefined 引发模板异常。
 */
const currentItem = computed(() => {
  if (!lesson.value || !lesson.value.content) return null
  const items = lesson.value.content.items
  if (!Array.isArray(items) || items.length === 0) return null
  return items[currentIndex.value] || items[0]
})

/**
 * 当前学习项对应的英文文本。
 * WORD 类型有 word 字段,SENTENCE 类型有 sentence 字段,
 * 统一取出用于 AudioButton 播放与 scorePronunciation 评测对照。
 */
const currentText = computed(() => {
  if (!currentItem.value) return ''
  return currentItem.value.word || currentItem.value.sentence || ''
})

/**
 * 学习项总数,用于进度展示与边界判断。
 */
const totalItems = computed(() => {
  if (!lesson.value || !lesson.value.content) return 0
  return lesson.value.content.items?.length ?? 0
})

/**
 * 当前学习进度百分比(0-100),用于顶部进度条填充。
 */
const progressPercent = computed(() => {
  if (totalItems.value === 0) return 0
  return Math.round(((currentIndex.value + 1) / totalItems.value) * 100)
})

/**
 * 是否为最后一个学习项,用于切换按钮文案与完成判断。
 */
const isLastItem = computed(() => currentIndex.value >= totalItems.value - 1)

/**
 * 整个课时累计最佳分数(各 item 历史最佳成绩的平均值)。
 * 完成课时提交时使用此分数,而非最后一项的临时分数,
 * 以反映孩子整堂课的综合表现,并允许针对单项多次重试。
 */
const totalBestScore = computed(() => {
  const validScores = bestScores.value.filter((s) => typeof s === 'number')
  if (validScores.length === 0) return 0
  const sum = validScores.reduce((acc, s) => acc + s, 0)
  return Math.round(sum / validScores.length)
})

/**
 * 整个课时最终获得的星星数,基于平均分计算。
 * >= 80 三星,>= 60 两星,>= 40 一星,否则 0 星。
 */
const totalStars = computed(() => scoreToStars(totalBestScore.value))

// ===== 生命周期 =====

onMounted(loadLesson)

// ===== 业务方法 =====

/**
 * 根据分数计算星级(0-3)。
 * 阈值与产品规则一致:80/60/40,低于 40 视为未达星。
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
 * 简单的英文单词到 emoji 映射表(常见水果与交通工具)。
 * 后端 content 仅返回单词字符串,前端补充 emoji 与释义以丰富儿童视觉。
 * 未命中的单词回退为 🔤 通用图标。
 */
const wordMetaMap = {
  // 水果
  apple: { emoji: '🍎', translation: '苹果', phonetic: 'ˈæpl' },
  banana: { emoji: '🍌', translation: '香蕉', phonetic: 'bəˈnɑːnə' },
  orange: { emoji: '🍊', translation: '橙子', phonetic: 'ˈɒrɪndʒ' },
  grape: { emoji: '🍇', translation: '葡萄', phonetic: 'greɪp' },
  // 陆地交通
  car: { emoji: '🚗', translation: '小汽车', phonetic: 'kɑː' },
  bus: { emoji: '🚌', translation: '公交车', phonetic: 'bʌs' },
  bike: { emoji: '🚲', translation: '自行车', phonetic: 'baɪk' },
  train: { emoji: '🚂', translation: '火车', phonetic: 'treɪn' },
  // 天空交通
  plane: { emoji: '✈️', translation: '飞机', phonetic: 'pleɪn' },
  helicopter: { emoji: '🚁', translation: '直升机', phonetic: 'ˈhelɪkɒptə' },
  balloon: { emoji: '🎈', translation: '热气球', phonetic: 'bəˈluːn' },
  rocket: { emoji: '🚀', translation: '火箭', phonetic: 'ˈrɒkɪt' },
  // 水上交通
  boat: { emoji: '⛵', translation: '小船', phonetic: 'bəʊt' },
  ship: { emoji: '🚢', translation: '大船', phonetic: 'ʃɪp' },
  submarine: { emoji: '🤿', translation: '潜水艇', phonetic: 'ˌsʌbməˈriːn' }
}

/**
 * 将后端 content 原始格式统一转换为 { items: [...] } 结构。
 * 后端单词课返回 { words: ["apple", ...] },
 * 句型课返回 { sentences: ["I have a car", ...] },
 * 这里转换为统一的 item 对象数组,补齐 emoji/phonetic/translation。
 * @param {Object} raw 解析后的 content 对象
 * @return {Object} { items: [...] }
 */
function normalizeContent(raw) {
  const items = []
  if (Array.isArray(raw.words)) {
    // 单词课:每个单词生成一个 item
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
    // 句型课:每个句子生成一个 item
    raw.sentences.forEach((s) => {
      items.push({ sentence: s, emoji: '💬', phonetic: '', translation: '' })
    })
  } else if (Array.isArray(raw.items)) {
    // 已是标准格式,直接使用
    raw.items.forEach((it) => items.push(it))
  }
  return { items }
}

/**
 * 加载课时详情并解析 content。
 * 后端 LessonDto.content 是 JSON 字符串(如 '{"words":["apple"]}' ),
 * 必须先 JSON.parse 再通过 normalizeContent 统一为 items 结构。
 */
async function loadLesson() {
  try {
    const data = await getLessonById(route.params.lessonId)
    // content 是 JSON 字符串,解析后通过 normalizeContent 统一为 items 结构
    if (typeof data.content === 'string') {
      data.content = normalizeContent(JSON.parse(data.content))
    } else if (data.content && typeof data.content === 'object') {
      data.content = normalizeContent(data.content)
    }
    lesson.value = data
    // 初始化每个 item 的最佳分槽位为 0,后续取 Math.max 更新
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
 * 同时更新该项的历史最佳分,确保最终结算反映孩子最好的发挥。
 * @param {Blob} wavBlob RecordButton 抛出的 WAV 音频 Blob(16kHz/16bit/mono)
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
    // 记录该项历史最高分,用于最终结算平均
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
 * 单独抽出便于阅读与后续可能的扩展(如多轮平均)。
 * @param {number} index 学习项索引
 * @param {number} score 本次评分
 */
function updateBestScore(index, score) {
  const prevBest = bestScores.value[index] || 0
  bestScores.value[index] = Math.max(prevBest, score)
}

/**
 * 重置当前项的评分展示状态。
 * 切换 item 或重新跟读前调用,避免旧分数误导孩子。
 */
function resetCurrentScoreState() {
  currentScore.value = null
  currentStars.value = 0
  scoreMessage.value = ''
}

/**
 * 切换到下一个学习项。
 * 若已是最后一项,则进入完成结算页。
 */
function nextItem() {
  if (currentIndex.value < totalItems.value - 1) {
    currentIndex.value++
    resetCurrentScoreState()
  } else {
    // 所有 item 学习完毕,进入完成结算页
    isComplete.value = true
  }
}

/**
 * 完成课时:提交进度并导航回单元列表页。
 * 使用 router.push 到具体 unit 路径,比 router.back() 更可靠,
 * 避免从书签等入口直接进入时无历史记录可返回的情况。
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
    // 导航到该课时所属的单元页,确保返回正确的列表上下文
    router.push(`/unit/${lesson.value.unitId}`)
  } catch (e) {
    console.error('保存进度失败:', e)
    // 失败时给出提示并允许重试,不跳转
    alert('保存失败,请重试')
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <div class="lesson-view">
    <!-- 顶部栏:返回按钮 + 当前星星(结算时展示累计星星) -->
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
      <!-- 未完成:展示当前学习项卡片 -->
      <div v-if="!isComplete && currentItem" class="card-area">
        <!-- 顶部进度条 + 数字标注 -->
        <div class="progress-section">
          <div class="progress-info">
            <span>第 {{ currentIndex + 1 }} / {{ totalItems }} 项</span>
          </div>
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
          </div>
        </div>

        <!-- 小老鼠陪伴插画(右上角) -->
        <img :src="mascotCompanion" alt="小老鼠陪伴" class="mascot-companion" />

        <!-- 学习项卡片:emoji/图片 + 英文 + 音标 + 中文释义 -->
        <div class="item-card">
          <div v-if="currentItem.image" class="word-image-wrapper">
            <img :src="currentItem.image" :alt="currentItem.word" class="word-image" />
          </div>
          <div v-else class="emoji">{{ currentItem.emoji }}</div>
          <h1 class="word">{{ currentText }}</h1>
          <p v-if="currentItem.phonetic" class="phonetic">/{{ currentItem.phonetic }}/</p>
          <p class="translation">{{ currentItem.translation }}</p>
        </div>

        <!-- 操作区:听发音 + 跟读录音 -->
        <div class="actions">
          <AudioButton :text="currentText" :translation="currentItem.translation || ''" />
          <RecordButton @recorded="handleRecorded" />
        </div>

        <!-- 评分结果区:已有分数时展示分数;评分中展示等待文案 -->
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

        <!-- 下一步 / 完成本课 -->
        <AppButton
          variant="primary"
          size="lg"
          block
          @click="nextItem"
        >
          {{ isLastItem ? '完成本课' : '下一步 →' }}
        </AppButton>
      </div>

      <!-- 已完成:结算页 -->
      <div v-else class="complete-area">
        <!-- 小老鼠庆祝插画(主视觉) -->
        <img :src="mascotCelebrate" alt="小老鼠庆祝" class="mascot-celebrate" />

        <h2>太棒了!</h2>
        <p class="complete-lesson-name">你完成了《{{ lesson.name }}》</p>

        <StarBar :stars="totalStars" size="lg" />

        <p class="total-score">平均得分:{{ totalBestScore }} 分</p>

        <AppButton
          variant="success"
          size="lg"
          :disabled="isSubmitting"
          @click="finishLesson"
        >
          {{ isSubmitting ? '保存中...' : '完成' }}
        </AppButton>
      </div>
    </template>
  </div>
</template>

<style scoped>
/* 页面容器:暖色背景,营造儿童友好的学习氛围 */
.lesson-view {
  min-height: 100vh;
  padding: var(--space-4);
  background: var(--gradient-warm);
  box-sizing: border-box;
  position: relative;
}

/* ===== 状态提示 ===== */
.state-tip {
  text-align: center;
  padding: var(--space-8);
  color: var(--text-tertiary);
}
.state-tip.error { color: var(--color-warning); }

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

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ===== 学习区 ===== */
.card-area {
  max-width: 480px;
  margin: 0 auto;
  position: relative;
}

/* 顶部进度条 */
.progress-section {
  margin-bottom: var(--space-5);
}

.progress-info {
  text-align: center;
  color: var(--text-tertiary);
  font-size: var(--text-sm);
  margin-bottom: var(--space-2);
}

.progress-bar {
  height: 8px;
  background: var(--bg-card);
  border-radius: var(--radius-pill);
  overflow: hidden;
  box-shadow: var(--shadow-soft);
}

.progress-fill {
  height: 100%;
  background: var(--gradient-primary);
  border-radius: var(--radius-pill);
  transition: width var(--duration-slow) var(--ease-smooth);
}

/* 小老鼠陪伴插画: 右上角,不遮挡内容 */
.mascot-companion {
  position: absolute;
  top: 0;
  right: 0;
  width: 80px;
  height: 80px;
  object-fit: contain;
  border-radius: var(--radius-md);
  z-index: 1;
  opacity: 0.9;
}

/* 学习项卡片:大圆角 + 白底,儿童视觉风格 */
.item-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--space-8) var(--space-6);
  text-align: center;
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-card);
}

.emoji {
  font-size: 5rem;
  margin-bottom: var(--space-4);
}

/* 单词图片展示(替代 emoji):圆角卡片内居中 */
.word-image-wrapper {
  margin-bottom: var(--space-4);
}

.word-image {
  width: 180px;
  height: 180px;
  object-fit: cover;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-soft);
}

.word {
  font-size: 2.5rem;
  margin-bottom: var(--space-2);
  color: var(--text-primary);
  word-break: break-word;
  font-weight: var(--font-bold);
}

.phonetic {
  color: var(--text-secondary);
  font-size: var(--text-lg);
  margin-bottom: var(--space-2);
}

.translation {
  color: var(--text-secondary);
  font-size: var(--text-base);
}

/* 操作区:两个按钮水平排列 */
.actions {
  display: flex;
  gap: var(--space-4);
  justify-content: center;
  margin-bottom: var(--space-6);
}

/* 评分结果区 */
.score-area {
  text-align: center;
  background: var(--bg-card);
  border-radius: var(--radius-md);
  padding: var(--space-4);
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-soft);
}

.score-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-3);
  margin-bottom: var(--space-2);
}

/* 得分数字: 橙色大号字 + 弹跳动效 */
.score {
  font-size: 2rem;
  font-weight: var(--font-bold);
  color: var(--color-orange);
}

@media (prefers-reduced-motion: no-preference) {
  .score {
    animation: scoreBounce var(--duration-normal) var(--ease-bounce) both;
  }
}

@keyframes scoreBounce {
  0% {
    transform: translateY(8px) scale(0.8);
    opacity: 0;
  }
  60% {
    transform: translateY(-4px) scale(1.1);
    opacity: 1;
  }
  100% {
    transform: translateY(0) scale(1);
    opacity: 1;
  }
}

.feedback {
  color: var(--text-secondary);
  font-size: var(--text-sm);
}

/* 评分中状态 */
.score-area.scoring {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-3);
}

.scoring-dot {
  width: 16px;
  height: 16px;
  border: 2px solid var(--border-light);
  border-top-color: var(--color-orange);
  border-radius: var(--radius-pill);
}

@media (prefers-reduced-motion: no-preference) {
  .scoring-dot {
    animation: spin 0.8s linear infinite;
  }
}

/* ===== 结算区 ===== */
.complete-area {
  text-align: center;
  padding: var(--space-6) var(--space-4);
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  max-width: 480px;
  margin: var(--space-6) auto;
  box-shadow: var(--shadow-card);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);
}

/* 小老鼠庆祝插画: 主视觉 */
.mascot-celebrate {
  width: 160px;
  height: 160px;
  object-fit: contain;
  border-radius: var(--radius-md);
}

@media (prefers-reduced-motion: no-preference) {
  .mascot-celebrate {
    animation: celebrateBounce var(--duration-slow) var(--ease-bounce) both;
  }
}

@keyframes celebrateBounce {
  0% {
    transform: scale(0) rotate(-180deg);
    opacity: 0;
  }
  60% {
    transform: scale(1.15) rotate(10deg);
    opacity: 1;
  }
  100% {
    transform: scale(1) rotate(0);
    opacity: 1;
  }
}

.complete-area h2 {
  font-size: var(--text-xl);
  color: var(--color-orange);
  font-weight: var(--font-bold);
}

.complete-lesson-name {
  color: var(--text-secondary);
  font-size: var(--text-base);
}

.total-score {
  color: var(--text-tertiary);
  font-size: var(--text-sm);
}

/* ===== 响应式: 手机端缩小吉祥物 ===== */
@media (max-width: 480px) {
  .mascot-companion { width: 64px; height: 64px; }
  .mascot-celebrate { width: 128px; height: 128px; }
  .word { font-size: 2rem; }
  .emoji { font-size: 4rem; }
}
</style>
