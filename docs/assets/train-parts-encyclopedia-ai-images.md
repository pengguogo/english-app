# 《火车零件大图鉴》素材记录

## AI 生成图片

- 生成日期：2026-08-25
- 生成方式：OpenAI 内置 ImageGen 工具（AI 生成）
- 用例分类：`scientific-educational`
- 本地目录：`backend/src/main/resources/static/images/train-parts/`
- 文件范围：
  - `part-001.jpg` 至 `part-100.jpg`：课程使用的零件独立特写
  - `*-plate.jpg`：8 张按时代划分的整车总览拆解图
- 文件格式：零件特写 724 × 543 JPG，质量 84；总览图 1200 × 900 JPG，质量 82
- 版权说明：全部为 AI 生成的项目素材，不冒充铁路机构、车辆制造商或图书出版社的
  官方素材；画面不含品牌标志、水印、人物和可识别文字。

## 100 张零件特写

为避免多个零件复用同一张大图，按课程编号每 4 个关联零件生成一张严格对齐的
2 × 2 四格母版，共 25 张。每格只有一个指定零件，主体居中且互不跨格；随后使用
本地 Sharp 图片库按精确象限分切为 100 张独立 JPG：

```text
母版 01 → part-001..004
母版 02 → part-005..008
...
母版 25 → part-097..100
```

母版共用提示：温暖米白色纯净书页背景；儿童百科全书精细手绘水彩与工程线稿；
严格等大的 2 × 2 网格；每格仅绘制一个指定零件且完整可见；禁止文字、数字、
箭头、品牌、人物、水印、整车和跨格元素。

## 总览图文件与提示词

所有图片共用以下视觉提示：儿童百科全书中的精细手绘水彩与清晰工程线稿结合，
温暖米白色书页背景，横向 4:3，三分之四侧视，部件适度分离并用细引导线表示
对应位置；机械结构可信、轮廓清晰；禁止文字、数字、品牌、人物和水印。

| 文件 | 主题提示 |
|---|---|
| `steam-boiler-plate.jpg` | 蒸汽机车锅炉、火箱、炉排、煤水车、阀门和仪表 |
| `steam-running-plate.jpg` | 蒸汽机车气缸、活塞、连杆、轮组、闸瓦和砂箱 |
| `diesel-engine-plate.jpg` | 柴油机、进排气、喷油、润滑和冷却系统 |
| `diesel-drive-plate.jpg` | 柴油电力传动、驾驶控制、转向架和空气制动 |
| `electric-roof-plate.jpg` | 电力机车受电弓、高压保护、变压与变流设备 |
| `electric-running-plate.jpg` | 电力机车走行、悬挂、齿轮传动和制动设备 |
| `high-speed-body-plate.jpg` | 高速动车组流线车头、车体骨架、驾驶室与客室 |
| `high-speed-systems-plate.jpg` | 高速动车组车顶、车底、转向架、牵引和控制设备 |

## 技术参考

- National Railway Museum，`Stephenson's Rocket, Rainhill and the rise of the locomotive`：
  https://www.railwaymuseum.org.uk/objects-and-stories/stephensons-rocket-rainhill-and-rise-locomotive
- Union Pacific，`Diesel-Electric Locomotives`：
  https://www.up.com/aboutup/special_trains/diesel-electric/
- Network Rail，`Electrification`：
  https://www.networkrail.co.uk/our-work/living-by-the-railway/electrification/
- Network Rail，`Efficient and dependable partner`（受电弓说明）：
  https://www.networkrail.co.uk/industry-and-commercial/efficient-and-dependable-partner/
- Central Japan Railway Company，`About the Shinkansen`：
  https://global.jr-central.co.jp/en/company/about_shinkansen/
- Central Japan Railway Company，`Annual Report 2025: Building Safe and Resilient Infrastructure`：
  https://global.jr-central.co.jp/en/company/ir/annualreport/_pdf/annualreport2025-11.pdf

技术资料仅用于核对动力传递、取电、制动和列车控制原理。插图为面向儿童的概念拆解图，
不用于真实车辆的检修、操作或安全培训。
