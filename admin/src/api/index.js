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

// ===== 用药管理 =====
export const getMedicationPlanList = (params) => request.get('/medication-plan/list', { params })
export const addMedicationPlan = (data) => request.post('/medication-plan', data)
export const updateMedicationPlan = (id, data) => request.put(`/medication-plan/${id}`, data)
export const deleteMedicationPlan = (id) => request.delete(`/medication-plan/${id}`)
export const getMedicationPlanById = (id) => request.get(`/medication-plan/${id}`)
export const getMedicationPlansByElderlyId = (elderlyId) => request.get(`/medication-plan/elderly/${elderlyId}`)

export const getMedicationRecordList = (params) => request.get('/medication-record/list', { params })
export const addMedicationRecord = (data) => request.post('/medication-record', data)
export const updateMedicationRecord = (id, data) => request.put(`/medication-record/${id}`, data)
export const getMedicationRecordsByElderlyId = (elderlyId) => request.get(`/medication-record/elderly/${elderlyId}`)
export const getMedicationRecordsByPlanId = (planId) => request.get(`/medication-record/plan/${planId}`)

// ===== 紧急呼叫 =====
export const getEmergencyCallList = (params) => request.get('/emergency-call/list', { params })
export const createEmergencyCall = (data) => request.post('/emergency-call', data)
export const handleEmergencyCall = (id, responderId, responderName) => request.put(`/emergency-call/handle/${id}`, null, { params: { responderId, responderName } })
export const completeEmergencyCall = (id, handleResult) => request.put(`/emergency-call/complete/${id}`, null, { params: { handleResult } })
export const getEmergencyCallsByElderlyId = (elderlyId) => request.get(`/emergency-call/elderly/${elderlyId}`)

// ===== 房间管理 =====
export const getRoomList = (params) => request.get('/room/list', { params })
export const getAllRooms = () => request.get('/room/all')
export const addRoom = (data) => request.post('/room', data)
export const updateRoom = (id, data) => request.put(`/room/${id}`, data)
export const deleteRoom = (id) => request.delete(`/room/${id}`)
export const getRoomById = (id) => request.get(`/room/${id}`)

// ===== 床位管理 =====
export const getBedList = (params) => request.get('/bed/list', { params })
export const getBedsByRoomId = (roomId) => request.get(`/bed/room/${roomId}`)
export const getAvailableBeds = () => request.get('/bed/available')
export const addBed = (data) => request.post('/bed', data)
export const updateBed = (id, data) => request.put(`/bed/${id}`, data)
export const deleteBed = (id) => request.delete(`/bed/${id}`)
export const getBedById = (id) => request.get(`/bed/${id}`)
export const checkInBed = (bedId, elderlyId, elderlyName) => request.put(`/bed/check-in/${bedId}`, null, { params: { elderlyId, elderlyName } })
export const checkOutBed = (bedId, remark) => request.put(`/bed/check-out/${bedId}`, null, { params: { remark } })

// ===== 费用项目 =====
export const getFeeItemList = (params) => request.get('/fee-item/list', { params })
export const getAllFeeItems = () => request.get('/fee-item/all')
export const addFeeItem = (data) => request.post('/fee-item', data)
export const updateFeeItem = (id, data) => request.put(`/fee-item/${id}`, data)
export const deleteFeeItem = (id) => request.delete(`/fee-item/${id}`)
export const getFeeItemById = (id) => request.get(`/fee-item/${id}`)

// ===== 费用账单 =====
export const getFeeBillList = (params) => request.get('/fee-bill/list', { params })
export const getFeeBillById = (id) => request.get(`/fee-bill/${id}`)
export const getFeeBillsByElderlyId = (elderlyId) => request.get(`/fee-bill/elderly/${elderlyId}`)
export const getFeeBillByMonth = (elderlyId, month) => request.get(`/fee-bill/elderly/${elderlyId}/month/${month}`)
export const addFeeBill = (data) => request.post('/fee-bill', data)
export const updateFeeBill = (id, data) => request.put(`/fee-bill/${id}`, data)
export const deleteFeeBill = (id) => request.delete(`/fee-bill/${id}`)

// ===== 支付管理 =====
export const getPaymentList = (params) => request.get('/payment/list', { params })
export const getPaymentById = (id) => request.get(`/payment/${id}`)
export const getPaymentsByElderlyId = (elderlyId) => request.get(`/payment/elderly/${elderlyId}`)
export const getPaymentsByBillId = (billId) => request.get(`/payment/bill/${billId}`)
export const createPayment = (data) => request.post('/payment', data)
export const updatePayment = (id, data) => request.put(`/payment/${id}`, data)

// ===== 活动管理 =====
export const getActivityList = (params) => request.get('/activity/list', { params })
export const getAllActivities = () => request.get('/activity/all')
export const getActivityById = (id) => request.get(`/activity/${id}`)
export const addActivity = (data) => request.post('/activity', data)
export const updateActivity = (id, data) => request.put(`/activity/${id}`, data)
export const deleteActivity = (id) => request.delete(`/activity/${id}`)
export const publishActivity = (id) => request.put(`/activity/publish/${id}`)
export const closeActivity = (id) => request.put(`/activity/close/${id}`)

// ===== 活动报名 =====
export const getActivitySignupList = (params) => request.get('/activity-signup/list', { params })
export const getActivitySignupsByActivityId = (activityId) => request.get(`/activity-signup/activity/${activityId}`)
export const getActivitySignupsByElderlyId = (elderlyId) => request.get(`/activity-signup/elderly/${elderlyId}`)
export const signupActivity = (activityId, elderlyId, elderlyName) => request.post('/activity-signup', null, { params: { activityId, elderlyId, elderlyName } })
export const approveActivitySignup = (id) => request.put(`/activity-signup/approve/${id}`)
export const signInActivity = (id) => request.put(`/activity-signup/sign-in/${id}`)
export const cancelActivitySignup = (id) => request.put(`/activity-signup/cancel/${id}`)
