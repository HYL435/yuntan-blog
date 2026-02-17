# 登录功能实现指南

## 功能概述

已完成以下登录相关功能的实现：

1. **API 服务层** - 调用后端登录接口
2. **Pinia 状态管理** - 管理用户信息和登录状态
3. **HTTP 请求拦截器** - 自动在所有请求中携带 Bearer Token
4. **登录页面** - 完整的登录表单和验证
5. **本地存储** - 将 Token 保存到浏览器 localStorage

## 项目结构

```
src/
├── api/
│   ├── http.ts           # HTTP 实例和拦截器配置
│   ├── auth.ts          # 认证相关 API
│   └── index.ts         # API 导出
├── stores/
│   └── user.ts          # 用户状态管理（Pinia）
├── views/
│   └── LoginView.vue    # 登录页面
└── main.ts              # 应用入口（已集成 Pinia）
```

## 核心功能说明

### 1. HTTP 拦截器 (`src/api/http.ts`)

- **请求拦截器**：自动从 localStorage 中获取 token，并在 Authorization 请求头中添加 Bearer 前缀
- **响应拦截器**：处理 401 未授权错误，清除本地 token 并重定向到登录页

```typescript
// 请求时自动添加 token
const token = localStorage.getItem("auth_token");
if (token) {
  config.headers.Authorization = token; // Bearer {token}
}
```

### 2. API 服务 (`src/api/auth.ts`)

定义了登录接口：

```typescript
interface LoginRequest {
  username?: string;
  email?: string;
  password: string;
}

interface LoginResponse {
  code: number;
  message: string;
  data: {
    id: number;
    nickname: string;
    image: string;
    role: number;
    status: number;
    token: string;
  };
}

export const loginApi = (params: LoginRequest) => {
  return http.post<LoginResponse>("/front/users/login", params);
};
```

### 3. 用户状态管理 (`src/stores/user.ts`)

Pinia store 管理用户信息：

```typescript
const login = async (credentials: LoginRequest) => {
  const response = await loginApi(credentials);
  const { code, data } = response.data;

  if (code === 200) {
    // 保存用户信息
    user.value = {
      /* ... */
    };

    // 保存 token 到 localStorage（带 Bearer 前缀）
    const bearerToken = `Bearer ${data.token}`;
    localStorage.setItem("auth_token", bearerToken);

    return { success: true, message: "登录成功" };
  }
};
```

### 4. 登录页面 (`src/views/LoginView.vue`)

集成了 Pinia store 和路由：

```typescript
const handleSubmit = async () => {
  const loginResult = await userStore.login({
    username: isEmail ? undefined : account,
    email: isEmail ? account : undefined,
    password: password.value,
  });

  if (loginResult.success) {
    ElMessage.success("登录成功");
    router.push("/"); // 跳转到首页
  }
};
```

## 环境配置

### 开发环境 (`.env.local`)

```
VITE_API_URL=http://localhost:9000
```

### 代理配置 (`vite.config.ts`)

已配置 Vite 开发服务器代理，将 `/front` 路由代理到 `http://localhost:9000`

## 数据流处理

### 登录成功流程

```
用户填写表单
    ↓
验证表单数据
    ↓
调用 userStore.login()
    ↓
发送 POST /front/users/login 请求
    ↓
后端返回 token 和用户信息
    ↓
保存 token 到 localStorage（格式：Bearer {token}）
    ↓
保存用户信息到 Pinia store
    ↓
显示成功提示
    ↓
跳转到首页
```

### 之后的请求流程

```
发送任何 API 请求
    ↓
请求拦截器执行
    ↓
从 localStorage 获取 token
    ↓
在 Authorization 请求头中添加 token
    ↓
发送请求
    ↓
接收响应
    ↓
如果是 401：清除 token，跳转到登录页
```

## 使用示例

### 在其他组件中使用用户信息

```typescript
import { useUserStore } from "@/stores/user";

export default {
  setup() {
    const userStore = useUserStore();

    // 获取用户信息
    console.log(userStore.user); // { id, nickname, image, role, status }
    console.log(userStore.isLoggedIn); // true/false
    console.log(userStore.token); // Bearer {token}

    // 登出
    const handleLogout = () => {
      userStore.logout();
      router.push("/login");
    };

    return { handleLogout };
  },
};
```

### 在其他地方调用 API

```typescript
import http from "@/api/http";

// 调用其他接口，token 会自动在请求头中
http.get("/front/articles").then((response) => {
  console.log(response.data);
});

// 或者使用自定义的 API 函数
import { someApi } from "@/api/article";

someApi().then((response) => {
  console.log(response.data);
});
```

## 与后端接口对接

### 后端需要提供的接口

1. **登录接口**
   - URL: `POST /front/users/login`
   - 请求体:
     ```json
     {
       "username": "k8qscpiui81",
       "password": "0JJJXnBB74951J9"
     }
     // 或者
     {
       "email": "user@example.com",
       "password": "0JJJXnBB74951J9"
     }
     ```
   - 响应格式:
     ```json
     {
       "code": 200,
       "message": "成功",
       "data": {
         "id": 1,
         "nickname": "用户昵称",
         "image": "头像URL",
         "role": 0,
         "status": 1,
         "token": "jwt_token_string"
       }
     }
     ```

### 关键点

1. **Token 格式**：后端返回的 token 会被添加 `Bearer ` 前缀后存储到 localStorage
2. **请求头**：后续所有请求的 Authorization 请求头格式为 `Bearer {token}`
3. **Token 过期**：如果返回 401，会自动清除 token 并跳转到登录页

## 常见问题

### Q: 如何禁用某个请求的 token 拦截器？

A: 在 `src/api/http.ts` 中修改请求拦截器，为特定的请求路由添加判断。

### Q: 如何刷新页面后保持登录状态？

A: Token 已保存在 localStorage，刷新页面后仍然存在。如需恢复用户信息，可在 `stores/user.ts` 中调用获取用户信息的 API。

### Q: 登录失败时如何获取错误信息？

A: 后端返回的 `message` 字段会通过 ElMessage 显示，并保存在 `errorMessage` 状态中显示在页面上。

## 调试建议

1. 在浏览器开发者工具的 Storage > localStorage 中查看是否成功保存 token
2. 在 Network 标签中查看请求头是否包含 Authorization
3. 在 Vue DevTools 中查看 Pinia store 的用户状态
