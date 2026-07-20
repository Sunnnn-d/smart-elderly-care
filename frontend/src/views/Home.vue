<template>
  <div class="home-page">
    <!-- 轮播图 -->
    <section class="banner-section">
      <el-carousel height="420px" :interval="5000" arrow="hover">
        <el-carousel-item v-for="banner in banners" :key="banner.id">
          <div class="banner-item" :style="{ backgroundImage: `url(${banner.image})` }">
            <div class="banner-overlay">
              <h2>{{ banner.title }}</h2>
            </div>
          </div>
        </el-carousel-item>
        <el-carousel-item v-if="banners.length === 0">
          <div class="banner-item banner-default">
            <div class="banner-overlay">
              <h2>暖夕伴养老 温暖相伴</h2>
              <p>专业养老服务，让晚年生活更幸福</p>
              <el-button type="primary" size="large" round @click="$router.push('/service')">
                立即预约服务
              </el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 服务项目 -->
    <section class="service-section">
      <div class="section-container">
        <div class="section-header">
          <h2>我们的服务</h2>
          <p>专业团队，用心服务每一位老人</p>
        </div>
        <div class="service-grid">
          <div v-for="item in serviceItems" :key="item.id" class="service-card">
            <div class="service-icon">
              <el-icon :size="48"><component :is="iconComponentMap[getServiceIcon(item.category)] || Circle" /></el-icon>
            </div>
            <h3>{{ item.name }}</h3>
            <p class="service-desc">{{ item.description }}</p>
            <div class="service-price">
              <span class="price">¥{{ item.price }}</span>
              <span class="unit">/{{ item.unit }}</span>
            </div>
            <el-button type="primary" round @click="goAppointment(item)">立即预约</el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 我们的优势 -->
    <section class="advantage-section">
      <div class="section-container">
        <div class="section-header">
          <h2>我们的优势</h2>
          <p>选择暖夕伴养老的四大理由</p>
        </div>
        <div class="advantage-grid">
          <div class="advantage-card">
            <el-icon :size="56" color="#FF8C00"><Checked /></el-icon>
            <h3>专业团队</h3>
            <p>持证上岗，定期培训</p>
          </div>
          <div class="advantage-card">
            <el-icon :size="56" color="#52C41A"><Clock /></el-icon>
            <h3>随叫随到</h3>
            <p>7x24小时在线服务</p>
          </div>
          <div class="advantage-card">
            <el-icon :size="56" color="#1890FF"><Shield /></el-icon>
            <h3>安全保障</h3>
            <p>全程监控，安心无忧</p>
          </div>
          <div class="advantage-card">
            <el-icon :size="56" color="#722ED1"><Star /></el-icon>
            <h3>口碑优良</h3>
            <p>98%客户满意度</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 健康资讯 -->
    <section class="news-section">
      <div class="section-container">
        <div class="section-header">
          <h2>健康资讯</h2>
          <p>关注健康，乐享晚年</p>
        </div>
        <div class="news-grid">
          <div v-for="item in notices" :key="item.id" class="news-card" @click="$router.push(`/news/${item.id}`)">
            <div class="news-cover">
              <el-icon :size="40" color="#FF8C00"><Document /></el-icon>
            </div>
            <div class="news-info">
              <h3>{{ item.title }}</h3>
              <p>{{ item.content?.substring(0, 80) }}...</p>
              <span class="news-date">{{ item.publishTime }}</span>
            </div>
          </div>
        </div>
        <div style="text-align:center;margin-top:32px">
          <el-button round @click="$router.push('/news')">查看更多资讯</el-button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getBanners, getServiceItems, getNotices } from '../api'
import { useUserStore } from '../stores/user'
import { getServiceIcon } from '../utils'
import { ShoppingCart, Trophy, FirstAidKit, Wallet, Bell, Edit, Document, Cellphone, UserFilled, Calendar } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const isMounted = ref(false)

const iconComponentMap = {
  ShoppingCart,
  Trophy,
  FirstAidKit,
  Wallet,
  Bell,
  Edit,
  Document,
  Cellphone,
  UserFilled,
  Calendar
}
const banners = ref([])
const serviceItems = ref([])
const notices = ref([])

const goAppointment = (item) => {
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
  } else {
    router.push({ path: '/service', query: { id: item.id } })
  }
}

onMounted(async () => {
  isMounted.value = true
  try {
    const [bannerRes, serviceRes, noticeRes] = await Promise.all([
      getBanners().catch(() => ({ data: [] })),
      getServiceItems().catch(() => ({ data: [] })),
      getNotices({ pageNum: 1, pageSize: 4 }).catch(() => ({ data: { records: [] } }))
    ])
    if (!isMounted.value) return
    banners.value = bannerRes.data || []
    serviceItems.value = serviceRes.data || []
    notices.value = noticeRes.data?.records || []
  } catch (e) {
    console.error('加载首页数据失败', e)
  }
})

onUnmounted(() => {
  isMounted.value = false
})
</script>

<style lang="scss" scoped>
.banner-section {
  .banner-item {
    height: 420px;
    background-size: cover;
    background-position: center;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .banner-default {
    background: linear-gradient(135deg, #FF8C00, #FFA940);
  }
  .banner-overlay {
    text-align: center;
    color: #fff;
    h2 {
      font-size: 2.8rem;
      font-weight: 700;
      margin-bottom: 16px;
      text-shadow: 0 2px 8px rgba(0,0,0,0.2);
    }
    p {
      font-size: 1.3rem;
      margin-bottom: 24px;
      opacity: 0.9;
    }
  }
}

.section-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 24px;
}

.section-header {
  text-align: center;
  margin-bottom: 48px;
  h2 {
    font-size: 2rem;
    color: #333;
    margin-bottom: 12px;
  }
  p {
    color: #888;
    font-size: 1.1rem;
  }
}

.service-section {
  background: #fff;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.service-card {
  text-align: center;
  padding: 32px 20px;
  border-radius: 16px;
  background: #FFF8F0;
  transition: all 0.3s;
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(255, 140, 0, 0.15);
  }
  .service-icon {
    margin-bottom: 16px;
  }
  h3 {
    font-size: 1.3rem;
    color: #333;
    margin-bottom: 12px;
  }
  .service-desc {
    color: #888;
    font-size: 0.9rem;
    line-height: 1.6;
    margin-bottom: 16px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  .service-price {
    margin-bottom: 16px;
    .price {
      font-size: 1.8rem;
      font-weight: 700;
      color: #FF8C00;
    }
    .unit {
      color: #999;
      font-size: 0.9rem;
    }
  }
}

.advantage-section {
  background: #FFF8F0;
}

.advantage-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}

.advantage-card {
  text-align: center;
  padding: 40px 20px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
  transition: all 0.3s;
  &:hover {
    transform: translateY(-4px);
  }
  h3 {
    font-size: 1.2rem;
    color: #333;
    margin: 16px 0 8px;
  }
  p {
    color: #888;
    font-size: 0.95rem;
  }
}

.news-section {
  background: #fff;
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.news-card {
  display: flex;
  gap: 20px;
  padding: 20px;
  border-radius: 12px;
  background: #FFF8F0;
  cursor: pointer;
  transition: all 0.3s;
  &:hover {
    background: #FFE8CC;
    transform: translateX(4px);
  }
  .news-cover {
    width: 100px;
    height: 100px;
    border-radius: 8px;
    background: #FFE8CC;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }
  .news-info {
    flex: 1;
    h3 {
      font-size: 1.1rem;
      color: #333;
      margin-bottom: 8px;
      display: -webkit-box;
      -webkit-line-clamp: 1;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    p {
      color: #888;
      font-size: 0.9rem;
      line-height: 1.6;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    .news-date {
      color: #bbb;
      font-size: 0.85rem;
      margin-top: 8px;
      display: block;
    }
  }
}

@media (max-width: 768px) {
  .home-page {
    width: 100%;
    overflow-x: hidden;
  }
  
  .banner-section {
    .el-carousel {
      height: 240px !important;
    }
    
    .banner-item { 
      height: 240px !important; 
    }
    
    .banner-overlay {
      padding: 0 20px;
      
      h2 { 
        font-size: 1.5rem !important; 
        margin-bottom: 10px;
        line-height: 1.4;
      }
      
      p { 
        font-size: 0.95rem !important; 
        margin-bottom: 16px;
        opacity: 0.95;
      }
      
      .el-button {
        padding: 12px 28px !important;
        font-size: 0.95rem !important;
      }
    }
  }
  
  .section-container {
    padding: 32px 12px;
    max-width: 100%;
  }
  
  .section-header {
    margin-bottom: 24px;
    text-align: center;
    
    h2 { 
      font-size: 1.4rem !important; 
      color: #333;
      margin-bottom: 8px;
      font-weight: 600;
    }
    
    p { 
      font-size: 0.95rem !important; 
      color: #888;
      margin-bottom: 0;
    }
  }
  
  .service-section {
    background: #fff;
  }
  
  .service-grid { 
    grid-template-columns: 1fr; 
    gap: 12px;
  }
  
  .service-card {
    padding: 20px 16px;
    border-radius: 12px;
    background: #FFF8F0;
    border: 1px solid #FFE4C4;
    
    .service-icon {
      margin-bottom: 12px;
      text-align: center;
      
      .el-icon {
        font-size: 40px;
      }
    }
    
    h3 { 
      font-size: 1.15rem !important; 
      color: #333;
      margin-bottom: 8px;
      text-align: center;
    }
    
    .service-desc { 
      font-size: 0.85rem !important; 
      color: #888;
      line-height: 1.5;
      margin-bottom: 12px;
      text-align: center;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .service-price { 
      margin-bottom: 14px;
      text-align: center;
      
      .price { 
        font-size: 1.4rem !important; 
        font-weight: 700;
        color: #FF8C00;
      }
      
      .unit { 
        font-size: 0.85rem;
        color: #999;
      }
    }
    
    .el-button {
      width: 100%;
      padding: 12px 0 !important;
      font-size: 0.95rem !important;
      border-radius: 8px !important;
    }
  }
  
  .advantage-section {
    background: #FFF8F0;
  }
  
  .advantage-grid { 
    grid-template-columns: repeat(2, 1fr); 
    gap: 12px;
  }
  
  .advantage-card {
    padding: 20px 12px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    text-align: center;
    
    .el-icon {
      font-size: 40px;
      margin-bottom: 10px;
    }
    
    h3 { 
      font-size: 1rem !important; 
      color: #333;
      margin: 10px 0 6px;
      font-weight: 600;
    }
    
    p { 
      font-size: 0.8rem !important; 
      color: #888;
      line-height: 1.5;
    }
  }
  
  .news-section {
    background: #fff;
  }
  
  .news-grid { 
    grid-template-columns: 1fr; 
    gap: 12px;
  }
  
  .news-card {
    flex-direction: row;
    padding: 14px;
    gap: 12px;
    background: #FFF8F0;
    border-radius: 10px;
    border: 1px solid #FFE4C4;
    cursor: pointer;
    transition: all 0.2s;
    
    &:active {
      background: #FFE8CC;
      transform: scale(0.98);
    }
    
    .news-cover {
      width: 60px;
      height: 60px;
      border-radius: 8px;
      background: #FFE8CC;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
      
      .el-icon {
        font-size: 28px;
      }
    }
    
    .news-info {
      flex: 1;
      min-width: 0;
      
      h3 { 
        font-size: 0.95rem !important; 
        color: #333;
        margin-bottom: 6px;
        display: -webkit-box;
        -webkit-line-clamp: 1;
        -webkit-box-orient: vertical;
        overflow: hidden;
        font-weight: 500;
      }
      
      p { 
        font-size: 0.82rem !important; 
        color: #888;
        line-height: 1.5;
        margin-bottom: 6px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }
      
      .news-date {
        font-size: 0.78rem;
        color: #bbb;
      }
    }
  }
  
  div[style*="text-align:center"] {
    margin-top: 24px !important;
    
    .el-button {
      padding: 12px 32px !important;
      font-size: 0.95rem !important;
    }
  }
}
</style>
