<template>
  <div class="medication-page">
    <div class="page-header">
      <h2>用药管理</h2>
    </div>

    <div class="medication-container" v-loading="loading">
      <el-card class="plan-card">
        <template #header>
          <span style="font-weight:600">用药计划</span>
        </template>
        <template>
          <el-empty v-if="plans.length === 0" description="暂无用药计划" />
          <div v-for="plan in plans" :key="plan.id" class="plan-item">
          <div class="plan-header">
            <div class="medicine-name">{{ plan.medicineName }}</div>
            <el-tag :type="plan.status === 1 ? 'success' : 'info'">{{ plan.status === 1 ? '执行中' : '已停用' }}</el-tag>
          </div>
          <div class="plan-detail">
            <div class="detail-item">
              <el-icon><FirstAidKit /></el-icon>
              <span>剂量：{{ plan.dosage }}</span>
            </div>
            <div class="detail-item">
              <el-icon><Clock /></el-icon>
              <span>{{ plan.frequency }}，{{ plan.timesPerDay }}次/天</span>
            </div>
            <div class="detail-item">
              <el-icon><Calendar /></el-icon>
              <span>时间：{{ plan.takeTimes }}</span>
            </div>
            <div class="detail-item">
              <el-icon><UserFilled /></el-icon>
              <span>医生：{{ plan.doctorName || '无' }}</span>
            </div>
            <div class="detail-item">
              <el-icon><Medal /></el-icon>
              <span>{{ plan.startDate }} ~ {{ plan.endDate }}</span>
            </div>
          </div>
          <div v-if="plan.remark" class="plan-remark">
            <el-icon><InfoFilled /></el-icon>
            <span>{{ plan.remark }}</span>
          </div>
        </div>
        </template>
      </el-card>

      <el-card class="record-card" style="margin-top:24px">
        <template #header>
          <span style="font-weight:600">用药记录</span>
        </template>
        <template>
          <el-empty v-if="records.length === 0" description="暂无用药记录" />
        <el-table :data="records" stripe size="small">
          <el-table-column prop="medicineName" label="药品名称" width="150" />
          <el-table-column prop="dosage" label="服用剂量" width="100" />
          <el-table-column prop="takeTime" label="服用时间" width="170" />
          <el-table-column prop="status" label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="['warning','success','danger','info'][row.status]">
                {{ ['未服用','已服用','漏服','拒服'][row.status] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="recorderName" label="记录人" width="100">
            <template #default="{ row }">{{ row.recorderName || '-' }}</template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        </el-table>
        </template>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMedicationPlansByElderlyId, getMedicationRecordsByElderlyId } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { FirstAidKit, Clock, Calendar, UserFilled, Medal, InfoFilled } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const isMounted = ref(false)
const loading = ref(false)
const plans = ref([])
const records = ref([])

const loadData = async () => {
  if (!userStore.isLogin) {
    router.push('/login')
    return
  }
  if (!userStore.userInfo?.id) {
    console.warn('用户信息未加载完成')
    ElMessage.warning('用户信息加载中，请稍候')
    return
  }
  loading.value = true
  try {
    const [plansRes, recordsRes] = await Promise.all([
      getMedicationPlansByElderlyId(userStore.userInfo.id),
      getMedicationRecordsByElderlyId(userStore.userInfo.id)
    ])
    if (!isMounted.value) return
    plans.value = plansRes.data || []
    records.value = recordsRes.data || []
  } catch (e) {
    console.error('获取用药数据失败', e)
    ElMessage.error('获取用药数据失败')
  } finally {
    if (isMounted.value) {
      loading.value = false
    }
  }
}

onMounted(() => {
  isMounted.value = true
  loadData()
})

onUnmounted(() => {
  isMounted.value = false
})
</script>

<style lang="scss" scoped>
.medication-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 30px 24px;
}

.page-header {
  margin-bottom: 30px;
  
  h2 {
    font-size: 1.5rem;
    color: #304156;
    margin: 0;
  }
}

.medication-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.plan-item {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  
  .medicine-name {
    font-size: 1.2rem;
    font-weight: 600;
    color: #333;
  }
}

.plan-detail {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  
  .detail-item {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 0.9rem;
    color: #666;
    
    :deep(.el-icon) {
      font-size: 14px;
      color: #FF8C00;
    }
  }
}

.plan-remark {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px dashed #eee;
  font-size: 0.9rem;
  color: #999;
  
  :deep(.el-icon) {
    font-size: 14px;
  }
}

@media (max-width: 768px) {
  .medication-page {
    padding: 20px 16px;
  }
  
  .page-header {
    margin-bottom: 20px;
    
    h2 { font-size: 1.4rem; }
  }
  
  .medication-container {
    gap: 16px;
  }
  
  .plan-item {
    padding: 16px;
    margin-bottom: 12px;
  }
  
  .plan-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
    margin-bottom: 12px;
    
    .medicine-name { font-size: 1.1rem !important; }
  }
  
  .plan-detail { 
    flex-direction: column; 
    gap: 10px; 
  }
  
  .plan-remark {
    font-size: 0.85rem;
    padding-top: 12px;
    margin-top: 12px;
  }
  
  :deep(.el-card__header) {
    padding: 16px !important;
  }
  
  :deep(.el-card__body) {
    padding: 16px !important;
  }
}
</style>