#!/bin/bash
# 云主机部署脚本:前端构建 → 复制静态资源 → Maven 打包
# 用法:在项目根目录执行 ./scripts/deploy.sh
# 由 GitHub Actions(deploy.yml)经 SSH 调用,或人工在云主机执行
set -e

# 切到项目根目录(脚本可能在 scripts/ 下被调用)
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"
echo "[deploy] 工作目录: $(pwd)"

# ---------- 1. 前端构建 ----------
echo "[deploy] 1/3 前端构建"
cd frontend
# 安装依赖(有 lock 文件用 ci,更快更稳定)
if [ -d node_modules ] && [ package-lock.json -nt node_modules/.package-lock.json ] 2>/dev/null; then
  echo "[deploy] node_modules 较新,跳过安装"
else
  echo "[deploy] 安装依赖 (npm ci)"
  npm ci
fi
# 构建产物直接输出到 backend/src/main/resources/static/app
npm run build
cd "$ROOT_DIR"
echo "[deploy] 前端产物已写入 backend/src/main/resources/static/app"

# ---------- 2. 后端打包 ----------
echo "[deploy] 2/3 后端 Maven 打包"
cd backend
# 跳过测试:CI 已跑过测试;云主机资源紧张时避免重复跑
# 如需验收测试,手动执行 mvn test
mvn clean package -DskipTests -q
cd "$ROOT_DIR"

# 校验产物
JAR_PATH=$(ls backend/target/english-app-backend-*.jar 2>/dev/null | head -1)
if [ -z "$JAR_PATH" ]; then
  echo "[deploy] 错误:未找到构建产物 JAR" >&2
  exit 1
fi
echo "[deploy] 产物: $JAR_PATH"

# ---------- 3. 完成 ----------
echo "[deploy] 3/3 构建完成,等待 systemd 重启服务"
echo "[deploy] 提示:由 deploy.yml 调用 systemctl restart mimi,或手动执行"
