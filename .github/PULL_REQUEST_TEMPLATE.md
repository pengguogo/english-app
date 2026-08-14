## 变更说明

<!-- 简述本 PR 做了什么、为什么这么做 -->

## 变更类型

- [ ] feat  新增功能
- [ ] fix  修复缺陷
- [ ] docs  文档
- [ ] refactor  重构
- [ ] test  测试
- [ ] chore  构建/配置

## 自检清单

- [ ] 代码注释使用中文
- [ ] 后端遵循分层约定（Controller/Service/Repository/DTO）
- [ ] Controller 不返回 JPA 实体
- [ ] Service 公共方法已补单测，方法名 `should_期望行为_当_条件`
- [ ] 前端色值统一用 `var(--xxx)`，未硬编码
- [ ] 新增/修改课程已配图（`image` 非空，emoji 不可替代）
- [ ] 课程改动通过新 Flyway 迁移交付，未修改已提交迁移
- [ ] 后端测试通过：`cd backend && mvn test`
- [ ] 前端构建通过：`cd frontend && npm ci && npm run build`

## 截图 / 演示

<!-- 若涉及 UI 改动，请附截图或 GIF -->

## 关联 Issue

<!-- 例如 Closes #12 -->
