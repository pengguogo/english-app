import { AmbientLight, DirectionalLight, OrthographicCamera, Scene, WebGLRenderer } from 'three'
import { buildMouse } from './mousePreviewModel'

export function createMousePreview(canvas) {
  const renderer = new WebGLRenderer({ canvas, alpha: true, antialias: true })
  const style = getComputedStyle(canvas)
  const color = name => style.getPropertyValue(name)
  const pet = buildMouse({ pink: color('--color-pink'), cream: color('--bg-card'),
    ink: color('--text-primary'), rose: color('--color-warning') })
  const scene = new Scene()
  const camera = new OrthographicCamera(-1.25, 1.25, 1.25, -1.25, .1, 20)
  camera.position.set(.04, .17, 5)
  camera.lookAt(0, -.02, 0)
  const light = new DirectionalLight(color('--bg-card').trim(), 2.4)
  light.position.set(-3, 5, 6)
  scene.add(pet.mouse, light, new AmbientLight(color('--bg-card').trim(), 1.4))

  let input = { state: 'idle', action: '', visible: true, reduced: false }
  let raf
  let started = 0

  function draw(now = started) {
    const { state, action, reduced, visible } = input
    if (!visible) return
    const elapsed = (now - started) / 1000
    const pulse = Math.sin(Math.PI * Math.min(elapsed, 1))
    const motion = reduced ? 0 : pulse
    const talking = state === 'speaking'
    const waving = state === 'wave'
    pet.mouse.position.y = state === 'cheer' ? motion * .25 : 0
    pet.mouse.scale.y = 1 + (action === 'stretch' ? motion * .13 : 0)
    pet.head.rotation.y = state === 'hint' ? -.24 : action === 'tilt' ? motion * .66 : -.13
    pet.head.rotation.z = state === 'thinking' ? -.13 : 0
    pet.head.rotation.x = state === 'listening' ? .12 : 0
    pet.paw.rotation.z = waving ? 2.2 + (reduced ? 0 : Math.sin(elapsed * 17) * motion * .38) : state === 'hint' ? .8 : 0
    pet.eyes.forEach(eye => { eye.scale.y = action === 'blink' ? 1 - motion * .87 : 1 })
    pet.ears[1].rotation.z = action === 'ear' ? motion * Math.sin(elapsed * 19) * .25 : 0
    pet.nose.position.y = -.16 + (action === 'sniff' ? motion * Math.sin(elapsed * 22) * .038 : 0)
    pet.mouth.scale.y = talking && !reduced ? .033 + Math.abs(Math.sin(elapsed * 11)) * .045 : .039
    pet.tail.rotation.z = action === 'tail' ? Math.sin(elapsed * 16) * motion * .13 : 0
    renderer.render(scene, camera)
    if (!reduced && (talking || ((waving || state === 'cheer' || action) && elapsed < 1))) {
      raf = requestAnimationFrame(draw)
    }
  }

  function resize() {
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
    renderer.setSize(canvas.clientWidth, canvas.clientHeight, false)
    cancelAnimationFrame(raf)
    draw(performance.now())
  }

  const observer = new ResizeObserver(resize)
  observer.observe(canvas)
  return {
    update(next) {
      input = next
      started = performance.now()
      cancelAnimationFrame(raf)
      draw()
    },
    dispose() {
      observer.disconnect()
      cancelAnimationFrame(raf)
      pet.dispose()
      renderer.dispose()
    }
  }
}
