import http from './http'

export const getReviewQuestions = (lessonId) => http.get(`/lessons/${lessonId}/review`)
export const submitReview = (lessonId, answers) =>
  http.post(`/lessons/${lessonId}/review`, { answers })
