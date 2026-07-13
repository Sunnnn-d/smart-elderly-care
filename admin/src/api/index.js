import request from './request'

// 注册
export const register = (data) => request.post('/auth/register', data)

// 登录
export const login = (data) => request.post('/auth/login', data)

// 获取用户信息
export const getUserInfo = () => request.get('/auth/info')

// ===== 老人档案 =====
export const getElderlyList = (params) => request.get('/elderly/list', { params })
export const addElderly = (data) => request.post('/elderly', data)
export const updateElderly = (data) => request.put('/elderly', data)
export const deleteElderly = (id) => request.delete(`/elderly/${id}`)

// ===== 服务项目 =====
export const getServiceItemList = (params) => request.get('/service-item/list', { params })
export const addServiceItem = (data) => request.post('/service-item', data)
export const updateServiceItem = (data) => request.put('/service-item', data)
export const deleteServiceItem = (id) => request.delete(`/service-item/${id}`)

// ===== 服务订单 =====
export const getOrderList = (params) => request.get('/order/list', { params })
export const getOrderDetail = (id) => request.get(`/order/${id}`)
export const dispatchOrder = (data) => request.put('/order/dispatch', data)
export const completeOrder = (id) => request.put(`/order/complete/${id}`)
export const cancelOrder = (id, reason) => request.put(`/order/cancel/${id}`, null, { params: { reason } })

// ===== 健康监测 =====
export const getHealthList = (params) => request.get('/health/list', { params })
export const addHealthRecord = (data) => request.post('/health', data)

// ===== 护理计划 =====
export const getCarePlanList = (params) => request.get('/care-plan/list', { params })
export const addCarePlan = (data) => request.post('/care-plan', data)
export const updateCarePlan = (data) => request.put('/care-plan', data)
export const deleteCarePlan = (id) => request.delete(`/care-plan/${id}`)

// ===== 公告管理 =====
export const getNoticeList = (params) => request.get('/notice/list', { params })
export const addNotice = (data) => request.post('/notice', data)
export const updateNotice = (data) => request.put('/notice', data)
export const deleteNotice = (id) => request.delete(`/notice/${id}`)

// ===== 轮播图 =====
export const getBannerList = () => request.get('/banner/list')
export const addBanner = (data) => request.post('/banner', data)
export const updateBanner = (data) => request.put('/banner', data)
export const deleteBanner = (id) => request.delete(`/banner/${id}`)

// ===== 系统用户管理 =====
export const getUserList = () => request.get('/user/list')
export const getUserPage = (params) => request.get('/user/page', { params })
export const getUserById = (id) => request.get(`/user/${id}`)
export const addUser = (data) => request.post('/user', data)
export const updateUser = (id, data) => request.put(`/user/${id}`, data)
export const deleteUser = (id) => request.delete(`/user/${id}`)
export const deleteUserBatch = (ids) => request.delete('/user/batch', { data: ids })
export const resetUserPassword = (id) => request.post(`/user/${id}/reset-password`)
export const uploadUserAvatar = (id, formData) => request.post(`/user/${id}/avatar`, formData, { headers: { 'Content-Type': 'multipart/form-data' } })
export const changeUserPassword = (id, data) => request.post(`/user/${id}/change-password`, data)
export const changeUsername = (data) => request.put('/auth/username', data)
export const uploadAvatar = (formData) => request.post('/auth/avatar', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
export const changePassword = (data) => request.post('/auth/change-password', data)
export const getNurseList = () => request.get('/user/nurses')

// ===== 消息管理 =====
export const getAdminMessages = (params) => request.get('/message/admin/list', { params })
export const getAdminUnreadCount = () => request.get('/message/admin/unread-count')
export const markAdminMessageRead = (messageId) => request.put(`/message/admin/${messageId}/read`)
export const markAllAdminMessagesRead = () => request.put('/message/admin/all/read')
export const deleteAdminMessage = (messageId) => request.delete(`/message/admin/${messageId}`)
export const sendMessageToUser = (data) => request.post('/message/send-to-user', data)
export const sendSystemMessage = (data) => request.post('/message/send-system', data)
export const getMessageTypes = () => request.get('/message/types')

// ===== 客户端用户管理 =====
export const getAppUserList = () => request.get('/app-user/list')
export const getAppUserPage = (params) => request.get('/app-user/page', { params })
export const getAppUserById = (id) => request.get(`/app-user/${id}`)
export const addAppUser = (data) => request.post('/app-user', data)
export const updateAppUser = (id, data) => request.put(`/app-user/${id}`, data)
export const deleteAppUser = (id) => request.delete(`/app-user/${id}`)
export const deleteAppUserBatch = (ids) => request.delete('/app-user/batch', { data: ids })
export const resetAppUserPassword = (id) => request.post(`/app-user/${id}/reset-password`)
export const uploadAppUserAvatar = (id, formData) => request.post(`/app-user/${id}/avatar`, formData, { headers: { 'Content-Type': 'multipart/form-data' } })
export const changeAppUserPassword = (id, data) => request.post(`/app-user/${id}/change-password`, data)

// ===== 评价 =====
export const getEvaluationByOrder = (orderId) => request.get(`/evaluation/order/${orderId}`)

// ===== 角色管理 =====
export const listRole = (pageNum, pageSize, keyword) => request.get('/role/list', { params: { pageNum, pageSize, keyword } })
export const getAllRoles = () => request.get('/role/all')
export const addRole = (data) => request.post('/role/add', data)
export const updateRole = (data) => request.put('/role/update', data)
export const deleteRole = (id) => request.delete(`/role/delete/${id}`)
