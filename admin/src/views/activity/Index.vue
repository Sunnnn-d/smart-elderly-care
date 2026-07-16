<template>
  <div class="activity-page">
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span style="font-size:1.1rem;font-weight:600">活动管理</span>
          <div style="display:flex;gap:12px">
            <el-input v-model="query.activityName" placeholder="活动名称" clearable style="width:150px" @clear="loadActivityData" @keyup.enter="loadActivityData" />
            <el-select v-model="query.activityType" placeholder="活动类型" clearable style="width:130px" @change="loadActivityData">
              <el-option label="文化娱乐" value="culture" />
              <el-option label="体育健身" value="sports" />
              <el-option label="健康讲座" value="health" />
              <el-option label="志愿者活动" value="volunteer" />
              <el-option label="其他" value="other" />
            </el-select>
            <el-select v-model="query.status" placeholder="状态" clearable style="width:120px" @change="loadActivityData">
              <el-option label="未发布" :value="0" />
              <el-option label="报名中" :value="1" />
              <el-option label="进行中" :value="2" />
              <el-option label="已结束" :value="3" />
            </el-select>
            <el-button type="primary" @click="loadActivityData"><el-icon><Search /></el-icon> 查询</el-button>
            <el-button type="primary" @click="handleAddActivity"><el-icon><Plus /></el-icon> 新增活动</el-button>
          </div>
        </div>
      </template>

      <el-table :data="activityTableData" stripe v-loading="activityLoading">
        <el-table-column prop="activityName" label="活动名称" width="150" />
        <el-table-column prop="activityType" label="活动类型" width="100">
          <template #default="{ row }">{{ activityTypeLabels[row.activityType] || row.activityType }}</template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="170" />
        <el-table-column prop="endTime" label="结束时间" width="170" />
        <el-table-column prop="location" label="活动地点" width="120" />
        <el-table-column prop="currentParticipants" label="报名人数" width="100">
          <template #default="{ row }">{{ row.currentParticipants }}/{{ row.maxParticipants }}</template>
        </el-table-column>
        <el-table-column prop="organizerName" label="组织者" width="100" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="['info','success','warning','danger'][row.status]">
              {{ ['未发布','报名中','进行中','已结束'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleActivityDetail(row)">详情</el-button>
            <el-button text type="warning" @click="handleEditActivity(row)">编辑</el-button>
            <el-button v-if="row.status === 0" text type="success" @click="handlePublish(row)">发布</el-button>
            <el-button v-if="row.status === 1 || row.status === 2" text type="danger" @click="handleClose(row)">结束</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:20px;display:flex;justify-content:flex-end">
        <el-pagination
          v-model:current-page="activityQuery.pageNum"
          v-model:page-size="activityQuery.pageSize"
          :total="activityTotal"
          :page-sizes="[10,20,50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadActivityData"
          @current-change="loadActivityData"
        />
      </div>
    </el-card>

    <el-card style="margin-top:24px">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span style="font-size:1.1rem;font-weight:600">活动报名管理</span>
          <div style="display:flex;gap:12px">
            <el-select v-model="signupQuery.activityId" placeholder="选择活动" clearable style="width:180px" @change="loadSignupData">
              <el-option v-for="a in activityList" :key="a.id" :label="a.activityName" :value="a.id" />
            </el-select>
            <el-select v-model="signupQuery.status" placeholder="状态" clearable style="width:120px" @change="loadSignupData">
              <el-option label="待审核" :value="0" />
              <el-option label="已通过" :value="1" />
              <el-option label="已签到" :value="2" />
              <el-option label="已取消" :value="3" />
            </el-select>
            <el-button type="primary" @click="loadSignupData"><el-icon><Search /></el-icon> 查询</el-button>
          </div>
        </div>
      </template>

      <el-table :data="signupTableData" stripe v-loading="signupLoading">
        <el-table-column prop="activityName" label="活动名称" width="150" />
        <el-table-column prop="elderlyName" label="老人姓名" width="100" />
        <el-table-column prop="signupTime" label="报名时间" width="170" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="['warning','success','','info'][row.status]">
              {{ ['待审核','已通过','已签到','已取消'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="signInTime" label="签到时间" width="170">
          <template #default="{ row }">{{ row.signInTime || '-' }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" text type="success" @click="handleApprove(row)">审核通过</el-button>
            <el-button v-if="row.status === 1" text type="primary" @click="handleSignIn(row)">签到</el-button>
            <el-button v-if="row.status < 2" text type="danger" @click="handleCancelSignup(row)">取消报名</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:20px;display:flex;justify-content:flex-end">
        <el-pagination
          v-model:current-page="signupQuery.pageNum"
          v-model:page-size="signupQuery.pageSize"
          :total="signupTotal"
          :page-sizes="[10,20,50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadSignupData"
          @current-change="loadSignupData"
        />
      </div>
    </el-card>

    <el-dialog v-model="activityDialogVisible" :title="activityForm.id ? '编辑活动' : '新增活动'" width="650px" destroy-on-close>
      <el-form ref="activityFormRef" :model="activityForm" :rules="activityRules" label-width="100px">
        <el-form-item label="活动名称" prop="activityName">
          <el-input v-model="activityForm.activityName" placeholder="请输入活动名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="活动类型">
              <el-select v-model="activityForm.activityType" style="width:100%">
                <el-option label="文化娱乐" value="culture" />
                <el-option label="体育健身" value="sports" />
                <el-option label="健康讲座" value="health" />
                <el-option label="志愿者活动" value="volunteer" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动地点">
              <el-input v-model="activityForm.location" placeholder="请输入活动地点" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-date-picker v-model="activityForm.startTime" type="datetime" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间">
              <el-date-picker v-model="activityForm.endTime" type="datetime" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="最大参与人数">
              <el-input-number v-model="activityForm.maxParticipants" :min="1" :max="500" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="组织者">
              <el-input v-model="activityForm.organizerName" placeholder="请输入组织者姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="活动描述">
          <el-input v-model="activityForm.description" type="textarea" :rows="3" placeholder="请输入活动描述" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="activityForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="activityDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="activitySubmitLoading" @click="handleActivitySubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getActivityList, addActivity, updateActivity, deleteActivity, publishActivity, closeActivity, getAllActivities,
  getActivitySignupList, approveActivitySignup, signInActivity, cancelActivitySignup
} from '../../api'

const activityLoading = ref(false)
const signupLoading = ref(false)
const activitySubmitLoading = ref(false)
const activityDialogVisible = ref(false)
const activityTotal = ref(0)
const signupTotal = ref(0)
const activityTableData = ref([])
const signupTableData = ref([])
const activityList = ref([])
const activityFormRef = ref()

const activityQuery = reactive({ pageNum: 1, pageSize: 10, activityName: '', activityType: null, status: null })
const signupQuery = reactive({ pageNum: 1, pageSize: 10, activityId: null, status: null })

const activityForm = reactive({
  id: null, activityName: '', activityType: 'culture', description: '',
  startTime: null, endTime: null, location: '', maxParticipants: 20,
  currentParticipants: 0, coverImage: '', status: 0,
  organizerId: null, organizerName: '', remark: ''
})

const activityRules = { activityName: [{ required: true, message: '请输入活动名称', trigger: 'blur' }] }

const activityTypeLabels = {
  culture: '文化娱乐', sports: '体育健身', health: '健康讲座',
  volunteer: '志愿者活动', other: '其他'
}

const loadActivityData = async () => {
  activityLoading.value = true
  try {
    const res = await getActivityList(activityQuery)
    activityTableData.value = res.data?.records || []
    activityTotal.value = res.data?.total || 0
  } finally { activityLoading.value = false }
}

const loadSignupData = async () => {
  signupLoading.value = true
  try {
    const res = await getActivitySignupList(signupQuery)
    signupTableData.value = res.data?.records || []
    signupTotal.value = res.data?.total || 0
  } finally { signupLoading.value = false }
}

const loadAllActivities = async () => {
  const res = await getAllActivities()
  activityList.value = res.data || []
}

const handleAddActivity = () => {
  Object.assign(activityForm, { id: null, activityName: '', activityType: 'culture', description: '', startTime: null, endTime: null, location: '', maxParticipants: 20, currentParticipants: 0, coverImage: '', status: 0, organizerId: null, organizerName: '', remark: '' })
  activityDialogVisible.value = true
}

const handleEditActivity = (row) => {
  Object.assign(activityForm, row)
  activityDialogVisible.value = true
}

const handleActivityDetail = (row) => {
  Object.assign(activityForm, row)
  activityDialogVisible.value = true
}

const handlePublish = async (row) => {
  await publishActivity(row.id)
  ElMessage.success('活动已发布')
  loadActivityData()
}

const handleClose = async (row) => {
  await closeActivity(row.id)
  ElMessage.success('活动已结束')
  loadActivityData()
}

const handleActivitySubmit = async () => {
  await activityFormRef.value.validate()
  activitySubmitLoading.value = true
  try {
    if (activityForm.id) {
      await updateActivity(activityForm.id, activityForm)
      ElMessage.success('更新成功')
    } else {
      await addActivity(activityForm)
      ElMessage.success('添加成功')
    }
    activityDialogVisible.value = false
    loadActivityData()
    loadAllActivities()
  } finally { activitySubmitLoading.value = false }
}

const handleApprove = async (row) => {
  await approveActivitySignup(row.id)
  ElMessage.success('审核通过')
  loadSignupData()
}

const handleSignIn = async (row) => {
  await signInActivity(row.id)
  ElMessage.success('签到成功')
  loadSignupData()
}

const handleCancelSignup = async (row) => {
  await ElMessageBox.confirm('确认取消报名？', '提示', { type: 'warning' })
  await cancelActivitySignup(row.id)
  ElMessage.success('已取消报名')
  loadSignupData()
}

onMounted(() => { loadActivityData(); loadSignupData(); loadAllActivities() })
</script>