/**
 * useChildPhotos.js - 成长照片页数据与操作管理
 * 用途: 集中管理照片列表(缩略图预览)、分类标签、多选状态、批量上传、
 *       zip 下载与单张编辑/删除,让 ChildPhotosView 只负责展示,符合单一职责。
 * 作者: english-app
 * 创建日期: 2026-09-30
 */
import { computed, onUnmounted, ref } from 'vue'
import {
  addChildPhoto, deleteChildPhoto, downloadChildPhotoZip,
  getChildPhotoCategories, getChildPhotoImage, getChildPhotoThumbnail,
  getChildPhotos, updateChildPhoto
} from '../api/childGrowth'
import { today } from '../utils/date'

/**
 * 将 blob 保存为本地文件(临时 <a> 触发浏览器下载)。
 * @param {Blob} blob 文件内容
 * @param {String} fileName 保存的文件名
 */
function saveBlob(blob, fileName) {
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  link.click()
  URL.revokeObjectURL(url)
}

/**
 * 成长照片组合式函数。
 * @returns {Object} 照片列表、分类、选择状态与全部动作函数
 */
export function useChildPhotos() {
  // ===== 页面数据 =====
  const photos = ref([])
  const categories = ref([])

  // ===== 流程状态 =====
  const loading = ref(false)
  const downloading = ref(false)
  const error = ref('')
  const notice = ref('')

  // ===== 多选状态: 保存照片 id,供批量下载 =====
  const selectedIds = ref([])
  const selectedCount = computed(() => selectedIds.value.length)

  // blob URL 登记: 缩略图随列表整体重建;原图供灯箱按需加载并长期缓存
  const thumbUrls = new Map()
  const originUrls = new Map()

  /**
   * 拉取照片列表与分类标签,并为每张照片生成缩略图预览地址。
   */
  async function loadPhotos() {
    loading.value = true
    error.value = ''
    try {
      const [items, tags] = await Promise.all([getChildPhotos(), getChildPhotoCategories()])
      for (const url of thumbUrls.values()) URL.revokeObjectURL(url)
      thumbUrls.clear()
      // 并发请求缩略图;单张失败仅降级为空地址,不阻塞整个列表
      photos.value = await Promise.all(items.map(async item => {
        try {
          const src = URL.createObjectURL(await getChildPhotoThumbnail(item.id))
          thumbUrls.set(item.id, src)
          return { ...item, src }
        } catch {
          return { ...item, src: '' }
        }
      }))
      categories.value = tags
    } catch (e) {
      error.value = e.response?.data?.message || '照片加载失败，请重试'
    } finally {
      loading.value = false
    }
  }

  /**
   * 按需加载某张照片的原图(灯箱使用),结果缓存避免重复请求。
   * @param {Number} id 照片 id
   * @returns {Promise<String>} 原图 blob URL
   */
  async function loadOrigin(id) {
    if (originUrls.has(id)) return originUrls.get(id)
    const url = URL.createObjectURL(await getChildPhotoImage(id))
    originUrls.set(id, url)
    return url
  }

  /**
   * 删除照片后同步释放其原图缓存,避免 blob URL 泄漏。
   * @param {Number} id 照片 id
   */
  function discardOrigin(id) {
    const url = originUrls.get(id)
    if (url) {
      URL.revokeObjectURL(url)
      originUrls.delete(id)
    }
  }

  // ===== 多选操作 =====

  /**
   * 判断某张照片是否处于选中状态。
   * @param {Number} id 照片 id
   * @returns {Boolean} 是否已选中
   */
  function isSelected(id) {
    return selectedIds.value.includes(id)
  }

  /**
   * 切换某张照片的选中状态。
   * @param {Number} id 照片 id
   */
  function toggleSelect(id) {
    const next = new Set(selectedIds.value)
    if (next.has(id)) next.delete(id)
    else next.add(id)
    selectedIds.value = [...next]
  }

  /**
   * 全选/取消全选当前可见照片(不影响其他筛选结果里已选中的照片)。
   * @param {Array} visibleIds 当前筛选后可见的照片 id 列表
   */
  function toggleSelectAll(visibleIds) {
    const next = new Set(selectedIds.value)
    const allSelected = visibleIds.length > 0 && visibleIds.every(id => next.has(id))
    if (allSelected) visibleIds.forEach(id => next.delete(id))
    else visibleIds.forEach(id => next.add(id))
    selectedIds.value = [...next]
  }

  /** 清空全部选择。 */
  function clearSelection() {
    selectedIds.value = []
  }

  // ===== 批量动作 =====

  /**
   * 批量上传: 逐张顺序提交,避免并发请求压满服务端;单张失败不中断整体。
   * @param {Object} payload { takenAt, category, files } 弹窗提交内容
   * @param {Function} onProgress 每完成一张回调 (done, total, failed)
   * @returns {Promise<Object>} 上传结果 { total, failed }
   */
  async function uploadBatch(payload, onProgress) {
    let failed = 0
    for (let done = 0; done < payload.files.length; done++) {
      const form = new FormData()
      form.append('takenAt', payload.takenAt)
      form.append('category', payload.category)
      form.append('image', payload.files[done])
      try {
        await addChildPhoto(form)
      } catch {
        failed++
      }
      onProgress?.(done + 1, payload.files.length, failed)
    }
    await loadPhotos()
    return { total: payload.files.length, failed }
  }

  /**
   * 下载 zip: 优先打包选中照片,无选中时打包全部。
   */
  async function downloadZip() {
    downloading.value = true
    error.value = ''
    try {
      const ids = selectedIds.value
      // blob 类型的错误响应无法解析出 message,统一给出友好文案
      const blob = await downloadChildPhotoZip(ids.length ? ids : null)
      saveBlob(blob, `成长照片-${today()}.zip`)
      notice.value = ids.length ? `已下载 ${ids.length} 张照片` : '已下载全部照片'
    } catch {
      error.value = '照片下载失败，请稍后重试'
    } finally {
      downloading.value = false
    }
  }

  /**
   * 删除一张照片(带确认),并清理其选择状态与原图缓存。
   * @param {Object} item 待删除的照片
   */
  async function removePhoto(item) {
    if (!window.confirm(`删除 ${item.takenAt} 的照片？`)) return
    error.value = ''
    notice.value = ''
    try {
      await deleteChildPhoto(item.id)
      discardOrigin(item.id)
      // 同步移除选择,避免已删除 id 残留导致批量下载 404
      selectedIds.value = selectedIds.value.filter(id => id !== item.id)
      await loadPhotos()
      notice.value = '照片已删除'
    } catch (e) {
      error.value = e.response?.data?.message || '照片删除失败'
    }
  }

  /**
   * 更新照片信息(日期/说明/分类)。
   * @param {Object} photo 待修改的照片
   * @param {Object} data { takenAt, caption, category }
   * @returns {Promise<Boolean>} 成功返回 true,失败返回 false(视图保持弹窗打开)
   */
  async function updatePhoto(photo, data) {
    error.value = ''
    try {
      await updateChildPhoto(photo.id, data)
      await loadPhotos()
      return true
    } catch (e) {
      error.value = e.response?.data?.message || '照片信息保存失败，请检查输入'
      return false
    }
  }

  // 组件卸载时释放全部照片 blob URL,防止内存泄漏
  onUnmounted(() => {
    for (const url of thumbUrls.values()) URL.revokeObjectURL(url)
    for (const url of originUrls.values()) URL.revokeObjectURL(url)
  })

  return {
    photos, categories, loading, downloading, error, notice,
    selectedIds, selectedCount,
    loadPhotos, loadOrigin,
    isSelected, toggleSelect, toggleSelectAll, clearSelection,
    uploadBatch, downloadZip, removePhoto, updatePhoto
  }
}
