import http from './http'

export const getGameAccess = () => http.get('/games/access')

export const recordStudyTime = (lessonId, seconds) =>
  http.post('/games/study-time', { lessonId, seconds })

export const unlockGames = (password) =>
  http.post('/games/unlock', { password })
