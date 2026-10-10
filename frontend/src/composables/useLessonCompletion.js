import { completeLesson } from '../api/progress'
import { getLessonsByUnit } from '../api/lesson'
import { getUnitsByTheme } from '../api/unit'
import { findNextReadingLesson } from '../utils/continuousPlayback'

export function useLessonCompletion(state) {
  const { route, router, safeBack, lesson, isSubmitting, isProgressSaved, showReview,
    isComplete, saveError, totalStars, totalBestScore, showMascotFeedback } = state
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

  return { advanceContinuousPlayback, handleReviewPassed, clearContinuousQuery, goBack, finishLesson }
}
