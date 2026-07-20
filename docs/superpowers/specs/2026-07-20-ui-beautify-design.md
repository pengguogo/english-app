# UI 美化设计文档

> 日期: 2026-07-20
> 范围: 视觉统一 + 轻量游戏化（B 方案）
> 参考原始设计图: `ChatGPT Image 2026年7月17日 10_46_05.png`

## 一、设计目标

将当前极简风格的 4 个页面重构为符合原始设计图的儿童英语学习 APP 视觉风格，引入吉祥物情感化设计和轻量游戏化元素，同时建立可复用的设计令牌系统并将视觉规范沉淀为项目规则。

## 二、设计令牌系统

新建 `frontend/src/styles/tokens.css`，全局引入。所有组件和页面禁止硬编码色值，必须通过 `var(--xxx)` 引用。

### 2.1 配色令牌

| 令牌 | 值 | 用途 |
|------|-----|------|
| `--color-primary` | `#6B7CFF` | 主按钮、导航、链接 |
| `--color-accent` | `#FFD93D` | 星星、奖励、CTA |
| `--color-success` | `#4CD964` | 完成、正确反馈 |
| `--color-warning` | `#FF6B6B` | 错误、未解锁 |
| `--color-pink` | `#FFB6C1` | 装饰、柔和元素 |
| `--color-sky` | `#87CEEB` | 天空场景 |
| `--color-orange` | `#FF9800` | 评分、录音 |
| `--text-primary` | `#333333` | 主文字 |
| `--text-secondary` | `#666666` | 次级文字 |
| `--text-tertiary` | `#999999` | 辅助文字 |
| `--bg-page` | `#FFF8F0` | 页面暖底 |
| `--bg-card` | `#FFFFFF` | 卡片白底 |

### 2.2 圆角令牌

| 令牌 | 值 | 用途 |
|------|-----|------|
| `--radius-sm` | `8px` | 小元素 |
| `--radius-md` | `16px` | 卡片 |
| `--radius-lg` | `24px` | 大卡片、按钮 |
| `--radius-pill` | `999px` | 胶囊形 |

### 2.3 阴影令牌

| 令牌 | 值 |
|------|-----|
| `--shadow-card` | `0 4px 16px rgba(107,124,255,0.08)` |
| `--shadow-hover` | `0 8px 24px rgba(107,124,255,0.15)` |

### 2.4 间距令牌（8 倍数体系）

`--space-1: 4px` / `--space-2: 8px` / `--space-3: 12px` / `--space-4: 16px` / `--space-5: 20px` / `--space-6: 24px` / `--space-8: 32px`

### 2.5 字号令牌

`--text-xs: 12px` / `--text-sm: 14px` / `--text-base: 16px` / `--text-lg: 24px` / `--text-xl: 32px`

### 2.6 动效令牌

| 令牌 | 值 |
|------|-----|
| `--ease-bounce` | `cubic-bezier(0.34, 1.56, 0.64, 1)` |
| `--duration-fast` | `150ms` |
| `--duration-normal` | `250ms` |

### 2.7 字体

`'PingFang SC', 'Noto Sans SC', system-ui, sans-serif`，圆润无衬线。

## 三、吉祥物与插画系统

### 3.1 吉祥物：小老鼠 Mimi

圆润灰色小老鼠，圆耳朵、大眼睛、粉色鼻头和腮红，围着明黄色围巾。扁平 2.5D 手绘插画风格。

三张 PNG 插画（透明背景）：

| 场景 | 姿态 | 尺寸 | 文件 |
|------|------|------|------|
| 首页欢迎 | 站立挥手，开心 | 200×200 | `mascot-welcome.png` |
| 学习陪伴 | 坐姿倾听，微笑 | 120×120 | `mascot-companion.png` |
| 完成庆祝 | 双手举起跳跃，星星环绕 | 240×240 | `mascot-celebrate.png` |

存放路径：`frontend/src/assets/mascot/`

### 3.2 主题地图场景

3 个单元场景节点用 SVG + CSS 绘制：
- 陆地交通：小山丘 + 公路，绿色调
- 天空交通：云朵 + 飞机，天蓝色调
- 水上交通：海浪 + 小船，蓝紫色调

节点状态：已完成（绿勾）/ 进行中（黄箭头）/ 锁定（灰锁）。

## 四、页面布局

### 4.1 首页 HomeView

- 顶部渐变 header + 吉祥物欢迎插画 + 飘浮装饰
- 主体改为主题地图：3 个圆形场景节点用虚线路径串联
- 节点点击进入对应单元列表

### 4.2 单元列表页 ThemeView

- 顶部场景 banner（SVG 地形）
- 卡片圆角 24px、柔和阴影、左侧彩色竖条
- 进度条渐变填充，完成显示星星徽章

### 4.3 课时列表页 UnitView

- 列表项改为学习卡片风格
- 类型标签药丸形（单词课蓝、句型课橙）
- 已完成课时加星星徽章，当前课时轻微脉冲

### 4.4 课时学习页 LessonView

- 暖底背景 + 右上角小老鼠陪伴插画
- 顶部进度条 + 数字标注
- 评分区橙色大号字 + 美化星星
- 结算页小老鼠庆祝插画为主视觉

## 五、组件

### 5.1 重构组件

| 组件 | 重构要点 |
|------|---------|
| `StarBar.vue` | SVG 星星，实心 `--color-accent`，支持 `size` prop |
| `AudioButton.vue` | `--color-success`、扬声器 SVG + 声波动画、胶囊形 |
| `RecordButton.vue` | `--color-warning`、麦克风 SVG、录音红点呼吸 + 波形 |

### 5.2 新增组件

| 组件 | 职责 |
|------|------|
| `AppButton.vue` | 统一按钮基类，variant/size/block |
| `BackBar.vue` | 统一返回栏，替代散落的 `.back-btn` |

## 六、动效规范（CSS only）

| 场景 | 动效 | 时长 |
|------|------|------|
| 星星获得 | 缩放弹入 + 淡入 | 250ms |
| 得分数字 | 弹跳淡入 | 200ms |
| 卡片 hover | 上浮 + 阴影增强 | 150ms |
| 按钮点击 | 按压回弹 | 100ms |
| 录音中 | 红点呼吸 | 1s 循环 |
| 进度条填充 | 宽度过渡 | 300ms |
| 装饰飘浮 | 上下移动 | 6s 循环 |

所有装饰性动画包裹在 `@media (prefers-reduced-motion: no-preference)` 中。

## 七、响应式适配

- 手机（<480px）：单列，地图节点纵向，吉祥物缩小 20%
- 平板（480-800px）：地图横向，header 并排
- 桌面（>800px）：居中 800px 容器

## 八、实施顺序

1. 创建 `tokens.css` + 更新 `main.js` 引入
2. 生成 3 张吉祥物 PNG
3. 新增 `AppButton.vue` + `BackBar.vue`
4. 重构 `StarBar.vue` + `AudioButton.vue` + `RecordButton.vue`
5. 重构 4 个页面视图
6. 更新 `App.vue` 全局容器
7. 启动开发服务器验证
