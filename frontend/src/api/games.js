import http from './http'

export const getGameAccess = () => http.get('/games/access')

// keepalive 允许离页时仍发送不足15秒的尾段；事件ID用于服务端去重。
export async function recordStudyTime(lessonId, seconds, eventId) {
  const response = await fetch('/api/v1/games/study-time', {
    method: 'POST', headers: { 'Content-Type': 'application/json' }, keepalive: true,
    body: JSON.stringify({ lessonId, seconds, eventId })
  })
  const result = await response.json()
  if (!response.ok || result.code !== 200) throw new Error(result.message || '记录学习时长失败')
  return result.data
}

export const unlockGames = (password) =>
  http.post('/games/unlock', { password })
