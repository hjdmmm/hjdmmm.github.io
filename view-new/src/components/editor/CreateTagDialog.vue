<script lang="ts" setup>
import {Button} from "@/components/ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger
} from "@/components/ui/dialog";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form";
import {Input} from "@/components/ui/input";
import {toTypedSchema} from "@vee-validate/zod";
import * as z from 'zod';

const newTagFormSchema = toTypedSchema(z.object({
  name: z.string().min(1, "标题不能为空").max(20, "标题不能超过20个字符"),
  remark: z.string().max(100, "备注不能超过100个字符"),
}));

const emits = defineEmits<{
  'createTag': [name: string, remark: string];
}>();
</script>

<template>
  <Form
      v-slot="{handleSubmit, meta}"
      :validation-schema="newTagFormSchema"
      keep-values
  >
    <Dialog>
      <DialogTrigger as-child>
        <slot/>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>新建标签</DialogTitle>
        </DialogHeader>
        <form
            id="newTagForm"
            class="grid gap-2"
            @submit="handleSubmit($event, (values) => emits('createTag', values.name, values.remark))"
        >
          <FormField v-slot="{componentField}" name="name">
            <FormItem>
              <FormLabel>标题</FormLabel>
              <FormControl>
                <Input type="text" v-bind="componentField"/>
              </FormControl>
              <FormMessage/>
            </FormItem>
          </FormField>
          <FormField v-slot="{componentField}" name="remark">
            <FormItem>
              <FormLabel>备注</FormLabel>
              <FormControl>
                <Input type="text" v-bind="componentField"/>
              </FormControl>
              <FormMessage/>
            </FormItem>
          </FormField>
        </form>
        <DialogFooter>
          <DialogClose as-child>
            <Button :disabled="!meta.valid" class="cursor-pointer" form="newTagForm" type="submit">
              确定
            </Button>
          </DialogClose>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </Form>
</template>
