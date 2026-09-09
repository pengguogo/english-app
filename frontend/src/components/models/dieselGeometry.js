import * as THREE from 'three'
import { mergeGeometries } from 'three/addons/utils/BufferGeometryUtils.js'

// 按零件与材质合并静态几何体，保留拾取边界，减少细节带来的绘制调用。
export function createDieselBuilder(colors) {
  const root = new THREE.Group()
  const materials = Object.fromEntries(Object.entries(colors).map(([name, color]) => [name,
    new THREE.MeshStandardMaterial({ color, metalness: name === 'steel' ? .8 : .35, roughness: name === 'steel' ? .32 : .58 })
  ]))
  const batches = new Map()
  function part(name) {
    const group = new THREE.Group()
    group.name = name
    root.add(group)
    batches.set(group, new Map())
    return group
  }
  function add(group, geometry, material, position, rotation = [0, 0, 0]) {
    const transform = new THREE.Matrix4().compose(new THREE.Vector3(...position), new THREE.Quaternion().setFromEuler(new THREE.Euler(...rotation)), new THREE.Vector3(1, 1, 1))
    geometry.applyMatrix4(transform)
    const batch = batches.get(group)
    if (!batch.has(material)) batch.set(material, [])
    batch.get(material).push(geometry)
  }
  const box = (g, size, mat, pos, rot) => add(g, new THREE.BoxGeometry(...size), mat, pos, rot)
  const cylinder = (g, r, length, mat, pos, axis = 'y', segments = 24) => add(g, new THREE.CylinderGeometry(r, r, length, segments), mat, pos, axis === 'z' ? [Math.PI / 2, 0, 0] : axis === 'x' ? [0, 0, Math.PI / 2] : undefined)
  const torus = (g, r, tube, mat, pos, rot = [0, 0, 0]) => add(g, new THREE.TorusGeometry(r, tube, 6, 32), mat, pos, rot)
  function pipe(g, points, radius, mat) {
    const curve = new THREE.CatmullRomCurve3(points.map(p => new THREE.Vector3(...p)))
    add(g, new THREE.TubeGeometry(curve, Math.max(24, points.length * 2), radius, 6, false), mat, [0, 0, 0])
  }
  function finish() {
    for (const [group, batch] of batches) for (const [key, geometries] of batch) {
      const mesh = new THREE.Mesh(mergeGeometries(geometries), materials[key])
      mesh.castShadow = true
      mesh.receiveShadow = true
      group.add(mesh)
      geometries.forEach(geometry => geometry.dispose())
    }
    // 未使用的材质不会进入场景的统一清理流程。
    const used = new Set([...batches.values()].flatMap(batch => [...batch.keys()]))
    Object.entries(materials).forEach(([key, material]) => { if (!used.has(key)) material.dispose() })
    return root
  }
  return { part, box, cylinder, torus, pipe, finish }
}
