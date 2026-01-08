# 智能日程助手（Smart Schedule Assistant）

一个基于Vue3 + Spring Boot的智能日程管理系统，支持任务管理、AI自动分类和多轮对话助手。

## 功能特性

- ✅ 用户注册与登录（JWT认证）
- ✅ 任务管理（创建、查看、删除）
- ✅ AI自动分类任务（学习/工作/健康/生活/其他）
- ✅ AI多轮对话助手（支持任务上下文）

## 快速开始

### 前置要求

- Docker 和 Docker Compose
- AI API密钥（提交作业文档里有）

### 启动服务

```bash
# 1. 配置AI API密钥（可选，不配置则使用fallback模式）
# Windows CMD
set AI_API_KEY=sk-你的DeepSeek密钥

# Windows PowerShell
$env:AI_API_KEY="sk-你的DeepSeek密钥"

# Linux/Mac
export AI_API_KEY="sk-你的DeepSeek密钥"

# 2. 启动所有服务
docker compose up -d --build

# 3. 查看日志
docker compose logs -f
```

访问：
- 前端：http://localhost:3000
- 后端：http://localhost:8080

### AI功能配置

**系统默认使用DeepSeek API**（国内可访问，价格便宜）。

1. 获取API密钥：访问 https://platform.deepseek.com/api_keys
2. 设置环境变量 `AI_API_KEY` 后启动服务
3. 详细配置说明：`backend/AI配置说明.md`

**注意**：未配置API密钥时，系统会使用关键词匹配作为fallback，分类准确性会降低。

### 本地开发

```bash
# 后端
cd backend
docker compose -f docker-compose.yml up -d  # 启动MySQL
mvn spring-boot:run

# 前端
cd frontend
npm install
npm run dev
```

## 测试

```bash
cd backend
mvn test
```

测试覆盖：用户认证、任务管理、AI聊天、Service层单元测试（使用H2内存数据库）。

## 技术栈

**后端**：Spring Boot 3.1.4、Spring Security、JWT、MySQL 8.0、JUnit 5

**前端**：Vue 3、Vite、Vue Router、Pinia、Axios

**部署**：Docker、Docker Compose、Nginx

## 常见问题

- **容器名称冲突**：运行 `cleanup-containers.bat`（Windows）或 `./cleanup-containers.sh`（Linux/Mac）
- **端口被占用**：修改 `docker-compose.yml` 中的端口映射
- **数据库连接失败**：等待MySQL完全初始化（30-60秒），查看日志：`docker logs ssa-mysql`
- **AI功能无法使用**：确认已配置 `AI_API_KEY`，查看后端日志：`docker logs ssa-backend`

## 项目文档

- [需求分析](doc/需求分析.md)
- [API设计](doc/API设计.md)
- [数据库设计](doc/数据库设计.md)
- [开发流程](doc/开发流程.md)
- [AI配置说明](backend/AI配置说明.md)

## 许可证

MIT
