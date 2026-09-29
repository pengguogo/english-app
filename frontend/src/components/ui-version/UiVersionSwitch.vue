<!--
  UiVersionSwitch.vue - UI 版本切换组件
  用途: 在页面右上角提供老版/新版切换入口，并同步全局 UI 状态。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import { useUiVersion } from '../../composables/useUiVersion'

const { version, setVersion, legacyVersion, modernVersion } = useUiVersion()
</script>

<template>
  <div class="ui-switch" aria-label="切换界面版本">
    <button
      type="button"
      class="switch-option"
      :class="{ active: version === legacyVersion }"
      :aria-pressed="version === legacyVersion"
      @click="setVersion(legacyVersion)"
    >
      老版
    </button>
    <button
      type="button"
      class="switch-option"
      :class="{ active: version === modernVersion }"
      :aria-pressed="version === modernVersion"
      @click="setVersion(modernVersion)"
    >
      新版
    </button>
  </div>
</template>

<style scoped>
.ui-switch {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  padding: var(--space-1);
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-pill);
  box-shadow: var(--shadow-card);
  backdrop-filter: blur(12px);
}

.switch-option {
  min-width: 56px;
  min-height: 36px;
  padding: 0 var(--space-3);
  color: var(--text-secondary);
  border-radius: var(--radius-pill);
  font-size: var(--text-sm);
  font-weight: var(--font-bold);
  transition:
    background var(--duration-fast) var(--ease-smooth),
    color var(--duration-fast) var(--ease-smooth),
    transform var(--duration-fast) var(--ease-bounce);
}

.switch-option.active {
  color: var(--text-on-primary);
  background: var(--gradient-primary);
}

.switch-option:active {
  transform: scale(0.96);
}
</style>
