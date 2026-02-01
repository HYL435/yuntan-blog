<template>
  <div class="toggle-switch">
    <label class="switch-label">
      <input type="checkbox" class="checkbox" v-model="isDark">
      <span class="slider"></span>
    </label>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const isDark = ref(false)
</script>

<style scoped>
.toggle-switch {
  position: relative;
  width: 54px;
  height: 27px;
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  --light: #d8dbe0;
  --dark: #28292c;
  --link: rgb(27, 129, 112);
  --link-hover: rgb(24, 94, 82);
}

.switch-label {
  position: relative;
  width: 100%;
  height: 27px;
  background-color: var(--dark);
  border-radius: 13.5px;
  cursor: pointer;
  border: 1.6px solid var(--dark);
  display: inline-block;
}

.checkbox {
  position: absolute;
  display: none;
}

.slider {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 13.5px;
  -webkit-transition: 0.3s;
  transition: 0.3s;
}

.checkbox:checked ~ .slider {
  background-color: var(--light);
}

.slider::before {
  content: "";
  position: absolute;
  top: 5.4px;
  left: 5.4px;
  width: 13.5px;
  height: 13.5px;
  border-radius: 50%;
  -webkit-box-shadow: inset 6.5px -2.2px 0px 0px var(--light);
  box-shadow: inset 6.5px -2.2px 0px 0px var(--light);
  background-color: var(--dark);
  -webkit-transition: 0.3s;
  transition: 0.3s;
}

.checkbox:checked ~ .slider::before {
  -webkit-transform: translateX(27px);
  -ms-transform: translateX(27px);
  transform: translateX(27px);
  background-color: var(--dark);
  -webkit-box-shadow: none;
  box-shadow: none;
}
</style>
