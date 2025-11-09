<script lang="ts" setup>
import {login as loginApi, logout as logoutApi} from "@/api/article.ts";
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
import {getSuccessData} from "@/lib/utils.ts";
import {user} from "@/store/userStore.ts";
import {toTypedSchema} from "@vee-validate/zod";
import {onBeforeUnmount} from "vue";
import * as z from 'zod';

const loginFormSchema = toTypedSchema(z.object({
  username: z.string().min(1, "请输入用户名"),
  password: z.string().min(1, "请输入密码"),
}));

let loginTimeout: number | null = null;

const login = async (username: string, password: string) => {
  const result = getSuccessData(await loginApi(username, password));
  const tokenMaxAgeSeconds = result.tokenMaxAgeSeconds;
  user.value = {token: result.token, id: result.id};
  loginTimeout = setTimeout(clearUserStore, tokenMaxAgeSeconds * 1000)
}

const logout = async () => {
  await logoutApi();
  clearUserStore();
}

const clearUserStore = () => {
  if (loginTimeout) {
    clearTimeout(loginTimeout);
    loginTimeout = null;
  }
  user.value = null;
}

onBeforeUnmount(() => {
  clearUserStore();
});
</script>

<template>
  <div class="flex justify-between items-center p-2 bg-white border-b">
    <div class="text-xl font-bold">
      hjdmmm的博客
    </div>

    <template v-if="user !== null">
      <Button size="sm" variant="ghost" @click="logout()">
        退出登录
      </Button>
    </template>
    <template v-else>
      <Form
          v-slot="{handleSubmit, meta}"
          :validation-schema="loginFormSchema"
          keep-values
      >
        <Dialog>
          <DialogTrigger as-child>
            <Button class="ml-auto" size="sm" variant="ghost">
              登录
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>登录</DialogTitle>
            </DialogHeader>
            <form
                id="loginForm"
                class="grid gap-2"
                @submit="handleSubmit($event, (values) => login(values.username, values.password))"
            >
              <FormField v-slot="{componentField}" name="username">
                <FormItem>
                  <FormLabel>用户名</FormLabel>
                  <FormControl>
                    <Input type="text" v-bind="componentField"/>
                  </FormControl>
                  <FormMessage/>
                </FormItem>
              </FormField>
              <FormField v-slot="{componentField}" name="password">
                <FormItem>
                  <FormLabel>密码</FormLabel>
                  <FormControl>
                    <Input type="password" v-bind="componentField"/>
                  </FormControl>
                  <FormMessage/>
                </FormItem>
              </FormField>
            </form>
            <DialogFooter>
              <DialogClose as-child>
                <Button :disabled="!meta.valid" class="cursor-pointer" form="loginForm" type="submit">
                  确定
                </Button>
              </DialogClose>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </Form>
    </template>
  </div>
</template>
