# 《铁路设施大图鉴》素材记录

## AI 生成图片

- 生成日期：2026-08-26
- 生成方式：OpenAI 内置 ImageGen 工具（AI 生成）
- 用例分类：`scientific-educational`
- 本地目录：`backend/src/main/resources/static/images/railway-facilities/`
- 文件范围：
  - `facility-001.jpg` 至 `facility-100.jpg`：课程使用的设施独立插图
  - `*-overview.jpg`：8 张分类问答使用的综合场景图
- 文件格式：设施插图 724 × 543 JPG，质量 84；问答综合图 1200 × 900 JPG，
  质量 82。
- 版权说明：全部为 AI 生成的项目素材，不冒充铁路机构、车辆制造商或图书出版社的
  官方素材；画面不含品牌标志、水印、人物和可识别文字。

## 100 张设施插图

按课程编号每 4 个关联设施生成一张严格对齐的 2 × 2 四格母版，共 25 张；随后按
精确象限分切成 100 张独立 JPG。每格只绘制一个指定设施，主体居中且互不跨格：

```text
母版 01 → facility-001..004
母版 02 → facility-005..008
...
母版 25 → facility-097..100
```

母版共用提示：温暖米白色纯净书页背景；儿童百科全书精细手绘水彩与清晰工程线稿；
严格等大的 2 × 2 网格；每格仅绘制一个指定设施且完整可见；禁止文字、数字、箭头、
品牌、人物、水印、整列火车和跨格元素。每张母版按迁移中的连续四个条目依次指定左上、
右上、左下、右下主题。

## 问答综合图

问答综合图从对应分类母版中选取文图关系清楚、不会直接显示答案文字的完整四格图，
转换为以下 9 个 JPG key：

| 文件 | 内容范围 |
|---|---|
| `track-overview.jpg` | 钢轨、轨枕、扣件与道砟 |
| `route-overview.jpg` | 尖轨、辙叉、转辙机与渡线 |
| `station-overview.jpg` | 旅客站台与无障碍设施 |
| `freight-overview.jpg` | 货场、货运站台、仓库与起重机 |
| `signal-overview.jpg` | 信号、联锁与区间检测设施 |
| `detection-overview.jpg` | 线路检测与铁路气象设施 |
| `power-overview.jpg` | 接触网与牵引供电设施 |
| `crossing-overview.jpg` | 平交道口及其警示设施 |
| `maintenance-overview.jpg` | 养护、检修与补给设施 |

## 技术参考

- Federal Railroad Administration，`Highway-Rail Grade Crossings Overview`：
  https://railroads.fra.dot.gov/program-areas/highway-rail-grade-crossing/highway-rail-grade-crossings-overview
- Federal Railroad Administration，`Highway-Rail Grade Crossing Safety`：
  https://railroads.fra.dot.gov/railroad-safety/divisions/crossing-safety-and-trespass-prevention/railroad-crossing-safety
- Federal Railroad Administration / Federal Highway Administration，
  `Highway-Rail Crossing Handbook, Third Edition`：
  https://railroads.fra.dot.gov/sites/fra.dot.gov/files/2020-01/GXHandbook2019FRAFHWA.pdf
- Network Rail，`Track`：
  https://www.networkrail.co.uk/running-the-railway/looking-after-the-railway/track/
- Network Rail，`Signalling`：
  https://www.networkrail.co.uk/running-the-railway/looking-after-the-railway/signalling/
- Network Rail，`Electrification`：
  https://www.networkrail.co.uk/our-work/living-by-the-railway/electrification/
- Network Rail，`Level crossings`：
  https://www.networkrail.co.uk/communities/safety-in-the-community/level-crossings/

技术资料只用于核对设施名称、基本功能与安全关系。插图和文字是面向儿童的概念介绍，
不用于真实铁路的驾驶、调度、检修、供电或应急操作培训。
