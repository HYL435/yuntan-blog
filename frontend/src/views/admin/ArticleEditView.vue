<template>
  <div class="h-full flex flex-col bg-gray-50 dark:bg-[#0f172a] transition-colors duration-300">
    <!-- 顶部操作栏 -->
    <header class="h-16 px-6 bg-white dark:bg-[#1e293b] border-b border-gray-200 dark:border-gray-800 flex items-center justify-between z-10 sticky top-0 shrink-0">
      <div class="flex items-center gap-4">
        <el-button circle plain @click="handleCancel">
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <h2 class="text-lg font-bold text-gray-800 dark:text-white">
          {{ isEditMode ? '编辑文章' : '新建文章' }}
        </h2>
        <span v-if="lastSavedTime" class="text-xs text-gray-400 flex items-center gap-1">
          <el-icon><Check /></el-icon> 草稿保存于 {{ lastSavedTime }}
        </span>
      </div>
      <div class="flex gap-3">
        <el-button plain @click="handleSaveDraft">{{ isEditMode ? '保存' : '存草稿' }}</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          <el-icon class="mr-1"><Position /></el-icon> 发布
        </el-button>
      </div>
    </header>

    <!-- 主体内容区：双栏布局 -->
    <div class="flex-1 overflow-hidden flex flex-col md:flex-row">
      
      <!-- 左侧：主要编辑区 -->
      <div class="flex-1 flex flex-col min-h-0 overflow-hidden">
        <div class="flex-1 flex flex-col overflow-y-auto p-4 md:p-6 custom-scroll">
          <div class="max-w-6xl w-full mx-auto flex flex-col h-full space-y-4">
            
            <!-- 标题输入 -->
            <input
              v-model="form.title"
              type="text"
              placeholder="请输入文章标题..."
              class="w-full text-3xl font-bold bg-transparent border-none outline-none placeholder-gray-300 dark:placeholder-gray-600 text-gray-800 dark:text-gray-100 py-2 shrink-0"
            />

            <!-- 真实 Markdown 编辑器 -->
            <!-- 直接撑满剩余高度，提升沉浸感 -->
            <div class="flex-1 border border-gray-200 dark:border-gray-700 rounded-lg overflow-hidden shadow-sm flex flex-col min-h-125">
              <md-editor
                v-model="form.content"
                :theme="isDark ? 'dark' : 'light'"
                language="zh-CN"
                placeholder="开始你的创作..."
                class="flex-1 w-full min-h-125"
                :toolbars="toolbars"
                :show-toolbar="true"
                :autoFocus="false"
                :preview="false"
                @onUploadImg="handleUploadImage"
              />
            </div>

          </div>
        </div>
      </div>

      <!-- 右侧：设置侧边栏 (固定宽度 320px) -->
      <aside class="w-full md:w-80 bg-white dark:bg-[#1e293b] border-l border-gray-200 dark:border-gray-800 overflow-y-auto p-5 space-y-6 shadow-[-4px_0_15px_-3px_rgba(0,0,0,0.05)] shrink-0 custom-scroll">
        <h3 class="font-bold text-gray-700 dark:text-gray-200 mb-4">发布设置</h3>

        <!-- 封面图 -->
        <div class="space-y-2">
          <label class="text-sm font-medium text-gray-600 dark:text-gray-400">封面图</label>
          <div
            class="border-2 border-dashed border-gray-300 dark:border-gray-700 rounded-lg p-4 text-center hover:border-blue-500 transition-colors cursor-pointer relative group h-40 flex items-center justify-center bg-gray-50 dark:bg-gray-800"
            @click="handleCoverUpload"
          >
            <div v-if="!form.coverImg" class="text-gray-400">
              <el-icon class="text-2xl mb-2"><Plus /></el-icon>
              <div class="text-xs">点击上传或粘贴 URL</div>
            </div>
            <img v-else :src="form.coverImg" class="w-full h-full object-cover rounded" />
            <input ref="coverInputRef" type="file" accept="image/*" class="hidden" @change="onCoverFileChange" />
            <div class="absolute inset-0 bg-black/50 hidden group-hover:flex items-center justify-center rounded gap-2 transition-opacity">
              <el-button size="small" type="primary" @click.stop="handleCoverUpload">更换</el-button>
              <el-button size="small" type="danger" circle icon="Delete" @click.stop="form.coverImg = ''" />
            </div>
          </div>
          <!-- 备用：URL 输入 -->
          <el-input v-if="!form.coverImg" v-model="form.coverImg" placeholder="或输入图片地址" size="small" clearable>
            <template #prefix><el-icon><Link /></el-icon></template>
          </el-input>
        </div>

        <!-- 摘要 -->
        <div class="space-y-2">
          <label class="text-sm font-medium text-gray-600 dark:text-gray-400">摘要</label>
          <el-input 
            v-model="form.summary" 
            type="textarea" 
            :rows="3" 
            placeholder="文章摘要（可选），默认截取正文前100字" 
            maxlength="200"
            show-word-limit
          />
        </div>

        <!-- 分类与标签 -->
        <div class="space-y-4">
          <div>
            <label class="text-sm font-medium text-gray-600 dark:text-gray-400 block mb-2">分类</label>
            <el-select v-model="form.category" placeholder="选择分类" class="w-full" @change="handleCategoryChange">
              <el-option
                v-for="cat in normalizedCategories"
                :key="cat.id ?? cat.label"
                :label="cat.label"
                :value="cat.id ?? cat.label"
                :data-id="cat.id ?? null"
              />
            </el-select>
          </div>
          
          <div>
            <label class="text-sm font-medium text-gray-600 dark:text-gray-400 block mb-2">标签</label>
            <el-select 
              v-model="form.tags" 
              multiple 
              filterable 
              allow-create 
              default-first-option
              placeholder="回车添加标签" 
              class="w-full"
              @change="handleTagsChange"
            >
              <el-option
                v-for="tag in normalizedTags"
                :key="tag.id ?? tag.label"
                :label="tag.label"
                :value="tag.id ?? tag.label"
                :data-id="tag.id ?? null"
              />
            </el-select>
          </div>
        </div>

        <el-divider border-style="dashed" />

        <!-- 属性设置 -->
        <div class="space-y-4">
          <div class="flex items-center justify-between">
            <span class="text-sm text-gray-600 dark:text-gray-400">是否原创</span>
            <el-switch v-model="form.isOriginal" :active-value="1" :inactive-value="0" />
          </div>
          <div class="flex items-center justify-between">
            <span class="text-sm text-gray-600 dark:text-gray-400">置顶文章</span>
            <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
          </div>
          <div class="flex items-center justify-between">
            <span class="text-sm text-gray-600 dark:text-gray-400">SEO关键词</span>
            <el-popover placement="left" :width="200" trigger="click">
              <template #reference>
                <el-button link type="primary" size="small">设置</el-button>
              </template>
              <el-input v-model="form.keywords" placeholder="逗号分隔" size="small" />
            </el-popover>
          </div>
        </div>

        <!-- 状态选择 -->
        <div class="pt-4">
          <label class="text-sm font-medium text-gray-600 dark:text-gray-400 block mb-2">发布状态</label>
          <el-radio-group v-model="form.status" class="w-full grid grid-cols-3 gap-2">
            <el-radio-button :value="1" class="w-full">已发布</el-radio-button>
            <el-radio-button :value="0" class="w-full">草稿</el-radio-button>
            <el-radio-button :value="2" class="w-full">私密</el-radio-button>
          </el-radio-group>
        </div>

      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { resolveCategoryId, resolveTagArrays } from '@/utils/resolveIds'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Plus, Link, Position, Check } from '@element-plus/icons-vue'
import http from '@/api/http'

// --- md-editor-v3 相关 ---
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import type { ToolbarNames } from 'md-editor-v3'

const router = useRouter()
const route = useRoute()

// 状态
const submitting = ref(false)
const lastSavedTime = ref('')
// 允许后端返回多种字段名（id/name/tagName/categoryName）或直接返回字符串
type OptionItem = { id?: number; name?: string; tagName?: string; categoryName?: string } | string
const categories = ref<OptionItem[]>([])
const allTags = ref<OptionItem[]>([])
// 判断当前是否深色模式 (你需要根据你项目的实际状态管理来修改这里)
const isDark = ref(document.documentElement.classList.contains('dark'))

// 编辑器工具栏配置
const toolbars: ToolbarNames[] = [
  'bold', 'underline', 'italic', '-', 'title', 'strikeThrough', 'sub', 'sup', 'quote', 'unorderedList', 'orderedList', 'task', '-',
  'codeRow', 'code', 'link', 'image', 'table', 'mermaid', 'katex', '-',
  'revoke', 'next', 'save', '=', 'pageFullscreen', 'fullscreen', 'preview', 'htmlPreview', 'catalog'
]

// 监听系统主题变化（可选）
const observer = new MutationObserver(() => {
  isDark.value = document.documentElement.classList.contains('dark')
})
onMounted(() => {
  observer.observe(document.documentElement, { attributes: true, attributeFilter: ['class'] })
})

// 表单数据
const form = reactive({
  id: undefined as string | number | undefined,
  title: '',
  content: '',
  summary: '',
  coverImg: '',
  keywords: '',
  // 若文章正文存储在 MongoDB 中，后端可能返回 mongoId，需要在提交时回传以更新对应文档
  mongoId: '' as string | undefined,
  // category 和 tags 将存储 id（若可用），否则回退到名称字符串
  category: '' as string | number,
  tags: [] as Array<string | number>,
  isOriginal: 1,
  isTop: 0,
  status: 1, 
})

const isEditMode = computed(() => !!form.id)

// 初始化
onMounted(async () => {
  await Promise.all([fetchCategories(), fetchTags()])

  // 支持通过 query 或 params 传递 id，避免使用占位符或空值
  // 不要把长整型 id 转为 Number（会造成精度丢失），直接以字符串形式使用原始 id
  const rawId = (route.query.id as string) || (route.params.id as string) || ''
  if (rawId && rawId !== '{id}') {
    loadArticle(rawId)
  }
})

// 加载分类
const fetchCategories = async () => {
  try {
    const res = await http.get('/front/categories')
    const data = res.data?.data || res.data || []
    categories.value = Array.isArray(data) ? data : (data.list || [])
  } catch (e) { console.error(e) }
}

// 加载标签
const fetchTags = async () => {
  try {
    const res = await http.get('/front/tags')
    const data = res.data?.data || res.data || []
    allTags.value = Array.isArray(data) ? data : (data.list || [])
  } catch (e) { console.error(e) }
}

// 规范化选项，模板中使用 label/id，避免直接访问不确定字段导致的类型错误
const normalizedCategories = computed(() =>
  categories.value.map((c: OptionItem) => {
    if (typeof c === 'string') return { id: undefined as unknown as string | undefined, label: c }
    const o = c as any
    const rawId = o.id ?? o._id ?? undefined
    return {
      id: rawId != null ? String(rawId) : undefined,
      label: o.name ?? o.categoryName ?? String(o),
    }
  })
)

const normalizedTags = computed(() =>
  allTags.value.map((t: OptionItem) => {
    if (typeof t === 'string') return { id: undefined as unknown as string | undefined, label: t }
    const o = t as any
    const rawId = o.id ?? o._id ?? undefined
    return {
      id: rawId != null ? String(rawId) : undefined,
      label: o.tagName ?? o.name ?? String(o),
    }
  })
)

// 使用工具模块中的 resolveCategoryId / resolveTagArrays

// 当分类下拉变化时，优先取选项的 id，否则保留选择的字符串
const handleCategoryChange = (val: string | number) => {
  if (val === undefined || val === null || val === '') {
    form.category = ''
    return
  }
  const s = String(val)
  // 尝试按 id 或 label 查找
  const found = normalizedCategories.value.find((c: any) => String(c.id) === s || String(c.label) === s)
  if (found && found.id != null) form.category = String(found.id)
  else form.category = val
}

// 当标签选择变化时，尽可能把标签解析为 id 列表，不能解析的保留为名称
const handleTagsChange = (vals: Array<string | number>) => {
  if (!Array.isArray(vals)) {
    form.tags = []
    return
  }
  const out: Array<string | number> = []
  vals.forEach((v) => {
    const s = String(v)
    // 先看是否为纯数字 id
    if (/^\d+$/.test(s)) {
      out.push(String(Number(s)))
      return
    }
    // 否则尝试在 normalizedTags 中查找对应项的 id
    const found = normalizedTags.value.find((t: any) => String(t.label) === s || String(t.id) === s)
    if (found && found.id != null && /^\d+$/.test(String(found.id))) out.push(String(found.id))
    else out.push(s)
  })
  form.tags = out
}

// 加载文章并映射到表单（兼容后端返回的不同字段格式）
// id 可能很长（超过 JS Number 精度），因此接受 string | number 并以字符串形式发送到后端
const loadArticle = async (id: string | number) => {
  try {
    const idStr = encodeURIComponent(String(id))
    const res = await http.get(`/admin/articles/${idStr}`)
    const data = res.data?.data || res.data
    if (!data) throw new Error('空文章数据')

    // 始终以字符串保存 id，避免 JS 数字精度丢失
    form.id = String(data.id ?? data._id ?? id)
    // 多个后端实现可能使用不同字段名存储 Mongo 文档 id，尝试兼容常见字段
    form.mongoId = data.mongoId ?? data.contentMongoId ?? data.contentId ?? data.mongo_id ?? data.mongoIdStr ?? undefined
    form.title = data.title ?? ''
    form.content = data.content ?? data.body ?? ''
    form.summary = data.summary ?? data.summaryText ?? ''
    form.coverImg = data.coverImg ?? data.coverUrl ?? ''
    // keywords 可能是数组或逗号分隔字符串
    if (Array.isArray(data.keywords)) {
      form.keywords = data.keywords.join(',')
    } else {
      form.keywords = data.keywords ?? ''
    }

    // category 可能是对象 {id,name} 或字符串，优先使用 id（便于提交），回退到名称
    form.category = data.category?.id ?? data.category ?? ''

    // tags 可能是对象数组或字符串，优先映射为 id 数组（若没有 id 则使用名称）
    if (Array.isArray(data.tags)) {
      form.tags = data.tags.map((t: any) => (t && (t.id ?? (t.tagName ?? t.name ?? t)))) .filter(Boolean)
    } else if (typeof data.tags === 'string') {
      // 假如后端返回逗号分隔的标签名列表，保留为名称数组
      form.tags = data.tags.split(',').map((s: string) => s.trim()).filter(Boolean)
    } else {
      form.tags = []
    }

    form.isOriginal = data.isOriginal ?? data.isOriginal ?? 1
    form.isTop = data.isTop ?? 0
    form.status = data.status ?? 1
  } catch (e) {
    console.error(e)
    ElMessage.error('加载文章失败')
  }
}

// 处理图片上传：支持两种调用约定
// 1) md-editor-v3 会传入 (files) 并期望返回 Promise<string[]>
// 2) 或者会传入 (files, callback) 的回调风格
const handleUploadImage = async (files: File[], callback?: (urls: string[]) => void) => {
  try {
    const res = await Promise.all(
      files.map((file) => {
        const formData = new FormData()
        formData.append('file', file)
        // 修改为你的真实上传接口
        return http.post('/admin/upload', formData)
      })
    )

    // 假设后端返回 { code: 200, data: "https://url.com/img.png" }
    // 或者 { code: 200, data: { url: "..." } }
    const urls = res.map((r: any) => {
      const data = r.data?.data || r.data
      return typeof data === 'string' ? data : data?.url
    }).filter(Boolean as any)

    if (typeof callback === 'function') callback(urls)
    return urls
  } catch (err) {
    // 回退为本地对象 URL 以便能在编辑器中立即显示（注意：本地 URL 未上传到服务器）
    const localUrls = files.map((f) => URL.createObjectURL(f))
    if (typeof callback === 'function') callback(localUrls)
    ElMessage.warning('上传接口不可用，已使用本地预览（未上传到服务器）')
    return localUrls
  }
}

// 封面图片上传引用和处理
const coverInputRef = ref<HTMLInputElement | null>(null)
// 保留用户选择的封面文件，以便在提交时作为 multipart 的 imageFile 字段上传
const coverFileRef = ref<File | null>(null)

const handleCoverUpload = (e?: Event) => {
  if (e) e.stopPropagation()
  if (coverInputRef.value) {
    coverInputRef.value.value = ''
    coverInputRef.value.click()
  }
}

const onCoverFileChange = async (e: Event) => {
  const input = e.target as HTMLInputElement
  const files = input.files
  if (!files || !files.length) return
  const file = files[0]
  // 记录用户选择的文件，供提交时使用
  coverFileRef.value = file
  if (!file.type.startsWith('image/')) return ElMessage.error('请选择图片文件')
  if (file.size > 10 * 1024 * 1024) return ElMessage.error('图片不能超过10MB')
  const formData = new FormData()
  formData.append('file', file)
  try {
    const res = await http.post('/admin/upload', formData)
    const data = res.data?.data || res.data
    form.coverImg = typeof data === 'string' ? data : data.url
  } catch (err) {
    // 如后端上传接口不可用，回退为本地预览（不会持久化至后端）
    try {
      const fallbackUrl = URL.createObjectURL((formData as any).get('file'))
      form.coverImg = fallbackUrl
      ElMessage.warning('上传接口不可用，已使用本地预览（未上传到服务器）')
    } catch (e) {
      ElMessage.error('上传失败')
    }
  }
}

// 自动生成摘要
const generateSummary = () => {
  if (!form.summary && form.content) {
    // 简单过滤 Markdown 字符
    const text = form.content.replace(/[#*`>~\[\]\(\)\n]/g, ' ').slice(0, 100)
    form.summary = text + '...'
  }
}

// 保存草稿 / 编辑时保存（编辑时不强制改为草稿）
const handleSaveDraft = async () => {
  if (!form.title) return ElMessage.warning('请至少输入标题')
  // 新建时保存为草稿（status=0），编辑时保持当前 status
  if (!form.id) form.status = 0
  await submitLogic(true)
}

// 提交发布
const handleSubmit = async () => {
  if (!form.title || !form.content) return ElMessage.warning('标题和正文不能为空')
  generateSummary()
  // 如果是编辑已有文章，使用修改状态的接口快速发布
  if (form.id) {
    submitting.value = true
    try {
      const payload = { id: String(form.id), status: 1 }
      await http.put('/admin/articles/status', payload)
      ElMessage.success('发布成功')
      router.push('/admin/articles')
    } catch (err: any) {
      ElMessage.error(err?.message || '发布失败')
    } finally {
      submitting.value = false
    }
  } else {
    // 新建文章走原有的创建流程，确保 status 为已发布
    form.status = 1
    await submitLogic(false)
  }
}

// 提交逻辑
const submitLogic = async (isDraft: boolean) => {
  submitting.value = true
  try {
    const url = '/admin/articles'

    // 优先尝试：若用户选择了封面文件，则以 multipart/form-data 一次性提交（包含 id 与 mongoId）
    if (coverFileRef.value) {
      const fd = new FormData()
      fd.append('imageFile', coverFileRef.value)
      if (form.id) fd.append('id', String(form.id))
      if (form.mongoId) fd.append('mongoId', String(form.mongoId))
      fd.append('title', form.title)
      fd.append('summary', form.summary)
      fd.append('content', form.content)
      if (form.keywords) fd.append('keywords', form.keywords)

      // 计算 categoryId/tagIds/tagNames（使用 helper，返回字符串 id）
      const resolvedCategoryId = resolveCategoryId(form.category as any, normalizedCategories.value)
      if (resolvedCategoryId) fd.append('categoryId', resolvedCategoryId)
      const resolvedTags = resolveTagArrays(form.tags as any, normalizedTags.value)
      if (resolvedTags.tagIds.length) fd.append('tagIds', JSON.stringify(resolvedTags.tagIds))
      if (resolvedTags.tagNames.length) fd.append('tagNames', JSON.stringify(resolvedTags.tagNames))

      fd.append('isOriginal', String(form.isOriginal))
      fd.append('isTop', String(form.isTop))
      fd.append('status', String(form.status))

      try {
        const res = await http.post(url, fd)
        const rdata = res.data?.data || res.data
        if (rdata) {
          if (typeof rdata === 'string') form.coverImg = rdata
          else if (rdata.url) form.coverImg = rdata.url
          if (!form.id && rdata.id) form.id = String(rdata.id)
        }
        if (isDraft) {
          const now = new Date()
          lastSavedTime.value = `${now.getHours()}:${now.getMinutes().toString().padStart(2, '0')}`
          ElMessage.success(form.id ? '保存成功' : '草稿已保存')
        } else {
          ElMessage.success(form.id ? '更新成功' : '发布成功')
          router.push('/admin/articles')
        }
        submitting.value = false
        return
      } catch (err) {
        // multipart 被后端拒绝或失败，回退到上传封面再以 JSON 提交
        console.warn('multipart submit failed, falling back to upload-first JSON flow', err)
        try {
          const upFd = new FormData()
          upFd.append('file', coverFileRef.value)
          const upRes = await http.post('/admin/upload', upFd)
          const upData = upRes.data?.data || upRes.data
          const coverUrl = typeof upData === 'string' ? upData : upData?.url
          if (coverUrl) form.coverImg = coverUrl
        } catch (e) {
          ElMessage.warning('封面上传失败，继续提交（使用当前封面 URL/本地预览）')
        }
      }
    }

    // 没有封面文件，或 multipart 失败后走这里，以 JSON 提交（包含 id 与 mongoId）

    // 计算 categoryId 与 tagIds/tagNames（使用 helper，返回字符串 id）
    const resolvedCategoryId = resolveCategoryId(form.category as any, normalizedCategories.value)
    const resolvedTags = resolveTagArrays(form.tags as any, normalizedTags.value)

    const payload: any = {
      // 始终以字符串形式传递 id，避免 JS Number 精度丢失导致后端收到不正确的 id
      id: form.id ? String(form.id) : undefined,
      mongoId: form.mongoId || undefined,
      title: form.title,
      summary: form.summary,
      content: form.content,
      keywords: form.keywords || undefined,
      categoryId: resolvedCategoryId,
      tagIds: resolvedTags.tagIds.length ? resolvedTags.tagIds : undefined,
      tagNames: resolvedTags.tagNames.length ? resolvedTags.tagNames : undefined,
      isOriginal: form.isOriginal,
      isTop: form.isTop,
      status: form.status,
    }
    if (form.coverImg && form.coverImg.startsWith('http')) payload.coverImg = form.coverImg

    const res = await http.post(url, payload)

    if (isDraft) {
      const now = new Date()
      lastSavedTime.value = `${now.getHours()}:${now.getMinutes().toString().padStart(2, '0')}`
      ElMessage.success(form.id ? '保存成功' : '草稿已保存')
      if (!form.id && res.data?.data?.id) {
        form.id = String(res.data.data.id)
      }
    } else {
      ElMessage.success(form.id ? '更新成功' : '发布成功')
      router.push('/admin/articles')
    }
  } catch (err: any) {
    ElMessage.error(err.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  router.back()
}
</script>

<style scoped>
/* 自定义滚动条样式 */
.custom-scroll::-webkit-scrollbar {
  width: 6px;
}
.custom-scroll::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 3px;
}
.dark .custom-scroll::-webkit-scrollbar-thumb {
  background-color: #475569;
}

/* 调整 md-editor 在深色模式下的边框颜色，使其更融合 */
.dark :deep(.md-editor) {
  --md-border-color: #374151; /* gray-700 */
}

/* 让 md-editor-v3 渲染内容靠左对齐 */
:deep(.md-editor-preview-wrapper),
:deep(.md-editor-preview-wrapper .md-editor-preview) {
  margin-left: 0 !important;
  margin-right: 0 !important;
  text-align: left !important;
  justify-content: flex-start !important;
}

:deep(.md-editor-content) {
  text-align: left !important;
}

/* 小圆点演示样式 */
:deep(.md-editor-preview-wrapper ul) {
  list-style-type: disc !important;
  padding-left: 1.5em !important;
  margin-left: 0 !important;
}
:deep(.md-editor-preview-wrapper ul li) {
  position: relative;
  padding-left: 0.3em;
  margin-bottom: 0.2em;
}

</style>