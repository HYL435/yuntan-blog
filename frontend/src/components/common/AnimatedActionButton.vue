<template>
  <div class="heart-container" :title="title" :class="icon === 'star' ? 'star-mode' : ''">
    <input
      type="checkbox"
      class="checkbox"
      :id="id"
      :checked="checked"
      @change="onChange"
    />
    <div class="svg-container">
      <svg v-if="icon === 'heart'" viewBox="0 0 24 24" class="svg-outline" xmlns="http://www.w3.org/2000/svg">
        <path d="M17.5,1.917a6.4,6.4,0,0,0-5.5,3.3,6.4,6.4,0,0,0-5.5-3.3A6.8,6.8,0,0,0,0,8.967c0,4.547,4.786,9.513,8.8,12.88a4.974,4.974,0,0,0,6.4,0C19.214,18.48,24,13.514,24,8.967A6.8,6.8,0,0,0,17.5,1.917Zm-3.585,18.4a2.973,2.973,0,0,1-3.83,0C4.947,16.006,2,11.87,2,8.967a4.8,4.8,0,0,1,4.5-5.05A4.8,4.8,0,0,1,11,8.967a1,1,0,0,0,2,0,4.8,4.8,0,0,1,4.5-5.05A4.8,4.8,0,0,1,22,8.967C22,11.87,19.053,16.006,13.915,20.313Z"/>
      </svg>
      <svg v-if="icon === 'heart'" viewBox="0 0 24 24" class="svg-filled" xmlns="http://www.w3.org/2000/svg">
        <path d="M17.5,1.917a6.4,6.4,0,0,0-5.5,3.3,6.4,6.4,0,0,0-5.5-3.3A6.8,6.8,0,0,0,0,8.967c0,4.547,4.786,9.513,8.8,12.88a4.974,4.974,0,0,0,6.4,0C19.214,18.48,24,13.514,24,8.967A6.8,6.8,0,0,0,17.5,1.917Z"/>
      </svg>
      <svg v-if="icon === 'heart'" class="svg-celebrate" width="100" height="100" xmlns="http://www.w3.org/2000/svg">
        <polygon points="10,10 20,20"></polygon>
        <polygon points="10,50 20,50"></polygon>
        <polygon points="20,80 30,70"></polygon>
        <polygon points="90,10 80,20"></polygon>
        <polygon points="90,50 80,50"></polygon>
        <polygon points="80,80 70,70"></polygon>
      </svg>
      <!-- star 未选中为黄色描边中空，选中为黄色填充 -->
      <svg v-if="icon === 'star'" viewBox="0 0 24 24" class="svg-outline" xmlns="http://www.w3.org/2000/svg">
        <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"
          :fill="checked ? '#ffd600' : 'none'"
          :stroke="'#ffd600'"
          stroke-width="2"
        />
      </svg>
      <svg v-if="icon === 'star'" viewBox="0 0 24 24" class="svg-filled" xmlns="http://www.w3.org/2000/svg">
        <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" fill="#ffd600"/>
      </svg>
      <svg v-if="icon === 'star'" class="svg-celebrate" width="100" height="100" xmlns="http://www.w3.org/2000/svg">
        <polygon points="10,10 20,20" style="stroke:#ffd600;fill:#ffd600"/>
        <polygon points="10,50 20,50" style="stroke:#ffd600;fill:#ffd600"/>
        <polygon points="20,80 30,70" style="stroke:#ffd600;fill:#ffd600"/>
        <polygon points="90,10 80,20" style="stroke:#ffd600;fill:#ffd600"/>
        <polygon points="90,50 80,50" style="stroke:#ffd600;fill:#ffd600"/>
        <polygon points="80,80 70,70" style="stroke:#ffd600;fill:#ffd600"/>
      </svg>
      <svg v-if="icon === 'comment'" viewBox="0 0 24 24" class="svg-outline" xmlns="http://www.w3.org/2000/svg">
        <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" fill="#06b6d4" stroke="#06b6d4" />
      </svg>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  checked: Boolean,
  icon: {
    type: String,
    default: 'heart', // heart | star | comment
  },
  id: {
    type: String,
    default: '',
  },
  title: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['update:checked', 'change'])

function onChange(e: Event) {
  const checked = (e.target as HTMLInputElement).checked
  emit('update:checked', checked)
  emit('change', checked)
}
</script>

<style scoped>
.heart-container {
  --heart-color: rgb(255, 91, 137);
  position: relative;
  width: 32px;
  height: 32px;
  transition: .3s;
}
.heart-container .checkbox {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0;
  z-index: 20;
  cursor: pointer;
}
.heart-container .svg-container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}
/* 默认心形按钮颜色 */
.heart-container .svg-outline,
.heart-container .svg-filled {
  fill: var(--heart-color);
  position: absolute;
}
/* 收藏按钮特殊样式 */
.heart-container.star-mode .svg-outline {
  fill: none !important;
  stroke: #ffd600 !important;
}
.heart-container.star-mode .svg-filled {
  fill: #ffd600 !important;
}
.heart-container.star-mode .svg-celebrate polygon {
  stroke: #ffd600 !important;
  fill: #ffd600 !important;
}
.heart-container .svg-filled {
  animation: keyframes-svg-filled 1s;
  display: none;
}
.heart-container .svg-celebrate {
  position: absolute;
  animation: keyframes-svg-celebrate .5s;
  animation-fill-mode: forwards;
  display: none;
  stroke: var(--heart-color);
  fill: var(--heart-color);
  stroke-width: 2px;
}
.heart-container .checkbox:checked~.svg-container .svg-filled {
  display: block;
}
.heart-container .checkbox:checked~.svg-container .svg-celebrate {
  display: block;
}
@keyframes keyframes-svg-filled {
  0% {
    transform: scale(0);
  }
  25% {
    transform: scale(1.2);
  }
  50% {
    transform: scale(1);
    filter: brightness(1.5);
  }
}
@keyframes keyframes-svg-celebrate {
  0% {
    transform: scale(0);
  }
  50% {
    opacity: 1;
    filter: brightness(1.5);
  }
  100% {
    transform: scale(1.4);
    opacity: 0;
    display: none;
  }
}
</style>
