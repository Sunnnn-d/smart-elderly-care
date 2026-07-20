<template>
  <div id="app">
    <el-header class="site-header">
      <div class="header-content">
        <div class="header-left">
          <div class="logo" @click="$router.push('/')">
            <el-icon :size="32" color="#FF8C00"><Sunny /></el-icon>
            <span class="logo-text">暖夕伴养老</span>
          </div>
          <div class="mobile-menu-btn" @click="toggleMobileMenu">
            <el-icon :size="24" color="#333"><Menu /></el-icon>
          </div>
        </div>
        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          :ellipsis="false"
          class="nav-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            首页
          </el-menu-item>
          <el-menu-item index="/service" @click="checkLogin('/service')">
            <el-icon><Calendar /></el-icon>
            服务预约
          </el-menu-item>
          <el-menu-item index="/news">
            <el-icon><Document /></el-icon>
            健康资讯
          </el-menu-item>
          <el-menu-item index="/about">
            <el-icon><InfoFilled /></el-icon>
            关于我们
          </el-menu-item>
          <el-menu-item v-if="userStore.isLogin" index="/orders">
            <el-icon><ShoppingCart /></el-icon>
            我的订单
          </el-menu-item>
          <el-menu-item v-if="userStore.isLogin" index="/activities">
            <el-icon><Trophy /></el-icon>
            活动中心
          </el-menu-item>
          <el-menu-item v-if="userStore.isLogin" index="/medication">
            <el-icon><FirstAidKit /></el-icon>
            用药管理
          </el-menu-item>
          <el-menu-item v-if="userStore.isLogin" index="/bills">
            <el-icon><Wallet /></el-icon>
            费用账单
          </el-menu-item>
          <el-menu-item v-if="userStore.isLogin" index="/message">
            <el-icon><Bell /></el-icon>
            消息中心
            <el-badge :value="messageStore.appUnreadCount" :hidden="messageStore.appUnreadCount === 0" class="message-badge" />
          </el-menu-item>
        </el-menu>
        <div class="header-right">
          <div v-if="!userStore.isLogin" class="login-btn">
            <el-button type="primary" round @click="goLogin">
              <el-icon><User /></el-icon>
              登录
            </el-button>
          </div>
          <el-dropdown v-else class="user-dropdown" trigger="click">
            <div class="user-info">
              <el-avatar :size="40" :src="userStore.userInfo?.avatar" class="user-avatar">
                <User />
              </el-avatar>
              <span class="user-name">{{ userStore.userInfo?.username }}</span>
              <el-icon class="dropdown-icon"><ChevronDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="openUsernameDialog">
                  <el-icon><Edit /></el-icon>
                  修改用户名
                </el-dropdown-item>
                <el-dropdown-item @click="openAvatarDialog">
                  <el-icon><User /></el-icon>
                  修改头像
                </el-dropdown-item>
                <el-dropdown-item @click="openPasswordDialog">
                  <el-icon><Lock /></el-icon>
                  修改密码
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><Back /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    <div v-if="mobileMenuVisible" class="mobile-menu-overlay" @click="mobileMenuVisible = false"></div>
    <div v-if="mobileMenuVisible" class="mobile-menu">
      <div class="mobile-menu-header">
        <span class="mobile-menu-title">菜单</span>
        <el-icon class="mobile-menu-close" @click="mobileMenuVisible = false"><Close /></el-icon>
      </div>
      <el-menu
        :default-active="activeMenu"
        mode="vertical"
        class="mobile-menu-list"
        @select="handleMobileMenuSelect"
      >
        <el-menu-item index="/">
          <el-icon><HomeFilled /></el-icon>
          首页
        </el-menu-item>
        <el-menu-item index="/service">
          <el-icon><Calendar /></el-icon>
          服务预约
        </el-menu-item>
        <el-menu-item index="/news">
          <el-icon><Document /></el-icon>
          健康资讯
        </el-menu-item>
        <el-menu-item index="/about">
          <el-icon><InfoFilled /></el-icon>
          关于我们
        </el-menu-item>
        <el-menu-item v-if="userStore.isLogin" index="/orders">
          <el-icon><ShoppingCart /></el-icon>
          我的订单
        </el-menu-item>
        <el-menu-item v-if="userStore.isLogin" index="/activities">
          <el-icon><Trophy /></el-icon>
          活动中心
        </el-menu-item>
        <el-menu-item v-if="userStore.isLogin" index="/medication">
          <el-icon><FirstAidKit /></el-icon>
          用药管理
        </el-menu-item>
        <el-menu-item v-if="userStore.isLogin" index="/bills">
          <el-icon><Wallet /></el-icon>
          费用账单
        </el-menu-item>
        <el-menu-item v-if="userStore.isLogin" index="/message">
          <el-icon><Bell /></el-icon>
          消息中心
          <el-badge :value="messageStore.appUnreadCount" :hidden="messageStore.appUnreadCount === 0" />
        </el-menu-item>
      </el-menu>
      <div v-if="!userStore.isLogin" class="mobile-menu-login">
        <el-button type="primary" round @click="handleMobileLogin">
          <el-icon><User /></el-icon>
          登录
        </el-button>
      </div>
      <div v-else class="mobile-menu-user">
        <div class="mobile-user-info">
          <el-avatar :size="48" :src="userStore.userInfo?.avatar">
            <User />
          </el-avatar>
          <div class="mobile-user-detail">
            <span class="mobile-user-name">{{ userStore.userInfo?.username }}</span>
            <span class="mobile-user-logout" @click="handleLogout">退出登录</span>
          </div>
        </div>
      </div>
    </div>
    <main class="site-main">
      <router-view />
    </main>
    <el-footer class="site-footer">
      <div class="footer-content">
        <div class="footer-info">
          <h3>智慧养老服务热线</h3>
          <p class="hotline">400-888-9999</p>
          <p>服务时间：周一至周日 8:00-20:00</p>
          <p>地址：幸福路100号养老服务大厦</p>
        </div>
        <div class="footer-links">
          <h3>快速链接</h3>
          <p><a href="/service">服务预约</a></p>
          <p><a href="/news">健康资讯</a></p>
          <p><a href="/about">关于我们</a></p>
        </div>
        <div class="footer-qrcode">
          <h3>关注我们</h3>
          <div class="qrcode-placeholder">
            <el-icon :size="48" color="#999"><Cellphone /></el-icon>
            <p>扫码关注公众号</p>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p>Copyright 2026 智慧养老服务中心 All Rights Reserved</p>
      </div>
    </el-footer>

    <!-- 修改用户名弹窗 -->
    <el-dialog title="修改用户名" v-model="usernameDialogVisible" width="450px" :close-on-click-modal="false">
      <el-form ref="usernameFormRef" :model="usernameForm" :rules="usernameRules" label-width="100px">
        <el-form-item label="当前用户名">
          <el-input v-model="usernameForm.currentUsername" disabled />
        </el-form-item>
        <el-form-item label="新用户名" prop="newUsername">
          <el-input v-model="usernameForm.newUsername" placeholder="请输入新用户名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="usernameDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="usernameLoading" @click="saveUsername">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改头像弹窗 -->
    <el-dialog title="修改头像" v-model="avatarDialogVisible" width="450px" :close-on-click-modal="false">
      <el-form :model="avatarForm" label-width="80px">
        <el-form-item label="当前头像">
          <div class="avatar-preview">
            <el-avatar :size="120" :src="avatarForm.avatar" class="preview-avatar">
              <User />
            </el-avatar>
          </div>
        </el-form-item>
        <el-form-item label="上传新头像">
          <div class="avatar-upload">
            <input type="file" id="avatar-file" accept="image/*" style="display:none" @change="handleAvatarChange" />
            <el-button type="primary" @click="triggerAvatarUpload">
              <el-icon><Upload /></el-icon>
              选择图片
            </el-button>
            <span v-if="avatarForm.fileName" class="file-name">{{ avatarForm.fileName }}</span>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="avatarDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="avatarLoading" @click="saveAvatar">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码弹窗 -->
    <el-dialog title="修改密码" v-model="passwordDialogVisible" width="450px" :close-on-click-modal="false">
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入旧密码" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="passwordLoading" @click="savePassword">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from './stores/user'
import { useMessageStore } from './stores/message'
import { getAppUserInfo, uploadAppUserAvatar, changeAppUserPassword, changeAppUsername, uploadAppAvatar, changeAppPassword } from './api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ShoppingCart, Trophy, FirstAidKit, Wallet, Bell, Edit, Lock, Back, HomeFilled, Calendar, Document, InfoFilled, Menu, Close } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const messageStore = useMessageStore()
const isMounted = ref(false)
const mobileMenuVisible = ref(false)

const toggleMobileMenu = () => {
  mobileMenuVisible.value = !mobileMenuVisible.value
}

const handleMobileMenuSelect = (index) => {
  mobileMenuVisible.value = false
  if (index === '/service') {
    checkLogin('/service')
  } else {
    router.push(index)
  }
}

const handleMobileLogin = () => {
  mobileMenuVisible.value = false
  router.push('/login')
}

const openMessageCenter = () => {
  router.push('/message')
}

const goOrders = () => {
  router.push('/orders')
}

const goActivities = () => {
  router.push('/activities')
}

const goMedication = () => {
  router.push('/medication')
}

const goBills = () => {
  router.push('/bills')
}

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/service')) return '/service'
  if (path.startsWith('/news')) return '/news'
  if (path.startsWith('/about')) return '/about'
  if (path.startsWith('/orders')) return '/orders'
  if (path.startsWith('/activities')) return '/activities'
  if (path.startsWith('/medication')) return '/medication'
  if (path.startsWith('/bills')) return '/bills'
  if (path.startsWith('/message')) return '/message'
  return '/'
})

const usernameDialogVisible = ref(false)
const avatarDialogVisible = ref(false)
const passwordDialogVisible = ref(false)
const usernameLoading = ref(false)
const avatarLoading = ref(false)
const passwordLoading = ref(false)
const usernameFormRef = ref(null)
const passwordFormRef = ref(null)

const usernameForm = reactive({
  currentUsername: '',
  newUsername: ''
})

const avatarForm = reactive({
  avatar: '',
  fileName: '',
  file: null
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const usernameRules = {
  newUsername: [
    { required: true, message: '请输入新用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
      if (value !== passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ]
}

const handleMenuSelect = (index) => {
  if (index === '/service') {
    checkLogin('/service')
  } else {
    router.push(index)
  }
}

const checkLogin = (path) => {
  if (!userStore.isLogin) {
    ElMessageBox.confirm(
      '您还未登录，需要登录后才能使用此功能',
      '提示',
      {
        confirmButtonText: '去登录',
        cancelButtonText: '取消',
        type: 'info'
      }
    ).then(() => {
      router.push('/login')
    }).catch(() => {})
  } else {
    router.push(path)
  }
}

const goLogin = () => {
  router.push('/login')
}

const openUsernameDialog = () => {
  usernameForm.currentUsername = userStore.userInfo?.username || ''
  usernameForm.newUsername = ''
  usernameDialogVisible.value = true
}

const saveUsername = async () => {
  await usernameFormRef.value.validate()
  usernameLoading.value = true
  try {
    await changeAppUsername({ username: usernameForm.newUsername })
    if (!isMounted.value) return
    userStore.updateUserInfo({ username: usernameForm.newUsername })
    ElMessage.success('用户名修改成功')
    usernameDialogVisible.value = false
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '用户名修改失败')
  } finally {
    if (isMounted.value) {
      usernameLoading.value = false
    }
  }
}

const openAvatarDialog = () => {
  avatarForm.avatar = userStore.userInfo?.avatar || ''
  avatarForm.fileName = ''
  avatarForm.file = null
  avatarDialogVisible.value = true
}

const openPasswordDialog = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordDialogVisible.value = true
}

const triggerAvatarUpload = () => {
  document.getElementById('avatar-file').click()
}

const handleAvatarChange = (e) => {
  const file = e.target.files[0]
  if (file) {
    avatarForm.file = file
    avatarForm.fileName = file.name
    const reader = new FileReader()
    reader.onload = (event) => {
      avatarForm.avatar = event.target.result
    }
    reader.readAsDataURL(file)
  }
}

const saveAvatar = async () => {
  if (!avatarForm.file) {
    ElMessage.warning('请选择要上传的图片')
    return
  }
  avatarLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', avatarForm.file)
    const res = await uploadAppUserAvatar(userStore.userInfo.id, formData)
    if (!isMounted.value) return
    if (res.data) {
      userStore.updateUserInfo({ avatar: res.data.avatar })
      ElMessage.success('头像修改成功')
      avatarDialogVisible.value = false
    }
  } catch (e) {
    ElMessage.error('头像修改失败')
  } finally {
    if (isMounted.value) {
      avatarLoading.value = false
    }
  }
}

const savePassword = async () => {
  await passwordFormRef.value.validate()
  passwordLoading.value = true
  try {
    await changeAppUserPassword(userStore.userInfo.id, {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    if (!isMounted.value) return
    ElMessage.success('密码修改成功')
    passwordDialogVisible.value = false
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '密码修改失败')
  } finally {
    if (isMounted.value) {
      passwordLoading.value = false
    }
  }
}

const handleLogout = () => {
  ElMessageBox.confirm(
    '确定要退出登录吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/')
  }).catch(() => {})
}

const loadUserInfo = async () => {
  if (userStore.token && !userStore.userInfo) {
    try {
      const res = await getAppUserInfo()
      if (!isMounted.value) return
      userStore.updateUserInfo(res.data)
    } catch (e) {
      console.error('获取用户信息失败', e)
      if (isMounted.value) {
        userStore.logout()
      }
    }
  }
}

onMounted(() => {
  isMounted.value = true
  loadUserInfo()
  if (userStore.token) {
    messageStore.fetchAppUnreadCount()
  }
})

onUnmounted(() => {
  isMounted.value = false
})
</script>

<style lang="scss" scoped>
.site-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  height: 72px;
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  margin-right: 40px;
  .logo-text {
    font-size: 1.5rem;
    font-weight: 700;
    color: #FF8C00;
    width: 150px ;
  }
}

.nav-menu {
  flex: 1;
  border-bottom: none !important;
  .el-menu-item {
    font-size: 1.05rem;
    padding: 0 20px;
    height: 72px;
    line-height: 72px;
    &.is-active {
      color: #FF8C00 !important;
      border-bottom-color: #FF8C00 !important;
    }
    &:hover {
      color: #FF8C00 !important;
      background: #FFF8F0 !important;
    }
    .message-badge {
      margin-left: 4px;
    }
  }
}

.header-right {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.login-btn {
  .el-button {
    padding: 8px 24px;
    font-size: 1rem;
  }
}

.user-dropdown {
  display: flex;
  align-items: center;
  
  .el-dropdown__caret-button {
    display: none;
  }
  
  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 12px;
    cursor: pointer;
    border-radius: 20px;
    transition: background 0.3s;
    
    &:hover {
      background: #FFF8F0;
    }
  }
  
  .user-avatar {
    border: 2px solid #FF8C00;
  }
  
  .user-name {
    font-size: 0.95rem;
    font-weight: 500;
    color: #333;
    max-width: 80px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  
  .dropdown-icon {
    font-size: 16px;
    color: #999;
  }
}

.site-main {
  min-height: calc(100vh - 72px - 280px);
}

.site-footer {
  background: #2C2C2C;
  color: #ccc;
  height: auto;
  padding: 40px 24px 0;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 40px;
  padding-bottom: 30px;
  border-bottom: 1px solid #444;
}

.footer-info, .footer-links, .footer-qrcode {
  h3 {
    color: #fff;
    font-size: 1.1rem;
    margin-bottom: 16px;
  }
  p {
    margin-bottom: 8px;
    font-size: 0.95rem;
  }
  a {
    color: #ccc;
    &:hover { color: #FF8C00; }
  }
}

.hotline {
  font-size: 1.8rem !important;
  font-weight: 700;
  color: #FF8C00 !important;
}

.qrcode-placeholder {
  text-align: center;
  padding: 16px;
  background: #3a3a3a;
  border-radius: 8px;
  width: 120px;
  p {
    font-size: 0.85rem;
    margin-top: 8px;
    margin-bottom: 0;
  }
}

.footer-bottom {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px 0;
  text-align: center;
  font-size: 0.85rem;
  color: #888;
}

.avatar-preview {
  display: flex;
  justify-content: center;
}

.preview-avatar {
  margin-bottom: 16px;
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 12px;
}

.file-name {
  font-size: 0.9rem;
  color: #666;
}

.mobile-menu-btn {
  display: none;
  cursor: pointer;
  padding: 8px;
}

@media (min-width: 769px) {
  .mobile-menu-btn {
    display: none !important;
  }
}

.mobile-menu-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 999;
}

.mobile-menu {
  position: fixed;
  top: 0;
  right: 0;
  width: 260px;
  height: 100vh;
  background: #fff;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  box-shadow: -4px 0 12px rgba(0, 0, 0, 0.1);
  animation: slideInRight 0.3s ease;
}

@keyframes slideInRight {
  from {
    transform: translateX(100%);
  }
  to {
    transform: translateX(0);
  }
}

.mobile-menu-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  background: #FFF8F0;
}

.mobile-menu-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #FF8C00;
}

.mobile-menu-close {
  font-size: 20px;
  cursor: pointer;
  color: #666;
  padding: 4px;
}

.mobile-menu-list {
  flex: 1;
  padding: 12px 0;
  border-bottom: none !important;
  
  .el-menu-item {
    font-size: 1rem;
    height: 50px;
    line-height: 50px;
    padding: 0 20px !important;
    border-bottom: 1px solid #f5f5f5;
    
    &.is-active {
      color: #FF8C00 !important;
      background: #FFF8F0 !important;
    }
    
    &:hover {
      background: #FFF8F0 !important;
      color: #FF8C00 !important;
    }
  }
}

.mobile-menu-login {
  padding: 20px;
  text-align: center;
  border-top: 1px solid #f0f0f0;
  
  .el-button {
    width: 100%;
    padding: 12px 0;
    font-size: 1rem;
  }
}

.mobile-menu-user {
  padding: 20px;
  border-top: 1px solid #f0f0f0;
  background: #FFF8F0;
}

.mobile-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.mobile-user-detail {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.mobile-user-name {
  font-size: 1rem;
  font-weight: 500;
  color: #333;
}

.mobile-user-logout {
  font-size: 0.85rem;
  color: #FF8C00;
  cursor: pointer;
  
  &:hover {
    text-decoration: underline;
  }
}

@media (max-width: 768px) {
  .site-header {
    height: 56px;
    padding: 0;
  }
  
  .header-content { 
    padding: 0 12px; 
    height: 56px;
    display: flex !important;
    align-items: center !important;
    justify-content: space-between !important;
  }
  
  .header-left {
    display: flex !important;
    align-items: center !important;
    gap: 8px !important;
  }
  
  .logo { 
    margin-right: 0 !important; 
    flex-shrink: 0 !important;
    gap: 4px !important;
    min-width: 0 !important;
    flex: 0 0 auto !important;
    
    .el-icon {
      font-size: 22px !important;
    }
    
    .logo-text { 
      font-size: 0.85rem !important; 
      font-weight: 600 !important;
      color: #FF8C00 !important;
      white-space: nowrap !important;
      overflow: hidden !important;
      text-overflow: ellipsis !important;
      max-width: 100px !important;
      display: block !important;
    }
  }
  
  .nav-menu {
    display: none !important;
  }
  
  .header-right {
    display: flex !important;
    align-items: center !important;
    gap: 0 !important;
    flex-shrink: 0 !important;
  }
  
  .login-btn {
    display: none !important;
  }
  
  .user-dropdown {
    display: flex !important;
    align-items: center !important;
  }
  
  .user-info {
    padding: 4px 8px !important;
    gap: 6px !important;
  }
  
  .user-avatar {
    width: 32px !important;
    height: 32px !important;
    border-width: 1px !important;
  }
  
  .user-name { 
    display: block !important;
    font-size: 0.85rem !important;
    max-width: 60px !important;
    overflow: hidden !important;
    text-overflow: ellipsis !important;
    white-space: nowrap !important;
  }
  
  .dropdown-icon {
    display: none !important;
  }
  
  .mobile-menu-btn {
    display: flex !important;
    align-items: center !important;
    flex-shrink: 0 !important;
    padding: 6px !important;
  }
  
  .footer-content { 
    grid-template-columns: 1fr; 
    gap: 24px; 
  }
  
  .site-main {
    min-height: calc(100vh - 56px - 280px);
  }
}
</style>