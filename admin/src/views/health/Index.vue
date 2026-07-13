<template>
  <div class="health-page">
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span style="font-size:1.1rem;font-weight:600">健康监测记录</span>
          <div style="display:flex;gap:12px">
            <el-select v-model="elderlyId" placeholder="选择老人" clearable style="width:180px" @change="loadData">
              <el-option v-for="e in elderlyList" :key="e.id" :label="e.name" :value="e.id" />
            </el-select>
            <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增记录</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="elderlyName" label="老人姓名" width="100" />
        <el-table-column prop="recordTime" label="记录时间" width="170" />
        <el-table-column prop="heartRate" label="心率" width="80">
          <template #default="{ row }">{{ row.heartRate }}次/分</template>
        </el-table-column>
        <el-table-column prop="bloodPressureHigh" label="血压" width="120">
          <template #default="{ row }">{{ row.bloodPressureHigh }}/{{ row.bloodPressureLow }}mmHg</template>
        </el-table-column>
        <el-table-column prop="bloodSugar" label="血糖" width="90">
          <template #default="{ row }">{{ row.bloodSugar }}mmol/L</template>
        </el-table-column>
        <el-table-column prop="temperature" label="体温" width="80">
          <template #default="{ row }">{{ row.temperature }}°C</template>
        </el-table-column>
        <el-table-column prop="weight" label="体重" width="80">
          <template #default="{ row }">{{ row.weight }}kg</template>
        </el-table-column>
        <el-table-column prop="oxygenSaturation" label="血氧" width="80">
          <template #default="{ row }">{{ row.oxygenSaturation }}%</template>
        </el-table-column>
        <el-table-column prop="mood" label="情绪" width="80">
          <template #default="{ row }">
            <el-tag :type="['','success','warning','danger'][row.mood]">
              {{ ['','良好','一般','低落'][row.mood] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
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

    <el-dialog v-model="dialogVisible" title="新增健康记录" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="选择老人" prop="elderlyId">
          <el-select v-model="form.elderlyId" placeholder="请选择老人" style="width:100%" @change="onElderlyChange">
            <el-option v-for="e in elderlyList" :key="e.id" :label="e.name" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="心率">
              <el-input-number v-model="form.heartRate" :min="40" :max="200" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体温">
              <el-input-number v-model="form.temperature" :min="35" :max="42" :precision="1" :step="0.1" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="收缩压">
              <el-input-number v-model="form.bloodPressureHigh" :min="60" :max="250" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="舒张压">
              <el-input-number v-model="form.bloodPressureLow" :min="40" :max="150" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="血糖">
              <el-input-number v-model="form.bloodSugar" :min="0" :max="30" :precision="1" :step="0.1" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体重">
              <el-input-number v-model="form.weight" :min="20" :max="200" :precision="1" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="血氧饱和度">
              <el-input-number v-model="form.oxygenSaturation" :min="70" :max="100" :precision="1" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="情绪状态">
              <el-select v-model="form.mood" style="width:100%">
                <el-option label="良好" :value="1" />
                <el-option label="一般" :value="2" />
                <el-option label="低落" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
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
import { ElMessage } from 'element-plus'
import { getHealthList, addHealthRecord, getElderlyList } from '../../api'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const elderlyId = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])
const elderlyList = ref([])
const formRef = ref()

const form = reactive({
  elderlyId: null, elderlyName: '', heartRate: 72, bloodPressureHigh: 120,
  bloodPressureLow: 80, bloodSugar: 5.6, temperature: 36.5, weight: 60,
  oxygenSaturation: 98, mood: 1, remark: '', recordTime: null,
  recorderId: null, recorderName: ''
})

const rules = { elderlyId: [{ required: true, message: '请选择老人', trigger: 'change' }] }

const loadData = async () => {
  loading.value = true
  try {
    const res = await getHealthList({ pageNum: pageNum.value, pageSize: pageSize.value, elderlyId: elderlyId.value })
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
  Object.assign(form, {
    elderlyId: null, elderlyName: '', heartRate: 72, bloodPressureHigh: 120,
    bloodPressureLow: 80, bloodSugar: 5.6, temperature: 36.5, weight: 60,
    oxygenSaturation: 98, mood: 1, remark: '', recordTime: null
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (!form.recordTime) form.recordTime = new Date().toISOString()
    await addHealthRecord(form)
    ElMessage.success('添加成功')
    dialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

onMounted(() => { loadData(); loadElderly() })
</script>
