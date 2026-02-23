<template>
  <el-card>
    <h2 class="text-2xl font-bold mb-4">仪表盘</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card>
          <div class="text-sm text-gray-500">总文章</div>
          <div class="text-3xl font-bold">128</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="text-sm text-gray-500">用户数</div>
          <div class="text-3xl font-bold">1,024</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="text-sm text-gray-500">本周访问</div>
          <div class="text-3xl font-bold">—</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="text-sm text-gray-500">待审核</div>
          <div class="text-3xl font-bold">—</div>
        </el-card>
      </el-col>
    </el-row>

    <div class="mt-6">
      <el-card>
        <div class="text-sm text-gray-500 mb-2">最近文章</div>
        <el-table :data="[]" style="width:100%">
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="author" label="作者" />
          <el-table-column prop="created" label="创建时间" />
        </el-table>
      </el-card>
    </div>
  </el-card>
</template>

<script setup lang="ts">
</script>
