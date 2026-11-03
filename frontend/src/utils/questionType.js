/**
 * 错题类型中文名。键与后端 com.englishapp.domain.enums.LessonType 保持一致。
 */
const QUESTION_TYPE_TEXT = {
  WORD: '单词',
  SENTENCE: '句子',
  READING: '阅读',
  QUIZ: '选择题',
  CALCULATE: '计算题',
  GAME: '游戏',
  SPEAK: '口语',
  PHONICS: '自然拼读',
  DIALOGUE: '对话'
}

/**
 * @param {string} type 后端 questionType 枚举值
 * @returns {string} 中文名；未知类型返回"题目"，不露出原始枚举
 */
export function getQuestionTypeText(type) {
  return QUESTION_TYPE_TEXT[String(type ?? '').toUpperCase()] || '题目'
}
