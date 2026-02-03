<template>
  <button class="uiverse-btn">
    <slot>Button</slot>
  </button>
</template>

<script setup lang="ts" name="OptionButton">
</script>

<style scoped>
/* From Uiverse.io by jas19072000 */ 
.uiverse-btn {
  align-items: center;
  background-color: transparent;
  color: #fff; /* 注意：字体是白色的 */
  cursor: pointer;
  display: flex;
  justify-content: center; /* 添加水平居中 */
  font-family: ui-sans-serif,system-ui,-apple-system,system-ui,"Segoe UI",Roboto,"Helvetica Neue",Arial,"Noto Sans",sans-serif,"Apple Color Emoji","Segoe UI Emoji","Segoe UI Symbol","Noto Color Emoji";
  font-size: 0.85rem; /*稍微减小字号*/
  font-weight: 700;
  line-height: 1.5;
  text-decoration: none;
  text-transform: uppercase;
  outline: 0;
  border: 0;
  padding: 0.6rem 0.8rem; /* 减小内边距 */
  width: 100%; /* 确保填满容器 */
  white-space: nowrap;
}

.uiverse-btn:before {
  background-color: rgb(213, 39, 39); /* 红线 */
  content: "";
  display: inline-block;
  height: 1px;
  margin-right: 10px;
  transition: all .42s cubic-bezier(.25,.8,.25,1);
  width: 0;
}

.uiverse-btn:hover:before {
  background-color: #fff;
  width: 2rem; /* 伸长长度 */
}
</style>