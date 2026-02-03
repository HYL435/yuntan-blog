<template>
  <div class="grid-wrapper">
    <!-- 背景网格层 -->
    <div class="grid-background"></div>
    
    <!-- 内容插槽 -->
    <div class="content-container">
      <slot></slot>
    </div>
  </div>
</template>

<style scoped>
/* =========================================
   1. 容器基础样式 (浅色模式默认)
   背景：从图片里的 #aecdef (淡蓝) 渐变到 #f0f9ff (近白)
   ========================================= */
.grid-wrapper {
  min-height: 100vh;
  width: 100%;
  position: relative;
  display: flex;
  flex-direction: column;
  /* 浅色模式背景：天蓝 -> 极淡蓝 */
  background: linear-gradient(to bottom, #aecdef 0%, #f0f9ff 100%);
  background-attachment: fixed; /* 让背景固定，滚动时更有质感 */
  transition: background 0.5s ease;
  overflow-x: hidden;
}

/* =========================================
   2. 网格层样式
   使用 linear-gradient 绘制 1px 的线
   ========================================= */
.grid-background {
  position: absolute;
  inset: 0; /* top/right/bottom/left: 0 的简写 */
  z-index: 0;
  
  /* 
    核心逻辑：绘制网格 
    两条渐变：一条横向，一条纵向
    rgba(255, 255, 255, 0.4) 是白色半透明线条
  */
  background-image: 
    linear-gradient(to right, rgba(255, 255, 255, 0.35) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(255, 255, 255, 0.35) 1px, transparent 1px);
    
  /* 关键：网格大小设为 80px */
  background-size: 80px 80px;

  /* 
    蒙版：让网格在中间清晰，向四周淡出 
    radial-gradient 从中心向外扩散
  */
  -webkit-mask-image: radial-gradient(
    ellipse 80% 80% at 50% 20%,
    rgba(0,0,0,1) 0%,
    rgba(0,0,0,0.4) 50%,
    transparent 80%
  );
  mask-image: radial-gradient(
    ellipse 80% 80% at 50% 20%,
    rgba(0,0,0,1) 0%,
    rgba(0,0,0,0.4) 50%,
    transparent 80%
  );
  
  pointer-events: none; /* 防止遮挡点击 */
}

/* =========================================
   3. 内容层
   ========================================= */
.content-container {
  position: relative;
  z-index: 1;
  width: 100%;
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* =========================================
   4. 深色模式适配 (Dark Mode)
   假设你的深色模式是在 html 标签上加 'dark' 类
   ========================================= */

/* 深色模式：容器背景变深蓝/黑 */
:global(.dark) .grid-wrapper {
  background: linear-gradient(to bottom, #0f172a 0%, #020617 100%);
}

/* 深色模式：网格线条调整 */
:global(.dark) .grid-background {
  /* 线条稍微调淡一点，保持“蓝图”感 */
  background-image: 
    linear-gradient(to right, rgba(255, 255, 255, 0.1) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
    
  /* 蒙版不需要变，因为透明度逻辑是一样的 */
}
</style>