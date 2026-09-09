import * as THREE from 'three'
import { dieselParts } from './dieselTrain.js'

export function createInspection(renderer, camera, controls, train, render, onSelect) {
  const raycaster = new THREE.Raycaster()
  let down
  function focus(id) {
    const part = dieselParts.find(item => item.id === id)
    if (!part) return
    if (id === 'engine') train.userData.hood.visible = false
    controls.target.set(...part.target)
    camera.position.copy(controls.target).add(new THREE.Vector3(...part.offset))
    controls.update()
    onSelect(id, !train.userData.hood.visible)
    render()
  }
  function pointerDown(event) {
    down = event.isPrimary ? { x: event.clientX, y: event.clientY, time: performance.now() } : null
  }
  function pointerUp(event) {
    if (!down || Math.hypot(event.clientX - down.x, event.clientY - down.y) > 5 || performance.now() - down.time > 500) return
    down = null
    const rect = renderer.domElement.getBoundingClientRect()
    raycaster.setFromCamera(new THREE.Vector2((event.clientX - rect.left) / rect.width * 2 - 1, -(event.clientY - rect.top) / rect.height * 2 + 1), camera)
    const hit = raycaster.intersectObject(train).find(({ object }) => {
      for (let node = object; node; node = node.parent) if (!node.visible) return false
      return true
    })
    if (hit) focus(hit.object.parent.name)
  }
  renderer.domElement.addEventListener('pointerdown', pointerDown)
  renderer.domElement.addEventListener('pointerup', pointerUp)
  return {
    focus,
    setHood(open) { train.userData.hood.visible = !open; render() },
    dispose() {
      renderer.domElement.removeEventListener('pointerdown', pointerDown)
      renderer.domElement.removeEventListener('pointerup', pointerUp)
    }
  }
}
