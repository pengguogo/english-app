/** 本机续学优先，其次采用服务端错题或未完成课程建议。 */
export function chooseTodayTask(storage, suggestion) {
  try {
    const saved = JSON.parse(storage.getItem('lastLesson') || 'null')
    if (Number.isInteger(saved?.id) && typeof saved.name === 'string'
        && storage.getItem(`lesson:${saved.id}`)) {
      return { title: `继续：${saved.name}`, reason: '接着上次的位置学习', path: `/lesson/${saved.id}`, query: saved.query }
    }
  } catch { /* 本机存储不可用时使用服务端建议。 */ }
  return suggestion
}
