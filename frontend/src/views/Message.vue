<template>
  <div class="message-page">
    <div class="page-header">
      <h2>消息中心</h2>
      <div class="header-actions">
        <el-button type="primary" @click="markAllRead" :loading="markAllLoading">
          <el-icon><Finished /></el-icon>
          全部标为已读
        </el-button>
      </div>
    </div>

    <div class="message-tabs">
      <el-tabs v-model="activeTab" @change="handleTabChange">
        <el-tab-pane label="全部消息" name="all">
          <el-badge :value="totalUnread" class="badge-tab" />
        </el-tab-pane>
        <el-tab-pane label="未读消息" name="unread">
          <el-badge :value="unreadCount" class="badge-tab" />
        </el-tab-pane>
        <el-tab-pane label="已读消息" name="read" />
      </el-tabs>
    </div>

    <div class="message-list" v-loading="loading">
      <el-empty v-if="filteredMessages.length === 0" description="暂无消息" />
      <div v-for="msg in filteredMessages" :key="msg.id" class="message-item" :class="{ 'unread': msg.readFlag === 0 }">
        <div class="message-icon">
          <el-icon v-if="msg.type === 'order'" color="#FF8C00"><ShoppingCart /></el-icon>
          <el-icon v-else-if="msg.type === 'system'" color="#1890ff"><Bell /></el-icon>
          <el-icon v-else color="#52c41a"><Info /></el-icon>
        </div>
        <div class="message-content">
          <div class="message-header">
            <span class="message-title">{{ msg.title }}</span>
            <span class="message-time">{{ formatTime(msg.createTime) }}</span>
          </div>
          <p class="message-body">{{ msg.content }}</p>
          <div v-if="msg.orderId" class="message-order">
            <el-tag type="info" size="small">关联订单</el-tag>
            <span class="order-id">订单ID: {{ msg.orderId }}</span>
          </div>
        </div>
        <div class="message-actions">
          <el-button v-if="msg.readFlag === 0" size="small" @click="markRead(msg.id)">标为已读</el-button>
          <el-button size="small" type="danger" @click="deleteMessage(msg.id)">删除</el-button>
        </div>
      </div>
    </div>

    <el-pagination
      v-if="total > 0"
      :current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      @current-change="handlePageChange"
      layout="total, prev, pager, next, jumper"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAppUserMessages, getAppUserUnreadCount, markAppUserMessageRead, markAllAppUserMessagesRead, deleteAppUserMessage } from '../api'
import { ElMessage } from 'element-plus'
import { Finished, ShoppingCart, Bell, Info } from '@element-plus/icons-vue'

const router = useRouter()
const activeTab = ref('all')
const pageNum = ref(1)
const pageSize = ref(20)
const loading = ref(false)
const markAllLoading = ref(false)
const messages = ref([])
const total = ref(0)
const unreadCount = ref(0)
const totalUnread = ref(0)

const filteredMessages = computed(() => {
  if (activeTab.value === 'unread') {
    return messages.value.filter(m => m.readFlag === 0)
  } else if (activeTab.value === 'read') {
    return messages.value.filter(m => m.readFlag === 1)
  }
  return messages.value
})

const fetchMessages = async () => {
  loading.value = true
  try {
    const res = await getAppUserMessages({ pageNum: pageNum.value, pageSize: pageSize.value })
    messages.value = res.data?.records || []
    total.value = res.data?.total || 0
    totalUnread.value = messages.value.filter(m => m.readFlag === 0).length
    unreadCount.value = totalUnread.value
  } catch (e) {
    console.error('获取消息列表失败', e)
    ElMessage.error('获取消息列表失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  if (activeTab.value === 'unread') {
    unreadCount.value = totalUnread.value
  } else {
    unreadCount.value = 0
  }
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchMessages()
}

const markRead = async (messageId) => {
  try {
    await markAppUserMessageRead(messageId)
    const msg = messages.value.find(m => m.id === messageId)
    if (msg) msg.readFlag = 1
    if (totalUnread.value > 0) totalUnread.value--
    ElMessage.success('已标记为已读')
  } catch (e) {
    ElMessage.error('标记失败')
  }
}

const markAllRead = async () => {
  markAllLoading.value = true
  try {
    await markAllAppUserMessagesRead()
    messages.value.forEach(m => m.readFlag = 1)
    totalUnread.value = 0
    unreadCount.value = 0
    ElMessage.success('已全部标记为已读')
  } catch (e) {
    ElMessage.error('标记失败')
  } finally {
    markAllLoading.value = false
  }
}

const deleteMessage = async (messageId) => {
  try {
    await deleteAppUserMessage(messageId)
    messages.value = messages.value.filter(m => m.id !== messageId)
    total.value--
    ElMessage.success('删除成功')
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  } else if (days === 1) {
    return '昨天 ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  } else if (days < 7) {
    return days + '天前'
  } else {
    return date.toLocaleDateString('zh-CN')
  }
}

onMounted(() => {
  fetchMessages()
})
</script>

<style lang="scss" scoped>
.message-page {
  padding: 30px;
  max-width: 800px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  h2 {
    margin: 0;
    font-size: 1.5rem;
    color: #304156;
  }
}

.header-actions {
  display: flex;
  gap: 10px;
}

.message-tabs {
  margin-bottom: 20px;
  
  .badge-tab {
    margin-left: 8px;
  }
}

.message-list {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  min-height: 400px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s;
  
  &:hover {
    background-color: #fafafa;
  }
  
  &.unread {
    background-color: #fffbe6;
  }
  
  &:last-child {
    border-bottom: none;
  }
}

.message-icon {
  margin-right: 16px;
  font-size: 24px;
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.message-title {
  font-weight: 600;
  color: #304156;
}

.message-time {
  font-size: 0.875rem;
  color: #8c8c8c;
}

.message-body {
  margin: 0;
  color: #666;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.message-order {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  
  .order-id {
    font-size: 0.875rem;
    color: #8c8c8c;
  }
}

.message-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-left: 16px;
}

.el-pagination {
  margin-top: 20px;
  text-align: center;
}
</style>
