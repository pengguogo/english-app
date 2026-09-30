<!--
  PhotoLightbox.vue - 成长照片全屏灯箱
  用途: 点击照片后全屏查看原图,支持键盘(←/→/ESC)与悬浮按钮切换;
        原图按需加载(父组件注入加载函数并缓存),底部显示日期/分类/说明。
  作者: english-app
  创建日期: 2026-09-30
-->
<script setup>
import { computed, onBeforeUnmount, ref, watch } from 'vue'

/**
 * 组件 props:
 * @param {Boolean} open - 是否显示灯箱
 * @param {Array} photos - 当前筛选下的照片列表(含 takenAt/caption/category)
 * @param {Number} index - 当前查看的照片下标
 * @param {Function} loadImage - (id) => Promise<blob URL> 原图按需加载函数,由父组件注入并缓存
 */
const props = defineProps({
  open: { type: Boolean, default: false },
  photos: { type: Array, default: () => [] },
  index: { type: Number, default: 0 },
  loadImage: { type: Function, required: true }
})

/**
 * 组件事件:
 * @event close - 关闭灯箱(ESC 键/关闭按钮/点击空白区域)
 * @event update:index - 切换照片,负载为新下标
 */
const emit = defineEmits(['close', 'update:index'])

const current = computed(() => props.photos[props.index] || null)
const originSrc = ref('')
const originLoading = ref(false)
const originError = ref(false)

/**
 * 加载当前照片原图;切换到某张时才请求,减少无效流量。
 */
async function load() {
  if (!props.open || !current.value) return
  originError.value = false
  originLoading.value = true
  try {
    originSrc.value = await props.loadImage(current.value.id)
  } catch {
    originError.value = true
  } finally {
    originLoading.value = false
  }
}

// 打开或切换照片时重新加载原图
watch(() => [props.open, props.index], load, { immediate: true })

/**
 * 切换到相邻照片,越界时忽略。
 * @param {Number} delta 偏移量(-1 上一张 / 1 下一张)
 */
function step(delta) {
  const next = props.index + delta
  if (next >= 0 && next < props.photos.length) emit('update:index', next)
}

// 键盘导航: 仅在灯箱打开期间监听,避免干扰页面其他按键
function onKeydown(event) {
  if (event.key === 'Escape') emit('close')
  else if (event.key === 'ArrowLeft') step(-1)
  else if (event.key === 'ArrowRight') step(1)
}

// 打开时锁定页面滚动;卸载前兜底恢复
watch(() => props.open, (isOpen) => {
  window.removeEventListener('keydown', onKeydown)
  if (isOpen) {
    window.addEventListener('keydown', onKeydown)
    document.body.style.overflow = 'hidden'
  } else {
    document.body.style.overflow = ''
  }
}, { immediate: true })
onBeforeUnmount(() => {
  window.removeEventListener('keydown', onKeydown)
  document.body.style.overflow = ''
})
</script>

<template>
  <Teleport to="body">
    <Transition name="lb">
      <div v-if="open" class="lb-mask" role="dialog" aria-modal="true" aria-label="查看照片">
        <button type="button" class="lb-close" aria-label="关闭查看" @click="emit('close')">✕</button>
        <button v-if="photos.length > 1" type="button" class="lb-nav" aria-label="上一张" @click="step(-1)">‹</button>
        <div class="lb-stage" @click.self="emit('close')">
          <Transition name="lb-img" mode="out-in">
            <img v-if="originSrc && !originError" :key="current?.id" class="lb-photo"
              :src="originSrc" :alt="current?.caption || '成长照片'" />
            <p v-else-if="originLoading" key="loading" class="lb-tip">原图加载中…</p>
            <p v-else key="error" class="lb-tip">原图加载失败，请关闭后重试</p>
          </Transition>
        </div>
        <button v-if="photos.length > 1" type="button" class="lb-nav lb-next" aria-label="下一张" @click="step(1)">›</button>
        <footer v-if="current" class="lb-info">
          <span class="lb-count">{{ index + 1 }} / {{ photos.length }}</span>
          <div class="lb-meta">
            <time class="lb-date">{{ current.takenAt }}</time>
            <span v-if="current.category" class="lb-tag">{{ current.category }}</span>
          </div>
          <p v-if="current.caption" class="lb-caption">{{ current.caption }}</p>
        </footer>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
/* 遮罩: 深底全屏,层级高于通用弹窗,确保灯箱始终最前 */
.lb-mask {
  position: fixed;
  inset: 0;
  z-index: 1100;
  display: flex;
  flex-direction: column;
  background: var(--lb-veil);
}

/* 关闭按钮: 右上角圆形半透明 */
.lb-close {
  position: absolute;
  top: var(--space-4);
  right: var(--space-4);
  z-index: 1;
  width: var(--touch-target);
  height: var(--touch-target);
  border: 0;
  border-radius: var(--radius-pill);
  background: var(--lb-btn-bg);
  color: var(--text-on-primary);
  font-size: var(--text-base);
  cursor: pointer;
}

/* 左右切换按钮: 垂直居中悬浮,圆形触控目标 */
.lb-nav {
  position: absolute;
  top: 50%;
  left: var(--space-4);
  transform: translateY(-50%);
  width: var(--touch-target);
  height: var(--touch-target);
  border: 0;
  border-radius: var(--radius-pill);
  background: var(--lb-btn-bg);
  color: var(--text-on-primary);
  font-size: var(--text-xl);
  line-height: 1;
  cursor: pointer;
}
.lb-next { left: auto; right: var(--space-4); }

/* 照片舞台: 占满中部,左右预留按钮空间;点击空白关闭 */
.lb-stage {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 0;
  padding: var(--space-8) calc(var(--space-8) + var(--touch-target));
}
.lb-photo {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  border-radius: var(--radius-sm);
}
.lb-tip {
  margin: 0;
  color: var(--text-on-primary-muted);
  font-size: var(--text-base);
}

/* 底部信息栏: 白字 + 文字阴影保证深底可读 */
.lb-info {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: var(--space-1) var(--space-3);
  padding: var(--space-3) var(--space-5) var(--space-5);
  color: var(--text-on-primary);
  text-shadow: var(--shadow-text);
}
.lb-count { font-size: var(--text-sm); }
.lb-meta { display: flex; align-items: center; gap: var(--space-2); }
.lb-date { font-size: var(--text-sm); font-weight: var(--font-medium); }
.lb-tag {
  padding: 2px var(--space-2);
  border-radius: var(--radius-pill);
  background: var(--lb-btn-bg);
  font-size: var(--text-xs);
}
.lb-caption { flex-basis: 100%; margin: 0; font-size: var(--text-sm); }

/* 手机端: 收紧舞台内边距,给照片更大空间 */
@media (max-width: 480px) {
  .lb-stage { padding: var(--space-4) calc(var(--space-2) + var(--touch-target)); }
}

/* 进出场与切图动效: 尊重"减弱动态效果"设置 */
@media (prefers-reduced-motion: no-preference) {
  .lb-enter-active, .lb-leave-active { transition: opacity var(--duration-normal) var(--ease-smooth); }
  .lb-enter-from, .lb-leave-to { opacity: 0; }
  .lb-img-enter-active, .lb-img-leave-active { transition: opacity var(--duration-fast) var(--ease-smooth); }
  .lb-img-enter-from, .lb-img-leave-to { opacity: 0; }
}
</style>
