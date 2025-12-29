<template>
  <div class="login-page">
    <div class="container">
      <div class="card login-card">
        <h2>用户登录</h2>
        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label class="label">邮箱</label>
            <input
              type="email"
              v-model="email"
              class="input"
              placeholder="请输入邮箱"
              required
            />
          </div>
          <div class="form-group">
            <label class="label">密码</label>
            <input
              type="password"
              v-model="password"
              class="input"
              placeholder="请输入密码"
              required
            />
          </div>
          <div v-if="error" class="error-message">{{ error }}</div>
          <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
            {{ loading ? '登录中...' : '登录' }}
          </button>
          <div class="form-footer">
            <span>还没有账号？</span>
            <router-link to="/register">立即注册</router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

const handleLogin = async () => {
  error.value = ''
  loading.value = true
  
  const result = await authStore.login(email.value, password.value)
  
  if (result.success) {
    router.push('/tasks')
  } else {
    error.value = result.error
  }
  
  loading.value = false
}
</script>

<style scoped>
.login-page {
  min-height: calc(100vh - 80px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  max-width: 500px;
  width: 100%;
  margin: 0 auto;
  padding: 40px;
}

.login-card h2 {
  text-align: center;
  margin-bottom: 35px;
  color: #667eea;
  font-size: 28px;
  font-weight: 600;
}

.form-group {
  margin-bottom: 24px;
}

.form-group .label {
  font-size: 15px;
  margin-bottom: 8px;
}

.form-group .input {
  padding: 12px 16px;
  font-size: 15px;
}

.error-message {
  color: #dc3545;
  margin-bottom: 15px;
  padding: 10px;
  background: #f8d7da;
  border-radius: 4px;
}

.form-footer {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.form-footer a {
  color: #667eea;
  text-decoration: none;
  margin-left: 5px;
}

.form-footer a:hover {
  text-decoration: underline;
}

.btn-block {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  font-weight: 500;
  margin-top: 10px;
}
</style>

