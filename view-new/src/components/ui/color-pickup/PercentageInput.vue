<script lang="ts" setup>
import {Input} from '@/components/ui/input';
import {cn} from "@/lib/utils.ts";
import type {HTMLAttributes} from 'vue';

const props = defineProps<{
  value?: string | number,
  class?: HTMLAttributes['class'],
}>();
</script>

<template>
  <div class="relative">
    <Input
        :class="cn('h-8 w-[3.25rem] rounded-l-none bg-secondary px-2 text-xs shadow-none', props.class)"
        :modelValue="props.value"
        readonly
        v-bind="$attrs"
    />
    <span class="-translate-y-1/2 absolute top-1/2 right-2 text-muted-foreground text-xs">
      %
    </span>
  </div>
</template>