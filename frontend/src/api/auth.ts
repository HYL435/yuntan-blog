import http from './http';

export interface LoginRequest {
  username?: string;
  email?: string;
  password: string;
}

/**
 * 用户信息响应数据类型
 */
export interface UserData {
  id: number;
  username: string;
  nickname: string;
  image: string;
  role: number;
  status: number;
  email?: string;
  phone?: string;
  intro?: string;
}

/**
 * 登录响应
 */
export interface LoginResponse {
  code: number;
  message: string;
  data: UserData & { token: string };
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
}

export interface RegisterResponse {
  code: number;
  message: string;
  data?: {
    id: number;
    username: string;
    email: string;
  };
}

/**
 * 用户注册 API
 */
export const registerApi = (params: RegisterRequest) => {
  return http.post<RegisterResponse>('/front/users/register', params);
};

/**
 * 获取登录 API
 */
export const loginApi = (params: LoginRequest) => {
  return http.post<LoginResponse>('/front/users/login', params);
};

/**
 * 获取用户信息 API（根据用户 ID）
 */
export const getUserProfileApi = (userId: number) => {
  return http.get<{ code: number; message: string; data: UserData }>(`/front/users/${userId}`);
};

/**
 * 更新用户信息 API
 */
export interface UpdateUserRequest {
  id: number;
  nickname?: string;
  email?: string;
  phone?: string;
  image?: string | File; // 支持 URL 字符串或文件对象
  intro?: string;
  password?: string;
}

export const updateUserProfileApi = (params: UpdateUserRequest | FormData) => {
  // 添加防御性检查
  if (!params) {
    throw new Error('更新参数不能为空');
  }

  // 如果是 FormData，直接发送（不设置 Content-Type，让浏览器自动处理 boundary）
  if (params instanceof FormData) {
    console.log('发送 FormData 请求');
    return http.put<{ code: number; message: string; data: UserData }>('/front/users', params, {
      headers: {
        'Content-Type': undefined, // 删除默认 Content-Type，让浏览器自动设置
      },
    } as any);
  }

  // 现在 params 肯定是 UpdateUserRequest（不是 FormData）
  const updateRequest = params as UpdateUserRequest;

  // 验证必要字段
  if (!updateRequest || !updateRequest.id) {
    throw new Error('用户 ID 不能为空');
  }

  console.log('准备发送更新请求，image 类型:', typeof updateRequest.image, '是否为 File:', updateRequest.image instanceof File);

  // 如果包含 File 对象，转换为 FormData
  if (updateRequest.image instanceof File) {
    console.log('检测到文件对象，转换为 FormData');
    const formData = new FormData();
    formData.append('id', String(updateRequest.id));
    formData.append('nickname', String(updateRequest.nickname || ''));
    formData.append('email', String(updateRequest.email || ''));
    formData.append('phone', String(updateRequest.phone || ''));
    formData.append('imageFile', updateRequest.image); // 后端期望的字段名是 imageFile
    formData.append('intro', String(updateRequest.intro || ''));
    formData.append('password', String(updateRequest.password || ''));

    return http.put<{ code: number; message: string; data: UserData }>('/front/users', formData, {
      headers: {
        'Content-Type': undefined, // 删除默认 Content-Type，让浏览器自动设置
      },
    } as any);
  }

  // 否则作为 JSON 发送（图片为 URL 字符串或空字符串）
  console.log('发送 JSON 请求');
  return http.put<{ code: number; message: string; data: UserData }>('/front/users', updateRequest);
};

/**
 * 退出登录 API
 */
export const logoutApi = () => {
  return http.post<{ code: number; message: string }>('/front/users/logout', {});
};

/**
 * 忘记密码：请求发送重置密码邮件或验证码
 */
export const forgotPasswordApi = (email: string) => {
  return http.post<{ code: number; message: string }>('/front/users/forgot', { email });
};

/**
 * 发送重置验证码到邮箱（6位验证码）
 */
export const sendResetCodeApi = (email: string) => {
  // 后端接口为 @PostMapping("/emailCode") 并使用 @RequestParam String email
  // 通过 params 将 email 附加到查询字符串，避免使用 JSON body（@RequestParam 更稳定）
  return http.post<{ code: number; message: string }>('/front/users/emailCode', null, { params: { email } });
};

/**
 * 使用验证码重置密码
 */
export const resetPasswordApi = (params: { email: string; code: string; newPassword: string }) => {
  return http.post<{ code: number; message: string }>('/front/users/forgot/reset', params);
};
