<!--
  ModelsView.vue - 模型页版本包装页
  用途: 根据全局 UI 版本状态切换老版模型页和新版模型页。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import UiVersionPageShell from '../components/ui-version/UiVersionPageShell.vue'
import LegacyModelsPage from '../components/ui-version/legacy/LegacyModelsPage.vue'
import ModernModelsPage from '../components/ui-version/modern/ModernModelsPage.vue'
</script>

<template>
  <UiVersionPageShell :legacy-page="LegacyModelsPage" :modern-page="ModernModelsPage" />
</template>
