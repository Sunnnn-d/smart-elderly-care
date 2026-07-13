<template>
  <el-drawer
    :model-value="modelValue"
    title="消息中心"
    :size="500"
    :close-on-click-modal="false"
    @update:model-value="handleClose"
  >
    <div class="message-center">
      <div class="message-header">
        <div class="tabs">
          <el-button
            :type="activeTab === 'all' ? 'primary' : 'default'"
            @click="activeTab = 'all'"
          >
            全部 ({{ messageStore.appMessages.length }})
          </el-button>
          <el-button
            :type="activeTab === 'unread' ? 'primary' : 'default'"
            @click="activeTab = 'unread'"
          >
            未读 ({{ messageStore.appUnreadCount }})
          </el-button>
        </div>
        <el-button
          v-if="messageStore.appUnreadCount > 0"
          type="text"
          @click="markAllRead"
        >
          全部已读
        </el-button>
      </div>

      <div class="message-list" v-if="!messageStore.appLoading">
        <div
          v-for="msg in filteredMessages"
          :key="msg.id"
          class="message-item"
          :class="{ unread: msg.readFlag === 0 }"
          @click="markRead(msg)"
        >
          <div class="message-icon">
            <el-icon v-if="msg.type === 'system'" :size="24" color="#FF8C00">
              <Bell />
            </el-icon>
            <el-icon v-else-if="msg.type === 'order'" :size="24" color="#52C41A">
              <ShoppingCart />
            </el-icon>
            <el-icon v-else :size="24" color="#1890FF">
              <ChatDotRound />
            </el-icon>
          </div>
          <div class="message-content">
            <div class="message-title">{{ msg.title }}</div>
            <div class="message-body">{{ msg.content }}</div>
            <div class="message-time">{{ formatTime(msg.createTime) }}</div>
          </div>
          <div class="message-actions">
            <el-button text @click.stop="deleteMessage(msg.id)">
              <el-icon color="#999"><Delete /></el-icon>
            </el-button>
          </div>
        </div>

        <el-empty v-if="filteredMessages.length === 0" description="暂无消息" />
      </div>

      <div v-else class="loading">
        <el-spin size="large" />
      </div>
    </div>
  </el-drawer>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useMessageStore } from '../stores/message'
import { ElMessage } from 'element-plus'
import { Bell, ShoppingCart, ChatDotRound, Delete } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: Boolean
})

const emit = defineEmits(['update:modelValue'])

const messageStore = useMessageStore()
const activeTab = ref('all')

const filteredMessages = computed(() => {
  if (activeTab.value === 'unread') {
    return messageStore.appMessages.filter(m => m.readFlag === 0)
  }
  return messageStore.appMessages
})

const handleClose = () => {
  emit('update:modelValue', false)
}

const markRead = async (msg) => {
  if (msg.readFlag === 0) {
    await messageStore.markAppMessageRead(msg.id)
  }
}

const markAllRead = async () => {
  await messageStore.markAllAppMessagesRead()
  ElMessage.success('已全部标记为已读')
}

const deleteMessage = async (id) => {
  await messageStore.removeAppMessage(id)
  ElMessage.success('删除成功')
}

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  
  return `${d.getMonth() + 1}/${d.getDate()}`
}

watch(() => props.modelValue, (val) => {
  if (val) {
    messageStore.fetchAppMessages()
  }
})
</script>

<style lang="scss" scoped>
.message-center {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 16px;
  
  .tabs {
    display: flex;
    gap: 8px;
  }
}

.message-list {
  flex: 1;
  overflow-y: auto;
}

.message-item {
  display: flex;
  align-items: flex-start;
  padding: 16px;
  background: #fafafa;
  border-radius: 12px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    background: #fff;
    box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  }
  
  &.unread {
    background: #fff;
    border-left: 4px solid #FF8C00;
  }
}

.message-icon {
  margin-right: 12px;
  padding: 8px;
  background: #f0f0f0;
  border-radius: 8px;
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-title {
  font-size: 1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.message-body {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-time {
  font-size: 0.8rem;
  color: #999;
}

.message-actions {
  margin-left: 12px;
  opacity: 0;
  transition: opacity 0.3s;
  
  .message-item:hover & {
    opacity: 1;
  }
}

.loading {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>