// 每轮洗牌，目标答案位置不与课程序号绑定。
export function shuffleChoices(items, random = Math.random) {
  const result = [...items]
  for (let i = result.length - 1; i > 0; i--) {
    const j = Math.floor(random() * (i + 1))
    ;[result[i], result[j]] = [result[j], result[i]]
  }
  return result
}

export function characterChoices(target, pool, random = Math.random, imageOnly = false) {
  const others = [...new Map(pool.filter(item => item.word !== target.word
    && (!imageOnly || (item.imageChoice !== false && item.image !== target.image)))
    .map(item => [imageOnly ? item.image : item.word, item])).values()]
  return shuffleChoices([target, ...shuffleChoices(others, random).slice(0, 2)], random)
}

export function recognitionOutcome(wrong, assisted) {
  return wrong ? 'WRONG' : assisted ? 'ASSISTED' : 'INDEPENDENT'
}
