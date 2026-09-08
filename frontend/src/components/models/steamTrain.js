import * as THREE from 'three'

// 模型由本地几何体构成，坐标约定：车头朝 +X，Y 向上，Z 为车身宽度。
export function createSteamTrain(colors) {
  const train = new THREE.Group()
  const materials = Object.fromEntries(Object.entries(colors).map(([key, color]) => [key,
    new THREE.MeshStandardMaterial({ color, roughness: key === 'brass' ? .3 : .65, metalness: ['brass', 'steel', 'dark'].includes(key) ? .55 : .12 })
  ]))
  function mesh(geometry, color, position, parent = train) {
    const item = new THREE.Mesh(geometry, materials[color])
    item.position.set(...position)
    item.castShadow = true
    item.receiveShadow = true
    parent.add(item)
    return item
  }
  const box = (size, color, pos, parent) => mesh(new THREE.BoxGeometry(...size), color, pos, parent)
  function cylinder(radius, length, color, pos, axis = 'y', parent) {
    const item = mesh(new THREE.CylinderGeometry(radius, radius, length, 32), color, pos, parent)
    if (axis === 'x') item.rotation.z = Math.PI / 2
    if (axis === 'z') item.rotation.x = Math.PI / 2
    return item
  }

  // 主车架、圆柱锅炉与锅炉箍。
  box([5.1, .25, 1.65], 'dark', [.35, .83, 0])
  box([4.9, .13, 1.78], 'red', [.4, 1.01, 0])
  cylinder(.68, 2.85, 'dark', [1.05, 1.77, 0], 'x')
  for (const x of [-.15, .9, 2.3]) cylinder(.695, .07, 'brass', [x, 1.77, 0], 'x')
  cylinder(.58, .12, 'steel', [2.53, 1.77, 0], 'x')
  cylinder(.46, .15, 'dark', [2.6, 1.77, 0], 'x')
  cylinder(.1, .08, 'brass', [2.7, 1.77, 0], 'x')
  for (let i = 0; i < 12; i++) {
    const angle = i * Math.PI / 6
    mesh(new THREE.SphereGeometry(.032, 8, 6), 'brass', [2.61, 1.77 + Math.cos(angle) * .51, Math.sin(angle) * .51])
  }

  // 烟囱、汽包、安全阀和前照灯。
  cylinder(.23, .7, 'dark', [1.95, 2.63, 0])
  mesh(new THREE.CylinderGeometry(.36, .23, .3, 32), 'dark', [1.95, 3.08, 0])
  cylinder(.37, .08, 'brass', [1.95, 3.26, 0])
  cylinder(.28, .025, 'dark', [1.95, 3.31, 0])
  mesh(new THREE.SphereGeometry(.3, 24, 16), 'brass', [.65, 2.4, 0])
  cylinder(.11, .24, 'brass', [0, 2.49, 0])
  cylinder(.19, .2, 'brass', [2.52, 2.42, 0], 'x')
  cylinder(.145, .025, 'glass', [2.63, 2.42, 0], 'x')

  // 驾驶室以分离立柱和窗格建模，保留通透的侧窗。
  box([1.4, .8, 1.55], 'red', [-1.05, 1.46, 0])
  for (const x of [-1.69, -.41]) {
    for (const z of [-.7, .7]) box([.13, 1, .13], 'red', [x, 2.32, z])
    box([.12, .7, 1.3], 'glass', [x, 2.31, 0])
  }
  for (const z of [-.76, .76]) {
    box([1.4, .12, .08], 'brass', [-1.05, 1.92, z])
    box([.1, .8, .1], 'red', [-1.05, 2.35, z])
    box([.72, .12, .3], 'steel', [-1.12, .67, z * 1.22])
  }
  box([1.7, .2, 1.94], 'dark', [-1.05, 2.85, 0])
  box([1.45, .08, 1.75], 'brass', [-1.05, 2.98, 0])

  // 每个车轮具有轮缘、轮毂和八根独立辐条。
  function wheel(x, z, radius) {
    const group = new THREE.Group()
    group.position.set(x, radius + .15, z)
    train.add(group)
    const rim = mesh(new THREE.TorusGeometry(radius - .065, .065, 10, 32), 'dark', [0, 0, 0], group)
    rim.castShadow = true
    cylinder(.12, .2, 'brass', [0, 0, 0], 'z', group)
    for (let i = 0; i < 8; i++) {
      const spoke = box([radius * 1.7, .065, .075], 'red', [0, 0, 0], group)
      spoke.rotation.z = i * Math.PI / 4
    }
  }
  for (const z of [-.87, .87]) {
    for (const x of [-1.3, -.05, 1.2]) wheel(x, z, .58)
    wheel(2.35, z, .32)
    box([2.7, .105, .1], 'steel', [-.05, .65, z * 1.14])
    for (const x of [-1.3, -.05, 1.2]) cylinder(.09, .13, 'brass', [x, .65, z * 1.19], 'z')
    cylinder(.1, 2.5, 'brass', [1, 1.68, z * .74], 'x')
  }
  box([.23, .36, 1.9], 'red', [2.98, .72, 0])
  for (const z of [-.62, .62]) cylinder(.15, .24, 'dark', [3.14, .73, z], 'x')

  // 煤水车与车钩，煤块使用确定性排列，保证每次加载一致。
  box([.7, .12, .22], 'steel', [-2.14, .68, 0])
  box([1.65, .22, 1.6], 'dark', [-3.1, .8, 0])
  box([1.6, .83, 1.45], 'red', [-3.1, 1.28, 0])
  box([1.45, .08, 1.32], 'dark', [-3.1, 1.73, 0])
  for (let i = 0; i < 18; i++) {
    const coal = mesh(new THREE.DodecahedronGeometry(.18), 'dark', [-3.65 + (i % 6) * .22, 1.78 + (i % 3) * .045, -.42 + Math.floor(i / 6) * .4])
    coal.rotation.set(i, i * .7, 0)
  }
  for (const z of [-.82, .82]) for (const x of [-3.62, -2.58]) wheel(x, z, .36)

  // 固定轨道与展台。
  for (const z of [-.86, .86]) box([9.2, .13, .12], 'steel', [-.35, .08, z])
  for (let i = 0; i < 18; i++) box([.23, .12, 2.25], 'wood', [-4.65 + i * .5, -.015, 0])
  box([9.7, .15, 2.8], 'ground', [-.35, -.16, 0])
  return train
}
