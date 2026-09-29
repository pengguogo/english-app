<!--
  SubjectView.vue - 学科页版本包装页
  用途: 根据全局 UI 版本状态切换老版学科页和新版学科页。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import UiVersionPageShell from '../components/ui-version/UiVersionPageShell.vue'
import LegacySubjectPage from '../components/ui-version/legacy/LegacySubjectPage.vue'
import ModernSubjectPage from '../components/ui-version/modern/ModernSubjectPage.vue'
</script>

<template>
  <UiVersionPageShell :legacy-page="LegacySubjectPage" :modern-page="ModernSubjectPage" />
</template>
