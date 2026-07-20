/**
 * @file 主题视觉配置(集中管理)
 * @description 按 themeId 集中维护每个主题的展示信息:emoji 图标、标题、banner 描述、
 *              以及主题地图中各单元节点的场景配置(图标 + 主色调 + 标签)。
 *              新增主题时只需在此处追加一项配置,无需改动各页面组件,避免硬编码散落。
 *              说明:颜色一律引用 tokens.css 中的 CSS 变量,不硬编码色值。
 * @author englishapp
 * @since 2026-07-20
 */

/**
 * 单个场景节点配置。
 * @typedef {Object} SceneConfig
 * @property {string} icon  场景 emoji 图标(用于地图节点)
 * @property {string} color 场景主色调(引用 tokens.css 变量)
 * @property {string} label 场景文字标签
 */

/**
 * 单个主题配置。
 * @typedef {Object} ThemeVisualConfig
 * @property {string} emoji        主题 emoji(首页卡片 + banner)
 * @property {string} title        主题标题
 * @property {string} description  banner 副标题描述
 * @property {SceneConfig[]} scenes 各单元节点的场景配置(按顺序对应单元列表)
 */

// 主题视觉配置表:键为主题 ID(数字)
const themeConfig = {
  // 主题 1:水果乐园(3 单元:常见水果 / 瓜果与浆果 / 水果大集合)
  1: {
    emoji: '🍎',
    title: '水果乐园',
    description: '认识美味又好玩的水果',
    scenes: [
      { icon: '🍎', color: 'var(--color-warning)', label: '常见水果' },
      { icon: '🍓', color: 'var(--color-orange)', label: '瓜果与浆果' },
      { icon: '🍉', color: 'var(--color-success)', label: '水果大集合' }
    ]
  },
  // 主题 2:交通工具乐园
  2: {
    emoji: '🚗',
    title: '交通工具乐园',
    description: '探索陆地、天空和水上的交通工具',
    scenes: [
      { icon: '🚗', color: 'var(--color-success)', label: '陆地' },
      { icon: '✈️', color: 'var(--color-sky)', label: '天空' },
      { icon: '🚢', color: 'var(--color-primary)', label: '水上' }
    ]
  },
  // 主题 3:汪汪队立大功(3 单元:狗狗小队 / 救援行动 / 团队精神)
  3: {
    emoji: '🐕',
    title: '汪汪队立大功',
    description: '和汪汪队一起学习救援与合作',
    scenes: [
      { icon: '🐕', color: 'var(--color-primary)', label: '狗狗小队' },
      { icon: '🚑', color: 'var(--color-danger)', label: '救援行动' },
      { icon: '🤝', color: 'var(--color-accent)', label: '团队精神' }
    ]
  },
  // 主题 4:小砾工程队(2 单元:工程工具 / 工程家族)
  4: {
    emoji: '🏗️',
    title: '小砾工程队',
    description: '和工程狗狗们一起建造有趣的东西',
    scenes: [
      { icon: '🔧', color: 'var(--color-primary)', label: '工程工具' },
      { icon: '🐕', color: 'var(--color-accent)', label: '工程家族' }
    ]
  }
}

// 兜底配置:未知主题 ID 时使用,保证页面不会因缺配置而空白
const defaultConfig = {
  emoji: '📚',
  title: '学习乐园',
  description: '一起来学英语吧',
  scenes: [
    { icon: '📚', color: 'var(--color-primary)', label: '课程' },
    { icon: '📖', color: 'var(--color-accent)', label: '课程' },
    { icon: '✏️', color: 'var(--color-orange)', label: '课程' }
  ]
}

/**
 * 根据主题 ID 获取视觉配置。
 * themeId 可能为数字或字符串(来自路由参数),统一转为数字匹配。
 * 未命中时返回兜底配置,避免渲染异常。
 *
 * @param {number|string} themeId 主题 ID
 * @return {ThemeVisualConfig} 主题视觉配置
 */
export function getThemeConfig(themeId) {
  return themeConfig[Number(themeId)] || defaultConfig
}

export default themeConfig
