<template>
  <div class="message-page">
    <div class="page-header">
      <h2>消息中心</h2>
      <div class="header-actions">
        <el-button type="primary" @click="markAllRead" :loading="markAllLoading">
          <el-icon><Finished /></el-icon>
          全部标为已读
        </el-button>
        <el-button type="success" @click="showSendDialog = true">
          <el-icon><ChatDotRound /></el-icon>
          发送消息
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
          <el-icon v-else color="#52c41a"><InfoFilled /></el-icon>
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

    <el-dialog v-model="showSendDialog" title="发送消息给管理员" width="480px" :close-on-click-modal="false">
      <el-form :model="sendForm" label-width="80px">
        <el-form-item label="消息类型">
          <el-select v-model="sendForm.type" placeholder="请选择消息类型">
            <el-option label="服务提醒" value="service" />
            <el-option label="订单消息" value="order" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="消息标题">
          <el-input v-model="sendForm.title" placeholder="请输入消息标题" />
        </el-form-item>
        <el-form-item label="消息内容">
          <el-input v-model="sendForm.content" type="textarea" :rows="4" placeholder="请输入消息内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSendDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSendMessage" :loading="sendLoading">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAppUserMessages, getAppUserUnreadCount, markAppUserMessageRead, markAllAppUserMessagesRead, deleteAppUserMessage, sendAppMessageToAdmin } from '../api'
import { cancelAllRequests } from '../api/request'
import { ElMessage } from 'element-plus'
import { Finished, ShoppingCart, Bell, InfoFilled, ChatDotRound } from '@element-plus/icons-vue'

const router = useRouter()
const isMounted = ref(false)
const activeTab = ref('all')
const pageNum = ref(1)
const pageSize = ref(20)
const loading = ref(false)
const markAllLoading = ref(false)
const sendLoading = ref(false)
const showSendDialog = ref(false)
const messages = ref([])
const total = ref(0)
const unreadCount = ref(0)
const totalUnread = ref(0)

const sendForm = ref({
  type: 'service',
  title: '',
  content: ''
})

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
    const [msgRes, countRes] = await Promise.all([
      getAppUserMessages({ pageNum: pageNum.value, pageSize: pageSize.value }),
      getAppUserUnreadCount()
    ])
    if (!isMounted.value) return
    messages.value = msgRes.data?.records || []
    total.value = msgRes.data?.total || 0
    totalUnread.value = countRes.data || 0
    unreadCount.value = totalUnread.value
  } catch (e) {
    console.error('获取消息列表失败', e)
    ElMessage.error('获取消息列表失败')
  } finally {
    if (isMounted.value) {
      loading.value = false
    }
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
    if (!isMounted.value) return
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
    if (!isMounted.value) return
    messages.value.forEach(m => m.readFlag = 1)
    totalUnread.value = 0
    unreadCount.value = 0
    ElMessage.success('已全部标记为已读')
  } catch (e) {
    ElMessage.error('标记失败')
  } finally {
    if (isMounted.value) {
      markAllLoading.value = false
    }
  }
}

const deleteMessage = async (messageId) => {
  try {
    await deleteAppUserMessage(messageId)
    if (!isMounted.value) return
    messages.value = messages.value.filter(m => m.id !== messageId)
    total.value--
    ElMessage.success('删除成功')
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

const handleSendMessage = async () => {
  if (!sendForm.value.title.trim()) {
    ElMessage.warning('请输入消息标题')
    return
  }
  if (!sendForm.value.content.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }
  sendLoading.value = true
  try {
    await sendAppMessageToAdmin({
      type: sendForm.value.type,
      title: sendForm.value.title.trim(),
      content: sendForm.value.content.trim()
    })
    showSendDialog.value = false
    sendForm.value = { type: 'service', title: '', content: '' }
    ElMessage.success('消息发送成功，管理员会尽快回复您')
  } catch (e) {
    ElMessage.error('发送失败')
  } finally {
    sendLoading.value = false
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
  isMounted.value = true
  fetchMessages()
})

onUnmounted(() => {
  isMounted.value = false
  cancelAllRequests()
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

@media (max-width: 768px) {
  .message-page {
    padding: 20px 16px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    margin-bottom: 16px;
    
    h2 { font-size: 1.4rem; }
  }
  
  .header-actions {
    width: 100%;
  }
  
  .message-tabs {
    margin-bottom: 16px;
  }
  
  .message-item {
    padding: 12px;
    flex-wrap: wrap;
  }
  
  .message-icon {
    margin-right: 12px;
    font-size: 20px;
  }
  
  .message-header {
    margin-bottom: 6px;
    
    .message-time {
      font-size: 0.8rem;
    }
  }
  
  .message-body {
    font-size: 0.9rem;
    line-height: 1.5;
  }
  
  .message-order {
    margin-top: 6px;
    
    .order-id {
      font-size: 0.8rem;
    }
  }
  
  .message-actions {
    margin-left: 0;
    margin-top: 12px;
    flex-direction: row;
    gap: 10px;
    width: 100%;
    justify-content: flex-end;
  }
  
  .el-pagination {
    margin-top: 16px;
  }
}
</style>
