# 部署到阿里云(直装裸 JAR + systemd)

面向 **2 核 2G / Alibaba Cloud Linux 3 / 3Mbps** 的小规格云主机,采用直装 JRE 跑 JAR + systemd 管理的方案,省去 Docker daemon 的内存开销。

> 不使用 Docker。项目原设计即"单 JAR 部署"(Vue 构建产物嵌入 Spring Boot 静态资源),SQLite 内嵌,无外部依赖服务。

---

## 一、架构与流程

```
GitHub (main push) ──> GitHub Actions ──SSH(密码)──> 云主机
                                              ├─ git pull --ff-only
                                              ├─ ./scripts/deploy.sh  (前端构建+Maven打包)
                                              └─ systemctl restart mimi

公网用户 ──HTTP:8080──> 39.96.59.120:8080/app/
```

- 构建在云主机本地跑(首次慢,有 Maven/npm 缓存后增量快)
- systemd 管服务:开机自启、崩溃重启、日志到 `/var/log/mimi/app.log`
- 内存限制:`-Xmx512m -Xss256k`(2G 机器预留系统资源)

---

## 二、云主机初始化(一次性)

### 1. SSH 登录

```bash
ssh root@39.96.59.120
```

### 2. 安装运行与构建依赖

```bash
# 基础工具
dnf install -y git tar wget

# Node.js 18(前端构建用,NodeSource 源)
curl -fsSL https://rpm.nodesource.com/setup_18.x | bash -
dnf install -y nodejs

# Amazon Corretto 17(JRE+构建用)
# Alibaba Cloud Linux 3 的 AppStream 通常自带 corretto17 源
dnf install -y java-17-amazon-corretto-devel
# 若上面找不到,改用:
# dnf install -y java-17-openjdk-devel

# Maven
dnf install -y maven

# 验证
java -version
mvn -v
node -v
npm -v
```

### 3. 安全组放行端口(阿里云控制台)

- `22` SSH
- `8080` HTTP(阶段一临时公网访问)

> 后续上 HTTPS/Nginx 后,关闭 `8080` 公网,改开 `80`/`443`。

---

## 三、克隆仓库与首次部署

```bash
mkdir -p /opt && cd /opt

# 方式 A:HTTPS 克隆(若仓库公开)
git clone https://github.com/pengguogo/english-app.git

# 方式 B:SSH 克隆(需在云主机配 GitHub SSH key)
# git clone git@github.com:pengguogo/english-app.git

cd english-app
```

### 配置百度语音密钥(可选,不配则语音功能降级)

手动创建 `.env` 并填入密钥(此文件已被 `.gitignore` 忽略,不会提交):

```bash
cat >.env <<'EOF'
BAIDU_APP_ID=你的AppID
BAIDU_API_KEY=你的ApiKey
BAIDU_SECRET_KEY=你的SecretKey
EOF
```

### 首次构建

```bash
./scripts/deploy.sh
```

> 首次会下载 Maven/npm 依赖,耗时较长(5-15 分钟)。后续有缓存会快很多。

### 配置 systemd 服务

```bash
# 日志目录
mkdir -p /var/log/mimi

# 安装服务单元
cp scripts/mimi.service /etc/systemd/system/mimi.service
systemctl daemon-reload
systemctl enable --now mimi

# 查看状态
systemctl status mimi
tail -f /var/log/mimi/app.log
```

### 验证

浏览器访问:`http://39.96.59.120:8080/app/`

健康检查:
```bash
curl http://localhost:8080/api/v1/subjects
```

---

## 四、自动化部署配置(GitHub Actions)

推送 main 分支即自动部署到云主机。需在 GitHub 仓库配置 Secrets。

### 1. 在 GitHub 仓库配置 Secrets

仓库页面 → `Settings` → `Secrets and variables` → `Actions` → `New repository secret`,添加:

| Secret 名 | 值 | 说明 |
|---|---|---|
| `ALIYUN_HOST` | `39.96.59.120` | 云主机公网 IP |
| `ALIYUN_USER` | `root` | SSH 登录用户 |
| `ALIYUN_PASSWORD` | 你的云主机登录密码 | 明文存为 Secret(GitHub 加密存储) |

> 密码方式安全性低于 SSH 密钥,但配置简单。建议云主机密码设强密码,并考虑后续改为 SSH 密钥(见第六节)。

### 2. 工作流文件

已配置 `.github/workflows/deploy.yml`,触发条件:
- `push` 到 `main` 分支
- 手动触发(Actions 页面 → `Run workflow`)

### 3. 首次自动部署

推送代码到 main 后,在 GitHub Actions 页面查看部署日志。云主机侧验证:
```bash
systemctl status mimi
tail -f /var/log/mimi/app.log
```

---

## 五、日常运维

| 任务 | 命令 |
|---|---|
| 查看状态 | `systemctl status mimi` |
| 重启服务 | `systemctl restart mimi` |
| 停止服务 | `systemctl stop mimi` |
| 查看日志 | `tail -f /var/log/mimi/app.log` |
| 重新构建 | `cd /opt/english-app && ./scripts/deploy.sh && systemctl restart mimi` |
| 手动部署 | `cd /opt/english-app && git pull && ./scripts/deploy.sh && systemctl restart mimi` |
| 回滚 | `cd /opt/english-app && git checkout <旧commit> && ./scripts/deploy.sh && systemctl restart mimi` |

### 数据库备份(SQLite)

```bash
# 写入 /etc/cron.daily/backup-mimi
cat >/etc/cron.daily/backup-mimi <<'EOF'
#!/bin/bash
BACKUP_DIR=/opt/backup
mkdir -p $BACKUP_DIR
cp /opt/english-app/backend/english.db $BACKUP_DIR/english-$(date +%F).db
find $BACKUP_DIR -name 'english-*.db' -mtime +7 -delete
EOF
chmod +x /etc/cron.daily/backup-mimi
```

---

## 六、后续增强(可选,本次不做)

### 1. SSH 密钥替代密码(更安全)

```bash
# 本地生成密钥对
ssh-keygen -t ed25519 -f ~/.ssh/aliyun_mimi -C "github-actions-deploy"

# 公钥追加到云主机
ssh-copy-id -i ~/.ssh/aliyun_mimi.pub root@39.96.59.120

# 私钥内容粘贴到 GitHub Secret: ALIYUN_SSH_KEY
```

deploy.yml 改用 `key: ${{ secrets.ALIYUN_SSH_KEY }}` 替代 `password`。

### 2. HTTPS + Nginx 反向代理

有域名后:
1. 装 Nginx:`dnf install -y nginx`
2. 配置反代到 `127.0.0.1:8080`
3. 申请 Let's Encrypt 证书:`dnf install -y certbot python3-certbot-nginx && certbot --nginx`
4. 安全组:关 `8080` 公网,开 `80`/`443`

### 3. 构建压力缓解(若 2G 顶不住)

Maven + npm 同时跑可能 OOM。预案:
- 在 GitHub Actions 里跑构建,产物 JAR 经 `scp` 上传到云主机
- 云主机只执行 `systemctl restart mimi`
- 触发条件:Maven 报 `Java heap space` 或 `free -m` 持续低于 200MB

---

## 七、排障

| 现象 | 排查 |
|---|---|
| 服务起不来 | `systemctl status mimi` + `journalctl -u mimi -n 100` |
| 端口不通 | `ss -tlnp \| grep 8080` + 阿里云安全组是否放行 8080 |
| 构建失败 | 检查 `./scripts/deploy.sh` 输出,常见为 npm/maven 依赖下载失败 |
| 内存不足 | `free -m`,构建时 OOM 用 `-Xmx256m` 或启用第六节方案 |
| git pull 冲突 | `cd /opt/english-app && git status`,手动解决或 `git reset --hard origin/main` |
| GitHub Actions SSH 失败 | 检查 Secrets 值、云主机 22 端口安全组、密码是否正确 |
