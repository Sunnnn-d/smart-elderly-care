import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

const pendingRequests = new Map()

const getRequestKey = (config) => {
  return `${config.method}_${config.url}_${JSON.stringify(config.params)}`
}

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    const key = getRequestKey(config)
    if (pendingRequests.has(key)) {
      pendingRequests.get(key).cancel()
    }
    const source = axios.CancelToken.source()
    config.cancelToken = source.token
    pendingRequests.set(key, source)
    return config
  },
  error => Promise.reject(error)
)

request.interceptors.response.use(
  response => {
    const key = getRequestKey(response.config)
    pendingRequests.delete(key)
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    if (axios.isCancel(error)) {
      return Promise.reject(error)
    }
    const key = getRequestKey(error.config)
    pendingRequests.delete(key)
    if (error.response) {
      if (error.response.status === 401) {
        ElMessage.error('登录已过期，请重新登录')
        localStorage.removeItem('token')
        window.location.href = '/login'
      } else {
        ElMessage.error(error.response.data?.message || '服务器错误')
      }
    } else {
      ElMessage.error('网络连接失败')
    }
    return Promise.reject(error)
  }
)

export const cancelRequest = (config) => {
  const key = getRequestKey(config)
  if (pendingRequests.has(key)) {
    pendingRequests.get(key).cancel()
    pendingRequests.delete(key)
  }
}

export const cancelAllRequests = () => {
  pendingRequests.forEach(source => source.cancel())
  pendingRequests.clear()
}

export default request
