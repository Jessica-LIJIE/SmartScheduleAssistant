<template>
  <div class="home">
    <div class="container">
      <div class="hero">
        <h1>欢迎使用智能日程助手</h1>
        <p>高效管理您的任务，AI智能分类，对话助手</p>
        <div class="actions">
          <router-link to="/register" class="btn btn-primary">立即注册</router-link>
          <router-link to="/login" class="btn btn-secondary">登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.home {
  min-height: calc(100vh - 80px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero {
  text-align: center;
  color: white;
  padding: 60px 20px;
}

.hero h1 {
  font-size: 48px;
  margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.hero p {
  font-size: 20px;
  margin-bottom: 40px;
  opacity: 0.9;
}

.actions {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.actions .btn {
  padding: 15px 30px;
  font-size: 16px;
  text-decoration: none;
  display: inline-block;
}
</style>

