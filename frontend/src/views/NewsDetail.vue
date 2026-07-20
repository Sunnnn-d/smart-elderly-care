<template>
  <div class="news-detail-page">
    <div class="detail-container">
      <el-button text @click="$router.back()" class="back-btn">
        <el-icon><ArrowLeft /></el-icon> 返回列表
      </el-button>

      <div class="detail-content" v-if="notice">
        <div class="detail-header">
          <el-tag :type="getTypeTag(notice.type)" effect="light">
            {{ getTypeName(notice.type) }}
          </el-tag>
          <h1>{{ notice.title }}</h1>
          <div class="detail-meta">
            <span><el-icon><Calendar /></el-icon> {{ notice.publishTime }}</span>
            <span><el-icon><User /></el-icon> {{ notice.author }}</span>
          </div>
        </div>
        <div class="detail-body" v-html="notice.content"></div>
      </div>

      <el-skeleton :loading="loading" animated :rows="10" v-else />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { getNoticeDetail } from '../api'

const route = useRoute()
const isMounted = ref(false)
const notice = ref(null)
const loading = ref(true)

const getTypeName = (type) => {
  const map = { 1: '公告通知', 2: '健康知识', 3: '活动通知' }
  return map[type] || '其他'
}

const getTypeTag = (type) => {
  const map = { 1: 'warning', 2: 'success', 3: 'primary' }
  return map[type] || 'info'
}

onMounted(async () => {
  isMounted.value = true
  try {
    const res = await getNoticeDetail(route.params.id)
    if (!isMounted.value) return
    notice.value = res.data
  } catch (e) {
    console.error('加载资讯详情失败', e)
  } finally {
    if (isMounted.value) {
      loading.value = false
    }
  }
})

onUnmounted(() => {
  isMounted.value = false
})
</script>

<style lang="scss" scoped>
.detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 24px;
}

.back-btn {
  margin-bottom: 24px;
  font-size: 1rem;
}

.detail-content {
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.detail-header {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
  h1 {
    font-size: 1.8rem;
    color: #333;
    margin: 16px 0;
    line-height: 1.5;
  }
  .detail-meta {
    display: flex;
    gap: 24px;
    color: #999;
    font-size: 0.95rem;
    span {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }
}

.detail-body {
  font-size: 1.1rem;
  line-height: 2;
  color: #555;
  :deep(p) {
    margin-bottom: 16px;
  }
  :deep(img) {
    max-width: 100%;
    border-radius: 8px;
  }
}

@media (max-width: 768px) {
  .detail-container {
    padding: 24px 16px;
  }
  
  .back-btn {
    margin-bottom: 16px;
    font-size: 0.9rem;
  }
  
  .detail-content {
    padding: 24px 20px;
  }
  
  .detail-header {
    margin-bottom: 24px;
    padding-bottom: 16px;
    
    h1 {
      font-size: 1.4rem !important;
      margin: 12px 0;
    }
    
    .detail-meta {
      gap: 16px;
      font-size: 0.85rem;
      flex-wrap: wrap;
    }
  }
  
  .detail-body {
    font-size: 1rem;
    line-height: 1.8;
  }
}
</style>
