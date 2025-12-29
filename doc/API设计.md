# 后端 API 设计文档（RESTful）

## 1. API 设计说明

- **后端框架**：Spring Boot
- **API 风格**：RESTful
- **数据格式**：JSON
- **鉴权方式**：JWT Token
- **AI 能力**：
  - 任务分类
  - 多轮对话（前端维护上下文）

---

## 2. 认证模块 API（Auth API）

### 2.1 用户注册

**接口说明**  
用于新用户注册账号

**请求**
```
POST /auth/register
```

**Request Body**
```json
{
  "email": "test@example.com",
  "password": "123456"
}
```

**Response（成功）**
```json
{
  "message": "Register success"
}
```

**说明**
- 密码需在后端加密存储（BCrypt）
- 邮箱唯一，重复注册需返回错误

---

### 2.2 用户登录

**接口说明**  
用户登录并获取 JWT Token

**请求**
```
POST /auth/login
```

**Request Body**
```json
{
  "email": "test@example.com",
  "password": "123456"
}
```

**Response（成功）**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**说明**
- Token 由后端生成
- 前端需保存并在后续请求中携带

---

## 3. 任务管理模块 API（Task API）

所有任务相关接口都需要在 Header 中携带 Token
```
Authorization: Bearer <JWT_TOKEN>
```

### 3.1 创建任务

**请求**
```
POST /tasks
```

**Request Body**
```json
{
  "content": "完成强化学习课程作业"
}
```

**Response**
```json
{
  "id": 1,
  "content": "完成强化学习课程作业",
  "category": null,
  "createdAt": "2025-06-01 10:30:00"
}
```

---

### 3.2 获取任务列表

**请求**
```
GET /tasks
```

**Response**
```json
[
  {
    "id": 1,
    "content": "完成强化学习课程作业",
    "category": "学习",
    "createdAt": "2025-06-01 10:30:00"
  }
]
```

**说明**
- 仅返回当前登录用户的任务

---

### 3.3 删除任务（可选）

**请求**
```
DELETE /tasks/{id}
```

**Response**
```json
{
  "message": "Task deleted"
}
```

---

## 4. AI 任务分类 API

### 4.1 AI 分类任务

**接口说明**  
调用 LLM 对指定任务进行分类

**请求**
```
POST /tasks/{id}/classify
```

**Response**
```json
{
  "taskId": 1,
  "category": "学习"
}
```

**后端逻辑说明**
1. 根据 taskId 查询任务
2. 构造 prompt
3. 调用 LLM API
4. 更新 task.category
5. 返回分类结果

---

## 5. AI 多轮对话 API（前端维护上下文）

### 5.1 多轮对话接口

**接口说明**  
前端发送完整对话上下文 messages，后端直接转发给 LLM

**请求**
```
POST /chat
```

**Request Body**
```json
{
  "messages": [
    { "role": "user", "content": "你好" },
    { "role": "assistant", "content": "你好，我是你的智能助手。" },
    { "role": "user", "content": "帮我规划一下学习任务" }
  ]
}
```

**Response**
```json
{
  "reply": "当然可以，请告诉我你的学习任务有哪些？"
}
```

**说明**
- 不存储对话记录
- 多轮对话上下文由前端负责维护
- 后端保持无状态，结构清晰
