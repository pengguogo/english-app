<!--
  PhotoEditModal.vue - 成长照片编辑弹窗
  用途: 新增照片(选文件+日期+说明)或修改已有照片的日期与说明(后端不支持换图);
        保存动作交给父组件完成。文件校验(JPG/PNG、≤8MB)在弹窗内前置完成。
  作者: english-app
  创建日期: 2026-09-30
-->
<script setup>
import { computed, reactive, ref, watch } from 'vue'
import AppModal from '../AppModal.vue'
import AppButton from '../AppButton.vue'
import { today } from '../../utils/date'
import './childgrowth-form.css'

/** 照片文件大小上限(字节): 8 MB,与后端校验保持一致 */
const MAX_PHOTO_SIZE = 8 * 1024 * 1024

/**
 * 组件 props:
 * @param {Boolean} open - 是否显示弹窗
 * @param {Boolean} saving - 父组件是否正在提交(用于禁用按钮)
 * @param {String} error - 父组件接口返回的错误信息
 * @param {Object} photo - 待修改的照片记录;null 表示新增模式
 */
const props = defineProps({
  open: { type: Boolean, default: false },
  saving: { type: Boolean, default: false },
  error: { type: String, default: '' },
  photo: { type: Object, default: null }
})

/**
 * 组件事件:
 * @event close - 关闭弹窗
 * @event save - 提交表单,payload 为 { takenAt, caption, image }
 */
const emit = defineEmits(['close', 'save'])

// 修改模式只能调整日期和说明,标题与提示文案随模式切换
const title = computed(() => (props.photo ? '修改照片信息' : '添加成长照片'))

const form = reactive({ takenAt: today(), caption: '', image: null })
const localError = ref('')
const fileInput = ref(null)

// 打开时按模式重建表单;同时清空原生 file 控件的残留值
watch(() => props.open, (isOpen) => {
  if (!isOpen) return
  localError.value = ''
  if (props.photo) {
    Object.assign(form, { takenAt: props.photo.takenAt, caption: props.photo.caption || '', image: null })
  } else {
    Object.assign(form, { takenAt: today(), caption: '', image: null })
  }
  if (fileInput.value) fileInput.value.value = ''
})

/**
 * 选择照片文件: 仅暂存,提交时才做大小与格式校验。
 * @param {Event} event - 原生 change 事件
 */
function selectImage(event) {
  form.image = event.target.files?.[0] || null
}

/**
 * 提交表单: 新增模式必须选图且不超 8MB,通过后抛给父组件。
 */
function submit() {
  localError.value = ''
  if (!props.photo && !form.image) {
    localError.value = '请先选择照片'
    return
  }
  if (form.image && form.image.size > MAX_PHOTO_SIZE) {
    localError.value = '照片不能超过 8 MB'
    return
  }
  emit('save', { takenAt: form.takenAt, caption: form.caption, image: form.image })
}
</script>

<template>
  <AppModal :open="open" :title="title" @close="emit('close')">
    <form class="cg-form" @submit.prevent="submit">
      <p v-if="localError || error" role="alert" class="cg-error">{{ localError || error }}</p>
      <label class="cg-field">拍摄日期
        <input v-model="form.takenAt" class="cg-input" type="date" :max="today()" required />
      </label>
      <label v-if="!photo" class="cg-field cg-wide">照片（JPG/PNG，最多 8 MB）
        <input ref="fileInput" class="cg-file" type="file" accept="image/jpeg,image/png" @change="selectImage" />
      </label>
      <p v-else class="cg-hint">如需更换图片，请删除后重新添加照片。</p>
      <label class="cg-field cg-wide">一句话记录
        <input v-model.trim="form.caption" class="cg-input" maxlength="200" placeholder="例如：第一次骑上自行车" />
      </label>
      <div class="cg-actions">
        <AppButton :disabled="saving" @click="submit">{{ photo ? '保存修改' : '保存照片' }}</AppButton>
        <AppButton variant="ghost" :disabled="saving" @click="emit('close')">取消</AppButton>
      </div>
    </form>
  </AppModal>
</template>
