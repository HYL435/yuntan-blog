<template>
  <div class="p-4 bg-white dark:bg-[#1e293b] rounded-lg shadow-sm transition-colors duration-300">
    <div class="mb-4 flex justify-between items-center">
      <h2 class="text-xl font-bold text-gray-800 dark:text-white">文章管理</h2>
    </div>

    <el-form :inline="true" :model="queryParams" class="mb-4 flex flex-wrap gap-2" @submit.prevent>
      <el-form-item label="标题">
        <el-input v-model="queryParams.title" placeholder="请输入标题" clearable class="!w-64" />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="queryParams.category" placeholder="请选择分类" clearable class="!w-40">
          <el-option v-for="cat in categories" :key="cat.id || cat.name" :label="cat.name || cat.title" :value="cat.name || cat.title" />
        </el-select>
      </el-form-item>
      <el-form-item label="是否原创">
        <el-select v-model="queryParams.isOriginal" placeholder="全部" clearable class="!w-32">
          <el-option label="是" :value="1" />
          <el-option label="否" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable class="!w-32">
          <el-option label="草稿" :value="0" />
          <el-option label="已发布" :value="1" />
          <el-option label="私密" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" border stripe style="width:100%" v-loading="loading"
      :header-cell-style="{ background: isDark ? '#1e293b' : '#f8fafc', color: isDark ? '#e2e8f0' : '#475569' }"
      @row-contextmenu="handleRowContextmenu">
      <el-table-column label="序号" width="80" align="center">
        <template #default="{ row, $index }">
          <div>
            <span style="display:none" :data-id="row.id"></span>
            {{ (queryParams.current - 1) * queryParams.size + ($index + 1) }}
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="coverImg" label="封面" width="120" align="center">
        <template #default="{ row }">
          <el-image :src="row.coverImg" style="width:72px; height:48px; object-fit:cover" :preview-src-list="[row.coverImg]" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="240" show-overflow-tooltip />
      <el-table-column prop="category" label="分类" width="140" />
      <el-table-column prop="tags" label="标签" min-width="160">
        <template #default="{ row }">
          <div class="flex flex-wrap gap-1">
            <el-tag v-for="(t, i) in (row.tags || [])" :key="i" size="small">{{ t }}</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="isOriginal" label="原创" width="80" align="center">
        <template #default="{ row }">{{ row.isOriginal === 1 ? '是' : '否' }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="{ row }">{{ statusText(row.status) }}</template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览" width="90" />
      <el-table-column prop="likeCount" label="点赞" width="90" />
      <el-table-column prop="publishTime" label="发布时间" width="180" />
      <el-table-column prop="updateTime" label="更新时间" width="180" />
    </el-table>

    <!-- 右键操作迷你悬浮框 -->
    <div
      v-if="contextMenuVisible"
      ref="menuRef"
      :style="contextMenuStyle"
      class="z-50 bg-white dark:bg-[#0f172a] border rounded shadow-md py-1 w-40"
      @click.stop
    >
      <div class="flex flex-col text-sm">
        <button class="flex items-center gap-2 px-3 py-2 hover:bg-gray-100 dark:hover:bg-[#0b1220] rounded text-gray-700 dark:text-gray-200" @click="onContextEdit">编辑文章</button>
        <button class="flex items-center gap-2 px-3 py-2 hover:bg-gray-100 dark:hover:bg-[#0b1220] rounded text-gray-700 dark:text-gray-200" @click="onContextToggleTop">{{ isContextRowTop ? '取消置顶' : '置顶文章' }}</button>
        <button class="flex items-center gap-2 px-3 py-2 hover:bg-gray-100 dark:hover:bg-[#0b1220] rounded text-gray-700 dark:text-gray-200" @click="onContextOpenStatusDialog">修改状态</button>
        <button class="flex items-center gap-2 px-3 py-2 hover:bg-gray-100 dark:hover:bg-[#0b1220] rounded text-red-600" @click="onContextDelete">删除文章</button>
      </div>
    </div>

    <!-- 修改状态对话框 -->
    <el-dialog v-model="statusDialogVisible" title="修改文章状态" width="360px" destroy-on-close>
      <el-form :model="{ status: statusDialogStatus }">
        <el-form-item label="状态">
          <el-select v-model="statusDialogStatus" placeholder="请选择状态" class="w-full">
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="私密" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStatusChange">确定</el-button>
      </template>
    </el-dialog>

    <div class="mt-6 flex justify-end">
      <el-pagination background layout="total, sizes, prev, pager, next, jumper" :total="total"
        v-model:current-page="queryParams.current" v-model:page-size="queryParams.size" :page-sizes="[10,20,50]"
        @size-change="handleSizeChange" @current-change="handlePageChange" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '@/api/http'

type ArticleAdmin = {
  id?: number
  title?: string
  coverImg?: string
  isOriginal?: number
  status?: number
  category?: string
  tags?: string[]
  likeCount?: number
  commentCount?: number
  collectCount?: number
  viewCount?: number
  publishTime?: string
  createTime?: string
  updateTime?: string
}

const loading = ref(false)
const tableData = ref<ArticleAdmin[]>([])
const total = ref(0)
const isDark = computed(() => document.documentElement.classList.contains('dark'))

const queryParams = reactive({ current: 1, size: 10, title: '', category: '', isOriginal: undefined as number | undefined, status: undefined as number | undefined })

// 分类列表，从前端公开接口获取
const categories = ref<Array<{ id?: number; name?: string; title?: string }>>([])

const fetchCategories = async () => {
  try {
    const res = await http.get('/front/categories')
    const body = res.data || res
    const data = body.data || body
    // 兼容返回数组或 { data: [...] }
    const list = Array.isArray(data) ? data : (data.list || data.records || [])
    categories.value = list.map((it: any) => ({ id: it.id, name: it.name || it.title || it.category || it.categoryName }))
  } catch (err) {
    console.warn('获取分类失败', err)
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      pageNo: queryParams.current,
      pageSize: queryParams.size,
      title: queryParams.title || undefined,
      category: queryParams.category || undefined,
      isOriginal: queryParams.isOriginal,
      status: queryParams.status
    }
    const res = await http.get('/admin/articles/page', { params })
    const responseBody = res.data || res
    const dataPayload = responseBody.data || responseBody
    const records = dataPayload.records || dataPayload.list || dataPayload.rows || []
    const totalCount = dataPayload.total || dataPayload.totalCount || records.length || 0
    // 按更新时间降序排序，确保最新更新的文章排在首位
    records.sort((a: any, b: any) => {
      const ta = a?.updateTime ? Date.parse(String(a.updateTime)) : 0
      const tb = b?.updateTime ? Date.parse(String(b.updateTime)) : 0
      return tb - ta
    })
    tableData.value = records
    total.value = Number(totalCount)
  } catch (err: any) {
    console.error('获取文章列表失败', err)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.current = 1
  fetchList()
}
const handleReset = () => {
  queryParams.title = ''
  queryParams.category = ''
  queryParams.status = undefined
  handleSearch()
}
const handleSizeChange = (val: number) => {
  queryParams.size = val
  handleSearch()
}
const handlePageChange = (val: number) => {
  queryParams.current = val
  fetchList()
}

const statusText = (s: number | undefined) => {
  if (s === 0) return '草稿'
  if (s === 1) return '已发布'
  if (s === 2) return '私密'
  return '-'
}

onMounted(() => {
  fetchList()
  fetchCategories()
  document.addEventListener('click', _docClickHandler)
})
onUnmounted(() => {
  document.removeEventListener('click', _docClickHandler)
})

// 右键菜单相关
const menuRef = ref<HTMLElement | null>(null)
const contextMenuVisible = ref(false)
const contextMenuRow = ref<ArticleAdmin | null>(null)
const contextMenuStyle = reactive<Record<string, string>>({ left: '0px', top: '0px', position: 'fixed' })

const hideContextMenu = () => {
  contextMenuVisible.value = false
  contextMenuRow.value = null
}

const _docClickHandler = () => hideContextMenu()

const handleRowContextmenu = (row: ArticleAdmin, _column: any, event: MouseEvent) => {
  event.preventDefault()
  event.stopPropagation()
  contextMenuRow.value = row
  let left = event.clientX
  let top = event.clientY
  contextMenuStyle.left = `${left}px`
  contextMenuStyle.top = `${top}px`
  contextMenuVisible.value = true
  nextTick(() => {
    try {
      const menuEl = menuRef.value as HTMLElement | null
      if (!menuEl) return
      const mRect = menuEl.getBoundingClientRect()
      const padding = 8
      if (left + mRect.width + padding > window.innerWidth) {
        left = Math.max(padding, window.innerWidth - mRect.width - padding)
      }
      if (top + mRect.height + padding > window.innerHeight) {
        top = Math.max(padding, window.innerHeight - mRect.height - padding)
      }
      left = Math.max(padding, left)
      top = Math.max(padding, top)
      contextMenuStyle.left = `${left}px`
      contextMenuStyle.top = `${top}px`
    } catch (err) {
      // ignore
    }
  })
}

const router = useRouter()

// 计算当前右键行是否已置顶
const isContextRowTop = computed(() => {
  const row = contextMenuRow.value as any
  if (!row) return false
  const t = row.top ?? row.isTop ?? 0
  return Number(t) === 1
})

const onContextEdit = () => {
  if (!contextMenuRow.value) return
  router.push({ path: '/admin/articles/edit', query: { id: String(contextMenuRow.value.id) } }).catch(() => {})
  hideContextMenu()
}

const onContextToggleTop = async () => {
  if (!contextMenuRow.value) return
  const row = contextMenuRow.value
  // 按要求：发送当前置顶状态（当前没有置顶则发送 0，当前置顶则发送 1）
  const currentTop = Number((row as any).top ?? (row as any).isTop ?? 0)
  try {
    await http.put('/admin/articles/top', null, { params: { id: String(row.id), top: String(currentTop) } })
    ElMessage.success('置顶操作已提交')
    fetchList()
  } catch (err: any) {
    ElMessage.error(err?.message || '置顶操作失败')
  } finally {
    hideContextMenu()
  }
}

// 修改状态对话框
const statusDialogVisible = ref(false)
const statusDialogStatus = ref<number | null>(null)
const statusDialogRow = ref<ArticleAdmin | null>(null)

const onContextOpenStatusDialog = () => {
  if (!contextMenuRow.value) return
  statusDialogRow.value = contextMenuRow.value
  statusDialogStatus.value = contextMenuRow.value.status ?? 0
  statusDialogVisible.value = true
}

const submitStatusChange = async () => {
  if (!statusDialogRow.value) return
  try {
    await http.put('/admin/articles/status', null, { params: { id: String(statusDialogRow.value.id), status: String(statusDialogStatus.value) } })
    ElMessage.success('状态修改成功')
    statusDialogVisible.value = false
    fetchList()
  } catch (err: any) {
    ElMessage.error(err?.message || '修改状态失败')
  }
  hideContextMenu()
}

const onContextDelete = async () => {
  if (!contextMenuRow.value) return
  const row = contextMenuRow.value
  ElMessageBox.confirm(
    `确定要删除文章 "${row.title}" 吗？`,
    '警告',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  ).then(async () => {
    try {
      await http.put(`/admin/articles/delete/${row.id}`)
      ElMessage.success('删除成功')
      if (tableData.value.length === 1 && queryParams.current > 1) queryParams.current--
      fetchList()
    } catch (error: any) {
      ElMessage.error(error?.message || '删除失败')
    }
  })
  hideContextMenu()
}


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
