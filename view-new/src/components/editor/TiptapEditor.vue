<script lang="ts" setup>
import CreateTagDialog from "@/components/editor/CreateTagDialog.vue";
import EditorToolbar from "@/components/editor/EditorToolbar.vue";
import {isColHeader, isRowHeader} from "@/components/editor/tableUtils.ts";
import {Badge} from '@/components/ui/badge';
import {Button} from '@/components/ui/button';
import {ButtonGroup} from "@/components/ui/button-group";
import {
  ContextMenu,
  ContextMenuCheckboxItem,
  ContextMenuContent,
  ContextMenuGroup,
  ContextMenuItem,
  ContextMenuLabel,
  ContextMenuSeparator,
  ContextMenuTrigger,
} from "@/components/ui/context-menu";
import {Input} from "@/components/ui/input";
import {Select, SelectContent, SelectGroup, SelectItem, SelectTrigger} from '@/components/ui/select';
import {Separator} from '@/components/ui/separator';
import {
  availableTags,
  clearCurrentArticle,
  contentChanged,
  createNewTag,
  initArticleContent,
  loadingDetail,
  loadingMoreTags,
  loadTags,
  saveContent,
  savingContent,
  savingTags,
  savingTitle,
  selectedArticleDetail,
  selectedArticleId,
  tagsChanged,
  tagsHasMore,
  titleChanged,
  updateContent,
  updateTags,
  updateTitle
} from "@/store/articleStore.ts";
import CodeBlockLowlight from '@tiptap/extension-code-block-lowlight';
import {Details, DetailsContent, DetailsSummary} from "@tiptap/extension-details";
import FileHandler from '@tiptap/extension-file-handler';
import Image from '@tiptap/extension-image';
import {TaskItem, TaskList} from "@tiptap/extension-list";
import {TableKit} from '@tiptap/extension-table';
import {TextStyleKit} from '@tiptap/extension-text-style';
import {CharacterCount} from '@tiptap/extensions'
import StarterKit from '@tiptap/starter-kit';
import {EditorContent, useEditor} from '@tiptap/vue-3';
import {common, createLowlight} from 'lowlight';
import {ChevronDown, Loader2, X} from "lucide-vue-next"
import {computed, onBeforeUnmount, onMounted, ref, watch} from 'vue';

const lowlight = createLowlight(common);

const insertImage = (file: File, pos: number) => {
  const fileReader = new FileReader()
  fileReader.readAsDataURL(file)
  fileReader.onload = () => {
    editor.value?.chain().insertContentAt(pos, {
      type: 'image',
      attrs: {
        src: fileReader.result,
      },
    }).focus().run();
  }
}

const editor = useEditor({
  editable: true,
  editorProps: {
    attributes: {
      class: 'tiptap',
    },
  },
  content: '',
  extensions: [
    StarterKit.configure({
      bulletList: {
        keepMarks: true,
        keepAttributes: false,
      },
      orderedList: {
        keepMarks: true,
        keepAttributes: false,
      },
      codeBlock: false,
      link: {
        openOnClick: false,
        defaultProtocol: 'https',
      }
    }),
    TableKit.configure({
      table: {
        resizable: true,
        lastColumnResizable: true,
      },
    }),
    TextStyleKit,
    CharacterCount,
    Image.configure({
      allowBase64: true,
    }),
    CodeBlockLowlight.configure({
      lowlight,
      enableTabIndentation: true,
    }),
    FileHandler.configure({
      allowedMimeTypes: ['image/png', 'image/jpeg', 'image/gif', 'image/webp'],
      onDrop: (_, files, pos) => {
        files.forEach(file => {
          insertImage(file, pos);
        });
      },
      onPaste: (editor, files) => {
        files.forEach(file => {
          insertImage(file, editor.state.selection.anchor);
        });
      },
    }),
    Details.configure({
      persist: true,
      HTMLAttributes: {
        class: 'details',
      },
    }),
    DetailsSummary,
    DetailsContent,
    TaskList,
    TaskItem.configure({
      nested: true,
    }),
  ],
  onUpdate: ({editor}) => {
    onContentUpdated(editor.getHTML());
  },
});
const editable = ref(true);
const lastModifiedTime = computed(() => {
  if (!selectedArticleDetail.value) {
    return '';
  }
  return new Date(parseInt(selectedArticleDetail.value.updateTime)).toLocaleDateString();
});

const onTitleChanged = (newTitle: string | number) => {
  updateTitle(newTitle.toString());
};

const onContentUpdated = (newContent: string) => {
  updateContent(newContent);
};

const loadMoreTags = async () => {
  await loadTags();
};

const removeTag = (id: string) => {
  console.log("removeTag", id);
  selectedTagIds.value = selectedTagIds.value.filter(tagId => tagId !== id);
}

const selectedTagIds = computed({
  get: () => selectedArticleDetail.value?.tags.map(tag => tag.id) || [],
  set: (newTagIds: string[]) => {
    updateTags(newTagIds);
  }
});

watch([selectedArticleId, initArticleContent], ([newSelectedArticleId, newInitArticleContent], [oldSelectedArticleId, oldInitArticleContent]) => {
  if (newInitArticleContent !== oldInitArticleContent) {
    editor.value?.chain().setMeta('addToHistory', false).focus().setContent(newInitArticleContent, {
      emitUpdate: false,
      parseOptions: {
        preserveWhitespace: 'full',
      }
    }).run();
  } else if (newSelectedArticleId !== oldSelectedArticleId) {
    editor.value?.chain().setMeta('addToHistory', false).focus().setContent('', {
      emitUpdate: false,
      parseOptions: {
        preserveWhitespace: 'full',
      }
    }).run();
  }
});

watch(() => selectedArticleDetail.value?.canEdit, (newCanEdit) => {
  if (!newCanEdit) {
    editable.value = false;
  }
});

watch([editable, editor], ([newEditable, newEditor]) => {
  newEditor?.setOptions({editable: newEditable});
});

onMounted(async () => {
  window.addEventListener('beforeunload', e => {
    clearCurrentArticle();
    const isChangedOrSaving = contentChanged.value || titleChanged.value || tagsChanged.value || savingTitle.value || savingContent.value || savingTags.value;
    if (isChangedOrSaving && !window.confirm("正在保存中，离开可能会失去未保存内容。是否离开？")) {
      e.preventDefault();
    }
  });
});

onBeforeUnmount(async () => {
  editor.value?.destroy();
  await clearCurrentArticle();
});
</script>

<template>
  <div v-if="editor && selectedArticleDetail && !loadingDetail" class="w-full p-4">
    <header
        class="w-full sticky top-0 z-50 bg-background/100 backdrop-blur supports-[backdrop-filter]:bg-background/95">
      <div class="border-b mb-1 grid gap-1">
        <div class="flex h-12 items-center">
          <div class="flex items-center gap-4 text-sm">
            <span class="text-base font-bold">{{ selectedArticleDetail.title || '无标题' }}</span>
            <span><span class="text-gray-500">作者：</span>{{ selectedArticleDetail.author }}</span>
            <span><span class="text-gray-500">最后修改时间：</span>{{ lastModifiedTime }}</span>
            <div class="flex items-center gap-1">
              <span class="text-gray-500">标签：</span>
              <Select
                  v-if="selectedArticleDetail.canEdit"
                  v-model="selectedTagIds"
                  :disabled="!editable"
                  multiple
              >
                <div class="flex gap-2">
                </div>
                <SelectTrigger>
                </SelectTrigger>
                <SelectContent class="min-w-32">
                  <CreateTagDialog @create-tag="createNewTag">
                    <Button class="w-full cursor-pointer" size="sm">
                      添加新标签
                    </Button>
                  </CreateTagDialog>
                  <SelectGroup>
                    <SelectItem v-for="tag in availableTags" :key="tag.id" :value="tag.id">
                      {{ tag.id }} - {{ tag.name }}
                    </SelectItem>
                  </SelectGroup>
                  <div v-if="loadingMoreTags" class="flex justify-center p-2">
                    <Loader2 class="w-4 h-4 animate-spin"/>
                  </div>
                  <Button v-else :disabled="!tagsHasMore" class="w-full flex items-center" size="sm" variant="ghost"
                          @click="loadMoreTags">
                    <ChevronDown class="w-4 h-4"/>
                  </Button>
                </SelectContent>
              </Select>
              <Badge
                  v-for="tag in selectedArticleDetail.tags"
                  :key="tag.id"
                  class="text-sm flex"
              >
                {{ tag.name }}
                <Button
                    v-if="editable"
                    class="cursor-pointer bg-gray-500 rounded-full w-4 h-4 hover:bg-gray-600 transition-colors"
                    @click="removeTag(tag.id)"
                >
                  <X class="w-3 h-3 text-gray-350"/>
                </Button>
              </Badge>
            </div>
          </div>
          <div v-if="selectedArticleDetail.canEdit" class="ms-auto flex items-center gap-4">
            <Button
                v-show="editable"
                :disabled="savingContent || !contentChanged"
                class="cursor-pointer"
                size="sm"
                @click="saveContent"
            >
              <Loader2 v-if="savingContent" class="w-4 h-4 mr-2 animate-spin"/>
              保存
            </Button>
            <ButtonGroup>
              <Button
                  :disabled="!editable"
                  class="cursor-pointer"
                  size="sm"
                  variant="secondary"
                  @click="editable = false"
              >
                浏览
              </Button>
              <Button
                  :disabled="editable"
                  class="cursor-pointer"
                  size="sm"
                  variant="secondary"
                  @click="editable = true"
              >
                编辑
              </Button>
            </ButtonGroup>
          </div>
        </div>
        <EditorToolbar v-show="editable" :editor="editor"/>
      </div>
    </header>
    <main>
      <div class="overflow-hidden grid gap-2.5">
        <Input
            :modelValue="selectedArticleDetail.title"
            :placeholder="editable ? '请输入标题...' : '无标题'"
            :readonly="!editable"
            class="border-none focus-visible:ring-0 p-0 rounded-none shadow-none h-full md:text-4xl text-4xl font-bold"
            @update:modelValue="onTitleChanged"
        />

        <div class="flex items-center gap-2 text-xs text-gray-400">
          正文 {{ editor.storage.characterCount.characters() }} 字
          <Separator class="data-[orientation=vertical]:h-6" orientation="vertical"/>
          浏览 {{ selectedArticleDetail.viewCount }} 次
        </div>

        <ContextMenu>
          <ContextMenuTrigger :disabled="!editor.isActive('table')">
            <EditorContent :editor="editor"/>
          </ContextMenuTrigger>
          <ContextMenuContent class="w-auto min-w-48">
            <ContextMenuGroup v-if="editor.isActive('table')">
              <ContextMenuLabel class="font-bold">表格操作</ContextMenuLabel>
              <ContextMenuCheckboxItem
                  :disabled="!editor.can().chain().focus().toggleHeaderRow().run()"
                  :modelValue="isRowHeader(editor)"
                  @update:modelValue="editor.chain().focus().toggleHeaderRow().run()">
                表头行
              </ContextMenuCheckboxItem>
              <ContextMenuCheckboxItem
                  :disabled="!editor.can().chain().focus().toggleHeaderColumn().run()"
                  :modelValue="isColHeader(editor)"
                  @update:modelValue="editor.chain().focus().toggleHeaderColumn().run()">
                表头列
              </ContextMenuCheckboxItem>
              <ContextMenuItem
                  :disabled="!editor.can().chain().focus().addRowBefore().run()"
                  @click="editor.chain().focus().addRowBefore().run()">
                在前面添加行
              </ContextMenuItem>
              <ContextMenuItem
                  :disabled="!editor.can().chain().focus().addRowAfter().run()"
                  @click="editor.chain().focus().addRowAfter().run()">
                在后面添加行
              </ContextMenuItem>
              <ContextMenuItem
                  :disabled="!editor.can().chain().focus().addColumnBefore().run()"
                  @click="editor.chain().focus().addColumnBefore().run()">
                在前面添加列
              </ContextMenuItem>
              <ContextMenuItem
                  :disabled="!editor.can().chain().focus().addColumnAfter().run()"
                  @click="editor.chain().focus().addColumnAfter().run()">
                在后面添加列
              </ContextMenuItem>
              <ContextMenuItem
                  :disabled="!editor.can().chain().focus().deleteRow().run()"
                  @click="editor.chain().focus().deleteRow().run()">
                删除行
              </ContextMenuItem>
              <ContextMenuItem
                  :disabled="!editor.can().chain().focus().deleteColumn().run()"
                  @click="editor.chain().focus().deleteColumn().run()">
                删除列
              </ContextMenuItem>
              <ContextMenuItem
                  :disabled="!editor.can().chain().focus().deleteTable().run()"
                  @click="editor.chain().focus().deleteTable().run()">
                删除表格
              </ContextMenuItem>
              <ContextMenuItem
                  :disabled="!editor.can().chain().focus().mergeCells().run()"
                  @click="editor.chain().focus().mergeCells().run()">
                合并单元格
              </ContextMenuItem>
              <ContextMenuItem
                  :disabled="!editor.can().chain().focus().splitCell().run()"
                  @click="editor.chain().focus().splitCell().run()">
                拆分单元格
              </ContextMenuItem>
              <ContextMenuItem
                  :disabled="!editor.can().chain().focus().fixTables().run()"
                  @click="editor.chain().focus().fixTables().run()">
                修复表格
              </ContextMenuItem>
              <ContextMenuSeparator/>
            </ContextMenuGroup>
          </ContextMenuContent>
        </ContextMenu>
      </div>
    </main>
  </div>
  <div v-else class="w-full p-4 flex">
    <Loader2 class="w-24 h-24 m-auto animate-spin"/>
  </div>
</template>
