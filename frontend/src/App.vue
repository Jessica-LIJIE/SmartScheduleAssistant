<template>
  <div id="app">
    <nav class="navbar">
      <div class="container">
        <h1>智能日程助手</h1>
        <div class="nav-links">
          <router-link to="/" v-if="!isAuthenticated">首页</router-link>
          <router-link to="/tasks" v-if="isAuthenticated">任务管理</router-link>
          <router-link to="/chat" v-if="isAuthenticated">AI助手</router-link>
          <button @click="handleLogout" v-if="isAuthenticated" class="btn btn-secondary">退出</button>
        </div>
      </div>
    </nav>
    <router-view />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const isAuthenticated = computed(() => authStore.isAuthenticated)

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.navbar .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 30px;
}

.navbar h1 {
  color: #667eea;
  font-size: 26px;
  font-weight: 600;
  margin: 0;
}

.nav-links {
  display: flex;
  gap: 30px;
  align-items: center;
}

.nav-links a {
  text-decoration: none;
  color: #333;
  font-weight: 500;
  font-size: 16px;
  padding: 8px 16px;
  border-radius: 6px;
  transition: all 0.3s;
}

.nav-links a:hover {
  color: #667eea;
  background: #f0f0ff;
}

.nav-links a.router-link-active {
  color: #667eea;
  background: #f0f0ff;
  font-weight: 600;
}

.nav-links .btn-secondary {
  font-size: 14px;
  padding: 8px 20px;
}
</style>

