<template>
  <div class="dashboard-page">
    <el-row :gutter="24" class="stat-row">
      <el-col :span="6">
        <div class="stat-card" style="background:linear-gradient(135deg,#FF8C00,#FFA940)">
          <div class="stat-value">{{ stats.elderlyCount }}</div>
          <div class="stat-label">在院老人</div>
          <el-icon class="stat-icon"><User /></el-icon>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="background:linear-gradient(135deg,#52C41A,#73D13D)">
          <div class="stat-value">{{ stats.serviceCount }}</div>
          <div class="stat-label">服务项目</div>
          <el-icon class="stat-icon"><Goods /></el-icon>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="background:linear-gradient(135deg,#1890FF,#40A9FF)">
          <div class="stat-value">{{ stats.orderCount }}</div>
          <div class="stat-label">本月订单</div>
          <el-icon class="stat-icon"><List /></el-icon>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="background:linear-gradient(135deg,#722ED1,#9254DE)">
          <div class="stat-value">{{ stats.nurseCount }}</div>
          <div class="stat-label">护理员工</div>
          <el-icon class="stat-icon"><Avatar /></el-icon>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="24" style="margin-top:24px">
      <el-col :span="12">
        <el-card>
          <template #header><span style="font-weight:600">订单状态分布</span></template>
          <div ref="pieChartRef" style="height:320px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span style="font-weight:600">服务类型统计</span></template>
          <div ref="barChartRef" style="height:320px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="24" style="margin-top:24px">
      <el-col :span="12">
        <el-card>
          <template #header><span style="font-weight:600">最近订单</span></template>
          <el-table :data="recentOrders" size="small">
            <el-table-column prop="orderNo" label="订单号" width="160" />
            <el-table-column prop="elderlyName" label="老人" width="80" />
            <el-table-column prop="serviceName" label="服务" width="100" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="['warning','','success','info'][row.status]" size="small">
                  {{ ['待派单','服务中','已完成','已取消'][row.status] }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span style="font-weight:600">待处理事项</span></template>
          <div class="todo-list">
            <div class="todo-item">
              <el-icon color="#FF8C00"><Warning /></el-icon>
              <span>有 {{ stats.pendingOrders }} 个订单待派单</span>
              <el-button text type="primary" @click="$router.push('/order')">去处理</el-button>
            </div>
            <div class="todo-item">
              <el-icon color="#52C41A"><FirstAidKit /></el-icon>
              <span>今日需记录健康数据老人 {{ stats.todayHealth }} 人</span>
            </div>
            <div class="todo-item">
              <el-icon color="#1890FF"><Document /></el-icon>
              <span>有 {{ stats.activePlans }} 个护理计划执行中</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getElderlyList, getServiceItemList, getOrderList, getNurseList } from '../../api'

const pieChartRef = ref(null)
const barChartRef = ref(null)
const recentOrders = ref([])

const stats = reactive({
  elderlyCount: 0,
  serviceCount: 0,
  orderCount: 0,
  nurseCount: 0,
  pendingOrders: 0,
  todayHealth: 5,
  activePlans: 3
})

const loadStats = async () => {
  try {
    const [elderlyRes, serviceRes, orderRes, nurseRes] = await Promise.all([
      getElderlyList({ pageNum: 1, pageSize: 1 }),
      getServiceItemList({ pageNum: 1, pageSize: 1 }),
      getOrderList({ pageNum: 1, pageSize: 5 }),
      getNurseList()
    ])
    stats.elderlyCount = elderlyRes.data?.total || 0
    stats.serviceCount = serviceRes.data?.total || 0
    stats.orderCount = orderRes.data?.total || 0
    stats.nurseCount = (nurseRes.data || []).length
    recentOrders.value = orderRes.data?.records || []
    stats.pendingOrders = recentOrders.value.filter(o => o.status === 0).length
  } catch (e) {
    console.error(e)
  }
}

const initCharts = () => {
  const pieChart = echarts.init(pieChartRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: 3, name: '待派单' },
        { value: 5, name: '服务中' },
        { value: 20, name: '已完成' },
        { value: 2, name: '已取消' }
      ],
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.5)' } }
    }]
  })

  const barChart = echarts.init(barChartRef.value)
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['助餐', '保洁', '护理', '康复', '陪伴', '其他'] },
    yAxis: { type: 'value' },
    series: [{
      type: 'bar',
      data: [12, 8, 15, 6, 10, 3],
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#FF8C00' },
          { offset: 1, color: '#FFA940' }
        ]),
        borderRadius: [4, 4, 0, 0]
      }
    }]
  })

  window.addEventListener('resize', () => { pieChart.resize(); barChart.resize() })
}

onMounted(async () => {
  await loadStats()
  initCharts()
})
</script>

<style lang="scss" scoped>
.stat-row {
  .stat-card {
    position: relative;
    padding: 24px;
    border-radius: 12px;
    color: #fff;
    .stat-value { font-size: 2.2rem; font-weight: 700; margin-bottom: 8px; }
    .stat-label { font-size: 0.95rem; opacity: 0.9; }
    .stat-icon { font-size: 3rem; opacity: 0.2; position: absolute; right: 20px; top: 20px; }
  }
}

.todo-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 0.95rem;
  span { flex: 1; }
}
</style>
