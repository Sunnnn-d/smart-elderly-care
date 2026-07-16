import request from './request'

// 客户端用户注册
export const appRegister = (data) => request.post('/app-auth/register', data)

// 客户端用户登录
export const appLogin = (data) => request.post('/app-auth/login', data)

// 客户端获取用户信息
export const getAppUserInfo = () => request.get('/app-auth/info')

// 获取轮播图
export const getBanners = () => request.get('/banner/public/list')

// 获取服务项目列表
export const getServiceItems = () => request.get('/service-item/all')

// 获取服务项目分页
export const getServiceItemPage = (params) => request.get('/service-item/list', { params })

// 创建预约订单
export const createAppointment = (data) => request.post('/order/appointment', data)

// 获取客户端用户订单列表
export const getAppUserOrders = (params) => request.get('/order/app-user/list', { params })

// 获取客户端用户订单详情
export const getAppUserOrderDetail = (id) => request.get(`/order/app-user/${id}`)

// 用户取消订单
export const userCancelOrder = (id, reason) => request.put(`/order/app-user/cancel/${id}`, null, { params: { reason } })

// 获取公告列表
export const getNotices = (params) => request.get('/notice/public/list', { params })

// 获取公告详情
export const getNoticeDetail = (id) => request.get(`/notice/public/${id}`)

// 登录
export const login = (data) => request.post('/auth/login', data)

// 获取用户信息
export const getUserInfo = () => request.get('/auth/info')

// 客户端用户头像上传
export const uploadAppUserAvatar = (id, formData) => request.post(`/app-user/${id}/avatar`, formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})

// 客户端用户修改密码
export const changeAppUserPassword = (id, data) => request.post(`/app-user/${id}/change-password`, data)

// 客户端用户修改用户名
export const changeAppUsername = (data) => request.put('/app-auth/username', data)

// 客户端用户修改密码（新接口）
export const changeAppPassword = (data) => request.post('/app-auth/change-password', data)

// 客户端用户上传头像（新接口）
export const uploadAppAvatar = (formData) => request.post('/app-auth/avatar', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})

// ===== 消息管理 =====
export const getAppUserMessages = (params) => request.get('/message/app-user/list', { params })
export const getAppUserUnreadCount = () => request.get('/message/app-user/unread-count')
export const markAppUserMessageRead = (messageId) => request.put(`/message/app-user/${messageId}/read`)
export const markAllAppUserMessagesRead = () => request.put('/message/app-user/all/read')
export const deleteAppUserMessage = (messageId) => request.delete(`/message/app-user/${messageId}`)

// ===== 用药管理 =====
export const getMedicationPlansByElderlyId = (elderlyId) => request.get(`/medication-plan/elderly/${elderlyId}`)
export const getMedicationRecordsByElderlyId = (elderlyId) => request.get(`/medication-record/elderly/${elderlyId}`)

// ===== 紧急呼叫 =====
export const createEmergencyCall = (data) => request.post('/emergency-call', data)

// ===== 活动管理 =====
export const getAllActivities = () => request.get('/activity/all')
export const getActivityById = (id) => request.get(`/activity/${id}`)

// ===== 活动报名 =====
export const signupActivity = (activityId, elderlyId, elderlyName) => request.post('/activity-signup', null, { params: { activityId, elderlyId, elderlyName } })
export const getActivitySignupsByElderlyId = (elderlyId) => request.get(`/activity-signup/elderly/${elderlyId}`)
export const cancelActivitySignup = (id) => request.put(`/activity-signup/cancel/${id}`)

// ===== 费用账单 =====
export const getFeeBillsByElderlyId = (elderlyId) => request.get(`/fee-bill/elderly/${elderlyId}`)

// ===== 支付管理 =====
export const getPaymentsByElderlyId = (elderlyId) => request.get(`/payment/elderly/${elderlyId}`)
export const createPayment = (data) => request.post('/payment', data)
