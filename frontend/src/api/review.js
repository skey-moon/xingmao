import request from './request'

export const getReviewByOrderId = (orderId) => {
  return request.get('/review/order/' + orderId)
}

export const createReview = (data) => {
  return request.post('/review', data)
}

export const getMyReviews = (params) => {
  return request.get('/review/my', { params })
}