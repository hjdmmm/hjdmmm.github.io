<script lang="ts" setup>
import {cn} from "@/lib/utils"
import {reactiveOmit} from "@vueuse/core"
import type {DialogTitleProps} from "reka-ui"
import {DialogTitle} from "reka-ui"
import type {HTMLAttributes} from "vue"

const props = defineProps<DialogTitleProps & { class?: HTMLAttributes["class"] }>()

const delegatedProps = reactiveOmit(props, "class")
</script>

<template>
  <DialogTitle
      :class="cn('text-foreground font-semibold', props.class)"
      data-slot="sheet-title"
      v-bind="delegatedProps"
  >
    <slot/>
  </DialogTitle>
</template>
