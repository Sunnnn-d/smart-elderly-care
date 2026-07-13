<template>
  <div class="care-plan-page">
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span style="font-size:1.1rem;font-weight:600">护理计划管理</span>
          <div style="display:flex;gap:12px">
            <el-select v-model="elderlyId" placeholder="选择老人" clearable style="width:180px" @change="loadData" />
            <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增计划</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="elderlyName" label="老人姓名" width="100" />
        <el-table-column prop="planName" label="计划名称" width="150" />
        <el-table-column prop="careType" label="护理类型" width="120">
          <template #default="{ row }">
            <el-tag effect="light">{{ row.careType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="nurseName" label="负责护理员" width="120" />
        <el-table-column prop="frequency" label="执行频率" width="100" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="['info','success','warning'][row.status]">{{ ['已停止','执行中','已完成'][row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button text type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:20px;display:flex;justify-content:flex-end">
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑护理计划' : '新增护理计划'" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="选择老人" prop="elderlyId">
          <el-select v-model="form.elderlyId" placeholder="请选择老人" style="width:100%" @change="onElderlyChange">
            <el-option v-for="e in elderlyList" :key="e.id" :label="e.name" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划名称" prop="planName">
          <el-input v-model="form.planName" placeholder="请输入计划名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="护理类型" prop="careType">
              <el-select v-model="form.careType" style="width:100%">
                <el-option label="日常护理" value="日常护理" />
                <el-option label="专项护理" value="专项护理" />
                <el-option label="康复护理" value="康复护理" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="执行频率">
              <el-select v-model="form.frequency" style="width:100%">
                <el-option label="每天" value="每天" />
                <el-option label="每周" value="每周" />
                <el-option label="每月" value="每月" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期">
              <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期">
              <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="护理内容">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="请输入护理内容" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">已停止</el-radio>
            <el-radio :value="1">执行中</el-radio>
            <el-radio :value="2">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCarePlanList, addCarePlan, updateCarePlan, deleteCarePlan, getElderlyList } from '../../api'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const elderlyId = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])
const elderlyList = ref([])
const formRef = ref()

const form = reactive({
  id: null, elderlyId: null, elderlyName: '', planName: '', careType: '日常护理',
  content: '', frequency: '每天', startDate: '', endDate: '', status: 1
})

const rules = {
  elderlyId: [{ required: true, message: '请选择老人', trigger: 'change' }],
  planName: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  careType: [{ required: true, message: '请选择护理类型', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCarePlanList({ pageNum: pageNum.value, pageSize: pageSize.value, elderlyId: elderlyId.value })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const loadElderly = async () => {
  const res = await getElderlyList({ pageNum: 1, pageSize: 999 })
  elderlyList.value = res.data?.records || []
}

const onElderlyChange = (val) => {
  const e = elderlyList.value.find(item => item.id === val)
  form.elderlyName = e?.name || ''
}

const handleAdd = () => {
  Object.assign(form, { id: null, elderlyId: null, elderlyName: '', planName: '', careType: '日常护理', content: '', frequency: '每天', startDate: '', endDate: '', status: 1 })
  isEdit.value = false; dialogVisible.value = true
}

const handleEdit = (row) => { Object.assign(form, { ...row }); isEdit.value = true; dialogVisible.value = true }

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) { await updateCarePlan(form); ElMessage.success('更新成功') }
    else { await addCarePlan(form); ElMessage.success('添加成功') }
    dialogVisible.value = false; loadData()
  } finally { submitLoading.value = false }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该护理计划吗？', '提示', { type: 'warning' })
    .then(async () => { await deleteCarePlan(row.id); ElMessage.success('删除成功'); loadData() }).catch(() => {})
}

onMounted(() => { loadData(); loadElderly() })
</script>
