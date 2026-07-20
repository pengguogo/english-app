#!/bin/bash
# 启动后端(自动加载 .env 中的密钥)
# 用法:cd backend && ./run.sh
set -e

# 加载 .env 环境变量
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
  echo "[run.sh] 已加载 .env 中的环境变量"
else
  echo "[run.sh] 警告:.env 文件不存在,请参考 .env.example 创建"
fi

# 启动 Spring Boot
mvn spring-boot:run
