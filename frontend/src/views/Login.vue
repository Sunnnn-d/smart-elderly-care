<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-left">
        <div class="login-brand">
          <el-icon :size="72" color="#FFFFFF"><Sunny /></el-icon>
          <h1>暖夕伴养老</h1>
          <p>温暖服务 从心开始</p>
        </div>
        <div class="login-features">
          <div class="feature-item">
            <el-icon><Clock /></el-icon>
            <span>24小时专业护理</span>
          </div>
          <div class="feature-item">
            <el-icon><FirstAidKit /></el-icon>
            <span>健康实时监测</span>
          </div>
          <div class="feature-item">
            <el-icon><User /></el-icon>
            <span>个性化服务</span>
          </div>
        </div>
      </div>
      <div class="login-right">
        <div class="login-form-wrap">
          <h2>用户登录</h2>
          <p class="login-tip">欢迎回来，请输入您的账号信息</p>
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
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { appLogin, getAppUserInfo } from '../api'

const router = useRouter()
const userStore = useUserStore()
const isMounted = ref(true)
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名或手机号', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
    { pattern: /^(?=.*[a-zA-Z])(?=.*\d)/, message: '密码需包含字母和数字', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  try {
    await formRef.value.validate()
  } catch (e) {
    ElMessage.warning('请填写完整的登录信息')
    return
  }
  
  loading.value = true
  try {
    const loginRes = await appLogin(form)
    if (!isMounted.value) return
    const { token } = loginRes.data
    
    localStorage.setItem('token', token)
    
    const infoRes = await getAppUserInfo()
    if (!isMounted.value) return
    userStore.login(token, infoRes.data)
    
    ElMessage.success('登录成功，欢迎回来')
    router.push('/')
  } catch (e) {
    console.error('登录失败', e)
    if (isMounted.value) {
      const errorMsg = e.response?.data?.message || '登录失败，请检查用户名和密码'
      ElMessage.error(errorMsg)
    }
  } finally {
    if (isMounted.value) {
      loading.value = false
    }
  }
}

onUnmounted(() => {
  isMounted.value = false
})
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FFF8F0 0%, #FFE4C4 100%);
  padding: 20px;
}

.login-container {
  display: flex;
  width: 1000px;
  max-width: 95vw;
  background: #fff;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(255, 140, 0, 0.15);
}

.login-left {
  width: 420px;
  background: linear-gradient(135deg, #FF8C00 0%, #FF6B00 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
    animation: pulse 15s ease-in-out infinite;
  }

  @keyframes pulse {
    0%, 100% { transform: scale(1); opacity: 0.5; }
    50% { transform: scale(1.1); opacity: 0.8; }
  }

  .login-brand {
    text-align: center;
    color: #fff;
    position: relative;
    z-index: 1;

    h1 {
      font-size: 2.5rem;
      margin: 20px 0 12px;
      font-weight: 600;
      letter-spacing: 2px;
    }

    p {
      font-size: 1.1rem;
      opacity: 0.95;
      letter-spacing: 4px;
    }
  }

  .login-features {
    margin-top: 50px;
    display: flex;
    flex-direction: column;
    gap: 20px;
    position: relative;
    z-index: 1;

    .feature-item {
      display: flex;
      align-items: center;
      gap: 12px;
      color: #fff;
      font-size: 1rem;
      opacity: 0.95;

      .el-icon {
        font-size: 24px;
        background: rgba(255,255,255,0.2);
        padding: 10px;
        border-radius: 12px;
      }
    }
  }
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 48px;

  .login-form-wrap {
    width: 100%;
    max-width: 380px;

    h2 {
      font-size: 1.8rem;
      color: #333;
      margin-bottom: 8px;
      font-weight: 600;
    }

    .login-tip {
      color: #999;
      margin-bottom: 40px;
      font-size: 0.95rem;
    }

    :deep(.el-input__wrapper) {
      padding: 8px 16px;
      border-radius: 12px;
      box-shadow: 0 0 0 1px #e8e8e8;

      &:hover, &:focus {
        box-shadow: 0 0 0 1px #FF8C00;
      }
    }

    :deep(.el-button--primary) {
      background: linear-gradient(135deg, #FF8C00 0%, #FF6B00 100%);
      border: none;
      margin-top: 10px;

      &:hover {
        background: linear-gradient(135deg, #FF6B00 0%, #FF8C00 100%);
      }
    }
  }
}

.login-footer {
  text-align: center;
  margin-top: 30px;
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

@media (max-width: 768px) {
  .login-left {
    display: none;
  }

  .login-right {
    padding: 40px 24px;

    .login-form-wrap {
      h2 {
        font-size: 1.6rem;
      }
    }
  }
}
</style>