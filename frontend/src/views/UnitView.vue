<!--
  UnitView.vue - 课时列表页版本包装页
  用途: 根据全局 UI 版本状态切换老版课时列表页(LegacyUnitPage)和新版课时列表页(ModernUnitPage)。
        原页面逻辑已原样迁移至 legacy/LegacyUnitPage.vue。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import UiVersionPageShell from '../components/ui-version/UiVersionPageShell.vue'
import LegacyUnitPage from '../components/ui-version/legacy/LegacyUnitPage.vue'
import ModernUnitPage from '../components/ui-version/modern/ModernUnitPage.vue'
</script>

<template>
  <UiVersionPageShell :legacy-page="LegacyUnitPage" :modern-page="ModernUnitPage" />
</template>
