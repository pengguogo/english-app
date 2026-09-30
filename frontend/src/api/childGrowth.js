import http from './http'

const auth = () => ({ headers: { 'X-Child-Growth-Key': sessionStorage.getItem('childGrowthKey') || '' } })

export const getChildProfile = () => http.get('/child-growth/profile', auth())
export const updateChildProfile = (data) => http.put('/child-growth/profile', data, auth())
export const getGrowthMeasurements = () => http.get('/child-growth/measurements', auth())
export const createGrowthMeasurement = (data) => http.post('/child-growth/measurements', data, auth())
export const updateGrowthMeasurement = (id, data) => http.put(`/child-growth/measurements/${id}`, data, auth())
export const deleteGrowthMeasurement = (id) => http.delete(`/child-growth/measurements/${id}`, auth())
export const getChildPhotos = () => http.get('/child-growth/photos', auth())
export const addChildPhoto = (data) => http.post('/child-growth/photos', data, auth())
export const deleteChildPhoto = (id) => http.delete(`/child-growth/photos/${id}`, auth())
export const updateChildPhoto = (id, data) => http.put(`/child-growth/photos/${id}`, data, auth())
export const getChildPhotoImage = (id) => http.get(`/child-growth/photos/${id}/image`, { ...auth(), responseType: 'blob' })
