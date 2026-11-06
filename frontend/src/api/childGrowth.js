import http from './http'


export const getChildProfile = () => http.get('/child-growth/profile')
export const updateChildProfile = (data) => http.put('/child-growth/profile', data)
export const getGrowthMeasurements = () => http.get('/child-growth/measurements')
export const createGrowthMeasurement = (data) => http.post('/child-growth/measurements', data)
export const updateGrowthMeasurement = (id, data) => http.put(`/child-growth/measurements/${id}`, data)
export const deleteGrowthMeasurement = (id) => http.delete(`/child-growth/measurements/${id}`)

// ---------- 儿童成长照片(独立相册页) ----------
// 批量场景下单张上传与大文件放宽超时(默认 15s 不够)
export const getChildPhotos = () => http.get('/child-growth/photos')
export const getChildPhotoCategories = () => http.get('/child-growth/photos/categories')
export const addChildPhoto = (data) => http.post('/child-growth/photos', data, { timeout: 60000 })
export const deleteChildPhoto = (id) => http.delete(`/child-growth/photos/${id}`)
export const updateChildPhoto = (id, data) => http.put(`/child-growth/photos/${id}`, data)
export const getChildPhotoImage = (id) => http.get(`/child-growth/photos/${id}/image`, { responseType: 'blob' })
export const getChildPhotoThumbnail = (id) =>
  http.get(`/child-growth/photos/${id}/thumbnail`, { responseType: 'blob', timeout: 60000 })

/**
 * 打包下载照片为 zip。
 * @param {number[]} ids 选中的照片 id;空数组表示下载全部
 * @returns {Promise<Blob>} zip 二进制
 */
export const downloadChildPhotoZip = (ids) =>
  http.get('/child-growth/photos/zip', {
    responseType: 'blob',
    // 流式打包大相册可能耗时,放宽到 2 分钟
    timeout: 120000,
    params: ids && ids.length ? { ids: ids.join(',') } : {}
  })
