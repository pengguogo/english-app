# 低龄车辆双语故事素材记录

## AI 生成图片

- 生成日期：2026-08-25
- 生成方式：Codex 内置 ImageGen（`illustration-story`）
- 适用年龄：3～6 岁
- 用途：《红色小火车出发》《忙碌的小挖机》《红色消防车》的逐页课程配图与单元封面
- 版权说明：全部为 AI 生成的项目原创素材，不冒充车辆品牌、影视角色或图书出版社的官方素材

## 文件范围

- `backend/src/main/resources/static/images/vehicle-stories/little-red-train-01.jpg` 至 `little-red-train-04.jpg`
- `backend/src/main/resources/static/images/vehicle-stories/busy-digger-01.jpg` 至 `busy-digger-04.jpg`
- `backend/src/main/resources/static/images/vehicle-stories/red-fire-truck-01.jpg` 至 `red-fire-truck-04.jpg`
- `frontend/src/assets/units/unit-148.webp` 至 `unit-150.webp`

## 生成与后处理

每篇故事先生成严格的 2 × 2 四格分镜，再按画格裁切为四张独立 JPG；单元封面由对应故事首图裁切生成 WebP。画面统一采用温暖明亮、圆润柔和的低龄绘本风格，车辆跨分镜保持颜色和造型一致。

### 提示词摘要

1. 红色小火车：乡间车站出发，依次穿过安全隧道、驶过溪流石桥、抵达鲜花车站。
2. 忙碌的小挖机：黄色挖掘机挖土并装入蓝色卡车，最后共同完成小树种植。
3. 红色消防车：在消防站等待，沿空旷道路出发，用水扑灭公园石圈里的小火并安全归队。

所有提示均要求：无文字、无数字、无品牌、无水印、无知名角色；画面明亮安全，不呈现危险驾驶、受伤人物或惊吓场面。
