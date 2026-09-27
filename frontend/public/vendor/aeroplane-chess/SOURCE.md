# 极简飞行棋来源

- 上游项目：https://github.com/ZTMYO/MinimalistAeroplaneChess
- 上游版本：`a4da3943c25dba29362b689e35e02ef0759a1711`
- 许可证：GNU GPL v3，完整文本见同目录 `LICENSE`

本项目以独立静态页面形式嵌入上游前端，仅启用本地人机和本地多人玩法。
为了支持部署在 `/app/vendor/aeroplane-chess/` 子目录，对上游源码做了以下构建适配：

1. `frontend/vite.config.js` 增加 `base: './'`。
2. `frontend/js/indexMain.js` 的本地游戏入口由 `/game` 改为 `./game.html`。
3. `frontend/js/settlementModal.js` 与 `frontend/js/multiplayerGameManager.js` 的主页返回地址改为 `./index.html`。
4. `frontend/index.html` 隐藏未接入后端的在线联机入口，并将页脚链接统一指向上游项目。

对应源码可从上述固定提交下载，并应用这些路径变更后使用 `npm ci && npm run build` 重建。
