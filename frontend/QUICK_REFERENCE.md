# 登录功能快速参考卡片

## 核心文件概览

### 1️⃣ HTTP 拦截器 (`src/api/http.ts`)

```typescript
// ✅ 在这里配置
baseURL: "http://localhost:9000";

// ✅ 请求拦截器自动：
Authorization: localStorage.getItem("auth_token");
// 结果：Authorization: "Bearer eyJhbGciOiJIUzI1NiIs..."

// ✅ 响应拦截器自动：
if (error.response?.status === 401) {
  localStorage.removeItem("auth_token");
  window.location.href = "/login";
}
```

### 2️⃣ 登录 API (`src/api/auth.ts`)

```typescript
// 调用后端
POST /front/users/login
Content-Type: application/json

{
  "username": "k8qscpiui81",
  "password": "0JJJXnBB74951J9"
}

// 接收响应
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "nickname": "用户昵称",
    "image": "头像URL",
    "role": 0,
    "status": 1,
    "token": "jwt_token"
  }
}
```

### 3️⃣ 用户存储 (`src/stores/user.ts`)

```typescript
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();

// 登录
await userStore.login({
  username: "user",
  password: "pass",
});

// 获取状态
userStore.user; // { id, nickname, image, role, status }
userStore.token; // "Bearer eyJhbGci..."
userStore.isLoggedIn; // true/false

// 登出
userStore.logout();
```

### 4️⃣ 登录页面 (`src/views/LoginView.vue`)

```vue
<script setup>
import { useUserStore } from "@/stores/user";
import { useRouter } from "vue-router";

const userStore = useUserStore();
const router = useRouter();

const handleSubmit = async () => {
  const result = await userStore.login({
    username: "user",
    password: "pass",
  });

  if (result.success) {
    router.push("/"); // 跳转首页
  }
};
</script>
```

## localStorage 数据格式

| Key          | Value        | 示例                                             |
| ------------ | ------------ | ------------------------------------------------ |
| `auth_token` | Bearer token | `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...` |

## 请求/响应流程

```
用户操作
  ↓
form 验证
  ↓
userStore.login(credentials)
  ↓
http.post('/front/users/login', credentials)
  ↓
[请求拦截器] 添加 Authorization 请求头
  ↓
后端验证处理
  ↓
[响应拦截器] 检查状态码
  ↓
成功 → 保存 token + 用户信息 → 跳转
失败 → 显示错误信息
```

## 常用操作

### 检查登录状态

```typescript
const userStore = useUserStore();
if (userStore.isLoggedIn) {
  console.log("已登录:", userStore.user);
} else {
  console.log("未登录");
}
```

### 发送需要认证的请求

```typescript
import http from '@/api/http';

// token 会自动添加到请求头
const response = await http.get('/front/articles');
const response = await http.post('/front/order', { ... });
```

### 处理登出

```typescript
const userStore = useUserStore();
const router = useRouter();

const logout = () => {
  userStore.logout(); // 清除状态 + localStorage
  router.push("/login");
};
```

### 在元素 Plus 中显示错误

```typescript
import { ElMessage } from "element-plus";

const loginResult = await userStore.login(credentials);
if (!loginResult.success) {
  ElMessage.error(loginResult.message);
}
```

## 调试命令

打开浏览器控制台，复制粘贴以下命令：

```javascript
// 查看 token
localStorage.getItem("auth_token");

// 查看所有 localStorage
console.log(localStorage);

// 清除 token（测试无认证状态）
localStorage.removeItem("auth_token");

// 模拟请求
fetch("http://localhost:9000/front/articles", {
  headers: {
    Authorization: localStorage.getItem("auth_token"),
  },
})
  .then((r) => r.json())
  .then((d) => console.log(d));
```

## 状态码说明

| 代码 | 含义                | 处理                 |
| ---- | ------------------- | -------------------- |
| 200  | 成功                | 保存 token，跳转首页 |
| 400  | 请求参数错误        | 显示错误信息         |
| 401  | 未授权或 token 过期 | 清除 token，跳转登录 |
| 500  | 服务器错误          | 显示错误信息         |

## 后端需要做的事

✅ 提供 `POST /front/users/login` 接口
✅ 验证 username/email 和 password
✅ 生成 JWT token
✅ 返回指定格式的响应（见上方 API 格式）
✅ 验证 Authorization 请求头中的 token 有效性
✅ token 无效时返回 401 状态码

## 常见错误排查

| 问题                   | 排查方法                                                 |
| ---------------------- | -------------------------------------------------------- |
| 登录后 token 没有保存  | 检查 localStorage，应该有 `auth_token`                   |
| 后续请求没有 token     | 在 Network 标签查看 Authorization 请求头                 |
| 请求总是返回 401       | 检查后端是否正确验证 Bearer token                        |
| 页面刷新后登录状态丢失 | 正常现象，token 仍在 localStorage 但用户信息需要重新获取 |

## 文件修改清单

下面这些文件已修改或创建：

- ✅ `src/api/http.ts` - 新建
- ✅ `src/api/auth.ts` - 新建
- ✅ `src/api/index.ts` - 新建
- ✅ `src/stores/user.ts` - 新建
- ✅ `src/views/LoginView.vue` - 已修改
- ✅ `src/main.ts` - 已修改
- ✅ `vite.config.ts` - 已修改
- ✅ `.env.local` - 新建
- ✅ `.env.example` - 新建

---

📚 详细文档请查看 [LOGIN_GUIDE.md](LOGIN_GUIDE.md)
