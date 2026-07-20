<template>
  <div class="order-page">
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span style="font-size:1.1rem;font-weight:600">服务订单管理</span>
          <div style="display:flex;gap:12px">
            <el-input v-model="query.elderlyName" placeholder="老人姓名" clearable style="width:150px" @clear="loadData" @keyup.enter="loadData" />
            <el-input v-model="query.orderNo" placeholder="订单编号" clearable style="width:180px" @clear="loadData" @keyup.enter="loadData" />
            <el-select v-model="query.status" placeholder="订单状态" clearable style="width:130px" @change="loadData">
              <el-option label="待派单" :value="0" />
              <el-option label="服务中" :value="1" />
              <el-option label="已完成" :value="2" />
              <el-option label="已取消" :value="3" />
            </el-select>
            <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon> 查询</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" width="180" />
        <el-table-column prop="elderlyName" label="老人姓名" width="100" />
        <el-table-column prop="serviceName" label="服务项目" width="120" />
        <el-table-column prop="servicePrice" label="服务价格" width="100">
          <template #default="{ row }">¥{{ row.servicePrice }}</template>
        </el-table-column>
        <el-table-column prop="nurseName" label="护理员" width="100">
          <template #default="{ row }">{{ row.nurseName || '待派单' }}</template>
        </el-table-column>
        <el-table-column prop="appointmentTime" label="预约时间" width="170" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="['warning','','success','info'][row.status]">
              {{ ['待派单','服务中','已完成','已取消'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, row)">
              <el-button text type="primary">
                操作<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="detail">详情</el-dropdown-item>
                  <el-dropdown-item v-if="row.status === 0" command="dispatch">派单</el-dropdown-item>
                  <el-dropdown-item v-if="row.status === 1" command="complete">完成</el-dropdown-item>
                  <el-dropdown-item v-if="row.status < 2" command="cancel">取消</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:20px;display:flex;justify-content:flex-end">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10,20,50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="550px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="老人姓名">{{ currentOrder.elderlyName }}</el-descriptions-item>
        <el-descriptions-item label="服务项目">{{ currentOrder.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="服务价格">¥{{ currentOrder.servicePrice }}</el-descriptions-item>
        <el-descriptions-item label="护理员">{{ currentOrder.nurseName || '待派单' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ currentOrder.appointmentTime }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="['warning','','success','info'][currentOrder.status]">
            {{ ['待派单','服务中','已完成','已取消'][currentOrder.status] }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentOrder.cancelReason" label="取消原因" :span="2">
          {{ currentOrder.cancelReason }}
          <span v-if="currentOrder.cancelType" class="cancel-type">({{ getCancelTypeText(currentOrder.cancelType) }})</span>
        </el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ currentOrder.address }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 派单对话框 -->
    <el-dialog v-model="dispatchVisible" title="派单" width="450px">
      <el-form label-width="100px">
        <el-form-item label="订单编号">{{ currentOrder?.orderNo }}</el-form-item>
        <el-form-item label="选择护理员">
          <el-select v-model="dispatchForm.nurseId" placeholder="请选择护理员" style="width:100%" @change="onNurseChange">
            <el-option v-for="n in nurses" :key="n.id" :label="`${n.realName} (${n.phone || ''})`" :value="n.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dispatchVisible = false">取消</el-button>
        <el-button type="primary" :loading="dispatchLoading" @click="submitDispatch">确定派单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { getOrderList, dispatchOrder, completeOrder, cancelOrder, getNurseList } from '../../api'

const loading = ref(false)
const dispatchLoading = ref(false)
const detailVisible = ref(false)
const dispatchVisible = ref(false)
const total = ref(0)
const tableData = ref([])
const currentOrder = ref(null)
const nurses = ref([])

const query = reactive({ pageNum: 1, pageSize: 10, elderlyName: '', orderNo: '', status: null })
const dispatchForm = reactive({ orderId: null, nurseId: null, nurseName: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOrderList(query)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const handleDetail = (row) => { currentOrder.value = row; detailVisible.value = true }

const handleDispatch = async (row) => {
  currentOrder.value = row
  dispatchForm.orderId = row.id
  dispatchForm.nurseId = null
  dispatchForm.nurseName = ''
  const res = await getNurseList()
  nurses.value = res.data || []
  dispatchVisible.value = true
}

const onNurseChange = (val) => {
  const nurse = nurses.value.find(n => n.id === val)
  dispatchForm.nurseName = nurse?.realName || ''
}

const submitDispatch = async () => {
  if (!dispatchForm.nurseId) { ElMessage.warning('请选择护理员'); return }
  dispatchLoading.value = true
  try {
    await dispatchOrder(dispatchForm)
    ElMessage.success('派单成功')
    dispatchVisible.value = false
    loadData()
  } finally { dispatchLoading.value = false }
}

const handleComplete = (row) => {
  ElMessageBox.confirm('确认完成该订单？', '提示', { type: 'warning' })
    .then(async () => { await completeOrder(row.id); ElMessage.success('订单已完成'); loadData() }).catch(() => {})
}

const handleCancel = (row) => {
  ElMessageBox.prompt('请输入取消原因', '取消订单', { confirmButtonText: '确定', cancelButtonText: '取消' })
    .then(async ({ value }) => { await cancelOrder(row.id, value); ElMessage.success('订单已取消'); loadData() }).catch(() => {})
}

const getCancelTypeText = (cancelType) => {
  const texts = {
    manual: '用户自主取消',
    admin: '管理员取消',
    timeout: '超时自动取消'
  }
  return texts[cancelType] || '未知'
}

const handleCommand = (cmd, row) => {
  switch (cmd) {
    case 'detail': handleDetail(row); break
    case 'dispatch': handleDispatch(row); break
    case 'complete': handleComplete(row); break
    case 'cancel': handleCancel(row); break
  }
}

onMounted(() => loadData())
</script>
