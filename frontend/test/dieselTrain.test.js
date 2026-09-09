import test from 'node:test'
import assert from 'node:assert/strict'
import * as THREE from 'three'
import { createDieselTrain, dieselParts } from '../src/components/models/dieselTrain.js'
import { createInspection } from '../src/components/models/modelInspection.js'

const colors = Object.fromEntries(['red', 'dark', 'brass', 'steel', 'glass', 'wood', 'ground'].map(key => [key, 'gray']))
function dispose(train) {
  const materials = new Set()
  train.traverse(object => { object.geometry?.dispose(); if (object.material) materials.add(object.material) })
  materials.forEach(material => material.dispose())
}

test('燃油机车具备立体零件且几何规模适合移动端', () => {
  const train = createDieselTrain(colors)
  let meshes = 0
  let triangles = 0
  for (const part of dieselParts) assert.ok(train.getObjectByName(part.id)?.children.length)
  train.traverse(object => {
    if (!object.isMesh) return
    meshes++
    triangles += object.geometry.index.count / 3
    assert.ok(Array.from(object.geometry.attributes.position.array).every(Number.isFinite))
  })
  assert.ok(meshes < 65, `绘制网格数：${meshes}`)
  assert.ok(triangles < 100000, `三角形数：${triangles}`)
  const bounds = new THREE.Box3().setFromObject(train)
  assert.ok(bounds.max.x > 4.8 && bounds.min.x < -4.8)
  dispose(train)
})

test('柴油机近景打开机罩并定位镜头，销毁时移除拾取事件', () => {
  const train = createDieselTrain(colors)
  const handlers = new Map()
  const element = {
    addEventListener: (name, handler) => handlers.set(name, handler),
    removeEventListener: name => handlers.delete(name)
  }
  const camera = new THREE.PerspectiveCamera()
  const controls = { target: new THREE.Vector3(), update() {} }
  let selection
  const inspection = createInspection({ domElement: element }, camera, controls, train, () => {}, (...args) => { selection = args })
  inspection.focus('engine')
  assert.equal(train.userData.hood.visible, false)
  assert.deepEqual(selection, ['engine', true])
  assert.deepEqual(controls.target.toArray(), dieselParts.find(part => part.id === 'engine').target)
  assert.ok(camera.position.distanceTo(controls.target) < 4)
  inspection.setHood(false)
  assert.equal(train.userData.hood.visible, true)
  inspection.focus('bogie')
  assert.deepEqual(selection, ['bogie', false])
  inspection.dispose()
  assert.equal(handlers.size, 0)
  dispose(train)
})
