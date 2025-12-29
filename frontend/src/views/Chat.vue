<template>
  <div class="chat-page">
    <div class="container">
      <div class="card chat-container">
        <h2 class="section-title">AI智能助手</h2>
        <div class="chat-messages" ref="messagesContainer">
          <div
            v-for="(message, index) in messages"
            :key="index"
            :class="['message', message.role]"
          >
            <div class="message-content">
              <div class="message-role">
                {{ message.role === 'user' ? '我' : 'AI助手' }}
              </div>
              <div class="message-text">{{ message.content }}</div>
            </div>
          </div>
          <div v-if="loading" class="message assistant">
            <div class="message-content">
              <div class="message-role">AI助手</div>
              <div class="message-text">正在思考...</div>
            </div>
          </div>
        </div>
        <form @submit.prevent="sendMessage" class="chat-input-form">
          <input
            type="text"
            v-model="inputMessage"
            class="input chat-input"
            placeholder="输入消息..."
            :disabled="loading"
            required
          />
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? '发送中...' : '发送' }}
          </button>
          <button
            type="button"
            @click="clearChat"
            class="btn btn-secondary"
            :disabled="loading"
          >
            清空对话
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import api from '../api'

const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const messagesContainer = ref(null)
const tasks = ref([])

// 获取当前用户的任务列表，用于在聊天时提供上下文
const fetchTasks = async () => {
  try {
    const response = await api.get('/tasks')
    tasks.value = response.data || []
    console.log('获取到任务列表，数量:', tasks.value.length, '任务:', tasks.value)
  } catch (error) {
    console.error('获取任务列表失败（聊天上下文）:', error)
    tasks.value = []
  }
}

const buildTasksContextMessage = () => {
  console.log('buildTasksContextMessage 被调用，tasks.value:', tasks.value)
  if (!tasks.value || tasks.value.length === 0) {
    console.warn('任务列表为空，无法构建任务上下文')
    return null
  }

  const lines = tasks.value.map((t, index) => {
    const category = t.category || '未分类'
    return `${index + 1}. [${category}] ${t.content}`
  })

  const contextMessage = {
    role: 'system',
    content:
      '你是一个智能日程助手，帮助用户管理任务和规划时间。' +
      '下面是用户当前的任务列表，请在回答时参考这些任务，不要编造不存在的任务：\n' +
      lines.join('\n')
  }
  
  console.log('构建的任务上下文消息:', contextMessage)
  return contextMessage
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return

  const userMessage = {
    role: 'user',
    content: inputMessage.value
  }

  // 只在界面上记录用户消息
  messages.value.push(userMessage)
  inputMessage.value = ''
  loading.value = true

  scrollToBottom()

  // 每次发送消息前，重新获取最新的任务列表（确保能获取到刚创建的任务）
  console.log('发送消息前，开始获取任务列表...')
  await fetchTasks()
  console.log('获取任务列表完成，任务数量:', tasks.value.length)
  
  // 构造发送给后端 / LLM 的完整消息列表
  // 重要：只包含 user 和 assistant 的对话历史，排除欢迎消息（role为assistant但content是欢迎语的）
  const payloadMessages = messages.value
    .filter(msg => {
      // 排除欢迎消息（assistant的欢迎语）
      if (msg.role === 'assistant' && msg.content.includes('你好！我是你的智能日程助手')) {
        console.log('过滤掉欢迎消息:', msg.content.substring(0, 50))
        return false
      }
      return true
    })
    .map(msg => ({
      role: msg.role,
      content: msg.content
    }))
  
  console.log('过滤后的对话消息:', payloadMessages)
  
  // 将任务上下文作为 system 消息放在最前面
  const tasksContext = buildTasksContextMessage()
  
  // 调试日志：检查任务列表
  console.log('当前任务列表:', tasks.value)
  console.log('任务上下文消息:', tasksContext)
  console.log('过滤后的对话消息数量:', payloadMessages.length)
  console.log('发送给后端的消息数量:', payloadMessages.length + (tasksContext ? 1 : 0))
  
  if (tasksContext) {
    payloadMessages.unshift(tasksContext)
    console.log('已添加system消息到payload，现在第一条消息是:', payloadMessages[0])
  } else {
    console.warn('⚠️ 任务上下文为空，不会发送system消息！')
  }
  
  console.log('最终发送给后端的消息列表:', payloadMessages)

  try {
    const response = await api.post('/chat', {
      messages: payloadMessages
    })

    const assistantMessage = {
      role: 'assistant',
      content: response.data.reply
    }

    messages.value.push(assistantMessage)
  } catch (error) {
    console.error('发送消息失败:', error)
    messages.value.push({
      role: 'assistant',
      content: '抱歉，发生了错误：' + (error.response?.data?.error || '未知错误')
    })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const clearChat = () => {
  messages.value = []
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

onMounted(async () => {
  await fetchTasks()
  // 欢迎消息（只显示在界面上，不发送给后端）
  messages.value.push({
    role: 'assistant',
    content:
      '你好！我是你的智能日程助手，可以根据你的任务列表帮你整理计划、安排日程。' +
      '你可以对我说："帮我看看今天有哪些任务" 或 "帮我规划一下今晚的学习时间"。',
    isWelcome: true // 标记为欢迎消息，发送给后端时排除
  })
})
</script>

<style scoped>
.chat-page {
  padding: 20px 0;
}

.chat-container {
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 200px);
}

.chat-container .section-title {
  margin-bottom: 24px;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: linear-gradient(to bottom, #f8f9fa, #ffffff);
  border-radius: 12px;
  margin-bottom: 24px;
  min-height: 450px;
  border: 1px solid #e9ecef;
}

.message {
  margin-bottom: 24px;
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-content {
  max-width: 75%;
}

.message.user .message-content {
  margin-left: auto;
}

.message.assistant .message-content {
  margin-right: auto;
}

.message-role {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.message-text {
  padding: 14px 18px;
  border-radius: 16px;
  word-wrap: break-word;
  line-height: 1.6;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.message.user .message-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 4px;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.message.assistant .message-text {
  background: white;
  color: #333;
  border: 1px solid #e0e0e0;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.chat-input-form {
  display: flex;
  gap: 12px;
  align-items: center;
}

.chat-input {
  flex: 1;
  margin-bottom: 0;
  padding: 14px 18px;
  font-size: 15px;
}

.chat-input-form .btn {
  padding: 14px 24px;
  font-size: 15px;
  white-space: nowrap;
}
</style>

