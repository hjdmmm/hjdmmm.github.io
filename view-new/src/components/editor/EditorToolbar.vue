<script lang="ts" setup>
import EditorColorPicker from "@/components/editor/EditorColorPicker.vue";
import {getTableSize, isTableActive, resizeTable} from "@/components/editor/tableUtils.ts";
import {Button} from '@/components/ui/button';
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from '@/components/ui/form'
import {Input} from '@/components/ui/input';
import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue
} from '@/components/ui/select';
import {Separator} from '@/components/ui/separator';
import {Toggle} from '@/components/ui/toggle';
import {cn} from '@/lib/utils.ts';
import type {Level as HeadingLevel} from "@tiptap/extension-heading";
import type {Editor} from "@tiptap/vue-3";
import {toTypedSchema} from '@vee-validate/zod'
import type {ColorInstance} from "color";
import Color from "color";
import {
  Baseline,
  Bold,
  ChevronDown,
  Code,
  Italic,
  List as ListUnordered,
  ListOrdered,
  Palette,
  Quote,
  Redo,
  SquareCode,
  Strikethrough,
  Table,
  Undo,
} from 'lucide-vue-next';
import type {HTMLAttributes} from "vue";
import {ref} from 'vue';
import * as z from 'zod';

const props = defineProps<{
  editor: Editor,
  class?: HTMLAttributes["class"],
}>();

const fontSizes = [
  {label: '小', value: 'small'},
  {label: '中', value: 'medium'},
  {label: '大', value: 'large'},
]

const headings = [
  {label: '一级标题', value: 1},
  {label: '二级标题', value: 2},
  {label: '三级标题', value: 3},
  {label: '四级标题', value: 4},
  {label: '五级标题', value: 5},
  {label: '六级标题', value: 6},
]

const codeLanguages = [
  {label: 'arduino', value: 'arduino'},
  {label: 'bash', value: 'bash'},
  {label: 'c', value: 'c'},
  {label: 'cpp', value: 'cpp'},
  {label: 'csharp', value: 'csharp'},
  {label: 'css', value: 'css'},
  {label: 'diff', value: 'diff'},
  {label: 'go', value: 'go'},
  {label: 'graphql', value: 'graphql'},
  {label: 'ini', value: 'ini'},
  {label: 'java', value: 'java'},
  {label: 'javascript', value: 'javascript'},
  {label: 'json', value: 'json'},
  {label: 'kotlin', value: 'kotlin'},
  {label: 'less', value: 'less'},
  {label: 'lua', value: 'lua'},
  {label: 'makefile', value: 'makefile'},
  {label: 'markdown', value: 'markdown'},
  {label: 'objectivec', value: 'objectivec'},
  {label: 'perl', value: 'perl'},
  {label: 'php', value: 'php'},
  {label: 'php-template', value: 'php-templa'},
  {label: 'plaintext', value: 'plaintext'},
  {label: 'python', value: 'python'},
  {label: 'python-repl', value: 'python-rep'},
  {label: 'r', value: 'r'},
  {label: 'ruby', value: 'ruby'},
  {label: 'rust', value: 'rust'},
  {label: 'scss', value: 'scss'},
  {label: 'shell', value: 'shell'},
  {label: 'sql', value: 'sql'},
  {label: 'swift', value: 'swift'},
  {label: 'typescript', value: 'typescript'},
  {label: 'vbnet', value: 'vbnet'},
  {label: 'wasm', value: 'wasm'},
  {label: 'xml', value: 'xml'},
  {label: 'yaml', value: 'yaml'},
];

const selectedRow = ref(0);
const selectedCol = ref(0);
const oldTableSize = ref({row: 0, col: 0});
const handleTableCellMouseEnter = (row: number, col: number) => {
  selectedRow.value = row;
  selectedCol.value = col;
};
const getTableCellClass = (row: number, col: number) => {
  const isTableCellHovered = row <= selectedRow.value && col <= selectedCol.value;
  const isTableCellInOldTable = row <= oldTableSize.value.row && col <= oldTableSize.value.col;
  let bgColor = 'bg-white';
  if (isTableCellHovered) {
    bgColor = 'bg-blue-500';
  } else if (isTableCellInOldTable) {
    bgColor = 'bg-blue-500/40';
  }
  return cn('w-4 h-4 border border-gray-300 cursor-pointer', bgColor);
};
const insertOrUpdateTable = (row: number, col: number) => {
  const editor = props.editor;
  if (isTableActive(editor)) {
    resizeTable(editor, row, col);
  } else {
    editor.chain().focus().insertTable({rows: row, cols: col}).run();
  }
};
const tableColRowFormSchema = toTypedSchema(z.object({
  row: z.number().int("行数必须为整数").min(1, "行数必须大于等于1").max(20, "行数必须小于等于20"),
  col: z.number().int("列数必须为整数").min(1, "列数必须大于等于1").max(20, "列数必须小于等于20"),
}));

const handleTextColorChange = (color: ColorInstance | null) => {
  if (color) {
    props.editor.chain().setColor(color.hex()).run();
  } else {
    props.editor.chain().unsetColor().run();
  }
};

const handleBackgroundColorChange = (color: ColorInstance | null) => {
  if (color) {
    props.editor.chain().setBackgroundColor(color.hex()).run();
  } else {
    props.editor.chain().unsetBackgroundColor().run();
  }
};
</script>

<template>
  <div :class="cn('flex items-center px-2 py-1 gap-2', props.class)">
    <div class="flex items-center gap-1">
      <Select
          :disabled="!editor.can().chain().focus().setHeading({level: 1}).run() && !editor.can().chain().focus().setHeading({level: 2}).run()"
          :modelValue="editor.getAttributes('heading').level">
        <SelectTrigger>
          <SelectValue placeholder="正文"/>
        </SelectTrigger>
        <SelectContent>
          <SelectGroup>
            <SelectLabel class="font-bold">标题</SelectLabel>
            <SelectItem value="default" @select="editor.chain().focus().setNode('paragraph').run()">正文</SelectItem>
            <SelectItem
                v-for="heading in headings"
                :key="heading.value"
                :value="heading.value"
                @select="editor.chain().focus().setHeading({ level: heading.value as HeadingLevel }).run()"
            >
              {{ heading.label }}
            </SelectItem>
          </SelectGroup>
        </SelectContent>
      </Select>
    </div>

    <Separator class="data-[orientation=vertical]:h-6" orientation="vertical"/>

    <div class="flex items-center gap-1">
      <Select
          :disabled="!editor.can().chain().focus().unsetFontSize().run()"
          :modelValue="editor.getAttributes('textStyle').fontSize"
      >
        <SelectTrigger>
          <SelectValue placeholder="默认"/>
        </SelectTrigger>
        <SelectContent>
          <SelectGroup>
            <SelectLabel class="font-bold">字号</SelectLabel>
            <SelectItem value="default" @select="editor.chain().focus().unsetFontSize().run()">默认</SelectItem>
            <SelectItem
                v-for="size in fontSizes"
                :key="size.value"
                :value="size.value"
                @select="editor.chain().focus().setFontSize(size.value).run()"
            >
              {{ size.label }}
            </SelectItem>
          </SelectGroup>
        </SelectContent>
      </Select>

      <Popover>
        <PopoverTrigger as-child>
          <Button
              :disabled="!editor.can().chain().focus().unsetBackgroundColor().run()"
              size="sm"
              variant="ghost"
          >
            <Palette :color="editor.getAttributes('textStyle')?.backgroundColor || undefined"/>
            <ChevronDown/>
          </Button>
        </PopoverTrigger>
        <PopoverContent>
          <EditorColorPicker
              :color="Color(editor.getAttributes('textStyle')?.backgroundColor)"
              @update:value="handleBackgroundColorChange"
          />
        </PopoverContent>
      </Popover>

      <Popover>
        <PopoverTrigger as-child>
          <Button
              :disabled="!editor.can().chain().focus().unsetColor().run()"
              size="sm"
              variant="ghost"
          >
            <Baseline :color="editor.getAttributes('textStyle')?.color || undefined"/>
            <ChevronDown/>
          </Button>
        </PopoverTrigger>
        <PopoverContent>
          <EditorColorPicker
              :color="Color(editor.getAttributes('textStyle')?.color)"
              @update:value="handleTextColorChange"
          />
        </PopoverContent>
      </Popover>

      <Toggle
          :disabled="!editor.can().chain().focus().toggleBold().run()"
          :modelValue="editor.isActive('bold')"
          size="sm"
          @update:modelValue="editor.chain().focus().toggleBold().run()"
      >
        <Bold class="h-4 w-4"/>
      </Toggle>
      <Toggle
          :disabled="!editor.can().chain().focus().toggleItalic().run()"
          :modelValue="editor.isActive('italic')"
          size="sm"
          @update:modelValue="editor.chain().focus().toggleItalic().run()"
      >
        <Italic class="h-4 w-4"/>
      </Toggle>
      <Toggle
          :disabled="!editor.can().chain().focus().toggleStrike().run()"
          :modelValue="editor.isActive('strike')"
          size="sm"
          @update:modelValue="editor.chain().focus().toggleStrike().run()"
      >
        <Strikethrough class="h-4 w-4"/>
      </Toggle>
    </div>

    <Separator class="data-[orientation=vertical]:h-6" orientation="vertical"/>

    <div class="flex items-center gap-1">
      <Toggle
          :modelValue="editor.isActive('bulletList')"
          size="sm"
          @update:modelValue="editor.chain().focus().toggleBulletList().run()"
      >
        <ListUnordered class="h-4 w-4"/>
      </Toggle>
      <Toggle
          :modelValue="editor.isActive('orderedList')"
          size="sm"
          @update:modelValue="editor.chain().focus().toggleOrderedList().run()"
      >
        <ListOrdered class="h-4 w-4"/>
      </Toggle>
      <Toggle
          :modelValue="editor.isActive('blockquote')"
          size="sm"
          @update:modelValue="editor.chain().focus().toggleBlockquote().run()"
      >
        <Quote class="h-4 w-4"/>
      </Toggle>
      <Toggle
          :disabled="!editor.can().chain().focus().toggleCode().run()"
          :modelValue="editor.isActive('code')"
          size="sm"
          @update:modelValue="editor.chain().focus().toggleCode().run()"
      >
        <Code class="h-4 w-4"/>
      </Toggle>
    </div>

    <Separator class="data-[orientation=vertical]:h-6" orientation="vertical"/>

    <div class="flex items-center gap-1">
      <Popover>
        <PopoverTrigger>
          <Toggle
              :disabled="!editor.can().chain().focus().insertTable().run()"
              :modelValue="isTableActive(editor)"
              size="sm"
              @click="oldTableSize = getTableSize(editor)"
          >
            <Table class="h-4 w-4"/>
            <ChevronDown/>
          </Toggle>
        </PopoverTrigger>
        <PopoverContent class="grid gap-1 w-auto">
          <div
              v-for="row in Array.from({ length: 8 }, (_, index) => index + 1)"
              :key="row"
              class="flex gap-1"
          >
            <div
                v-for="col in Array.from({ length: 8 }, (_, index) => index + 1)"
                :key="col"
                :class="getTableCellClass(row, col)"
                @click="insertOrUpdateTable(row, col)"
                @mouseenter="handleTableCellMouseEnter(row, col)"
            >
            </div>
          </div>
          <div class="flex gap-1 items-center">
            <span class="text-sm text-muted-foreground">{{ selectedRow }}×{{ selectedCol }} 表格</span>
            <Form
                v-slot="{handleSubmit, meta}"
                :validation-schema="tableColRowFormSchema"
                keep-values
            >
              <Dialog>
                <DialogTrigger class="ml-auto">
                  <Button class="text-xs" size="sm" variant="secondary">
                    指定行列数
                  </Button>
                </DialogTrigger>
                <DialogContent>
                  <DialogHeader>
                    <DialogTitle>请输入行列数</DialogTitle>
                  </DialogHeader>
                  <form
                      id="tableColRowForm"
                      class="grid gap-2"
                      @submit="handleSubmit($event, (values) => insertOrUpdateTable(values.row, values.col))"
                  >
                    <FormField v-slot="{componentField}" name="row">
                      <FormItem>
                        <FormLabel>行数</FormLabel>
                        <FormControl>
                          <Input type="number" v-bind="componentField"/>
                        </FormControl>
                        <FormMessage/>
                      </FormItem>
                    </FormField>
                    <FormField v-slot="{componentField}" name="col">
                      <FormItem>
                        <FormLabel>列数</FormLabel>
                        <FormControl>
                          <Input type="number" v-bind="componentField"/>
                        </FormControl>
                        <FormMessage/>
                      </FormItem>
                    </FormField>
                  </form>
                  <DialogFooter>
                    <DialogClose as-child>
                      <Button :disabled="!meta.valid" class="cursor-pointer" form="tableColRowForm" type="submit">
                        确定
                      </Button>
                    </DialogClose>
                  </DialogFooter>
                </DialogContent>
              </Dialog>
            </Form>
          </div>
        </PopoverContent>
      </Popover>
      <Select
          :disabled="!editor.can().chain().focus().setCodeBlock().run() && !editor.getAttributes('codeBlock').language"
          :modelValue="editor.getAttributes('codeBlock').language"
      >
        <SelectTrigger :show-default="false" class="p-0 border-0">
          <Toggle
              :modelValue="!!editor.getAttributes('codeBlock').language"
              size="sm"
          >
            <SquareCode class="h-4 w-4"/>
            <ChevronDown/>
          </Toggle>
        </SelectTrigger>
        <SelectContent class="w-auto">
          <SelectGroup>
            <SelectLabel class="font-bold">语言</SelectLabel>
            <SelectItem
                v-for="codeLanguage in codeLanguages"
                :key="codeLanguage.value"
                :value="codeLanguage.value"
                @select="editor.chain().focus().toggleCodeBlock({language: codeLanguage.value}).run()"
            >
              {{ codeLanguage.label }}
            </SelectItem>
          </SelectGroup>
        </SelectContent>
      </Select>
    </div>

    <Separator class="data-[orientation=vertical]:h-6" orientation="vertical"/>

    <div class="flex items-center gap-1">
      <Button
          :disabled="!editor.can().chain().focus().undo().run()"
          size="sm"
          variant="ghost"
          @click="editor.chain().focus().undo().run()"
      >
        <Undo class="h-4 w-4"/>
      </Button>
      <Button
          :disabled="!editor.can().chain().focus().redo().run()"
          size="sm"
          variant="ghost"
          @click="editor.chain().focus().redo().run()"
      >
        <Redo class="h-4 w-4"/>
      </Button>
    </div>
  </div>
</template>
