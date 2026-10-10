import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getLessonById } from '../api/lesson'
import { recordWrongAnswer } from '../api/wrongAnswer'
import { useSafeBack } from './useSafeBack'
import { stopActiveTts } from './useTts'
import { useLessonBookmark } from './useLessonBookmark'
import { useStudyTimer } from './useStudyTimer'
import { useLearningAttempts } from './useLearningAttempts'
import { normalizeContent } from '../utils/lessonContent'
import { useLessonScoring, scoreToStars } from './useLessonScoring'
import { useLessonCompletion } from './useLessonCompletion'
import WordLesson from '../components/lesson-templates/WordLesson.vue'
import CharacterLesson from '../components/lesson-templates/CharacterLesson.vue'
import SentenceLesson from '../components/lesson-templates/SentenceLesson.vue'
import ReadingLesson from '../components/lesson-templates/ReadingLesson.vue'
import QuizLesson from '../components/lesson-templates/QuizLesson.vue'
import CalculateLesson from '../components/lesson-templates/CalculateLesson.vue'
import PhonicsLesson from '../components/lesson-templates/PhonicsLesson.vue'
import DialogueLesson from '../components/lesson-templates/DialogueLesson.vue'

/** 新旧界面共用课程加载、学习状态和完成流程。 */
export function useLessonLearning() {
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


  const state = { route, router, safeBack, lesson, isLoading, errorMsg, currentIndex, currentScore,
      currentStars, scoreMessage, isScoring, isComplete, showReview, isSubmitting, isProgressSaved,
      saveError, mascotFeedback, bestScores, engagedItems, currentItem, currentText, totalItems,
      isLastItem, currentItemEngaged, totalBestScore, totalStars, lessonTemplate, lessonTemplateProps,
      markCurrentItemEngaged, showMascotFeedback, getVersion: () => lessonLoadVersion }
  const { handleRecorded, updateBestScore, resetCurrentScoreState } = useLessonScoring(state)
  const completion = useLessonCompletion(state)
  const { recordAttempt } = useLearningAttempts(lesson, currentIndex)
  onMounted(loadLesson)
  watch(() => route.params.lessonId, loadLesson)
  onBeforeUnmount(() => {
    lessonLoadVersion++
    stopActiveTts()
    clearMascotFeedback()
  })
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

  function handleAnswered({ correct, userAnswer, correctAnswer, score, assisted, firstWrong = !correct }) {
    recordAttempt({ correct, assisted })
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


  return { route, router, safeBack, lesson, isLoading, errorMsg, currentIndex, currentScore,
      currentStars, scoreMessage, isScoring, isComplete, showReview, isSubmitting, isProgressSaved,
      saveError, mascotFeedback, bestScores, engagedItems, currentItem, currentText, totalItems,
      isLastItem, currentItemEngaged, totalBestScore, totalStars, lessonTemplate, lessonTemplateProps,
      markCurrentItemEngaged, showMascotFeedback, loadLesson, handleRecorded, handleAnswered, handleListened,
    skipCurrentItem, nextItem, prevItem, ...completion }
}
