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

// 管理后台：分页获取评论列表
export const listCommentsAdmin = (params: Record<string, any>) => {
  return http.get('/admin/comments', { params })
}

// 管理后台：修改评论状态
export const updateCommentStatus = (data: { id: string | number; status: number }) => {
  return http.put('/admin/comments/status', data)
}

// 管理后台：删除评论
export const deleteComment = (id: string | number) => {
  return http.put(`/admin/comments/deleted/${id}`)
}

export default { postComment, getComments, getCommentCount, listCommentsAdmin, updateCommentStatus, deleteComment }
