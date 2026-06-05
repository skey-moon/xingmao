import request from './request'

export const getOrderList = (params) => {
  return request.get('/order/list', { params })
}

export const getOrderDetail = (id) => {
  return request.get('/order/' + id)
}

export const getMyOrders = (params) => {
  return request.get('/order/my', { params })
}

export const createOrder = (data) => {
  return request.post('/order', data)
}

export const updateOrderStatus = (data) => {
  return request.put('/order/status', data)
}

export const dispatchOrder = (orderId) => {
  return request.post('/order/dispatch/' + orderId)
}

export const confirmReceipt = (orderId) => {
  return request.post('/order/confirm/' + orderId)
}