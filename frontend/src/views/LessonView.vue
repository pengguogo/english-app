<!--
  LessonView.vue - 课时学习页版本包装页
  用途: 根据全局 UI 版本状态切换老版课时学习页(LegacyLessonPage)和新版课时学习页(ModernLessonPage)。
        原页面逻辑（音频/TTS、逐项学习、完成庆祝、进度保存、错题记录、连续播放）
        已原样迁移至 legacy/LegacyLessonPage.vue。
  作者: TRAE Agent
  创建日期: 2026-09-29
-->
<script setup>
import UiVersionPageShell from '../components/ui-version/UiVersionPageShell.vue'
import LegacyLessonPage from '../components/ui-version/legacy/LegacyLessonPage.vue'
import ModernLessonPage from '../components/ui-version/modern/ModernLessonPage.vue'
</script>

<template>
  <UiVersionPageShell :legacy-page="LegacyLessonPage" :modern-page="ModernLessonPage" />
</template>
