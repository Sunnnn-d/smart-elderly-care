<template>
  <div class="bills-page">
    <div class="page-header">
      <h2>费用账单</h2>
    </div>

    <div class="bills-container" v-loading="loading">
      <template>
        <el-empty v-if="bills.length === 0" description="暂无账单" />
        <div v-for="bill in bills" :key="bill.id" class="bill-card">
        <div class="bill-header">
          <div class="bill-no">账单编号：{{ bill.billNo }}</div>
          <el-tag :type="getBillStatusTag(bill.status)">{{ getBillStatusText(bill.status) }}</el-tag>
        </div>
        <div class="bill-content">
          <div class="bill-month">
            <el-icon><Calendar /></el-icon>
            <span>{{ bill.billMonth }}月账单</span>
          </div>
          <div class="bill-amount">
            <div class="amount-item">
              <span class="label">总金额</span>
              <span class="value">¥{{ bill.totalAmount }}</span>
            </div>
            <div class="amount-item">
              <span class="label">已付金额</span>
              <span class="value paid">¥{{ bill.paidAmount }}</span>
            </div>
            <div class="amount-item">
              <span class="label">待付金额</span>
              <span class="value pending">¥{{ (bill.totalAmount - bill.paidAmount).toFixed(2) }}</span>
            </div>
          </div>
          <div v-if="bill.dueDate" class="bill-due">
            <el-icon><Clock /></el-icon>
            <span>到期日期：{{ bill.dueDate }}</span>
          </div>
          <div v-if="bill.remark" class="bill-remark">
            <el-icon><InfoFilled /></el-icon>
            <span>{{ bill.remark }}</span>
          </div>
        </div>
        <div class="bill-footer">
          <el-button 
            v-if="bill.status < 2" 
            type="primary" 
            round 
            @click="handlePay(bill)"
          >去支付</el-button>
          <el-button 
            v-if="bill.status === 2" 
            type="success" 
            round 
            disabled
          >已结清</el-button>
        </div>
      </div>
      </template>
    </div>

    <el-pagination
      v-if="bills.length > 0"
      :current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      @current-change="handlePageChange"
      layout="total, prev, pager, next, jumper"
    />

    <el-dialog v-model="payDialogVisible" title="支付账单" width="450px">
      <div v-if="currentBill">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="账单编号">{{ currentBill.billNo }}</el-descriptions-item>
          <el-descriptions-item label="账单月份">{{ currentBill.billMonth }}</el-descriptions-item>
          <el-descriptions-item label="总金额">¥{{ currentBill.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="已付金额">¥{{ currentBill.paidAmount }}</el-descriptions-item>
          <el-descriptions-item label="待付金额" :span="2">¥{{ (currentBill.totalAmount - currentBill.paidAmount).toFixed(2) }}</el-descriptions-item>
        </el-descriptions>
        <el-form label-width="100px" style="margin-top:20px">
          <el-form-item label="支付金额">
            <el-input-number 
              v-model="payForm.payAmount" 
              :min="0" 
              :max="currentBill.totalAmount - currentBill.paidAmount"
              :precision="2" 
              style="width:100%" 
            />
          </el-form-item>
          <el-form-item label="支付方式">
            <el-radio-group v-model="payForm.payMethod">
              <el-radio value="wechat">
                <el-icon color="#07C160"><ChatDotRound /></el-icon>
                微信支付
              </el-radio>
              <el-radio value="alipay">
                <el-icon color="#1677FF"><Wallet /></el-icon>
                支付宝
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="payLoading" @click="submitPay">确认支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFeeBillsByElderlyId, createPayment } from '../api'
import { useUserStore } from '../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, Clock, InfoFilled, ChatDotRound, Wallet } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const isMounted = ref(false)
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const bills = ref([])
const payDialogVisible = ref(false)
const payLoading = ref(false)
const currentBill = ref(null)

const payForm = ref({
  payAmount: 0,
  payMethod: 'wechat'
})

const getBillStatusText = (status) => {
  const texts = ['待支付', '部分支付', '已结清']
  return texts[status] || '未知'
}

const getBillStatusTag = (status) => {
  const tags = ['warning', '', 'success']
  return tags[status] || ''
}

const loadBills = async () => {
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
    const res = await getFeeBillsByElderlyId(userStore.userInfo.id)
    if (!isMounted.value) return
    bills.value = res.data || []
    total.value = bills.value.length
  } catch (e) {
    console.error('获取账单列表失败', e)
    ElMessage.error('获取账单列表失败')
  } finally {
    if (isMounted.value) {
      loading.value = false
    }
  }
}

const handlePageChange = (page) => {
  pageNum.value = page
  loadBills()
}

const handlePay = (bill) => {
  if (!userStore.isLogin) {
    ElMessageBox.confirm('您还未登录，需要登录后才能支付', '提示', {
      confirmButtonText: '去登录',
      cancelButtonText: '取消',
      type: 'info'
    }).then(() => router.push('/login')).catch(() => {})
    return
  }
  currentBill.value = bill
  payForm.value = {
    payAmount: bill.totalAmount - bill.paidAmount,
    payMethod: 'wechat'
  }
  payDialogVisible.value = true
}

const submitPay = async () => {
  if (payForm.value.payAmount <= 0) {
    ElMessage.warning('请输入支付金额')
    return
  }
  payLoading.value = true
  try {
    await createPayment({
      billId: currentBill.value.id,
      billNo: currentBill.value.billNo,
      elderlyId: userStore.userInfo?.id,
      elderlyName: userStore.userInfo?.realName || '',
      payAmount: payForm.value.payAmount,
      payMethod: payForm.value.payMethod,
      status: 2,
      payTime: new Date().toISOString()
    })
    ElMessage.success('支付成功')
    payDialogVisible.value = false
    loadBills()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '支付失败')
  } finally {
    payLoading.value = false
  }
}

onMounted(() => {
  isMounted.value = true
  loadBills()
})

onUnmounted(() => {
  isMounted.value = false
})
</script>

<style lang="scss" scoped>
.bills-page {
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

.bills-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.bill-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.bill-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
  
  .bill-no {
    font-size: 0.9rem;
    color: #666;
  }
}

.bill-content {
  padding: 20px;
}

.bill-month {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  
  :deep(.el-icon) {
    font-size: 18px;
    color: #FF8C00;
  }
}

.bill-amount {
  display: flex;
  gap: 30px;
  margin-bottom: 16px;
  
  .amount-item {
    display: flex;
    flex-direction: column;
    gap: 4px;
    
    .label {
      font-size: 0.85rem;
      color: #999;
    }
    
    .value {
      font-size: 1.3rem;
      font-weight: 700;
      color: #333;
      
      &.paid {
        color: #52C41A;
      }
      
      &.pending {
        color: #FF8C00;
      }
    }
  }
}

.bill-due {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 12px;
  
  :deep(.el-icon) {
    font-size: 14px;
    color: #999;
  }
}

.bill-remark {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.9rem;
  color: #999;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
  
  :deep(.el-icon) {
    font-size: 14px;
  }
}

.bill-footer {
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
  .bills-page {
    padding: 20px 16px;
  }
  
  .page-header {
    margin-bottom: 20px;
    
    h2 { font-size: 1.4rem; }
  }
  
  .bills-container {
    gap: 16px;
  }
  
  .bill-header {
    padding: 12px 16px;
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
    
    .bill-no { font-size: 0.85rem; }
  }
  
  .bill-content {
    padding: 16px;
  }
  
  .bill-month {
    font-size: 1rem !important;
    margin-bottom: 16px;
  }
  
  .bill-amount { 
    flex-direction: column; 
    gap: 12px; 
    margin-bottom: 12px;
  }
  
  .bill-due {
    font-size: 0.85rem;
    margin-bottom: 8px;
  }
  
  .bill-remark {
    font-size: 0.85rem;
    padding: 10px;
  }
  
  .bill-footer {
    padding: 12px 16px;
    justify-content: flex-start;
  }
  
  .el-pagination {
    margin-top: 20px;
  }
}
</style>