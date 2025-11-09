<script lang="ts" setup>
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from '@/components/ui/select';
import {type HTMLAttributes, inject} from 'vue';
import {colorPickerKey, type Context} from "./utils.ts";

const {mode} = inject(colorPickerKey) as Context;

const formats = ['hex', 'rgb', 'css', 'hsl'] as const;

const props = defineProps<{
  class?: HTMLAttributes["class"],
}>();
</script>

<template>
  <Select
      :class="props.class"
      :modelValue="mode"
      v-bind="$attrs"
      @update:modelValue="(value) => mode = value! as string"
  >
    <SelectTrigger class="w-[100px]">
      <SelectValue placeholder="Mode"/>
    </SelectTrigger>
    <SelectContent>
      <SelectItem
          v-for="format in formats"
          :key="format"
          :value="format"
          class="text-xs"
      >
        {{ format.toUpperCase() }}
      </SelectItem>
    </SelectContent>
  </Select>
</template>
