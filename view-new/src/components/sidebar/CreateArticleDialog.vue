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

const newArticleFormSchema = toTypedSchema(z.object({
  title: z.string().min(1, "标题不能为空").max(50, "标题不能超过50个字符"),
}));

const emits = defineEmits<{
  'createArticle': [title: string];
}>();
</script>

<template>
  <Form
      v-slot="{handleSubmit, meta}"
      :validation-schema="newArticleFormSchema"
      keep-values
  >
    <Dialog>
      <DialogTrigger as-child>
        <slot/>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>新建子文档</DialogTitle>
        </DialogHeader>
        <form
            id="newArticleForm"
            class="grid gap-2"
            @submit="handleSubmit($event, (values) => emits('createArticle', values.title))"
        >
          <FormField v-slot="{componentField}" name="title">
            <FormItem>
              <FormLabel>标题</FormLabel>
              <FormControl>
                <Input type="text" v-bind="componentField"/>
              </FormControl>
              <FormMessage/>
            </FormItem>
          </FormField>
        </form>
        <DialogFooter>
          <DialogClose as-child>
            <Button :disabled="!meta.valid" class="cursor-pointer" form="newArticleForm" type="submit">
              确定
            </Button>
          </DialogClose>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </Form>
</template>
