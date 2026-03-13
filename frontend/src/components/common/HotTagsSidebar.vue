<script setup lang="ts" name="HotTagsSidebar">
import { ref, watch, computed } from 'vue';
const props = defineProps<{ tags: Array<{ id: number; tagName: string }>; goToTag: (tag: string) => void }>();
const selected = ref(props.tags.length ? props.tags[0].tagName : '');
watch(() => props.tags, (val) => {
  if (val.length && !val.find(t => t.tagName === selected.value)) {
    selected.value = val[0].tagName;
  }
});
import { useRoute } from 'vue-router';
const route = useRoute();
watch(
  () => route.params.name,
  (newName) => {
    if (typeof newName === 'string') {
      selected.value = newName;
    }
  },
  { immediate: true }
);
const totalRadio = computed(() => props.tags.length || 1);
function handleChange(tag: string) {
  selected.value = tag;
  props.goToTag(tag);
}
</script>

<template>
  <div class="sidebar-padding">
    <div class="radio-container" :style="{ '--total-radio': totalRadio }">
    <template v-for="tag in tags" :key="tag.id">
      <input
        :id="'radio-tag-' + tag.id"
        type="radio"
        name="radio-tag"
        :checked="selected === tag.tagName"
        @change="handleChange(tag.tagName)"
      />
      <label :for="'radio-tag-' + tag.id">{{ tag.tagName }}</label>
    </template>
    <div class="glider-container">
      <div class="glider"
        :style="{ transform: `translateY(${tags.findIndex(t => t.tagName === selected) * 100}% )`, height: `calc(100% / ${totalRadio})` }"
      ></div>
    </div>
    </div>
  </div>
</template>

<style scoped>
/* 为侧边栏整体添加左侧间隙 */
.sidebar-padding {
  padding-left: 28px;
}
.radio-container {
  --main-color: #f7e479;
  --main-color-opacity: #f7e4791c;

  /* change this according inputs count */
  --total-radio: 3;

  display: flex;
  flex-direction: column;
  position: relative;
  padding-left: 0.5rem;
  min-width: 120px; /* 更窄的宽度 */
  max-width: 160px; /* 限制最大宽度，防止过宽 */
}
.radio-container input {
  cursor: pointer;
  appearance: none;
}
.radio-container .glider-container {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  background: linear-gradient(
    0deg,
    rgba(0, 0, 0, 0) 0%,
    rgba(27, 27, 27, 1) 50%,
    rgba(0, 0, 0, 0) 100%
  );
  width: 1px;
}
.radio-container .glider-container .glider {
  position: relative;
  height: calc(100% / var(--total-radio));
  width: 100%;
  background: linear-gradient(
    0deg,
    rgba(0, 0, 0, 0) 0%,
    var(--main-color) 50%,
    rgba(0, 0, 0, 0) 100%
  );
  transition: transform 0.5s cubic-bezier(0.37, 1.95, 0.66, 0.56);
}
.radio-container .glider-container .glider::before {
  content: "";
  position: absolute;
  height: 60%;
  width: 300%;
  top: 50%;
  transform: translateY(-50%);
  background: var(--main-color);
  filter: blur(10px);
}
.radio-container .glider-container .glider::after {
  content: "";
  position: absolute;
  left: 0;
  height: 100%;
  width: 150px;
  background: linear-gradient(
    90deg,
    var(--main-color-opacity) 0%,
    rgba(0, 0, 0, 0) 100%
  );
}
.radio-container label {
  cursor: pointer;
  padding: 1rem;
  position: relative;
  color: grey;
  transition: all 0.3s ease-in-out;
}

.radio-container input:checked + label {
  color: var(--main-color);
}

.radio-container input:nth-of-type(1):checked ~ .glider-container .glider {
  transform: translateY(0);
}

.radio-container input:nth-of-type(2):checked ~ .glider-container .glider {
  transform: translateY(100%);
}

.radio-container input:nth-of-type(3):checked ~ .glider-container .glider {
  transform: translateY(200%);
}

.radio-container input:nth-of-type(4):checked ~ .glider-container .glider {
  transform: translateY(300%);
}

.radio-container input:nth-of-type(5):checked ~ .glider-container .glider {
  transform: translateY(400%);
}

.radio-container input:nth-of-type(6):checked ~ .glider-container .glider {
  transform: translateY(500%);
}

.radio-container input:nth-of-type(7):checked ~ .glider-container .glider {
  transform: translateY(600%);
}

.radio-container input:nth-of-type(8):checked ~ .glider-container .glider {
  transform: translateY(700%);
}

.radio-container input:nth-of-type(9):checked ~ .glider-container .glider {
  transform: translateY(800%);
}

.radio-container input:nth-of-type(10):checked ~ .glider-container .glider {
  transform: translateY(900%);
}

</style>
