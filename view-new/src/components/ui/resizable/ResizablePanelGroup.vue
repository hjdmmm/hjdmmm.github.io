<script lang="ts" setup>
import {cn} from "@/lib/utils"
import {reactiveOmit} from "@vueuse/core"
import type {SplitterGroupEmits, SplitterGroupProps} from "reka-ui"
import {SplitterGroup, useForwardPropsEmits} from "reka-ui"
import type {HTMLAttributes} from "vue"

const props = defineProps<SplitterGroupProps & { class?: HTMLAttributes["class"] }>()
const emits = defineEmits<SplitterGroupEmits>()

const delegatedProps = reactiveOmit(props, "class")

const forwarded = useForwardPropsEmits(delegatedProps, emits)
</script>

<template>
  <SplitterGroup
      :class="cn('flex h-full w-full data-[orientation=vertical]:flex-col', props.class)"
      data-slot="resizable-panel-group"
      v-bind="forwarded"
  >
    <slot/>
  </SplitterGroup>
</template>
