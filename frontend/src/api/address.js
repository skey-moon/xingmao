import request from './request'

export const getMyAddresses = () => {
  return request.get('/address')
}

export const addAddress = (data) => {
  return request.post('/address', data)
}

export const updateAddress = (id, data) => {
  return request.put('/address/' + id, data)
}

export const deleteAddress = (id) => {
  return request.delete('/address/' + id)
}

export const setDefaultAddress = (id) => {
  return request.put('/address/default/' + id)
}