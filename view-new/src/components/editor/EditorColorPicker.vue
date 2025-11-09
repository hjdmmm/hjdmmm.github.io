<script lang="ts" setup>
import {Button} from '@/components/ui/button';
import {
  ColorPicker,
  ColorPickerEyeDropper,
  ColorPickerForm,
  ColorPickerFormat,
  ColorPickerHue,
  ColorPickerSelection
} from '@/components/ui/color-pickup';
import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover";
import type {ColorInstance} from "color";
import {ChevronRight, Palette,} from 'lucide-vue-next';

defineProps<{
  color?: ColorInstance,
}>();

const emits = defineEmits<{
  'update:value': [value: ColorInstance | null];
}>();

</script>

<template>
  <div>
    <Button
        class="w-full"
        size="sm"
        variant="outline"
        @click="emits('update:value', null)"
    >
      默认颜色
    </Button>
    <ColorPicker
        :value="color"
        @update:value="emits('update:value', $event)"
    >
      <ColorPickerForm/>
      <Popover>
        <PopoverTrigger as-child>
          <Button class="w-full justify-start" variant="outline">
            <Palette/>
            更多颜色
            <ChevronRight class="ms-auto"/>
          </Button>
        </PopoverTrigger>
        <PopoverContent>
          <ColorPickerSelection class="h-[16rem]"/>
          <div class="flex items-center gap-2">
            <ColorPickerEyeDropper/>
            <ColorPickerHue/>
          </div>
          <ColorPickerFormat/>
        </PopoverContent>
      </Popover>
    </ColorPicker>
  </div>
</template>