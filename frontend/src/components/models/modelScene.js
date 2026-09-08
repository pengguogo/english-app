import * as THREE from 'three'
import { OrbitControls } from 'three/addons/controls/OrbitControls.js'
import { createSteamTrain } from './steamTrain'

export function createModelScene(host) {
  const styles = getComputedStyle(host)
  const color = name => styles.getPropertyValue(`--model-${name}`).trim()
  const scene = new THREE.Scene()
  scene.background = new THREE.Color(color('stage'))
  const camera = new THREE.PerspectiveCamera(36, 1, .1, 100)
  const renderer = new THREE.WebGLRenderer({ antialias: true })
  renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 2))
  renderer.shadowMap.enabled = true
  renderer.shadowMap.type = THREE.PCFSoftShadowMap
  renderer.domElement.setAttribute('aria-label', '蒸汽火车三维模型，可拖动旋转，使用下方按钮调整视角')
  renderer.domElement.setAttribute('role', 'img')
  host.appendChild(renderer.domElement)
  const controls = new OrbitControls(camera, renderer.domElement)
  controls.enablePan = false
  controls.minDistance = 7
  controls.maxDistance = 23
  controls.minPolarAngle = .15
  controls.maxPolarAngle = Math.PI / 2.05
  const render = () => renderer.render(scene, camera)
  controls.addEventListener('change', render)
  scene.add(new THREE.HemisphereLight(styles.getPropertyValue('--bg-card').trim(), color('ground'), 3))
  const light = new THREE.DirectionalLight(styles.getPropertyValue('--bg-card').trim(), 4)
  light.position.set(4, 9, 6)
  light.castShadow = true
  light.shadow.mapSize.set(1024, 1024)
  Object.assign(light.shadow.camera, { left: -7, right: 7, top: 6, bottom: -6 })
  light.shadow.bias = -.001
  scene.add(light)
  scene.add(createSteamTrain(Object.fromEntries(['red', 'dark', 'brass', 'steel', 'glass', 'wood', 'ground'].map(key => [key, color(key)]))))

  function view(name = 'default') {
    controls.target.set(-.4, 1.3, 0)
    const distance = host.clientWidth < 520 ? 18 : 14
    const directions = { default: [1, .55, 1.35], side: [0, .15, 1], front: [1, .18, 0], top: [0, 1, .01] }
    camera.position.copy(new THREE.Vector3(...directions[name]).normalize().multiplyScalar(distance).add(controls.target))
    controls.update()
    render()
  }
  function resize() {
    const { clientWidth: width, clientHeight: height } = host
    if (!width || !height) return
    camera.aspect = width / height
    camera.updateProjectionMatrix()
    renderer.setSize(width, height)
    render()
  }
  const observer = new ResizeObserver(resize)
  observer.observe(host)
  resize()
  view()
  return {
    view,
    zoom(factor) {
      const offset = camera.position.clone().sub(controls.target)
      offset.setLength(THREE.MathUtils.clamp(offset.length() * factor, controls.minDistance, controls.maxDistance))
      camera.position.copy(controls.target).add(offset)
      controls.update()
      render()
    },
    dispose() {
      observer.disconnect()
      controls.dispose()
      const materials = new Set()
      scene.traverse(object => {
        object.geometry?.dispose()
        if (object.material) materials.add(object.material)
      })
      materials.forEach(material => material.dispose())
      light.shadow.dispose()
      renderer.dispose()
      renderer.domElement.remove()
    }
  }
}
