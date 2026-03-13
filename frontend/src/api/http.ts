import axios, { AxiosInstance, AxiosResponse } from 'axios';

// 创建 axios 实例
// 开发环境将所有请求前缀为 /api（由 Vite 代理转发到后端），生产环境使用绝对 URL
const baseURL = (import.meta.env.MODE === 'development') ? '/api' : ((import.meta.env as any).VITE_API_URL || 'https://yuntan-blog.top');

const http: AxiosInstance = axios.create({
  baseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
http.interceptors.request.use(
  (config) => {
    // 从本地存储获取 token 并添加到请求头
    const token = localStorage.getItem('auth_token');
    try {
      // 将 token 注入到请求头，并打印调试信息，方便定位拦截器是否生效
      if (token) {
        ;(config.headers as any).Authorization = token;
        ;(config.headers as any)['authorization'] = token;
      }
      // 如果请求体是 FormData，删除默认 Content-Type，让浏览器自动设置带 boundary 的 multipart header
      try {
        if (config.data && typeof FormData !== 'undefined' && config.data instanceof FormData) {
          if (config.headers) {
            delete (config.headers as any)['Content-Type'];
            delete (config.headers as any)['content-type'];
          }
        }
      } catch (e) {}

      console.debug('[http] 请求:', config.method, config.url, 'HasAuthorization=', !!token)
      // 如果是 /front/comments 相关请求，打印调用栈帮助定位谁发起了 GET /front/comments
      try {
        if (typeof config.url === 'string' && config.url.includes('/front/comments')) {
          console.groupCollapsed('[http] 调试 /front/comments 调用栈]')
          console.trace()
          console.groupEnd()
        }
      } catch (e) {}
    } catch (err) {
      // 忽略日志错误，继续请求
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
http.interceptors.response.use(
  (response: AxiosResponse) => {
    // 调试响应，方便排查后端返回与前端解析不一致的问题
    try {
      console.debug('[http] 响应:', response.config?.url, response.status)
    } catch (err) {}
    return response;
  },
  (error) => {
    // 只在某些特定情况下处理 401 错误
    // 一般来说，获取文章等公开操作不需要登录，所以不自动重定向
    // 如果需要在某些页面处理 401，由业务逻辑决定
    if (error.response?.status === 401) {
      // token 过期或无效，只清除本地存储，不自动重定向
      console.warn('[http] 401 响应，移除 auth_token')
      localStorage.removeItem('auth_token');
    }
    return Promise.reject(error);
  }
);

export default http;
