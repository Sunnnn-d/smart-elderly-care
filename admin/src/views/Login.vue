<template>
  <div class="login-page">
    <div class="login-box">
      <div class="login-header">
        <el-icon :size="56" color="#FF8C00"><Sunny /></el-icon>
        <h1>暖夕伴养老管理系统</h1>
        <p>后台管理平台</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" size="large">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            clearable
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" round style="width:100%;height:48px;font-size:16px;" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <span>还没有账号？</span>
        <router-link to="/register">立即注册</router-link>
      </div>
    </div>
    <div class="login-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await login(form)
    const { token, username } = res.data
    localStorage.setItem('admin_token', token)
    localStorage.setItem('admin_username', username)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (e) {
    console.error('登录失败', e)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  position: relative;
  overflow: hidden;
}

.login-box {
  width: 440px;
  padding: 50px 48px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.4);
  position: relative;
  z-index: 10;

  .login-header {
    text-align: center;
    margin-bottom: 40px;

    h1 {
      font-size: 1.6rem;
      color: #1a1a2e;
      margin: 16px 0 8px;
      font-weight: 600;
    }

    p {
      color: #666;
      font-size: 0.95rem;
    }
  }

  :deep(.el-input__wrapper) {
    padding: 12px 16px;
    border-radius: 12px;
    box-shadow: 0 0 0 1px #e0e0e0;
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 0 0 1px #FF8C00;
    }

    &:focus-within {
      box-shadow: 0 0 0 2px rgba(255, 140, 0, 0.2);
    }
  }

  :deep(.el-button--primary) {
    background: linear-gradient(135deg, #FF8C00 0%, #FF6B00 100%);
    border: none;
    margin-top: 10px;
    font-weight: 500;
    letter-spacing: 2px;
    transition: all 0.3s;

    &:hover {
      background: linear-gradient(135deg, #FF6B00 0%, #FF8C00 100%);
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(255, 140, 0, 0.4);
    }

    &:active {
      transform: translateY(0);
    }
  }
}

.login-footer {
  text-align: center;
  margin-top: 28px;
  font-size: 0.95rem;

  span {
    color: #999;
  }

  a {
    color: #FF8C00;
    text-decoration: none;
    font-weight: 500;
    margin-left: 4px;

    &:hover {
      text-decoration: underline;
    }
  }
}

.login-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;

  .decoration-circle {
    position: absolute;
    border-radius: 50%;
    background: linear-gradient(135deg, rgba(255, 140, 0, 0.1) 0%, rgba(255, 107, 0, 0.05) 100%);
    animation: float 20s ease-in-out infinite;
  }

  .circle-1 {
    width: 400px;
    height: 400px;
    top: -100px;
    right: -100px;
    animation-delay: 0s;
  }

  .circle-2 {
    width: 300px;
    height: 300px;
    bottom: -50px;
    left: -50px;
    animation-delay: -5s;
  }

  .circle-3 {
    width: 200px;
    height: 200px;
    top: 50%;
    left: 20%;
    animation-delay: -10s;
  }

  @keyframes float {
    0%, 100% {
      transform: translate(0, 0) scale(1);
    }
    25% {
      transform: translate(20px, -20px) scale(1.05);
    }
    50% {
      transform: translate(0, -40px) scale(1);
    }
    75% {
      transform: translate(-20px, -20px) scale(0.95);
    }
  }
}
</style>
