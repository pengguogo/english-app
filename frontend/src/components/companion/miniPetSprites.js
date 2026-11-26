import poseUrl from '../../assets/companion/doudou-mini.webp'
import runUrl from '../../assets/companion/doudou-run.webp'

const poseIndex = { idle: 0, listening: 1, thinking: 1, hint: 1, safety: 1, speaking: 0, wave: 3, cheer: 3 }

async function loadFrames(url, columns, run = false) {
  const image = new Image()
  image.src = url
  await image.decode()
  const width = Math.floor(image.width / columns)
  const height = Math.floor(image.height / 2)
  const sample = document.createElement('canvas')
  sample.width = width + 14
  sample.height = height
  const context = sample.getContext('2d', { willReadFrequently: true })

  return Array.from({ length: columns * 2 }, (_, index) => {
    // 姿势图下排的跑步备选帧尾巴略微越过中线。
    const offset = !run && index >= 2 ? 14 : 0
    const x = index % columns * width + (index === 3 && !run ? offset : 0)
    const y = Math.floor(index / columns) * height
    const span = width + (index === 2 ? offset : -offset)
    context.clearRect(0, 0, sample.width, height)
    context.drawImage(image, x, y, span, height, 0, 0, span, height)
    const pixels = context.getImageData(0, 0, span, height).data
    let left = span, right = 0, top = height, bottom = 0
    for (let row = 0; row < height; row++) {
      for (let col = 0; col < span; col++) {
        if (pixels[(row * span + col) * 4 + 3] < 96) continue
        left = Math.min(left, col)
        right = Math.max(right, col)
        top = Math.min(top, row)
        bottom = Math.max(bottom, row)
      }
    }
    if (right <= left || bottom <= top) throw new Error('豆豆动作帧为空')
    return { image, x: x + left, y: y + top, width: right - left + 1, height: bottom - top + 1 }
  })
}

let poseFrames
let runFrames

export function loadMiniPet() {
  poseFrames ??= loadFrames(poseUrl, 2).catch(error => { poseFrames = null; throw error })
  return poseFrames
}

export function loadMiniPetRun() {
  runFrames ??= loadFrames(runUrl, 3, true).catch(error => { runFrames = null; throw error })
  return runFrames
}

export function paintMiniPet(context, poses, run, state, stride) {
  const running = state === 'running' && run.length
  const sprite = running ? run[stride % run.length] : poses[state === 'running' ? 2 : poseIndex[state] ?? 0]
  if (!sprite) return
  const sizing = running ? run : [sprite]
  const scale = Math.min(225 / Math.max(...sizing.map(frame => frame.width)),
    (running ? 176 : 216) / Math.max(...sizing.map(frame => frame.height)))
  const width = sprite.width * scale
  const height = sprite.height * scale
  context.clearRect(0, 0, 256, 256)
  context.drawImage(sprite.image, sprite.x, sprite.y, sprite.width, sprite.height,
    (256 - width) / 2, 233 - height, width, height)
}
