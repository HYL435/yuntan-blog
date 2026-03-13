import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { loginApi, getUserProfileApi, updateUserProfileApi, type LoginRequest, type UpdateUserRequest } from '@/api/auth';

export interface User {
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

export const useUserStore = defineStore('user', () => {
  const user = ref<User | null>(null);
  const isLoggedIn = computed(() => !!user.value);
  const token = ref<string>('');

  // 登录
  const login = async (credentials: LoginRequest) => {
    try {
      const response = await loginApi(credentials);
      const { code, message, data } = response.data;

      if (code === 200) {
        // 保存用户信息
        user.value = {
          id: data.id,
          username: data.username,
          nickname: data.nickname,
          image: data.image,
          role: data.role,
          status: data.status,
          email: data.email,
          phone: data.phone,
          intro: data.intro,
        };

        // 保存 token，添加 Bearer 前缀
        const bearerToken = `Bearer ${data.token}`;
        token.value = bearerToken;
        localStorage.setItem('auth_token', bearerToken);
        
        // 同时保存用户信息到 localStorage（用于页面刷新恢复）
        localStorage.setItem('user_info', JSON.stringify(user.value));

        return { success: true, message: '登录成功' };
      } else {
        return { success: false, message: message || '登录失败' };
      }
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || error.message || '登录请求失败';
      return { success: false, message: errorMessage };
    }
  };

  // 登出
  const logout = () => {
    user.value = null;
    token.value = '';
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_info');
  };

  // 从本地存储恢复登录状态
  const restoreLogin = async () => {
    try {
      const savedToken = localStorage.getItem('auth_token');
      const savedUserInfo = localStorage.getItem('user_info');

      if (!savedToken) {
        return; // 没有保存的 token，直接返回
      }

      // 设置 token
      token.value = savedToken;

      // 优先使用本地保存的用户信息（快速恢复显示）
      if (savedUserInfo) {
        try {
          user.value = JSON.parse(savedUserInfo);
        } catch (e) {
          console.warn('Failed to parse saved user info:', e);
        }
      }

      // 从 API 获取最新用户信息（异步更新）
      try {
        if (user.value) {
          const response = await getUserProfileApi(user.value.id);
          const { code, data } = response.data;

          if (code === 200) {
            user.value = {
              id: data.id,
              username: data.username,
              nickname: data.nickname,
              image: data.image,
              role: data.role,
              status: data.status,
              email: data.email,
              phone: data.phone,
              intro: data.intro,
            };
            // 更新本地保存的用户信息
            localStorage.setItem('user_info', JSON.stringify(user.value));
          }
        }
      } catch (error) {
        console.warn('Failed to restore user profile from API:', error);
        // API 失败时，如果本地有保存的用户信息，就保持使用
        // 否则清除登录状态
        if (!savedUserInfo) {
          logout();
        }
      }
    } catch (error) {
      console.error('Error restoring login state:', error);
    }
  };

  // 获取用户信息（刷新）
  const getUserProfile = async () => {
    if (!user.value) {
      return { success: false };
    }
    
    try {
      const response = await getUserProfileApi(user.value.id);
      const { code, data } = response.data;

      console.log('getUserProfile - 后端返回的完整数据:', response.data)
      console.log('getUserProfile - data 对象:', data)
      console.log('getUserProfile - email 字段:', data.email)

      if (code === 200) {
        user.value = {
          id: data.id,
          username: data.username,
          nickname: data.nickname,
          image: data.image,
          role: data.role,
          status: data.status,
          email: data.email,
          phone: data.phone,
          intro: data.intro,
        };
        console.log('getUserProfile - 保存后的 user.value:', user.value)
        // 更新本地存储
        localStorage.setItem('user_info', JSON.stringify(user.value));
        return { success: true };
      } else {
        // 错误码非 200，可能是令牌过期或其他问题
        return { success: false };
      }
    } catch (error: any) {
      console.error('Failed to get user profile:', error);
      // 如果是 401 未授权错误（令牌过期），清除登录状态
      if (error.response?.status === 401) {
        logout();
      }
      return { success: false };
    }
  };

  // 更新用户信息
  const updateProfile = async (updates: UpdateUserRequest) => {
    try {
      console.log('updateProfile - 发送的更新数据:', updates);
      const response = await updateUserProfileApi(updates);
      
      console.log('updateProfile - 完整响应:', response);
      console.log('updateProfile - 响应数据:', response?.data);

      if (!response || !response.data) {
        console.error('updateProfile - 响应为空或格式错误');
        return { success: false, message: '服务器响应格式错误' };
      }

      const { code, message, data } = response.data;
      const msg = (response.data as any).msg as string | undefined;

      console.log('updateProfile - 提取的数据:', { code, msg, message, data });

      if (code === 200) {
        // 如果后端返回了 data，直接更新；否则需要重新获取
        if (data && data.id) {
          console.log('updateProfile - 使用后端返回的数据直接更新');
          if (user.value) {
            user.value = {
              id: data.id,
              username: data.username || user.value.username,
              nickname: data.nickname,
              image: data.image,
              role: data.role,
              status: data.status,
              email: data.email,
              phone: data.phone,
              intro: data.intro,
            };
            localStorage.setItem('user_info', JSON.stringify(user.value));
            console.log('updateProfile - 本地数据已更新:', user.value);
          }
        } else {
          // 后端没有返回 data，需要重新获取用户信息
          console.log('updateProfile - 后端未返回数据，重新获取用户信息');
          if (user.value && user.value.id) {
            try {
              const profileResponse = await getUserProfileApi(user.value.id);
              const { code: profileCode, data: profileData } = profileResponse.data;
              
              if (profileCode === 200 && profileData) {
                user.value = {
                  id: profileData.id,
                  username: profileData.username,
                  nickname: profileData.nickname,
                  image: profileData.image,
                  role: profileData.role,
                  status: profileData.status,
                  email: profileData.email,
                  phone: profileData.phone,
                  intro: profileData.intro,
                };
                localStorage.setItem('user_info', JSON.stringify(user.value));
                console.log('updateProfile - 重新获取的用户信息:', user.value);
              }
            } catch (error) {
              console.warn('updateProfile - 重新获取用户信息失败:', error);
            }
          }
        }
        return { success: true, message: '更新成功' };
      } else {
        return { success: false, message: msg || message || '更新失败' };
      }
    } catch (error: any) {
      console.error('updateProfile - 捕获到异常:', error);
      console.error('updateProfile - 错误详情:', {
        message: error.message,
        response: error.response?.data,
        status: error.response?.status,
      });
      const errorMessage = error.response?.data?.message || error.message || '更新请求失败';
      return { success: false, message: errorMessage };
    }
  };

  return {
    user,
    isLoggedIn,
    token,
    login,
    logout,
    restoreLogin,
    getUserProfile,
    updateProfile,
  };
});
