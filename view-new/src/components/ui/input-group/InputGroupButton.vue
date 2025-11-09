<script lang="ts" setup>
import {Button} from '@/components/ui/button'
import {cn} from "@/lib/utils"
import type {InputGroupButtonProps} from "."
import {inputGroupButtonVariants} from "."

const props = withDefaults(defineProps<InputGroupButtonProps>(), {
  size: "xs",
  variant: "ghost",
})
</script>

<template>
  <Button
      :class="cn(inputGroupButtonVariants({ size: props.size }), props.class)"
      :data-size="props.size"
      :variant="props.variant"
  >
    <slot/>
  </Button>
</template>
