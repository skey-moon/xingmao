import request from './request'

export const getDeliveryList = (params) => {
  return request.get('/delivery/list', { params })
}

export const assignDelivery = (data) => {
  return request.post('/delivery/assign', data)
}

export const updateDeliveryStatus = (data) => {
  return request.put('/delivery/status', data)
}

export const getDeliveryPersons = () => {
  return request.get('/delivery/persons')
}

export const getAvailableDeliveryPersons = () => {
  return request.get('/delivery/persons/available')
}

export const autoAssignDelivery = (orderId) => {
  return request.post('/delivery/auto-assign/' + orderId)
}

// 配送员管理
export const addDeliveryPerson = (data) => {
  return request.post('/delivery/person', data)
}

export const updateDeliveryPerson = (data) => {
  return request.put('/delivery/person', data)
}

export const deleteDeliveryPerson = (id) => {
  return request.delete('/delivery/person/' + id)
}

export const getDeliveryPerson = (id) => {
  return request.get('/delivery/person/' + id)
}