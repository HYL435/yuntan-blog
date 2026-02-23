<template>
  <el-card>
    <h2 class="text-2xl font-bold mb-4">设置</h2>
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
  </el-card>
</template>

<script setup lang="ts">
</script>
