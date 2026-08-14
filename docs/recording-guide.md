# 录制演示 GIF 指南

本文档说明如何为 README 录制 15~30 秒的产品演示 GIF，这是提升 GitHub
Star 转化率性价比最高的改动。

## 推荐路径

```
首页 → 选择「英语」学科 → 选择主题 → 进入单元 → 选择课程
→ 播放发音 → 跟读录音 → 获得评分 → 完成课程庆祝
```

全程约 20 秒，覆盖核心学习闭环。

## 工具

- [OBS Studio](https://obsproject.com/)（免费，跨平台）录屏
- [ffmpeg](https://ffmpeg.org/) 压缩为 GIF
- 或 [Gifski](https://gif.ski/)（macOS，质量更高）

## 录制步骤

1. 本地启动应用：`docker compose up` 或按 README「本地开发」启动
2. 浏览器访问 `http://localhost:8080/app/`
3. 录屏上述路径，建议 1280×720 分辨率，时长控制在 20~30 秒
4. 用 ffmpeg 转为 GIF：

```bash
# 先录制为 mp4,再转换
ffmpeg -i recording.mp4 \
  -vf "fps=12,scale=640:-1:flags=lanczos" \
  -c:v gif -b:v 2M docs/assets/demo.gif
```

参数说明：
- `fps=12`：12 帧足够流畅，控制体积
- `scale=640`：宽度 640，README 中展示清晰且文件不大
- 目标体积 < 5MB，GitHub 渲染更流畅

## 截图

除 GIF 外，建议在 `docs/assets/` 放 3 张关键页面截图（首页、学习页、
完成庆祝页），用于 README 表格展示。建议尺寸 1280×800。

## 替换占位

录好后将文件放入 `docs/assets/`，并修改 README 中的占位链接：

```markdown
![Mimi 启蒙乐园演示](docs/assets/demo.gif)
```

截图同理替换表格中的「占位」文字。
