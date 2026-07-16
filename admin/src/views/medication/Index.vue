<template>
  <div class="medication-page">
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span style="font-size:1.1rem;font-weight:600">用药计划管理</span>
          <div style="display:flex;gap:12px">
            <el-input v-model="query.elderlyName" placeholder="老人姓名" clearable style="width:150px" @clear="loadPlanData" @keyup.enter="loadPlanData" />
            <el-input v-model="query.medicineName" placeholder="药品名称" clearable style="width:150px" @clear="loadPlanData" @keyup.enter="loadPlanData" />
            <el-select v-model="query.status" placeholder="状态" clearable style="width:120px" @change="loadPlanData">
              <el-option label="执行中" :value="1" />
              <el-option label="已停用" :value="0" />
            </el-select>
            <el-button type="primary" @click="loadPlanData"><el-icon><Search /></el-icon> 查询</el-button>
            <el-button type="primary" @click="handleAddPlan"><el-icon><Plus /></el-icon> 新增计划</el-button>
          </div>
        </div>
      </template>

      <el-table :data="planTableData" stripe v-loading="planLoading">
        <el-table-column prop="elderlyName" label="老人姓名" width="100" />
        <el-table-column prop="medicineName" label="药品名称" width="150" />
        <el-table-column prop="dosage" label="剂量" width="100" />
        <el-table-column prop="frequency" label="频率" width="80" />
        <el-table-column prop="takeTimes" label="用药时间" width="150" />
        <el-table-column prop="startDate" label="开始日期" width="110" />
        <el-table-column prop="endDate" label="结束日期" width="110" />
        <el-table-column prop="doctorName" label="医生" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '执行中' : '已停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="handlePlanDetail(row)">详情</el-button>
            <el-button text type="warning" @click="handleEditPlan(row)">编辑</el-button>
            <el-button text :type="row.status === 1 ? 'danger' : 'success'" @click="togglePlanStatus(row)">
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:20px;display:flex;justify-content:flex-end">
        <el-pagination
          v-model:current-page="planQuery.pageNum"
          v-model:page-size="planQuery.pageSize"
          :total="planTotal"
          :page-sizes="[10,20,50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadPlanData"
          @current-change="loadPlanData"
        />
      </div>
    </el-card>

    <el-card style="margin-top:24px">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span style="font-size:1.1rem;font-weight:600">用药记录</span>
          <div style="display:flex;gap:12px">
            <el-select v-model="recordQuery.elderlyId" placeholder="选择老人" clearable style="width:150px" @change="loadRecordData">
              <el-option v-for="e in elderlyList" :key="e.id" :label="e.name" :value="e.id" />
            </el-select>
            <el-select v-model="recordQuery.status" placeholder="状态" clearable style="width:120px" @change="loadRecordData">
              <el-option label="未服用" :value="0" />
              <el-option label="已服用" :value="1" />
              <el-option label="漏服" :value="2" />
              <el-option label="拒服" :value="3" />
            </el-select>
            <el-button type="primary" @click="loadRecordData"><el-icon><Search /></el-icon> 查询</el-button>
            <el-button type="primary" @click="handleAddRecord"><el-icon><Plus /></el-icon> 新增记录</el-button>
          </div>
        </div>
      </template>

      <el-table :data="recordTableData" stripe v-loading="recordLoading">
        <el-table-column prop="elderlyName" label="老人姓名" width="100" />
        <el-table-column prop="medicineName" label="药品名称" width="150" />
        <el-table-column prop="dosage" label="服用剂量" width="100" />
        <el-table-column prop="takeTime" label="服用时间" width="170" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="['warning','success','danger','info'][row.status]">
              {{ ['未服用','已服用','漏服','拒服'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="recorderName" label="记录人" width="100" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button text type="warning" @click="handleEditRecord(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:20px;display:flex;justify-content:flex-end">
        <el-pagination
          v-model:current-page="recordQuery.pageNum"
          v-model:page-size="recordQuery.pageSize"
          :total="recordTotal"
          :page-sizes="[10,20,50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadRecordData"
          @current-change="loadRecordData"
        />
      </div>
    </el-card>

    <el-dialog v-model="planDialogVisible" :title="planForm.id ? '编辑用药计划' : '新增用药计划'" width="700px" destroy-on-close>
      <el-form ref="planFormRef" :model="planForm" :rules="planRules" label-width="110px">
        <el-form-item label="选择老人" prop="elderlyId">
          <el-select v-model="planForm.elderlyId" placeholder="请选择老人" style="width:100%" @change="onPlanElderlyChange">
            <el-option v-for="e in elderlyList" :key="e.id" :label="e.name" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="药品名称" prop="medicineName">
          <el-input v-model="planForm.medicineName" placeholder="请输入药品名称" />
        </el-form-item>
        <el-form-item label="剂量" prop="dosage">
          <el-input v-model="planForm.dosage" placeholder="请输入剂量" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用药频率">
              <el-select v-model="planForm.frequency" style="width:100%">
                <el-option label="每天" value="每天" />
                <el-option label="每周" value="每周" />
                <el-option label="每月" value="每月" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每天次数">
              <el-input-number v-model="planForm.timesPerDay" :min="1" :max="10" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="用药时间">
          <el-input v-model="planForm.takeTimes" placeholder="例如：08:00,12:00,18:00" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期">
              <el-date-picker v-model="planForm.startDate" type="date" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期">
              <el-date-picker v-model="planForm.endDate" type="date" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="医生姓名">
          <el-input v-model="planForm.doctorName" placeholder="请输入医生姓名" />
        </el-form-item>
        <el-form-item label="医嘱编号">
          <el-input v-model="planForm.prescriptionNo" placeholder="请输入医嘱编号" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="planForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="planDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="planSubmitLoading" @click="handlePlanSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="recordDialogVisible" :title="recordForm.id ? '编辑用药记录' : '新增用药记录'" width="600px" destroy-on-close>
      <el-form ref="recordFormRef" :model="recordForm" :rules="recordRules" label-width="100px">
        <el-form-item label="选择老人" prop="elderlyId">
          <el-select v-model="recordForm.elderlyId" placeholder="请选择老人" style="width:100%" @change="onRecordElderlyChange">
            <el-option v-for="e in elderlyList" :key="e.id" :label="e.name" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="药品名称" prop="medicineName">
          <el-input v-model="recordForm.medicineName" placeholder="请输入药品名称" />
        </el-form-item>
        <el-form-item label="服用剂量" prop="dosage">
          <el-input v-model="recordForm.dosage" placeholder="请输入服用剂量" />
        </el-form-item>
        <el-form-item label="服用时间">
          <el-datetime-picker v-model="recordForm.takeTime" type="datetime" style="width:100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="recordForm.status" style="width:100%">
            <el-option label="未服用" :value="0" />
            <el-option label="已服用" :value="1" />
            <el-option label="漏服" :value="2" />
            <el-option label="拒服" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="recordForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="recordDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="recordSubmitLoading" @click="handleRecordSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getMedicationPlanList, addMedicationPlan, updateMedicationPlan, deleteMedicationPlan,
  getMedicationRecordList, addMedicationRecord, updateMedicationRecord,
  getElderlyList
} from '../../api'

const planLoading = ref(false)
const recordLoading = ref(false)
const planSubmitLoading = ref(false)
const recordSubmitLoading = ref(false)
const planDialogVisible = ref(false)
const recordDialogVisible = ref(false)
const planTotal = ref(0)
const recordTotal = ref(0)
const planTableData = ref([])
const recordTableData = ref([])
const elderlyList = ref([])
const planFormRef = ref()
const recordFormRef = ref()

const planQuery = reactive({ pageNum: 1, pageSize: 10, elderlyName: '', medicineName: '', status: null })
const recordQuery = reactive({ pageNum: 1, pageSize: 10, elderlyId: null, status: null })

const planForm = reactive({
  id: null, elderlyId: null, elderlyName: '', medicineName: '', dosage: '',
  frequency: '每天', timesPerDay: 1, takeTimes: '', startDate: null, endDate: null,
  doctorId: null, doctorName: '', prescriptionNo: '', remark: '', status: 1
})

const recordForm = reactive({
  id: null, planId: null, elderlyId: null, elderlyName: '', medicineName: '',
  dosage: '', takeTime: null, status: 0, recorderId: null, recorderName: '', remark: ''
})

const planRules = {
  elderlyId: [{ required: true, message: '请选择老人', trigger: 'change' }],
  medicineName: [{ required: true, message: '请输入药品名称', trigger: 'blur' }],
  dosage: [{ required: true, message: '请输入剂量', trigger: 'blur' }]
}

const recordRules = {
  elderlyId: [{ required: true, message: '请选择老人', trigger: 'change' }],
  medicineName: [{ required: true, message: '请输入药品名称', trigger: 'blur' }],
  dosage: [{ required: true, message: '请输入服用剂量', trigger: 'blur' }]
}

const loadPlanData = async () => {
  planLoading.value = true
  try {
    const res = await getMedicationPlanList(planQuery)
    planTableData.value = res.data?.records || []
    planTotal.value = res.data?.total || 0
  } finally { planLoading.value = false }
}

const loadRecordData = async () => {
  recordLoading.value = true
  try {
    const res = await getMedicationRecordList(recordQuery)
    recordTableData.value = res.data?.records || []
    recordTotal.value = res.data?.total || 0
  } finally { recordLoading.value = false }
}

const loadElderly = async () => {
  const res = await getElderlyList({ pageNum: 1, pageSize: 999 })
  elderlyList.value = res.data?.records || []
}

const onPlanElderlyChange = (val) => {
  const e = elderlyList.value.find(item => item.id === val)
  planForm.elderlyName = e?.name || ''
}

const onRecordElderlyChange = (val) => {
  const e = elderlyList.value.find(item => item.id === val)
  recordForm.elderlyName = e?.name || ''
}

const handleAddPlan = () => {
  Object.assign(planForm, {
    id: null, elderlyId: null, elderlyName: '', medicineName: '', dosage: '',
    frequency: '每天', timesPerDay: 1, takeTimes: '', startDate: null, endDate: null,
    doctorId: null, doctorName: '', prescriptionNo: '', remark: '', status: 1
  })
  planDialogVisible.value = true
}

const handleEditPlan = (row) => {
  Object.assign(planForm, row)
  planDialogVisible.value = true
}

const handlePlanDetail = (row) => {
  Object.assign(planForm, row)
  planDialogVisible.value = true
}

const togglePlanStatus = async (row) => {
  await updateMedicationPlan(row.id, { status: row.status === 1 ? 0 : 1 })
  ElMessage.success(row.status === 1 ? '已停用' : '已启用')
  loadPlanData()
}

const handlePlanSubmit = async () => {
  await planFormRef.value.validate()
  planSubmitLoading.value = true
  try {
    if (planForm.id) {
      await updateMedicationPlan(planForm.id, planForm)
      ElMessage.success('更新成功')
    } else {
      await addMedicationPlan(planForm)
      ElMessage.success('添加成功')
    }
    planDialogVisible.value = false
    loadPlanData()
  } finally { planSubmitLoading.value = false }
}

const handleAddRecord = () => {
  Object.assign(recordForm, {
    id: null, planId: null, elderlyId: null, elderlyName: '', medicineName: '',
    dosage: '', takeTime: new Date(), status: 0, recorderId: null, recorderName: '', remark: ''
  })
  recordDialogVisible.value = true
}

const handleEditRecord = (row) => {
  Object.assign(recordForm, row)
  recordDialogVisible.value = true
}

const handleRecordSubmit = async () => {
  await recordFormRef.value.validate()
  recordSubmitLoading.value = true
  try {
    if (recordForm.id) {
      await updateMedicationRecord(recordForm.id, recordForm)
      ElMessage.success('更新成功')
    } else {
      await addMedicationRecord(recordForm)
      ElMessage.success('添加成功')
    }
    recordDialogVisible.value = false
    loadRecordData()
  } finally { recordSubmitLoading.value = false }
}

onMounted(() => { loadPlanData(); loadRecordData(); loadElderly() })
</script>