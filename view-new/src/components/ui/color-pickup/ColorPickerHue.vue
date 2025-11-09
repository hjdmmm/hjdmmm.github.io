<script lang="ts" setup>
import {cn} from "@/lib/utils.ts"
import {SliderRange, SliderRoot, SliderThumb, SliderTrack} from "reka-ui"
import {type HTMLAttributes, inject} from "vue"
import {colorPickerKey, type Context} from "./utils.ts";

const props = defineProps<{
  class?: HTMLAttributes["class"],
}>()

const {hue} = inject(colorPickerKey) as Context;
</script>

<template>
  <SliderRoot
      :class="cn('relative flex h-4 w-full touch-none', props.class)"
      :max="360"
      :modelValue="[hue]"
      :step="1"
      @update:modelValue="(value) => hue = (value![0] as number)"
  >
    <SliderTrack
        class="relative my-0.5 h-3 w-full grow rounded-full bg-[linear-gradient(90deg,#FF0000,#FFFF00,#00FF00,#00FFFF,#0000FF,#FF00FF,#FF0000)]"
    >
      <SliderRange
          class="absolute h-full"
      />
    </SliderTrack>

    <SliderThumb
        class="block h-4 w-4 rounded-full border border-primary/50 bg-background shadow transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50"
    />
  </SliderRoot>
</template>
