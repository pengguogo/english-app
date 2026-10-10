// 兼容历史课程格式，并将拼读元数据合并到学习项。
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

export function normalizeContent(raw) {
  const items = []
  if (Array.isArray(raw.words)) {
    raw.words.forEach((word) => {
      const meta = wordMetaMap[word.toLowerCase()] || {}
      items.push({
        word,
        emoji: meta.emoji || '🔤',
        phonetic: meta.phonetic || '',
        translation: meta.translation || ''
      })
    })
  } else if (Array.isArray(raw.sentences)) {
    raw.sentences.forEach((sentence) => {
      items.push({ sentence, emoji: '💬', phonetic: '', translation: '' })
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
    raw.items.forEach(item => items.push({ ...phonicsMeta, ...item }))
  }
  return { items }
}
