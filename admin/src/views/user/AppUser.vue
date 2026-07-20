<template>
  <div class="app-user-page">
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span style="font-size:1.1rem;font-weight:600">客户端用户管理</span>
          <div style="display:flex;gap:12px">
            <el-input v-model="searchForm.username" placeholder="搜索用户名" clearable style="width:180px" @keyup.enter="loadData">
              <template #prefix><el-icon><Search /></el-icon></template>
            </el-input>
            <el-input v-model="searchForm.realName" placeholder="搜索真实姓名" clearable style="width:180px" @keyup.enter="loadData">
              <template #prefix><el-icon><User /></el-icon></template>
            </el-input>
            <el-input v-model="searchForm.phone" placeholder="搜索手机号" clearable style="width:150px" @keyup.enter="loadData">
              <template #prefix><el-icon><Phone /></el-icon></template>
            </el-input>
            <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增用户</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" stripe v-loading="loading" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.avatar" class="avatar">
              <User />
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">{{ row.gender === 1 ? '男' : row.gender === 0 ? '女' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="70" />
        <el-table-column prop="address" label="地址" show-overflow-tooltip />
        <el-table-column prop="emergencyContact" label="紧急联系人" width="110" />
        <el-table-column prop="emergencyPhone" label="紧急电话" width="130" />
        <el-table-column prop="healthStatus" label="健康状况" width="120" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="170" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, row)">
              <el-button text type="primary">
                操作<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">编辑</el-dropdown-item>
                  <el-dropdown-item command="changePassword">修改密码</el-dropdown-item>
                  <el-dropdown-item command="resetPassword">重置密码</el-dropdown-item>
                  <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:20px;display:flex;justify-content:space-between;align-items:center">
        <div>
          <el-button text type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
            <el-icon><Delete /></el-icon> 批量删除 ({{ selectedIds.length }})
          </el-button>
        </div>
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10,20,50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑客户端用户' : '新增客户端用户'" width="850px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="头像">
              <div style="display:flex;align-items:center;gap:20px">
                <el-avatar :size="80" :src="form.avatar" class="avatar">
                  <User />
                </el-avatar>
                <div>
                  <input type="file" id="app-avatar-upload" accept="image/*" style="display:none" @change="handleAvatarUpload" />
                  <el-button type="primary" @click="triggerAvatarUpload">
                    <el-icon><Upload /></el-icon> 上传头像
                  </el-button>
                  <el-button text v-if="form.avatar" @click="removeAvatar">移除头像</el-button>
                </div>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="性别">
              <el-radio-group v-model="form.gender">
                <el-radio :value="1">男</el-radio>
                <el-radio :value="0">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="年龄">
              <el-input-number v-model="form.age" :min="1" :max="150" style="width:100px" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="紧急联系人">
              <el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急电话">
              <el-input v-model="form.emergencyPhone" placeholder="请输入紧急联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="健康状况">
          <el-input v-model="form.healthStatus" placeholder="请输入健康状况" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="初始密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入初始密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="changePasswordVisible" title="修改密码" width="450px" destroy-on-close>
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入旧密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changePasswordVisible = false">取消</el-button>
        <el-button type="primary" :loading="passwordLoading" @click="submitChangePassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, User, Phone, Plus, Delete, Upload, ArrowDown } from '@element-plus/icons-vue'
import { getAppUserPage, addAppUser, updateAppUser, deleteAppUser, deleteAppUserBatch, resetAppUserPassword, uploadAppUserAvatar, changeAppUserPassword } from '../../api'

const loading = ref(false)
const submitLoading = ref(false)
const passwordLoading = ref(false)
const dialogVisible = ref(false)
const changePasswordVisible = ref(false)
const isEdit = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])
const selectedIds = ref([])
const formRef = ref()
const passwordFormRef = ref()
const currentUserId = ref(null)

const searchForm = reactive({
  username: '',
  realName: '',
  phone: ''
})

const form = reactive({
  id: null,
  username: '',
  realName: '',
  phone: '',
  email: '',
  avatar: '',
  gender: 1,
  age: null,
  address: '',
  emergencyContact: '',
  emergencyPhone: '',
  healthStatus: '',
  status: 1,
  password: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入初始密码', trigger: 'blur' }]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
      if (value !== passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }, trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAppUserPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      username: searchForm.username || undefined,
      realName: searchForm.realName || undefined,
      phone: searchForm.phone || undefined
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    username: '',
    realName: '',
    phone: '',
    email: '',
    avatar: '',
    gender: 1,
    age: null,
    address: '',
    emergencyContact: '',
    emergencyPhone: '',
    healthStatus: '',
    status: 1,
    password: ''
  })
}

const resetPasswordForm = () => {
  Object.assign(passwordForm, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  })
}

const handleCommand = (cmd, row) => {
  switch (cmd) {
    case 'edit':
      handleEdit(row)
      break
    case 'changePassword':
      handleChangePassword(row)
      break
    case 'resetPassword':
      handleResetPassword(row)
      break
    case 'delete':
      handleDelete(row)
      break
  }
}

const handleAdd = () => {
  resetForm()
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  Object.assign(form, { ...row })
  isEdit.value = true
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!isEdit.value) {
    await formRef.value.validate()
  }
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateAppUser(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await addAppUser(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除用户"${row.realName}"吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteAppUser(row.id)
      ElMessage.success('删除成功')
      loadData()
    }).catch(() => {})
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 个用户吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteAppUserBatch(selectedIds.value)
      ElMessage.success('批量删除成功')
      selectedIds.value = []
      loadData()
    }).catch(() => {})
}

const handleResetPassword = (row) => {
  ElMessageBox.confirm(`确定将用户"${row.realName}"的密码重置为"123456"吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await resetAppUserPassword(row.id)
      ElMessage.success('密码已重置为 123456')
    }).catch(() => {})
}

const handleChangePassword = (row) => {
  currentUserId.value = row.id
  resetPasswordForm()
  changePasswordVisible.value = true
}

const submitChangePassword = async () => {
  await passwordFormRef.value.validate()
  passwordLoading.value = true
  try {
    await changeAppUserPassword(currentUserId.value, {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    changePasswordVisible.value = false
  } finally { passwordLoading.value = false }
}

const handleSelectionChange = (val) => {
  selectedIds.value = val.map(item => item.id)
}

const triggerAvatarUpload = () => {
  document.getElementById('app-avatar-upload').click()
}

const handleAvatarUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  const formData = new FormData()
  formData.append('file', file)
  
  try {
    const res = await uploadAppUserAvatar(form.id, formData)
    form.avatar = res.data.avatar
    ElMessage.success('头像上传成功')
  } catch (error) {
    ElMessage.error('头像上传失败')
  }
  
  event.target.value = ''
}

const removeAvatar = () => {
  form.avatar = ''
}

onMounted(() => loadData())
</script>

<style scoped>
.avatar {
  border-radius: 50%;
}
</style>
