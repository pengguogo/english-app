/**
 * @description 新版模型馆只展示 3 个大类，每类绑定一个代表模型。
 * 主标题用中文直白命名，英文型号只作副标题，方便 6 岁孩子读页面。
 */
export const modelCategories = [
  {
    id: 'train',
    title: '火车',
    subtitle: '代表模型：GP7 真实机车',
    model: 'gp7',
    icon: '轨',
    accent: 'var(--color-primary)',
    description: '从车头、驾驶室到车轮，先认识最有代表性的大火车头。',
    examples: ['蒸汽火车', '燃油火车', 'GP7 真实机车', 'Deltic', 'CC 206']
  },
  {
    id: 'road-air',
    title: '汽车和飞机',
    subtitle: '代表模型：F-35A',
    model: 'fighter',
    icon: '翼',
    accent: 'var(--color-orange)',
    description: '再看跑得更快的交通工具，飞机和汽车长什么样。',
    examples: ['Porsche 911 Turbo', '警用摩托车', 'F-35A', '集装箱船']
  },
  {
    id: 'mechanics',
    title: '机器小百科',
    subtitle: '代表模型：动画蒸汽机',
    model: 'steamEngine',
    icon: '构',
    accent: 'var(--color-success)',
    description: '看得见里面的零件，才知道机器是怎样动起来的。',
    examples: ['动画蒸汽机', '涡扇发动机', '半剖潜艇', '铁路轮对', '道岔扳杆']
  }
]

export const modelHeadline = {
  gp7: {
    eyebrow: '火车观察站',
    title: '大火车头',
    english: 'EMD GP7 713',
    description: '看看驾驶室、长长的车身和下面的车轮，怎样拼成一整辆火车。'
  },
  fighter: {
    eyebrow: '飞机观察站',
    title: '喷气式飞机',
    english: 'F-35A Lightning II',
    description: '数一数机翼、尾巴上的小翅膀和轮子，看看它们各自管什么。'
  },
  steamEngine: {
    eyebrow: '机器小百科',
    title: '会动的蒸汽机',
    english: 'Animated Steam Engine',
    description: '点开动画，看活塞一进一出，把力气传给大轮子。'
  }
}
