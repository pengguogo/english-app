/**
 * @description 新版首页入口配置。
 */
export const featuredRoutes = [
  {
    title: '学科乐园',
    description: '先去英语主题站，完成今天的第一小步。',
    route: '/subject/1',
    kicker: '主线学习',
    accent: 'var(--color-primary)',
    icon: 'A'
  },
  {
    title: '绘本小火车',
    description: '一边看图，一边听故事，把单词装进旅程。',
    route: '/picturebooks',
    kicker: '故事陪伴',
    accent: 'var(--color-accent)',
    icon: '绘'
  },
  {
    title: '游戏专区',
    description: '先认真学习，再把小游戏奖励收入囊中。',
    route: '/games',
    kicker: '奖励解锁',
    accent: 'var(--color-success)',
    icon: '玩'
  },
  {
    title: '交通工具馆',
    description: '走进 3D 模型，看看火车、公路和航空世界。',
    route: '/models',
    kicker: '观察探索',
    accent: 'var(--color-orange)',
    icon: '模'
  }
]

export const supportRoutes = [
  {
    title: '错题本',
    description: '把今天还没记牢的地方，再轻轻复习一遍。',
    route: '/wrong-answers',
    accent: 'var(--color-warning)'
  },
  {
    title: '学习记录',
    description: '看看最近学过哪些内容，给自己一点鼓励。',
    route: '/learned',
    accent: 'var(--color-primary-light)'
  }
]
