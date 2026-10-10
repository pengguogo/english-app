import http from './http'

const auth = () => ({ headers: { 'X-Child-Growth-Key': sessionStorage.getItem('childGrowthKey') || '' } })
export const getTodayTask = () => http.get('/learning/today')
export const recordLearningAttempt = data => http.post('/learning/attempts', data)
export const getWeekReport = () => http.get('/parent-settings/week', auth())
export const getParentPasswordStatus = () => http.get('/parent-settings/password', auth())
export const setParentPassword = password => http.put('/parent-settings/password', { password }, auth())
