<!--
  ThemeView.vue - 主题学习页版本包装页
  用途: 根据全局 UI 版本状态切换老版主题页(LegacyThemePage)和新版主题页(ModernThemePage)。
        原页面逻辑已原样迁移至 legacy/LegacyThemePage.vue。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import UiVersionPageShell from '../components/ui-version/UiVersionPageShell.vue'
import LegacyThemePage from '../components/ui-version/legacy/LegacyThemePage.vue'
import ModernThemePage from '../components/ui-version/modern/ModernThemePage.vue'
</script>

<template>
  <UiVersionPageShell :legacy-page="LegacyThemePage" :modern-page="ModernThemePage" />
</template>
