<script lang="ts" setup>
import {cn} from "@/lib/utils.ts";
import {computed, type HTMLAttributes, inject, ref, watch} from 'vue';
import {colorPickerKey, type Context} from "./utils.ts";

const {hue, saturation, lightness} = inject(colorPickerKey) as Context;

const props = defineProps<{
  class?: HTMLAttributes["class"],
}>();

const containerRef = ref<HTMLDivElement | null>(null);
const isDragging = ref<boolean>(false);
const positionX = ref<number>(0);
const positionY = ref<number>(0);

const updatePosition = () => {
  const x = saturation.value / 100;
  const l = lightness.value;

  positionX.value = x;
  if (x < 0.01) {
    positionY.value = 1 - l / 100;
  } else {
    const topLightness = 50 + 50 * (1 - x);
    positionY.value = 1 - l / topLightness;
  }
};

watch([saturation, lightness], () => {
  if (!isDragging.value) {
    updatePosition();
  }
});

const backgroundGradient = computed(() => {
  return `linear-gradient(0deg, rgba(0,0,0,1), rgba(0,0,0,0)),
          linear-gradient(90deg, rgba(255,255,255,1), rgba(255,255,255,0)),
          hsl(${hue.value}, 100%, 50%)`;
});

const handlePointerMove = (event: PointerEvent) => {
  if (!isDragging.value || !containerRef.value) {
    return;
  }

  const rect = containerRef.value.getBoundingClientRect();
  const x = Math.max(0, Math.min(1, (event.clientX - rect.left) / rect.width));
  const y = Math.max(0, Math.min(1, (event.clientY - rect.top) / rect.height));

  positionX.value = x;
  positionY.value = y;
  saturation.value = x * 100;
  const topLightness = x < 0.01 ? 100 : 50 + 50 * (1 - x);
  lightness.value = topLightness * (1 - y);
};

const handlePointerUp = () => {
  isDragging.value = false;
};

watch(isDragging, (isDrag) => {
  if (isDrag) {
    window.addEventListener('pointermove', handlePointerMove);
    window.addEventListener('pointerup', handlePointerUp);
  } else {
    window.removeEventListener('pointermove', handlePointerMove);
    window.removeEventListener('pointerup', handlePointerUp);
  }
}, {immediate: true});

const handlePointerDown = (e: PointerEvent) => {
  e.preventDefault();
  isDragging.value = true;
  handlePointerMove(e);
};
</script>

<template>
  <div
      ref="containerRef"
      :class="cn('relative size-full cursor-crosshair rounded', props.class)"
      :style="{background: backgroundGradient}"
      v-bind="$attrs"
      @pointerdown="handlePointerDown"
  >
    <div
        :style="{
          left: `${positionX * 100}%`,
          top: `${positionY * 100}%`,
          boxShadow: '0 0 0 1px rgba(0,0,0,0.5)'
        }"
        class="-translate-x-1/2 -translate-y-1/2 pointer-events-none absolute h-4 w-4 rounded-full border-2 border-white"
    />
  </div>
</template>