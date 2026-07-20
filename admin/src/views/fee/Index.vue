<template>
  <div class="fee-page">
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span style="font-size:1.1rem;font-weight:600">费用项目管理</span>
          <div style="display:flex;gap:12px">
            <el-input v-model="itemQuery.itemName" placeholder="费用名称" clearable style="width:150px" @clear="loadItemData" @keyup.enter="loadItemData" />
            <el-select v-model="itemQuery.feeType" placeholder="计费方式" clearable style="width:130px" @change="loadItemData">
              <el-option label="按月" value="monthly" />
              <el-option label="按天" value="daily" />
              <el-option label="按次" value="per_time" />
            </el-select>
            <el-select v-model="itemQuery.status" placeholder="状态" clearable style="width:100px" @change="loadItemData">
              <el-option label="启用" :value="1" />
              <el-option label="停用" :value="0" />
            </el-select>
            <el-button type="primary" @click="loadItemData"><el-icon><Search /></el-icon> 查询</el-button>
            <el-button type="primary" @click="handleAddItem"><el-icon><Plus /></el-icon> 新增项目</el-button>
          </div>
        </div>
      </template>

      <el-table :data="itemTableData" stripe v-loading="itemLoading">
        <el-table-column prop="itemName" label="费用名称" width="150" />
        <el-table-column prop="feeType" label="计费方式" width="100">
          <template #default="{ row }">{{ feeTypeLabels[row.feeType] || row.feeType }}</template>
        </el-table-column>
        <el-table-column prop="price" label="单价" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
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
                  <el-dropdown-item command="edit">编辑</el-dropdown-item>
                  <el-dropdown-item command="toggleStatus">{{ row.status === 1 ? '停用' : '启用' }}</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:20px;display:flex;justify-content:flex-end">
        <el-pagination
          v-model:current-page="itemQuery.pageNum"
          v-model:page-size="itemQuery.pageSize"
          :total="itemTotal"
          :page-sizes="[10,20,50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadItemData"
          @current-change="loadItemData"
        />
      </div>
    </el-card>

    <el-card style="margin-top:24px">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span style="font-size:1.1rem;font-weight:600">费用账单管理</span>
          <div style="display:flex;gap:12px">
            <el-input v-model="billQuery.elderlyName" placeholder="老人姓名" clearable style="width:150px" @clear="loadBillData" @keyup.enter="loadBillData" />
            <el-input v-model="billQuery.billNo" placeholder="账单编号" clearable style="width:180px" @clear="loadBillData" @keyup.enter="loadBillData" />
            <el-select v-model="billQuery.status" placeholder="状态" clearable style="width:120px" @change="loadBillData">
              <el-option label="待支付" :value="0" />
              <el-option label="部分支付" :value="1" />
              <el-option label="已结清" :value="2" />
            </el-select>
            <el-button type="primary" @click="loadBillData"><el-icon><Search /></el-icon> 查询</el-button>
            <el-button type="primary" @click="handleAddBill"><el-icon><Plus /></el-icon> 新增账单</el-button>
          </div>
        </div>
      </template>

      <el-table :data="billTableData" stripe v-loading="billLoading">
        <el-table-column prop="billNo" label="账单编号" width="150" />
        <el-table-column prop="elderlyName" label="老人姓名" width="100" />
        <el-table-column prop="billMonth" label="账单月份" width="100" />
        <el-table-column prop="totalAmount" label="总金额" width="100">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="paidAmount" label="已付金额" width="100">
          <template #default="{ row }">¥{{ row.paidAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="['warning','','success'][row.status]">
              {{ ['待支付','部分支付','已结清'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="到期日期" width="110" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleBillDetail(row)">详情</el-button>
            <el-button text type="warning" @click="handleEditBill(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:20px;display:flex;justify-content:flex-end">
        <el-pagination
          v-model:current-page="billQuery.pageNum"
          v-model:page-size="billQuery.pageSize"
          :total="billTotal"
          :page-sizes="[10,20,50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadBillData"
          @current-change="loadBillData"
        />
      </div>
    </el-card>

    <el-dialog v-model="itemDialogVisible" :title="itemForm.id ? '编辑费用项目' : '新增费用项目'" width="550px" destroy-on-close>
      <el-form ref="itemFormRef" :model="itemForm" :rules="itemRules" label-width="100px">
        <el-form-item label="费用名称" prop="itemName">
          <el-input v-model="itemForm.itemName" placeholder="请输入费用名称" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计费方式">
              <el-select v-model="itemForm.feeType" style="width:100%">
                <el-option label="按月" value="monthly" />
                <el-option label="按天" value="daily" />
                <el-option label="按次" value="per_time" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单价">
              <el-input-number v-model="itemForm.price" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="计价单位">
          <el-input v-model="itemForm.unit" placeholder="例如：月、天、次" />
        </el-form-item>
        <el-form-item label="费用描述">
          <el-input v-model="itemForm.description" type="textarea" :rows="2" placeholder="请输入费用描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="itemSubmitLoading" @click="handleItemSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="billDialogVisible" :title="billForm.id ? '编辑费用账单' : '新增费用账单'" width="600px" destroy-on-close>
      <el-form ref="billFormRef" :model="billForm" :rules="billRules" label-width="100px">
        <el-form-item label="选择老人" prop="elderlyId">
          <el-select v-model="billForm.elderlyId" placeholder="请选择老人" style="width:100%" @change="onBillElderlyChange">
            <el-option v-for="e in elderlyList" :key="e.id" :label="e.name" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="账单月份">
              <el-date-picker v-model="billForm.billMonth" type="month" format="YYYY-MM" value-format="YYYY-MM" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总金额">
              <el-input-number v-model="billForm.totalAmount" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="到期日期">
          <el-date-picker v-model="billForm.dueDate" type="date" style="width:100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="billForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="billDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="billSubmitLoading" @click="handleBillSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import {
  getFeeItemList, addFeeItem, updateFeeItem,
  getFeeBillList, addFeeBill, updateFeeBill,
  getElderlyList
} from '../../api'

const itemLoading = ref(false)
const billLoading = ref(false)
const itemSubmitLoading = ref(false)
const billSubmitLoading = ref(false)
const itemDialogVisible = ref(false)
const billDialogVisible = ref(false)
const itemTotal = ref(0)
const billTotal = ref(0)
const itemTableData = ref([])
const billTableData = ref([])
const elderlyList = ref([])
const itemFormRef = ref()
const billFormRef = ref()

const itemQuery = reactive({ pageNum: 1, pageSize: 10, itemName: '', feeType: null, status: null })
const billQuery = reactive({ pageNum: 1, pageSize: 10, elderlyName: '', billNo: '', status: null })

const itemForm = reactive({
  id: null, itemName: '', feeType: 'monthly', price: 0,
  unit: '月', description: '', status: 1, sortOrder: 0
})

const billForm = reactive({
  id: null, billNo: '', elderlyId: null, elderlyName: '',
  billMonth: '', totalAmount: 0, paidAmount: 0, status: 0,
  dueDate: null, remark: ''
})

const feeTypeLabels = { monthly: '按月', daily: '按天', per_time: '按次' }

const itemRules = { itemName: [{ required: true, message: '请输入费用名称', trigger: 'blur' }] }
const billRules = { elderlyId: [{ required: true, message: '请选择老人', trigger: 'change' }] }

const loadItemData = async () => {
  itemLoading.value = true
  try {
    const res = await getFeeItemList(itemQuery)
    itemTableData.value = res.data?.records || []
    itemTotal.value = res.data?.total || 0
  } finally { itemLoading.value = false }
}

const loadBillData = async () => {
  billLoading.value = true
  try {
    const res = await getFeeBillList(billQuery)
    billTableData.value = res.data?.records || []
    billTotal.value = res.data?.total || 0
  } finally { billLoading.value = false }
}

const loadElderly = async () => {
  const res = await getElderlyList({ pageNum: 1, pageSize: 999 })
  elderlyList.value = res.data?.records || []
}

const onBillElderlyChange = (val) => {
  const e = elderlyList.value.find(item => item.id === val)
  billForm.elderlyName = e?.name || ''
}

const handleAddItem = () => {
  Object.assign(itemForm, { id: null, itemName: '', feeType: 'monthly', price: 0, unit: '月', description: '', status: 1, sortOrder: 0 })
  itemDialogVisible.value = true
}

const handleEditItem = (row) => {
  Object.assign(itemForm, row)
  itemDialogVisible.value = true
}

const handleItemDetail = (row) => {
  Object.assign(itemForm, row)
  itemDialogVisible.value = true
}

const handleCommand = (cmd, row) => {
  const actions = {
    detail: handleItemDetail,
    edit: handleEditItem,
    toggleStatus: toggleItemStatus
  }
  actions[cmd]?.(row)
}

const toggleItemStatus = async (row) => {
  await updateFeeItem(row.id, { status: row.status === 1 ? 0 : 1 })
  ElMessage.success(row.status === 1 ? '已停用' : '已启用')
  loadItemData()
}

const handleItemSubmit = async () => {
  await itemFormRef.value.validate()
  itemSubmitLoading.value = true
  try {
    if (itemForm.id) {
      await updateFeeItem(itemForm.id, itemForm)
      ElMessage.success('更新成功')
    } else {
      await addFeeItem(itemForm)
      ElMessage.success('添加成功')
    }
    itemDialogVisible.value = false
    loadItemData()
  } finally { itemSubmitLoading.value = false }
}

const handleAddBill = () => {
  Object.assign(billForm, { id: null, billNo: '', elderlyId: null, elderlyName: '', billMonth: '', totalAmount: 0, paidAmount: 0, status: 0, dueDate: null, remark: '' })
  billDialogVisible.value = true
}

const handleEditBill = (row) => {
  Object.assign(billForm, row)
  billDialogVisible.value = true
}

const handleBillDetail = (row) => {
  Object.assign(billForm, row)
  billDialogVisible.value = true
}

const handleBillSubmit = async () => {
  await billFormRef.value.validate()
  billSubmitLoading.value = true
  try {
    if (billForm.id) {
      await updateFeeBill(billForm.id, billForm)
      ElMessage.success('更新成功')
    } else {
      await addFeeBill(billForm)
      ElMessage.success('添加成功')
    }
    billDialogVisible.value = false
    loadBillData()
  } finally { billSubmitLoading.value = false }
}

onMounted(() => { loadItemData(); loadBillData(); loadElderly() })
</script>