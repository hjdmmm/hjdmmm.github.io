<script lang="ts" setup>
import {Input} from '@/components/ui/input';
import {cn} from "@/lib/utils.ts";
import Color from 'color';
import {computed, type HTMLAttributes, inject} from 'vue';
import PercentageInput from './PercentageInput.vue';
import {colorPickerKey, type Context} from "./utils.ts";

const {hue, saturation, lightness, alpha, mode} = inject(colorPickerKey) as Context;

const props = defineProps<{
  class?: HTMLAttributes["class"],
}>();

const currentColor = computed(() => {
  return Color.hsl(hue.value, saturation.value, lightness.value).alpha(alpha.value / 100);
});

const hexValue = computed(() => currentColor.value.hex());
const rgbValues = computed(() => currentColor.value.rgb().array().map(Math.round));
const cssValue = computed(() => {
  const [r, g, b] = rgbValues.value;
  return `rgba(${r}, ${g}, ${b}, ${alpha.value}%)`;
});
const hslValues = computed(() => currentColor.value.hsl().array().map(Math.round));
</script>

<template>
  <div :class="props.class" v-bind="$attrs">
    <template v-if="mode === 'hex'">
      <div class="-space-x-px flex w-full items-center rounded-md shadow-sm">
        <Input
            :modelValue="hexValue"
            class="h-8 rounded-r-none bg-secondary px-2 text-xs shadow-none"
            readonly
        />
        <PercentageInput :value="alpha"/>
      </div>
    </template>

    <template v-else-if="mode === 'rgb'">
      <div class="-space-x-px flex items-center rounded-md shadow-sm">
        <Input
            v-for="(val, idx) in rgbValues"
            :key="idx"
            :class="cn(
                'h-8 rounded-r-none bg-secondary px-2 text-xs shadow-none',
                idx !== 0 ? 'rounded-l-none' : '',
            )"
            :modelValue="val"
            readonly
        />
        <PercentageInput :value="alpha"/>
      </div>
    </template>

    <template v-else-if="mode === 'css'">
      <Input
          :modelValue="cssValue"
          class="h-8 w-full bg-secondary px-2 text-xs shadow-none"
          readonly
      />
    </template>

    <template v-else-if="mode === 'hsl'">
      <div class="-space-x-px flex items-center rounded-md shadow-sm">
        <Input
            v-for="(val, idx) in hslValues"
            :key="idx"
            :class="cn(
                'h-8 rounded-r-none bg-secondary px-2 text-xs shadow-none',
                idx !== 0 ? 'rounded-l-none' : '',
            )"
            :modelValue="val"
            readonly
        />
        <PercentageInput :value="alpha"/>
      </div>
    </template>
  </div>
</template>
