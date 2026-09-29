/**
 * @description 新版首页入口配置。文案面向 6 岁儿童：短句、直白、说结果不说概念。
 */
export const featuredRoutes = [
  {
    title: '学科乐园',
    description: '今天的英语学习，从这里开始。',
    route: '/subject/1',
    kicker: '先学习',
    accent: 'var(--color-primary)',
    icon: 'A'
  },
  {
    title: '绘本小火车',
    description: '看图听故事，边玩边学单词。',
    route: '/picturebooks',
    kicker: '读故事',
    accent: 'var(--color-accent)',
    icon: '绘'
  },
  {
    title: '游戏专区',
    description: '认真学习，就能解锁好玩的小游戏。',
    route: '/games',
    kicker: '玩游戏',
    accent: 'var(--color-success)',
    icon: '玩'
  },
  {
    title: '交通工具馆',
    description: '转一转 3D 小火车和汽车！',
    route: '/models',
    kicker: '看模型',
    accent: 'var(--color-orange)',
    icon: '模'
  }
]

export const supportRoutes = [
  {
    title: '错题本',
    description: '再练一遍还没记住的题。',
    route: '/wrong-answers',
    accent: 'var(--color-warning)'
  },
  {
    title: '学习记录',
    description: '看看学了哪些课，攒了多少星星。',
    route: '/learned',
    accent: 'var(--color-primary-light)'
  }
]
