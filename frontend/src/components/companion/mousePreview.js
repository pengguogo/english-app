import { AmbientLight, DirectionalLight, MathUtils, OrthographicCamera, Scene, WebGLRenderer } from 'three'
import { buildMouse } from './mousePreviewModel'

export function createMousePreview(canvas) {
  const renderer = new WebGLRenderer({ canvas, alpha: true, antialias: true })
  const style = getComputedStyle(canvas)
  const color = name => style.getPropertyValue(name)
  const pet = buildMouse({
    pink: color('--pilot-fur'), cream: color('--pilot-cream'), ink: color('--pilot-ink'),
    rose: color('--pilot-shade'), shell: color('--pilot-shell'), rim: color('--pilot-rim'),
    light: color('--cabin-cyan')
  })
  const equipmentColors = {
    thinking: color('--cabin-violet'), hint: color('--cabin-warning'),
    safety: color('--cabin-warning'), cheer: color('--color-success')
  }
  const scene = new Scene()
  const camera = new OrthographicCamera(-1.25, 1.25, 1.25, -1.25, .1, 20)
  camera.position.set(.04, .17, 5)
  camera.lookAt(0, -.02, 0)
  const light = new DirectionalLight(color('--pilot-cream').trim(), 2.4)
  light.position.set(-3, 5, 6)
  scene.add(pet.mouse, light, new AmbientLight(color('--pilot-cream').trim(), 1.4))

  let input = { state: 'idle', action: '', visible: true, reduced: false }
  let raf
  let started = 0
  let previous = { yaw: 0, roll: 0, pitch: 0, paw: 0 }
  function readPose() {
    return { yaw: pet.head.rotation.y, roll: pet.head.rotation.z, pitch: pet.head.rotation.x, paw: pet.paw.rotation.z }
  }

  function draw(now = started) {
    const { state, action, reduced, visible } = input
    if (!visible) return
    const elapsed = (now - started) / 1000
    const motion = reduced ? 0 : Math.sin(Math.PI * MathUtils.clamp(elapsed, 0, 1))
    const settle = reduced ? 1 : MathUtils.smoothstep(elapsed, 0, .32)
    const talking = state === 'speaking'
    const waving = state === 'wave'
    const cheering = state === 'cheer'
    const hop = cheering && !reduced ? Math.sin(Math.PI * MathUtils.clamp((elapsed - .11) / .6, 0, 1)) : 0
    const bounce = cheering && !reduced ? Math.sin(elapsed * 20) * Math.max(0, 1 - elapsed * 1.2) : 0
    const yaw = state === 'hint' ? -.24 : -.13
    const roll = state === 'thinking' ? -.13 : 0
    const pitch = state === 'listening' ? .12 : 0
    const paw = waving ? 2.2 : state === 'hint' ? .8 : 0
    pet.mouse.position.y = hop * .23
    pet.mouse.scale.set(1 + bounce * .055, 1 - bounce * .065 + (action === 'stretch' ? motion * .13 : 0), 1)
    pet.head.rotation.y = MathUtils.lerp(previous.yaw, yaw, settle) + (action === 'tilt' ? motion * .61 : 0)
    pet.head.rotation.z = MathUtils.lerp(previous.roll, roll, settle) + hop * .1
    pet.head.rotation.x = MathUtils.lerp(previous.pitch, pitch, settle)
    pet.paw.rotation.z = MathUtils.lerp(previous.paw, paw, settle) + (waving ? Math.sin(elapsed * 23) * motion * .44 : 0)
    pet.eyes.forEach(eye => { eye.scale.y = action === 'blink' ? 1 - motion * .87 : 1 })
    pet.ears[1].rotation.z = action === 'ear' ? motion * Math.sin(elapsed * 19) * .25 : 0
    pet.nose.position.y = -.16 + (action === 'sniff' ? motion * Math.sin(elapsed * 22) * .038 : 0)
    pet.mouth.scale.y = talking && !reduced ? .033 + Math.abs(Math.sin(elapsed * 11)) * .045 : .039
    pet.tail.rotation.z = action === 'tail' ? Math.sin(elapsed * 16) * motion * .13 : 0
    renderer.render(scene, camera)
    if (!reduced && (talking || elapsed < .32 || ((waving || cheering || action) && elapsed < 1))) {
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
      const lightColor = equipmentColors[next.state] || color('--cabin-cyan')
      pet.equipmentLight.color.set(lightColor.trim())
      pet.equipmentLight.emissive.copy(pet.equipmentLight.color)
      previous = readPose()
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
