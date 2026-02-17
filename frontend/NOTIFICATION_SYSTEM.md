# 通知系统使用指南

## 功能说明

已成功创建并集成一个自定义通知卡片系统，替代 Element Plus 的 ElMessage。该系统提供：

- ✅ 美观的波浪设计卡片
- ✅ 四种通知类型：`success`（成功）、`error`（错误）、`warning`（警告）、`info`（信息）
- ✅ 完整的深色模式和浅色模式支持
- ✅ 平滑的进入/退出动画
- ✅ 自动消失功能（可自定义时长）
- ✅ 手动关闭按钮
- ✅ 通知队列管理

## 文件结构

```
src/
├── components/
│   ├── common/
│   │   └── NotificationCard.vue          # 单个通知卡片组件
│   └── layout/
│       └── NotificationsContainer.vue    # 通知容器组件（在 App.vue 中使用）
├── composables/
│   └── useNotification.ts                # 通知管理 composable
├── App.vue                               # 已集成通知容器
└── views/
    ├── LoginView.vue                     # 已配置使用新通知系统
    ├── RegisterView.vue                  # 已配置使用新通知系统
    └── components/common/UserProfile.vue # 已配置使用新通知系统
```

## 使用方式

### 在任何组件中使用

```typescript
import { useNotification } from "@/composables/useNotification";

export default {
  setup() {
    const { success, error, warning, info } = useNotification();

    // 显示成功消息
    success("登录成功", "欢迎回来");

    // 显示错误消息
    error("登录失败", "用户名或密码错误");

    // 显示警告消息
    warning("注意", "操作可能会影响数据");

    // 显示信息消息
    info("提示", "这是一条提示信息");

    return { success, error, warning, info };
  },
};
```

### API 方法

#### `success(title, message?, duration?)`

- **title** (string): 通知标题
- **message** (string, 可选): 通知副文本
- **duration** (number, 可选): 自动关闭时间（毫秒，默认 3000ms）

#### `error(title, message?, duration?)`

- 错误类型通知

#### `warning(title, message?, duration?)`

- 警告类型通知

#### `info(title, message?, duration?)`

- 信息类型通知

#### `show(title, options?)`

```typescript
const notificationId = show("标题", {
  type: "success", // 'success' | 'error' | 'warning' | 'info'
  message: "副文本",
  duration: 3000, // 0 表示不自动关闭
});
```

#### `remove(notificationId)`

- 手动移除指定通知

#### `clear()`

- 清除所有通知

## 已配置的集成点

### 1. **登录页面** (LoginView.vue)

- ✅ 登录成功显示绿色成功提示
- ✅ 登录失败显示红色错误提示

### 2. **注册页面** (RegisterView.vue)

- ✅ 注册成功自动登录并显示绿色提示
- ✅ 注册失败显示红色错误提示
- ✅ 自动登录失败显示黄色警告提示

### 3. **用户头像下拉菜单** (UserProfile.vue)

- ✅ 注销成功显示绿色提示

## CSS 变量配置

通知卡片使用 CSS 变量实现主题切换，支持浅色模式和深色模式：

### 浅色模式（默认）

```css
--notif-success-wave: #04e4003a;
--notif-success-icon: #269b24;
--notif-success-bg: #04e40048;
--notif-success-text: #269b24;
--notif-success-subtext: #555;

--notif-error-wave: #ff4d4f3a;
--notif-error-icon: #d9363e;
--notif-error-bg: #ff4d4f48;
--notif-error-text: #d9363e;
--notif-error-subtext: #555;

--notif-warning-wave: #faad143a;
--notif-warning-icon: #d48806;
--notif-warning-bg: #faad1448;
--notif-warning-text: #d48806;
--notif-warning-subtext: #555;

--notif-info-wave: #1890ff3a;
--notif-info-icon: #0050b3;
--notif-info-bg: #1890ff48;
--notif-info-text: #0050b3;
--notif-info-subtext: #555;
```

### 深色模式 (html.dark)

```css
--notif-success-wave: #52c4413a;
--notif-success-icon: #95de64;
/* ... 等等 */
```

## 功能特性

### 1. 波浪背景动画

- 使用 SVG 路径绘制的波浪设计
- 与每个通知类型的颜色相匹配

### 2. 图标

- 四种不同的 SVG 图标
- 成功：对勾
- 错误：双叉
- 警告：感叹号
- 信息：信息符号

### 3. 动画

- **进入**: 从上方逐渐淡入，轻微向下平移
- **退出**: 淡出，向上轻微平移
- **过渡时间**: 300ms

### 4. 位置

- 固定在页面右上角 (top: 20px, right: 20px)
- 移动设备上会自适应 (top: 10px, right: 10px, left: 10px)

### 5. Z-Index

- 固定为 9999，确保始终在顶层显示

## 示例代码

### 登录流程

```typescript
try {
  const loginResult = await userStore.login({
    username: "user123",
    password: "password123",
  });

  if (loginResult.success) {
    success("登录成功");
    setTimeout(() => {
      router.push("/");
    }, 500);
  } else {
    error(loginResult.message);
  }
} catch (errorObj) {
  error("登录失败，请重试");
}
```

### 自定义时长

```typescript
// 显示 5 秒后自动消失
success("操作成功", "数据已保存", 5000);

// 永不自动关闭
info("重要提示", "请仔细阅读", 0);
```

## 升级说明

之前使用 ElMessage 的代码已全部替换为新的通知系统：

- ❌ `ElMessage.success()` → ✅ `success()`
- ❌ `ElMessage.error()` → ✅ `error()`
- ❌ `ElMessage.warning()` → ✅ `warning()`
- ❌ `ElMessage.info()` → ✅ `info()`

所有 Element Plus 消息依赖均已移除。

## 浏览器兼容性

- ✅ Chrome/Edge (最新版本)
- ✅ Firefox (最新版本)
- ✅ Safari (最新版本)
- ✅ 移动浏览器

## 性能考虑

- 通知组件使用异步加载，不会阻止主渲染
- 自动关闭功能使用 setTimeout，不会泄漏内存
- 完整支持界面热更新 (HMR)
