<template>
  <div class="register-page">
    <div class="container">
      <div class="card register-card">
        <h2>用户注册</h2>
        <form @submit.prevent="handleRegister">
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
              placeholder="请输入密码（至少6位）"
              required
              minlength="6"
            />
          </div>
          <div v-if="error" class="error-message">{{ error }}</div>
          <div v-if="success" class="success-message">注册成功！正在跳转...</div>
          <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
            {{ loading ? '注册中...' : '注册' }}
          </button>
          <div class="form-footer">
            <span>已有账号？</span>
            <router-link to="/login">立即登录</router-link>
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
const success = ref(false)
const loading = ref(false)

const handleRegister = async () => {
  error.value = ''
  success.value = false
  loading.value = true
  
  const result = await authStore.register(email.value, password.value)
  
  if (result.success) {
    success.value = true
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } else {
    error.value = result.error
  }
  
  loading.value = false
}
</script>

<style scoped>
.register-page {
  min-height: calc(100vh - 80px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-card {
  max-width: 500px;
  width: 100%;
  margin: 0 auto;
  padding: 40px;
}

.register-card h2 {
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

.success-message {
  color: #28a745;
  margin-bottom: 15px;
  padding: 10px;
  background: #d4edda;
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

