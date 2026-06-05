import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  const role = ref(localStorage.getItem('role') || '')
  const userId = ref(localStorage.getItem('userId') || '')

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    if (info) {
      role.value = info.role || ''
      userId.value = info.userId || ''
      localStorage.setItem('role', role.value)
      localStorage.setItem('userId', userId.value)
    }
  }

  const setLoginResult = (result) => {
    token.value = result.token
    role.value = result.role
    userId.value = result.userId
    localStorage.setItem('token', result.token)
    localStorage.setItem('role', result.role)
    localStorage.setItem('userId', result.userId)
    userInfo.value = {
      userId: result.userId,
      username: result.username,
      nickname: result.nickname,
      role: result.role
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    role.value = ''
    userId.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('userId')
  }

  return {
    token,
    userInfo,
    role,
    userId,
    setToken,
    setUserInfo,
    setLoginResult,
    logout
  }
})