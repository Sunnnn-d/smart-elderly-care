<template>
  <div class="news-page">
    <div class="page-banner">
      <h1>健康资讯</h1>
      <p>关注健康，乐享晚年</p>
    </div>

    <div class="news-container">
      <div class="news-tabs">
        <el-radio-group v-model="currentType" @change="loadNotices" size="large">
          <el-radio-button :value="null">全部</el-radio-button>
          <el-radio-button :value="1">公告通知</el-radio-button>
          <el-radio-button :value="2">健康知识</el-radio-button>
          <el-radio-button :value="3">活动通知</el-radio-button>
        </el-radio-group>
      </div>

      <div class="news-list" v-if="!loading">
        <div v-for="item in notices" :key="item.id" class="news-item" @click="$router.push(`/news/${item.id}`)">
          <div class="news-type-tag">
            <el-tag :type="getNoticeTypeTag(item.type)" effect="light">{{ getNoticeTypeName(item.type) }}</el-tag>
            <el-tag v-if="item.isTop === 1" type="danger" effect="dark" size="small">置顶</el-tag>
          </div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.content?.substring(0, 120) }}...</p>
          <div class="news-meta">
            <span><el-icon><Calendar /></el-icon> {{ item.publishTime }}</span>
            <span><el-icon><User /></el-icon> {{ item.author }}</span>
          </div>
        </div>
      </div>

      <div v-else class="news-skeleton">
        <div v-for="i in 5" :key="i" class="skeleton-item">
          <el-skeleton :rows="4" animated />
        </div>
      </div>

      <el-empty v-if="!loading && notices.length === 0" description="暂无资讯" />

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadNotices"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNotices } from '../api'
import { getNoticeTypeName, getNoticeTypeTag } from '../utils'

const notices = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentType = ref(null)
const loading = ref(true)

const loadNotices = async () => {
  loading.value = true
  try {
    const res = await getNotices({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      type: currentType.value
    })
    notices.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载资讯失败', e)
    notices.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

onMounted(() => loadNotices())
</script>

<style lang="scss" scoped>
.page-banner {
  background: linear-gradient(135deg, #52C41A, #73D13D);
  padding: 60px 24px;
  text-align: center;
  color: #fff;
  h1 { font-size: 2.2rem; font-weight: 700; margin-bottom: 12px; }
  p { font-size: 1.2rem; opacity: 0.9; }
}

.news-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 40px 24px;
}

.news-tabs {
  text-align: center;
  margin-bottom: 32px;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.news-item {
  background: #fff;
  border-radius: 12px;
  padding: 28px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  cursor: pointer;
  transition: all 0.3s;
  &:hover {
    transform: translateX(4px);
    box-shadow: 0 4px 16px rgba(0,0,0,0.1);
  }
  .news-type-tag { display: flex; gap: 8px; margin-bottom: 12px; }
  h3 { font-size: 1.3rem; color: #333; margin-bottom: 12px; }
  p { color: #888; font-size: 0.95rem; line-height: 1.8; margin-bottom: 16px; }
  .news-meta {
    display: flex;
    gap: 24px;
    color: #bbb;
    font-size: 0.9rem;
    span { display: flex; align-items: center; gap: 4px; }
  }
}

.news-skeleton {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.skeleton-item {
  background: #fff;
  border-radius: 12px;
  padding: 28px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.pagination-wrap {
  margin-top: 32px;
  text-align: center;
}
</style>
