// 服务类别图标映射
export const serviceIconMap = {
  '助餐': 'ShoppingCart',
  '保洁': 'Bell',
  '护理': 'FirstAidKit',
  '康复': 'Edit',
  '陪伴': 'UserFilled',
  '其他': 'Document'
}

// 获取服务图标
export const getServiceIcon = (category) => {
  return serviceIconMap[category] || 'Document'
}

// 资讯类型名称映射
export const noticeTypeNameMap = {
  1: '公告通知',
  2: '健康知识',
  3: '活动通知'
}

// 获取资讯类型名称
export const getNoticeTypeName = (type) => {
  return noticeTypeNameMap[type] || '其他'
}

// 资讯类型标签映射
export const noticeTypeTagMap = {
  1: 'warning',
  2: 'success',
  3: 'primary'
}

// 获取资讯类型标签
export const getNoticeTypeTag = (type) => {
  return noticeTypeTagMap[type] || 'info'
}

// 格式化手机号
export const formatPhone = (phone) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})(\d{4})(\d{4})/, '$1****$3')
}

// 格式化日期
export const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 格式化时间
export const formatDateTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 防抖函数
export const debounce = (fn, delay = 300) => {
  let timer = null
  return function (...args) {
    clearTimeout(timer)
    timer = setTimeout(() => fn.apply(this, args), delay)
  }
}

// 节流函数
export const throttle = (fn, delay = 500) => {
  let lastTime = 0
  return function (...args) {
    const now = Date.now()
    if (now - lastTime >= delay) {
      lastTime = now
      fn.apply(this, args)
    }
  }
}