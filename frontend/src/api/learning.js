import http from './http'

export const getTodayTask = () => http.get('/learning/today')
export const recordLearningAttempt = data => http.post('/learning/attempts', data)
export const getWeekReport = () => http.get('/parent-settings/week')
