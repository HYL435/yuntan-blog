# 登录功能实现总结

## 已完成的功能

### 1. ✅ API 层 (`src/api/`)

**http.ts** - HTTP 请求实例配置

- 创建 axios 实例，基础 URL 为 `http://localhost:9000`
- **请求拦截器**：自动从 `localStorage` 获取 token，添加到 `Authorization` 请求头
- **响应拦截器**：处理 401 未授权错误，清除 token 并跳转到登录页

**auth.ts** - 认证 API 接口

- `loginApi()` - 调用后端登录接口 `POST /front/users/login`
- 支持用户名/邮箱 + 密码登录
- 返回用户信息和 token

### 2. ✅ 状态管理 (`src/stores/user.ts`)

使用 Pinia 管理用户状态：

```typescript
// 可用属性和方法
user; // 用户信息对象
isLoggedIn; // 是否已登录（计算属性）
token; // 存储的 token
login(); // 登录函数
logout(); // 登出函数
restoreLogin(); // 恢复登录状态
```

核心逻辑：

- 登录成功后，响应的 token 自动添加 `Bearer ` 前缀后保存到 `localStorage`
- 用户信息保存到 Pinia store

### 3. ✅ 登录页面 (`src/views/LoginView.vue`)

- 集成了用户认证逻辑
- 支持邮箱或用户名登录
- 登录时禁用按钮，显示加载状态
- 登录成功后自动跳转到首页
- 错误信息通过 ElMessage 消息框和页面提示显示

### 4. ✅ 项目配置

**main.ts** - 集成 Pinia 到 Vue 应用
**vite.config.ts** - 配置开发服务器代理
**.env.local** - 环境变量配置

## Token 存储格式

| 场景       | 存储位置      | 格式                     | 示例                                            |
| ---------- | ------------- | ------------------------ | ----------------------------------------------- |
| 登录成功后 | localStorage  | `Bearer {token}`         | `Bearer eyJhbGciOiJIUzI1NiIs...`                |
| API 请求头 | Authorization | 自动从 localStorage 读取 | `Authorization: Bearer eyJhbGciOiJIUzI1NiIs...` |

## 请求拦截过程

```javascript
// 当发送任何 API 请求时：
1. 请求拦截器触发
2. 从 localStorage 读取 'auth_token'
3. 将其添加到 Authorization 请求头
4. 发送请求

例如：
const response = await http.get('/front/articles');
// 实际请求头：Authorization: Bearer {token}
```

## 响应错误处理

```javascript
// 当响应返回 401 Unauthorized 时
1. 响应拦截器触发
2. 删除 localStorage 中的 auth_token
3. 重定向到 /login 页面
4. 用户需要重新登录
```

## 如何在其他组件中使用

### 获取用户信息

```vue
<script setup>
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();

// 获取用户信息
console.log(userStore.user?.nickname); // 用户昵称
console.log(userStore.isLoggedIn); // 是否登录
console.log(userStore.token); // Bearer token

// 执行登出
const handleLogout = () => {
  userStore.logout();
  router.push("/login");
};
</script>
```

### 发送 API 请求

```typescript
import http from "@/api/http";

// 所有请求都会自动带上 token
const getArticles = async () => {
  try {
    const response = await http.get("/front/articles");
    console.log(response.data);
  } catch (error) {
    // 400/401/500 等错误会在这里被捕获
  }
};
```

## 与后端对接检查清单

- [ ] 后端提供登录接口 `POST /front/users/login`
- [ ] 后端验证前端发送的 `username`/`email` 和 `password`
- [ ] 后端返回格式如下：
  ```json
  {
    "code": 200,
    "message": "成功",
    "data": {
      "id": 用户ID,
      "nickname": "用户昵称",
      "image": "头像URL",
      "role": 角色代码,
      "status": 状态代码,
      "token": "jwt_token"
    }
  }
  ```
- [ ] 后端在返回的 token 中**不包含** `Bearer ` 前缀（前端会自动添加）
- [ ] 后端验证 Authorization 请求头中的 Bearer token 是否有效
- [ ] 后端在 token 无效时返回 401 状态码

## 调试技巧

1. **查看保存的 token**
   - 打开浏览器开发者工具 > Application/Storage > Local Storage
   - 查找 `auth_token` 的值

2. **查看请求头**
   - 打开浏览器开发者工具 > Network
   - 点击任何 API 请求
   - 在 Request Headers 中查看 Authorization 字段

3. **检查登录状态**
   - 安装 Vue DevTools 浏览器扩展
   - 打开 Pinia tab
   - 查看 user store 的状态

4. **测试 token 过期**
   - 手动删除 localStorage 中的 `auth_token`
   - 刷新页面或发送请求
   - 应该自动跳转到登录页

## 文件清单

| 文件                      | 描述                   |
| ------------------------- | ---------------------- |
| `src/api/http.ts`         | HTTP 实例和拦截器配置  |
| `src/api/auth.ts`         | 登录 API 接口定义      |
| `src/api/index.ts`        | API 导出               |
| `src/stores/user.ts`      | Pinia 用户状态管理     |
| `src/views/LoginView.vue` | 登录页面组件           |
| `src/main.ts`             | 应用入口（集成 Pinia） |
| `vite.config.ts`          | Vite 配置（开发代理）  |
| `.env.local`              | 本地环境变量           |
| `.env.example`            | 环境变量示例           |
| `LOGIN_GUIDE.md`          | 详细使用文档           |

---

🎉 登录功能已完全实现！可以开始测试了。
