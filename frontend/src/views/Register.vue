<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-left">
        <div class="register-brand">
          <el-icon :size="72" color="#FFFFFF"><Sunny /></el-icon>
          <h1>暖夕伴养老</h1>
          <p>温暖服务 从心开始</p>
        </div>
        <div class="register-benefits">
          <div class="benefit-item">
            <el-icon><Check /></el-icon>
            <span>专业护理团队</span>
          </div>
          <div class="benefit-item">
            <el-icon><Check /></el-icon>
            <span>贴心健康服务</span>
          </div>
          <div class="benefit-item">
            <el-icon><Check /></el-icon>
            <span>便捷预约流程</span>
          </div>
        </div>
      </div>
      <div class="register-right">
        <div class="register-form-wrap">
          <h2>用户注册</h2>
          <p class="register-tip">创建您的账号，开始使用暖夕伴养老服务</p>
          <el-form ref="formRef" :model="form" :rules="rules" size="large">
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="请输入用户名（3-20个字符）"
                prefix-icon="User"
                clearable
              />
            </el-form-item>
            <el-form-item prop="realName">
              <el-input
                v-model="form.realName"
                placeholder="请输入真实姓名（选填）"
                prefix-icon="UserFilled"
                clearable
              />
            </el-form-item>
            <el-form-item prop="phone">
              <el-input
                v-model="form.phone"
                placeholder="请输入手机号（选填）"
                prefix-icon="Phone"
                clearable
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码（6-20个字符）"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="form.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" round style="width:100%;height:48px;font-size:16px;" @click="handleRegister">
                注 册
              </el-button>
            </el-form-item>
          </el-form>
          <div class="register-footer">
            <span>已有账号？</span>
            <router-link to="/login">立即登录</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { appRegister } from '../api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback()
    return
  }
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phoneRegex.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
    { pattern: /^(?=.*[a-zA-Z])(?=.*\d)/, message: '密码需包含字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  phone: [
    { validator: validatePhone, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  try {
    await formRef.value.validate()
  } catch (e) {
    ElMessage.warning('请填写完整的注册信息')
    return
  }
  
  loading.value = true
  try {
    await appRegister(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    console.error('注册失败', e)
    const errorMsg = e.response?.data?.message || '注册失败，请稍后重试'
    ElMessage.error(errorMsg)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FFF8F0 0%, #FFE4C4 100%);
  padding: 20px;
}

.register-container {
  display: flex;
  width: 1000px;
  max-width: 95vw;
  background: #fff;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(255, 140, 0, 0.15);
}

.register-left {
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

  .register-brand {
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

  .register-benefits {
    margin-top: 50px;
    display: flex;
    flex-direction: column;
    gap: 20px;
    position: relative;
    z-index: 1;

    .benefit-item {
      display: flex;
      align-items: center;
      gap: 12px;
      color: #fff;
      font-size: 1rem;
      opacity: 0.95;

      .el-icon {
        font-size: 20px;
        background: rgba(255,255,255,0.2);
        padding: 8px;
        border-radius: 50%;
      }
    }
  }
}

.register-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 50px 48px;

  .register-form-wrap {
    width: 100%;
    max-width: 380px;

    h2 {
      font-size: 1.8rem;
      color: #333;
      margin-bottom: 8px;
      font-weight: 600;
    }

    .register-tip {
      color: #999;
      margin-bottom: 30px;
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

.register-footer {
  text-align: center;
  margin-top: 24px;
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
  .register-left {
    display: none;
  }

  .register-right {
    padding: 40px 24px;

    .register-form-wrap {
      h2 {
        font-size: 1.6rem;
      }
    }
  }
}
</style>
