import { scorePronunciation } from '../api/voice'

export function scoreToStars(score) {
  if (score >= 80) return 3
  if (score >= 60) return 2
  if (score >= 40) return 1
  return 0
}

export function useLessonScoring(state) {
  const { lesson, currentItem, currentText, currentIndex, currentScore, currentStars,
    scoreMessage, isScoring, bestScores, getVersion, markCurrentItemEngaged, showMascotFeedback } = state
  let requestVersion = 0
  async function handleRecorded(wavBlob) {
    if (!currentItem.value || !currentText.value) return
    const request = ++requestVersion
    const sourceLesson = lesson.value
    const sourceIndex = currentIndex.value
    const sourceVersion = getVersion()
    markCurrentItemEngaged()
    isScoring.value = true
    scoreMessage.value = '评分中...'
    currentScore.value = null

    try {
      const result = await scorePronunciation(wavBlob, currentText.value)
      if (request !== requestVersion || lesson.value !== sourceLesson || getVersion() !== sourceVersion || currentIndex.value !== sourceIndex) return
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
      if (request !== requestVersion || lesson.value !== sourceLesson || getVersion() !== sourceVersion || currentIndex.value !== sourceIndex) return
      currentStars.value = 0
      scoreMessage.value = '评分失败，请重试，本次不计成绩'
      console.error('发音评测失败:', error)
    } finally {
      if (request === requestVersion && lesson.value === sourceLesson && getVersion() === sourceVersion) isScoring.value = false
    }
  }

  function updateBestScore(index, score) {
    const prevBest = bestScores.value[index] || 0
    bestScores.value[index] = Math.max(prevBest, score)
  }

  function resetCurrentScoreState() {
    requestVersion++
    isScoring.value = false
    currentScore.value = null
    currentStars.value = 0
    scoreMessage.value = ''
  }


  return { handleRecorded, updateBestScore, resetCurrentScoreState }
}
