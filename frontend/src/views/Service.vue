<template>
  <div class="service-page">
    <div class="page-banner">
      <h1>服务预约</h1>
      <p>选择您需要的服务，一键预约，专业团队为您服务</p>
    </div>

    <div class="service-container">
      <div class="category-filter">
        <el-radio-group v-model="currentCategory" @change="filterServices" size="large">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="助餐">助餐服务</el-radio-button>
          <el-radio-button label="保洁">保洁服务</el-radio-button>
          <el-radio-button label="护理">护理服务</el-radio-button>
          <el-radio-button label="康复">康复理疗</el-radio-button>
          <el-radio-button label="陪伴">陪伴聊天</el-radio-button>
        </el-radio-group>
      </div>

      <div class="service-list">
        <div v-for="item in filteredServices" :key="item.id" class="service-item-card">
          <div class="service-image">
            <div class="image-placeholder">
              <el-icon :size="64" color="#FF8C00"><component :is="iconComponentMap[getServiceIcon(item.category)]" /></el-icon>
            </div>
          </div>
          <div class="service-detail">
            <div class="service-tag">
              <el-tag type="warning" effect="light">{{ item.category }}</el-tag>
            </div>
            <h3>{{ item.name }}</h3>
            <p class="desc">{{ item.description }}</p>
            <div class="service-meta">
              <span class="duration">
                <el-icon><Clock /></el-icon>
                约{{ item.duration }}分钟
              </span>
            </div>
            <div class="service-bottom">
              <div class="price-info">
                <span class="price">¥{{ item.price }}</span>
                <span class="unit">/{{ item.unit }}</span>
              </div>
              <el-button type="primary" round @click="openAppointment(item)">立即预约</el-button>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-if="filteredServices.length === 0" description="暂无服务项目" />
    </div>

    <!-- 预约对话框 -->
    <el-dialog v-model="dialogVisible" title="预约服务" width="520px" destroy-on-close>
      <div class="appointment-service-info">
        <el-icon :size="32" color="#FF8C00"><component :is="iconComponentMap[getServiceIcon(currentService?.category)]" /></el-icon>
        <div>
          <div class="service-name">{{ currentService?.name }}</div>
          <div class="service-price">¥{{ currentService?.price }}/{{ currentService?.unit }}</div>
        </div>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" label-width="105px">
        <el-form-item label="老人姓名" prop="elderlyName">
          <el-input v-model="form.elderlyName" placeholder="请输入老人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="预约时间" prop="appointmentTime">
          <el-date-picker v-model="form.appointmentTime" type="datetime" placeholder="选择预约时间" style="width:100%" />
        </el-form-item>
        <el-form-item label="服务地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入服务地址" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitAppointment">确认预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getServiceItems, createAppointment } from '../api'
import { useUserStore } from '../stores/user'
import { getServiceIcon } from '../utils'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ShoppingCart, Bell, FirstAidKit, Edit, UserFilled, Document, Clock } from '@element-plus/icons-vue'

const iconComponentMap = {
  ShoppingCart,
  Bell,
  FirstAidKit,
  Edit,
  UserFilled,
  Document
}

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isMounted = ref(false)
const allServices = ref([])
const currentCategory = ref('')
const dialogVisible = ref(false)
const currentService = ref(null)
const submitLoading = ref(false)
const formRef = ref()

const form = ref({
  elderlyName: '', contactPhone: '', appointmentTime: null, address: '', remark: ''
})

const rules = {
  elderlyName: [
    { required: true, message: '请输入老人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在2-20个字符之间', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  appointmentTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }],
  address: [{ required: true, message: '请输入服务地址', trigger: 'blur' }]
}

const filteredServices = computed(() => {
  if (!currentCategory.value) return allServices.value
  return allServices.value.filter(s => s.category === currentCategory.value)
})

const filterServices = () => {}

const openAppointment = (item) => {
  if (!userStore.isLogin) {
    ElMessageBox.confirm(
      '您还未登录，需要登录后才能预约服务',
      '提示',
      {
        confirmButtonText: '去登录',
        cancelButtonText: '取消',
        type: 'info'
      }
    ).then(() => {
      router.push('/login')
    }).catch(() => {})
    return
  }
  currentService.value = item
  form.value = { elderlyName: '', contactPhone: '', appointmentTime: null, address: '', remark: '' }
  dialogVisible.value = true
}

const submitAppointment = async () => {
  try {
    await formRef.value.validate()
  } catch (e) {
    ElMessage.warning('请填写完整的预约信息')
    return
  }
  
  submitLoading.value = true
  try {
    await createAppointment({
      ...form.value,
      serviceId: currentService.value.id,
      userId: userStore.userInfo?.id
    })
    ElMessage.success('预约成功！我们将尽快安排服务人员')
    dialogVisible.value = false
  } catch (e) {
    console.error(e)
    ElMessage.error(e.response?.data?.message || '预约失败，请稍后重试')
  } finally {
    submitLoading.value = false
  }
}

onMounted(async () => {
  isMounted.value = true
  try {
    const res = await getServiceItems()
    if (!isMounted.value) return
    allServices.value = res.data || []
    if (route.query.id) {
      const target = allServices.value.find(s => s.id === Number(route.query.id))
      if (target) openAppointment(target)
    }
  } catch (e) {
    console.error(e)
  }
})

onUnmounted(() => {
  isMounted.value = false
})
</script>

<style lang="scss" scoped>
.page-banner {
  background: linear-gradient(135deg, #FF8C00, #FFA940);
  padding: 60px 24px;
  text-align: center;
  color: #fff;
  h1 { font-size: 2.2rem; font-weight: 700; margin-bottom: 12px; }
  p { font-size: 1.2rem; opacity: 0.9; }
}

.service-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 24px;
}

.category-filter {
  text-align: center;
  margin-bottom: 32px;
}

.service-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.service-item-card {
  display: flex;
  gap: 32px;
  padding: 28px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  border: 2px solid transparent;
  transition: all 0.3s;
  &:hover {
    border-color: #FF8C00;
    box-shadow: 0 4px 20px rgba(255, 140, 0, 0.15);
  }
}

.service-image {
  flex-shrink: 0;
  .image-placeholder {
    width: 160px;
    height: 160px;
    border-radius: 12px;
    background: #FFF8F0;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.service-detail {
  flex: 1;
  .service-tag { margin-bottom: 8px; }
  h3 { font-size: 1.4rem; color: #333; margin-bottom: 8px; }
  .desc { color: #888; font-size: 0.95rem; line-height: 1.6; margin-bottom: 12px; }
  .service-meta { color: #999; font-size: 0.9rem; margin-bottom: 16px; display: flex; align-items: center; gap: 4px; }
}

.service-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  .price-info {
    .price { font-size: 2rem; font-weight: 700; color: #FF8C00; }
    .unit { color: #999; font-size: 0.9rem; }
  }
}

.appointment-service-info {
  background: #FFF8F0;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  .service-name { font-size: 1.1rem; font-weight: 600; }
  .service-price { color: #FF8C00; font-weight: 700; font-size: 1.1rem; }
}

@media (max-width: 768px) {
  .page-banner {
    padding: 40px 16px;
    h1 { font-size: 1.6rem !important; }
    p { font-size: 1rem !important; }
  }
  
  .service-container {
    padding: 24px 16px;
  }
  
  .category-filter {
    margin-bottom: 20px;
  }
  
  .service-list {
    gap: 16px;
  }
  
  .service-item-card { 
    flex-direction: column; 
    gap: 16px;
    padding: 20px;
  }
  
  .service-image .image-placeholder { 
    width: 100%; 
    height: 100px; 
  }
  
  .service-detail {
    h3 { font-size: 1.2rem !important; }
    .desc { font-size: 0.9rem !important; }
  }
  
  .service-bottom {
    .price-info .price { font-size: 1.6rem !important; }
  }
  
  .appointment-service-info {
    flex-direction: row;
    align-items: center;
    padding: 12px;
    gap: 12px;
    
    .service-name { font-size: 0.95rem !important; }
    .service-price { font-size: 0.95rem !important; }
  }
  
  :deep(.el-dialog__body) {
    padding: 20px !important;
  }
  
  :deep(.el-form-item) {
    margin-bottom: 16px !important;
  }
  
  :deep(.el-form-item__label) {
    font-size: 0.9rem !important;
  }
}
</style>
