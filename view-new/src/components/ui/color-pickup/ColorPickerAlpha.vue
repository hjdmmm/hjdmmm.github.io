<script lang="ts" setup>
import {cn} from "@/lib/utils.ts"
import {SliderRange, SliderRoot, SliderThumb, SliderTrack} from "reka-ui"
import {type HTMLAttributes, inject} from "vue"
import {colorPickerKey, type Context} from "./utils.ts";

const CHECKERBOARD_BG = 'url(\'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAMUlEQVQ4T2NkYGAQYcAP3uCTZhw1gGGYhAGBZIA/nYDCgBDAm9BGDWAAJyRCgLaBCAAgXwixzAS0pgAAAABJRU5ErkJggg==\') left center';

const props = defineProps<{
  class?: HTMLAttributes["class"],
}>()

const {alpha} = inject(colorPickerKey) as Context;
</script>

<template>
  <SliderRoot
      :class="cn('relative flex h-4 w-full touch-none', props.class)"
      :max="100"
      :modelValue="[alpha]"
      :step="1"
      v-bind="$attrs"
      @update:modelValue="(value) => alpha = value![0] as number"
  >
    <SliderTrack
        :style="{background: CHECKERBOARD_BG}"
        class="relative my-0.5 h-3 w-full grow rounded-full"
    >
      <div class="absolute inset-0 rounded-full bg-gradient-to-r from-transparent to-black/50"/>
      <SliderRange
          class="absolute h-full rounded-full bg-transparent"
      />
    </SliderTrack>

    <SliderThumb
        class="block h-4 w-4 rounded-full border border-primary/50 bg-background shadow transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50"
    />
  </SliderRoot>
</template>