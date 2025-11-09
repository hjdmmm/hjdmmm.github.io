<script lang="ts" setup>
import CreateArticleDialog from "@/components/sidebar/CreateArticleDialog.vue";
import {Label} from '@/components/ui/label'
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupAction,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarInput,
  SidebarMenu,
  SidebarRail,
  useSidebar,
} from '@/components/ui/sidebar'
import {cn} from "@/lib/utils.ts";
import {
  categoryArticleTree,
  changeParent,
  createNewArticle,
  hotArticleTree,
  initAllArticles,
  latestArticleTree,
  loadCategoryArticles,
  loadingMoreArticle,
} from "@/store/articleStore.ts";
import {user} from "@/store/userStore.ts";
import {Loader2, Plus, Search} from "lucide-vue-next";
import {computed, onMounted} from 'vue'
import ArticleTreeLeafNode from "./ArticleTreeLeafNode.vue";
import ArticleTreeNode from "./ArticleTreeNode.vue";

const {open} = useSidebar();

const checkScrollBottom = async (event: Event) => {
  const target = event.target as HTMLElement;
  const {scrollTop, scrollHeight, clientHeight} = target;
  if (scrollHeight - scrollTop - clientHeight < 10) {
    await loadCategoryArticles();
  }
};

const sidebarPositionClass = computed(() => {
  if (open.value) {
    return "sticky";
  }
  return "fixed";
})

const newCategoryNode = async (title: string) => {
  await createNewArticle(false, title, null);
}

const canCreateCategory = computed(() => user.value !== null);

const handleDragOver = (event: DragEvent) => {
  event.preventDefault();
  if (!event.dataTransfer) {
    return;
  }
  if (canCreateCategory) {
    event.dataTransfer.dropEffect = 'move';
  } else {
    event.dataTransfer.dropEffect = 'none';
  }
};

const handleDrop = async (event: DragEvent) => {
  event.preventDefault();
  if (!event.dataTransfer) {
    return;
  }
  const draggedNodeId = event.dataTransfer.getData('text/plain');
  if (draggedNodeId.length === 0) {
    return;
  }
  if (!canCreateCategory) {
    return;
  }
  await changeParent(draggedNodeId, null);
};

onMounted(async () => {
  await initAllArticles();
});
</script>

<template>
  <Sidebar :class="cn(sidebarPositionClass, 'top-0 z-3')">
    <div
        v-if="loadingMoreArticle"
        class="absolute inset-0 bg-gray-200/40 bg-opacity-30 z-10"
        @click.prevent
    ></div>

    <SidebarHeader>
      <SidebarMenu>
        <SidebarGroup>
          <SidebarGroupContent class="relative">
            <Label class="sr-only" for="search">
              搜索
            </Label>
            <SidebarInput
                id="search"
                class="pl-8"
                placeholder="搜索文章标题"
            />
            <Search class="pointer-events-none absolute top-1/2 left-2 size-4 -translate-y-1/2 opacity-50 select-none"/>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarMenu>
    </SidebarHeader>
    <SidebarContent
        class="no-scrollbar"
        @scroll="checkScrollBottom"
    >
      <SidebarGroup>
        <SidebarGroupLabel class="text-lg">热门文章</SidebarGroupLabel>
        <SidebarMenu>
          <ArticleTreeLeafNode
              v-for="nodeId in hotArticleTree"
              :key="nodeId"
              :level="0"
              :node-id="nodeId"
          />
        </SidebarMenu>
      </SidebarGroup>
      <SidebarGroup>
        <SidebarGroupLabel class="text-lg">最新文章</SidebarGroupLabel>
        <SidebarMenu>
          <ArticleTreeLeafNode
              v-for="nodeId in latestArticleTree"
              :key="nodeId"
              :level="0"
              :node-id="nodeId"
          />
        </SidebarMenu>
      </SidebarGroup>
      <SidebarGroup>
        <SidebarGroupLabel
            class="text-lg"
            @dragover="handleDragOver"
            @drop="handleDrop"
        >
          文章分类
        </SidebarGroupLabel>
        <CreateArticleDialog v-if="canCreateCategory" @create-article="newCategoryNode">
          <SidebarGroupAction title="添加新分类">
            <Plus/>
          </SidebarGroupAction>
        </CreateArticleDialog>
        <SidebarMenu>
          <ArticleTreeNode
              v-for="nodeId in categoryArticleTree"
              :key="nodeId"
              :node-id="nodeId"
          />
        </SidebarMenu>
        <div v-if="loadingMoreArticle" class="flex items-center justify-center py-2">
          <Loader2 class="w-4 h-4 mr-2 animate-spin"/>
          <span class="text-sm text-muted-foreground">加载中...</span>
        </div>
      </SidebarGroup>
    </SidebarContent>
    <SidebarRail/>
  </Sidebar>
</template>
