import http from './http';

export interface CategoryFrontVO {
  id: number | string;
  categoryName: string;
}

// 获取所有分类，返回 CategoryFrontVO 数组
export async function getCategories(): Promise<CategoryFrontVO[]> {
  console.log('准备请求分类接口 /front/categories');
  return http.get('/front/categories').then(res => {
    console.log('分类接口原始响应:', res);
    if (Array.isArray(res.data)) return res.data;
    if (res.data && Array.isArray(res.data.data)) return res.data.data;
    return [];
  });
}
