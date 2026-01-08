<template>
  <div class="tasks-page">
    <div class="container">
      <div class="card">
        <h2 class="section-title">任务管理</h2>
        <form @submit.prevent="createTask" class="task-form">
          <input
            type="text"
            v-model="newTaskContent"
            class="input"
            placeholder="输入新任务..."
            required
          />
          <button type="submit" class="btn btn-primary">创建任务</button>
        </form>
      </div>

      <div class="card">
        <h3 class="section-title">我的任务列表</h3>
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="tasks.length === 0" class="empty">暂无任务</div>
        <div v-else class="task-list">
          <div v-for="task in tasks" :key="task.id" class="task-item">
            <div class="task-content">
              <p>{{ task.content }}</p>
              <div class="task-meta">
                <span class="category" :class="getCategoryClass(task.category)">
                  {{ task.category || '未分类' }}
                </span>
                <span class="date">{{ formatDate(task.createdAt) }}</span>
              </div>
            </div>
            <div class="task-actions">
              <button
                @click="classifyTask(task.id)"
                class="btn btn-secondary"
                :disabled="classifyingId === task.id || deletingId === task.id"
              >
                {{ classifyingId === task.id ? '分类中...' : 'AI分类' }}
              </button>
              <button
                @click="deleteTask(task.id)"
                class="btn btn-danger"
                :disabled="classifyingId === task.id || deletingId === task.id"
              >
                {{ deletingId === task.id ? '删除中...' : '删除' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'

const tasks = ref([])
const newTaskContent = ref('')
const loading = ref(false)
const classifyingId = ref(null)
const deletingId = ref(null)

const fetchTasks = async () => {
  loading.value = true
  try {
    const response = await api.get('/tasks')
    tasks.value = response.data
  } catch (error) {
    console.error('获取任务失败:', error)
  } finally {
    loading.value = false
  }
}

const createTask = async () => {
  if (!newTaskContent.value.trim()) return
  
  try {
    await api.post('/tasks', { content: newTaskContent.value })
    newTaskContent.value = ''
    await fetchTasks()
  } catch (error) {
    console.error('创建任务失败:', error)
    alert('创建任务失败: ' + (error.response?.data?.error || '未知错误'))
  }
}

const classifyTask = async (taskId) => {
  classifyingId.value = taskId
  try {
    await api.post(`/tasks/${taskId}/classify`)
    await fetchTasks()
  } catch (error) {
    console.error('分类任务失败:', error)
    alert('分类任务失败: ' + (error.response?.data?.error || '未知错误'))
  } finally {
    classifyingId.value = null
  }
}

const deleteTask = async (taskId) => {
  if (!confirm('确定要删除这个任务吗？')) {
    return
  }
  
  deletingId.value = taskId
  try {
    await api.delete(`/tasks/${taskId}`)
    await fetchTasks()
  } catch (error) {
    console.error('删除任务失败:', error)
    alert('删除任务失败: ' + (error.response?.data?.error || '未知错误'))
  } finally {
    deletingId.value = null
  }
}

const getCategoryClass = (category) => {
  const classes = {
    '学习': 'category-study',
    '工作': 'category-work',
    '健康': 'category-health',
    '其他': 'category-other'
  }
  return classes[category] || 'category-other'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

onMounted(() => {
  fetchTasks()
})
</script>

<style scoped>
.tasks-page {
  padding: 20px 0;
}

.task-form {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.task-list {
  margin-top: 20px;
}

.task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s;
  border-radius: 8px;
  margin-bottom: 8px;
}

.task-item:hover {
  background-color: #f9f9f9;
}

.task-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.task-content {
  flex: 1;
}

.task-content p {
  margin-bottom: 10px;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  line-height: 1.5;
}

.task-meta {
  display: flex;
  gap: 15px;
  align-items: center;
}

.category {
  padding: 6px 14px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 500;
  display: inline-block;
}

.category-study {
  background: #e3f2fd;
  color: #1976d2;
}

.category-work {
  background: #fff3e0;
  color: #f57c00;
}

.category-health {
  background: #e8f5e9;
  color: #388e3c;
}

.category-other {
  background: #f3e5f5;
  color: #7b1fa2;
}

.btn-danger {
  background: #f44336;
  color: white;
  border: none;
}

.btn-danger:hover:not(:disabled) {
  background: #d32f2f;
}

.btn-danger:disabled {
  background: #ffcdd2;
  cursor: not-allowed;
}

.date {
  font-size: 12px;
  color: #999;
}

.task-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.task-actions .btn {
  font-size: 13px;
  padding: 8px 16px;
}

.loading, .empty {
  text-align: center;
  padding: 40px;
  color: #999;
}

h2, h3 {
  margin-bottom: 24px;
  color: #333;
  font-weight: 600;
}

h2 {
  font-size: 24px;
}

h3 {
  font-size: 20px;
}

.section-title {
  margin-bottom: 24px;
  color: #333;
  font-weight: 600;
  font-size: 20px;
}

.task-form {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

.task-form .input {
  flex: 1;
  margin-bottom: 0;
}

.task-form .btn {
  white-space: nowrap;
}
</style>

