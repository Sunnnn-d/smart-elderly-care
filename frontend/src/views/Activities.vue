<template>
  <div class="activities-page">
    <div class="page-banner">
      <h1>活动中心</h1>
      <p>参与丰富多彩的活动，让生活更加精彩</p>
    </div>

    <div class="activities-container">
      <div v-loading="loading" class="activity-list">
        <template>
          <el-empty v-if="activities.length === 0" description="暂无活动" />
          <div v-for="activity in activities" :key="activity?.id" v-if="activity" class="activity-card">
          <div class="activity-header">
            <div class="activity-type">
              <el-tag :type="getActivityTypeTag(activity?.activityType)">
                {{ getActivityTypeText(activity?.activityType) }}
              </el-tag>
            </div>
            <el-tag :type="getActivityStatusTag(activity?.status)">
              {{ getActivityStatusText(activity?.status) }}
            </el-tag>
          </div>
          <h3>{{ activity?.activityName || '-' }}</h3>
          <p class="activity-desc">{{ activity?.description || '-' }}</p>
          <div class="activity-info">
            <div class="info-item">
              <el-icon><Calendar /></el-icon>
              <span>{{ activity?.startTime || '-' }} ~ {{ activity?.endTime || '-' }}</span>
            </div>
            <div class="info-item">
              <el-icon><Location /></el-icon>
              <span>{{ activity?.location || '-' }}</span>
            </div>
            <div class="info-item">
              <el-icon><User /></el-icon>
              <span>{{ activity?.currentParticipants || 0 }}/{{ activity?.maxParticipants || 0 }}人</span>
            </div>
            <div class="info-item">
              <el-icon><UserFilled /></el-icon>
              <span>组织者：{{ activity?.organizerName || '-' }}</span>
            </div>
          </div>
          <div class="activity-footer">
            <el-button 
              v-if="activity?.status === 1" 
              type="primary" 
              round 
              :disabled="hasSignedUp(activity?.id)"
              @click="handleSignup(activity)"
            >
              {{ hasSignedUp(activity?.id) ? '已报名' : '立即报名' }}
            </el-button>
            <el-button 
              v-if="activity?.status === 2" 
              type="success" 
              round 
              :disabled="hasSignedIn(activity?.id)"
              @click="handleSignIn(activity)"
            >
              {{ hasSignedIn(activity?.id) ? '已签到' : '立即签到' }}
            </el-button>
            <el-button 
              v-if="activity?.status === 3" 
              disabled 
              round
            >
              活动已结束
            </el-button>
            <el-button 
              v-if="activity?.status === 0" 
              disabled 
              round
            >
              未发布
            </el-button>
          </div>
        </div>
        </template>
      </div>
    </div>

    <el-dialog v-model="signupDialogVisible" title="确认报名" width="450px">
      <div v-if="currentActivity">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="活动名称">{{ currentActivity?.activityName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="活动类型">{{ getActivityTypeText(currentActivity?.activityType) }}</el-descriptions-item>
          <el-descriptions-item label="活动时间">{{ currentActivity?.startTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="活动地点">{{ currentActivity?.location || '-' }}</el-descriptions-item>
        </el-descriptions>
        <el-form label-width="100px" style="margin-top:20px">
          <el-form-item label="老人姓名">
            <el-input v-model="signupForm.elderlyName" placeholder="请输入老人姓名" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="signupDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="signupLoading" @click="submitSignup">确认报名</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAllActivities, signupActivity, getActivitySignupsByElderlyId } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, Location, User, UserFilled } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const isMounted = ref(false)
const loading = ref(false)
const activities = ref([])
const signups = ref([])
const signupDialogVisible = ref(false)
const signupLoading = ref(false)
const currentActivity = ref(null)

const signupForm = ref({ elderlyName: '' })

const getActivityTypeText = (type) => {
  const texts = { culture: '文化娱乐', sports: '体育健身', health: '健康讲座', volunteer: '志愿者活动', other: '其他' }
  return texts[type] || type
}

const getActivityTypeTag = (type) => {
  const tags = { culture: 'warning', sports: 'success', health: '', volunteer: 'primary', other: 'info' }
  return tags[type] || ''
}

const getActivityStatusText = (status) => {
  const texts = ['未发布', '报名中', '进行中', '已结束']
  return texts[status] || '未知'
}

const getActivityStatusTag = (status) => {
  const tags = ['info', 'success', 'warning', 'danger']
  return tags[status] || ''
}

const hasSignedUp = (activityId) => {
  return signups.value.some(s => s.activityId === activityId && s.status < 3)
}

const hasSignedIn = (activityId) => {
  return signups.value.some(s => s.activityId === activityId && s.status === 2)
}

const loadActivities = async () => {
  loading.value = true
  try {
    const res = await getAllActivities()
    if (!isMounted.value) return
    activities.value = (res.data || []).filter(Boolean)
  } catch (e) {
    console.error('获取活动列表失败', e)
  } finally {
    if (isMounted.value) {
      loading.value = false
    }
  }
}

const loadSignups = async () => {
  if (!userStore.isLogin) return
  try {
    const res = await getActivitySignupsByElderlyId(userStore.userInfo?.id)
    if (!isMounted.value) return
    signups.value = res.data || []
  } catch (e) {
    console.error('获取报名记录失败', e)
  }
}

const handleSignup = (activity) => {
  if (!userStore.isLogin) {
    ElMessageBox.confirm('您还未登录，需要登录后才能报名', '提示', {
      confirmButtonText: '去登录',
      cancelButtonText: '取消',
      type: 'info'
    }).then(() => router.push('/login')).catch(() => {})
    return
  }
  currentActivity.value = activity
  signupForm.value.elderlyName = ''
  signupDialogVisible.value = true
}

const submitSignup = async () => {
  if (!signupForm.value.elderlyName) {
    ElMessage.warning('请输入老人姓名')
    return
  }
  if (!currentActivity.value?.id) {
    ElMessage.error('活动信息异常')
    return
  }
  signupLoading.value = true
  try {
    await signupActivity(currentActivity.value.id, userStore.userInfo?.id, signupForm.value.elderlyName)
    if (!isMounted.value) return
    ElMessage.success('报名成功，等待审核')
    signupDialogVisible.value = false
    loadSignups()
  } catch (e) {
    if (isMounted.value) {
      ElMessage.error(e.response?.data?.message || '报名失败')
    }
  } finally {
    if (isMounted.value) {
      signupLoading.value = false
    }
  }
}

const handleSignIn = (activity) => {
  if (!userStore.isLogin) {
    ElMessageBox.confirm('您还未登录，需要登录后才能签到', '提示', {
      confirmButtonText: '去登录',
      cancelButtonText: '取消',
      type: 'info'
    }).then(() => router.push('/login')).catch(() => {})
    return
  }
  ElMessage.success('签到成功')
}

onMounted(() => {
  isMounted.value = true
  loadActivities()
  loadSignups()
})

onUnmounted(() => {
  isMounted.value = false
})
</script>

<style lang="scss" scoped>
.page-banner {
  background: linear-gradient(135deg, #52C41A, #73D13D);
  padding: 60px 24px;
  text-align: center;
  color: #fff;
  h1 { font-size: 2.2rem; font-weight: 700; margin-bottom: 12px; }
  p { font-size: 1.2rem; opacity: 0.9; }
}

.activities-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 24px;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.activity-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  padding: 28px;
  border: 2px solid transparent;
  transition: all 0.3s;
  
  &:hover {
    border-color: #52C41A;
    box-shadow: 0 4px 20px rgba(82, 196, 26, 0.15);
  }
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.activity-card h3 {
  font-size: 1.4rem;
  color: #333;
  margin: 0 0 12px 0;
}

.activity-desc {
  color: #888;
  font-size: 0.95rem;
  line-height: 1.6;
  margin-bottom: 20px;
}

.activity-info {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 20px;
  
  .info-item {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 0.9rem;
    color: #666;
    
    :deep(.el-icon) {
      font-size: 14px;
      color: #999;
    }
  }
}

.activity-footer {
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .activity-info { flex-direction: column; gap: 12px; }
}
</style>