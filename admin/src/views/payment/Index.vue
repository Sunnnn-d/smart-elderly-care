<template>
  <div class="payment-page">
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span style="font-size:1.1rem;font-weight:600">支付记录管理</span>
          <div style="display:flex;gap:12px">
            <el-input v-model="query.elderlyName" placeholder="老人姓名" clearable style="width:150px" @clear="loadData" @keyup.enter="loadData" />
            <el-input v-model="query.payNo" placeholder="支付编号" clearable style="width:180px" @clear="loadData" @keyup.enter="loadData" />
            <el-select v-model="query.status" placeholder="状态" clearable style="width:120px" @change="loadData">
              <el-option label="待支付" :value="0" />
              <el-option label="支付中" :value="1" />
              <el-option label="支付成功" :value="2" />
              <el-option label="支付失败" :value="3" />
            </el-select>
            <el-select v-model="query.payMethod" placeholder="支付方式" clearable style="width:130px" @change="loadData">
              <el-option label="微信支付" value="wechat" />
              <el-option label="支付宝" value="alipay" />
              <el-option label="银行转账" value="bank" />
              <el-option label="现金" value="cash" />
            </el-select>
            <el-button type="primary" @click="loadData"><el-icon><Search /></el-icon> 查询</el-button>
            <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增支付</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="payNo" label="支付编号" width="150" />
        <el-table-column prop="billNo" label="账单编号" width="150">
          <template #default="{ row }">{{ row.billNo || '-' }}</template>
        </el-table-column>
        <el-table-column prop="elderlyName" label="老人姓名" width="100" />
        <el-table-column prop="payAmount" label="支付金额" width="110">
          <template #default="{ row }">¥{{ row.payAmount }}</template>
        </el-table-column>
        <el-table-column prop="payMethod" label="支付方式" width="110">
          <template #default="{ row }">{{ payMethodLabels[row.payMethod] || row.payMethod }}</template>
        </el-table-column>
        <el-table-column prop="payTime" label="支付时间" width="170">
          <template #default="{ row }">{{ row.payTime || '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="['warning','','success','danger'][row.status]">
              {{ ['待支付','支付中','支付成功','支付失败'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="transactionId" label="交易ID" width="180" show-overflow-tooltip>
          <template #default="{ row }">{{ row.transactionId || '-' }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleDetail(row)">详情</el-button>
            <el-button text type="warning" @click="handleEdit(row)">编辑</el-button>
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

    <el-dialog v-model="detailVisible" title="支付详情" width="550px">
      <el-descriptions :column="2" border v-if="currentPayment">
        <el-descriptions-item label="支付编号">{{ currentPayment.payNo }}</el-descriptions-item>
        <el-descriptions-item label="账单编号">{{ currentPayment.billNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="老人姓名">{{ currentPayment.elderlyName }}</el-descriptions-item>
        <el-descriptions-item label="支付金额">¥{{ currentPayment.payAmount }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ payMethodLabels[currentPayment.payMethod] || currentPayment.payMethod }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ currentPayment.payTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="['warning','','success','danger'][currentPayment.status]">
            {{ ['待支付','支付中','支付成功','支付失败'][currentPayment.status] }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="交易ID">{{ currentPayment.transactionId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentPayment.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑支付记录' : '新增支付记录'" width="550px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="选择老人" prop="elderlyId">
          <el-select v-model="form.elderlyId" placeholder="请选择老人" style="width:100%" @change="onElderlyChange">
            <el-option v-for="e in elderlyList" :key="e.id" :label="e.name" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="支付金额">
              <el-input-number v-model="form.payAmount" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付方式">
              <el-select v-model="form.payMethod" style="width:100%">
                <el-option label="微信支付" value="wechat" />
                <el-option label="支付宝" value="alipay" />
                <el-option label="银行转账" value="bank" />
                <el-option label="现金" value="cash" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="关联账单">
          <el-select v-model="form.billId" placeholder="请选择账单（可选）" style="width:100%" @change="onBillChange">
            <el-option v-for="b in billList" :key="b.id" :label="`${b.billNo} - ${b.elderlyName}`" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="交易ID">
          <el-input v-model="form.transactionId" placeholder="请输入第三方交易ID" />
        </el-form-item>
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
import { getPaymentList, createPayment, updatePayment, getElderlyList, getFeeBillList } from '../../api'

const loading = ref(false)
const submitLoading = ref(false)
const detailVisible = ref(false)
const dialogVisible = ref(false)
const total = ref(0)
const tableData = ref([])
const currentPayment = ref(null)
const elderlyList = ref([])
const billList = ref([])
const formRef = ref()

const query = reactive({ pageNum: 1, pageSize: 10, elderlyName: '', payNo: '', status: null, payMethod: null })

const form = reactive({
  id: null, payNo: '', billId: null, billNo: '',
  elderlyId: null, elderlyName: '', payAmount: 0,
  payMethod: 'wechat', payTime: null, status: 0,
  transactionId: '', remark: ''
})

const rules = { elderlyId: [{ required: true, message: '请选择老人', trigger: 'change' }] }

const payMethodLabels = { wechat: '微信支付', alipay: '支付宝', bank: '银行转账', cash: '现金' }

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPaymentList(query)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const loadElderly = async () => {
  const res = await getElderlyList({ pageNum: 1, pageSize: 999 })
  elderlyList.value = res.data?.records || []
}

const loadBills = async () => {
  const res = await getFeeBillList({ pageNum: 1, pageSize: 999 })
  billList.value = res.data?.records || []
}

const onElderlyChange = (val) => {
  const e = elderlyList.value.find(item => item.id === val)
  form.elderlyName = e?.name || ''
}

const onBillChange = (val) => {
  const b = billList.value.find(item => item.id === val)
  form.billNo = b?.billNo || ''
}

const handleDetail = (row) => { currentPayment.value = row; detailVisible.value = true }

const handleAdd = () => {
  Object.assign(form, { id: null, payNo: '', billId: null, billNo: '', elderlyId: null, elderlyName: '', payAmount: 0, payMethod: 'wechat', payTime: null, status: 0, transactionId: '', remark: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (form.id) {
      await updatePayment(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await createPayment(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

onMounted(() => { loadData(); loadElderly(); loadBills() })
</script>