import http from './http'

export const getGameAccess = () => http.get('/games/access')

export const recordStudyTime = (seconds) =>
  http.post('/games/study-time', { seconds })

export const unlockGames = (password) =>
  http.post('/games/unlock', { password })
