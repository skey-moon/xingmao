import request from './request'

export const login = (data) => {
  return request.post('/user/login', data)
}

export const register = (data) => {
  return request.post('/user/register', data)
}

export const getUserList = (params) => {
  return request.get('/user/list', { params })
}

export const addUser = (data) => {
  return request.post('/user', data)
}

export const updateUser = (data) => {
  return request.put('/user', data)
}

export const deleteUser = (id) => {
  return request.delete('/user/' + id)
}