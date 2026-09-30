<!--
  BatchUploadModal.vue - 成长照片批量上传弹窗
  用途: 一次选择多张照片(JPG/PNG,每张≤8MB,格式与大小前置校验),
        共用同一拍摄日期与分类;逐张上传进度由父组件回传显示,
        上传中禁止关闭,避免误操作中断批量任务。
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
/** 允许的照片 MIME 类型 */
const ALLOWED_TYPES = ['image/jpeg', 'image/png']

/**
 * 组件 props:
 * @param {Boolean} open - 是否显示弹窗
 * @param {Array} categories - 已存在的分类(供输入建议)
 * @param {Boolean} uploading - 父组件是否正在上传(锁定弹窗)
 * @param {Object} progress - 上传进度 { done, total, failed }
 * @param {String} error - 父组件接口返回的错误信息
 */
const props = defineProps({
  open: { type: Boolean, default: false },
  categories: { type: Array, default: () => [] },
  uploading: { type: Boolean, default: false },
  progress: { type: Object, default: null },
  error: { type: String, default: '' }
})

/**
 * 组件事件:
 * @event close - 关闭弹窗(上传中会被拦截)
 * @event upload - 提交上传,payload 为 { takenAt, category, files }
 */
const emit = defineEmits(['close', 'upload'])

const form = reactive({ takenAt: today(), category: '' })
// 待上传文件清单: reason 为空表示有效,非空为跳过原因
const entries = ref([])
const localError = ref('')
const fileInput = ref(null)

// 打开时重置表单与文件清单,避免残留上次的半成品输入
watch(() => props.open, (isOpen) => {
  if (!isOpen) return
  localError.value = ''
  form.takenAt = today()
  form.category = ''
  entries.value = []
})

/**
 * 校验单个文件,返回跳过原因(空串表示通过)。
 * @param {File} file 待校验文件
 * @returns {String} 不合格原因或空串
 */
function validate(file) {
  if (!ALLOWED_TYPES.includes(file.type)) return '仅支持 JPG/PNG'
  if (file.size > MAX_PHOTO_SIZE) return '超过 8 MB'
  return ''
}

/**
 * 选择文件: 重建清单并前置校验;同时清空原生控件残留值便于重新选择。
 * @param {Event} event 原生 change 事件
 */
function selectFiles(event) {
  localError.value = ''
  entries.value = [...event.target.files].map(file => ({
    file,
    name: file.name,
    sizeText: formatSize(file.size),
    reason: validate(file)
  }))
  if (fileInput.value) fileInput.value.value = ''
}

/** 有效文件(通过校验)清单。 */
const validEntries = computed(() => entries.value.filter(entry => !entry.reason))
/** 不合格文件数量,用于提示"将被跳过"。 */
const invalidCount = computed(() => entries.value.length - validEntries.value.length)

/**
 * 从清单移除一个文件。
 * @param {Number} index 待移除下标
 */
function removeAt(index) {
  entries.value.splice(index, 1)
}

/** 提交按钮文案: 展示即将上传的张数。 */
const uploadLabel = computed(() => {
  if (props.uploading) return '上传中…'
  const count = validEntries.value.length
  return count ? `上传 ${count} 张` : '上传'
})

/** 进度条填充百分比。 */
const fillPercent = computed(() => {
  if (!props.progress || !props.progress.total) return '0%'
  return `${Math.round((props.progress.done / props.progress.total) * 100)}%`
})

/** 关闭请求: 上传中拦截,避免误触中断批量任务。 */
function requestClose() {
  if (!props.uploading) emit('close')
}

/**
 * 提交表单: 至少一张有效照片才放行,交给父组件执行逐张上传。
 */
function submit() {
  localError.value = ''
  if (!validEntries.value.length) {
    localError.value = '请先选择有效的照片文件'
    return
  }
  emit('upload', {
    takenAt: form.takenAt,
    category: form.category,
    files: validEntries.value.map(entry => entry.file)
  })
}

/**
 * 文件大小人性化展示。
 * @param {Number} bytes 字节数
 * @returns {String} 如 "2.3 MB"
 */
function formatSize(bytes) {
  if (bytes < 1024 * 1024) return `${Math.max(1, Math.round(bytes / 1024))} KB`
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
}
</script>

<template>
  <AppModal :open="open" title="批量上传照片" @close="requestClose">
    <form class="cg-form" @submit.prevent="submit">
      <p v-if="localError || error" role="alert" class="cg-error">{{ localError || error }}</p>
      <label class="cg-field">拍摄日期（应用于全部）
        <input v-model="form.takenAt" class="cg-input" type="date" :max="today()" :disabled="uploading" required />
      </label>
      <label class="cg-field">分类（应用于全部）
        <input v-model.trim="form.category" class="cg-input" maxlength="50" list="bu-category-options"
          placeholder="例如：游玩" :disabled="uploading" />
        <datalist id="bu-category-options">
          <option v-for="item in categories" :key="item" :value="item" />
        </datalist>
      </label>
      <label class="cg-field cg-wide">选择照片（可多选，JPG/PNG，每张最多 8 MB）
        <input ref="fileInput" class="cg-file" type="file" multiple accept="image/jpeg,image/png"
          :disabled="uploading" @change="selectFiles" />
      </label>
      <ul v-if="entries.length" class="bu-list">
        <li v-for="(entry, i) in entries" :key="entry.name + i" :class="{ 'bu-invalid': entry.reason }">
          <span class="bu-name" :title="entry.name">{{ entry.name }}</span>
          <span class="bu-size">{{ entry.sizeText }}</span>
          <span v-if="entry.reason" class="bu-reason">{{ entry.reason }}</span>
          <button v-else-if="!uploading" type="button" class="bu-remove" @click="removeAt(i)">移除</button>
        </li>
      </ul>
      <p v-if="invalidCount" class="cg-hint">{{ invalidCount }} 个文件不符合要求，上传时将被跳过</p>
      <div v-if="uploading && progress" class="bu-progress" role="progressbar"
        :aria-valuenow="progress.done" :aria-valuemin="0" :aria-valuemax="progress.total">
        <div class="bu-fill" :style="{ width: fillPercent }"></div>
      </div>
      <p v-if="uploading && progress" class="cg-hint" aria-live="polite">
        正在上传 {{ progress.done }} / {{ progress.total }}<template v-if="progress.failed">，失败 {{ progress.failed }} 张</template>
      </p>
      <div class="cg-actions">
        <AppButton :disabled="uploading || !validEntries.length" @click="submit">{{ uploadLabel }}</AppButton>
        <AppButton variant="ghost" :disabled="uploading" @click="requestClose">取消</AppButton>
      </div>
    </form>
  </AppModal>
</template>

<style scoped>
/* 文件清单: 紧凑可滚动列表,类名 bu- 前缀(cg- 为共享表单样式) */
.bu-list {
  grid-column: 1 / -1;
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
  max-height: 200px;
  overflow-y: auto;
}
.bu-list li {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  background: var(--bg-muted);
  font-size: var(--text-sm);
}
.bu-name {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--text-primary);
}
.bu-size { color: var(--text-tertiary); flex-shrink: 0; }
.bu-reason { color: var(--color-warning); flex-shrink: 0; }
.bu-remove {
  flex-shrink: 0;
  border: 0;
  background: transparent;
  color: var(--text-secondary);
  font: inherit;
  cursor: pointer;
  min-height: var(--touch-target);
  padding: 0 var(--space-1);
}
.bu-invalid { background: var(--color-warning-bg); }

/* 上传进度条: 主色渐变填充,功能性反馈(非装饰) */
.bu-progress {
  grid-column: 1 / -1;
  height: 10px;
  border-radius: var(--radius-pill);
  background: var(--bg-muted);
  overflow: hidden;
}
.bu-fill {
  height: 100%;
  border-radius: var(--radius-pill);
  background: var(--gradient-primary);
}
</style>
