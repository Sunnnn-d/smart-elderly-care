import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  getAppUserMessages,
  getAppUserUnreadCount,
  markAppUserMessageRead,
  markAllAppUserMessagesRead,
  deleteAppUserMessage,
  getAdminMessages,
  getAdminUnreadCount,
  sendMessageToUser,
  sendSystemMessage,
  markAdminMessageRead,
  markAllAdminMessagesRead,
  deleteAdminMessage
} from '../api/message'

export const useMessageStore = defineStore('message', () => {
  // 客户端消息状态
  const appMessages = ref([])
  const appUnreadCount = ref(0)
  const appLoading = ref(false)

  // 管理端消息状态
  const adminMessages = ref([])
  const adminUnreadCount = ref(0)
  const adminLoading = ref(false)

  // 获取客户端消息列表
  const fetchAppMessages = async (params = {}) => {
    appLoading.value = true
    try {
      const res = await getAppUserMessages({ pageNum: 1, pageSize: 20, ...params })
      appMessages.value = res.data?.records || []
    } catch (e) {
      console.error('获取客户端消息失败', e)
    } finally {
      appLoading.value = false
    }
  }

  // 获取客户端未读消息数（最大显示99）
  const fetchAppUnreadCount = async () => {
    try {
      const res = await getAppUserUnreadCount()
      const count = res.data || 0
      appUnreadCount.value = count > 99 ? 99 : count
    } catch (e) {
      console.error('获取未读消息数失败', e)
    }
  }

  // 标记单条客户端消息已读
  const markAppMessageRead = async (messageId) => {
    try {
      await markAppUserMessageRead(messageId)
      const msg = appMessages.value.find(m => m.id === messageId)
      if (msg) msg.readFlag = 1
      if (appUnreadCount.value > 0) appUnreadCount.value--
    } catch (e) {
      console.error('标记消息已读失败', e)
    }
  }

  // 标记所有客户端消息已读
  const markAllAppMessagesRead = async () => {
    try {
      await markAllAppUserMessagesRead()
      appMessages.value.forEach(m => m.readFlag = 1)
      appUnreadCount.value = 0
    } catch (e) {
      console.error('标记所有消息已读失败', e)
    }
  }

  // 删除客户端消息
  const removeAppMessage = async (messageId) => {
    try {
      await deleteAppUserMessage(messageId)
      appMessages.value = appMessages.value.filter(m => m.id !== messageId)
    } catch (e) {
      console.error('删除消息失败', e)
    }
  }

  // 获取管理端消息列表
  const fetchAdminMessages = async (params = {}) => {
    adminLoading.value = true
    try {
      const res = await getAdminMessages({ pageNum: 1, pageSize: 20, ...params })
      adminMessages.value = res.data?.records || []
    } catch (e) {
      console.error('获取管理端消息失败', e)
    } finally {
      adminLoading.value = false
    }
  }

  // 获取管理端未读消息数（最大显示99）
  const fetchAdminUnreadCount = async () => {
    try {
      const res = await getAdminUnreadCount()
      const count = res.data || 0
      adminUnreadCount.value = count > 99 ? 99 : count
    } catch (e) {
      console.error('获取管理端未读消息数失败', e)
    }
  }

  // 发送消息给用户
  const sendToUser = async (data) => {
    try {
      await sendMessageToUser(data)
    } catch (e) {
      console.error('发送消息失败', e)
      throw e
    }
  }

  // 发送系统消息
  const sendSystem = async (data) => {
    try {
      await sendSystemMessage(data)
    } catch (e) {
      console.error('发送系统消息失败', e)
      throw e
    }
  }

  // 标记管理端消息已读
  const markAdminMessageAsRead = async (messageId) => {
    try {
      await markAdminMessageRead(messageId)
      const msg = adminMessages.value.find(m => m.id === messageId)
      if (msg) msg.readFlag = 1
      if (adminUnreadCount.value > 0) adminUnreadCount.value--
    } catch (e) {
      console.error('标记管理端消息已读失败', e)
    }
  }

  // 标记所有管理端消息已读
  const markAllAdminMessagesAsRead = async () => {
    try {
      await markAllAdminMessagesRead()
      adminMessages.value.forEach(m => m.readFlag = 1)
      adminUnreadCount.value = 0
    } catch (e) {
      console.error('标记所有管理端消息已读失败', e)
    }
  }

  // 删除管理端消息
  const removeAdminMessage = async (messageId) => {
    try {
      await deleteAdminMessage(messageId)
      adminMessages.value = adminMessages.value.filter(m => m.id !== messageId)
    } catch (e) {
      console.error('删除管理端消息失败', e)
    }
  }

  // 未读消息列表（客户端）
  const unreadAppMessages = computed(() => appMessages.value.filter(m => m.readFlag === 0))

  // 已读消息列表（客户端）
  const readAppMessages = computed(() => appMessages.value.filter(m => m.readFlag === 1))

  // 未处理消息列表（管理端）
  const unreadAdminMessages = computed(() => adminMessages.value.filter(m => m.readFlag === 0))

  // 已处理消息列表（管理端）
  const readAdminMessages = computed(() => adminMessages.value.filter(m => m.readFlag === 1))

  return {
    // 客户端状态
    appMessages,
    appUnreadCount,
    appLoading,
    // 管理端状态
    adminMessages,
    adminUnreadCount,
    adminLoading,
    // 客户端方法
    fetchAppMessages,
    fetchAppUnreadCount,
    markAppMessageRead,
    markAllAppMessagesRead,
    removeAppMessage,
    // 管理端方法
    fetchAdminMessages,
    fetchAdminUnreadCount,
    sendToUser,
    sendSystem,
    markAdminMessageAsRead,
    markAllAdminMessagesAsRead,
    removeAdminMessage,
    // 计算属性
    unreadAppMessages,
    readAppMessages,
    unreadAdminMessages,
    readAdminMessages
  }
})