<!--
  LessonView.vue - 课时核心学习页
  用途:实现"听发音 -> 跟读录音 -> 百度发音评分 -> 切换下一项 -> 完成课时"的完整学习闭环,
       支持单词课(WORD)与句型课(SENTENCE)两种课时类型。
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
 * 加载课时详情并解析 content。
 * 后端 LessonDto.content 是 JSON 字符串(如 '{"items":[...]}' ),
 * 直接访问 .items 会返回 undefined,必须先 JSON.parse 解析为对象。
 */
async function loadLesson() {
  try {
    const data = await getLessonById(route.params.lessonId)
    // content 是 JSON 字符串,解析为对象后才能访问 .items
    if (typeof data.content === 'string') {
      data.content = JSON.parse(data.content)
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
    <div class="top-bar">
      <button class="back-btn" @click="router.back()">← 返回</button>
      <StarBar :stars="isComplete ? totalStars : currentStars" />
    </div>

    <!-- 加载中 -->
    <div v-if="isLoading" class="state-tip">加载中...</div>
    <!-- 加载失败 -->
    <div v-else-if="errorMsg" class="state-tip error">{{ errorMsg }}</div>

    <!-- 学习内容区 -->
    <template v-else>
      <!-- 未完成:展示当前学习项卡片 -->
      <div v-if="!isComplete && currentItem" class="card-area">
        <!-- 进度指示 -->
        <div class="progress">
          第 {{ currentIndex + 1 }} / {{ totalItems }} 项
        </div>

        <!-- 学习项卡片:emoji + 英文 + 音标 + 中文释义 -->
        <div class="item-card">
          <div class="emoji">{{ currentItem.emoji }}</div>
          <h1 class="word">{{ currentText }}</h1>
          <p v-if="currentItem.phonetic" class="phonetic">/{{ currentItem.phonetic }}/</p>
          <p class="translation">{{ currentItem.translation }}</p>
        </div>

        <!-- 操作区:听发音 + 跟读录音 -->
        <div class="actions">
          <AudioButton :text="currentText" />
          <RecordButton @recorded="handleRecorded" />
        </div>

        <!-- 评分结果区:已有分数时展示分数;评分中展示等待文案 -->
        <div v-if="currentScore !== null" class="score-area">
          <p class="score">{{ currentScore }} 分</p>
          <p class="feedback">{{ scoreMessage }}</p>
        </div>
        <div v-else-if="isScoring" class="score-area">
          <p class="feedback">{{ scoreMessage }}</p>
        </div>

        <!-- 下一步 / 完成本课 -->
        <button class="next-btn" @click="nextItem">
          {{ isLastItem ? '完成本课' : '下一步 →' }}
        </button>
      </div>

      <!-- 已完成:结算页 -->
      <div v-else class="complete-area">
        <h2>太棒了!</h2>
        <p>你完成了《{{ lesson.name }}》</p>
        <StarBar :stars="totalStars" />
        <p class="total-score">平均得分:{{ totalBestScore }} 分</p>
        <button
          class="finish-btn"
          :disabled="isSubmitting"
          @click="finishLesson"
        >
          {{ isSubmitting ? '保存中...' : '完成' }}
        </button>
      </div>
    </template>
  </div>
</template>

<style scoped>
/* 页面容器:暖色渐变背景,营造儿童友好的学习氛围 */
.lesson-view {
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(135deg, #fff3e0 0%, #fff8e1 100%);
  box-sizing: border-box;
}

/* 顶部栏:返回按钮靠左,星星靠右 */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.back-btn {
  background: none;
  border: none;
  font-size: 1rem;
  color: #667eea;
  cursor: pointer;
}

/* 状态提示(加载中 / 错误) */
.state-tip {
  text-align: center;
  padding: 40px;
  color: #888;
  font-size: 1.1rem;
}
.state-tip.error { color: #f44336; }

/* 学习区:限制最大宽度,大屏居中 */
.card-area {
  max-width: 480px;
  margin: 0 auto;
}

/* 进度指示 */
.progress {
  text-align: center;
  color: #999;
  font-size: 0.9rem;
  margin-bottom: 12px;
}

/* 学习项卡片:大圆角 + 白底,儿童视觉风格 */
.item-card {
  background: white;
  border-radius: 24px;
  padding: 40px 24px;
  text-align: center;
  margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}
.emoji { font-size: 5rem; margin-bottom: 16px; }
.word {
  font-size: 2.5rem;
  margin-bottom: 8px;
  color: #333;
  word-break: break-word;
}
.phonetic {
  color: #888;
  font-size: 1.2rem;
  margin-bottom: 8px;
}
.translation {
  color: #555;
  font-size: 1.1rem;
}

/* 操作区:两个按钮水平排列 */
.actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-bottom: 24px;
}

/* 评分结果区:暖色背景突出展示 */
.score-area {
  text-align: center;
  background: #fff3e0;
  border-radius: 16px;
  padding: 16px;
  margin-bottom: 24px;
}
.score {
  font-size: 2rem;
  font-weight: bold;
  color: #FF9800;
}
.feedback { color: #666; }

/* 下一步按钮:主色填充,大圆角,触控友好 */
.next-btn {
  width: 100%;
  padding: 16px;
  font-size: 1.1rem;
  border: none;
  border-radius: 24px;
  background: #667eea;
  color: white;
  cursor: pointer;
  transition: background 0.2s;
}
.next-btn:hover { background: #5568d3; }

/* 结算区:卡片式展示,突出成就感 */
.complete-area {
  text-align: center;
  padding: 40px 24px;
  background: white;
  border-radius: 24px;
  max-width: 480px;
  margin: 40px auto;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}
.complete-area h2 {
  font-size: 2rem;
  color: #FF9800;
  margin-bottom: 16px;
}
.complete-area p { color: #555; margin-bottom: 12px; }
.total-score { color: #888; }

/* 完成按钮:绿色,鼓励孩子 */
.finish-btn {
  margin-top: 24px;
  padding: 16px 48px;
  font-size: 1.1rem;
  border: none;
  border-radius: 24px;
  background: #4CAF50;
  color: white;
  cursor: pointer;
  transition: background 0.2s;
}
.finish-btn:hover { background: #43A047; }
.finish-btn:disabled {
  background: #9E9E9E;
  cursor: not-allowed;
}
</style>
