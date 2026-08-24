# 《奇想与发现》十篇故事 AI 配图记录

- 生成日期：2026-08-24
- 生成方式：Codex 内置 `image_gen`
- 用途分类：`illustration-story`
- 风格：温暖精致的 3D 儿童绘本，16:9 横版，无品牌、无水印
- 角色：约 7 岁中国男孩彭泽宇，短黑发、浅蓝连帽衫、深蓝长裤、白色运动鞋
- 风格参考：本项目 `growth-tasks/public-rules.jpg`，仅用于保持原创角色和画风一致
- 版权：全部为本课程原创生成，可用于本项目课程展示

## 文件与用途

| 本地文件 | 故事 | 最终提示词摘要 |
|---|---|---|
| `varied-stories/weather-letter.jpg` | 屋顶上的天气信 | 安全屋顶气象角，孩子观察云、风向袋与雨量器，雨后晴空。 |
| `varied-stories/tiny-forest.jpg` | 一厘米森林 | 木栈道低机位，放大镜下的苔藓、蕨芽、水珠与瓢虫。 |
| `varied-stories/power-outage-stars.jpg` | 停电后的星星地图 | 父子在阳台门内安全观星，以应急灯和红光手电查看星图。 |
| `varied-stories/four-sounds.jpg` | 四种声音的音乐会 | 四位儿童用豆罐、木块、纸筒与拍手合作创造节奏。 |
| `varied-stories/missing-puzzle.jpg` | 少一块也能出发 | 社区路线拼图留下河流缺口，三人用透明描图纸补出临时路线。 |
| `varied-stories/slow-rain-walk.jpg` | 雨伞下的慢慢路 | 黄色大伞下，两位孩子在校园雨后步道放慢脚步同行。 |
| `varied-stories/button-suitcase.jpg` | 旧纽扣的旅行箱 | 祖孙整理旧纽扣、车票、布料与家庭照片，父母在旁倾听。 |
| `varied-stories/shadow-mystery.jpg` | 影子借走了什么 | 安全台灯、双耳纸兔与单耳重叠影子组成清楚的光影实验。 |
| `varied-stories/late-seed.jpg` | 种子没有按时发芽 | 三个图形标记花盆长势不同，最晚的菜豆刚从裂土中冒芽。 |
| `varied-stories/future-letter.jpg` | 给未来的第十封信 | 学期末教室里，孩子把第十只信封放入已有九封信的时间盒。 |

## 生成与处理说明

1. 每幅图使用独立场景提示词生成，没有用同一构图批量替换背景。
2. 生成图以 `growth-tasks/public-rules.jpg` 作为角色与画风参考，不包含版权角色或品牌元素。
3. 原始输出为 PNG；使用 macOS `sips` 转为质量 85 的 JPG，并统一为 1200×675。
4. 每篇的阅读页和问答页复用一张概览图；图片表现故事核心环境，但不直接暴露选择题答案。
5. 停电、屋顶、雨天、光影实验和自然观察场景均加入相应安全边界。
