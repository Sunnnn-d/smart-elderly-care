<template>
  <div class="admin-layout">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="admin-sidebar">
      <div class="sidebar-logo">
        <el-icon :size="28" color="#FF8C00"><Sunny /></el-icon>
        <span v-show="!isCollapse" class="logo-text">暖夕伴养老管理</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#FF8C00"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>首页概览</span>
        </el-menu-item>
        <el-menu-item index="/elderly">
          <el-icon><User /></el-icon>
          <span>老人档案</span>
        </el-menu-item>
        <el-menu-item index="/service">
          <el-icon><Goods /></el-icon>
          <span>服务项目</span>
        </el-menu-item>
        <el-menu-item index="/order">
          <el-icon><List /></el-icon>
          <span>服务订单</span>
        </el-menu-item>
        <el-menu-item index="/health">
          <el-icon><FirstAidKit /></el-icon>
          <span>健康监测</span>
        </el-menu-item>
        <el-menu-item index="/care-plan">
          <el-icon><Document /></el-icon>
          <span>护理计划</span>
        </el-menu-item>
        <el-menu-item index="/notice">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
        <el-menu-item index="/banner">
          <el-icon><Picture /></el-icon>
          <span>轮播图管理</span>
        </el-menu-item>
        <el-menu-item index="/sys-user">
          <el-icon><User /></el-icon>
          <span>系统用户</span>
        </el-menu-item>
        <el-menu-item index="/sys-role">
          <el-icon><Key /></el-icon>
          <span>角色管理</span>
        </el-menu-item>
        <el-menu-item index="/app-user">
          <el-icon><User /></el-icon>
          <span>客户端用户</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <div class="message-btn" @click="goToMessage">
            <el-badge :value="adminUnreadCount" :hidden="adminUnreadCount === 0">
              <el-button icon="Bell" circle />
            </el-badge>
          </div>
          <el-dropdown>
            <div class="user-info">
              <el-avatar :size="32" style="background:#FF8C00">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="username">{{ username }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="openUsernameDialog">
                  <el-icon><Edit /></el-icon> 修改用户名
                </el-dropdown-item>
                <el-dropdown-item @click="openAvatarDialog">
                  <el-icon><User /></el-icon> 修改头像
                </el-dropdown-item>
                <el-dropdown-item @click="openPasswordDialog">
                  <el-icon><Lock /></el-icon> 修改密码
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>

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
              <el-icon><User /></el-icon>
            </el-avatar>
          </div>
        </el-form-item>
        <el-form-item label="上传新头像">
          <div class="avatar-upload">
            <input type="file" id="admin-avatar-file" accept="image/*" style="display:none" @change="handleAvatarChange" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { changeUsername, uploadAvatar, changePassword, getAdminUnreadCount } from '../api'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)
const username = computed(() => localStorage.getItem('admin_username') || '管理员')
const adminUnreadCount = ref(0)

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

const openUsernameDialog = () => {
  usernameForm.currentUsername = username.value
  usernameForm.newUsername = ''
  usernameDialogVisible.value = true
}

const openAvatarDialog = () => {
  avatarForm.avatar = ''
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

const saveUsername = async () => {
  await usernameFormRef.value.validate()
  usernameLoading.value = true
  try {
    await changeUsername({ username: usernameForm.newUsername })
    localStorage.setItem('admin_username', usernameForm.newUsername)
    ElMessage.success('用户名修改成功')
    usernameDialogVisible.value = false
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '用户名修改失败')
  } finally {
    usernameLoading.value = false
  }
}

const triggerAvatarUpload = () => {
  document.getElementById('admin-avatar-file').click()
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
    const res = await uploadAvatar(formData)
    if (res.data) {
      ElMessage.success('头像修改成功')
      avatarDialogVisible.value = false
    }
  } catch (e) {
    ElMessage.error('头像修改失败')
  } finally {
    avatarLoading.value = false
  }
}

const savePassword = async () => {
  await passwordFormRef.value.validate()
  passwordLoading.value = true
  try {
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordDialogVisible.value = false
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '密码修改失败')
  } finally {
    passwordLoading.value = false
  }
}

const goToMessage = () => {
  router.push('/message')
}

const fetchAdminUnreadCount = async () => {
  try {
    const res = await getAdminUnreadCount()
    const count = res.data || 0
    adminUnreadCount.value = count > 99 ? 99 : count
  } catch (e) {
    console.error('获取管理端未读消息数失败', e)
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确定退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_username')
    router.push('/login')
  }).catch(() => {})
}

onMounted(() => {
  fetchAdminUnreadCount()
})
</script>

<style lang="scss" scoped>
.admin-layout {
  height: 100vh;
  display: flex;
}

.admin-sidebar {
  background: #304156;
  transition: width 0.3s;
  overflow-x: hidden;
  .sidebar-logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    border-bottom: 1px solid rgba(255,255,255,0.1);
    .logo-text {
      color: #fff;
      font-size: 1.1rem;
      font-weight: 600;
      white-space: nowrap;
    }
  }
  .el-menu { border-right: none; }
}

.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #eee;
  padding: 0 24px;
  height: 60px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;
    .collapse-btn {
      font-size: 1.3rem;
      cursor: pointer;
      color: #666;
      &:hover { color: #FF8C00; }
    }
  }
  .header-right {
    display: flex;
    align-items: center;
    gap: 20px;
    .message-btn {
      cursor: pointer;
    }
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      .username { font-size: 0.95rem; color: #333; }
    }
  }
}

.admin-main {
  background: #f5f7fa;
  padding: 24px;
  overflow-y: auto;
}

.avatar-preview {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .file-name {
    color: #666;
    font-size: 0.9rem;
  }
}
</style>
