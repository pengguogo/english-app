/**
 * @file 学习进度 Pinia store
 * @description
 *  集中管理跨组件共享的学习进度状态:总星星数、当前课时进度。
 *  使用 setup 语法(defineStore + composition 函数),与组合式 API 风格一致。
 * @author english-app
 * @since 2026-07-20
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useProgressStore = defineStore('progress', () => {
  // 用户累计获得的总星星数,用于顶部进度条展示
  const totalStars = ref(0)
  // 当前正在学习的课时进度对象,结构与后端 progress 一致
  const currentLessonProgress = ref(null)

  /**
   * 设置总星星数。
   *
   * @param {number} count 星星总数
   */
  function setTotalStars(count) {
    totalStars.value = count
  }

  /**
   * 设置当前课时进度。
   *
   * @param {Object|null} progress 课时进度对象
   */
  function setCurrentProgress(progress) {
    currentLessonProgress.value = progress
  }

  return { totalStars, currentLessonProgress, setTotalStars, setCurrentProgress }
})
