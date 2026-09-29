import { externalVehicleModels } from '../../models/externalVehicleModels'

/**
 * @description 新版模型馆只展示 3 个大类，每类绑定一个代表模型。
 */
export const modelCategories = [
  {
    id: 'train',
    title: '火车',
    subtitle: '代表模型：GP7 真实机车',
    model: 'gp7',
    icon: '轨',
    accent: 'var(--color-primary)',
    description: '从车头、驾驶室到转向架，先认识最有代表性的真实机车。',
    examples: ['蒸汽火车', '燃油火车', 'GP7 真实机车', 'Deltic', 'CC 206']
  },
  {
    id: 'road-air',
    title: '公路与航空',
    subtitle: '代表模型：F-35A',
    model: 'fighter',
    icon: '翼',
    accent: 'var(--color-orange)',
    description: '把视角切到更快的交通工具，观察速度感来自哪些结构。',
    examples: ['Porsche 911 Turbo', '警用摩托车', 'F-35A', '集装箱船']
  },
  {
    id: 'mechanics',
    title: '结构小百科',
    subtitle: '代表模型：动画蒸汽机',
    model: 'steamEngine',
    icon: '构',
    accent: 'var(--color-success)',
    description: '看得见内部结构，才更容易明白动力和机械是怎样工作的。',
    examples: ['动画蒸汽机', '涡扇发动机', '半剖潜艇', '铁路轮对', '道岔扳杆']
  }
]

export const modelHeadline = {
  gp7: {
    eyebrow: '火车观察站',
    title: externalVehicleModels.gp7.title,
    english: 'Diesel-electric locomotive',
    description: '先从真实机车开始，看看驾驶室、长机罩和转向架怎样组合成整辆列车。'
  },
  fighter: {
    eyebrow: '公路与航空观察站',
    title: externalVehicleModels.fighter.title,
    english: 'Jet aircraft',
    description: '把镜头转向喷气式飞机，认识机翼、进气道、尾翼和起落架的分工。'
  },
  steamEngine: {
    eyebrow: '结构小百科',
    title: externalVehicleModels.steamEngine.title,
    english: 'Animated steam engine',
    description: '播放动画后观察活塞、连杆和飞轮，感受动力怎样一路传递出去。'
  }
}
