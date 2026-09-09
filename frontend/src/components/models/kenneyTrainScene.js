import * as THREE from 'three'
import { OrbitControls } from 'three/addons/controls/OrbitControls.js'
import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js'
import modelUrl from '../../assets/models/kenney-train/train-diesel-a.glb?url'
import textureUrl from '../../assets/models/kenney-train/colormap.png?url'

function releaseModel(root) {
  const materials = new Set()
  const textures = new Set()
  root.traverse(object => {
    object.geometry?.dispose()
    if (object.material) {
      for (const material of [object.material].flat()) materials.add(material)
    }
  })
  materials.forEach(material => {
    Object.values(material).forEach(value => { if (value?.isTexture) textures.add(value) })
    material.dispose()
  })
  textures.forEach(texture => texture.dispose())
}

export function createKenneyTrainScene(host, onStatus) {
  const styles = getComputedStyle(host)
  const scene = new THREE.Scene()
  scene.background = new THREE.Color(styles.getPropertyValue('--model-stage').trim())
  const camera = new THREE.PerspectiveCamera(36, 1, .01, 100)
  const renderer = new THREE.WebGLRenderer({ antialias: true })
  renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 2))
  renderer.toneMapping = THREE.ACESFilmicToneMapping
  renderer.domElement.setAttribute('role', 'img')
  renderer.domElement.setAttribute('aria-label', 'Kenney 柴油火车三维模型，可拖动旋转和缩放')
  host.appendChild(renderer.domElement)
  const controls = new OrbitControls(camera, renderer.domElement)
  controls.minDistance = 3
  controls.maxDistance = 18
  controls.enablePan = false
  scene.add(new THREE.HemisphereLight(styles.getPropertyValue('--bg-card').trim(), styles.getPropertyValue('--model-ground').trim(), 3))
  const light = new THREE.DirectionalLight(styles.getPropertyValue('--bg-card').trim(), 3)
  light.position.set(3, 5, 4)
  scene.add(light)
  const render = () => renderer.render(scene, camera)
  controls.addEventListener('change', render)
  let disposed = false
  function view(name = 'default') {
    const directions = { default: [1, .55, 1.3], side: [1, .12, 0], front: [0, .12, 1], top: [0, 1, .01] }
    camera.position.copy(new THREE.Vector3(...directions[name]).normalize().multiplyScalar(camera.aspect < 1 ? 11 : 8))
    controls.target.set(0, 0, 0)
    controls.update()
    render()
  }
  function resize() {
    if (!host.clientWidth || !host.clientHeight) return
    camera.aspect = host.clientWidth / host.clientHeight
    camera.updateProjectionMatrix()
    renderer.setSize(host.clientWidth, host.clientHeight)
    render()
  }
  const observer = new ResizeObserver(resize)
  observer.observe(host)
  resize()
  view()
  // 原始 GLB 外置了调色板纹理，显式映射到 Vite 打包后的本地资源。
  const manager = new THREE.LoadingManager()
  manager.setURLModifier(url => url.endsWith('Textures/colormap.png') ? textureUrl : url)
  new GLTFLoader(manager).load(modelUrl, gltf => {
    if (disposed) { releaseModel(gltf.scene); return }
    const bounds = new THREE.Box3().setFromObject(gltf.scene)
    const scale = 4 / Math.max(...bounds.getSize(new THREE.Vector3()).toArray())
    gltf.scene.scale.setScalar(scale)
    gltf.scene.position.copy(bounds.getCenter(new THREE.Vector3())).multiplyScalar(-scale)
    scene.add(gltf.scene)
    render()
    onStatus('ready')
  }, undefined, cause => {
    if (!disposed) { console.error('加载 Kenney 火车失败', cause); onStatus('error') }
  })
  return {
    view,
    zoom(factor) {
      camera.position.setLength(THREE.MathUtils.clamp(camera.position.length() * factor, controls.minDistance, controls.maxDistance))
      controls.update()
      render()
    },
    dispose() {
      disposed = true
      observer.disconnect()
      controls.dispose()
      releaseModel(scene)
      renderer.dispose()
      renderer.domElement.remove()
    }
  }
}
