import axios, { AxiosInstance, AxiosResponse } from 'axios';

// 创建 axios 实例
// 开发环境使用相对路径，生产环境使用绝对 URL
const baseURL = (import.meta.env.MODE === 'development') ? '/' : ((import.meta.env as any).VITE_API_URL || 'http://localhost:9000');

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
    if (token) {
      config.headers.Authorization = token;
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
    return response;
  },
  (error) => {
    // 只在某些特定情况下处理 401 错误
    // 一般来说，获取文章等公开操作不需要登录，所以不自动重定向
    // 如果需要在某些页面处理 401，由业务逻辑决定
    if (error.response?.status === 401) {
      // token 过期或无效，只清除本地存储，不自动重定向
      localStorage.removeItem('auth_token');
      // 由具体的业务逻辑决定是否需要重定向到登录
    }
    return Promise.reject(error);
  }
);

export default http;
