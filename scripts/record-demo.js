/**
 * README 顶部演示 GIF 录制脚本
 *
 * 用 Playwright 驱动系统 Chrome 自动走完学习闭环并录屏，
 * 再用 ffmpeg 转为 640×400 / 12fps GIF。
 *
 * 演示路径（真实跑通，约 24 秒）：
 *   首页 → 选英语（展示选科） → 返回 → 选课外 → 火车主题
 *   → 火车家族问答 QUIZ 课程 → 5 题答对 → 完成庆祝页
 *
 * 选用 QUIZ 课程是因为它纯点击作答、无需麦克风，可在 headless 下
 * 真实走通到庆祝页；发音评分功能在 README 文字与截图表格中另行展示。
 *
 * ## 依赖
 *   - Node.js 18+
 *   - 系统 Chrome（macOS 路径：/Applications/Google Chrome.app/Contents/MacOS/Google Chrome）
 *   - ffmpeg（用于 webm → gif 转换）
 *   - npm i playwright（仅核心库，无需下浏览器，复用系统 Chrome）
 *
 * ## 运行
 *   cd <本文件所在目录的上级>
 *   node scripts/record-demo.js
 *   ffmpeg -i <生成的.webm> -vf "fps=12,scale=640:-1:flags=lanczos,split[s0][s1];[s0]palettegen=max_colors=128[p];[s1][p]paletteuse=dither=bayer:bayer_scale=5" -y docs/assets/demo.gif
 *
 * ## 注意
 *   录制依赖线上 Demo 可达；若改用本地应用，将 BASE 改为
 *   http://localhost:8080/app/ 并先启动后端。
 */
const { chromium } = require('playwright')
const path = require('path')
const fs = require('fs')

const sleep = (ms) => new Promise((r) => setTimeout(r, ms))

// 在线 Demo 地址，可改为本地 http://localhost:8080/app/
const BASE = 'http://39.96.59.120:8080/app/'

// lesson 40「火车家族问答」5 题正确答案
const ANSWERS = ['旅客', '速度很快', '可以睡觉的床位', '货物', '方便装卸']

// 系统 Chrome 路径（macOS）
const CHROME_PATH =
  '/Applications/Google Chrome.app/Contents/MacOS/Google Chrome'

;(async () => {
  const browser = await chromium.launch({
    headless: true,
    executablePath: CHROME_PATH
  })
  const ctx = await browser.newContext({
    viewport: { width: 1280, height: 800 },
    recordVideo: { dir: __dirname, size: { width: 1280, height: 800 } }
  })
  const page = await ctx.newPage()

  // 1. 首页
  await page.goto(BASE, { waitUntil: 'networkidle' })
  await sleep(2000)

  // 2. 选英语（展示选学科交互）
  await page.click('button.subject-card:has-text("英语")')
  await sleep(1400)

  // 3. 返回首页 → 选课外
  await page.click('button.back-btn')
  await sleep(1000)
  await page.click('button.subject-card:has-text("课外")')
  await sleep(1400)

  // 4. 找火车主题进入
  const themeCards = await page.$$('button.theme-card')
  for (const card of themeCards) {
    if (/火车/.test(await card.innerText())) {
      await card.click()
      break
    }
  }
  await sleep(1500)

  // 5. 找 QUIZ 课程进入
  const lessonCards = await page.$$('button.lesson-card')
  for (const card of lessonCards) {
    if (/问答/.test(await card.innerText())) {
      await card.click()
      break
    }
  }
  await sleep(1800)

  // 6. 逐题作答
  for (let i = 0; i < ANSWERS.length; i++) {
    const opt = await page.waitForSelector(
      `button.option:has-text("${ANSWERS[i]}")`,
      { timeout: 8000 }
    )
    await opt.click()
    await sleep(900)
    if (i < ANSWERS.length - 1) {
      const n = await page.waitForSelector(
        'button.app-btn.variant-primary:has-text("下一题")',
        { timeout: 5000 }
      )
      await n.click()
      await sleep(700)
    } else {
      const f = await page.waitForSelector(
        'button.app-btn.variant-primary:not(.disabled)',
        { timeout: 5000 }
      )
      await f.click()
      await sleep(1200)
    }
  }

  // 7. 庆祝页停留
  await sleep(3500)
  await page.screenshot({ path: path.join(__dirname, 'demo-final.png') })

  await ctx.close()
  await browser.close()
  const webm = fs
    .readdirSync(__dirname)
    .filter((f) => f.endsWith('.webm'))
  console.log('录制完成，视频文件:', webm)
  console.log('转 GIF 命令:')
  console.log(
    `ffmpeg -i ${webm[0]} -vf "fps=12,scale=640:-1:flags=lanczos,split[s0][s1];[s0]palettegen=max_colors=128[p];[s1][p]paletteuse=dither=bayer:bayer_scale=5" -y docs/assets/demo.gif`
  )
})().catch((e) => {
  console.error('FATAL', e)
  process.exit(1)
})
