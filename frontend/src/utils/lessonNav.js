/**
 * 课程页返回/完成后的目标：有主题信息回主题页，否则回首页。不会指向单元页。
 * @param {Record<string, any>} query 课程页的 route.query
 * @returns {{ path: string, query?: Record<string, string> }}
 */
export function buildThemeDestination(query = {}) {
  if (!query.themeId) return { path: '/' }
  const next = {}
  for (const key of ['themeName', 'subjectId', 'subjectName']) {
    if (query[key]) next[key] = String(query[key])
  }
  return { path: `/theme/${query.themeId}`, query: next }
}
