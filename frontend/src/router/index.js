/**
 * @file Vue Router 路由配置
 * @description 定义前端应用的五级学习流路由：学科→主题→单元→课时。
 *              所有页面均采用路由懒加载，按需打包，减小首屏体积。
 * @author english-app
 * @since 2026-07-20
 */
import { createRouter, createWebHistory } from 'vue-router'
import { normalizeRouterBase } from '../utils/routerBase'

// 路由表：home / subject / theme / unit / lesson 五级学习流 + 错题集/我学过的快捷入口
const routes = [
  { path: '/character-review', name: 'character-review', component: () => import('../views/CharacterReviewView.vue') },
  { path: '/games/picture-puzzle', name: 'picture-puzzle', component: () => import('../views/PicturePuzzleView.vue') },
  { path: '/games/shape-puzzle', name: 'shape-puzzle', component: () => import('../views/ShapePuzzleView.vue') },
  { path: '/games/memory-match', name: 'memory-match', component: () => import('../views/MemoryMatchView.vue') },
  { path: '/games/pattern-play', name: 'pattern-play', component: () => import('../views/PatternPlayView.vue') },
  { path: '/games/tic-tac-toe', name: 'tic-tac-toe', component: () => import('../views/TicTacToeView.vue') },
  { path: '/games', name: 'games', component: () => import('../views/GamesView.vue') },
  { path: '/games/gomoku', name: 'gomoku', component: () => import('../views/GomokuView.vue') },
  { path: '/games/flight-chess', name: 'flight-chess', component: () => import('../views/FlightChessView.vue') },
  { path: '/games/adventure-chess', name: 'adventure-chess', component: () => import('../views/AdventureChessView.vue') },
  { path: '/games/go', name: 'go', component: () => import('../views/GoView.vue') },
  { path: '/models', name: 'models', component: () => import('../views/ModelsView.vue') },
  { path: '/picturebooks', name: 'picturebooks', component: () => import('../views/PicturebooksView.vue') },
  { path: '/picturebooks/:unitId', name: 'picturebook', component: () => import('../views/PicturebookView.vue') },
  { path: '/', name: 'home', component: () => import('../views/HomeView.vue') },
  { path: '/child-profile', name: 'child-profile', component: () => import('../views/ChildProfileView.vue') },
  // 成长相册:独立照片墙页面,月份分组+多选下载+分类筛选+批量上传
  { path: '/child-photos', name: 'child-photos', component: () => import('../views/ChildPhotosView.vue') },
  { path: '/subject/:subjectId', name: 'subject', component: () => import('../views/SubjectView.vue') },
  { path: '/theme/:themeId', name: 'theme', component: () => import('../views/ThemeView.vue') },
  { path: '/unit/:unitId', name: 'unit', component: () => import('../views/UnitView.vue') },
  { path: '/lesson/:lessonId', name: 'lesson', component: () => import('../views/LessonView.vue') },
  // 错题集:展示答错的 QUIZ/CALCULATE 题目,支持标记掌握与重做
  { path: '/wrong-answers', name: 'wrong-answers', component: () => import('../views/WrongAnswersView.vue') },
  // 我学过的:展示已学课时记录,支持复习跳转
  { path: '/learned', name: 'learned', component: () => import('../views/LearnedView.vue') }
]

// 创建 history 模式路由
const router = createRouter({
  history: createWebHistory(normalizeRouterBase(import.meta.env.BASE_URL)),
  routes,
  scrollBehavior(to, from, savedPosition) {
    return savedPosition || { top: 0 }
  }
})

export default router
