<script lang="ts" setup>
import {cn} from "@/lib/utils.ts";
import Color, {type ColorInstance, type ColorLike} from 'color';
import {type HTMLAttributes, onMounted, provide, ref, watch} from 'vue';
import {colorPickerKey, type Context} from "./utils.ts";

const props = defineProps<{
  value?: ColorLike,
  defaultValue?: ColorLike,
  class?: HTMLAttributes["class"],
}>();

const emits = defineEmits<{
  'update:value': [value: ColorInstance];
}>();

const hue = ref<number>(0);
const saturation = ref<number>(100);
const lightness = ref<number>(50);
const alpha = ref<number>(100);
const mode = ref<string>('hex');

const initColor = () => {
  const targetColor = Color(props.value || props.defaultValue || '#000000');
  hue.value = targetColor.hue() || 0;
  saturation.value = targetColor.saturationl() || 100;
  lightness.value = targetColor.lightness() || 50;
  alpha.value = Math.round(targetColor.alpha() * 100) || 100;
};

onMounted(() => initColor());

watch(
    [hue, saturation, lightness, alpha],
    () => {
      const color = Color.hsl(hue.value, saturation.value, lightness.value).alpha(alpha.value / 100);
      emits('update:value', color);
    },
);

provide(colorPickerKey, {
  hue,
  saturation,
  lightness,
  alpha,
  mode,
} as Context);
</script>

<template>
  <div
      :class="cn('flex size-full flex-col gap-2', props.class)"
      v-bind="$attrs"
  >
    <slot/>
  </div>
</template>
