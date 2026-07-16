<template>
  <div class="emergency-page">
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span style="font-size:1.1rem;font-weight:600">紧急呼叫管理</span>
          <div style="display:flex;gap:12px">
            <el-input v-model="query.elderlyName" placeholder="老人姓名" clearable style="width:150px" @clear="loadData" @keyup.enter="loadData" />
            <el-select v-model="query.status" placeholder="状态" clearable style="width:120px" @change="loadData">
              <el-option label="待响应" :value="0" />
              <el-option label="处理中" :value="1" />
              <el-option label="已完成" :value="2" />
              <el-option label="已取消" :value="3" />
            </el-select>
            <el-select v-model="query.callType" placeholder="呼叫类型" clearable style="width:130px" @change="loadData">
              <el-option label="紧急求助" value="emergency" />
              <el-option label="跌倒报警" value="fall" />
              <el-option label="健康异常" value="health" />
              <el-option label="其他" value="other" />
            </el-select>
            <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon> 查询</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="elderlyName" label="老人姓名" width="100" />
        <el-table-column prop="roomNumber" label="房间号" width="90" />
        <el-table-column prop="callTime" label="呼叫时间" width="170" />
        <el-table-column prop="callType" label="呼叫类型" width="100">
          <template #default="{ row }">
            <el-tag :type="callTypeTags[row.callType]?.type || 'info'">
              {{ callTypeTags[row.callType]?.label || row.callType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="['danger','warning','success','info'][row.status]">
              {{ ['待响应','处理中','已完成','已取消'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="responderName" label="响应人员" width="100">
          <template #default="{ row }">{{ row.responderName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="responseTime" label="响应时间" width="170">
          <template #default="{ row }">{{ row.responseTime || '-' }}</template>
        </el-table-column>
        <el-table-column prop="handleResult" label="处理结果" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 0" text type="warning" @click="handleRespond(row)">响应</el-button>
            <el-button v-if="row.status === 1" text type="success" @click="handleComplete(row)">完成</el-button>
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

    <el-dialog v-model="detailVisible" title="呼叫详情" width="550px">
      <el-descriptions :column="2" border v-if="currentCall">
        <el-descriptions-item label="老人姓名">{{ currentCall.elderlyName }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ currentCall.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="呼叫时间">{{ currentCall.callTime }}</el-descriptions-item>
        <el-descriptions-item label="呼叫类型">
          <el-tag :type="callTypeTags[currentCall.callType]?.type || 'info'">
            {{ callTypeTags[currentCall.callType]?.label || currentCall.callType }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="['danger','warning','success','info'][currentCall.status]">
            {{ ['待响应','处理中','已完成','已取消'][currentCall.status] }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="响应人员">{{ currentCall.responderName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="响应时间">{{ currentCall.responseTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2">{{ currentCall.handleResult || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="位置" :span="2">
          <span v-if="currentCall.locationLat">{{ currentCall.locationLat }}, {{ currentCall.locationLng }}</span>
          <span v-else>暂无</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="respondVisible" title="响应呼叫" width="450px">
      <el-form label-width="100px">
        <el-form-item label="老人姓名">{{ currentCall?.elderlyName }}</el-form-item>
        <el-form-item label="房间号">{{ currentCall?.roomNumber }}</el-form-item>
        <el-form-item label="呼叫时间">{{ currentCall?.callTime }}</el-form-item>
        <el-form-item label="响应人员">
          <el-input v-model="respondForm.responderName" placeholder="请输入响应人员姓名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="respondVisible = false">取消</el-button>
        <el-button type="primary" :loading="respondLoading" @click="submitRespond">确认响应</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="completeVisible" title="完成处理" width="450px">
      <el-form label-width="100px">
        <el-form-item label="处理结果">
          <el-input v-model="completeForm.handleResult" type="textarea" :rows="3" placeholder="请输入处理结果" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeVisible = false">取消</el-button>
        <el-button type="primary" :loading="completeLoading" @click="submitComplete">确认完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getEmergencyCallList, handleEmergencyCall, completeEmergencyCall } from '../../api'

const loading = ref(false)
const respondLoading = ref(false)
const completeLoading = ref(false)
const detailVisible = ref(false)
const respondVisible = ref(false)
const completeVisible = ref(false)
const total = ref(0)
const tableData = ref([])
const currentCall = ref(null)

const query = reactive({ pageNum: 1, pageSize: 10, elderlyName: '', status: null, callType: null })

const respondForm = reactive({ responderId: null, responderName: '' })
const completeForm = reactive({ handleResult: '' })

const callTypeTags = {
  emergency: { label: '紧急求助', type: 'danger' },
  fall: { label: '跌倒报警', type: 'danger' },
  health: { label: '健康异常', type: 'warning' },
  other: { label: '其他', type: 'info' }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getEmergencyCallList(query)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const handleDetail = (row) => { currentCall.value = row; detailVisible.value = true }

const handleRespond = (row) => {
  currentCall.value = row
  respondForm.responderName = ''
  respondVisible.value = true
}

const submitRespond = async () => {
  if (!respondForm.responderName) { ElMessage.warning('请输入响应人员姓名'); return }
  respondLoading.value = true
  try {
    await handleEmergencyCall(currentCall.value.id, 1, respondForm.responderName)
    ElMessage.success('响应成功')
    respondVisible.value = false
    loadData()
  } finally { respondLoading.value = false }
}

const handleComplete = (row) => {
  currentCall.value = row
  completeForm.handleResult = ''
  completeVisible.value = true
}

const submitComplete = async () => {
  if (!completeForm.handleResult) { ElMessage.warning('请输入处理结果'); return }
  completeLoading.value = true
  try {
    await completeEmergencyCall(currentCall.value.id, completeForm.handleResult)
    ElMessage.success('处理完成')
    completeVisible.value = false
    loadData()
  } finally { completeLoading.value = false }
}

onMounted(() => loadData())
</script>