<template>
  <div class="p-4 bg-white dark:bg-[#1e293b] rounded-lg shadow-sm transition-colors duration-300 relative">
    <!-- 顶部工具栏 -->
    <div class="mb-4 flex justify-between items-center">
      <h2 class="text-xl font-bold text-gray-800 dark:text-white">用户管理</h2>
      <div>
        <el-button type="primary" :icon="Plus" @click="handleCreate">新增用户</el-button>
      </div>
    </div>

    <!-- 查询表单 -->
    <el-form :inline="true" :model="queryParams" class="mb-4 flex flex-wrap gap-2" @submit.prevent>
      <el-form-item label="用户名">
        <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable class="!w-40" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="queryParams.email" placeholder="请输入邮箱" clearable class="!w-40" />
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="queryParams.role" placeholder="全部" clearable class="!w-32">
          <el-option label="博主" :value="0" />
          <el-option label="管理员" :value="1" />
          <el-option label="访客" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable class="!w-32">
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 用户表格 -->
    <el-table 
      :data="tableData" 
      border 
      stripe 
      style="width: 100%" 
      v-loading="loading"
      :header-cell-style="{ background: isDark ? '#1e293b' : '#f8fafc', color: isDark ? '#e2e8f0' : '#475569' }"
      @row-contextmenu="handleRowContextmenu"
    >
      <el-table-column prop="image" label="头像" width="80" align="center">
        <template #default="scope">
          <el-avatar :size="40" :src="scope.row.image || scope.row.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
        </template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" min-width="120" show-overflow-tooltip />
      <el-table-column prop="nickname" label="昵称" min-width="120" show-overflow-tooltip />
      <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
      <el-table-column prop="phone" label="手机号" min-width="140" show-overflow-tooltip />
      <el-table-column prop="intro" label="简介" min-width="200" show-overflow-tooltip />
      <el-table-column prop="role" label="角色" width="100" align="center">
        <template #default="scope">
          <el-tag :type="getRoleTagType(scope.row.role)">
            {{ getRoleName(scope.row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" effect="dark">
            {{ scope.row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="180" sortable />
      <!-- 操作已移至右键悬浮菜单 -->
    </el-table>

    <!-- 右键操作迷你悬浮框 -->
    <div
      v-if="contextMenuVisible"
      ref="menuRef"
      :style="contextMenuStyle"
      class="z-50 bg-white dark:bg-[#0f172a] border rounded shadow-md py-1 w-36"
      @click.stop
    >
      <div class="flex flex-col text-sm">
        <button class="flex items-center gap-2 px-3 py-2 hover:bg-gray-100 dark:hover:bg-[#0b1220] rounded text-gray-700 dark:text-gray-200" @click="onContextToggleEnable">
          {{ contextMenuRow?.status === 1 ? '禁用账户' : '启用账户' }}
        </button>
        <button class="flex items-center gap-2 px-3 py-2 hover:bg-gray-100 dark:hover:bg-[#0b1220] rounded text-gray-700 dark:text-gray-200" @click="onContextEdit">编辑用户</button>
        <button class="flex items-center gap-2 px-3 py-2 hover:bg-gray-100 dark:hover:bg-[#0b1220] rounded text-gray-700 dark:text-gray-200" @click="onContextOpenRoleDialog">修改角色</button>
        <button class="flex items-center gap-2 px-3 py-2 hover:bg-gray-100 dark:hover:bg-[#0b1220] rounded text-red-600" @click="onContextDelete">删除</button>
      </div>
    </div>

    <!-- 修改角色对话框 -->
    <el-dialog v-model="roleDialogVisible" title="修改角色" width="360px" destroy-on-close>
      <el-form :model="{ role: roleDialogRole }">
        <el-form-item label="角色">
          <el-select v-model="roleDialogRole" placeholder="请选择角色" class="w-full">
            <el-option label="博主" :value="0" />
            <el-option label="管理员" :value="1" />
            <el-option label="访客" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRoleChange">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分页 -->
    <div class="mt-6 flex justify-end">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :page-sizes="[10, 20, 50, 100]"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 新增/编辑 弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogType === 'create' ? '新增用户' : '编辑用户'" 
      width="500px"
      destroy-on-close
    >
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" :disabled="dialogType === 'edit'" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="formData.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role" class="w-full">
            <el-option label="博主" :value="0" />
            <el-option label="管理员" :value="1" />
            <el-option label="访客" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- 仅在新增时显示密码框 -->
        <el-form-item label="密码" prop="password" v-if="dialogType === 'create'">
          <el-input v-model="formData.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, computed, nextTick, type CSSProperties } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import http from '@/api/http'

// --- 状态定义 ---
// 后端用户类型（对应 AdminUserVO）
type AdminUser = {
  id?: number
  username?: string
  nickname?: string
  email?: string
  phone?: string
  image?: string
  intro?: string
  role?: number
  status?: number
  deleted?: number
  lastLoginTime?: string
  createTime?: string
  updateTime?: string
}

type AdminUserForm = AdminUser & { password?: string }
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref<'create' | 'edit'>('create')
const isDark = computed(() => document.documentElement.classList.contains('dark'))

// 表格数据
const tableData = ref<AdminUser[]>([])
const total = ref(0)

// 右键菜单状态
const menuRef = ref<HTMLElement | null>(null)
const contextMenuVisible = ref(false)
const contextMenuRow = ref<AdminUser | null>(null)
const contextMenuStyle = reactive<CSSProperties>({ left: '0px', top: '0px', position: 'fixed' })

const hideContextMenu = () => {
  contextMenuVisible.value = false
  contextMenuRow.value = null
}

const handleRowContextmenu = (row: AdminUser, _column: any, event: MouseEvent) => {
  event.preventDefault()
  event.stopPropagation()
  contextMenuRow.value = row
  // 基于视口定位，先使用鼠标客户端坐标，然后在 DOM 渲染后调整以确保不超出视口
  let left = event.clientX
  let top = event.clientY
  contextMenuStyle.left = `${left}px`
  contextMenuStyle.top = `${top}px`
  contextMenuVisible.value = true
  // 等待菜单渲染，然后修正位置（防止溢出视口）
  nextTick(() => {
    try {
      const menuEl = menuRef.value as HTMLElement | null
      if (!menuEl) return
      const mRect = menuEl.getBoundingClientRect()
      const padding = 8
      // 右侧溢出
      if (left + mRect.width + padding > window.innerWidth) {
        left = Math.max(padding, window.innerWidth - mRect.width - padding)
      }
      // 底部溢出
      if (top + mRect.height + padding > window.innerHeight) {
        top = Math.max(padding, window.innerHeight - mRect.height - padding)
      }
      // 左/上边界
      left = Math.max(padding, left)
      top = Math.max(padding, top)
      contextMenuStyle.left = `${left}px`
      contextMenuStyle.top = `${top}px`
    } catch (err) {
      // ignore
    }
  })
}

const onContextEdit = () => {
  if (contextMenuRow.value) handleEdit(contextMenuRow.value)
  hideContextMenu()
}

const onContextDelete = () => {
  if (contextMenuRow.value) handleDelete(contextMenuRow.value)
  hideContextMenu()
}

// 启用/禁用账户
const onContextToggleEnable = async () => {
  if (!contextMenuRow.value) return
  const row = contextMenuRow.value
  // 后端约定：1 = 启用，0 = 禁用
  // 按要求：发送当前状态值（当前禁用则发送 '0'，当前启用则发送 '1'）
  const newStatus = String(row.status ?? 0)
  try {
    // 使用后端提供的禁用/启用接口：PUT /admin/users/status?id=...&status=...
    await http.put('/admin/users/status', null, { params: { id: String(row.id), status: newStatus } })
    ElMessage.success(row.status === 1 ? '账户当前已启用' : '账户当前已禁用')
    fetchList()
  } catch (err: any) {
    ElMessage.error(err?.message || '修改状态失败')
  } finally {
    hideContextMenu()
  }
}

// 打开修改角色对话框
const roleDialogVisible = ref(false)
const roleDialogRole = ref<number | null>(null)
const roleDialogRow = ref<AdminUser | null>(null)

const onContextOpenRoleDialog = () => {
  if (!contextMenuRow.value) return
  roleDialogRow.value = contextMenuRow.value
  roleDialogRole.value = contextMenuRow.value.role ?? 2
  roleDialogVisible.value = true
}

const submitRoleChange = async () => {
  if (!roleDialogRow.value) return
  try {
    // 按后端 DTO 要求，调用 /admin/users/upgrade 传递 { id, role }
    await http.put('/admin/users/upgrade', { id: roleDialogRow.value.id, role: roleDialogRole.value })
    ElMessage.success('角色修改成功')
    roleDialogVisible.value = false
    fetchList()
  } catch (err: any) {
    ElMessage.error(err?.message || '修改角色失败')
  }
  hideContextMenu()
}

// 查询参数 (current/size 是前端习惯，发请求时会转成 pageNo/pageSize)
const queryParams = reactive({
  current: 1,
  size: 10,
  username: '',
  email: '',
  role: undefined,
  status: undefined
})

// 表单数据
const formRef = ref<FormInstance>()
const formData = reactive<AdminUserForm>({
  id: undefined,
  username: '',
  nickname: '',
  email: '',
  phone: '',
  image: '',
  intro: '',
  password: '',
  role: 2,
  status: 1,
  deleted: 0,
  lastLoginTime: undefined,
  createTime: undefined,
  updateTime: undefined
})

// 校验规则
const rules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
})

// --- 核心方法 ---

// 1. 获取列表 (已增强健壮性)
const fetchList = async () => {
  loading.value = true
  try {
    // 构造请求参数
    const params = {
      pageNo: queryParams.current,
      pageSize: queryParams.size,
      username: queryParams.username || undefined,
      email: queryParams.email || undefined,
      role: queryParams.role,
      status: queryParams.status
    }

    console.log('🚀 发起请求:', params)

    const res = await http.get('/admin/users/page', { params })

    console.log('📦 收到响应:', res)

    // 数据解包逻辑：
    // 1. 先尝试获取后端返回的整个 JSON 对象 (通常是 res.data，如果拦截器已处理则直接是 res)
    const responseBody = res.data || res
    
    // 2. 尝试从 { code: 200, data: { ... } } 结构中取出 data 部分
    const dataPayload = responseBody.data || responseBody

    // 3. 从 dataPayload 中取出列表和总数
    // 兼容 MyBatis-Plus (records), PageHelper (list), 或直接是数组的情况
    const records = dataPayload.records || dataPayload.list || dataPayload.rows || []
    const totalCount = dataPayload.total || dataPayload.totalCount || records.length || 0

    tableData.value = records
    total.value = Number(totalCount)

  } catch (error: any) {
    console.error('❌ 获取列表失败:', error)
    ElMessage.error(error.message || '获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 2. 搜索与重置
const handleSearch = () => {
  queryParams.current = 1
  fetchList()
}

const handleReset = () => {
  queryParams.username = ''
  queryParams.email = ''
  queryParams.role = undefined
  queryParams.status = undefined
  handleSearch()
}

// 3. 分页处理
const handleSizeChange = (val: number) => {
  queryParams.size = val
  handleSearch() // 改变大小后通常回到第一页，handleSearch 里已经做了 current=1
}

const handlePageChange = (val: number) => {
  queryParams.current = val
  fetchList()
}

// 4. 打开新增弹窗
const handleCreate = () => {
  dialogType.value = 'create'
  Object.assign(formData, {
    id: undefined,
    username: '',
    nickname: '',
    email: '',
    password: '',
    role: 2,
    status: 1,
    avatar: ''
  })
  dialogVisible.value = true
  if (formRef.value) formRef.value.clearValidate()
}

// 5. 打开编辑弹窗
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  Object.assign(formData, JSON.parse(JSON.stringify(row)))
  // 编辑模式通常不需要密码校验规则，或者在这里动态移除规则，这里简化处理
  dialogVisible.value = true
  if (formRef.value) formRef.value.clearValidate()
}

// 6. 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (dialogType.value === 'create') {
          await http.post('/admin/users', formData)
          ElMessage.success('新增成功')
        } else {
          // 编辑时不传密码
          const { password, ...updateData } = formData
          await http.put('/admin/users', updateData)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        fetchList()
      } catch (error: any) {
        console.error(error)
        ElMessage.error(error.message || '操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 7. 删除用户
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    `确定要删除用户 "${row.username}" 吗？`,
    '警告',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      // 使用后端提供的删除接口：PUT /admin/users/delete/{id}
      await http.put(`/admin/users/delete/${row.id}`)
      ElMessage.success('删除成功')
      // 优化：如果删的是最后一页的最后一条，向前翻页
      if (tableData.value.length === 1 && queryParams.current > 1) {
        queryParams.current--
      }
      fetchList()
    } catch (error: any) {
      ElMessage.error(error.message || '删除失败')
    }
  })
}

// 工具函数
const getRoleName = (role: number) => {
  const map: Record<number, string> = { 0: '博主', 1: '管理员', 2: '访客' }
  return map[role] || '未知'
}
const getRoleTagType = (role: number) => {
  const map: Record<number, string> = { 0: 'danger', 1: 'warning', 2: 'info' }
  return map[role] || 'info'
}

onMounted(() => {
  fetchList()
  // 点击空白处关闭右键菜单
  const onDocClick = () => hideContextMenu()
  document.addEventListener('click', onDocClick)
  onUnmounted(() => {
    document.removeEventListener('click', onDocClick)
  })
})
</script>

<style scoped>
.dark :deep(.el-table) {
  --el-table-tr-bg-color: #1e293b;
  --el-table-header-bg-color: #1e293b;
  --el-table-border-color: #334155;
  background-color: #1e293b;
  color: #e2e8f0;
}
</style>