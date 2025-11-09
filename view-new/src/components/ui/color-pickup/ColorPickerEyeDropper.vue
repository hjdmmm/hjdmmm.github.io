<script lang="ts" setup>
import {Button} from '@/components/ui/button';
import {cn} from "@/lib/utils.ts";
import Color from 'color';
import {PipetteIcon} from 'lucide-vue-next';
import {type HTMLAttributes, inject} from 'vue';
import {colorPickerKey, type Context} from "./utils.ts";

const {hue, saturation, lightness, alpha} = inject(colorPickerKey) as Context;

const props = defineProps<{
  class?: HTMLAttributes["class"],
}>();

const handleEyeDropper = async () => {
  let eyeDropper;
  try {
    // @ts-expect-error: EyeDropper API is experimental
    eyeDropper = new window.EyeDropper();
  } catch (error) {
    console.error('取色器调用失败：', error);
    return;
  }

  const result = await eyeDropper.open();
  const color = Color(result.sRGBHex);
  const [h, s, l] = color.hsl().array();
  hue.value = h!;
  saturation.value = s!;
  lightness.value = l!;
  alpha.value = 100;
};
</script>

<template>
  <Button
      :class="cn('shrink-0 text-muted-foreground', props.class)"
      size="icon"
      v-bind="$attrs"
      variant="outline"
      @click="handleEyeDropper"
  >
    <PipetteIcon :size="16"/>
  </Button>
</template>
