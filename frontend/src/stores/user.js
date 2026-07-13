import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  const isLogin = computed(() => !!token.value && userInfo.value)

  const login = (newToken, info) => {
    token.value = newToken
    userInfo.value = info
    localStorage.setItem('token', newToken)
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  const updateUserInfo = (info) => {
    userInfo.value = { ...userInfo.value, ...info }
  }

  return {
    token,
    userInfo,
    isLogin,
    login,
    logout,
    updateUserInfo
  }
})