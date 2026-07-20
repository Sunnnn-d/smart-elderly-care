import request from './request'

// ========== 客户端消息接口 ==========

// 获取客户端用户消息列表
export const getAppUserMessages = (params) => request.get('/message/app-user/list', { params })

// 获取客户端用户未读消息数
export const getAppUserUnreadCount = () => request.get('/message/app-user/unread-count')

// 标记单条消息已读
export const markAppUserMessageRead = (messageId) => request.put(`/message/app-user/${messageId}/read`)

// 标记所有消息已读
export const markAllAppUserMessagesRead = () => request.put('/message/app-user/all/read')

// 删除客户端用户消息
export const deleteAppUserMessage = (messageId) => request.delete(`/message/app-user/${messageId}`)

// ========== 管理端消息接口 ==========

// 获取消息列表（管理端）
export const getAdminMessages = (params) => request.get('/message/admin/list', { params })

// 获取未处理消息数（管理端）
export const getAdminUnreadCount = () => request.get('/message/admin/unread-count')

// 发送消息给客户端用户
export const sendMessageToUser = (data) => request.post('/message/send-to-user', data)

// 发送系统消息（广播）
export const sendSystemMessage = (data) => request.post('/message/send-system', data)

// 标记消息已处理（管理端）
export const markAdminMessageRead = (messageId) => request.put(`/message/admin/${messageId}/read`)

// 标记所有消息已处理（管理端）
export const markAllAdminMessagesRead = () => request.put('/message/admin/all/read')

// 删除消息（管理端）
export const deleteAdminMessage = (messageId) => request.delete(`/message/admin/${messageId}`)

// 获取消息类型列表
export const getMessageTypes = () => request.get('/message/types')

// 客户端用户发送消息给管理员
export const sendAppMessageToAdmin = (data) => request.post('/message/app-user/send-to-admin', data)