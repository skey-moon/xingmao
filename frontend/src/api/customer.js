import request from './request'

export const getCustomerStats = () => {
  return request.get('/customer/stats')
}

export const getRecentOrders = (params) => {
  return request.get('/customer/recent-orders', { params })
}