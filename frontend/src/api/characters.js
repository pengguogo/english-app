import http from './http'

export const getCharacterProgress = () => http.get('/characters/progress')
export const getCharacterReview = () => http.get('/characters/review')
export const recordCharacterAttempt = request => http.post('/characters/attempts', request)
