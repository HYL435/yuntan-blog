
<template>
  <!-- 仪表盘内容区，仅保留主内容 -->
  <div class="space-y-6 animate-fade-in">
    <!-- 数据卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <el-card shadow="hover" class="border-0 bg-gradient-to-br from-blue-500 to-blue-600 text-white">
        <div class="flex justify-between items-start">
          <div>
            <div class="text-white/80 text-sm">总访问量</div>
            <div class="text-3xl font-bold mt-2">128.5k</div>
          </div>
          <el-icon class="text-2xl opacity-80"><DataLine /></el-icon>
        </div>
      </el-card>
      <el-card shadow="hover" class="border-0 dark:bg-[#1e293b]">
        <div class="flex justify-between">
          <div>
            <div class="text-gray-500 text-sm">文章总数</div>
            <div class="text-3xl font-bold mt-2 text-gray-800 dark:text-white">342</div>
          </div>
          <el-icon class="text-2xl text-green-500 bg-green-100 dark:bg-green-900/30 p-2 rounded h-10 w-10 box-content"><Document /></el-icon>
        </div>
      </el-card>
      <el-card shadow="hover" class="border-0 dark:bg-[#1e293b]">
        <div class="flex justify-between">
          <div>
            <div class="text-gray-500 text-sm">待审评论</div>
            <div class="text-3xl font-bold mt-2 text-gray-800 dark:text-white">18</div>
          </div>
          <el-icon class="text-2xl text-orange-500 bg-orange-100 dark:bg-orange-900/30 p-2 rounded h-10 w-10 box-content"><ChatDotSquare /></el-icon>
        </div>
      </el-card>
      <el-card shadow="hover" class="border-0 dark:bg-[#1e293b]">
        <div class="flex justify-between">
          <div>
            <div class="text-gray-500 text-sm">新增用户</div>
            <div class="text-3xl font-bold mt-2 text-gray-800 dark:text-white">56</div>
          </div>
          <el-icon class="text-2xl text-purple-500 bg-purple-100 dark:bg-purple-900/30 p-2 rounded h-10 w-10 box-content"><User /></el-icon>
        </div>
      </el-card>
    </div>

    <!-- 图表占位 + 快捷入口 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <el-card class="lg:col-span-2 border-0 shadow-sm dark:bg-[#1e293b]" shadow="never">
        <template #header><span class="font-bold">流量趋势</span></template>
        <div class="h-64 bg-gray-50 dark:bg-gray-800 rounded flex items-center justify-center border border-dashed border-gray-300 dark:border-gray-700 text-gray-400">
          [ ECharts 图表区域 - 自动适配深色模式 ]
        </div>
      </el-card>
      <el-card class="border-0 shadow-sm dark:bg-[#1e293b]" shadow="never">
        <template #header><span class="font-bold">快捷操作</span></template>
        <div class="grid grid-cols-2 gap-4">
          <el-button type="primary" plain class="w-full h-20 flex flex-col gap-2">
            <el-icon :size="24"><EditPen /></el-icon> 发布文章
          </el-button>
          <el-button type="success" plain class="w-full h-20 flex flex-col gap-2">
            <el-icon :size="24"><UserFilled /></el-icon> 添加用户
          </el-button>
          <el-button type="warning" plain class="w-full h-20 flex flex-col gap-2">
            <el-icon :size="24"><ChatLineSquare /></el-icon> 审核评论
          </el-button>
          <el-button type="info" plain class="w-full h-20 flex flex-col gap-2">
            <el-icon :size="24"><Tools /></el-icon> 网站设置
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
/* 定义淡入动画 */
.animate-fade-in {
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>