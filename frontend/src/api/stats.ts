import http from './http'

export interface AdminStatsResponse {
  // 通用结构：x 轴标签，series 为多个数据列
  x?: string[];
  series?: { name: string; data: number[] }[];
  // 兼容性：某些后端直接返回多个命名数组
  dates?: string[];
  visits?: number[];
  posts?: number[];
  users?: number[];
}

export const getAdminStats = async (): Promise<AdminStatsResponse> => {
  const res = await http.get('/admin/stats/overview')
  return res.data || {}
}

export default {
  getAdminStats
}
