import request from './request'

export const getDashboardStats = () => {
  return request.get('/stat/dashboard')
}

export const getStatData = (params) => {
  return request.get('/stat/dashboard', { params })
}

export const getTransactionList = (params) => {
  return request.get('/transaction/list', { params })
}

export const getTransactionSummary = () => {
  return request.get('/transaction/summary')
}