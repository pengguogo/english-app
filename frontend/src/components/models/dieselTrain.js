import { createDieselBuilder } from './dieselGeometry.js'
import { buildDieselBody, buildCab, buildCouplers } from './dieselBody.js'
import { buildRunningGear } from './dieselRunningGear.js'
import { buildEngine, buildCooling } from './dieselEngine.js'

export const dieselParts = [
  { id: 'bogie', label: '轮组与悬挂', target: [2.55, .72, 1], offset: [.8, .6, 2.4], description: '观察钢制轮缘、六角轴箱螺栓、制动块和螺旋弹簧。轮轴之间还能看到牵引电机。' },
  { id: 'cab', label: '驾驶室', target: [2.8, 2.45, .4], offset: [2.4, 1.1, 2.4], description: '找找挡风玻璃上的雨刷、后视镜、双前灯、门把手和车顶喇叭。' },
  { id: 'cooling', label: '散热风扇', target: [-2.8, 2.65, 0], offset: [.7, 2.4, 1.6], description: '透过同心护网观察风扇叶片与中心轴，再转到侧面查看散热格栅。' },
  { id: 'engine', label: '柴油机', target: [-.25, 2, 0], offset: [1, 2, 2.7], description: '机罩已打开：观察两列气缸盖、螺栓、燃油管路和末端发电机。内部为教学示意结构。' },
  { id: 'fuel', label: '燃油箱', target: [0, .8, .8], offset: [.4, .35, 2.3], description: '车架下方是燃油箱。查看固定箍带、加油口、盖把手与连接管路。' },
  { id: 'coupler', label: '车钩与风管', target: [4.64, .96, 0], offset: [1.8, .6, 1.6], description: '近看车钩钩头、锁销和两侧制动风管，连接处的金属接头也可以放大观察。' }
]

export function createDieselTrain(colors) {
  const b = createDieselBuilder(colors)
  const hood = buildDieselBody(b)
  buildRunningGear(b)
  buildEngine(b)
  buildCooling(b)
  buildCab(b)
  buildCouplers(b)
  const track = b.part('track')
  for (const z of [-.86, .86]) b.box(track, [10.8, .14, .12], 'steel', [0, .08, z])
  for (let i = 0; i < 23; i++) b.box(track, [.19, .12, 2.3], 'wood', [-5.2 + i * .47, -.025, 0])
  b.box(track, [11.2, .16, 2.8], 'ground', [0, -.17, 0])
  const root = b.finish()
  root.userData.hood = hood
  return root
}
