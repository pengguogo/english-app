import http from './http'

const auth = () => ({ headers: { 'X-Child-Growth-Key': sessionStorage.getItem('childGrowthKey') || '' } })

export const getChildProfile = () => http.get('/child-growth/profile', auth())
export const updateChildProfile = (data) => http.put('/child-growth/profile', data, auth())
export const getGrowthMeasurements = () => http.get('/child-growth/measurements', auth())
export const createGrowthMeasurement = (data) => http.post('/child-growth/measurements', data, auth())
export const updateGrowthMeasurement = (id, data) => http.put(`/child-growth/measurements/${id}`, data, auth())
export const deleteGrowthMeasurement = (id) => http.delete(`/child-growth/measurements/${id}`, auth())

// ---------- 儿童成长照片(独立相册页) ----------
// 批量场景下单张上传与大文件放宽超时(默认 15s 不够)
export const getChildPhotos = () => http.get('/child-growth/photos', auth())
export const getChildPhotoCategories = () => http.get('/child-growth/photos/categories', auth())
export const addChildPhoto = (data) => http.post('/child-growth/photos', data, { ...auth(), timeout: 60000 })
export const deleteChildPhoto = (id) => http.delete(`/child-growth/photos/${id}`, auth())
export const updateChildPhoto = (id, data) => http.put(`/child-growth/photos/${id}`, data, auth())
export const getChildPhotoImage = (id) => http.get(`/child-growth/photos/${id}/image`, { ...auth(), responseType: 'blob' })
export const getChildPhotoThumbnail = (id) =>
  http.get(`/child-growth/photos/${id}/thumbnail`, { ...auth(), responseType: 'blob', timeout: 60000 })

/**
 * 打包下载照片为 zip。
 * @param {number[]} ids 选中的照片 id;空数组表示下载全部
 * @returns {Promise<Blob>} zip 二进制
 */
export const downloadChildPhotoZip = (ids) =>
  http.get('/child-growth/photos/zip', {
    ...auth(),
    responseType: 'blob',
    // 流式打包大相册可能耗时,放宽到 2 分钟
    timeout: 120000,
    params: ids && ids.length ? { ids: ids.join(',') } : {}
  })
