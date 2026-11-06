# 学习伙伴小老鼠「豆豆」：素材来源与许可记录

记录人：ea-mascot　日期：2026-11-06　状态：已定稿（用户从 3 个候选里选了原候选 A）

## 结论

`frontend/src/assets/companion/mouse.svg` 是**原创重绘**的 SVG。参考素材只用来定造型比例，没有复制任何路径数据。
OpenMoji（CC BY-SA 4.0）、Twemoji（CC BY 4.0）和商业 IP 都**没有用**。
豆豆是粉色、没有围巾，和现有 Mimi（灰老鼠 + 黄围巾）明显不同。

## 参考来源核查（2026-11-06 打开仓库当前 LICENSE 核实）

| 来源 | 用到的文件 | 版本 / 提交 | 当前许可 | 核实链接 |
|---|---|---|---|---|
| Microsoft Fluent Emoji | `assets/Mouse face/Flat/mouse_face_flat.svg` | `1ffb34c752ecf5d402f04cfb4b392c77f57c54bc`（2026-08-24） | MIT（Copyright (c) Microsoft Corporation） | https://github.com/microsoft/fluentui-emoji/blob/main/LICENSE |

## 参考了什么、改了什么
- 参考：两只大圆耳加圆脸的比例。
- 改动：所有路径都是重画的。新加了身体、肚皮、卷尾、腮红，5 套表情和手势（idle / listening / thinking 托腮 / cheer / safety），
  以及状态特效（声波、思考点、星星、盾牌）。配色改成粉色系。
- 义务：没有复制或再分发 Fluent 的文件，所以不需要随附 MIT 声明。这里保留记录以便追溯；以后如果直接使用它的原始路径，需要随附 MIT 声明。

## 色值与实现
- SVG 内部只用 `var(--cm-*)` 变量，没有硬编码色值。变量在 `frontend/src/components/companion/CompanionMouse.vue` 里映射到 `tokens.css` 的现有变量
  （`--color-pink`、`--color-warning`、`--bg-page` 等）。没有新增 token。
- 动画只用 CSS keyframes/transform（眨眼、耳朵抖动、点头、思考点、星星闪烁），全部包在 `prefers-reduced-motion: no-preference` 里。
