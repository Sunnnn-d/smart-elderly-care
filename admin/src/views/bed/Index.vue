<template>
  <div class="bed-page">
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span style="font-size:1.1rem;font-weight:600">房间管理</span>
          <div style="display:flex;gap:12px">
            <el-input v-model="roomQuery.roomNumber" placeholder="房间号" clearable style="width:130px" @clear="loadRoomData" @keyup.enter="loadRoomData" />
            <el-select v-model="roomQuery.roomType" placeholder="房间类型" clearable style="width:130px" @change="loadRoomData">
              <el-option label="单人间" value="single" />
              <el-option label="双人间" value="double" />
              <el-option label="三人间" value="triple" />
              <el-option label="套房" value="suite" />
            </el-select>
            <el-select v-model="roomQuery.status" placeholder="状态" clearable style="width:100px" @change="loadRoomData">
              <el-option label="可用" :value="1" />
              <el-option label="停用" :value="0" />
            </el-select>
            <el-button type="primary" @click="loadRoomData"><el-icon><Search /></el-icon> 查询</el-button>
            <el-button type="primary" @click="handleAddRoom"><el-icon><Plus /></el-icon> 新增房间</el-button>
          </div>
        </div>
      </template>

      <el-table :data="roomTableData" stripe v-loading="roomLoading">
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="floor" label="楼层" width="70" />
        <el-table-column prop="roomType" label="房间类型" width="100">
          <template #default="{ row }">{{ roomTypeLabels[row.roomType] || row.roomType }}</template>
        </el-table-column>
        <el-table-column prop="bedCount" label="床位数量" width="100" />
        <el-table-column prop="windowDirection" label="朝向" width="80" />
        <el-table-column prop="facility" label="配套设施" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '可用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" @click="handleRoomDetail(row)">详情</el-button>
            <el-button text type="warning" @click="handleEditRoom(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:20px;display:flex;justify-content:flex-end">
        <el-pagination
          v-model:current-page="roomQuery.pageNum"
          v-model:page-size="roomQuery.pageSize"
          :total="roomTotal"
          :page-sizes="[10,20,50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadRoomData"
          @current-change="loadRoomData"
        />
      </div>
    </el-card>

    <el-card style="margin-top:24px">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span style="font-size:1.1rem;font-weight:600">床位管理</span>
          <div style="display:flex;gap:12px">
            <el-select v-model="bedQuery.roomId" placeholder="选择房间" clearable style="width:150px" @change="loadBedData">
              <el-option v-for="r in roomList" :key="r.id" :label="r.roomNumber" :value="r.id" />
            </el-select>
            <el-select v-model="bedQuery.status" placeholder="状态" clearable style="width:100px" @change="loadBedData">
              <el-option label="空闲" :value="0" />
              <el-option label="已入住" :value="1" />
              <el-option label="维修中" :value="2" />
            </el-select>
            <el-button type="primary" @click="loadBedData"><el-icon><Search /></el-icon> 查询</el-button>
            <el-button type="primary" @click="handleAddBed"><el-icon><Plus /></el-icon> 新增床位</el-button>
          </div>
        </div>
      </template>

      <el-table :data="bedTableData" stripe v-loading="bedLoading">
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="bedNumber" label="床位号" width="100" />
        <el-table-column prop="elderlyName" label="老人姓名" width="100">
          <template #default="{ row }">{{ row.elderlyName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="checkInTime" label="入住时间" width="170">
          <template #default="{ row }">{{ row.checkInTime || '-' }}</template>
        </el-table-column>
        <el-table-column prop="checkOutTime" label="离院时间" width="170">
          <template #default="{ row }">{{ row.checkOutTime || '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="['','success','warning'][row.status]">
              {{ ['空闲','已入住','维修中'][row.status] }}
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
                  <el-dropdown-item v-if="row.status === 0" command="checkIn">入住</el-dropdown-item>
                  <el-dropdown-item v-if="row.status === 1" command="checkOut">离院</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:20px;display:flex;justify-content:flex-end">
        <el-pagination
          v-model:current-page="bedQuery.pageNum"
          v-model:page-size="bedQuery.pageSize"
          :total="bedTotal"
          :page-sizes="[10,20,50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadBedData"
          @current-change="loadBedData"
        />
      </div>
    </el-card>

    <el-dialog v-model="roomDialogVisible" :title="roomForm.id ? '编辑房间' : '新增房间'" width="600px" destroy-on-close>
      <el-form ref="roomFormRef" :model="roomForm" :rules="roomRules" label-width="100px">
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="roomForm.roomNumber" placeholder="请输入房间号" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="楼层">
              <el-input-number v-model="roomForm.floor" :min="1" :max="20" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间类型">
              <el-select v-model="roomForm.roomType" style="width:100%">
                <el-option label="单人间" value="single" />
                <el-option label="双人间" value="double" />
                <el-option label="三人间" value="triple" />
                <el-option label="套房" value="suite" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="床位数量">
              <el-input-number v-model="roomForm.bedCount" :min="1" :max="10" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="朝向">
              <el-select v-model="roomForm.windowDirection" style="width:100%">
                <el-option label="东" value="东" />
                <el-option label="南" value="南" />
                <el-option label="西" value="西" />
                <el-option label="北" value="北" />
                <el-option label="东南" value="东南" />
                <el-option label="西南" value="西南" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="配套设施">
          <el-input v-model="roomForm.facility" type="textarea" :rows="2" placeholder="请输入配套设施" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="roomForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roomDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="roomSubmitLoading" @click="handleRoomSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="bedDialogVisible" :title="bedForm.id ? '编辑床位' : '新增床位'" width="500px" destroy-on-close>
      <el-form ref="bedFormRef" :model="bedForm" :rules="bedRules" label-width="100px">
        <el-form-item label="房间号" prop="roomId">
          <el-select v-model="bedForm.roomId" placeholder="请选择房间" style="width:100%" @change="onBedRoomChange">
            <el-option v-for="r in roomList" :key="r.id" :label="r.roomNumber" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="床位号" prop="bedNumber">
          <el-input v-model="bedForm.bedNumber" placeholder="请输入床位号" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="bedForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bedDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="bedSubmitLoading" @click="handleBedSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="checkInVisible" title="办理入住" width="450px">
      <el-form label-width="100px">
        <el-form-item label="房间号">{{ currentBed?.roomNumber }}</el-form-item>
        <el-form-item label="床位号">{{ currentBed?.bedNumber }}</el-form-item>
        <el-form-item label="选择老人">
          <el-select v-model="checkInForm.elderlyId" placeholder="请选择老人" style="width:100%" @change="onCheckInElderlyChange">
            <el-option v-for="e in elderlyList" :key="e.id" :label="e.name" :value="e.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkInVisible = false">取消</el-button>
        <el-button type="primary" :loading="checkInLoading" @click="submitCheckIn">确认入住</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="checkOutVisible" title="办理离院" width="450px">
      <el-form label-width="100px">
        <el-form-item label="房间号">{{ currentBed?.roomNumber }}</el-form-item>
        <el-form-item label="床位号">{{ currentBed?.bedNumber }}</el-form-item>
        <el-form-item label="老人姓名">{{ currentBed?.elderlyName }}</el-form-item>
        <el-form-item label="入住时间">{{ currentBed?.checkInTime }}</el-form-item>
        <el-form-item label="离院备注">
          <el-input v-model="checkOutForm.remark" type="textarea" :rows="2" placeholder="请输入离院备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkOutVisible = false">取消</el-button>
        <el-button type="primary" :loading="checkOutLoading" @click="submitCheckOut">确认离院</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import {
  getRoomList, addRoom, updateRoom, getAllRooms,
  getBedList, addBed, updateBed, checkInBed, checkOutBed,
  getElderlyList
} from '../../api'

const roomLoading = ref(false)
const bedLoading = ref(false)
const roomSubmitLoading = ref(false)
const bedSubmitLoading = ref(false)
const checkInLoading = ref(false)
const checkOutLoading = ref(false)
const roomDialogVisible = ref(false)
const bedDialogVisible = ref(false)
const checkInVisible = ref(false)
const checkOutVisible = ref(false)
const roomTotal = ref(0)
const bedTotal = ref(0)
const roomTableData = ref([])
const bedTableData = ref([])
const roomList = ref([])
const elderlyList = ref([])
const currentBed = ref(null)
const roomFormRef = ref()
const bedFormRef = ref()

const roomQuery = reactive({ pageNum: 1, pageSize: 10, roomNumber: '', roomType: null, status: null })
const bedQuery = reactive({ pageNum: 1, pageSize: 10, roomId: null, status: null })

const roomForm = reactive({
  id: null, roomNumber: '', floor: 1, roomType: 'double',
  bedCount: 2, windowDirection: '', facility: '', status: 1, remark: ''
})

const bedForm = reactive({
  id: null, roomId: null, roomNumber: '', bedNumber: '',
  elderlyId: null, elderlyName: '', checkInTime: null, checkOutTime: null,
  status: 0, remark: ''
})

const checkInForm = reactive({ elderlyId: null, elderlyName: '' })
const checkOutForm = reactive({ remark: '' })

const roomTypeLabels = {
  single: '单人间', double: '双人间', triple: '三人间', suite: '套房'
}

const roomRules = { roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }] }
const bedRules = { roomId: [{ required: true, message: '请选择房间', trigger: 'change' }], bedNumber: [{ required: true, message: '请输入床位号', trigger: 'blur' }] }

const loadRoomData = async () => {
  roomLoading.value = true
  try {
    const res = await getRoomList(roomQuery)
    roomTableData.value = res.data?.records || []
    roomTotal.value = res.data?.total || 0
  } finally { roomLoading.value = false }
}

const loadBedData = async () => {
  bedLoading.value = true
  try {
    const res = await getBedList(bedQuery)
    bedTableData.value = res.data?.records || []
    bedTotal.value = res.data?.total || 0
  } finally { bedLoading.value = false }
}

const loadAllRooms = async () => {
  const res = await getAllRooms()
  roomList.value = res.data || []
}

const loadElderly = async () => {
  const res = await getElderlyList({ pageNum: 1, pageSize: 999 })
  elderlyList.value = res.data?.records || []
}

const onBedRoomChange = (val) => {
  const r = roomList.value.find(item => item.id === val)
  bedForm.roomNumber = r?.roomNumber || ''
}

const onCheckInElderlyChange = (val) => {
  const e = elderlyList.value.find(item => item.id === val)
  checkInForm.elderlyName = e?.name || ''
}

const handleAddRoom = () => {
  Object.assign(roomForm, { id: null, roomNumber: '', floor: 1, roomType: 'double', bedCount: 2, windowDirection: '', facility: '', status: 1, remark: '' })
  roomDialogVisible.value = true
}

const handleEditRoom = (row) => {
  Object.assign(roomForm, row)
  roomDialogVisible.value = true
}

const handleRoomDetail = (row) => {
  Object.assign(roomForm, row)
  roomDialogVisible.value = true
}

const handleRoomSubmit = async () => {
  await roomFormRef.value.validate()
  roomSubmitLoading.value = true
  try {
    if (roomForm.id) {
      await updateRoom(roomForm.id, roomForm)
      ElMessage.success('更新成功')
    } else {
      await addRoom(roomForm)
      ElMessage.success('添加成功')
    }
    roomDialogVisible.value = false
    loadRoomData()
    loadAllRooms()
  } finally { roomSubmitLoading.value = false }
}

const handleAddBed = () => {
  Object.assign(bedForm, { id: null, roomId: null, roomNumber: '', bedNumber: '', elderlyId: null, elderlyName: '', checkInTime: null, checkOutTime: null, status: 0, remark: '' })
  bedDialogVisible.value = true
}

const handleBedDetail = (row) => {
  Object.assign(bedForm, row)
  bedDialogVisible.value = true
}

const handleCommand = (cmd, row) => {
  const actions = {
    detail: handleBedDetail,
    checkIn: handleCheckIn,
    checkOut: handleCheckOut
  }
  actions[cmd]?.(row)
}

const handleBedSubmit = async () => {
  await bedFormRef.value.validate()
  bedSubmitLoading.value = true
  try {
    if (bedForm.id) {
      await updateBed(bedForm.id, bedForm)
      ElMessage.success('更新成功')
    } else {
      await addBed(bedForm)
      ElMessage.success('添加成功')
    }
    bedDialogVisible.value = false
    loadBedData()
  } finally { bedSubmitLoading.value = false }
}

const handleCheckIn = (row) => {
  currentBed.value = row
  checkInForm.elderlyId = null
  checkInForm.elderlyName = ''
  checkInVisible.value = true
}

const submitCheckIn = async () => {
  if (!checkInForm.elderlyId) { ElMessage.warning('请选择老人'); return }
  checkInLoading.value = true
  try {
    await checkInBed(currentBed.value.id, checkInForm.elderlyId, checkInForm.elderlyName)
    ElMessage.success('入住成功')
    checkInVisible.value = false
    loadBedData()
  } finally { checkInLoading.value = false }
}

const handleCheckOut = (row) => {
  currentBed.value = row
  checkOutForm.remark = ''
  checkOutVisible.value = true
}

const submitCheckOut = async () => {
  checkOutLoading.value = true
  try {
    await checkOutBed(currentBed.value.id, checkOutForm.remark)
    ElMessage.success('离院成功')
    checkOutVisible.value = false
    loadBedData()
  } finally { checkOutLoading.value = false }
}

onMounted(() => { loadRoomData(); loadBedData(); loadAllRooms(); loadElderly() })
</script>