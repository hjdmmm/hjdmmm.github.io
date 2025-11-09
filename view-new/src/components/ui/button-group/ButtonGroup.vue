<script lang="ts" setup>
import {cn} from "@/lib/utils"
import type {HTMLAttributes} from "vue"
import type {ButtonGroupVariants} from "."
import {buttonGroupVariants} from "."

const props = defineProps<{
  class?: HTMLAttributes["class"]
  orientation?: ButtonGroupVariants["orientation"]
}>()
</script>

<template>
  <div
      :class="cn(buttonGroupVariants({ orientation: props.orientation }), props.class)"
      :data-orientation="props.orientation"
      data-slot="button-group"
      role="group"
  >
    <slot/>
  </div>
</template>
