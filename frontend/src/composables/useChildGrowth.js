/**
 * useChildGrowth.js - 孩子成长档案数据与弹窗流程管理
 * 用途: 集中管理成长档案的资料/测量记录/照片的加载、增删改与三个编辑弹窗的
 *       开关状态,让 ChildProfileView 只负责展示,符合单一职责。
 * 作者: english-app
 * 创建日期: 2026-09-30
 */
import { onUnmounted, reactive, ref } from 'vue'
import {
  getChildProfile, updateChildProfile, getGrowthMeasurements,
  createGrowthMeasurement, updateGrowthMeasurement, deleteGrowthMeasurement,
  getChildPhotos, addChildPhoto, deleteChildPhoto, updateChildPhoto, getChildPhotoImage
} from '../api/childGrowth'

/**
 * 成长档案组合式函数。
 * @returns {Object} 页面数据、流程状态、弹窗状态与全部动作函数
 */
export function useChildGrowth() {
  // ===== 页面数据 =====
  const profile = reactive({ nickname: '', birthDate: '', sex: '' })
  const records = ref([])
  const photos = ref([])

  // ===== 流程状态 =====
  const unlocked = ref(false)
  const loading = ref(false)
  const saving = ref(false)
  const pageError = ref('')
  const notice = ref('')

  // ===== 弹窗状态 =====
  // 三个弹窗互斥打开;editingXxx 为 null 表示"新增"模式,非 null 表示"修改"模式
  const profileOpen = ref(false)
  const measurementOpen = ref(false)
  const photoOpen = ref(false)
  const editingRecord = ref(null)
  const editingPhoto = ref(null)
  // 弹窗内展示的接口错误(保存失败时弹窗保持打开)
  const modalError = ref('')

  // 照片 blob URL 登记: 便于整体回收,防止内存泄漏
  const photoUrls = new Map()

  /**
   * 将照片列表转为带本地预览地址的对象数组。
   * @param {Array} items 接口返回的照片元数据列表
   */
  async function setPhotos(items) {
    for (const url of photoUrls.values()) URL.revokeObjectURL(url)
    photoUrls.clear()
    photos.value = await Promise.all(items.map(async item => {
      const blob = await getChildPhotoImage(item.id)
      const src = URL.createObjectURL(blob)
      photoUrls.set(item.id, src)
      return { ...item, src }
    }))
  }

  /**
   * 加载全部档案数据;口令错误(401)时回到解锁界面。
   */
  async function load() {
    loading.value = true
    pageError.value = ''
    try {
      const [child, measurements, savedPhotos] = await Promise.all([
        getChildProfile(), getGrowthMeasurements(), getChildPhotos()
      ])
      Object.assign(profile, {
        nickname: child.nickname || '',
        birthDate: child.birthDate || '',
        sex: child.sex || ''
      })
      records.value = measurements
      await setPhotos(savedPhotos)
      unlocked.value = true
    } catch (e) {
      if (e.response?.status === 401) unlocked.value = false
      pageError.value = e.response?.data?.message || '加载失败，请重试'
    } finally {
      loading.value = false
    }
  }

  /**
   * 解锁档案: 写入会话存储(接口层自动携带)后加载数据。
   * @param {String} accessKey 家长访问口令
   * @returns {Promise} 加载结果
   */
  function unlock(accessKey) {
    sessionStorage.setItem('childGrowthKey', accessKey)
    return load()
  }

  // ===== 弹窗开关: 打开前清空上一次的接口错误 =====
  function openProfile() {
    modalError.value = ''
    profileOpen.value = true
  }
  function closeProfile() { profileOpen.value = false }

  /**
   * 打开测量记录弹窗。
   * @param {Object|null} record 待修改的记录,null 表示新增
   */
  function openMeasurement(record = null) {
    modalError.value = ''
    editingRecord.value = record
    measurementOpen.value = true
  }
  function closeMeasurement() { measurementOpen.value = false }

  /**
   * 打开照片弹窗。
   * @param {Object|null} photo 待修改的照片,null 表示新增
   */
  function openPhoto(photo = null) {
    modalError.value = ''
    editingPhoto.value = photo
    photoOpen.value = true
  }
  function closePhoto() { photoOpen.value = false }

  // ===== 保存动作: 成功关弹窗,失败留在弹窗内提示 =====

  /**
   * 保存孩子资料。
   * @param {Object} data 弹窗提交的 { nickname, birthDate, sex }
   */
  async function saveProfile(data) {
    modalError.value = ''
    saving.value = true
    try {
      // 后端约定: 空字符串需转为 null 存储
      await updateChildProfile({ ...data, birthDate: data.birthDate || null, sex: data.sex || null })
      Object.assign(profile, data)
      profileOpen.value = false
      notice.value = '孩子资料已保存'
    } catch (e) {
      modalError.value = e.response?.data?.message || '资料保存失败'
    } finally {
      saving.value = false
    }
  }

  /**
   * 保存测量记录(新增或修改,取决于当前弹窗模式)。
   * @param {Object} data 弹窗提交的 { measuredAt, heightCm, weightKg, note }
   */
  async function saveMeasurement(data) {
    modalError.value = ''
    saving.value = true
    const payload = {
      measuredAt: data.measuredAt,
      heightCm: data.heightCm || null,
      weightKg: data.weightKg || null,
      note: data.note || null
    }
    try {
      if (editingRecord.value) await updateGrowthMeasurement(editingRecord.value.id, payload)
      else await createGrowthMeasurement(payload)
      records.value = await getGrowthMeasurements()
      measurementOpen.value = false
      notice.value = '测量记录已保存'
    } catch (e) {
      modalError.value = e.response?.data?.message || '记录保存失败，请检查输入'
    } finally {
      saving.value = false
    }
  }

  /**
   * 保存照片: 修改模式仅更新日期与说明;新增模式以 FormData 上传图片。
   * @param {Object} data 弹窗提交的 { takenAt, caption, image }
   */
  async function savePhoto(data) {
    modalError.value = ''
    saving.value = true
    try {
      if (editingPhoto.value) {
        await updateChildPhoto(editingPhoto.value.id, { takenAt: data.takenAt, caption: data.caption })
      } else {
        const form = new FormData()
        form.append('takenAt', data.takenAt)
        form.append('caption', data.caption)
        form.append('image', data.image)
        await addChildPhoto(form)
      }
      await setPhotos(await getChildPhotos())
      photoOpen.value = false
      notice.value = '照片已保存'
    } catch (e) {
      modalError.value = e.response?.data?.message || '照片保存失败，请使用 JPG 或 PNG 文件'
    } finally {
      saving.value = false
    }
  }

  // ===== 删除动作: 结果反映在页面级横幅 =====

  /**
   * 删除一条测量记录(带确认)。
   * @param {Object} item 待删除的测量记录
   */
  async function removeMeasurement(item) {
    if (!window.confirm(`删除 ${item.measuredAt} 的测量记录？`)) return
    pageError.value = ''
    notice.value = ''
    try {
      await deleteGrowthMeasurement(item.id)
      records.value = await getGrowthMeasurements()
      notice.value = '记录已删除'
    } catch (e) {
      pageError.value = e.response?.data?.message || '删除失败'
    }
  }

  /**
   * 删除一张成长照片(带确认)。
   * @param {Object} item 待删除的照片
   */
  async function removePhoto(item) {
    if (!window.confirm(`删除 ${item.takenAt} 的照片？`)) return
    pageError.value = ''
    notice.value = ''
    try {
      await deleteChildPhoto(item.id)
      await setPhotos(await getChildPhotos())
      notice.value = '照片已删除'
    } catch (e) {
      pageError.value = e.response?.data?.message || '照片删除失败'
    }
  }

  // 组件卸载时释放全部照片 blob URL
  onUnmounted(() => {
    for (const url of photoUrls.values()) URL.revokeObjectURL(url)
  })

  return {
    profile, records, photos, unlocked, loading, saving, pageError, notice,
    profileOpen, measurementOpen, photoOpen, editingRecord, editingPhoto, modalError,
    load, unlock,
    openProfile, closeProfile, saveProfile,
    openMeasurement, closeMeasurement, saveMeasurement, removeMeasurement,
    openPhoto, closePhoto, savePhoto, removePhoto
  }
}
