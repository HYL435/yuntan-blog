<template>
  <div class="p-4 bg-white dark:bg-[#0f172a] rounded-lg shadow-sm transition-colors duration-300">
    <h2 class="text-2xl font-bold mb-4 text-gray-800 dark:text-white">设置</h2>
    <el-form label-width="120px">
      <el-form-item label="站点标题">
        <el-input placeholder="站点标题" />
      </el-form-item>
      <el-form-item label="邮件通知">
        <el-switch />
      </el-form-item>
      <el-form-item>
        <el-button type="primary">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
</script>
