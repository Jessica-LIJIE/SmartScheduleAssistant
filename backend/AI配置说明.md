# AI服务配置说明

## 概述

系统已实现真正的AI分类和聊天功能，支持调用外部LLM API。**默认使用 DeepSeek API**（国内可访问，价格便宜），也支持 OpenAI 等其他服务。

## 配置步骤

### 1. 获取AI API密钥

根据你选择的AI服务提供商，获取API密钥：

- **DeepSeek（推荐，默认）**: 访问 https://platform.deepseek.com/api_keys
- **OpenAI**: 访问 https://platform.openai.com/api-keys
- **Claude (Anthropic)**: 访问 https://console.anthropic.com/
- **文心一言**: 访问 https://cloud.baidu.com/
- **通义千问**: 访问 https://dashscope.aliyun.com/

### 2. 配置API密钥

有两种方式配置：

#### 方式1：环境变量（推荐，更安全）

**使用 DeepSeek（默认，推荐）**：

```bash
# Windows CMD
set AI_API_KEY=sk-你的DeepSeek密钥
docker compose up -d --build

# Windows PowerShell
$env:AI_API_KEY="sk-你的DeepSeek密钥"
docker compose up -d --build

# Linux/Mac
export AI_API_KEY="sk-你的DeepSeek密钥"
docker compose up -d --build
```

**使用 OpenAI**（需要代理）：

```bash
# Windows CMD
set AI_API_KEY=sk-你的OpenAI密钥
set AI_PROVIDER=openai
docker compose up -d --build
```

**使用自定义API URL**：

```bash
set AI_API_KEY=sk-你的密钥
set AI_API_URL=https://你的API地址/v1/chat/completions
docker compose up -d --build
```

#### 方式2：配置文件（不推荐，会暴露密钥）

在 `backend/src/main/resources/application.yml` 中直接配置：

```yaml
ai:
  provider: deepseek  # 或 openai
  api:
    key: your-api-key-here
    url: https://api.deepseek.com/v1/chat/completions  # 可选，默认会根据provider自动选择
```

**注意**：如果使用配置文件方式，请确保不要将API密钥提交到Git仓库！

### 3. 重启服务

```bash
docker compose restart backend
```

## 支持的AI服务

### DeepSeek（默认，推荐）

DeepSeek 是国内可访问的AI服务，API格式与OpenAI兼容，价格便宜。

**环境变量配置**：
```bash
set AI_API_KEY=sk-你的DeepSeek密钥
docker compose up -d --build
```

**默认配置**：
- API URL: `https://api.deepseek.com/v1/chat/completions`
- Model: `deepseek-chat`
- 无需额外配置，系统会自动使用DeepSeek

### OpenAI

**环境变量配置**：
```bash
set AI_API_KEY=sk-你的OpenAI密钥
set AI_PROVIDER=openai
docker compose up -d --build
```

**配置说明**：
- API URL: `https://api.openai.com/v1/chat/completions`（自动）
- Model: `gpt-3.5-turbo`
- **注意**：国内访问需要代理或使用中转服务

### 其他AI服务

如果需要使用 Claude、文心一言、通义千问等，需要：
1. 设置 `AI_API_URL` 环境变量指向对应的API地址
2. 确保API格式与OpenAI兼容（或修改 `LlmService.java` 中的请求构建逻辑）

## 测试

配置完成后，可以：

1. **测试分类功能**：
   - 在前端创建任务
   - 点击"AI分类"按钮
   - 查看分类结果

2. **测试聊天功能**：
   - 访问AI助手页面
   - 发送消息
   - 查看AI回复

## 故障排除

### 问题1：分类返回"其他"或失败

- 检查API密钥是否正确
- 检查网络连接（是否能访问AI服务）
- 查看后端日志：`docker logs ssa-backend`

### 问题2：AI聊天无响应

- 检查API密钥配置
- 确认API URL正确
- 查看后端日志中的错误信息

### 问题3：API调用超时

- 检查网络连接
- 考虑增加超时时间（在 `RestTemplate` 配置中）

## 成本说明

使用外部AI API会产生费用：
- **DeepSeek**: 按token计费，价格非常便宜（约 $0.00014/1K tokens），适合国内使用
- **OpenAI**: 按token计费，约 $0.002/1K tokens
- 建议在AI服务商后台设置使用限额以避免意外费用

## 开发模式

如果没有配置API密钥，系统会使用fallback的关键词匹配作为备用方案，但分类准确性会降低。




