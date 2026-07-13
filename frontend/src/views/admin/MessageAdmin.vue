<template>
  <div class="message-admin">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1>消息管理</h1>
        <p>管理系统消息和用户通知</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="openSendDialog">
          <el-icon><Send /></el-icon>
          发送消息
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon" style="background:#FF8C00">
          <el-icon :size="32" color="#fff"><Bell /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ messageStore.adminMessages.length }}</div>
          <div class="stat-label">全部消息</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#FF4D4F">
          <el-icon :size="32" color="#fff"><AlertCircle /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ messageStore.adminUnreadCount }}</div>
          <div class="stat-label">未处理</div>
        </div>
      </div>
    </div>

    <!-- 操作栏 -->
    <div class="toolbar">
      <div class="tabs">
        <el-button
          :type="activeTab === 'all' ? 'primary' : 'default'"
          @click="activeTab = 'all'"
        >
          全部
        </el-button>
        <el-button
          :type="activeTab === 'unread' ? 'primary' : 'default'"
          @click="activeTab = 'unread'"
        >
          未处理
        </el-button>
        <el-button
          :type="activeTab === 'read' ? 'primary' : 'default'"
          @click="activeTab = 'read'"
        >
          已处理
        </el-button>
      </div>
      <div class="actions">
        <el-button text @click="markAllRead" v-if="messageStore.adminUnreadCount > 0">
          全部标记已处理
        </el-button>
      </div>
    </div>

    <!-- 消息列表 -->
    <div class="message-table" v-if="!messageStore.adminLoading">
      <el-table :data="filteredMessages" border style="width:100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="content" label="内容" min-width="300">
          <template #default="scope">
            {{ scope.row.content?.substring(0, 50) }}{{ scope.row.content?.length > 50 ? '...' : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="getTypeTag(scope.row.type)">{{ getTypeName(scope.row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="senderName" label="发送者" width="120" />
        <el-table-column prop="readFlag" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.readFlag === 1 ? 'success' : 'warning'">
              {{ scope.row.readFlag === 1 ? '已处理' : '未处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button text @click="viewDetail(scope.row)">查看</el-button>
            <el-button text @click="markRead(scope.row.id)">标记已处理</el-button>
            <el-button text @click="deleteMessage(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="filteredMessages.length === 0" description="暂无消息" />
    </div>

    <!-- 加载中 -->
    <div v-else class="loading">
      <el-spin size="large" />
    </div>

    <!-- 查看详情弹窗 -->
    <el-dialog title="消息详情" v-model="detailVisible" width="500px">
      <div v-if="currentMessage" class="detail-content">
        <div class="detail-row">
          <span class="label">标题：</span>
          <span>{{ currentMessage.title }}</span>
        </div>
        <div class="detail-row">
          <span class="label">类型：</span>
          <el-tag :type="getTypeTag(currentMessage.type)">{{ getTypeName(currentMessage.type) }}</el-tag>
        </div>
        <div class="detail-row">
          <span class="label">发送者：</span>
          <span>{{ currentMessage.senderName || '系统' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">时间：</span>
          <span>{{ currentMessage.createTime }}</span>
        </div>
        <div class="detail-row">
          <span class="label">内容：</span>
          <div class="content-box">{{ currentMessage.content }}</div>
        </div>
      </div>
    </el-dialog>

    <!-- 发送消息弹窗 -->
    <el-dialog title="发送消息" v-model="sendDialogVisible" width="550px">
      <el-form ref="sendFormRef" :model="sendForm" :rules="sendRules" label-width="100px">
        <el-form-item label="发送类型" prop="sendType">
          <el-radio-group v-model="sendForm.sendType">
            <el-radio :value="'single'">发送给单个用户</el-radio>
            <el-radio :value="'system'">系统广播</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="sendForm.sendType === 'single'" label="用户ID" prop="userId">
          <el-input v-model="sendForm.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="消息类型" prop="type">
          <el-select v-model="sendForm.type" placeholder="请选择消息类型">
            <el-option label="系统通知" value="system" />
            <el-option label="订单消息" value="order" />
            <el-option label="服务提醒" value="service" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="消息标题" prop="title">
          <el-input v-model="sendForm.title" placeholder="请输入消息标题" />
        </el-form-item>
        <el-form-item label="消息内容" prop="content">
          <el-input v-model="sendForm.content" type="textarea" :rows="4" placeholder="请输入消息内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sendDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="sending" @click="sendMessage">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useMessageStore } from '../../stores/message'
import { ElMessage } from 'element-plus'
import { Bell, AlertCircle, Send } from '@element-plus/icons-vue'

const messageStore = useMessageStore()
const activeTab = ref('all')
const detailVisible = ref(false)
const sendDialogVisible = ref(false)
const currentMessage = ref(null)
const sending = ref(false)
const sendFormRef = ref(null)

const sendForm = ref({
  sendType: 'system',
  userId: '',
  type: 'system',
  title: '',
  content: ''
})

const sendRules = {
  sendType: [{ required: true, message: '请选择发送类型', trigger: 'change' }],
  userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }],
  type: [{ required: true, message: '请选择消息类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入消息标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入消息内容', trigger: 'blur' }]
}

const filteredMessages = computed(() => {
  let list = messageStore.adminMessages
  if (activeTab.value === 'unread') {
    list = list.filter(m => m.readFlag === 0)
  } else if (activeTab.value === 'read') {
    list = list.filter(m => m.readFlag === 1)
  }
  return list
})

const getTypeName = (type) => {
  const map = {
    system: '系统通知',
    order: '订单消息',
    service: '服务提醒',
    other: '其他'
  }
  return map[type] || '未知'
}

const getTypeTag = (type) => {
  const map = {
    system: 'info',
    order: 'success',
    service: 'warning',
    other: 'default'
  }
  return map[type] || 'default'
}

const viewDetail = (msg) => {
  currentMessage.value = msg
  detailVisible.value = true
  if (msg.readFlag === 0) {
    markRead(msg.id)
  }
}

const markRead = async (id) => {
  await messageStore.markAdminMessageAsRead(id)
}

const markAllRead = async () => {
  await messageStore.markAllAdminMessagesAsRead()
  ElMessage.success('已全部标记为已处理')
}

const deleteMessage = async (id) => {
  await messageStore.removeAdminMessage(id)
  ElMessage.success('删除成功')
}

const openSendDialog = () => {
  sendForm.value = {
    sendType: 'system',
    userId: '',
    type: 'system',
    title: '',
    content: ''
  }
  sendDialogVisible.value = true
}

const sendMessage = async () => {
  try {
    await sendFormRef.value.validate()
  } catch (e) {
    ElMessage.warning('请填写完整信息')
    return
  }

  sending.value = true
  try {
    if (sendForm.value.sendType === 'system') {
      await messageStore.sendSystem({
        type: sendForm.value.type,
        title: sendForm.value.title,
        content: sendForm.value.content
      })
    } else {
      await messageStore.sendToUser({
        userId: sendForm.value.userId,
        type: sendForm.value.type,
        title: sendForm.value.title,
        content: sendForm.value.content
      })
    }
    ElMessage.success('发送成功')
    sendDialogVisible.value = false
    messageStore.fetchAdminMessages()
  } catch (e) {
    ElMessage.error('发送失败')
  } finally {
    sending.value = false
  }
}

onMounted(() => {
  messageStore.fetchAdminMessages()
  messageStore.fetchAdminUnreadCount()
})
</script>

<style lang="scss" scoped>
.message-admin {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  
  .header-left {
    h1 {
      font-size: 1.5rem;
      color: #333;
      margin-bottom: 4px;
    }
    p {
      color: #999;
    }
  }
}

.stats-row {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #fff;
  padding: 20px 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  min-width: 200px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-info {
  .stat-value {
    font-size: 1.5rem;
    font-weight: 700;
    color: #333;
  }
  .stat-label {
    font-size: 0.9rem;
    color: #999;
  }
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  
  .tabs {
    display: flex;
    gap: 8px;
  }
}

.message-table {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
}

.detail-content {
  .detail-row {
    margin-bottom: 16px;
    font-size: 1rem;
    
    .label {
      color: #999;
      margin-right: 8px;
    }
    
    .content-box {
      color: #666;
      padding: 12px;
      background: #fafafa;
      border-radius: 8px;
      margin-top: 8px;
      line-height: 1.8;
    }
  }
}
</style>