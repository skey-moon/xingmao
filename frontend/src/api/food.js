import request from './request'

export const getFoodList = (params) => {
  return request.get('/food/list', { params })
}

export const addFood = (data) => {
  return request.post('/food', data)
}

export const updateFood = (data) => {
  return request.put('/food', data)
}

export const getFoodCategories = () => {
  return request.get('/food/categories')
}

export const deleteFood = (id) => {
  return request.delete('/food/' + id)
}

export const uploadFoodImage = (formData) => {
  return request.post('/food/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}