/**
 * 首页大图宫格配置：学科来自接口，其余入口固定。
 * 图片均为 static/images 下已有素材。
 */
const SUBJECT_IMAGES = {
  ENGLISH: '/images/english-animal/owl.jpg',
  CHINESE: '/images/chinese-pinyin/child-speaking-a.jpg',
  MATH: '/images/math/number-1.jpg',
  EXTRACURRICULAR: '/images/science/rainbow.jpg'
}
const FALLBACK_SUBJECT_IMAGE = '/images/english-toy/block.jpg'

const FIXED_TILES = [
  { key: 'character', label: '认字', image: '/images/hanzi-pilot/hanzi-shan.jpg', to: '/character-review' },
  { key: 'picturebooks', label: '绘本', image: '/images/picturebooks/train-station.jpg', to: '/picturebooks' },
  {
    key: 'fruit',
    label: '收集水果',
    image: '/images/fruit/apple.jpg',
    to: { path: '/theme/1', query: { subjectId: '1', subjectName: '英语', themeName: '水果乐园' } }
  },
  { key: 'games', label: '游戏', image: '/images/english-toy/puzzle.jpg', to: '/games' },
  { key: 'models', label: '3D 火车', image: '/images/train/steam-locomotive.jpg', to: '/models' }
]

/**
 * @param {Array<{id:number, code:string, name:string}>} subjects 学科列表
 * @returns {Array<{key:string, label:string, image:string, to:string|object}>}
 */
export function buildHomeTiles(subjects = []) {
  const subjectTiles = subjects.map((s) => ({
    key: `subject-${s.id}`,
    label: s.name,
    image: SUBJECT_IMAGES[s.code] || FALLBACK_SUBJECT_IMAGE,
    to: `/subject/${s.id}`
  }))
  return [...subjectTiles, ...FIXED_TILES]
}
