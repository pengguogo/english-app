# 多阶段构建：前端构建 → 后端打包 → 运行
# 用法: docker build -t mimi-app .
# 或:   docker compose up

# ============ Stage 1: 前端构建 ============
FROM node:18-alpine AS frontend-build
WORKDIR /build/frontend
# 先复制 package 文件以利用 Docker 缓存
COPY frontend/package*.json ./
RUN npm ci
# 再复制源码与配置
COPY frontend/ ./
# 构建产物写入 backend/src/main/resources/static/app/（需后端源码可见）
# 这里先输出到默认 dist，下一步再合并
RUN npm run build

# ============ Stage 2: 后端打包（含前端产物） ============
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /build
# 复制后端源码
COPY backend/ ./backend/
# 将前端构建产物放入后端静态资源目录
COPY --from=frontend-build /build/frontend/dist/ ./backend/src/main/resources/static/app/
# 打包（跳过测试，因为 CI 场景应已通过测试；本地构建可用 --build-arg 覆盖）
ARG SKIP_TESTS=true
RUN --mount=type=cache,target=/root/.m2 \
    cd backend && \
    mvn clean package -DskipTests=${SKIP_TESTS}

# ============ Stage 3: 运行 ============
FROM eclipse-temurin:17-jre
WORKDIR /app
# 复制构建好的 JAR
COPY --from=backend-build /build/backend/target/*.jar app.jar
# 数据库与缓存目录
RUN mkdir -p /app/data /app/.tts-cache
ENV TTS_CACHE_DIR=/app/.tts-cache
# 百度语音密钥通过环境变量注入（可选）
ENV BAIDU_APP_ID= \
    BAIDU_API_KEY= \
    BAIDU_SECRET_KEY=
# 暴露端口
EXPOSE 8080
# 数据库持久化
VOLUME ["/app/data"]
ENTRYPOINT ["java", "-jar", "app.jar"]
