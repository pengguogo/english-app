<!--
  UiVersionPageShell.vue - 页面版本外壳
  用途: 为页面统一挂载 UI 版本切换入口，并按当前状态切换老版/新版组件。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import { computed } from 'vue'
import { useUiVersion } from '../../composables/useUiVersion'
import UiVersionSwitch from './UiVersionSwitch.vue'

const props = defineProps({
  legacyPage: {
    type: [Object, Function],
    required: true
  },
  modernPage: {
    type: [Object, Function],
    required: true
  }
})

const { isModern } = useUiVersion()
const currentPage = computed(() => (isModern.value ? props.modernPage : props.legacyPage))
</script>

<template>
  <div class="ui-version-page">
    <UiVersionSwitch class="page-switch" />
    <component :is="currentPage" />
  </div>
</template>

<style scoped>
.ui-version-page {
  min-height: 100vh;
}

.page-switch {
  position: fixed;
  top: calc(env(safe-area-inset-top) + var(--space-3));
  right: var(--space-4);
  z-index: 40;
}

@media (max-width: 480px) {
  .page-switch {
    top: calc(env(safe-area-inset-top) + var(--space-2));
    right: var(--space-3);
  }
}
</style>
