<template>
  <div class="p-4 bg-white dark:bg-[#0f172a] rounded-lg shadow-sm transition-colors duration-300">
    <div class="mb-4 flex justify-between items-center">
      <h2 class="text-xl font-bold text-gray-800 dark:text-white">分类管理</h2>
      <div>
        <el-button type="primary" @click="openCreate">新建分类</el-button>
      </div>
    </div>

    <el-table :data="categories" style="width:100%" v-loading="loading"
      :header-cell-style="{ background: isDark ? '#0b1220' : '#f8fafc', color: isDark ? '#e2e8f0' : '#475569' }">
      <el-table-column label="序号" width="80" align="center">
        <template #default="{ $index }">{{ $index + 1 }}</template>
      </el-table-column>
      <el-table-column prop="categoryName" label="分类名称" min-width="220" />
      <el-table-column prop="sort" label="排序" width="120" align="center" />
      <el-table-column prop="createTime" label="创建时间" width="180" align="center">
        <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="180" align="center">
        <template #default="{ row }">{{ formatDate(row.updateTime) }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="140" align="center">
        <template #default="{ row }">
          <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="onToggleStatus(row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center">
        <template #default="{ row }">
          <el-button type="text" size="small" @click="openEdit(row)">编辑</el-button>
          <el-button type="text" size="small" @click="confirmDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="formVisible" :title="isEditing ? '编辑分类' : '新建分类'" width="480px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import http from '@/api/http'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const categories = ref<Array<any>>([])
const formVisible = ref(false)
const isEditing = ref(false)
const formRef = ref()
const form = reactive({ id: undefined as unknown as number | string | undefined, categoryName: '', sort: 0, status: 1 })

const rules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const isDark = computed(() => document.documentElement.classList.contains('dark'))

const formatDate = (v: any) => {
  if (!v && v !== 0) return '-'
  try {
    const d = new Date(String(v))
    if (Number.isNaN(d.getTime())) return '-'
    return d.toLocaleString()
  } catch (e) {
    return '-'
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await http.get('/admin/categories')
    const body = res.data || res
    const data = body.data || body
    const list = Array.isArray(data) ? data : (data.list || data.records || data)
    categories.value = Array.isArray(list) ? list : []
  } catch (err) {
    console.error('获取分类失败', err)
    ElMessage.error('获取分类失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchList()
})

const openCreate = () => {
  isEditing.value = false
  form.id = undefined
  form.categoryName = ''
  form.sort = 0
  form.status = 1
  formVisible.value = true
}

const openEdit = (row: any) => {
  isEditing.value = true
  form.id = row.id
  form.categoryName = row.categoryName || row.name || ''
  form.sort = row.sort ?? 0
  form.status = row.status ?? 1
  formVisible.value = true
}

const submitForm = async () => {
  try {
    // @ts-ignore
    await formRef.value.validate()
  } catch (e) {
    return
  }
  try {
    if (isEditing.value) {
      const payload = { id: form.id, categoryName: form.categoryName, sort: form.sort, status: form.status }
      await http.put('/admin/categories', payload)
      ElMessage.success('更新成功')
    } else {
      const payload = { categoryName: form.categoryName, sort: form.sort, status: form.status }
      await http.post('/admin/categories', payload)
      ElMessage.success('创建成功')
    }
    formVisible.value = false
    fetchList()
  } catch (err: any) {
    console.error('提交分类失败', err)
    ElMessage.error(err?.message || '提交失败')
  }
}

const confirmDelete = (row: any) => {
  ElMessageBox.confirm(`确定删除分类 "${row.categoryName || row.name}" 吗？`, '删除确认', { type: 'warning' })
    .then(async () => {
      try {
        // 使用 PUT /admin/categories/deleted/{id} 接口执行删除（软删除）
        const idStr = String(row.id)
        await http.put(`/admin/categories/deleted/${idStr}`)
        ElMessage.success('删除成功')
        fetchList()
      } catch (err: any) {
        ElMessage.error(err?.message || '删除失败')
      }
    }).catch(() => {})
}

const onToggleStatus = async (row: any) => {
  try {
    const payload = { id: row.id, status: row.status }
    // try a status endpoint first, fallback to PUT
    try {
      await http.put('/admin/categories/status', payload)
    } catch (e) {
      await http.put('/admin/categories', payload)
    }
    ElMessage.success('状态已更新')
    fetchList()
  } catch (err: any) {
    ElMessage.error(err?.message || '更新状态失败')
  }
}

</script>

<style scoped>
:deep(.el-table) {
  background: transparent;
}
</style>
