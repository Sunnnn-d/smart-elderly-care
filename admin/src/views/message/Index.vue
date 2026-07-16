<template>
  <div class="message-page">
    <div class="page-header">
      <h2>消息管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="openSendModal">
          <el-icon><ChatLineRound /></el-icon>
          发送消息
        </el-button>
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
          <div v-if="msg.targetType === 'user' && msg.userId" class="message-user">
            <el-tag type="success" size="small">发送给用户</el-tag>
            <span class="user-id">用户ID: {{ msg.userId }}</span>
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

    <!-- 发送消息弹窗 -->
    <el-dialog title="发送消息" :visible.sync="sendModalVisible" width="500px" @close="resetSendForm">
      <el-form ref="sendFormRef" :model="sendForm" :rules="sendRules" label-width="100px">
        <el-form-item label="发送类型" prop="sendType">
          <el-radio-group v-model="sendForm.sendType">
            <el-radio label="single">发送给单个用户</el-radio>
            <el-radio label="system">系统广播（所有用户）</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="sendForm.sendType === 'single'" label="用户ID" prop="userId">
          <el-input v-model.number="sendForm.userId" placeholder="请输入接收消息的用户ID" />
        </el-form-item>

        <el-form-item label="消息类型" prop="type">
          <el-select v-model="sendForm.type" placeholder="请选择消息类型">
            <el-option v-for="(label, value) in messageTypes" :key="value" :label="label" :value="value" />
          </el-select>
        </el-form-item>

        <el-form-item label="消息标题" prop="title">
          <el-input v-model="sendForm.title" placeholder="请输入消息标题" />
        </el-form-item>

        <el-form-item label="消息内容" prop="content">
          <el-textarea v-model="sendForm.content" placeholder="请输入消息内容" :rows="4" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="sendModalVisible = false">取消</el-button>
        <el-button type="primary" :loading="sending" @click="sendMessage">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>import { ref, computed, onMounted, reactive } from 'vue';
import { getAdminMessages, markAdminMessageRead, markAllAdminMessagesRead, deleteAdminMessage, sendMessageToUser, sendSystemMessage, getMessageTypes } from '../../api';
import { ElMessage } from 'element-plus';
import { Finished, ShoppingCart, Bell, InfoFilledFilled, ChatLineRound } from '@element-plus/icons-vue';
const activeTab = ref('all');
const pageNum = ref(1);
const pageSize = ref(20);
const loading = ref(false);
const markAllLoading = ref(false);
const sending = ref(false);
const messages = ref([]);
const total = ref(0);
const unreadCount = ref(0);
const totalUnread = ref(0);
const sendModalVisible = ref(false);
const sendFormRef = ref();
const messageTypes = ref({});
const sendForm = reactive({
 sendType: 'single',
 userId: '',
 type: 'system',
 title: '',
 content: ''
});
const sendRules = {
 sendType: [{ required: true, message: '请选择发送类型', trigger: 'change' }],
 userId: [{ required: true, message: '请输入用户ID', trigger: 'blur', validator: (rule, value) => {
 if (sendForm.sendType === 'single' && (!value || value <= 0)) {
 return Promise.reject('请输入有效的用户ID');
 }
 return Promise.resolve();
 } }],
 type: [{ required: true, message: '请选择消息类型', trigger: 'change' }],
 title: [{ required: true, message: '请输入消息标题', trigger: 'blur' }],
 content: [{ required: true, message: '请输入消息内容', trigger: 'blur' }]
};
const filteredMessages = computed(() => {
 if (activeTab.value === 'unread') {
 return messages.value.filter(m => m.readFlag === 0);
 }
 else if (activeTab.value === 'read') {
 return messages.value.filter(m => m.readFlag === 1);
 }
 return messages.value;
});
const fetchMessages = async () => {
 loading.value = true;
 try {
 const res = await getAdminMessages({ pageNum: pageNum.value, pageSize: pageSize.value });
 messages.value = res.data?.records || [];
 total.value = res.data?.total || 0;
 totalUnread.value = messages.value.filter(m => m.readFlag === 0).length;
 unreadCount.value = totalUnread.value;
 }
 catch (e) {
 console.error('获取消息列表失败', e);
 ElMessage.error('获取消息列表失败');
 }
 finally {
 loading.value = false;
 }
};
const fetchMessageTypes = async () => {
 try {
 const res = await getMessageTypes();
 messageTypes.value = res.data || {
 system: '系统通知',
 order: '订单消息',
 service: '服务提醒',
 other: '其他'
 };
 }
 catch (e) {
 console.error('获取消息类型失败', e);
 messageTypes.value = {
 system: '系统通知',
 order: '订单消息',
 service: '服务提醒',
 other: '其他'
 };
 }
};
const handleTabChange = () => {
 if (activeTab.value === 'unread') {
 unreadCount.value = totalUnread.value;
 }
 else {
 unreadCount.value = 0;
 }
};
const handlePageChange = (page) => {
 pageNum.value = page;
 fetchMessages();
};
const markRead = async (messageId) => {
 try {
 await markAdminMessageRead(messageId);
 const msg = messages.value.find(m => m.id === messageId);
 if (msg)
 msg.readFlag = 1;
 if (totalUnread.value > 0)
 totalUnread.value--;
 ElMessage.success('已标记为已读');
 }
 catch (e) {
 ElMessage.error('标记失败');
 }
};
const markAllRead = async () => {
 markAllLoading.value = true;
 try {
 await markAllAdminMessagesRead();
 messages.value.forEach(m => m.readFlag = 1);
 totalUnread.value = 0;
 unreadCount.value = 0;
 ElMessage.success('已全部标记为已读');
 }
 catch (e) {
 ElMessage.error('标记失败');
 }
 finally {
 markAllLoading.value = false;
 }
};
const deleteMessage = async (messageId) => {
 try {
 await deleteAdminMessage(messageId);
 messages.value = messages.value.filter(m => m.id !== messageId);
 total.value--;
 ElMessage.success('删除成功');
 }
 catch (e) {
 ElMessage.error('删除失败');
 }
};
const openSendModal = () => {
 sendModalVisible.value = true;
 fetchMessageTypes();
};
const resetSendForm = () => {
 sendForm.sendType = 'single';
 sendForm.userId = '';
 sendForm.type = 'system';
 sendForm.title = '';
 sendForm.content = '';
};
const sendMessage = async () => {
 if (!sendFormRef.value)
 return;
 try {
 await sendFormRef.value.validate();
 sending.value = true;
 if (sendForm.sendType === 'single') {
 await sendMessageToUser({
 userId: sendForm.userId,
 type: sendForm.type,
 title: sendForm.title,
 content: sendForm.content
 });
 }
 else {
 await sendSystemMessage({
 title: sendForm.title,
 content: sendForm.content
 });
 }
 ElMessage.success('发送成功');
 sendModalVisible.value = false;
 resetSendForm();
 }
 catch (e) {
 if (e !== false) {
 ElMessage.error('发送失败');
 }
 }
 finally {
 sending.value = false;
 }
};
const formatTime = (time) => {
 if (!time)
 return '';
 const date = new Date(time);
 const now = new Date();
 const diff = now.getTime() - date.getTime();
 const days = Math.floor(diff / (1000 * 60 * 60 * 24));
 if (days === 0) {
 return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
 }
 else if (days === 1) {
 return '昨天 ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
 }
 else if (days < 7) {
 return days + '天前';
 }
 else {
 return date.toLocaleDateString('zh-CN');
 }
};
onMounted(() => {
 fetchMessages();
 fetchMessageTypes();
});
</script>

<style lang="scss" scoped>
.message-page {
  padding: 20px;
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

.message-order, .message-user {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  
  .order-id, .user-id {
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
