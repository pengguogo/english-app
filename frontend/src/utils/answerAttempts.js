/**
 * 计算一次答题后的学习状态。
 * 首次答错允许重试；第二次结束本题，提示后答对按 70 分计。
 */
export function evaluateAnswerAttempt(attempt, correct) {
  if (correct) {
    return {
      complete: true,
      assisted: attempt > 1,
      score: attempt > 1 ? 70 : 100,
      status: attempt > 1 ? 'assisted-correct' : 'correct'
    }
  }

  if (attempt < 2) {
    return { complete: false, assisted: false, score: 0, status: 'retry' }
  }

  return { complete: true, assisted: true, score: 0, status: 'revealed' }
}
