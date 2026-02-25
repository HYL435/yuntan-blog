import http from '@/api/http'

export const postComment = (data: FormData | Record<string, any>) => {
  // If data is FormData, let browser set Content-Type
  return http.post('/front/comments', data)
}

export const getComments = (articleId: string | number) => {
  return http.get(`/front/comments/${articleId}`)
}

export const getCommentCount = (articleId: string | number) => {
  return http.get(`/front/comments/${articleId}/count`)
}

export default { postComment, getComments }
