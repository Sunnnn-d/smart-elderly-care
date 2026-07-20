<template>
  <div class="orders-page">
    <div class="page-header">
      <h2>我的订单</h2>
      <div class="filter-tabs">
        <el-radio-group v-model="statusFilter" @change="loadOrders">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button :label="0">待派单</el-radio-button>
          <el-radio-button :label="1">服务中</el-radio-button>
          <el-radio-button :label="2">已完成</el-radio-button>
          <el-radio-button :label="3">已取消</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <div class="order-list" v-loading="loading">
      <el-empty v-if="filteredOrders.length === 0" description="暂无订单" />
      <div v-for="order in filteredOrders" :key="order.id" class="order-card">
        <div class="order-header">
          <div class="order-no">订单编号：{{ order.orderNo }}</div>
          <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
        </div>
        <div class="order-content">
          <div class="service-info">
            <div class="service-icon">
              <el-icon :size="48" color="#FF8C00"><ShoppingCart /></el-icon>
            </div>
            <div class="service-detail">
              <h3>{{ order.serviceName }}</h3>
              <p class="price">¥{{ order.servicePrice }}</p>
            </div>
          </div>
          <div class="order-meta">
            <div class="meta-item">
              <el-icon><User /></el-icon>
              <span>{{ order.elderlyName }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Phone /></el-icon>
              <span>{{ order.contactPhone }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Calendar /></el-icon>
              <span>{{ order.appointmentTime }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Location /></el-icon>
              <span>{{ order.address }}</span>
            </div>
          </div>
          <div v-if="order.nurseName" class="nurse-info">
            <el-icon><UserFilled /></el-icon>
            <span>服务人员：{{ order.nurseName }}</span>
          </div>
          <div v-if="order.cancelReason" class="cancel-info">
            <el-icon><WarningFilled /></el-icon>
            <span>取消原因：{{ order.cancelReason }}</span>
            <span class="cancel-type">({{ getCancelTypeText(order.cancelType) }})</span>
          </div>
        </div>
        <div class="order-actions">
          <el-button text @click="viewDetail(order)">查看详情</el-button>
          <el-button 
            v-if="order.status === 0" 
            text 
            type="danger" 
            @click="handleCancel(order)"
          >取消订单</el-button>
        </div>
      </div>
    </div>

    <el-pagination
      v-if="total > 0"
      :current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      @current-change="handlePageChange"
      layout="total, prev, pager, next, jumper"
    />

    <!-- 订单详情弹窗 -->
    <el-dialog title="订单详情" v-model="detailVisible" width="500px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="服务项目">{{ currentOrder.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="服务价格">¥{{ currentOrder.servicePrice }}</el-descriptions-item>
        <el-descriptions-item label="老人姓名">{{ currentOrder.elderlyName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ currentOrder.appointmentTime }}</el-descriptions-item>
        <el-descriptions-item label="服务人员">{{ currentOrder.nurseName || '待派单' }}</el-descriptions-item>
        <el-descriptions-item label="服务地址" :span="2">{{ currentOrder.address }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '无' }}</el-descriptions-item>
        <el-descriptions-item v-if="currentOrder.cancelReason" label="取消原因" :span="2">
          {{ currentOrder.cancelReason }} ({{ getCancelTypeText(currentOrder.cancelType) }})
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { getAppUserOrders, userCancelOrder } from '../api'
import { useUserStore } from '../stores/user'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ShoppingCart, User, Phone, Calendar, Location, UserFilled, WarningFilled } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const isMounted = ref(false)
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const orders = ref([])
const statusFilter = ref('')
const detailVisible = ref(false)
const currentOrder = ref(null)

const filteredOrders = computed(() => {
  if (!statusFilter.value) return orders.value
  return orders.value.filter(o => o.status === Number(statusFilter.value))
})

const loadOrders = async () => {
  if (!userStore.isLogin) {
    router.push('/login')
    return
  }
  loading.value = true
  try {
    const res = await getAppUserOrders({ pageNum: pageNum.value, pageSize: pageSize.value })
    if (!isMounted.value) return
    orders.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('获取订单列表失败', e)
    ElMessage.error('获取订单列表失败')
  } finally {
    if (isMounted.value) {
      loading.value = false
    }
  }
}

const handlePageChange = (page) => {
  pageNum.value = page
  loadOrders()
}

const viewDetail = (order) => {
  currentOrder.value = order
  detailVisible.value = true
}

const handleCancel = (order) => {
  ElMessageBox.prompt('请输入取消原因', '取消订单', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async ({ value }) => {
    try {
      await userCancelOrder(order.id, value)
      ElMessage.success('订单已取消')
      loadOrders()
    } catch (e) {
      ElMessage.error(e.response?.data?.message || '取消失败')
    }
  }).catch(() => {})
}

const getStatusText = (status) => {
  const texts = ['待派单', '服务中', '已完成', '已取消']
  return texts[status] || '未知'
}

const getStatusType = (status) => {
  const types = ['warning', '', 'success', 'info']
  return types[status] || ''
}

const getCancelTypeText = (cancelType) => {
  const texts = {
    manual: '用户自主取消',
    admin: '管理员取消',
    timeout: '超时自动取消'
  }
  return texts[cancelType] || '未知'
}

onMounted(() => {
  isMounted.value = true
  loadOrders()
})

onUnmounted(() => {
  isMounted.value = false
})
</script>

<style lang="scss" scoped>
.orders-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 30px 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  
  h2 {
    font-size: 1.5rem;
    color: #304156;
    margin: 0;
  }
}

.filter-tabs {
  .el-radio-button {
    margin-left: 8px;
  }
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  
  .order-no {
    font-size: 0.9rem;
    color: #666;
  }
}

.order-content {
  padding: 20px;
}

.service-info {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  
  .service-icon {
    width: 60px;
    height: 60px;
    background: #FFF8F0;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }
  
  .service-detail {
    h3 {
      font-size: 1.2rem;
      color: #333;
      margin: 0 0 8px 0;
    }
    
    .price {
      font-size: 1.3rem;
      font-weight: 700;
      color: #FF8C00;
    }
  }
}

.order-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
  
  .meta-item {
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

.nurse-info {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px;
  background: #f6ffed;
  border-radius: 8px;
  font-size: 0.9rem;
  color: #52c41a;
  margin-bottom: 12px;
  
  :deep(.el-icon) {
    font-size: 14px;
  }
}

.cancel-info {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px;
  background: #fff7e6;
  border-radius: 8px;
  font-size: 0.9rem;
  color: #fa8c16;
  
  :deep(.el-icon) {
    font-size: 14px;
  }
  
  .cancel-type {
    font-size: 0.8rem;
    opacity: 0.7;
  }
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
}

.el-pagination {
  margin-top: 30px;
  text-align: center;
}

@media (max-width: 768px) {
  .orders-page {
    padding: 20px 16px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
    margin-bottom: 20px;
    
    h2 {
      font-size: 1.4rem;
    }
  }
  
  .order-list {
    gap: 16px;
  }
  
  .order-header {
    padding: 12px 16px;
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
    
    .order-no {
      font-size: 0.85rem;
    }
  }
  
  .order-content {
    padding: 16px;
  }
  
  .service-info {
    margin-bottom: 16px;
    
    .service-icon {
      width: 50px;
      height: 50px;
    }
    
    .service-detail h3 { font-size: 1.1rem !important; }
    .service-detail .price { font-size: 1.2rem !important; }
  }
  
  .order-meta {
    gap: 12px;
    flex-direction: column;
    
    .meta-item {
      font-size: 0.85rem;
    }
  }
  
  .nurse-info, .cancel-info {
    font-size: 0.85rem;
    padding: 10px;
  }
  
  .order-actions {
    padding: 12px 16px;
    justify-content: flex-start;
  }
  
  .el-pagination {
    margin-top: 20px;
  }
}
</style>
