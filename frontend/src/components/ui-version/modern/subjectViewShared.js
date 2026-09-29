/**
 * @description 学科页共享的文案与配色配置。
 */
export const subjectColors = {
  1: 'var(--subject-english)',
  2: 'var(--subject-chinese)',
  3: 'var(--subject-math)',
  4: 'var(--subject-extracurricular)'
}

export const subjectNames = {
  1: '英语',
  2: '语文',
  3: '数学',
  4: '课外'
}

export function getSubjectLabel(subjectId) {
  return subjectNames[subjectId] || '学习'
}

export function getSubjectColor(subjectId) {
  return subjectColors[subjectId] || 'var(--color-primary)'
}
